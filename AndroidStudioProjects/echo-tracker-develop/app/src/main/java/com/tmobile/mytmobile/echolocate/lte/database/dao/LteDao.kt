package com.tmobile.mytmobile.echolocate.lte.database.dao

import androidx.room.*
import com.tmobile.mytmobile.echolocate.lte.database.LteDatabaseConstants
import com.tmobile.mytmobile.echolocate.lte.database.entity.*

/**
 * [LteDao] is responsible for defining the methods that access the database.
 * by using queries annotations
 */
@Dao
interface LteDao {

    /**
     * Inserts [BaseEchoLocateLteEntity] parameters into the database in a single transaction.
     * @param baseEchoLocateLteEntity :BaseEchoLocateLteEntity object
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertBaseEchoLocateLteEntity(baseEchoLocateLteEntity: BaseEchoLocateLteEntity): Long

    /**
     * Inserts [UpLinkRFConfigurationEntity] parameters into the database in a single transaction.
     * @param upLinkRFConfigurationEntity :UpLinkRFConfigurationEntity object
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertUpLinkRFConfigurationEntity(upLinkRFConfigurationEntity: UpLinkRFConfigurationEntity)

    /**
     * Inserts [BearerConfigurationEntity] parameters into the database in a single transaction.
     * @param bearerConfigurationEntity :BearerConfigurationEntity object
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertBearerConfigurationEntity(bearerConfigurationEntity: BearerConfigurationEntity)

    /**
     * Inserts [CommonRFConfigurationEntity] parameters into the database in a single transaction.
     * @param commonRFConfigurationEntity :CommonRFConfigurationEntity object
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertCommonRFConfigurationEntity(commonRFConfigurationEntity: CommonRFConfigurationEntity)

    /**
     * Inserts [UplinkCarrierInfoEntity] parameters into the database in a single transaction.
     * @param uplinkCarrierInfoEntity :UplinkCarrierInfoEntity object
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertUplinkCarrierInfoEntity(uplinkCarrierInfoEntity: UplinkCarrierInfoEntity)

    /**
     * Inserts [CAEntity] parameters into the database in a single transaction.
     * @param caEntity : CAEntity object
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAllCAEntity(vararg caEntity: CAEntity)

    /**
     * Inserts [BearerEntity] parameters into the database in a single transaction.
     * @param bearerEntity : BearerEntity object
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAllBearerEntity(vararg bearerEntity: BearerEntity)

    /**
     * Inserts [ThirdCarrierEntity] parameters into the database in a single transaction.
     * @param thirdCarrierEntity :ThirdCarrierEntity object
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertThirdCarrierEntity(thirdCarrierEntity: ThirdCarrierEntity)

    /**
     * Gets [ThirdCarrierEntity] parameters from the database in a single transaction.
     */
    @Query("SELECT * FROM " + LteDatabaseConstants.LTE_THIRD_CARRIER_TABLE_NAME)
    fun getThirdCarrierEntity(): List<ThirdCarrierEntity>


    /**
     * Gets [BaseEchoLocateLteEntity] parameters from the database in a single transaction.
     */
    @Query("SELECT * FROM " + LteDatabaseConstants.ECHO_LOCATE_LTE_BASE_TABLE_NAME + " ORDER BY triggerTimestamp ASC")
    fun getBaseEchoLocateLteEntity(): List<BaseEchoLocateLteEntity>

    /**
     * Delete all [BaseEchoLocateLteEntity] entries from the database in a single transaction.
     */
    @Query("DELETE FROM " + LteDatabaseConstants.ECHO_LOCATE_LTE_BASE_TABLE_NAME)
    fun deleteAllBaseEchoLocateLteEntity()

    /**
     * get base echo locate lte entity based on the session id
     */
    @Query("SELECT * FROM " + LteDatabaseConstants.ECHO_LOCATE_LTE_BASE_TABLE_NAME + " WHERE sessionId = :sessionId")
    fun getBaseEchoLocateLteEntityBySessionID(sessionId: String): BaseEchoLocateLteEntity

