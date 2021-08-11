package com.tmobile.mytmobile.echolocate.nr5g.nsa5g.database.dao

import androidx.room.*
import com.tmobile.mytmobile.echolocate.lte.database.LteDatabaseConstants
import com.tmobile.mytmobile.echolocate.nr5g.nsa5g.database.Nr5gDatabaseConstants
import com.tmobile.mytmobile.echolocate.nr5g.nsa5g.database.entity.*

/**
 * [Nr5gDao] is responsible for defining the methods that access the database.
 * by using queries annotations
 */
@Dao
interface Nr5gDao {

    /**
     * Update status of BaseEchoLocateNr5gEntity
     */
    @Query("SELECT * FROM " + Nr5gDatabaseConstants.NR5G_ECHO_LOCATE_BASE_TABLE_NAME + " WHERE status = :status")
    fun getBaseEchoLocateNr5gEntityByStatus(status: String): List<BaseEchoLocateNr5gEntity>

    /**
     * Get base echo locate Nr5g entity based on the session id
     */
    @Query("SELECT * FROM " + Nr5gDatabaseConstants.NR5G_ECHO_LOCATE_BASE_TABLE_NAME + " WHERE sessionId = :sessionId")
    fun getBaseEchoLocateNrGEntityBySessionID(sessionId: String): BaseEchoLocateNr5gEntity

    /**
     * Inserts [BaseEchoLocateNr5gEntity] parameters into the database in a single transaction.
     * @param baseEchoLocateFiveGEntity :BaseEchoLocateNr5gEntity object
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertBaseEchoLocateNr5gEntity(baseEchoLocateFiveGEntity: BaseEchoLocateNr5gEntity)

    /**
     * Inserts [EndcUplinkLogEntity] parameters into the database in a single transaction.
     * @param endcUplinkLogEntity :EndcUpLinkLogEntity object
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertEndcUplinkLogEntity(endcUplinkLogEntity: EndcUplinkLogEntity)

    /**
     * Inserts [Nr5gOEMSVEntity] parameters into the database in a single transaction.
     * @param nr5gOEMSVEntity: Nr5gOEMSVEntity object
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertNr5gOEMSVEntity(nr5gOEMSVEntity: Nr5gOEMSVEntity)

    /**
     * Inserts [DeviceInfoEntity] parameters into the database in a single transaction.
     * @param deviceInfoEntity: DeviceInfoEntity object
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertNr5gDeviceInfoEntity(deviceInfoEntity: Nr5gDeviceInfoEntity)

    /**
     * Inserts [ConnectedWifiStatusEntity] parameters into the database in a single transaction
     * @Param ConnectedWifiStatusEntity :WifiConnectionStatusEntity object
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertConnectedWifiStatusEntity(connectedWifiStatusEntity: ConnectedWifiStatusEntity)

    /**
     * Inserts [Nr5gWifiStateEntity] parameters into the database in a single transaction.
     * @param nr5gWifiStateEntity: Nr5gWifiStateEntity object
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertNr5gWifiStateEntity(nr5gWifiStateEntity: Nr5gWifiStateEntity)

    /**
     * Get [Nr5gDeviceInfoEntity] based on the session id
     */
    @Query("SELECT * FROM " + Nr5gDatabaseConstants.NR5G_DEVICE_INFO_TABLE_NAME + " WHERE sessionId = :sessionId")
    fun getDeviceInfoEntityBySessionID(sessionId: String): Nr5gDeviceInfoEntity

    /**
     * Inserts [Nr5gDataNetworkTypeEntity] parameters into the database in a single transaction.
     * @param nr5gDataNetworkTypeEntity: Nr5gDataNetworkTypeEntity object
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertNr5gDataNetworkTypeEntity(nr5gDataNetworkTypeEntity: Nr5gDataNetworkTypeEntity)

    /**
     * Get [GetWifiStateEntity] based on the session id
     */
    @Query("SELECT * FROM " + Nr5gDatabaseConstants.NR5G_GET_WIFI_STATE_TABLE_NAME + " WHERE sessionId = :sessionId")
    fun getNr5GGetWifiStateEntityBySessionID(sessionId: String): Nr5gWifiStateEntity

