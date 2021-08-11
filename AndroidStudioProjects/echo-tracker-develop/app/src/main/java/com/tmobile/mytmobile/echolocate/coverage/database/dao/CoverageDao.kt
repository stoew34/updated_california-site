package com.tmobile.mytmobile.echolocate.coverage.database.dao

import androidx.room.*
import com.tmobile.mytmobile.echolocate.coverage.database.CoverageDatabaseConstants
import com.tmobile.mytmobile.echolocate.coverage.database.entity.*

/**
 * [CoverageDao] is responsible for defining the methods that access the database.
 * by using queries annotations
 */
@Dao
interface CoverageDao {

    /**
     * get list of BaseEchoLocateCoverageEntity based on the status
     */
    @Query("SELECT * FROM " + CoverageDatabaseConstants.COVERAGE_BASE_TABLE_NAME + " WHERE status = :status")
    fun getBaseEchoLocateCoverageEntityByStatus(status: String): List<BaseEchoLocateCoverageEntity>

    /**
     * get BaseEchoLocateCoverageEntity based on the session id
     * @param sessionId: String used for BaseEchoLocateCoverageEntity entity
     */
    @Query("SELECT * FROM " + CoverageDatabaseConstants.COVERAGE_BASE_TABLE_NAME + " WHERE sessionId = :sessionId")
    fun getBaseEchoLocateCoverageEntityBySessionID(sessionId: String): BaseEchoLocateCoverageEntity

    /**
     * get CoverageTelephonyEntity entity based on sessionId
     * @param sessionId: String used for CoverageTelephonyEntity entity
     */
    @Query("SELECT * FROM " + CoverageDatabaseConstants.COVERAGE_TELEPHONY_TABLE_NAME + " WHERE sessionId =:sessionId")
    fun getCoverageTelephonyEntityBySessionID(sessionId: String): CoverageTelephonyEntity?

    /**
     * get CoveragePrimaryCellEntity entity based on sessionId
     * @param sessionId: String used for CoveragePrimaryCellEntity entity
     */
    @Query("SELECT * FROM " + CoverageDatabaseConstants.COVERAGE_PRIMARY_CELL_TABLE_NAME + " WHERE sessionId =:sessionId")
    fun getCoveragePrimaryCellEntityBySessionID(sessionId: String): CoveragePrimaryCellEntity?

    /**
     * get CoverageCellIdentityEntity entity based on sessionId
     * @param sessionId: String used for CoverageCellIdentityEntity entity
     */
    @Query("SELECT * FROM " + CoverageDatabaseConstants.COVERAGE_CELL_IDENTITY_TABLE_NAME + " WHERE sessionId =:sessionId")
    fun getCoverageCellIdentityEntityBySessionID(sessionId: String): CoverageCellIdentityEntity?

    /**
     * get CoverageCellSignalStrengthEntity entity based on sessionId
     * @param sessionId: String used for CoverageCellSignalStrengthEntity entity
     */
    @Query("SELECT * FROM " + CoverageDatabaseConstants.COVERAGE_CELL_SIGNAL_STRENGTH_TABLE_NAME + " WHERE sessionId =:sessionId")
    fun getCoverageCellSignalStrengthEntityBySessionID(sessionId: String): CoverageCellSignalStrengthEntity?

    /**
     * get CoverageNrCellEntity entity based on sessionId
     * @param sessionId: String used for CoverageNrCellEntity entity
     */
    @Query("SELECT * FROM " + CoverageDatabaseConstants.COVERAGE_NR_CELL_TABLE_NAME + " WHERE sessionId =:sessionId")
    fun getCoverageNrCellEntityBySessionID(sessionId: String): CoverageNrCellEntity?

    /**
     * get CoverageSettingsEntity entity based on sessionId
     * @param sessionId: String used for CoverageSettingsEntity entity
     */
    @Query("SELECT * FROM " + CoverageDatabaseConstants.COVERAGE_SETTINGS_TABLE_NAME + " WHERE sessionId =:sessionId")
    fun getCoverageSettingsEntityBySessionID(sessionId: String): CoverageSettingsEntity?