    /**
     * Gets [UpLinkRFConfigurationEntity] parameters from the database in a single transaction.
     *
     * Used for testing
     */
    @Query("SELECT * FROM " + LteDatabaseConstants.LTE_UPLINK_RF_CONFIGURATION_TABLE_NAME)
    fun getUpLinkRFConfigurationEntityList(): List<UpLinkRFConfigurationEntity>

    /**
     * Gets [BearerConfigurationEntity] parameters from the database in a single transaction.
     */
    @Query("SELECT * FROM " + LteDatabaseConstants.LTE_BEARER_CONFIGURATION_TABLE_NAME)
    fun getBearerConfigurationEntity(): List<BearerConfigurationEntity>

    /**
     * Gets [BearerEntity] parameters from the database in a single transaction.
     *
     * Used for testing only
     */
    @Query("SELECT * FROM " + LteDatabaseConstants.LTE_BEARER_TABLE_NAME)
    fun getBearerEntity(): List<BearerEntity>

    /**
     * Gets [CAEntity] parameters from the database in a single transaction.
     *
     * Used for testing only
     */
    @Query("SELECT * FROM " + LteDatabaseConstants.LTE_CA_TABLE_NAME)
    fun getCAEntity(): List<CAEntity>

    /**
     * Gets [UplinkCarrierInfoEntity] parameters from the database in a single transaction.
     */
    @Query("SELECT * FROM " + LteDatabaseConstants.LTE_UPLINK_CARRIER_INFO_TABLE_NAME)
    fun getUpLinkCarrierInfoEntity(): List<UplinkCarrierInfoEntity>

    /**
     * Gets [DownlinkRFConfigurationEntity] parameters from the database in a single transaction.
     */
    @Query("SELECT * FROM " + LteDatabaseConstants.LTE_DOWNLINK_RF_CONFIGURATION_TABLE_NAME)
    fun getDownLinkRFConfigurationInfoEntity(): List<DownlinkRFConfigurationEntity>

    /**
     * Gets [UplinkCarrierInfoEntity] parameters from the database in a single transaction.
     *
     * Used for test only
     */
    @Query("SELECT * FROM " + LteDatabaseConstants.LTE_SIGNAL_CONDITION_TABLE_NAME)
    fun getSignalConditionEntityList(): List<SignalConditionEntity>

    /**
     * Inserts [SignalConditionEntity] parameters into the database in a single transaction.
     * @param signalConditionEntity : SignalConditionEntity object
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertSignalConditionEntity(signalConditionEntity: SignalConditionEntity)

    /**
     * Inserts [SecondCarrierEntity] parameters into the database in a single transaction.
     * @param secondCarrierEntity : SecondCarrierEntity object
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertSecondCarrierEntity(secondCarrierEntity: SecondCarrierEntity)

    /**
     * Inserts [LteOEMSVEntity] parameters into the database in a single transaction.
     * @param lteOEMSVEntity: LteOEMSVEntity object
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertLteOEMSVEntity(lteOEMSVEntity: LteOEMSVEntity)

    /**
     * Inserts [DownLinkCarrierInfoEntity] parameters into the database in a single transaction.
     * @param downLinkCarrierInfoEntity: DownLinkCarrierInfoEntity object
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertDownLinkCarrierInfoEntity(downLinkCarrierInfoEntity: DownLinkCarrierInfoEntity)

    /**
     * Inserts [DownlinkRFConfigurationEntity] parameters into the database in a single transaction.
     * @param downlinkRFConfigurationEntity: DownlinkRFConfigurationEntity object
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertDownlinkRFConfigurationEntity(downlinkRFConfigurationEntity: DownlinkRFConfigurationEntity)

    /**
     * Inserts [LteLocationEntity] parameters into the database in a single transaction.
     * @param leLocationEntity: LteLocationEntity object
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertLteLocationEntity(leLocationEntity: LteLocationEntity)

    /**
     * Inserts [LteLocationEntity] parameters into the database in a single transaction.
     * @param leLocationEntity: LteLocationEntity object
     */
    @Update(onConflict = OnConflictStrategy.IGNORE)
    fun updateLteLocationEntity(leLocationEntity: LteLocationEntity)

