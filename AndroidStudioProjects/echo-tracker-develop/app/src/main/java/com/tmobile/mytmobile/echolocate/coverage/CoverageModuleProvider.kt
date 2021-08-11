package com.tmobile.mytmobile.echolocate.coverage

import android.annotation.SuppressLint
import android.content.Context
import com.google.gson.Gson
import com.tmobile.mytmobile.echolocate.configuration.events.reportingevents.DIAReportResponseEvent
import com.tmobile.mytmobile.echolocate.configuration.events.reportingevents.DIAReportResponseParameters
import com.tmobile.mytmobile.echolocate.coverage.manager.CoverageDataManager
import com.tmobile.mytmobile.echolocate.coverage.utils.CoverageEventType
import com.tmobile.mytmobile.echolocate.reporting.manager.ReportStatusFromModules.Companion.STATUS_COMPLETED
import com.tmobile.mytmobile.echolocate.reporting.manager.ReportStatusFromModules.Companion.STATUS_ERROR
import com.tmobile.mytmobile.echolocate.utils.EchoLocateDateUtils
import com.tmobile.mytmobile.echolocate.utils.EchoLocateLog
import com.tmobile.mytmobile.echolocate.utils.FileUtils
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import java.util.*

/**
 * This is class is used to initialize coverage data collection module and
 * is responsible for returning all the coverage reports for the mentioned
 * time range captured by the coverage data collection module.
 */
class CoverageModuleProvider private constructor(val context: Context) : ICoverageProvider {

    /**
     * CoverageDataManager that mediates interaction with other components,
     * coordinates flow between component of coverage data collection modules
     */
    private var coverageDataManager: CoverageDataManager? = null

    /** A companion object can access the private members of its companion.*/
    companion object {
        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var INSTANCE: CoverageModuleProvider? = null

        /***
         * creates [CoverageModuleProvider] instance
         */
        fun getInstance(context: Context): ICoverageProvider {
            return INSTANCE ?: synchronized(this) {
                val instance = CoverageModuleProvider(context)
                INSTANCE = instance
                instance
            }
        }
    }

    /**
     * Public API to initialize coverage module. This API is responsible for preparing the module
     * to start accepting the incoming data, process and store it.
     * If this API is not called, the coverage module will not start the data collection.
     * @param context : Context the context of the coverage module
     * */
    override fun initCoverageModule(context: Context) {
        if (coverageDataManager == null) {
            coverageDataManager = CoverageDataManager(context)
        }
        if (coverageDataManager != null && !coverageDataManager!!.isManagerInitialized()) {
            coverageDataManager?.initCoverageDataManager()
        }
    }

    /**
     * Public API to stop Coverage module.
     * If this function is called, the Coverage module will stop working.
     *
     * Call initCoverageModule [initCoverageModule] to start voice module again.
     */
    override fun stopCoverageModule() {
        coverageDataManager?.stopCoverageDataCollection()
        coverageDataManager = null
    }

    /**
     * This API is responsible for returning all the Coverage reports
     * for the mentioned time range
     */
    override fun getCoverageReport(
        startTime: Long,
        endTime: Long
    ): Observable<List<DIAReportResponseEvent>> {

        var coverageReportResponseParameters: DIAReportResponseParameters

        return Observable.create { emitter: ObservableEmitter<List<DIAReportResponseEvent>> ->

            val coverageReportEntityList = coverageDataManager!!.getCoverageReportEntity()
            val coverageReportList =
                coverageDataManager!!.getCoverageReport(coverageReportEntityList)
            val coverageReportResponseEventList = ArrayList<DIAReportResponseEvent>()

            if (coverageReportList.isEmpty()) {
                coverageReportResponseParameters =
                    DIAReportResponseParameters(
                    STATUS_ERROR,
                    "",
                    UUID.randomUUID().toString(),
                    CoverageEventType.COVERAGE_TYPE.name
                )
                coverageReportResponseEventList.add(
                    DIAReportResponseEvent(
                        coverageReportResponseParameters
                    )
                )
            } else {
                for (coverageReport in coverageReportList) {

                    val coverageReportJson = Gson().toJson(coverageReport).toString()

                    coverageReportResponseParameters = DIAReportResponseParameters(
                        STATUS_COMPLETED,
                        coverageReportJson,
                        UUID.randomUUID().toString(),
                        CoverageEventType.COVERAGE_TYPE.name
                    )
                    coverageReportResponseEventList.add(
                        DIAReportResponseEvent(
                            coverageReportResponseParameters
                        )
                    )
                }
                val coverageReportListJson = Gson().toJson(coverageReportList).toString()
                saveCoverageReportToFileForDebug(coverageReportListJson)
            }
            /** Log for verify a Coverage Report in lteReportResponseEventList*/
            EchoLocateLog.eLogD(
                "\tDiagnostic : Generated Coverage Report:\n" +
                        "\tReport Type: ${coverageReportResponseEventList[0].DIAReportResponseParameters.reportType}\n" +
                        "\tReport TimeStamp: ${EchoLocateDateUtils.convertToShemaDateFormat(
                            System.currentTimeMillis().toString()
                        )}\n" +
                        "\tReport Status: ${(
                                if (coverageReportList.isEmpty()) "EMPTY"
                                else coverageReportResponseEventList[0].DIAReportResponseParameters.requestReportStatus)}\n" +
                        "\tReport Payload(only first trigger shown): ${coverageReportResponseEventList[0].DIAReportResponseParameters.payload}\n"
            )

            emitter.onNext(coverageReportResponseEventList)
//            deleteReportFromDatabase(coverageReportEntityList)
        }
    }

    /**
     * This function deletes the processed reports from db
     */
    override fun deleteReportFromDatabase() {
        coverageDataManager?.deleteProcessedReportsFromDatabase()
    }

    /**
     * Function to write the payload to a file save in the external storage.
     */
    private fun saveCoverageReportToFileForDebug(coverageReportListJson: String) {

        FileUtils.saveFileToExternalStorage(
            coverageReportListJson,
            "coverage_report_" + EchoLocateDateUtils.convertToFileNameFormat(Date()),
            false
        )
    }

    /**
     * Public API to check if Coverage module is ready to collect the data.
     * Returns true if ready
     */
    override fun isCoverageModuleReady(): Boolean {
        return coverageDataManager?.isManagerInitialized() ?: false
    }
}