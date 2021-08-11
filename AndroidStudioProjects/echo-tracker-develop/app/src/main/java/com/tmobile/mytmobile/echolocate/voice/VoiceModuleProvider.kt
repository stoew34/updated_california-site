package com.tmobile.mytmobile.echolocate.voice

import android.annotation.SuppressLint
import android.content.Context
import com.google.gson.Gson
import com.tmobile.mytmobile.echolocate.configuration.events.reportingevents.DIAReportResponseEvent
import com.tmobile.mytmobile.echolocate.configuration.events.reportingevents.DIAReportResponseParameters
import com.tmobile.mytmobile.echolocate.reporting.manager.ReportStatusFromModules.Companion.STATUS_COMPLETED
import com.tmobile.mytmobile.echolocate.reporting.manager.ReportStatusFromModules.Companion.STATUS_ERROR
import com.tmobile.mytmobile.echolocate.utils.EchoLocateDateUtils
import com.tmobile.mytmobile.echolocate.utils.EchoLocateDateUtils.Companion.convertToFileNameFormat
import com.tmobile.mytmobile.echolocate.utils.EchoLocateLog
import com.tmobile.mytmobile.echolocate.utils.FileUtils
import com.tmobile.mytmobile.echolocate.voice.repository.database.entity.VoiceReportEntity
import com.tmobile.mytmobile.echolocate.voice.manager.VoiceDataManager
import com.tmobile.mytmobile.echolocate.voice.model.VoiceSingleSessionReport
import com.tmobile.mytmobile.echolocate.voice.utils.VoiceEventType
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import java.util.*

/**
 * This is class is used to initReportingModule voice module and
 * is responsible for returning all the voice reports for the mentioned time range
 * captured by the voice module
 */
