package com.tmobile.mytmobile.echolocate.voice.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import com.tmobile.echolocate.CallSessionProto
import com.tmobile.echolocate.CallStatusProto
import com.tmobile.mytmobile.echolocate.voice.repository.database.EchoLocateVoiceDatabase
import com.tmobile.mytmobile.echolocate.voice.repository.database.dao.VoiceDao
import com.tmobile.mytmobile.echolocate.voice.repository.database.entity.*
import com.tmobile.mytmobile.echolocate.voice.repository.datastore.CallStatusSerializer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking


/**
 * The [VoiceRepository] repository class will be responsible for interacting with the Room database
 * and will need to provide methods that use the DAO to insert, delete and query product records.
 * @param context :Context the context passed from activity
 */
class VoiceRepository(context: Context) {

    companion object {
        private var currentCallID: String? = null
        private var currentCallDataStore: DataStore<CallSessionProto>? = null

        fun getVoiceDataStoreFor(callID: String): DataStore<CallSessionProto>? {
            if (currentCallID == callID) {
                return currentCallDataStore
            }
            return null
        }

        fun setVoiceDataStoreFor(callID: String, voiceDataStore: DataStore<CallSessionProto>) {
            currentCallID = callID
            currentCallDataStore = voiceDataStore
        }

        /**
         * CALL STATUS
         */
        var gcallStatusDataStore: DataStore<CallStatusProto>? = null


    }

    init {
        if (gcallStatusDataStore == null) {
            val callStatusDataStore = DataStoreFactory.create(
                serializer = CallStatusSerializer,
                produceFile = { context.applicationContext.dataStoreFile("callstatus.pb") }
            )
            gcallStatusDataStore = callStatusDataStore
        }
    }

    /**
     * gets the voice DAO instance defined as a abstract class in [EchoLocateVoiceDatabase]
     */
    private val voiceDao: VoiceDao =
        EchoLocateVoiceDatabase.getEchoLocateVoiceDatabase(context).voiceDao()

    /**
     * Calls the insert method defined in DAO to insert [CellInfoEntity] parameters into the database
     * @param cellInfoEntity :CellInfoEntity object
     */
    fun insertCellInfoEntity(cellInfoEntity: CellInfoEntity) {
        if (getBaseEchoLocateVoiceEntityBySessionID(cellInfoEntity.callId) != null) {
            voiceDao.insertCellInfoEntity(cellInfoEntity)
        }
    }

    /**
     * Calls the insert method defined in DAO to insert [VoiceLocationEntity] parameters into the database
     * @param location :BaseEchoLocateVoiceEntity object
     */
    fun insertVoiceLocationEntity(location: VoiceLocationEntity) {
        if (getBaseEchoLocateVoiceEntityBySessionID(location.callId) != null) {
            voiceDao.insertLocationEntity(location)
        }
    }

    /**
     * Calls the insert method defined in DAO to insert [BaseEchoLocateVoiceEntity] parameters into the database
     * @param baseEchoLocateVoiceEntity :BaseEchoLocateVoiceEntity object
     */
    fun insertBaseEchoLocateVoiceEntity(baseEchoLocateVoiceEntity: BaseEchoLocateVoiceEntity): Long {
        return voiceDao.insertBaseEchoLocateVoiceEntity(baseEchoLocateVoiceEntity)
    }

    /**
     * InserCalls the insert method defined in DAO to insert [DetailedCallStateEntity] parameters into the database
     * @param detailedCallStateDataEntity :DetailedCallStateEntity object
     */
    fun insertDetailedCallStateEntity(detailedCallStateDataEntity: DetailedCallStateEntity) {
        if (getBaseEchoLocateVoiceEntityBySessionID(detailedCallStateDataEntity.callId) != null) {
            voiceDao.insertDetialedCallStateEntity(detailedCallStateDataEntity)
        }
    }

    /**
     * insertVoiceReportEntity the insert fun defined in DAO to insert [VoiceReportEntity] parameters into the database
     * @param voiceReportEntityList :VoiceReportEntity object
     */
    fun insertAllVoiceReportEntity(voiceReportEntityList: List<VoiceReportEntity>) {
        voiceDao.insertAllVoiceReportEntity(*voiceReportEntityList.toTypedArray())
    }


    /**
     * get echo locate voice entity based on the session id.
     * @param sessionId :String object
     */
    fun getBaseEchoLocateVoiceEntityBySessionID(sessionId: String): BaseEchoLocateVoiceEntity? =
        runBlocking(Dispatchers.Default) {
            val result =
                async { voiceDao.getBaseEchoLocateVoiceEntityBySessionID(sessionId) }.await()
            return@runBlocking result
        }
    //crash  Caused by: java.lang.IllegalStateException: Cannot access database on the main thread since it may potentially lock the UI for a long period of time.

//    fun getBaseEchoLocateVoiceEntityBySessionID(sessionId: String) = runBlocking(Dispatchers.Default) {
//        val result = async {
//            voiceDao.getBaseEchoLocateVoiceEntityBySessionID(sessionId)
//        }.await()
//        return@runBlocking result
//    }


    /**
     * Calls the insert method defined in DAO to insert [CallSettingDataEntity] parameters into the database
     * @param callSettingDataEntity :CallSettingDataEntity object
     */
    fun insertCallSettingEntity(callSettingDataEntity: CallSettingDataEntity) {
        if (getBaseEchoLocateVoiceEntityBySessionID(callSettingDataEntity.callId) != null) {
            voiceDao.insertCallSettingEntity(callSettingDataEntity)
        }
    }

