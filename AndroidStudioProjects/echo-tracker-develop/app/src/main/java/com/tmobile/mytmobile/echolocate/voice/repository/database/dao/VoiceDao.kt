package com.tmobile.mytmobile.echolocate.voice.repository.database.dao

import androidx.room.*
import com.tmobile.mytmobile.echolocate.voice.repository.database.VoiceDatabaseConstants
import com.tmobile.mytmobile.echolocate.voice.repository.database.entity.*


/**
 * [VoiceDao] is responsible for defining the methods that access the database.
 * by using queries annotations
 */
@Dao
interface VoiceDao {

    /**
     * Inserts [CellInfoEntity] parameters into the database in a single transaction.
     * @param cellInfo :CellInfoEntity object
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertCellInfoEntity(cellInfo: CellInfoEntity)

    /**
     * Inserts [VoiceLocationEntity] parameters into the database in a single transaction.
     * @param location :VoiceLocationEntity object
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertLocationEntity(location: VoiceLocationEntity)

    /**
     * Inserts [BaseEchoLocateVoiceEntity] parameters into the database in a single transaction.
     * @param baseEchoLocateVoiceEntity :BaseEchoLocateVoiceEntity object
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertBaseEchoLocateVoiceEntity(baseEchoLocateVoiceEntity: BaseEchoLocateVoiceEntity): Long

    /**
     * Inserts [DetailedCallStateEntity] parameters into the database in a single transaction.
     * @param detailedCallStateDataEntity :DetailedCallStateEntity object
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertDetialedCallStateEntity(detailedCallStateDataEntity: DetailedCallStateEntity)

    /**
     * Inserts [VoiceReportEntity] parameters into the database in a single transaction.
     * @param voiceReportEntity : VoiceReportEntity object
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertVoiceReportEntity(voiceReportEntity: VoiceReportEntity)

    /**
     * update [BaseEchoLocateVoiceEntity] parameters into the database in a single transaction.
     * @param baseEchoLocateVoiceEntity : BaseEchoLocateVoiceEntity object
     */
    @Update(onConflict = OnConflictStrategy.IGNORE)
    fun updateAllBaseEchoLocateVoiceEntityEndTime(vararg baseEchoLocateVoiceEntity: BaseEchoLocateVoiceEntity)

    /**
     * insert [DetailedCallStateEntity] parameters into the database in a single transaction.
     * @param detailedCallStateDataEntity : DetailedCallStateEntity object
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAllDetailedCallStateDataEntity(vararg detailedCallStateDataEntity: DetailedCallStateEntity)

    /**
     * Inserts [VoiceReportEntity] parameters into the database in a single transaction.
     * @param voiceReportEntity : VoiceReportEntity object
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAllVoiceReportEntity(vararg voiceReportEntity: VoiceReportEntity)

    /**
     * get voice report entities list
     */
    @Query("SELECT * FROM " + VoiceDatabaseConstants.VOICE_REPORT_TABLE_NAME)
    fun getVoiceReportEntity(): List<VoiceReportEntity>

    /**
     * get voice report entities list based on the state time and ent time
     * @param startTime  of voice report entity
     * @param endTime of voice report entity
     */
    @Query("SELECT * FROM " + VoiceDatabaseConstants.VOICE_REPORT_TABLE_NAME + " WHERE startTime >= :startTime AND endTime <= :endTime")
    fun getVoiceReportEntity(startTime: Long, endTime: Long): List<VoiceReportEntity>

    /**
     * Get [BaseEchoLocateVoiceEntity] parameters from the database in a single transaction.
     * @param (status: String): List<BaseEchoLocateVoiceEntity>
     */
    @Query("SELECT * FROM " + VoiceDatabaseConstants.VOICE_ECHO_LOCATE_BASE_TABLE_NAME + " WHERE status = :status")
    fun getBaseEchoLocateVoiceEntity(status: String): List<BaseEchoLocateVoiceEntity>

    /**
     * get base echo locate voice entity based on the start time and end time
     * @param startTime of base echo locate entity
     * @param endTime of base echo locate entity
     */
    @Query("SELECT * FROM " + VoiceDatabaseConstants.VOICE_ECHO_LOCATE_BASE_TABLE_NAME + " WHERE status = :status AND startTime >= :startTime AND endTime <= :endTime")
    fun getBaseEchoLocateVoiceEntity(
        status: String,
        startTime: Long,
        endTime: Long
    ): List<BaseEchoLocateVoiceEntity>