class VoiceModuleProvider private constructor(val context: Context) :
    VoiceModuleProviderAbstract() {

    /**
     * VoiceDataManager that mediates interaction with other components,
     * coordinates flow between component of voice data collection modules
     */
    private var voiceDataManager: VoiceDataManager? = null

    companion object {
        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var INSTANCE: VoiceModuleProvider? = null

        /** creates VoiceModuleProvider instance */
        fun getInstance(context: Context): VoiceModuleProvider {
            return INSTANCE ?: synchronized(this) {
                val instance = VoiceModuleProvider(context)
                INSTANCE = instance
                instance
            }
        }
    }

    /**
     * Public API to initReportingModule voice module.
     * This API is responsible for preparing the module to start accepting the incoming data,
     * process and store it. If not called, the voice module will not start the data collection.
     * @param context : Context the context of the calling module
     */
    override fun initVoiceModule(context: Context) {

        if (voiceDataManager == null) {
            voiceDataManager = VoiceDataManager(context)
            EchoLocateLog.eLogD(
                "Diagnostic : VoiceDataManager was null and got initialized"
            )
        }

        if (voiceDataManager != null && !voiceDataManager!!.isManagerInitialized()) {
            voiceDataManager?.initVoiceDataManager()
            EchoLocateLog.eLogD(
                "Diagnostic : VoiceDataManager not null and started fun initVoiceDataManager"
            )
        }
    }

    /**
     * Public API to check if voice module is ready to collect the data.
     *
     */
    override fun isVoiceModuleReady(): Boolean {
        return if (voiceDataManager == null) false
        else voiceDataManager!!.isManagerInitialized()
    }

    /**
     * This API is responsible for returning all the voice reports for the mentioned time range
     * captured by the voice module. This API will look up the voice report database, get the requested data
     * and align the data as per the contract defined between the client and the server for the voice module.
     */
    override fun getVoiceReport(
        startTime: Long,
        endTime: Long
    ): Observable<List<DIAReportResponseEvent>> {

        var voiceReportResponseParameters: DIAReportResponseParameters

        return Observable.create { emitter: ObservableEmitter<List<DIAReportResponseEvent>> ->

            val voiceReportEntityList = voiceDataManager!!.getVoiceReportEntity(startTime, endTime)
            val voiceReportResponseEventList = ArrayList<DIAReportResponseEvent>()

            if (voiceReportEntityList.isEmpty()) {

                voiceReportResponseParameters = DIAReportResponseParameters(
                    STATUS_ERROR,
                    "",
                    UUID.randomUUID().toString(),
                    VoiceEventType.VOICE_TYPE.name
                )
                voiceReportResponseEventList.add(DIAReportResponseEvent(voiceReportResponseParameters))
            } else {
                EchoLocateLog.eLogD("Diagnostic : Processing voice reports = ${voiceReportEntityList.size}")
                var startIndex = 0
                val voiceSessionToProcess = voiceDataManager!!.getCombinedCallsSettings()
                val voiceReportList = ArrayList<VoiceSingleSessionReport>()
                while (voiceReportEntityList.size > startIndex) {
                    val endIndex =
                        if (voiceReportEntityList.size > (startIndex + voiceSessionToProcess)) {
                            startIndex + voiceSessionToProcess
                        } else {
                            voiceReportEntityList.size
                        }
                    val currList: List<VoiceReportEntity> =
                        voiceReportEntityList.subList(startIndex, endIndex)

                    val voiceReport = voiceDataManager!!.getVoiceReport(currList)

                    val voiceReportJson = Gson().toJson(voiceReport).toString()
                    voiceReportList.add(voiceReport)

                    voiceReportResponseParameters = DIAReportResponseParameters(
                        STATUS_COMPLETED,
                        voiceReportJson,
                        UUID.randomUUID().toString(),
                        VoiceEventType.VOICE_TYPE.name
                    )
                    voiceReportResponseEventList.add(
                        DIAReportResponseEvent(
                            voiceReportResponseParameters
                        )
                    )
                    EchoLocateLog.eLogD("Diagnostic : Processed voice reports from $startIndex to ${endIndex - 1}")
                    startIndex += voiceSessionToProcess
                }

                val voiceReportListJson = Gson().toJson(voiceReportList).toString()
                saveVoiceReportToFileForDebug(voiceReportListJson)

            }

            /** Log for verify a Voice Report in VoiceReportResponseEvent*/
            EchoLocateLog.eLogD(
                "\tDiagnostic : Generated Voice Report:\n" +
                        "\tReport Id: ${voiceReportResponseEventList[0].DIAReportResponseParameters.ReportId}\n" +
                        "\tReport Type: ${voiceReportResponseEventList[0].DIAReportResponseParameters.reportType}\n" +
                        "\tReport TimeStamp: ${EchoLocateDateUtils.convertToShemaDateFormat(System.currentTimeMillis().toString())}\n" +
                        "\tReport Status: ${(if (voiceReportEntityList.size <= 0) "EMPTY" else voiceReportResponseEventList[0].DIAReportResponseParameters.requestReportStatus)}\n" +
                        "\tReport Payload: ${voiceReportResponseEventList[0].DIAReportResponseParameters.payload}"
            )

            emitter.onNext(voiceReportResponseEventList)
//            deleteReportFromDatabase(voiceReportEntityList)
        }
    }

    /**
     * This function deletes the processed reports from db
     */
    fun deleteReportFromDatabase() {
        voiceDataManager!!.deleteProcessedReportsFromDatabase()
    }

    /**
     * Function to write the payload to a file save in the external storage.
     */
    private fun saveVoiceReportToFileForDebug(voiceReportJson: String) {

        FileUtils.saveFileToExternalStorage(
            voiceReportJson,
            "voice_report_" + convertToFileNameFormat(Date()),
            false
        )
    }

    /**
     * Public API to stop voice module.
     * If this function is called, the voice module will stop working.
     *
     * Call initVoiceModule [initVoiceModule] to start voice module again.
     */
    override fun stopVoiceModule() {
        voiceDataManager?.stopVoiceDataCollection()
        voiceDataManager = null
    }
}