    /**
     * Inserts [NetworkIdentityEntity] parameters into the database in a single transaction.
     * @param networkIdentityEntity: NetworkIdentityEntity object
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertNetworkIdentityEntity(networkIdentityEntity: NetworkIdentityEntity)

    /**
     * Inserts [LteSettingsEntity] parameters into the database in a single transaction.
     * @param lteSettingsEntity: LteSettingsEntity object
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertLteSettingsEntity(lteSettingsEntity: LteSettingsEntity)

    /**
     * Inserts [LteSingleSessionReportEntity] parameters into the database in a single transaction.
     * @param lteSingleSessionReportEntity : LteSingleSessionReportEntity object
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertLteSingleSessionReportEntity(lteSingleSessionReportEntity: LteSingleSessionReportEntity)

    /**
     * Inserts [LteSingleSessionReportEntity] parameters into the database in a single transaction.
     * @param lteSingleSessionReportEntity : LteSingleSessionReportEntity object
     *
     * @return List of long values which are inserted into DB.
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAllLteSingleSessionReportEntity(vararg lteSingleSessionReportEntity: LteSingleSessionReportEntity): List<Long>

    /**
     * Update status of BaseEchoLocateLteEntity
     */
    @Query("SELECT * FROM " + LteDatabaseConstants.ECHO_LOCATE_LTE_BASE_TABLE_NAME + " WHERE status = :status")
    fun getBaseEchoLocateLteEntityByStatus(status: String): List<BaseEchoLocateLteEntity>

    /**
     * Update status for all BaseEchoLocateLteEntity
     * @param baseEchoLocateLteEntity :BaseEchoLocateLteEntity to update all the BaseEchoLocateLteEntity items
     */
    @Update(onConflict = OnConflictStrategy.IGNORE)
    fun updateAllBaseEchoLocateLteEntityStatus(vararg baseEchoLocateLteEntity: BaseEchoLocateLteEntity)

    /**
     * Update status for single record of BaseEchoLocateLteEntity
     * @param baseEchoLocateLteEntity :BaseEchoLocateLteEntity to update the BaseEchoLocateLteEntity items
     */
    @Update(onConflict = OnConflictStrategy.IGNORE)
    fun updateBaseEchoLocateLteEntityStatus(baseEchoLocateLteEntity: BaseEchoLocateLteEntity)

    /**
     * Gets list of [LteSingleSessionReportEntity]
     */
    @Query("SELECT * FROM " + LteDatabaseConstants.LTE_REPORT_TABLE_NAME)
    fun getLteSingleSessionReportEntityList(): List<LteSingleSessionReportEntity>

    /**
     * Update status of records from report table
     */
    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateLteReportEntityList(vararg LteReportEntity: LteSingleSessionReportEntity)

    /**
     * get list of LteSingleSessionReportEntity based on the state time and ent time
     * @param startTime start time of Lte Single Session
     * @param end time of Lte Single Session
     */
    @Query(
        "SELECT * FROM " + LteDatabaseConstants.LTE_REPORT_TABLE_NAME +
                " WHERE strftime(eventTimestamp) BETWEEN strftime(:startTime) AND strftime(:endTime) "
    )
    fun getLteSingleSessionReportEntityList(
        startTime: String,
        endTime: String
    ): List<LteSingleSessionReportEntity>


    //_________________ Single Object data return Obj

    /**
     * get LteOEMSV entity based on sessionId
     * @param callId: String used for LteOEMSV entity
     */
    @Query
        ("SELECT * FROM " + LteDatabaseConstants.LTE_OEMSV_TABLE_NAME + " WHERE sessionId =:sessionId")
    fun getLteOEMSVEntity(sessionId: String): LteOEMSVEntity?