    /**
     * Get [BaseEchoLocateVoiceEntity] parameters from the database in a single transaction.
     * @param (status: String): List<BaseEchoLocateVoiceEntity>
     */
    @Query("SELECT * FROM " + VoiceDatabaseConstants.VOICE_ECHO_LOCATE_BASE_TABLE_NAME)
    fun getAllBaseEchoLocateVoiceEntity(): List<BaseEchoLocateVoiceEntity>

    /**
     * Update status of data
     * replace oldStatus to status(new)
     */
    @Query("UPDATE " + VoiceDatabaseConstants.VOICE_ECHO_LOCATE_BASE_TABLE_NAME + " SET status = :status WHERE status = :oldStatus")
    fun updateBaseEchoLocateVoiceEntityStatus(status: String, oldStatus: String)

    /**
     * update base echo locate voice entity end time
     * @param sessionId : session id for the base echo locate voice entity
     * @param endTime : endTime of the voice call end state
     */
    @Query("UPDATE " + VoiceDatabaseConstants.VOICE_ECHO_LOCATE_BASE_TABLE_NAME + " SET endTime = :endTime WHERE sessionId = :sessionId")
    fun updateBaseEchoLocateVoiceEntityEndTime(sessionId: String, endTime: Long)

    /**
     * update all base echo locate voice entity statuses
     * @param baseEchoLocateVoiceEntity :BaseEchoLocateVoiceEntity to update all the base echo locate voice entity
     */
    @Update(onConflict = OnConflictStrategy.IGNORE)
    fun updateAllBaseEchoLocateVoiceEntityStatus(vararg baseEchoLocateVoiceEntity: BaseEchoLocateVoiceEntity)

    /**
     * get all base echo locate voice entity ended list
     */
    @Query("SELECT * FROM " + VoiceDatabaseConstants.VOICE_ECHO_LOCATE_BASE_TABLE_NAME + " WHERE endTime != 0 AND status = :status")
    fun getBaseEchoLocateVoiceEntityENDED(status: String): List<BaseEchoLocateVoiceEntity>

    /**
     * get all base echo locate voice entity till data not ended
     */
    @Query("SELECT * FROM " + VoiceDatabaseConstants.VOICE_ECHO_LOCATE_BASE_TABLE_NAME + " WHERE status = :status AND " +
            "startTime < (SELECT endTime FROM " + VoiceDatabaseConstants.VOICE_ECHO_LOCATE_BASE_TABLE_NAME +" WHERE endTime " +
            "= (SELECT max(endTime) FROM " + VoiceDatabaseConstants.VOICE_ECHO_LOCATE_BASE_TABLE_NAME + "))")
    fun getBaseEchoLocateVoiceEntityTillEnded(status: String): List<BaseEchoLocateVoiceEntity>

    /**
     * get all base echo locate voice entity not ended list
     */
    @Query("SELECT * FROM " + VoiceDatabaseConstants.VOICE_ECHO_LOCATE_BASE_TABLE_NAME + " WHERE endTime = 0 AND status = :status AND :currentTime - startTime >= :timeInterval")
    fun getBaseEchoLocateVoiceEntityNotENDED(
        status: String,
        currentTime: Long,
        timeInterval: Long
    ): List<BaseEchoLocateVoiceEntity>

    /**
     * get base echo locate voice entity based on the session id
     */
    @Query("SELECT * FROM " + VoiceDatabaseConstants.VOICE_ECHO_LOCATE_BASE_TABLE_NAME + " WHERE sessionId = :sessionId")
    fun getBaseEchoLocateVoiceEntityBySessionID(sessionId: String): BaseEchoLocateVoiceEntity

    /**
     * Get data from intent Entity's, as a list of specific intents with parameters
     */

    /**
     * get all app triggered call data entities list based on call id
     * @param callId: String used for getting app triggered call data entities
     */
    @Query("SELECT * FROM " + VoiceDatabaseConstants.VOICE_APP_TRIGGERED_CALL_DATA_TABLE_NAME + " WHERE callId =:callId")
    fun getAppTriggeredCallDataEntity(callId: String): List<AppTriggeredCallDataEntity>

