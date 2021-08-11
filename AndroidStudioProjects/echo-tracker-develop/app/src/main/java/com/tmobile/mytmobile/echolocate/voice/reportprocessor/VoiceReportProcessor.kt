package com.tmobile.mytmobile.echolocate.voice.reportprocessor

import android.content.Context
import android.text.TextUtils
import com.google.gson.Gson
import com.tmobile.mytmobile.echolocate.BuildConfig
import com.tmobile.mytmobile.echolocate.voice.utils.VoiceDeviceInfoDataCollector
import com.tmobile.mytmobile.echolocate.configuration.events.analytics.ELAnalyticActions
import com.tmobile.mytmobile.echolocate.configuration.events.analytics.ELAnalyticsEvent
import com.tmobile.mytmobile.echolocate.configuration.events.analytics.ELModulesEnum
import com.tmobile.mytmobile.echolocate.communicationbus.rxbus.PostTicket
import com.tmobile.mytmobile.echolocate.communicationbus.rxbus.RxBus
import com.tmobile.mytmobile.echolocate.standarddatablocks.OEMSV
import com.tmobile.mytmobile.echolocate.utils.EchoLocateDateUtils
import com.tmobile.mytmobile.echolocate.utils.EchoLocateLog
import com.tmobile.mytmobile.echolocate.utils.SingletonHolder
import com.tmobile.mytmobile.echolocate.voice.utils.VoiceUtils
import com.tmobile.mytmobile.echolocate.voice.repository.database.EchoLocateVoiceDatabase
import com.tmobile.mytmobile.echolocate.voice.repository.database.dao.VoiceDao
import com.tmobile.mytmobile.echolocate.voice.repository.database.entity.BaseEchoLocateVoiceEntity
import com.tmobile.mytmobile.echolocate.voice.repository.database.entity.VoiceReportEntity
import com.tmobile.mytmobile.echolocate.voice.repository.VoiceRepository
import com.tmobile.mytmobile.echolocate.voice.dataprocessor.VoiceLocationDataProcessor
import com.tmobile.mytmobile.echolocate.voice.model.*
import com.tmobile.mytmobile.echolocate.voice.reportprocessor.VoiceDataStatus.Companion.STATUS_PROCESSED
import com.tmobile.mytmobile.echolocate.voice.reportprocessor.VoiceDataStatus.Companion.STATUS_RAW
import com.tmobile.mytmobile.echolocate.voice.utils.VoiceIntents
//import kotlinx.serialization.ImplicitReflectionSerializer
import java.util.*
import kotlin.collections.ArrayList

/**
 * Generated reports from raw data collected from OEM intents into voice reports.
 * Generate JSON file from DB(model
 * -1- Create the table for sim Voice data
 */


class VoiceReportProcessor private constructor(val context: Context) {

    // Only single instance of report processor is needed to avoid report/data duplication
    companion object : SingletonHolder<VoiceReportProcessor, Context>(::VoiceReportProcessor)

    private val voiceDao: VoiceDao =
        EchoLocateVoiceDatabase.getEchoLocateVoiceDatabase(context).voiceDao()
    var numDiscardedIntents = 0
    val schemaVersion = "1"
    var status = ""
    var voiceRepository = VoiceRepository(context)
    private var locationDataProcessor = VoiceLocationDataProcessor(context)
    var deviceInfoDataCollector = VoiceDeviceInfoDataCollector()