    /**
     * get BearerConfiguration entity based on sessionId
     * @param sessionId: String used for BearerConfiguration entity
     */
    @Query("SELECT * FROM " + LteDatabaseConstants.LTE_BEARER_CONFIGURATION_TABLE_NAME + " WHERE sessionId =:sessionId")
    fun getBearerConfigurationEntity(sessionId: String): BearerConfigurationEntity?

    /**
     * get Bearer entity list based on sessionId
     * @param sessionId: String used for Bearer entity
     */
    @Query("SELECT * FROM " + LteDatabaseConstants.LTE_BEARER_TABLE_NAME + " WHERE sessionId =:sessionId")
    fun getBearerEntityList(sessionId: String): List<BearerEntity>

    /**
     * Gets [CAEntity] parameters from the database in a single transaction.
     * @param sessionId: String used for CAEntity
     */
    @Query("SELECT * FROM " + LteDatabaseConstants.LTE_CA_TABLE_NAME + " WHERE sessionId =:sessionId AND uniqueId =:uniqueId")
    fun getCAEntityList(sessionId: String, uniqueId: String): List<CAEntity>

    /**
     * Gets [CommonRFConfigurationEntity] parameters from the database in a single transaction based on the session id.
     */
    @Query("SELECT * FROM " + LteDatabaseConstants.LTE_COMMON_RF_CONFIGURATION_TABLE_NAME + " WHERE sessionId = :sessionId")
    fun getCommonRFConfigurationEntity(sessionId: String): CommonRFConfigurationEntity?

    /**
     * Gets [DownLinkCarrierInfoEntity] parameters from the database in a single transaction based on the session id.
     */
    @Query("SELECT * FROM " + LteDatabaseConstants.LTE_DOWNLINK_CARRIER_INO_TABLE_NAME + " WHERE sessionId = :sessionId")
    fun getDownlinkCarrierInfoEntity(sessionId: String): DownLinkCarrierInfoEntity?

    /**
     * Gets [DownlinkRFConfigurationEntity] parameters from the database in a single transaction based on the session id.
     */
    @Query("SELECT * FROM " + LteDatabaseConstants.LTE_DOWNLINK_RF_CONFIGURATION_TABLE_NAME + " WHERE sessionId = :sessionId")
    fun getDownlinkRFConfigurationEntity(sessionId: String): DownlinkRFConfigurationEntity?

    /**
     * Gets [LteLocationEntity] parameters from the database in a single transaction based on the session id.
     */
    @Query("SELECT * FROM " + LteDatabaseConstants.LTE_LOCATION_TABLE_NAME + " WHERE sessionId = :sessionId")
    fun getLteLocationEntity(sessionId: String): LteLocationEntity?

    /**
     * Gets [NetworkIdentityEntity] parameters from the database in a single transaction based on the session id.
     */
    @Query("SELECT * FROM " + LteDatabaseConstants.LTE_NETWORK_IDENTITY_TABLE_NAME + " WHERE sessionId = :sessionId")
    fun getNetworkIdentityEntity(sessionId: String): NetworkIdentityEntity?

    /**
     * Gets [LteSettingsEntity] parameters from the database in a single transaction based on the session id.
     */
    @Query("SELECT * FROM " + LteDatabaseConstants.LTE_SETTINGS_TABLE_NAME + " WHERE sessionId = :sessionId")
    fun getLteSettingsEntity(sessionId: String): LteSettingsEntity?

    /**
     * Gets [SignalConditionEntity] parameters from the database in a single transaction based on the session id.
     */
    @Query("SELECT * FROM " + LteDatabaseConstants.LTE_SIGNAL_CONDITION_TABLE_NAME + " WHERE sessionId = :sessionId")
    fun getSignalConditionEntity(sessionId: String): SignalConditionEntity?

