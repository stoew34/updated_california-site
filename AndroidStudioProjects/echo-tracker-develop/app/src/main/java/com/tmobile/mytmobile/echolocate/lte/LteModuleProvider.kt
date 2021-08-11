package com.tmobile.mytmobile.echolocate.lte

import android.annotation.SuppressLint
import android.content.Context
import com.google.gson.Gson
import com.tmobile.mytmobile.echolocate.configuration.events.reportingevents.DIAReportResponseEvent
import com.tmobile.mytmobile.echolocate.configuration.events.reportingevents.DIAReportResponseParameters
import com.tmobile.mytmobile.echolocate.lte.manager.LteDataManager
import com.tmobile.mytmobile.echolocate.lte.utils.LteEventType
import com.tmobile.mytmobile.echolocate.reporting.manager.ReportStatusFromModules.Companion.STATUS_COMPLETED
import com.tmobile.mytmobile.echolocate.reporting.manager.ReportStatusFromModules.Companion.STATUS_ERROR
import com.tmobile.mytmobile.echolocate.utils.EchoLocateDateUtils
import com.tmobile.mytmobile.echolocate.utils.EchoLocateDateUtils.Companion.convertToFileNameFormat
import com.tmobile.mytmobile.echolocate.utils.EchoLocateLog
import com.tmobile.mytmobile.echolocate.utils.FileUtils
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by Hitesh K Gupta on 2019-10-14
 *
 * Implementation class for [ILteProvider] for the APIs exposed to the external world.
 *
 * This class should perform the basic sanity on the arguments and delegate the work to manager class [LteDataManager]
 * which should be responsible for taking the action and provide the requested information.
 *
 */
class LteModuleProvider private constructor(val context: Context) :
    ILteProvider {

    /**
     * LteDataManager that mediates interaction with other components,
     * coordinates flow between component of voice data collection modules
     */
    private var lteDataManager: LteDataManager? = null

    companion object {
        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var INSTANCE: LteModuleProvider? = null

        /***
         * creates [LteModuleProvider] instance
         */
        fun getInstance(context: Context): ILteProvider {
            return INSTANCE ?: synchronized(this) {
                val instance = LteModuleProvider(context)
                INSTANCE = instance
                instance
            }
        }
    }

    /**
     * Public API to initialize lte module. This API is responsible for preparing the module
     * to start accepting the incoming data, process and store it.
     * If this API is not called, the lte module will not start the data collection.
     */
    override fun initLteModule(context: Context) {

        if (lteDataManager == null) {
            lteDataManager = LteDataManager(context)
        }
        if (lteDataManager != null && !lteDataManager!!.isManagerInitialized()) {
            lteDataManager?.initLteDataManager()
        }
    }

    /**
     * Public API to check if lte module is ready to collect the data.
     * Returns true if ready
     */
    override fun isLteModuleReady(): Boolean {
        return if (lteDataManager == null) false
        else lteDataManager!!.isManagerInitialized()
    }

    /**
     * This API is responsible for returning all the lte reports for the mentioned time range
     * captured by the lte module. This API will look up the lte report database, get the requested data
     * and align the data as per the contract defined between the client and the server for the lte module.
     *
     * @param startTime Start time in millis from where the data should be included in the report
     *
     * @param endTime End time in millis till when the data should be included in the report
     */
    override fun getLteReport(startTime: Long, endTime: Long): Observable<List<DIAReportResponseEvent>> {

        var lteReportResponseParameters: DIAReportResponseParameters

        return Observable.create { emitter: ObservableEmitter<List<DIAReportResponseEvent>> ->

            val lteReportEntityList = lteDataManager!!.getLteReportEntity(startTime, endTime)
            val lteReportList = lteDataManager!!.getLteReport(lteReportEntityList)
            val lteReportResponseEventList = ArrayList<DIAReportResponseEvent>()

            if (lteReportList.isEmpty()) {
                lteReportResponseParameters = DIAReportResponseParameters(
                    STATUS_ERROR,
                    "",
                    UUID.randomUUID().toString(),
                    LteEventType.LTE_TYPE.name
                )
                lteReportResponseEventList.add(DIAReportResponseEvent(lteReportResponseParameters))
            } else {
                for (lteReport in lteReportList) {

                    val lteReportJson = Gson().toJson(lteReport).toString()

                    lteReportResponseParameters = DIAReportResponseParameters(
                        STATUS_COMPLETED,
                        lteReportJson,
                        UUID.randomUUID().toString(),
                        LteEventType.LTE_TYPE.name
                    )
                    lteReportResponseEventList.add(
                        DIAReportResponseEvent(
                            lteReportResponseParameters
                        )
                    )
                }
                val lteReportListJson = Gson().toJson(lteReportList).toString()
                saveLteReportToFileForDebug(lteReportListJson)

                /** Log for verify a LTE Report in lteReportResponseEventList*/
                EchoLocateLog.eLogD(
                    "\tDiagnostic : Generated LTE Report:\n" +
                            "\tReport Type: ${lteReportResponseEventList[0].DIAReportResponseParameters.reportType}\n" +
                            "\tReport TimeStamp: ${EchoLocateDateUtils.convertToShemaDateFormat(System.currentTimeMillis().toString())}\n" +
                            "\tReport Status: ${(
                                    if (lteReportList.isEmpty()) "EMPTY" 
                                    else lteReportResponseEventList[0].DIAReportResponseParameters.requestReportStatus)}\n" +
                            "\tReport Payload(only first trigger shown): ${lteReportResponseEventList[0].DIAReportResponseParameters.payload}\n"
                )
            }
            emitter.onNext(lteReportResponseEventList)
        }
    }

    /**
     * function to write the payload to a file save in the external storage.
     */
    private fun saveLteReportToFileForDebug(lteReportListJson: String) {

        FileUtils.saveFileToExternalStorage(
            lteReportListJson,
            "lte_report_" + convertToFileNameFormat(Date()),
            false
        )
    }

    /**
     * This function deletes the processed reports from db
     */
    override fun deleteReportFromDatabase() {
        lteDataManager?.deleteProcessedReportsFromDatabase()
    }

    /**
     * Public API to stop lte module.
     * If this function is called, the lte module will stop working.
     *
     * Call initLteModule [initLteModule] to start voice module again.
     */
    override fun stopLteModule() {
        lteDataManager?.stopLteDataCollection()
        lteDataManager = null
    }

}