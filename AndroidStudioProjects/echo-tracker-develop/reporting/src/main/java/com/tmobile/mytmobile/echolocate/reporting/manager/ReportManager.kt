package com.tmobile.mytmobile.echolocate.reporting.manager

import android.content.Context
import com.tmobile.myaccount.events.diagnostics.pojos.collector.event.ClientSideEvent
import com.tmobile.mytmobile.echolocate.configuration.events.configurationevents.ReportConfigEvent
import com.tmobile.mytmobile.echolocate.communicationbus.rxbus.RxBus
import com.tmobile.mytmobile.echolocate.communicationbus.rxbus.SubscribeTicket
import com.tmobile.mytmobile.echolocate.configuration.ConfigKey
import com.tmobile.mytmobile.echolocate.reporting.ConfigProvider
import com.tmobile.mytmobile.echolocate.configuration.model.Base5g
import com.tmobile.mytmobile.echolocate.configuration.model.Report
import com.tmobile.mytmobile.echolocate.reporting.IBaseReportDataRequestor
import com.tmobile.mytmobile.echolocate.reporting.reportsender.ReportSender
import com.tmobile.mytmobile.echolocate.reporting.utils.ReportingLog
import com.tmobile.mytmobile.echolocate.reporting.utils.SingletonHolder
import io.reactivex.disposables.Disposable
import javax.inject.Inject

/**
 *  class ReportManager manages:
 *      - initiation of capture of auto-updated events to configuration
 *          via Rx Observable
 *      - initiation of direct configuration status requests
 *      - initiation of scheduled compilation configuration status reports
 *      - initiation of report compilations/consolidations
 *      - initiation of sending of reports
 */
class ReportManager @Inject constructor(val context: Context) {

    private val bus = RxBus.instance

    private var reportTypesList = mutableListOf<IBaseReportDataRequestor>()

    var clientSideEventList = ArrayList<ClientSideEvent>()

    private var reportCounter: Int = 0

    @Volatile
    private var isReportingInitialized: Boolean = false

    private var reportScheduler: ReportScheduler? = null

    private var reportConfigUpdateDisposable: Disposable? = null

    private lateinit var nr5gConfig: Base5g

    /**
     * singleton creation for Report manager
     */
    companion object : SingletonHolder<ReportManager, Context>(::ReportManager)

    /**
     * initialization data collection for Reporting by starting ReportManager
     * update(restart data collection) if CMS configurations changes by starting [updateReportModuleConfig]
     *
     * controlled by CMS configurations
     */
    fun initReportManager() {
        val configProvider = ConfigProvider.getInstance(context)
        val reportConfig = configProvider.getConfigurationForKey(ConfigKey.REPORTING, context)

        if (reportConfig != null) {
            manageReportDataCollection(reportConfig as Report)
        }

        updateReportModuleConfig()
    }

    /**
     * Manage data collection for Reporting by:
     * @param [reportConfig] from CMS configurations
     *
     * define [isReportingInitialized]
     * controlled by CMS configurations
     */
    private fun manageReportDataCollection(reportConfig: Report): Boolean {
        ReportingLog.eLogI(
            "Diagnostic : CMS config status for Reporting: ${reportConfig.isEnabled}"
        )

        if (!reportConfig.isEnabled) {

            stopReportDataCollection()

            return isReportingInitialized
        }

        startReportDataCollection(reportConfig)

        return isReportingInitialized
    }

    /**
     * Start data collection for Reporting by:
     * - start DIA Report Scheduler
     *
     * define [isReportingInitialized] as true
     * controlled by CMS configurations
     */
    private fun startReportDataCollection(reportConfig: Report) {
        if (!isReportingInitialized) {
            ReportingLog.eLogD("Diagnostic : REPORT module init", context)

            isReportingInitialized = true

        }

        // Clean the report sender table if required
        ReportSender.getInstance(context).cleanReportsDatabase()

        if (reportScheduler == null) {
            reportScheduler = ReportScheduler(context)
            ReportingLog.eLogD("Diagnostic : REPORT: reportScheduler is null", context)
        }
        val reportingInterval = getReportingInterval(reportConfig)
        ReportingLog.eLogD("Diagnostic : REPORT: reportingInterval: $reportingInterval", context)
        reportScheduler?.schedulerJob(reportingInterval, reportConfig.isEnabled)
        ReportingLog.eLogD("Diagnostic : REPORT: new scheduler job started, intervals: $reportingInterval", context)

    }

    /**
     * Stop data collection for Reporting by:
     * - stop Scheduler
     *
     * define [isReportingInitialized] as false
     * controlled by CMS configurations and Panic Mode
     */
    fun stopReportDataCollection() {
        if (isReportingInitialized) {

            reportScheduler?.stopScheduler()

            isReportingInitialized = false
        }
    }

    /**
     *This fun listens Configuration module and passing new value to fun @runUpdatedConfigDataCollection
     */
    public fun updateReportModuleConfig() {

        val subscribeTicket = SubscribeTicket(RxBus.SubjectType.PUBLISH_SUBJECT)

        reportConfigUpdateDisposable?.dispose()
        reportConfigUpdateDisposable = bus.register<ReportConfigEvent>(subscribeTicket).subscribe {
            it.configValue.let { params ->
                runUpdatedConfigDataCollection(it)
            }
        }
    }