    /**
     * Gets [UpLinkRFConfigurationEntity] parameters from the database in a single transaction based on the session id.
     */
    @Query("SELECT * FROM " + LteDatabaseConstants.LTE_UPLINK_RF_CONFIGURATION_TABLE_NAME + " WHERE sessionId = :sessionId")
    fun getUpLinkRFConfigurationEntity(sessionId: String): UpLinkRFConfigurationEntity?

    /**
     * Gets [UplinkCarrierInfoEntity] parameters from the database in a single transaction based on the session id.
     */
    @Query("SELECT * FROM " + LteDatabaseConstants.LTE_UPLINK_CARRIER_INFO_TABLE_NAME + " WHERE sessionId = :sessionId")
    fun getUplinkCarrierInfoEntity(sessionId: String): UplinkCarrierInfoEntity?

    /**
     * Gets [SecondCarrierEntity] parameters from the database in a single transaction based on the session id.
     * @param sessionId: String used for SecondCarrierEntity
     */
    @Query("SELECT * FROM " + LteDatabaseConstants.LTE_SECOND_CARRIER_TABLE_NAME + " WHERE sessionId =:sessionId")
    fun getSecondCarrierEntity(sessionId: String): SecondCarrierEntity?

    /**
     * Gets [ThirdCarrierEntity] parameters from the database in a single transaction based on the session id.
     * @param sessionId: String used for ThirdCarrierEntity
     */
    @Query("SELECT * FROM " + LteDatabaseConstants.LTE_THIRD_CARRIER_TABLE_NAME + " WHERE sessionId =:sessionId")
    fun getThirdCarrierEntity(sessionId: String): ThirdCarrierEntity?

    /**
     * Inserts [DownLinkCarrierInfoEntity] parameters into the database in a single transaction.
     * @param downLinkCarrierInfoEntity :DownLinkCarrierInfoEntity object
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertDownlinkCarrierInfoEntity(downLinkCarrierInfoEntity: DownLinkCarrierInfoEntity)

    /**
     * Gets [DownLinkCarrierInfoEntity] parameters from the database in a single transaction.
     */
    @Query("SELECT * FROM " + LteDatabaseConstants.LTE_DOWNLINK_CARRIER_INO_TABLE_NAME)
    fun getDownLinkCarrierInfoEntity(): List<DownLinkCarrierInfoEntity>


    /**
     * Inserts [DownlinkRFConfigurationEntity] parameters into the database in a single transaction.
     * @param downLinkRFConfigurationEntity :DownlinkRFConfigurationEntity object
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertDownLinkRFConfigurationEntity(downLinkRFConfigurationEntity: DownlinkRFConfigurationEntity)


    /**
     * Deletes the reports from lteSingleSessionReportEntity with status REPORTING
     * @param status
     */
    @Query("DELETE FROM " + LteDatabaseConstants.LTE_REPORT_TABLE_NAME + " WHERE reportStatus = :status")
    fun deleteProcessedReports(status: String)

    /**
     * Gets data from Lte table,this is used to test the report table data
     */
    @Query("SELECT * FROM " + LteDatabaseConstants.LTE_REPORT_TABLE_NAME)
    fun getProcessedReports(): List<LteSingleSessionReportEntity>

    /**
     * get base echo locate lte entity based on the session id
     */
    @Query("SELECT sessionId FROM " + LteDatabaseConstants.ECHO_LOCATE_LTE_BASE_TABLE_NAME + " WHERE status =:status")
    fun getSessionIDForProcessedStatus(status: String): List<String>

    /**
     * Deletes all the raw data from all the lte tables
     *
     * @param baseEchoLocateLteEntity List of entity classes that needs to be deleted. It will have
     * all the session id's which are processed for reports generation so that the same session id's
     * considered as raw data in other tables and  will be deleted here
     */
    @Delete
    fun deleteRawDataFromAllTables(baseEchoLocateLteEntity: List<BaseEchoLocateLteEntity>)

}