    /**
     * Calls the insert method defined in DAO to insert [UiCallStateEntity] parameters into the database
     * @param uiCallStateEntity :UiCallStateEntity object
     */
    fun insertUiCallStateEntity(uiCallStateEntity: UiCallStateEntity) {
        if (getBaseEchoLocateVoiceEntityBySessionID(uiCallStateEntity.callId) != null) {
            voiceDao.insertUiCallStateEntity(uiCallStateEntity)
        }
    }

    /**
     * Calls the insert method defined in DAO to insert [RadioHandoverEntity] parameters into the database
     * @param radioHandoverEntity :RadioHandoverEntity object
     */
    fun insertRadioHandOverEntity(radioHandoverEntity: RadioHandoverEntity) {
        if (getBaseEchoLocateVoiceEntityBySessionID(radioHandoverEntity.callId) != null) {
            voiceDao.insertRadioHandOverEntity(radioHandoverEntity)
        }
    }

    /**
     * Calls the insert method defined in DAO to insert [ImsSignallingEntity] parameters into the database
     * @param imsSignallingEntity :RadioHandoverEntity object
     */
    fun insertImsSignallingEntity(imsSignallingEntity: ImsSignallingEntity) {
        if (getBaseEchoLocateVoiceEntityBySessionID(imsSignallingEntity.callId) != null) {
            voiceDao.insertImsSignallingEntity(imsSignallingEntity)
        }
    }

    /**
     * InserCalls the insert method defined in DAO to insert [AppTriggeredCallDataEntity] parameters into the database
     * @param appTriggeredCallDataEntity :AppTriggeredCallDataEntity object
     */
    fun insertAppTriggeredCallDataEntity(appTriggeredCallDataEntity: AppTriggeredCallDataEntity) {
        if (getBaseEchoLocateVoiceEntityBySessionID(appTriggeredCallDataEntity.callId) != null) {
            voiceDao.insertAppTriggeredCallDataEntity(appTriggeredCallDataEntity)
        }
    }

    /**
     *  calls insert method defined in DAO to insert [RtpdlStateEntity]  params into the database
     *  @param rtpdlStateDataEntity :RtpdlStateEntity
     */
    fun insertRtpdlStateEntity(rtpdlStateDataEntity: RtpdlStateEntity) {
        if (getBaseEchoLocateVoiceEntityBySessionID(rtpdlStateDataEntity.callId) != null) {
            voiceDao.insertRtpdlStateEntity(rtpdlStateDataEntity)
        }
    }

    /**
     * oemSoftwareVersionEntity the insert fun defined in DAO to insert [OEMSoftwareVersionEntity] parameters into the database
     * @param oemSoftwareVersionEntity :OEMSoftwareVersionEntity object
     */
    fun insertOEMSoftwareVersionEntity(oemSoftwareVersionEntity: OEMSoftwareVersionEntity) {
        if (getBaseEchoLocateVoiceEntityBySessionID(oemSoftwareVersionEntity.callId) != null) {
            voiceDao.insertOEMSoftwareVersionEntity(oemSoftwareVersionEntity)
        }
    }

    /**
     * update base echo locate voice entity end time based on the session id into the database
     * @param sessionId :String
     * @param endTime :Long
     */
    fun updateBaseEchoLocateVoiceEntityEndTime(sessionId: String, endTime: Long) {
        voiceDao.updateBaseEchoLocateVoiceEntityEndTime(sessionId, endTime)
    }

    /**
     * get session id list from voice dao
     */
    fun getSessionId(): List<String> {
        return voiceDao.getSessionIdList()
    }

    /**
     * This function deletes the the report data from db based on the status
     * @param reportStatus
     */
    fun deleteProcessedReports(reportStatus: String) {
        voiceDao.deleteProcessedReports(reportStatus)
    }

    /**
     * Deletes all the raw data from all the lte tables once it is processed for production build
     * @param baseEchoLocateVoiceEntityList will have all the processed data list, so that data can be deleted from other tables
     */
    fun deleteRawData(baseEchoLocateVoiceEntityList: List<BaseEchoLocateVoiceEntity>) {
        voiceDao.deleteRawDataFromAllTables(baseEchoLocateVoiceEntityList)
    }

    /**
     * Insert function defined in DAO to insert [EmergencyCallTimerStateEntity] into the database
     * @param emergencyCallTimerStateEntity :EmergencyCallTimerStateEntity object
     */
    fun insertEmergencyCallTimerStateEntity(emergencyCallTimerStateEntity: EmergencyCallTimerStateEntity) {
        if (getBaseEchoLocateVoiceEntityBySessionID(emergencyCallTimerStateEntity.callId) != null) {
            voiceDao.insertEmergencyCallTimerStateEntity(emergencyCallTimerStateEntity)
        }
    }

    /**
     * Insert function defined in DAO to insert [CarrierConfigDataEntity] into the database
     * @param carrierConfigDataEntity :CarrierConfigDataEntity object
     */
    fun insertCarrierConfigDataEntity(carrierConfigDataEntity: CarrierConfigDataEntity) {
        if (getBaseEchoLocateVoiceEntityBySessionID(carrierConfigDataEntity.callId) != null) {
            voiceDao.insertCarrierConfigDataEntity(carrierConfigDataEntity)
        }

    }
}