    /**
     * Process and save raw data to database with status PROCESSED
     * generate new report list for all new record(with ENDED and RAW status)
     * [saveVoiceReportToDB] - save new generated reports to database with status RAW
     * [forEach{f -> f] - once proceed -> changed the status of data to PROCESSED
     * [markRawDataAsProcessed] - update status in database
     */
    @Synchronized
    fun processRawData(androidWorkId: String? = null) {
        EchoLocateLog.eLogD("Diagnostic : processRawData for voice module")

        val baseEchoLocateVoiceEntityList = voiceDao.getBaseEchoLocateVoiceEntityTillEnded(STATUS_RAW)

        val voiceReportEntityList = generateVoiceReportFromRawData(baseEchoLocateVoiceEntityList)

        saveVoiceReportToDB(voiceReportEntityList)

        /** get number of calls for every voice report(6h) and sent to analytics module*/
        if (!voiceReportEntityList.isNullOrEmpty()) {
            val analyticsEvent = ELAnalyticsEvent(
                ELModulesEnum.VOICE,
                ELAnalyticActions.EL_NUMBER_OF_SESSIONS,
                voiceReportEntityList.size.toString()
            )
            analyticsEvent.timeStamp = System.currentTimeMillis()

            val postAnalyticsTicket = PostTicket(analyticsEvent)
            RxBus.instance.post(postAnalyticsTicket)
        }

        EchoLocateLog.eLogD("Diagnostic : Processed ${baseEchoLocateVoiceEntityList.size} calls")
        /** Do not delete data in debug so that it can be tested and data can be viewed*/
        if (BuildConfig.DEBUG) {
            baseEchoLocateVoiceEntityList.forEach { f ->
                f.status = STATUS_PROCESSED
            }
            /** update status of data to PROCESSED in DB*/
            markRawDataAsProcessed(baseEchoLocateVoiceEntityList)
        } else {
            deleteRawData(baseEchoLocateVoiceEntityList)
        }

        VoiceUtils.sendJobCompletedToScheduler(
            androidWorkId,
            VoiceIntents.REPORT_GENERATOR_COMPONENT_NAME
        )

    }

    private fun deleteRawData(baseEchoLocateVoiceEntityList: List<BaseEchoLocateVoiceEntity>) {
        voiceRepository.deleteRawData(baseEchoLocateVoiceEntityList)
    }

    /**
     * This fun saves the entities to database
     * The function doesn't create a new thread and executes the statement in the same thread in which this function is called
     * @param voiceReportEntityList: VoiceReportEntity received in generateCallSessionsFromRawData()
     */
    private fun saveVoiceReportToDB(voiceReportEntityList: List<VoiceReportEntity>) {
        voiceRepository.insertAllVoiceReportEntity(voiceReportEntityList)
    }

    /**
     * This fun updates record in database with new data by using updateAllBaseEchoLocateVoiceEntityStatus
     * using Variable number of arguments (varargs) - getting latest value of data
     */
    private fun markRawDataAsProcessed(baseEchoLocateVoiceEntityList: List<BaseEchoLocateVoiceEntity>) {
        voiceDao.updateAllBaseEchoLocateVoiceEntityStatus(*baseEchoLocateVoiceEntityList.toTypedArray())
    }

    /**
     * Generated VoiceSingleSessionReport Report in accordance which Schema JSON and VoiceSingleSessionReport model
     * @params numDiscardedIntents - Marks number of intents that were dropped due to incompatibility with specification.
     * @params schemaVersion - The version of the schema for which the data is being reported.
     * @params status - status of data RAW or PROCESSED
     * @params callSessions - Call Session is contained by all data object describing Echo locate intents.
     * @params location - data from location module
     * @params deviceInfo - data from device info module
     * @return [VoiceSingleSessionReport]
     */

//    @UseExperimental(ImplicitReflectionSerializer::class)
    fun getVoiceSingleSessionReport(voiceReportEntityList: List<VoiceReportEntity>): VoiceSingleSessionReport {

        val totalNumDiscardedIntents = voiceReportEntityList.sumBy { it.numDiscardedIntents }
        val callSessionList = ArrayList<CallSessions>()

        for (voiceReportEntity in voiceReportEntityList) {
            val gson = Gson()
            val callSessions = gson.fromJson(voiceReportEntity.json, CallSessions::class.java)
            callSessionList.add(callSessions)
        }

        val locationData = locationDataProcessor.fetchLocationDataSync()

        if (locationData != null) {
            val calendar = Calendar.getInstance()
            locationData.timestamp =
                EchoLocateDateUtils.convertToShemaDateFormat(calendar.timeInMillis.toString())
        }

        val deviceInfo = deviceInfoDataCollector.getDeviceInformation(context)

        return VoiceSingleSessionReport(
            totalNumDiscardedIntents,
            schemaVersion,
            status,
            callSessionList,
            locationData,
            deviceInfo
        )
    }

