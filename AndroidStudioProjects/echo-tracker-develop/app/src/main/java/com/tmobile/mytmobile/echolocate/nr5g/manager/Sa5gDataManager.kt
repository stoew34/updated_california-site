package com.tmobile.mytmobile.echolocate.nr5g.manager

import android.content.Context
import com.google.gson.Gson
import com.tmobile.mytmobile.echolocate.configuration.events.reportingevents.DIAReportResponseEvent
import com.tmobile.mytmobile.echolocate.configuration.events.reportingevents.DIAReportResponseParameters
import com.tmobile.mytmobile.echolocate.nr5g.sa5g.database.entity.Sa5gSingleSessionReportEntity
import com.tmobile.mytmobile.echolocate.nr5g.sa5g.datacollector.Sa5gDataCollector
import com.tmobile.mytmobile.echolocate.nr5g.sa5g.model.Sa5gSingleSessionReport
import com.tmobile.mytmobile.echolocate.nr5g.sa5g.reportprocessor.Sa5gDataStatus
import com.tmobile.mytmobile.echolocate.nr5g.sa5g.reportprocessor.Sa5gReportProcessor
import com.tmobile.mytmobile.echolocate.nr5g.sa5g.utils.Sa5gEventType
import com.tmobile.mytmobile.echolocate.reporting.manager.ReportStatusFromModules.Companion.STATUS_COMPLETED
import com.tmobile.mytmobile.echolocate.reporting.manager.ReportStatusFromModules.Companion.STATUS_ERROR
import com.tmobile.mytmobile.echolocate.utils.EchoLocateDateUtils
import com.tmobile.mytmobile.echolocate.utils.EchoLocateLog
import com.tmobile.mytmobile.echolocate.utils.FileUtils
import java.util.*
import kotlin.collections.ArrayList

class Sa5gDataManager(context: Context) : Base5gDataManager(context) {

    private var sa5gDataCollector: Sa5gDataCollector? = null
    private var sa5gReportProcessor: Sa5gReportProcessor? = null

    /** Initialization block */
    init {
        sa5gDataCollector = Sa5gDataCollector(context)
        sa5gReportProcessor = Sa5gReportProcessor.getInstance(context)
    }

    /**
     * Store Sa5g entity data into room database
     * @param triggerCode: trigger code when app focus gain, screen on events called
     * @param isCountWithinLimit: trigger count limit
     * @param packageName: name of app package
     * @param triggerDelay: trigger delay
     */
    override fun store5gEntity(
        triggerCode: Int,
        isCountWithinLimit: Boolean,
        packageName: String,
        triggerDelay: Int
    ) {
        sa5gDataCollector?.storeSa5gEntity(
            triggerCode,
            packageName,
            triggerDelay
        )
    }

    /**
     * Process and save raw data to database with status PROCESSED
     * generate new report list for all new record(RAW status)
     */
    override fun processRawData(androidWorkId: String?) {
        sa5gReportProcessor?.processRawData()
    }

    /**
     * generates report by collecting data on mobile devices and
     * sending of OEM intents delivered to the application. These intents are the custom
     * intents implemented by the OEMS for the TMO Applications and can be listened only
     * by the system application. Those intents deliver detailed log data about Triggers made
     * from or to the device.
     */
    @Suppress("UNCHECKED_CAST")
    override fun get5gReportList(singleSessionReportEntityList: List<Any?>): List<Sa5gSingleSessionReport> {
        return sa5gReportProcessor!!.getSa5gMultiSessionReport(singleSessionReportEntityList as List<Sa5gSingleSessionReportEntity>)
    }

    /**
     * This function returns the list of sa5g reports entity
     * @param startTime reports start date
     * @param endTime reports end date
     */
    @Suppress("UNCHECKED_CAST")
    override fun get5gReportEntityList(
        startTime: Long,
        endTime: Long
    ): ArrayList<DIAReportResponseEvent> {

        val sa5gReportEntityList =
            sa5gReportProcessor!!.getSa5gMultiSessionReportEntity(startTime, endTime)
        sa5gReportEntityList.let {
            it.forEach {
                it.reportStatus = Sa5gDataStatus.STATUS_REPORTING
            }
        }
        sa5gReportProcessor!!.updateSa5gReportEntity(sa5gReportEntityList)
        return convertToDIAReportResponseEvent(get5gReportList(sa5gReportEntityList!!))
    }

    /**
     * Converting list of reports to DIA Report Response Event
     */
    override fun convertToDIAReportResponseEvent(reportEntityList: List<Any?>): ArrayList<DIAReportResponseEvent> {
        var reportResponseParameters: DIAReportResponseParameters
        val reportResponseEventList = ArrayList<DIAReportResponseEvent>()

        if (reportEntityList.isEmpty()) {
            reportResponseParameters = DIAReportResponseParameters(
                STATUS_ERROR,
                "",
                UUID.randomUUID().toString(),
                Sa5gEventType.SA5G_TYPE.name
            )
            reportResponseEventList.add(DIAReportResponseEvent(reportResponseParameters))
        } else {
            for (report in reportEntityList) {

                val reportJson = Gson().toJson(report).toString()
                val diaEventType = Sa5gEventType.SA5G_TYPE.name

                reportResponseParameters = DIAReportResponseParameters(
                    STATUS_COMPLETED,
                    reportJson,
                    UUID.randomUUID().toString(),
                    diaEventType
                )
                reportResponseEventList.add(DIAReportResponseEvent(reportResponseParameters))
            }
            val reportListJson = Gson().toJson(reportEntityList).toString()
            saveReportToFileForDebug(reportListJson)
        }
        /** Log for verify a Sa5g Report in sa5gReportResponseEventList*/
        EchoLocateLog.eLogD(
            "\tDiagnostic : Generated Sa5g Report:\n" +
                    "\tReport Type: ${reportResponseEventList[0].DIAReportResponseParameters.reportType}\n" +
                    "\tReport TimeStamp: ${EchoLocateDateUtils.convertToShemaDateFormat(
                        System.currentTimeMillis().toString()
                    )}\n" +
                    "\tReport Status: ${(
                            if (reportEntityList.isEmpty()) "EMPTY"
                            else reportResponseEventList[0].DIAReportResponseParameters.requestReportStatus)}\n" +
                    "\tReport Payload(only first trigger shown): ${reportResponseEventList[0].DIAReportResponseParameters.payload}\n"
        )
        return reportResponseEventList
    }

    /**
     * Function to write the payload to a file save in the external storage.
     */
    private fun saveReportToFileForDebug(reportListJson: String) {
        FileUtils.saveFileToExternalStorage(
            reportListJson,
            "Sa5g_report_" + EchoLocateDateUtils.convertToFileNameFormat(Date()),
            false
        )
    }

    /**
     * This function deletes the processed reports from db
     */
    override fun deleteProcessedReportsFromDatabase() {
        sa5gReportProcessor?.deleteProcessedReports(Sa5gDataStatus.STATUS_REPORTING)
    }
}