    /**
     * get all call setting entities list based on call id
     * @param callId: String used for getting call setting entities
     */
    @Query("SELECT * FROM " + VoiceDatabaseConstants.VOICE_CALL_SETTING_DATA_TABLE_NAME + " WHERE callId =:callId")
    fun getCallSettingDataEntity(callId: String): List<CallSettingDataEntity>

    /**
     * get detailed call state data entities list based on call id
     * @param callId: String used for getting detailed call state data entities
     */
    @Query("SELECT * FROM " + VoiceDatabaseConstants.VOICE_DETAILED_CALL_STATE_TABLE_NAME + " WHERE callId =:callId")
    fun getDetailedCallStateDataEntity(callId: String): List<DetailedCallStateEntity>

    /**
     * get all ims signalling data entities list based on call id
     * @param callId: String used for getting ims signalling data entities
     */
    @Query("SELECT * FROM " + VoiceDatabaseConstants.VOICE_IMS_SIGNALLING_DATA_TABLE_NAME + " WHERE callId =:callId")
    fun getImsSignallingDataEntity(callId: String): List<ImsSignallingEntity>

    /**
     * get rtpd state data entities list based on call id
     * @param callId: String used for getting rtpdl state data entities
     */
    @Query("SELECT * FROM " + VoiceDatabaseConstants.VOICE_RTPDL_STATE_DATA_TABLE_NAME + " WHERE callId =:callId")
    fun getRtpdlStateDataEntity(callId: String): List<RtpdlStateEntity>

    /**
     * get all ui cal state data entities list based on call id
     * @param callId: String used for getting ui call state data entities
     */
    @Query("SELECT * FROM " + VoiceDatabaseConstants.VOICE_UI_CALL_STATE_DATA_TABLE_NAME + " WHERE callId =:callId")
    fun getUiCallStateDataEntity(callId: String): List<UiCallStateEntity>

    /**
     * get all radio handover data entities list based on call id
     * @param callId: String used for getting handover data entities
     */
    @Query("SELECT * FROM " + VoiceDatabaseConstants.VOICE_RADIO_HANDOVER_DATA_TABLE_NAME + " WHERE callId =:callId")
    fun getRadioHandoverDataEntity(callId: String): List<RadioHandoverEntity>

    /**
     * get OEMSV entity based on call id
     * @param callId: String used for OEMSV entity
     */
    @Query("SELECT * FROM " + VoiceDatabaseConstants.VOICE_OEM_SOFTWARE_VERSION_TABLE_NAME + " WHERE callId =:callId")
    fun getOEMSVEntity(callId: String): OEMSoftwareVersionEntity

    /**
     * get voice location info based on call id and unique id
     * @param callId: String used for location entity
     * @param uniqueId: String used for the location entity which is stored before in voice location entity
     */
    @Query("SELECT * FROM " + VoiceDatabaseConstants.VOICE_LOCATION_TABLE_NAME + " WHERE callId =:callId AND uniqueId =:uniqueId")
    fun getLocationDataEntity(callId: String, uniqueId: String): VoiceLocationEntity?

    /**
     * get cell info based on call id and unique id
     * @param callId: String used for cell entity
     * @param uniqueId: String used for the location entity which is stored before in voice cell entity
     */
    @Query("SELECT * FROM " + VoiceDatabaseConstants.VOICE_CELL_INFO_DATA_TABLE_NAME + " WHERE callId =:callId AND uniqueId =:uniqueId")
    fun getCellInfoEntity(callId: String, uniqueId: String): CellInfoEntity

    /**
     * Inserts [CallSettingDataEntity] parameters into the database in a single transaction.
     * @param callSettingDataEntity :CallSettingDataEntity object
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertCallSettingEntity(callSettingDataEntity: CallSettingDataEntity)

    /**
     * Inserts [UiCallStateEntity] parameters into the database in a single transaction.
     * @param uiCallStateEntity :UiCallStateEntity object
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertUiCallStateEntity(uiCallStateEntity: UiCallStateEntity)

    /**
     * Gets inserted sessionId from voice_echo_locate_base_data database
     */
    @Query("SELECT sessionId from " + VoiceDatabaseConstants.VOICE_ECHO_LOCATE_BASE_TABLE_NAME)
    fun getSessionIdList(): List<String>

    /**
     * deletes record from scheduler job table
     */
    @Query("DELETE FROM " + VoiceDatabaseConstants.VOICE_ECHO_LOCATE_BASE_TABLE_NAME)
    fun deleteBaseEchoLocateVoiceEntity()