    fun getVoiceReportEntity(startTime: Long, endTime: Long): List<VoiceReportEntity> {
        return if (startTime == 0L && endTime == 0L) {
            voiceDao.getVoiceReportEntityList()
        } else {
            voiceDao.getVoiceSingleSessionReportEntityList(startTime.toString(), endTime.toString())
        }
    }

    fun updateVoiceReportEntity(voiceReportEntity: List<VoiceReportEntity>) {
        voiceDao.updateVoiceReportEntityList(*voiceReportEntity.toTypedArray())
    }

    /**
     * Generated reports and saved to DB by saveVoiceReportToDB(),
     * changed data status from RAW to PROCESSED by updateBaseEchoLocateVoiceEntityStatus()
     * Params from VoiceReportEntity:
     * @params voiceReportId - random generated ID
     * @params json - JSON generated from raw data by getVoiceData()
     * @params startTime - calculated min time from list of Call Sessions
     * @params endTime - calculated min time from list of Call Sessions
     * @params eventTimestamp - current time of event
     */
    private fun generateVoiceReportFromRawData(baseEchoLocateVoiceEntityList: List<BaseEchoLocateVoiceEntity>): List<VoiceReportEntity> {

        val voiceReportEntityList = ArrayList<VoiceReportEntity>()
        for (baseEchoLocateVoiceEntity in baseEchoLocateVoiceEntityList) {
            val callSession = getCallSession(baseEchoLocateVoiceEntity)
            val jsonObject = Gson().toJson(callSession)
            val voiceReportEntity =
                VoiceReportEntity(
                    UUID.randomUUID().toString(),
                    jsonObject,
                    numDiscardedIntents,
                    baseEchoLocateVoiceEntity.startTime,
                    baseEchoLocateVoiceEntity.endTime,
                    System.currentTimeMillis().toString(),
                    ""
                )

            voiceReportEntityList.add(voiceReportEntity)
            numDiscardedIntents = 0
        }
        return voiceReportEntityList
    }

    /**
     * Generated Call Session in accordance which Schema JSON, data get status RAW
     * Params of each Call Session in accordance with [com.tmobile.mytmobile.echolocate.voice.model.CallSessions]
     * @params networkIdentity - network operator info data from intents
     * @params callId or sessionId - the unique call ID common to all the intents for the same call session
     * @params callNumber - the phone number of the other party on the call.
     * @params clientVersion - the version of the client at the time of the event.
     * @params OEMSV - OEM software version - Ids of Android
     * @params deviceIntents - list of intents for this Call Sessions
     * @return [CallSessions] - single record of Call Sessions
     */
    private fun getCallSession(baseEchoLocateVoiceEntity: BaseEchoLocateVoiceEntity): CallSessions {

        return CallSessions(
            baseEchoLocateVoiceEntity.networkIdentity,
            baseEchoLocateVoiceEntity.sessionId,
            baseEchoLocateVoiceEntity.callNumber,
            baseEchoLocateVoiceEntity.clientVersion,
            getOEMSV(baseEchoLocateVoiceEntity.sessionId),
            getDeviceIntents(baseEchoLocateVoiceEntity.sessionId)
        )
    }

    /**
     * Generated a list of intents used in Call Sessions
     */
    private fun getDeviceIntents(callId: String): DeviceIntents {
        val appTriggeredCallData = getAppTriggeredCallDataList(callId)
        val callSettingData = getCallSettingDataList(callId)
        val detailedCallStateData = getDetailedCallStateDataList(callId)
        val imsSignallingData = getImsSignallingDataList(callId)
        val rtpdlStateData = getRtpdlStateDataList(callId)
        val uiCallStateData = getUiCallStateDataList(callId)
        val radioHandoverData = getRadioHandoverDataList(callId)
        val emergencyCallTimerStateData = getEmergencyCallTimerStateDataList(callId)
        val carrierConfigData = getCarrierConfigData(callId)

        return DeviceIntents(
            appTriggeredCallData,
            callSettingData,
            detailedCallStateData,
            imsSignallingData,
            rtpdlStateData,
            uiCallStateData,
            radioHandoverData,
            emergencyCallTimerStateData,
            carrierConfigData
        )
    }

