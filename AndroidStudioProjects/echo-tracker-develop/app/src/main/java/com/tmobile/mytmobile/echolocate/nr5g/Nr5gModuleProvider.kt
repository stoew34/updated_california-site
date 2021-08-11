package com.tmobile.mytmobile.echolocate.nr5g

import android.annotation.SuppressLint
import android.content.Context
import android.telephony.ServiceState
import com.tmobile.myaccount.events.diagnostics.pojos.collector.event.ClientSideEvent
import com.tmobile.mytmobile.echolocate.nr5g.core.oemdata.Nr5gDataMetricsWrapper
import com.tmobile.mytmobile.echolocate.nr5g.core.oemdata.Sa5gDataMetricsWrapper
import com.tmobile.mytmobile.echolocate.configuration.events.reportingevents.DIAReportResponseEvent
import com.tmobile.mytmobile.echolocate.nr5g.core.utils.Nr5gConstants
import com.tmobile.mytmobile.echolocate.nr5g.manager.Base5gDataManager
import com.tmobile.mytmobile.echolocate.nr5g.manager.Nsa5gDataManager
import com.tmobile.mytmobile.echolocate.nr5g.manager.Sa5gDataManager
import com.tmobile.mytmobile.echolocate.reportingmanager.ReportProvider
import com.tmobile.mytmobile.echolocate.utils.ELDeviceUtils
import com.tmobile.mytmobile.echolocate.utils.EchoLocateLog
import com.tmobile.mytmobile.echolocate.nr5g.core.utils.Nr5gUtils
import com.tmobile.mytmobile.echolocate.reporting.manager.ReportStatusFromModules
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.lang.reflect.Method

/**
 * This is class is used to initialize Nr5g data collection module and
 * is responsible for returning all the Nr5g reports for the mentioned
 * time range captured by the Nr5g data collection module.
 */
class Nr5gModuleProvider private constructor(val context: Context) : INr5gProvider {

    /**
     * Base5gDataManager that mediates interaction with other components,
     * coordinates flow between component of Nr5g data collection modules
     */
    private var baseDataManager: Base5gDataManager? = null
    private var sa5gDataManager: Sa5gDataManager? = null
    private var nsa5gDataManager: Nsa5gDataManager? = null

    /** Variable to check if Nr5g is supported */
    @Volatile
    var isNsa5gSupported = false

    /** Variable to check if Sa5g is supported */
    @Volatile
    var isSa5gSupported = false

    init {
        checkDataSupport()
    }

    /** A companion object can access the private members of its companion.*/
    companion object {
        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var INSTANCE: Nr5gModuleProvider? = null

        /** Creates Nr5gModuleProvider instance */
        fun getInstance(context: Context): Nr5gModuleProvider {
            return INSTANCE ?: synchronized(this) {
                val instance = Nr5gModuleProvider(context)
                INSTANCE = instance
                instance
            }
        }
    }

    /**
     * Public API to initialize Nr5g module. This API is responsible for preparing the module
     * to start accepting the incoming data, process and store it.
     * If this API is not called, the Nr5g module will not start the data collection.
     * @param context : Context the context of the Nr5g module
     */
    override fun initNr5gModule(context: Context) {

        checkDataSupport()

        if (baseDataManager == null) {
            baseDataManager = when {
                isSa5gSupported -> Sa5gDataManager(context)
                isNsa5gSupported -> Nsa5gDataManager(context)
                else -> null
            }
            if (baseDataManager != null) {
                EchoLocateLog.eLogD(
                    "Diagnostic :Nr5g provider " +
                            "SA - ${isSa5gSupported.toString()}, NSA - ${isNsa5gSupported.toString()}"
                )
                baseDataManager!!.isNsa5gSupported = isNsa5gSupported
                baseDataManager!!.isSa5gSupported = isSa5gSupported
            }
        }
        if (baseDataManager != null && !baseDataManager!!.isManagerInitialized()) {
            baseDataManager?.initNr5gDataManager()
        }
        EchoLocateLog.eLogD("Diagnostic :Nr5g initNr5gDataManager called")
    }

    /**
     * This API is responsible for returning all the Nr5g reports for the mentioned time range
     * captured by the Nr5g module. This API will look up the Nr5g report database, get the requested data
     * and align the data as per the contract defined between the client and the server for the Nr5g module.
     *
     * @param startTime Start time in millis from where the data should be included in the report
     *
     * @param endTime End time in millis till when the data should be included in the report
     */
    override fun get5gReport(
        startTime: Long,
        endTime: Long
    ): Observable<List<DIAReportResponseEvent>> {

        return Observable.create { emitter: ObservableEmitter<List<DIAReportResponseEvent>> ->

            val reportResponseEventList: ArrayList<DIAReportResponseEvent> =
                baseDataManager!!.get5gReportEntityList(startTime, endTime)

            emitter.onNext(reportResponseEventList)
        }
    }

