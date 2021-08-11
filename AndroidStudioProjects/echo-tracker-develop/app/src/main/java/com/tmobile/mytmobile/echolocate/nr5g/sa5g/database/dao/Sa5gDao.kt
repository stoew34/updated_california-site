package com.tmobile.mytmobile.echolocate.nr5g.sa5g.database.dao

import androidx.room.*
import com.tmobile.mytmobile.echolocate.nr5g.sa5g.database.Sa5gDatabaseConstants
import com.tmobile.mytmobile.echolocate.nr5g.sa5g.database.entity.*

/**
 * [Sa5gDao] is responsible for defining the methods that access the database.
 * by using queries annotations
 */
@Dao
interface Sa5gDao {

    /**
     * Update status of BaseEchoLocateSa5gEntity
     */
    @Query("SELECT * FROM " + Sa5gDatabaseConstants.SA5G_ECHO_LOCATE_BASE_TABLE_NAME + " WHERE status = :status")
    fun getBaseEchoLocateSa5gEntityByStatus(status: String): List<BaseEchoLocateSa5gEntity>

    /**
     * Get base echo locate Sa5g entity based on the session id
     */
    @Query("SELECT * FROM " + Sa5gDatabaseConstants.SA5G_ECHO_LOCATE_BASE_TABLE_NAME + " WHERE sessionId = :sessionId")
    fun getBaseEchoLocateSaGEntityBySessionID(sessionId: String): BaseEchoLocateSa5gEntity

    /**
     * Gets [Sa5gDeviceInfoEntity] parameters from the database in a single transaction based on the session id.
     */
    @Query("SELECT * FROM " + Sa5gDatabaseConstants.SA5G_DEVICE_INFO_TABLE_NAME + " WHERE sessionId = :sessionId")
    fun getSa5gDeviceInfoEntity(sessionId: String): Sa5gDeviceInfoEntity?

    /**
     * Gets [Sa5gLocationEntity] parameters from the database in a single transaction based on the session id.
     */
    @Query("SELECT * FROM " + Sa5gDatabaseConstants.SA5G_LOCATION_TABLE_NAME + " WHERE sessionId = :sessionId")
    fun getSa5gLocationEntity(sessionId: String): Sa5gLocationEntity?

    /**
     * Gets [Sa5gTriggerEntity] parameters from the database in a single transaction based on the session id.
     */
    @Query("SELECT * FROM " + Sa5gDatabaseConstants.SA5G_TRIGGER_TABLE_NAME + " WHERE sessionId = :sessionId")
    fun getSa5gTriggerEntity(sessionId: String): Sa5gTriggerEntity?

    /**
     * Gets [Sa5gWiFiStateEntity] parameters from the database in a single transaction based on the session id.
     */
    @Query("SELECT * FROM " + Sa5gDatabaseConstants.SA5G_WIFI_STATE_TABLE_NAME + " WHERE sessionId = :sessionId")
    fun getSa5gWiFiStateEntity(sessionId: String): Sa5gWiFiStateEntity?

    /**
     * Gets [Sa5gActiveNetworkEntity] parameters from the database in a single transaction based on the session id.
     */
    @Query("SELECT * FROM " + Sa5gDatabaseConstants.SA5G_GET_ACTIVE_NETWORK_TABLE_NAME + " WHERE sessionId = :sessionId")
    fun getSa5gActiveNetworkEntity(sessionId: String): Sa5gActiveNetworkEntity?

    /**
     * Gets [Sa5gConnectedWifiStatusEntity] parameters from the database in a single transaction based on the session id.
     */
    @Query("SELECT * FROM " + Sa5gDatabaseConstants.SA5G_CONNECTED_WIFI_STATUS_TABLE_NAME + " WHERE sessionId = :sessionId")
    fun getSa5gConnectedWifiStatusEntity(sessionId: String): Sa5gConnectedWifiStatusEntity?

    /**
     * Gets [Sa5gOEMSVEntity] parameters from the database in a single transaction based on the session id.
     */
    @Query("SELECT * FROM " + Sa5gDatabaseConstants.SA5G_OEMSV_TABLE_NAME + " WHERE sessionId = :sessionId")
    fun getSa5gOEMSVEntity(sessionId: String): Sa5gOEMSVEntity?

    /**
     * Gets [Sa5gDownlinkCarrierLogsEntity] parameters from the database in a single transaction based on the session id.
     */
    @Query("SELECT * FROM " + Sa5gDatabaseConstants.SA5G_DOWNLINK_CARRIER_LOGS_TABLE_NAME + " WHERE sessionId = :sessionId")
    fun getSa5gDownlinkCarrierLogsEntity(sessionId: String): List<Sa5gDownlinkCarrierLogsEntity>

    /**
     * Gets [Sa5gUplinkCarrierLogsEntity] parameters from the database in a single transaction based on the session id.
     */
    @Query("SELECT * FROM " + Sa5gDatabaseConstants.SA5G_UPLINK_CARRIER_LOGS_TABLE_NAME + " WHERE sessionId = :sessionId")
    fun getSa5gUplinkCarrierLogsEntity(sessionId: String): List<Sa5gUplinkCarrierLogsEntity>

