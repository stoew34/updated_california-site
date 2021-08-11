package com.tmobile.mytmobile.echolocate.nr5g.manager

import android.content.Context
import com.google.gson.Gson
import com.tmobile.mytmobile.echolocate.configuration.events.reportingevents.DIAReportResponseEvent
import com.tmobile.mytmobile.echolocate.configuration.events.reportingevents.DIAReportResponseParameters
import com.tmobile.mytmobile.echolocate.nr5g.nsa5g.database.entity.Nr5gSingleSessionReportEntity
import com.tmobile.mytmobile.echolocate.nr5g.nsa5g.datacollector.Nsa5gDataCollector
import com.tmobile.mytmobile.echolocate.nr5g.nsa5g.model.Nr5gSingleSessionReport
import com.tmobile.mytmobile.echolocate.nr5g.nsa5g.reportprocessor.Nsa5gDataStatus
import com.tmobile.mytmobile.echolocate.nr5g.nsa5g.reportprocessor.Nsa5gReportProcessor
import com.tmobile.mytmobile.echolocate.nr5g.nsa5g.utils.Nsa5gEventType
import com.tmobile.mytmobile.echolocate.reporting.manager.ReportStatusFromModules.Companion.STATUS_COMPLETED
import com.tmobile.mytmobile.echolocate.reporting.manager.ReportStatusFromModules.Companion.STATUS_ERROR
import com.tmobile.mytmobile.echolocate.utils.EchoLocateDateUtils
import com.tmobile.mytmobile.echolocate.utils.EchoLocateLog
import com.tmobile.mytmobile.echolocate.utils.FileUtils
import java.util.*
import kotlin.collections.ArrayList


class Nsa5gDataManager(context: Context) : Base5gDataManager(context) {

    private var nsa5gDataCollector: Nsa5gDataCollector? = null
    private var nr5gReportProcessor: Nsa5gReportProcessor? = null

    /** Initialization block */
    init {
        nsa5gDataCollector = Nsa5gDataCollector(context)
        nr5gReportProcessor = Nsa5gReportProcessor.getInstance(context)
    }

    /**
     * Store Nsa5g entity data into room database
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
        nsa5gDataCollector?.storeNr5gEntity(
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
        nr5gReportProcessor!!.processRawData(androidWorkId)
    }

    /**
     * generates report by collecting data on mobile devices and
     * sending of OEM intents delivered to the application. These intents are the custom
     * intents implemented by the OEMS for the TMO Applications and can be listened only
     * by the system application. Those intents deliver detailed log data about Triggers made
     * from or to the device.
     */
    @Suppress("UNCHECKED_CAST")
    override fun get5gReportList(singleSessionReportEntityList: List<Any?>): List<Nr5gSingleSessionReport> {
        return nr5gReportProcessor!!.getNr5gMultiSessionReport(singleSessionReportEntityList as List<Nr5gSingleSessionReportEntity>)
    }

    /**
     * This function returns the list of nr5g reports entity
     * @param startTime reports start date
     * @param endTime reports end date
     */
    @Suppress("UNCHECKED_CAST")
    override fun get5gReportEntityList(
            startTime: Long,
            endTime: Long
    ): ArrayList<DIAReportResponseEvent> {

        val nr5gReportEntityList =
                nr5gReportProcessor!!.getNr5gMultiSessionReportEntity(startTime, endTime)
        nr5gReportEntityList.let {
            it.forEach {
                it.reportStatus = Nsa5gDataStatus.STATUS_REPORTING
            }
        }
        nr5gReportProcessor!!.updateNr5gReportEntity(nr5gReportEntityList)

        return convertToDIAReportResponseEvent(get5gReportList(nr5gReportEntityList!!))
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
                    Nsa5gEventType.NR5G_TYPE.name
            )
            reportResponseEventList.add(DIAReportResponseEvent(reportResponseParameters))
        } else {
            for (report in reportEntityList) {

                val reportJson = Gson().toJson(report).toString()
                val diaEventType = Nsa5gEventType.NR5G_TYPE.name

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
        /** Log for verify a Nr5g Report in nr5gReportResponseEventList*/
        EchoLocateLog.eLogD(
                "\tDiagnostic : Generated Nsa5g Report:\n" +
                        "\tReport Type: ${reportResponseEventList[0].DIAReportResponseParameters.reportType}\n" +
                        "\tReport TimeStamp: ${
                            EchoLocateDateUtils.convertToShemaDateFormat(
                                    System.currentTimeMillis().toString()
                            )
                        }\n" +
                        "\tReport Status: ${
                            (
                                    if (reportEntityList.isEmpty()) "EMPTY"
                                    else reportResponseEventList[0].DIAReportResponseParameters.requestReportStatus)
                        }\n" +
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
                "Nsa5g_report_" + EchoLocateDateUtils.convertToFileNameFormat(Date()),
                false
        )
    }

    /**
     * This function deletes the processed reports from db
     */
    override fun deleteProcessedReportsFromDatabase() {
        nr5gReportProcessor?.deleteProcessedReports(Nsa5gDataStatus.STATUS_REPORTING)
    }
}