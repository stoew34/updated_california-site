package com.tmobile.mytmobile.echolocate.coverage.database.repository

import android.content.Context
import com.tmobile.mytmobile.echolocate.coverage.database.EchoLocateCoverageDatabase
import com.tmobile.mytmobile.echolocate.coverage.database.dao.CoverageDao
import com.tmobile.mytmobile.echolocate.coverage.database.entity.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking

/**
 * The [CoverageRepository] repository class will be responsible for interacting with the Room database
 * and will need to provide methods that use the DAO to insert, delete and query product records.
 * @param context :Context the context passed from activity
 */
class CoverageRepository(context: Context) {

    /**
     * gets the coverage DAO instance defined as a abstract class in [EchoLocateCoverageDatabase]
     */
    private val coverageDao: CoverageDao =
        EchoLocateCoverageDatabase.getEchoLocateCoverageDatabase(context).coverageDao()

    /**
     * Calls the insert method defined in DAO to insert [BaseEchoLocateCoverageEntity] parameters into the database
     * @param baseEchoLocateCoverageEntity :BaseEchoLocateCoverageEntity object
     */
    fun insertBaseEchoLocateCoverageEntity(baseEchoLocateCoverageEntity: BaseEchoLocateCoverageEntity) {
        coverageDao.insertBaseEchoLocateCoverageEntity(baseEchoLocateCoverageEntity)
    }

    /**
     * Calls the insert method defined in DAO to insert [CoverageCellIdentityEntity] parameters into the database
     * @param coverageCellIdentityEntity :CoverageCellIdentityEntity object
     */
    fun insertCoverageCellIdentityEntity(coverageCellIdentityEntity: CoverageCellIdentityEntity) {
        coverageDao.insertCoverageCellIdentityEntity(coverageCellIdentityEntity)
    }

    /**
     * Calls the insert method defined in DAO to insert [CoverageCellSignalStrengthEntity] parameters into the database
     * @param coverageCellSignalStrengthEntity :CoverageCellSignalStrengthEntity object
     */
    fun insertCoverageCellSignalStrengthEntity(coverageCellSignalStrengthEntity: CoverageCellSignalStrengthEntity) {
        coverageDao.insertCoverageCellSignalStrengthEntity(coverageCellSignalStrengthEntity)
    }

    /**
     * Calls the insert method defined in DAO to insert [CoverageSingleSessionReportEntity] parameters into the database
     * @param coverageSingleSessionReportEntity :EventDataEntity object
     */
    fun insertCoverageSingleSessionReportEntity(coverageSingleSessionReportEntity: CoverageSingleSessionReportEntity) {
        coverageDao.insertCoverageSingleSessionReportEntity(coverageSingleSessionReportEntity)
    }

    /**
     * Calls the insert method defined in DAO to insert [CoverageLocationEntity] parameters into the database
     * @param coverageLocationEntity :LocationEntity object
     */
    fun insertCoverageLocationEntity(coverageLocationEntity: CoverageLocationEntity) {
        coverageDao.insertCoverageLocationEntity(coverageLocationEntity)
    }

    /**
     * Calls the insert method defined in DAO to insert [CoverageNetEntity] parameters into the database
     * @param coverageNetEntity :NetEntity object
     */
    fun insertCoverageNetEntity(coverageNetEntity: CoverageNetEntity) {
        coverageDao.insertCoverageNetEntity(coverageNetEntity)
    }

    /**
     * Calls the insert method defined in DAO to insert [CoverageNrCellEntity] parameters into the database
     * @param coverageNrCellEntity :NrCellEntity object
     */
    fun insertCoverageNrCellEntity(coverageNrCellEntity: CoverageNrCellEntity) {
        coverageDao.insertCoverageNrCellEntity(coverageNrCellEntity)
    }

    /**
     * Calls the insert method defined in DAO to insert [CoverageOEMSVEntity] parameters into the database
     * @param coverageOEMSVEntity :CoverageOemSvEntity object
     */
    fun insertCoverageOEMSVEntity(coverageOEMSVEntity: CoverageOEMSVEntity) {
        coverageDao.insertCoverageOEMSVEntity(coverageOEMSVEntity)
    }

    /**
     * Calls the insert method defined in DAO to insert [CoveragePrimaryCellEntity] parameters into the database
     * @param coveragePrimaryCellEntity :CoveragePrimaryCellEntity object
     */
    fun insertCoveragePrimaryCellEntity(coveragePrimaryCellEntity: CoveragePrimaryCellEntity) {
        coverageDao.insertCoveragePrimaryCellEntity(coveragePrimaryCellEntity)
    }

    /**
     * Calls the insert method defined in DAO to insert [CoverageSettingsEntity] parameters into the database
     * @param coverageSettingsEntity :SettingsEntity object
     */
    fun insertCoverageSettingsEntity(coverageSettingsEntity: CoverageSettingsEntity) {
        coverageDao.insertCoverageSettingsEntity(coverageSettingsEntity)
    }

    /**
     * Calls the insert method defined in DAO to insert [CoverageTelephonyEntity] parameters into the database
     * @param coverageTelephonyEntity :TelephonyEntity object
     */

    fun insertCoverageTelephonyEntity(coverageTelephonyEntity: CoverageTelephonyEntity) {
        coverageDao.insertCoverageTelephonyEntity(coverageTelephonyEntity)
    }

    /**
     * Calls the insert method defined in DAO to insert [CoverageConnectedWifiStatusEntity] parameters into the database
     * @param coverageConnectedWifiStatusEntity :WifiStatusEntity object
     */
    fun insertCoverageConnectedWifiStatusEntity(coverageConnectedWifiStatusEntity: CoverageConnectedWifiStatusEntity) {
        coverageDao.insertCoverageConnectedWifiStatusEntity(coverageConnectedWifiStatusEntity)
    }

    /**
     * Deletes all the raw data from all the coverage tables once it is processed for production build
     * sessionIdList will have all the processed data list, so that data can be deleted from other tables
     */
    fun deleteRawData(baseEchoLocateCoverageEntityList: List<BaseEchoLocateCoverageEntity>) {
        coverageDao.deleteRawDataFromAllTables(baseEchoLocateCoverageEntityList)
    }

    /**
     * This function deletes the the report data from db based on status
     * @param reportStatus
     */
    fun deleteProcessedReports(reportStatus: String) {
        coverageDao.deleteProcessedReports(reportStatus)
    }


    /**
     * insertAllCoverageSingleSessionReportEntity insert the function defined in DAO
     * to insert [CoverageSingleSessionReportEntity] parameters into the database
     * @param coverageSingleSessionReportEntityList :CoverageSingleSessionReportEntity
     */
    fun insertAllCoverageSingleSessionReportEntity(coverageSingleSessionReportEntityList: List<CoverageSingleSessionReportEntity>): List<Long> {
        return runBlocking(Dispatchers.Default) {
            val result =
                async { coverageDao.insertAllCoverageSingleSessionReportEntity(*coverageSingleSessionReportEntityList.toTypedArray()) }.await()
            return@runBlocking result
        }
    }

    /**
     * To check the [BaseEchoLocateCoverageEntity] is available or not
     */
    fun isBaseEchoLocateCoverageEntityAvailable(sessionId: String): Boolean {
        return getBaseCoverageEntity(sessionId) != null
    }

    /**
     * To get [BaseEchoLocateCoverageEntity] of matched sessionId
     */
    private fun getBaseCoverageEntity(sessionId: String): BaseEchoLocateCoverageEntity? {
        return coverageDao.getBaseEchoLocateCoverageEntityBySessionID(sessionId)
    }
}