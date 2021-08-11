package com.tmobile.mytmobile.echolocate.analytics

import android.annotation.SuppressLint
import android.content.Context
import com.google.gson.Gson
import com.tmobile.mytmobile.echolocate.analytics.manager.AnalyticsManager
import com.tmobile.mytmobile.echolocate.analytics.utils.AnalyticsEventType
import com.tmobile.mytmobile.echolocate.configuration.events.analytics.ELAnalyticsEvent
import com.tmobile.mytmobile.echolocate.configuration.events.reportingevents.DIAReportResponseEvent
import com.tmobile.mytmobile.echolocate.configuration.events.reportingevents.DIAReportResponseParameters
import com.tmobile.mytmobile.echolocate.reporting.manager.ReportStatusFromModules.Companion.STATUS_COMPLETED
import com.tmobile.mytmobile.echolocate.reporting.manager.ReportStatusFromModules.Companion.STATUS_ERROR
import com.tmobile.mytmobile.echolocate.utils.EchoLocateDateUtils.Companion.convertToFileNameFormat
import com.tmobile.mytmobile.echolocate.utils.FileUtils
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import java.util.*

/**
 * Implementation class for [analyticsModuleManager] for the APIs exposed to the external world.
 *
 * This class should perform the basic sanity on the arguments and delegate the work to manager class [analyticsModuleManager]
 * which should be responsible for taking the action and provide the requested information.
 */

class AnalyticsModuleProvider private constructor(val context: Context) : IAnalytics {
    /**
     * analyticsModuleManager that mediates interaction with other components, cordinates flow between component of
     * analytics data collection modules
     */
    private var analyticsModuleManager: AnalyticsManager? = null

    companion object {
        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var INSTANCE: AnalyticsModuleProvider? = null

        /**
         * creates [AnalyticsModuleProvider] instance
         */
        fun getInstance(context: Context): IAnalytics {
            return INSTANCE ?: synchronized(this) {
                val instance = AnalyticsModuleProvider(context)
                INSTANCE = instance
                instance
            }
        }
    }

    /**
     * Public API to initAnalyticsModule analytics module. This API is responsible for preparing the module
     * to start accepting the incoming data, process and store it.
     * If this API is not called, the analytics module will not start the data collection.
     * @param context : Context the context of the calling module
     */
    override fun initAnalyticsModule(context: Context) {

        if (analyticsModuleManager == null) {
            analyticsModuleManager = AnalyticsManager(context)
        }

        if (analyticsModuleManager != null && !analyticsModuleManager!!.isManagerInitialized()) {
            analyticsModuleManager?.initAnalyticsDataManager()
        }
    }

    /**
     * Public API to check if analytics module is ready to collect the data.
     * Returns true if ready
     */
    override fun isAnalyticsModuleReady(): Boolean {
        return if (analyticsModuleManager == null) false
        else analyticsModuleManager!!.isManagerInitialized()
    }

    /**
     * This API is responsible for returning all the analytics reports for the mentioned time range
     * captured by the analytics module. This API will look up the analytics report database, get the requested data
     * and align the data as per the contract defined between the client and the server for the analytics module.
     */
    override fun getAnalyticsReport(): Observable<DIAReportResponseEvent> {
        var analyticsReportResponseParameters: DIAReportResponseParameters

        return Observable.create { emitter: ObservableEmitter<DIAReportResponseEvent> ->

            val analyticsReport = analyticsModuleManager!!.getAnalyticsReport()
            // If there are no sessions recorded, send an error status so that it is not processed by the reporting manager.
            if (analyticsReport.analytics!!.isEmpty()) {
                analyticsReportResponseParameters = DIAReportResponseParameters(
                    STATUS_ERROR,
                    "",
                    UUID.randomUUID().toString(),
                    AnalyticsEventType.ANALYTICS_TYPE.name
                )
            } else {
                val analyticsReportJson = Gson().toJson(analyticsReport).toString()

                saveAnalyticsReportToFileForDebug(analyticsReportJson)

                analyticsReportResponseParameters = DIAReportResponseParameters(
                    STATUS_COMPLETED,
                    analyticsReportJson,
                    UUID.randomUUID().toString(),
                    AnalyticsEventType.ANALYTICS_TYPE.name
                )
            }
            val analyticsReportResponseEvent =
                DIAReportResponseEvent(analyticsReportResponseParameters)

            emitter.onNext(analyticsReportResponseEvent)
        }
    }

    /**
     * function to write the payload to a file save in the external storage.
     */
    private fun saveAnalyticsReportToFileForDebug(analyticsReportJson: String) {

        FileUtils.saveFileToExternalStorage(
            analyticsReportJson,
            "analytics_report_" + convertToFileNameFormat(Date()),
            false
        )
    }

    /**
     * This function deletes the processed reports from db
     */
    override fun deleteReportFromDatabase() {
        analyticsModuleManager?.deleteProcessedReportsFromDatabase()
    }

    /**
     * Public API to stop analytics module.
     * If this function is called, the analytics module will stop working.
     *
     * Call initAnalyticsModule [initAnalyticsModule] to start analytics module again.
     */
    override fun stopAnalyticsModule() {
        analyticsModuleManager?.stopAnalyticsDataCollection()
        analyticsModuleManager = null
    }

    /**
     * Used for testing to insert mock data from playground
     */
    override fun insertMockAnalyticsData(elAnalyticsEvent: ELAnalyticsEvent) {
        if (analyticsModuleManager != null && analyticsModuleManager!!.isManagerInitialized()) {
            analyticsModuleManager!!.processAnalyticsEventData(elAnalyticsEvent)
        }
    }
}