    /**
     * Gets [Sa5gRrcLogEntity] parameters from the database in a single transaction based on the session id.
     */
    @Query("SELECT * FROM " + Sa5gDatabaseConstants.SA5G_RRC_LOG_TABLE_NAME + " WHERE sessionId = :sessionId")
    fun getSa5gRrcLogEntity(sessionId: String): Sa5gRrcLogEntity?

    /**
     * Gets [Sa5gNetworkLogEntity] parameters from the database in a single transaction based on the session id.
     */
    @Query("SELECT * FROM " + Sa5gDatabaseConstants.SA5G_NETWORK_LOG_TABLE_NAME + " WHERE sessionId = :sessionId")
    fun getSa5gNetworkLogEntity(sessionId: String): Sa5gNetworkLogEntity?

    /**
     * Gets [Sa5gSettingsLogEntity] parameters from the database in a single transaction based on the session id.
     */
    @Query("SELECT * FROM " + Sa5gDatabaseConstants.SA5G_SETTINGS_LOG_TABLE_NAME + " WHERE sessionId = :sessionId")
    fun getSa5gSettingsLogEntity(sessionId: String): Sa5gSettingsLogEntity?

    /**
     * Gets [Sa5gUiLogEntity] parameters from the database in a single transaction based on the session id.
     */
    @Query("SELECT * FROM " + Sa5gDatabaseConstants.SA5G_UI_LOG_TABLE_NAME + " WHERE sessionId = :sessionId")
    fun getSa5gUiLogEntity(sessionId: String): Sa5gUiLogEntity?

    /**
     * Inserts [BaseEchoLocateSa5gEntity] parameters into the database in a single transaction.
     * @param baseEchoLocateSa5gEntity :BaseEchoLocateSa5gEntity object
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertBaseEchoLocateSa5gEntity(baseEchoLocateSa5gEntity: BaseEchoLocateSa5gEntity): Long

    /**
     * Inserts [Sa5gOEMSVEntity] parameters into the database in a single transaction.
     * @param sa5gOEMSVEntity: Sa5gOEMSVEntity object
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertSa5gOEMSVEntity(sa5gOEMSVEntity: Sa5gOEMSVEntity)

    /**
     * Inserts [Sa5gDeviceInfoEntity] parameters into the database in a single transaction.
     * @param sa5gDeviceInfoEntity: Sa5gDeviceInfoEntity object
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertSa5gDeviceInfoEntity(sa5gDeviceInfoEntity: Sa5gDeviceInfoEntity)

    /**
     * Inserts [Sa5gLocationEntity] parameters into the database in a single transaction.
     * @param sa5gLocationEntity: Sa5gLocationEntity object
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSa5gLocationEntity(sa5gLocationEntity: Sa5gLocationEntity)

    /**
     * Inserts [Sa5gTriggerEntity] parameters into the database in a single transaction.
     * @param sa5gTriggerEntity: Sa5gTriggerEntity object
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertSa5gTriggerEntity(sa5gTriggerEntity: Sa5gTriggerEntity): Long

    /**
     * Inserts [Sa5gDownlinkCarrierLogsEntity] parameters into the database in a single transaction.
     * @param sa5gDownlinkCarrierLogsEntity: Sa5gDownlinkCarrierLogsEntity object
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertSa5gDownlinkCarrierLogsEntity(sa5gDownlinkCarrierLogsEntity: Sa5gDownlinkCarrierLogsEntity)

    /**
     * Inserts [Sa5gRrcLogEntity] parameters into the database in a single transaction.
     * @param sa5gRrcLogEntity: Sa5gRrcLogEntity object
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertSa5gRrcLogEntity(sa5gRrcLogEntity: Sa5gRrcLogEntity)

    /**
     * Inserts [Sa5gNetworkLogEntity] parameters into the database in a single transaction.
     * @param sa5gNetworkLogEntity: Sa5gNetworkLogEntity object
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertSa5gNetworkLogEntity(sa5gNetworkLogEntity: Sa5gNetworkLogEntity)

    /**
     * Inserts [Sa5gSettingsLogEntity] parameters into the database in a single transaction.
     * @param sa5gSettingsLogEntity: Sa5gSettingsLogEntity object
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertSa5gSettingsLogEntity(sa5gSettingsLogEntity: Sa5gSettingsLogEntity)

    /**
     * Inserts [Sa5gUiLogEntity] parameters into the database in a single transaction.
     * @param sa5gUiLogEntity: Sa5gUiLogEntity object
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertSa5gUiLogEntity(sa5gUiLogEntity: Sa5gUiLogEntity)

    /**
     * Inserts [Sa5gConnectedWifiStatusEntity] parameters into the database in a single transaction.
     * @param sa5gConnectedWifiStatusEntity: Sa5gConnectedWifiStatusEntity object
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertSa5gConnectedWifiStatusEntity(sa5gConnectedWifiStatusEntity: Sa5gConnectedWifiStatusEntity)

    /**
     * Inserts [Sa5gActiveNetworkEntity] parameters into the database in a single transaction.
     * @param sa5gActiveNetworkEntity: Sa5gActiveNetworkEntity object
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertSa5gActiveNetworkEntity(sa5gActiveNetworkEntity: Sa5gActiveNetworkEntity)

    /**
     * Inserts [Sa5gWiFiStateEntity] parameters into the database in a single transaction.
     * @param sa5gWiFiStateEntity: Sa5gWiFiStateEntity object
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertSa5gWiFiStateEntity(sa5gWiFiStateEntity: Sa5gWiFiStateEntity)

    /**
     * Inserts [Sa5gCarrierConfigEntity] parameters into the database in a single transaction.
     * @param sa5gCarrierConfigEntity: Sa5gCarrierConfigEntity object
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertSa5gCarrierConfigEntity(sa5gCarrierConfigEntity: Sa5gCarrierConfigEntity)

    /**
     * Inserts [sa5gSingleSessionReportEntity] parameters into the database in a single transaction.
     * @param sa5gSingleSessionReportEntity : sa5gSingleSessionReportEntity object
     *
     * @return List of long values which are inserted into DB.
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAllSa5gSingleSessionReportEntity(vararg sa5gSingleSessionReportEntity: Sa5gSingleSessionReportEntity): List<Long>

    /**
     * Inserts [Sa5gDownlinkCarrierLogsEntity] parameters into the database in a single transaction.
     * @param sa5gDownlinkCarrierLogsEntity : Sa5gDownlinkCarrierLogsEntity object
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAllSa5gDownlinkCarrierLogsEntity(vararg sa5gDownlinkCarrierLogsEntity: Sa5gDownlinkCarrierLogsEntity)

    /**
     * Inserts [Sa5gUplinkCarrierLogsEntity] parameters into the database in a single transaction.
     * @param sa5gUplinkCarrierLogsEntity: Sa5gUplinkCarrierLogsEntity object
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAllSa5gUplinkCarrierLogsEntity(vararg sa5gUplinkCarrierLogsEntity: Sa5gUplinkCarrierLogsEntity)

    /**
     * Deletes all the raw data from all the Sa5g tables
     *
     * @param baseEchoLocateSa5gEntityList List of entity classes that needs to be deleted. It will have
     * all the session id's which are processed for reports generation so that the same session id's
     * considered as raw data in other tables and  will be deleted here
     */
    @Delete
    fun deleteRawDataFromAllTables(baseEchoLocateSa5gEntityList: List<BaseEchoLocateSa5gEntity>)