    /**
     * Combine location and cellInfo information
     * Based on [com.tmobile.mytmobile.echolocate.voice.model.EventInfo]
     */
    private fun getEventInfo(callId: String, uniqueId: String): EventInfo {
        var location = getStoredLocationData(callId, uniqueId)
        val cellInfo = getCellInfo(callId, uniqueId)
        location = location ?: LocationData()
        return EventInfo(cellInfo, location)
    }

    /**
     * Generated a list of AppTriggeredCall of [com.tmobile.mytmobile.echolocate.voice.database.entity.AppTriggeredCallDataEntity]
     * Based on data class [com.tmobile.mytmobile.echolocate.voice.model.AppTriggeredCallData]
     * Data from AppTriggeredCall INTENT ENTRY DATA
     * @return list of [AppTriggeredCallData]
     */
    private fun getAppTriggeredCallDataList(callId: String): List<AppTriggeredCallData>? {
        val appTriggeredCallDataEntityList = voiceDao.getAppTriggeredCallDataEntity(callId)

        if (appTriggeredCallDataEntityList.isEmpty()) {
            return null
        }

        val appTriggeredCallDataList = ArrayList<AppTriggeredCallData>()

        for (appTriggeredCallDataEntity in appTriggeredCallDataEntityList) {
            if (appTriggeredCallDataEntity.appName.isNotEmpty()) {

                val eventInfo =
                    getEventInfo(
                        appTriggeredCallDataEntity.callId,
                        appTriggeredCallDataEntity.uniqueId
                    )
                val appTriggeredCallData = AppTriggeredCallData(
                    appTriggeredCallDataEntity.appName,
                    appTriggeredCallDataEntity.appPackageId,
                    appTriggeredCallDataEntity.appVersionCode,
                    appTriggeredCallDataEntity.appVersionName,
                    EchoLocateDateUtils.convertToShemaDateFormat(appTriggeredCallDataEntity.oemTimestamp),
                    EchoLocateDateUtils.convertToShemaDateFormat(appTriggeredCallDataEntity.eventTimestamp),
                    eventInfo
                )

                appTriggeredCallDataList.add(appTriggeredCallData)
            } else {
                numDiscardedIntents++
            }
        }
        return appTriggeredCallDataList
    }

    /**
     * Generated a list of CallSettingData of [com.tmobile.mytmobile.echolocate.voice.database.entity.CallSettingDataEntity]
     * Based on data class [com.tmobile.mytmobile.echolocate.voice.model.CallSettingData]
     * Data from CallSetting INTENT ENTRY DATA
     * @return list of [CallSettingData]
     */
    fun getCallSettingDataList(callId: String): List<CallSettingData> {
        val callSettingDataEntityList = voiceDao.getCallSettingDataEntity(callId)

        val callSettingDataList = ArrayList<CallSettingData>()

        for (callSettingDataEntity in callSettingDataEntityList) {

            val callSettingData = CallSettingData(
                callSettingDataEntity.volteStatus,
                callSettingDataEntity.wfcStatus,
                callSettingDataEntity.wfcPreference,
                EchoLocateDateUtils.convertToShemaDateFormat(callSettingDataEntity.oemTimestamp),
                EchoLocateDateUtils.convertToShemaDateFormat(callSettingDataEntity.eventTimestamp),

                getEventInfo(callSettingDataEntity.callId, callSettingDataEntity.uniqueId)
            )
            callSettingDataList.add(callSettingData)
        }
        return callSettingDataList
    }