    /**
     * Inserts [Nr5gStatusEntity] parameters into the database in a single transaction.
     * @param nr5gStatusEntity: Nr5gStatusEntity object
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertNr5gStatusEntity(nr5gStatusEntity: Nr5gStatusEntity)

    /**
     * Inserts [LocationEntity] parameters into the database in a single transaction.
     * @param nr5GLocationEntity: LocationEntity object
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertNr5gLocationEntity(nr5GLocationEntity: Nr5gLocationEntity)

    /**
     * Updates [LocationEntity] parameters into the database in a single transaction.
     * @param nr5GLocationEntity: LocationEntity object
     */
    @Update(onConflict = OnConflictStrategy.IGNORE)
    fun updateNr5GLocationEntity(nr5GLocationEntity: Nr5gLocationEntity)

    /**
     * Inserts [GetNetworkIdentityEntity] parameters into the database in a single transaction.
     * @param nr5gOEMSVEntity: Nr5gOEMSVEntity object
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertNr5gNetworkIdentityEntity(nr5gNetworkIdentityEntity: Nr5gNetworkIdentityEntity)

    /**
     * Inserts [Nr5gTriggerEntity] parameters into the database in a single transaction.
     * @param nr5gTriggerEntity: Nr5gTriggerEntity object
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertNr5gTriggerEntity(nr5gTriggerEntity: Nr5gTriggerEntity)

    /**
     * Inserts [EndcLteLogEntity] parameters into the database in a single transaction.
     * @param endcLteLogEntity: EndcLteLogEntity object
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertEndcLteLogEntity(endcLteLogEntity: EndcLteLogEntity)

    /**
     * Inserts [Nr5gMmwCellLogEntity] parameters into the database in a single transaction.
     * @param nr5gMmwCellLogEntity: Nr5gMmwCellLogEntity object
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertNr5gMmwCellLogEntity(nr5gMmwCellLogEntity: Nr5gMmwCellLogEntity)

    /**
     * Inserts [Nr5gUiLogEntity] parameters into the database in a single transaction.
     * @param nr5gUiLogEntity: Nr5gUiLogEntity object
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertNr5gUiLogEntity(nr5gUiLogEntity: Nr5gUiLogEntity)

    /**
     * Insert row in the [Nr5gDatabaseConstants.NR5G_GET_ACTIVE_NETWORK_TABLE_NAME] table
     * @param getActiveNetworkEntity: GetActiveNetworkEntity object
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertNr5gActiveNetworkEntity(getActiveNetworkEntity: Nr5gActiveNetworkEntity)

    /**
     * Inserts [Nr5gSingleSessionReportEntity] parameters into the database in a single transaction.
     * @param Nr5gSingleSessionReportEntity : Nr5gSingleSessionReportEntity object
     *
     * @return List of long values which are inserted into DB.
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAllNr5gSingleSessionReportEntity(vararg nr5gSingleSessionReportEntity: Nr5gSingleSessionReportEntity): List<Long>


//_______GET_________
    /**
     * get Nr5gOEMSVEntity entity based on sessionId
     * @param sessionId: String used for Nr5gOEMSVEntity entity
     */
    @Query
        ("SELECT * FROM " + Nr5gDatabaseConstants.NR5G_OEMSV_TABLE_NAME + " WHERE sessionId =:sessionId")
    fun getNr5gOEMSVEntity(sessionId: String): Nr5gOEMSVEntity?

    /**
     * get Nr5gStatusEntity entity based on sessionId
     * @param sessionId: String used for Nr5gStatusEntity entity
     */
    @Query
        ("SELECT * FROM " + Nr5gDatabaseConstants.NR5G_GET_NR_STATUS_TABLE_NAME + " WHERE sessionId =:sessionId")
    fun getNr5gStatusEntity(sessionId: String): Nr5gStatusEntity?

    /**
     * Gets [LocationEntity] parameters from the database in a single transaction based on the session id.
     */
    @Query("SELECT * FROM " + Nr5gDatabaseConstants.NR5G_LOCATION_TABLE_NAME + " WHERE sessionId = :sessionId")
    fun getNr5gLocationEntity(sessionId: String): Nr5gLocationEntity?