    /**
     * Inserts [RadioHandoverEntity] parameters into the database in a single transaction.
     * @param radioHandoverEntity :RadioHandoverEntity object
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertRadioHandOverEntity(radioHandoverEntity: RadioHandoverEntity)

    /**
     * Inserts [ImsSignallingEntity] parameters into the database in a single transaction.
     * @param imsSignallingEntity :ImsSignallingEntity object
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertImsSignallingEntity(imsSignallingEntity: ImsSignallingEntity)

    /**
     * Inserts [AppTriggeredCallDataEntity] parameters into the database in a single transaction.
     * @param appTriggeredCallDataEntity :AppTriggeredCallDataEntity object
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAppTriggeredCallDataEntity(appTriggeredCallDataEntity: AppTriggeredCallDataEntity)

    /**
     * Inserts [RtpdlStateEntity] parameters into the database
     * @param rtpdlStateDataEntity : RtpdlStateEntity object
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertRtpdlStateEntity(rtpdlStateDataEntity: RtpdlStateEntity)

    /**
     * Inserts [OEMSoftwareVersionEntity] parameters into the database
     * @param oemSoftwareVersionEntity : OEMSoftwareVersionEntity object
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertOEMSoftwareVersionEntity(oemSoftwareVersionEntity: OEMSoftwareVersionEntity)

    /**
     * Gets list of [VoiceReportEntity]
     */
    @Query("SELECT * FROM " + VoiceDatabaseConstants.VOICE_REPORT_TABLE_NAME)
    fun getVoiceReportEntityList(): List<VoiceReportEntity>

    /**
     * Update status of records from report table
     */
    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateVoiceReportEntityList(vararg voiceReportEntity: VoiceReportEntity)


    /**
     * get voice report entities list based on the state time and ent time
     * @param startTime of voice report entity
     * @param endTime of voice report entity
     */
    @Query("SELECT * FROM " + VoiceDatabaseConstants.VOICE_REPORT_TABLE_NAME + " WHERE startTime >= :startTime AND endTime <= :endTime")
    fun getVoiceSingleSessionReportEntityList(
        startTime: String,
        endTime: String
    ): List<VoiceReportEntity>

    /**
     * Deletes the reports from VoiceReportEntity with status REPORTING
     * @param status
     */
    @Query("DELETE FROM " + VoiceDatabaseConstants.VOICE_REPORT_TABLE_NAME + " WHERE reportStatus = :status")
    fun deleteProcessedReports(status: String)

    /**
     * Deletes all the raw data from all the voice tables
     *
     * @param baseEchoLocateVoiceEntity List of entity classes that needs to be deleted. It will have
     * all the session id's which are processed for reports generation so that the same session id's
     * considered as raw data in other tables and  will be deleted here
     */
    @Delete
    fun deleteRawDataFromAllTables(baseEchoLocateVoiceEntity: List<BaseEchoLocateVoiceEntity>)

    /**
     * Inserts [EmergencyCallTimerStateEntity] parameters into the database in a single transaction.
     * @param emergencyCallTimerStateEntity :EmergencyCallTimerStateEntity object
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertEmergencyCallTimerStateEntity(emergencyCallTimerStateEntity: EmergencyCallTimerStateEntity)

    /**
     * get all Emergency Call Timer State entities list based on call id
     * @param callId: String used for getting app triggered call data entities
     */
    @Query("SELECT * FROM " + VoiceDatabaseConstants.VOICE_EMERGENCY_CALL_TIMER_STATE + " WHERE callId =:callId")
    fun getEmergencyCallTimerStateEntityList(callId: String): List<EmergencyCallTimerStateEntity>

    /**
     * Inserts [CarrierConfigDataEntity] parameters into the database in a single transaction.
     * @param carrierConfigDataEntity :CarrierConfigDataEntity object
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertCarrierConfigDataEntity(carrierConfigDataEntity: CarrierConfigDataEntity)

    /**
     * get all Carrier Config Data entities list based on call id
     */
    @Query("SELECT * FROM " + VoiceDatabaseConstants.VOICE_CARRIER_CONFIG + " WHERE callId =:callId")
    fun getCarrierConfigDataEntity(callId: String): CarrierConfigDataEntity?

}