    /**
     * Generated a list of DetailedCallStateData of [com.tmobile.mytmobile.echolocate.voice.database.entity.DetailedCallStateEntity]
     * Based on data class [com.tmobile.mytmobile.echolocate.voice.model.DetailedCallStateData]
     * Data from DetailedCallState INTENT ENTRY DATA
     * @return list of [DetailedCallStateData]
     */
    fun getDetailedCallStateDataList(callId: String): List<DetailedCallStateData> {
        val detailedCallStateDataEntityList = voiceDao.getDetailedCallStateDataEntity(callId)

        val detailedCallStateDataList = ArrayList<DetailedCallStateData>()

        for (detailedCallStateDataEntity in detailedCallStateDataEntityList) {
            if (TextUtils.isEmpty(detailedCallStateDataEntity.callState)) {
                numDiscardedIntents++
                continue
            }
            val detailedCallStateData = DetailedCallStateData(
                detailedCallStateDataEntity.callCode,
                detailedCallStateDataEntity.callState,
                EchoLocateDateUtils.convertToShemaDateFormat(detailedCallStateDataEntity.oemTimestamp),
                EchoLocateDateUtils.convertToShemaDateFormat(detailedCallStateDataEntity.eventTimestamp),
                getEventInfo(
                    detailedCallStateDataEntity.callId,
                    detailedCallStateDataEntity.uniqueId
                )
            )
            detailedCallStateDataList.add(detailedCallStateData)
        }
        return detailedCallStateDataList
    }

    /**
     * Generated a list of ImsSignallingData of [com.tmobile.mytmobile.echolocate.voice.database.entity.ImsSignallingEntity]
     * Based on data class [com.tmobile.mytmobile.echolocate.voice.model.ImsSignallingData]
     * Data from ImsSignalling INTENT ENTRY DATA
     * @return list of [ImsSignallingData]
     */
    fun getImsSignallingDataList(callId: String): List<ImsSignallingData> {
        val imsSignallingDataEntityList = voiceDao.getImsSignallingDataEntity(callId)

        val imsSignallingDataList = ArrayList<ImsSignallingData>()

        for (imsSignallingDataEntity in imsSignallingDataEntityList) {

            val imsSignallingData = ImsSignallingData(
                imsSignallingDataEntity.sipCallId,
                imsSignallingDataEntity.sipCseq,
                imsSignallingDataEntity.sipLine1,
                imsSignallingDataEntity.sipOrigin,
                imsSignallingDataEntity.sipReason,
                imsSignallingDataEntity.sipSDP,
                EchoLocateDateUtils.convertToShemaDateFormat(imsSignallingDataEntity.oemTimestamp),
                EchoLocateDateUtils.convertToShemaDateFormat(imsSignallingDataEntity.eventTimestamp),

                getEventInfo(imsSignallingDataEntity.callId, imsSignallingDataEntity.uniqueId)
            )
            imsSignallingDataList.add(imsSignallingData)
        }
        return imsSignallingDataList
    }

    /**
     * Generated a list of RtpdlStateData of [com.tmobile.mytmobile.echolocate.voice.database.entity.RtpdlStateEntity]
     * Based on data class [com.tmobile.mytmobile.echolocate.voice.model.RtpdlStateData]
     * Data from RtpdlState INTENT ENTRY DATA
     * @return list of [RtpdlStateData]
     */
    fun getRtpdlStateDataList(callId: String): List<RtpdlStateData> {
        val rtpdlStateDataEntityList = voiceDao.getRtpdlStateDataEntity(callId)

        val rtpdlStateDataList = ArrayList<RtpdlStateData>()

        for (rtpdlStateDataEntity in rtpdlStateDataEntityList) {

            val rtpdlStateData = RtpdlStateData(
                rtpdlStateDataEntity.delay,
                rtpdlStateDataEntity.sequence,
                rtpdlStateDataEntity.jitter,
                rtpdlStateDataEntity.lossRate,
                rtpdlStateDataEntity.measuredPeriod,
                EchoLocateDateUtils.convertToShemaDateFormat(rtpdlStateDataEntity.oemTimestamp),
                EchoLocateDateUtils.convertToShemaDateFormat(rtpdlStateDataEntity.eventTimestamp),
                getEventInfo(rtpdlStateDataEntity.callId, rtpdlStateDataEntity.uniqueId)
            )
            rtpdlStateDataList.add(rtpdlStateData)
        }
        return rtpdlStateDataList
    }