    /**
     * get CoverageOEMSVEntity entity based on sessionId
     * @param sessionId: String used for CoverageOEMSVEntity entity
     */
    @Query("SELECT * FROM " + CoverageDatabaseConstants.COVERAGE_OEMSV_TABLE_NAME + " WHERE sessionId =:sessionId")
    fun getCoverageOEMSVEntityBySessionID(sessionId: String): CoverageOEMSVEntity?

    /**
     * get CoverageLocationEntity entity based on sessionId
     * @param sessionId: String used for CoverageLocationEntity entity
     */
    @Query("SELECT * FROM " + CoverageDatabaseConstants.COVERAGE_LOCATION_TABLE_NAME + " WHERE sessionId =:sessionId")
    fun getCoverageLocationEntityBySessionID(sessionId: String): CoverageLocationEntity?

    /**
     * get CoverageNetEntity entity based on sessionId
     * @param sessionId: String used for CoverageNetEntity entity
     */
    @Query("SELECT * FROM " + CoverageDatabaseConstants.COVERAGE_NET_TABLE_NAME + " WHERE sessionId =:sessionId")
    fun getCoverageNetEntityBySessionID(sessionId: String): CoverageNetEntity?

    /**
     * get CoverageConnectedWifiStatusEntity entity based on sessionId
     * @param sessionId: String used for CoverageConnectedWifiStatusEntity entity
     */
    @Query("SELECT * FROM " + CoverageDatabaseConstants.COVERAGE_WIFI_STATUS__TABLE_NAME + " WHERE sessionId =:sessionId")
    fun getCoverageConnectedWifiStatusEntityEntityBySessionID(sessionId: String): CoverageConnectedWifiStatusEntity?

    /**
     * Gets list of [CoverageSingleSessionReportEntity]
     */
    @Query("SELECT * FROM " + CoverageDatabaseConstants.COVERAGE_REPORT_TABLE_NAME)
    fun getCoverageSingleSessionReportEntityList(): List<CoverageSingleSessionReportEntity>

    /**
     * Update status of records from report table
     */
    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateCoverageReportEntityList(vararg voiceReportEntity: CoverageSingleSessionReportEntity)