    /**
     * This API is responsible for directly sending all the Nr5g reports captured by the Nr5g module.
     * This API will look up the Nr5g report database, get the requested data
     * and align the data as per the contract defined between the client and the server for the Nr5g module.
     *
     * Called by Report Sender Scheduler
     */
    override fun send5gReport(androidWorkId: String?) {
        if (isNr5gModuleReady()) {

            var clientSideEventList = ArrayList<ClientSideEvent>()
            val reportResponseEventList: ArrayList<DIAReportResponseEvent> =
                baseDataManager!!.get5gReportEntityList(0, 0)
            var reportPayload: String

            //convert DIAReportResponseEvent list to ClientSideEventList
            reportResponseEventList.forEach {
                if (it.DIAReportResponseParameters.requestReportStatus
                    == ReportStatusFromModules.STATUS_COMPLETED
                ) {
                    reportPayload = it.DIAReportResponseParameters.payload
                    if (isSa5gSupported) {
                        var reportClientSideEvent =
                            Nr5gUtils.createSa5gClientSideEvent(reportPayload)
                        reportClientSideEvent?.let { clientSideEvent ->
                            clientSideEventList.add(
                                clientSideEvent
                            )
                        }
                    } else {
                        var reportClientSideEvent =
                            Nr5gUtils.createNr5gClientSideEvent(reportPayload)
                        reportClientSideEvent?.let { clientSideEvent ->
                            clientSideEventList.add(
                                clientSideEvent
                            )
                        }
                    }
                }
            }

            try {
                GlobalScope.launch(Dispatchers.IO) {
                    ReportProvider.getInstance(context)
                        .performReportSending(clientSideEventList)
                    delay(1000)
                    deleteReportFromDatabase()
                }
            } catch (ex: Exception) {
                EchoLocateLog.eLogE("error: " + ex.localizedMessage)
            }
            Nr5gUtils.sendJobCompletedToScheduler(
                androidWorkId,
                Nr5gConstants.REPORT_SENDER_COMPONENT_NAME
            )
        }
    }

    /**
     * Public API to stop nr5g module.
     * If this function is called, the nr5g module will stop working.
     *
     * Call init Nr5g Module [initNr5gModule] to start nr5g module again.
     */
    override fun stopNr5gModule() {
        baseDataManager?.stopNr5gDataCollection()
        baseDataManager = null
        sa5gDataManager = null
        nsa5gDataManager = null
    }

    /**
     *  This method checks is the device Nr5g supported or not.
     *  If Data metrics class is available and the data metrics version >=1
     */
    private fun isNsa5gDataSupported(): Boolean {
        val nr5gDataMetricsWrapper =
            Nr5gDataMetricsWrapper(
                context
            )

        val apiVersionCode = nr5gDataMetricsWrapper.getApiVersion().intCode
        EchoLocateLog.eLogV("EchoLocate ApiVersion: $apiVersionCode")

        if (nr5gDataMetricsWrapper.isDataMetricsAvailable()) {
            when {
                /**If the version is less than 1 (unknown version) EchoLocate5g -> not supported*/
                apiVersionCode < 1 -> return false
                /**If the version is greater than or equal to 3 EchoLocate5g -> supported*/
                apiVersionCode >= 3 -> return true
                /**If greater than or equal to Q -> not supported with less than version 3*/
                ELDeviceUtils.isQDeviceOrHigher() -> return false

                else -> {
                    val serviceState = ServiceState()
                    var getNrStatus: Method? = null

                    try {
                        getNrStatus = serviceState.javaClass.getMethod("getNrStatus")

                    } catch (e: NoSuchMethodException) {
                        e.printStackTrace()
                    }
                    return getNrStatus != null
                }
            }
        }
        return false
    }

    /**
     *  This method checks is the device Nr5g supported or not.
     *  If Data metrics class is available and the data metrics version >=1
     */
    private fun checkDataSupport() {
        val sa5gDataMetricsWrapper =
            Sa5gDataMetricsWrapper(
                context
            )
        isSa5gSupported = sa5gDataMetricsWrapper.isDataMetricsAvailable()

        if (!isSa5gSupported) {
            isNsa5gSupported = isNsa5gDataSupported()
        }
    }

    /**
     * This function deletes the processed reports from db
     */
    fun deleteReportFromDatabase() {
        baseDataManager?.deleteProcessedReportsFromDatabase()
    }

    /**
     * Public API to check if Nr5g module is ready to collect the data.
     * Returns true if ready
     */
    override fun isNr5gModuleReady(): Boolean {
        return if (baseDataManager == null) false
        else baseDataManager!!.isManagerInitialized()
    }
}