    /**
     * Generated a list of UiCallStateData of [com.tmobile.mytmobile.echolocate.voice.database.entity.UiCallStateEntity]
     * Based on data class [com.tmobile.mytmobile.echolocate.voice.model.UiCallStateData]
     * Data from UiCallState INTENT ENTRY DATA
     * @return list of [UiCallStateData]
     */
    fun getUiCallStateDataList(callId: String): List<UiCallStateData> {
        val uiCallStateDataEntityList = voiceDao.getUiCallStateDataEntity(callId)

        val uiCallStateDataList = ArrayList<UiCallStateData>()

        for (uiCallStateDataEntity in uiCallStateDataEntityList) {

            val uiCallStateData = UiCallStateData(
                uiCallStateDataEntity.uICallState,
                EchoLocateDateUtils.convertToShemaDateFormat(uiCallStateDataEntity.oemTimestamp),
                EchoLocateDateUtils.convertToShemaDateFormat(uiCallStateDataEntity.eventTimestamp),

                getEventInfo(uiCallStateDataEntity.callId, uiCallStateDataEntity.uniqueId)
            )
            uiCallStateDataList.add(uiCallStateData)
        }
        return uiCallStateDataList
    }

    /**
     * Generated a list of RadioHandoverData of [com.tmobile.mytmobile.echolocate.voice.database.entity.RtpdlStateEntity]
     * Based on data class [com.tmobile.mytmobile.echolocate.voice.model.RtpdlStateData]
     * Data from RadioHandover INTENT ENTRY DATA
     * @return list of [RadioHandoverData]
     */
    fun getRadioHandoverDataList(callId: String): List<RadioHandoverData> {
        val radioHandoverDataEntityList = voiceDao.getRadioHandoverDataEntity(callId)

        val radioHandoverDataList = ArrayList<RadioHandoverData>()

        for (radioHandoverDataEntity in radioHandoverDataEntityList) {

            val radioHandoverData = RadioHandoverData(
                radioHandoverDataEntity.handoverState,
                EchoLocateDateUtils.convertToShemaDateFormat(radioHandoverDataEntity.oemTimestamp),
                EchoLocateDateUtils.convertToShemaDateFormat(radioHandoverDataEntity.eventTimestamp),
                getEventInfo(radioHandoverDataEntity.callId, radioHandoverDataEntity.uniqueId)
            )
            radioHandoverDataList.add(radioHandoverData)
        }
        return radioHandoverDataList
    }

    /**
     * Generated an object of LocationData of [com.tmobile.mytmobile.echolocate.voice.database.entity.VoiceLocationEntity]
     * Based on data class [com.tmobile.mytmobile.echolocate.voice.model.LocationData]
     * Data from location module provided by [com.tmobile.mytmobile.echolocate.voice.dataprocessor]
     * @return [LocationData]
     */
    private fun getStoredLocationData(callId: String, uniqueId: String): LocationData? {
        val locationEntity = voiceDao.getLocationDataEntity(callId, uniqueId) ?: return null

        return LocationData(
            locationEntity.altitude,
            locationEntity.altitudePrecision,
            locationEntity.latitude,
            locationEntity.longitude,
            locationEntity.precision,
            locationEntity.locationAge
        )
    }

    /**
     * Generated an object of CellInfo of [com.tmobile.mytmobile.echolocate.voice.database.entity.CellInfoEntity]
     * Based on data class [com.tmobile.mytmobile.echolocate.voice.model.CellInfo]
     * Data from CellInfo INTENT ENTRY DATA
     * @return [CellInfo]
     */
    private fun getCellInfo(callId: String, uniqueId: String): CellInfo {
        val cellInfo = voiceDao.getCellInfoEntity(callId, uniqueId)

        return if (cellInfo != null) CellInfo(
            cellInfo.ecio,
            cellInfo.rscp,
            cellInfo.rsrp,
            cellInfo.rsrq,
            cellInfo.rssi,
            cellInfo.sinr,
            cellInfo.snr,
            cellInfo.lac,
            cellInfo.networkBand,
            cellInfo.cellId,
            cellInfo.networkType
        ) else CellInfo(
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
        )
    }