    /**
     * Inserts [BaseEchoLocateCoverageEntity] parameters into the database in a single transaction.
     * @param baseEchoLocateCoverageEntity :BaseEchoLocateCoverageEntity object
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertBaseEchoLocateCoverageEntity(baseEchoLocateCoverageEntity: BaseEchoLocateCoverageEntity): Long

    /**
     * Inserts [CoverageCellIdentityEntity] parameters into the database in a single transaction.
     * @param coverageCellIdentityEntity :CoverageCellIdentityEntity object
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertCoverageCellIdentityEntity(coverageCellIdentityEntity: CoverageCellIdentityEntity)

    /**
     * Inserts [CoverageCellSignalStrengthEntity] parameters into the database in a single transaction.
     * @param coverageCellSignalStrengthEntity :CoverageCellSignalStrengthEntity object
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertCoverageCellSignalStrengthEntity(coverageCellSignalStrengthEntity: CoverageCellSignalStrengthEntity)

    /**
     * Inserts [CoverageSingleSessionReportEntity] parameters into the database in a single transaction.
     * @param coverageSingleSessionReportEntity :CoverageSingleSessionReportEntity object
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertCoverageSingleSessionReportEntity(coverageSingleSessionReportEntity: CoverageSingleSessionReportEntity)

    /**
     * Inserts [CoverageLocationEntity] parameters into the database in a single transaction.
     * @param coverageLocationEntity :CoverageLocationEntity object
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCoverageLocationEntity(coverageLocationEntity: CoverageLocationEntity)

    /**
     * Inserts [CoverageNetEntity] parameters into the database in a single transaction.
     * @param coverageNetEntity :CoverageNetEntity object
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertCoverageNetEntity(coverageNetEntity: CoverageNetEntity)

    /**
     * Inserts [CoverageNrCellEntity] parameters into the database in a single transaction.
     * @param coverageNrCellEntity :CoverageNrCellEntity object
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertCoverageNrCellEntity(coverageNrCellEntity: CoverageNrCellEntity)

    /**
     * Inserts [CoverageOEMSVEntity] parameters into the database in a single transaction.
     * @param coverageOEMSVEntity :CoverageOEMSVEntity object
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertCoverageOEMSVEntity(coverageOEMSVEntity: CoverageOEMSVEntity)

    /**
     * Inserts [CoveragePrimaryCellEntity] parameters into the database in a single transaction.
     * @param coveragePrimaryCellEntity :CoveragePrimaryCellEntity object
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertCoveragePrimaryCellEntity(coveragePrimaryCellEntity: CoveragePrimaryCellEntity)

    /**
     * Inserts [CoverageSettingsEntity] parameters into the database in a single transaction.
     * @param coverageSettingsEntity :CoverageSettingsEntity object
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertCoverageSettingsEntity(coverageSettingsEntity: CoverageSettingsEntity)

    /**
     * Inserts [CoverageTelephonyEntity] parameters into the database in a single transaction.
     * @param coverageTelephonyEntity :CoverageTelephonyEntity object
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertCoverageTelephonyEntity(coverageTelephonyEntity: CoverageTelephonyEntity)

    /**
     * Inserts [CoverageConnectedWifiStatusEntity] parameters into the database in a single transaction.
     * @param coverageConnectedWifiStatusEntity :CoverageConnectedWifiStatusEntity object
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertCoverageConnectedWifiStatusEntity(coverageConnectedWifiStatusEntity: CoverageConnectedWifiStatusEntity)

    /**
     * Inserts [CoverageSingleSessionReportEntity] parameters into the database in a single transaction.
     * @param coverageSingleSessionReportEntity : CoverageSingleSessionReportEntity object
     *
     * @return List of long values which are inserted into DB.
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAllCoverageSingleSessionReportEntity(vararg coverageSingleSessionReportEntity: CoverageSingleSessionReportEntity): List<Long>

    /**
     * Deletes all the raw data from all the coverage tables
     *
     * @param baseEchoLocateCoverageEntity List of entity classes that needs to be deleted. It will have
     * all the session id's which are processed for reports generation so that the same session id's
     * considered as raw data in other tables and  will be deleted here
     */
    @Delete
    fun deleteRawDataFromAllTables(baseEchoLocateCoverageEntity: List<BaseEchoLocateCoverageEntity>)

    /**
     * Deletes the reports from [CoverageSingleSessionReportEntity] with status REPORTING
     * @param status
     */
    @Query("DELETE FROM " + CoverageDatabaseConstants.COVERAGE_REPORT_TABLE_NAME + " WHERE reportStatus = :status")
    fun deleteProcessedReports(status: String)

    /**
     * Update status for all BaseEchoLocateCoverageEntity
     * @param baseEchoLocateCoverageEntity :BaseEchoLocateCoverageEntity to update all the BaseEchoLocateCoverageEntity items
     */
    @Update(onConflict = OnConflictStrategy.IGNORE)
    fun updateAllBaseEchoLocateCoverageEntityStatus(vararg baseEchoLocateCoverageEntity: BaseEchoLocateCoverageEntity)

    /**
     * Update status for single record of BaseEchoLocateLteEntity
     * @param baseEchoLocateCoverageEntity :BaseEchoLocateCoverageEntity to update
     * the BaseEchoLocateCoverageEntity items
     */
    @Update(onConflict = OnConflictStrategy.IGNORE)
    fun updateBaseEchoLocateCoverageEntityStatus(baseEchoLocateCoverageEntity: BaseEchoLocateCoverageEntity)

}