    /**
     * Deletes the reports from [Sa5gSingleSessionReportEntity] with status REPORTING
     * @param status
     */
    @Query("DELETE FROM " + Sa5gDatabaseConstants.SA5G_REPORT_TABLE_NAME + " WHERE reportStatus = :status")
    fun deleteProcessedReports(status: String)

    /**
     * Update status for all BaseEchoLocateSa5gEntity
     * @param baseEchoLocateSa5gEntity :BaseEchoLocateSa5gEntity to update all the BaseEchoLocateSa5gEntity items
     */
    @Update(onConflict = OnConflictStrategy.IGNORE)
    fun updateAllBaseEchoLocateSa5gEntityStatus(vararg baseEchoLocateSa5gEntity: BaseEchoLocateSa5gEntity)

    /**
     * Update status for a single record of BaseEchoLocateSa5gEntity
     * @param baseEchoLocateSa5gEntity :BaseEchoLocateSa5gEntity to update the BaseEchoLocateSa5gEntity item
     */
    @Update(onConflict = OnConflictStrategy.IGNORE)
    fun updateBaseEchoLocateSa5gEntityStatus(baseEchoLocateSa5gEntity: BaseEchoLocateSa5gEntity)

    /**
     * Gets list of [Sa5gSingleSessionReportEntity]
     */
    @Query("SELECT * FROM " + Sa5gDatabaseConstants.SA5G_REPORT_TABLE_NAME)
    fun getSa5gSingleSessionReportEntityList(): List<Sa5gSingleSessionReportEntity>

    /**
     * Update status of records from report table
     */
    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateSa5gReportEntityList(vararg nr5gReportEntity: Sa5gSingleSessionReportEntity)

    /**
     * get list of Sa5gSingleSessionReportEntity based on the state time and ent time
     * @param startTime start time of Sa5g Single Session
     * @param endTime time of Sa5g Single Session
     */
    @Query(
        "SELECT * FROM " + Sa5gDatabaseConstants.SA5G_REPORT_TABLE_NAME +
                " WHERE strftime(eventTimestamp) BETWEEN strftime(:startTime) AND strftime(:endTime) "
    )
    fun getNr5gSingleSessionReportEntityList(
        startTime: String,
        endTime: String
    ): List<Sa5gSingleSessionReportEntity>

    /**
     * Gets [Sa5gCarrierConfigEntity] parameters from the database in a single transaction based on the session id.
     */
    @Query("SELECT * FROM " + Sa5gDatabaseConstants.SA5G_CARRIER_CONFIG_TABLE_NAME + " WHERE sessionId = :sessionId")
    fun getSa5gCarrierConfigEntity(sessionId: String): Sa5gCarrierConfigEntity?

}