    /**
     * Generated an object of OEMSV of OEMSoftwareVersionEntity
     * Based on data class [OEMSV]
     * Data from OEMSV INTENT ENTRY DATA
     * @return [OEMSV]
     */
    private fun getOEMSV(callId: String): OEMSV {
        val oEMSV = voiceDao.getOEMSVEntity(callId)

        return OEMSV(
            oEMSV.softwareVersion?: "",
            oEMSV.customVersion?: "",
            oEMSV.radioVersion?: "",
            oEMSV.buildName,
            oEMSV.androidVersion
        )
    }

    /**
     * Generated a list of EmergencyCallTimerState of EmergencyCallTimerStateEntity
     * Based on data class EmergencyCallTimerStateData
     * Data from EmergencyCallTimerState INTENT ENTRY DATA
     * @return list of [EmergencyCallTimerStateData]
     */
    private fun getEmergencyCallTimerStateDataList(callId: String): List<EmergencyCallTimerStateData>? {
        val emergencyCallTimerStateEntityList =
            voiceDao.getEmergencyCallTimerStateEntityList(callId)

        if (emergencyCallTimerStateEntityList.isEmpty()) {
            return null
        }

        val emergencyCallTimerStateDataList = ArrayList<EmergencyCallTimerStateData>()

        for (emergencyCallTimerStateEntity in emergencyCallTimerStateEntityList) {
            if (emergencyCallTimerStateEntity.timerName.isNotEmpty()) {

                val eventInfo =
                    getEventInfo(
                        emergencyCallTimerStateEntity.callId,
                        emergencyCallTimerStateEntity.uniqueId
                    )
                val emergencyCallTimerStateData = EmergencyCallTimerStateData(
                    emergencyCallTimerStateEntity.timerName,
                    emergencyCallTimerStateEntity.timerState,
                    EchoLocateDateUtils.convertToShemaDateFormat(emergencyCallTimerStateEntity.eventTimestamp),
                    EchoLocateDateUtils.convertToShemaDateFormat(emergencyCallTimerStateEntity.oemTimestamp),
                    eventInfo
                )

                emergencyCallTimerStateDataList.add(emergencyCallTimerStateData)
            } else {
                numDiscardedIntents++
            }
        }
        return emergencyCallTimerStateDataList
    }

    /**
     * Generated an object of CarrierConfigData
     * Based on data class [CarrierConfigData]
     * @return [CarrierConfigData]
     */
    private fun getCarrierConfigData(callId: String): CarrierConfigData? {
        val carrierConfigDataEntity = voiceDao.getCarrierConfigDataEntity(callId)
        val delimiter = ","
        val standaloneBands5gKeys = carrierConfigDataEntity?.standaloneBands5gKeys
        val standaloneBands5gValues = carrierConfigDataEntity?.standaloneBands5gValues

        val bandKeys =
            standaloneBands5gKeys?.substring(1, "$standaloneBands5gKeys".length - 1)
                ?.split(delimiter) // substring is to eliminate the [] from keys
        val bandValues =
            standaloneBands5gValues?.substring(1, "$standaloneBands5gValues".length - 1)
                ?.split(delimiter) // substring is to eliminate the [] from values

        val bandConfigList = ArrayList<BandConfig>()

        //bandKeys and bandValues always be equal in size
        if (bandKeys != null && bandValues != null) {
            for ((index, keyValue) in bandKeys.withIndex()) {
                bandConfigList.add(BandConfig(keyValue, bandValues[index]))
            }
            EchoLocateLog.eLogD("carrierSa5gBandConfig list in voice report processor: $bandConfigList")
        }

        return if (carrierConfigDataEntity == null) {
            null
        } else {
            return CarrierConfigData(
                carrierConfigDataEntity.carrierVoiceConfig,
                carrierConfigDataEntity.carrierVoWiFiConfig,
                bandConfigList,
                carrierConfigDataEntity.carrierConfigVersion,
                EchoLocateDateUtils.convertToShemaDateFormat(carrierConfigDataEntity.eventTimestamp)
            )
        }
    }

    /**
     * This function deletes the the report data from db based on the status
     * @param reportStatus
     */
    fun deleteProcessedReports(reportStatus: String) {
        voiceRepository.deleteProcessedReports(reportStatus)
    }
}