    /**
     * Gets [Nr5gDeviceInfoEntity] parameters from the database in a single transaction based on the session id.
     */
    @Query("SELECT * FROM " + Nr5gDatabaseConstants.NR5G_DEVICE_INFO_TABLE_NAME + " WHERE sessionId = :sessionId")
    fun getNr5gDeviceInfoEntity(sessionId: String): Nr5gDeviceInfoEntity?

    /**
     * Gets [ConnectedWifiStatusEntity] parameters from the database in a single transaction
     * based on the session id.
     */
    @Query("SELECT * FROM " + Nr5gDatabaseConstants.NR5G_CONNECTED_WIFI_STATUS_TABLE_NAME + " WHERE sessionId = :sessionId")
    fun getConnectedWifiStatusEntity(sessionId: String): ConnectedWifiStatusEntity?

    /**
     * Gets [EndcLteLogEntity] parameters from the database in a single transaction
     * based on the session id.
     */
    @Query("SELECT * FROM " + Nr5gDatabaseConstants.NR5G_END_C_LTE_LOG_TABLE_NAME + " WHERE sessionId = :sessionId")
    fun getEndcLteLogEntity(sessionId: String): EndcLteLogEntity?

    /**
     * Gets [EndcUplinkLogEntity] parameters from the database in a single transaction
     * based on the session id.
     */
    @Query("SELECT * FROM " + Nr5gDatabaseConstants.NR5G_END_C_UPLINK_LOG_TABLE_NAME + " WHERE sessionId = :sessionId")
    fun getEndcUplinkLogEntity(sessionId: String): EndcUplinkLogEntity?

    /**
     * Gets [Nr5gUiLogEntity] parameters from the database in a single transaction
     * based on the session id.
     */
    @Query("SELECT * FROM " + Nr5gDatabaseConstants.NR5G_UI_LOG_TABLE_NAME + " WHERE sessionId = :sessionId")
    fun getNr5gUiLogEntity(sessionId: String): Nr5gUiLogEntity?

    /**
     * Gets [Nr5gMmwCellLogEntity] parameters from the database in a single transaction
     * based on the session id.
     */
    @Query("SELECT * FROM " + Nr5gDatabaseConstants.NR5G_MMW_CELL_LOG_TABLE_NAME + " WHERE sessionId = :sessionId")
    fun getNr5gMmwCellLogEntity(sessionId: String): Nr5gMmwCellLogEntity?

    /**
     * Get base echo locate Nr5g entity based on the trigger id
     */
    @Query("SELECT * FROM " + Nr5gDatabaseConstants.NR5G_ECHO_LOCATE_BASE_TABLE_NAME + " WHERE trigger = :triggerId")
    fun getBaseEchoLocateNrGEntityByTriggerID(triggerId: Int): List<BaseEchoLocateNr5gEntity>

    /**
     * Gets [Nr5gActiveNetworkEntity] for the given sessionID
     */
    @Query("SELECT * FROM " + Nr5gDatabaseConstants.NR5G_GET_ACTIVE_NETWORK_TABLE_NAME + " WHERE sessionId = :sessionId")
    fun getNr5gActiveEntity(sessionId: String): Nr5gActiveNetworkEntity

    /**
     * Gets [Nr5gNetworkIdentityEntity] parameters from the database in a single transaction
     * based on the session id.
     */
    @Query("SELECT * FROM " + Nr5gDatabaseConstants.NR5G_GET_NETWORK_IDENTITY_TABLE_NAME + " WHERE sessionId = :sessionId")
    fun getNr5gNetworkIdentityEntity(sessionId: String): Nr5gNetworkIdentityEntity?

    /**
     * Gets [Nr5gDataNetworkTypeEntity] parameters from the database in a single transaction
     * based on the session id.
     */
    @Query("SELECT * FROM " + Nr5gDatabaseConstants.NR5G_GET_DATA_NETWORK_TYPE_TABLE_NAME + " WHERE sessionId = :sessionId")
    fun getNr5gDataNetworkTypeEntity(sessionId: String): Nr5gDataNetworkTypeEntity?