    /**
     * This fun receive new value from Configuration module
     * and restart Reporting module Data Collection with new configuration
     */
    private fun runUpdatedConfigDataCollection(it: ReportConfigEvent) {
        if (!it.configValue.isEnabled || it.configValue.panicMode) {
            stopReportDataCollection()
        } else {
            startReportDataCollection(it.configValue)
        }
    }

    fun addReportTypes(reportType: IBaseReportDataRequestor) {
        // Check the if the report type already exists to avoid duplication.
        if (reportTypesList.contains(reportType)) {
            return
        }

        reportTypesList.add(reportType)
    }

    fun removeReportTypes(reportType: IBaseReportDataRequestor) {
        reportTypesList.remove(reportType)
    }

    fun getReportTypes() : MutableList<IBaseReportDataRequestor> {
        return reportTypesList
    }

    /**
     * Function responsible for comparing Counter(num of received reports) vs size of reportTypesList
     * And if there is anything to send in the collection
     * and determining the availability of reports
     */
    fun checkIfReadyToSend(): Boolean {
        ReportingLog.eLogD(
            "Diagnostic : reportCounter: $reportCounter vs reportTypesList.size: ${reportTypesList.size}", context
        )
        return (reportCounter == reportTypesList.size)
    }

    /**
     * Function responsible for sending report if [checkIfReadyToSend] is true
     */
    fun sendReportsIfReady(moduleName: String) : Boolean {
        ReportingLog.eLogI("Diagnostic: sendReportsIfReady- Module calling synchronized block - $moduleName")
        ReportingLog.eLogI("Diagnostic: sendReportsIfReady- ReportCounter value before incrementing - $reportCounter")
        reportCounter++
        if (checkIfReadyToSend()) {
            reportCounter = 0
            ReportingLog.eLogD("Diagnostic : Reports is ready to send", context)
            if (ReportSender.getInstance(context).processReportSending(clientSideEventList)) {
                clientSideEventList.clear()
                return true
            }
        }
        return false
    }

    /**
     *  Gets interval for reporting module for which the reports should be generated.
     */
    private fun getReportingInterval(reportConfig: Report): Long {
        val intervalHours = reportConfig.reportingInterval

        // Convert the hours from config to minutes
        return (intervalHours * 60).toLong()
    }

    /**
     * Tells if the report manager is up and running.
     */
    fun isManagerInitialized(): Boolean {
        return isReportingInitialized
    }

    fun getReportCounter() : Int {
        return this.reportCounter
    }

    fun setReportCounter(counter: Int) {
        this.reportCounter = counter
    }

    /**
     * Request the data from the data collection modules based on the registration list.
     *
     * @params key the specified report type key
     * @params value Report Payload json in string format
     * @return returns HashMap of available Reports
     */
    @Synchronized
    fun requestReportsFromModules(androidWorkId: String?) {
        if (isReportingInitialized) {
            ReportingLog.eLogD("Diagnostic : Reports requested from all modules", context)
            setReportCounter(0) // Resetting the counter to zero
            ReportSender.getInstance(context).setWorkIdForScheduledJob(androidWorkId)
            var reportFileCreated = false
            val reportTypesList = getReportTypes()
            if (reportTypesList != null) {
                for (reportType in reportTypesList) {
                    var clientSideEventListFromModule = reportType.getReports(0L, 0L)
                    if (clientSideEventListFromModule.isNotEmpty()) {
                        clientSideEventList.addAll(clientSideEventListFromModule)
                    }
// Increase the counter when a status is received by the data collection module regardless of the status
                    reportFileCreated = sendReportsIfReady(reportType.javaClass.toGenericString())
                }
                // if report file is created, then delete the processed reports from data collection modules
                if (reportFileCreated) {
                    deleteProcessedReportRecords()
                }
            } else {
                ReportingLog.eLogE("Diagnostic : reportTypesList is empty")
            }
            ReportingLog.eLogD("Diagnostic : Reports sent from all modules", context)
        } else {
            ReportingLog.eLogD("Diagnostic : Reports module is not initialized", context)
        }
    }

    private fun deleteProcessedReportRecords() {
        val reportTypesList = getReportTypes()
        if (reportTypesList != null) {
            for (reportType in reportTypesList) {
                try {
                    reportType.deleteReports()
                } catch (ex: Exception) {
                    // If any module fails while deleting the processed reports,
                    // we should continue deleting the processed records from other modules
                    // This way, we are limiting the duplication of data only for affected module
                    ReportingLog.eLogE("Diagnostic : Error while deleting the records from $reportType : " +
                            "${ex.localizedMessage}")
                }
            }
        }
    }

    /**
     * Generic function for start process of report sending from data collection module to server.
     * Working the same way for all data collection modules.
     * Called from data module and start sending process.
     */
    fun receiveReportsFromModules(reportEventList: List<ClientSideEvent>) {
        reportEventList?.let {
            clientSideEventList.addAll(reportEventList)
        }
        if (ReportSender.getInstance(context).processReportSending(clientSideEventList)) {
            clientSideEventList.clear()
        }
    }
}