    /**
     * Gets [Nr5gTriggerEntity] parameters from the database in a single transaction
     * based on the session id.
     */
    @Query("SELECT * FROM " + Nr5gDatabaseConstants.NR5G_TRIGGER_TABLE_NAME + " WHERE sessionId = :sessionId")
    fun getNr5gTriggerEntity(sessionId: String): Nr5gTriggerEntity?

    /**
     * Gets [Nr5gWifiStateEntity] parameters from the database in a single transaction
     * based on the session id.
     */
    @Query("SELECT * FROM " + Nr5gDatabaseConstants.NR5G_GET_WIFI_STATE_TABLE_NAME + " WHERE sessionId = :sessionId")
    fun getNr5gWifiStateEntity(sessionId: String): Nr5gWifiStateEntity?

    /**
     * Gets [Nr5gActiveNetworkEntity] parameters from the database in a single transaction
     * based on the session id.
     */
    @Query("SELECT * FROM " + Nr5gDatabaseConstants.NR5G_GET_ACTIVE_NETWORK_TABLE_NAME + " WHERE sessionId = :sessionId")
    fun getNr5gActiveNetworkEntity(sessionId: String): Nr5gActiveNetworkEntity?

    /**
     * get list of Nr5gSingleSessionReportEntity based on the state time and ent time
     * @param startTime start time of Nr5g Single Session
     * @param end time of Nr5g Single Session
     */
    @Query(
        "SELECT * FROM " + Nr5gDatabaseConstants.NR5G_REPORT_TABLE_NAME +
                " WHERE strftime(eventTimestamp) BETWEEN strftime(:startTime) AND strftime(:endTime) "
    )
    fun getNr5gSingleSessionReportEntityList(
        startTime: String,
        endTime: String
    ): List<Nr5gSingleSessionReportEntity>

    /**
     * Update status of records from report table
     */
    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateNr5gReportEntityList(vararg nr5gReportEntity: Nr5gSingleSessionReportEntity)

    /**
     * Gets list of [Nr5gSingleSessionReportEntity]
     */
    @Query("SELECT * FROM " + Nr5gDatabaseConstants.NR5G_REPORT_TABLE_NAME)
    fun getNr5gSingleSessionReportEntityList(): List<Nr5gSingleSessionReportEntity>

    /**
     * get Nr5gUiLogEntity entity based on sessionId
     * @param sessionId: String used for Nr5gUiLogEntity entity
     */
    @Query("SELECT * FROM " + Nr5gDatabaseConstants.NR5G_UI_LOG_TABLE_NAME + " WHERE sessionId =:sessionId")
    fun getNr5gUILogEntity(sessionId: String): Nr5gUiLogEntity

    /**
     * Deletes all the raw data from all the Nr5g tables
     *
     * @param baseEchoLocateNr5gEntity List of entity classes that needs to be deleted. It will have
     * all the session id's which are processed for reports generation so that the same session id's
     * considered as raw data in other tables and  will be deleted here
     */
    @Delete
    fun deleteRawDataFromAllTables(baseEchoLocateNr5gEntityList: List<BaseEchoLocateNr5gEntity>)

    /**
     * Deletes the reports from [Nr5gSingleSessionReportEntity] with status REPORTING
     * @param status
     */
    @Query("DELETE FROM " + Nr5gDatabaseConstants.NR5G_REPORT_TABLE_NAME + " WHERE reportStatus = :status")
    fun deleteProcessedReports(status: String)

    /**
     * Update status for all BaseEchoLocateNr5gEntity
     * @param baseEchoLocateNr5gEntity :BaseEchoLocateNr5gEntity to update all the BaseEchoLocateNr5gEntity items
     */
    @Update(onConflict = OnConflictStrategy.IGNORE)
    fun updateAllBaseEchoLocateNr5gEntityStatus(vararg baseEchoLocateNr5gEntity: BaseEchoLocateNr5gEntity)

    /**
     * Update status for a single record of BaseEchoLocateNr5gEntity
     * @param baseEchoLocateNr5gEntity :BaseEchoLocateNr5gEntity to update the BaseEchoLocateNr5gEntity item
     */
    @Update(onConflict = OnConflictStrategy.IGNORE)
    fun updateBaseEchoLocateNr5gEntityStatus(baseEchoLocateNr5gEntity: BaseEchoLocateNr5gEntity)
}