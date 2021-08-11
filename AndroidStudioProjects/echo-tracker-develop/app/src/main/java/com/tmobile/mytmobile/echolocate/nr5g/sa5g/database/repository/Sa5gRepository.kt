package com.tmobile.mytmobile.echolocate.nr5g.sa5g.database.repository

import android.content.Context
import com.tmobile.mytmobile.echolocate.nr5g.sa5g.database.EchoLocateSa5gDatabase
import com.tmobile.mytmobile.echolocate.nr5g.sa5g.database.dao.Sa5gDao
import com.tmobile.mytmobile.echolocate.nr5g.sa5g.database.entity.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

/**
 * The [Sa5gRepository] repository class will be responsible for interacting with the Room database
 * and will need to provide methods that use the DAO to insert, delete and query product records.
 * @param context :Context the context passed from activity
 */
class Sa5gRepository(context: Context) {

    /***
     * gets the Sa5g DAO instance defined as a abstract class in [EchoLocateSa5gDatabase]
     */
    private val sa5gDao: Sa5gDao =
        EchoLocateSa5gDatabase.getEchoLocateSa5gDatabase(context).sa5gDao()

    /**
     * insertSa5gOEMSVEntity the insert fun defined in DAO to
     * insert [Sa5gOEMSVEntity] parameters into the database
     * @param sa5gOEMSVEntity :Sa5gOEMSVEntity object
     */
    fun insertSa5gOEMSVEntity(sa5gOEMSVEntity: Sa5gOEMSVEntity) {
        sa5gDao.insertSa5gOEMSVEntity(sa5gOEMSVEntity)
    }

    /**
     * insertSa5gLocationEntity function is defined in DAO to
     * insert [Sa5gLocationEntity] parameters into the database
     * @param sa5gLocationEntity :Sa5gLocationEntity object
     */
    fun insertSa5gLocationEntity(sa5gLocationEntity: Sa5gLocationEntity) {
        sa5gDao.insertSa5gLocationEntity(sa5gLocationEntity)
    }

    /**
     * insertSa5gDeviceInfoEntity  function is defined in DAO to
     * insert [Sa5gDeviceInfoEntity] parameters into the database
     * @param sa5gDeviceInfoEntity :Sa5gDeviceInfoEntity object
     */
    fun insertSa5gDeviceInfoEntity(sa5gDeviceInfoEntity: Sa5gDeviceInfoEntity) {
        sa5gDao.insertSa5gDeviceInfoEntity(sa5gDeviceInfoEntity)
    }

    /**
     * insertSa5gTriggerEntity function is defined in DAO to
     * insert [Sa5gTriggerEntity] parameters into the database
     * @param sa5gTriggerEntity :Sa5gTriggerEntity object
     */
    fun insertSa5gTriggerEntity(sa5gTriggerEntity: Sa5gTriggerEntity) {
        sa5gDao.insertSa5gTriggerEntity(sa5gTriggerEntity)
    }

    /**
     * Calls the insert method defined in DAO to insert [Sa5gRrcLogEntity] parameters into the database
     * @param sa5gRrcLogEntity :Sa5gRrcLogEntity object
     */
    fun insertSa5gRrcLogEntity(sa5gRrcLogEntity: Sa5gRrcLogEntity) {
        sa5gDao.insertSa5gRrcLogEntity(sa5gRrcLogEntity)
    }

    /**
     * Calls the insert method defined in DAO to insert [Sa5gNetworkLogEntity] parameters into the database
     * @param sa5gNetworkLogEntity :Sa5gNetworkLogEntity object
     */
    fun insertSa5gNetworkLogEntity(sa5gNetworkLogEntity: Sa5gNetworkLogEntity) {
        sa5gDao.insertSa5gNetworkLogEntity(sa5gNetworkLogEntity)
    }

    /**
     * Calls the insert method defined in DAO to insert [Sa5gSettingsLogEntity] parameters into the database
     * @param sa5gSettingsLogEntity :Sa5gSettingsLogEntity object
     */
    fun insertSa5gSettingsLogEntity(sa5gSettingsLogEntity: Sa5gSettingsLogEntity) {
        sa5gDao.insertSa5gSettingsLogEntity(sa5gSettingsLogEntity)
    }

    /**
     * Calls the insert method defined in DAO to insert [Sa5gUiLogEntity] parameters into the database
     * @param sa5gUiLogEntity :Sa5gUiLogEntity object
     */
    fun insertSa5gUiLogEntity(sa5gUiLogEntity: Sa5gUiLogEntity) {
        sa5gDao.insertSa5gUiLogEntity(sa5gUiLogEntity)
    }

    /**
     * Calls the insert method defined in DAO to insert [Sa5gConnectedWifiStatusEntity] parameters into the database
     * @param sa5gConnectedWifiStatusEntity :Sa5gConnectedWifiStatusEntity object
     */
    fun insertSa5gConnectedWifiStatusEntity(sa5gConnectedWifiStatusEntity: Sa5gConnectedWifiStatusEntity) {
        sa5gDao.insertSa5gConnectedWifiStatusEntity(sa5gConnectedWifiStatusEntity)
    }

    /**
     * Calls the insert method defined in DAO to insert [Sa5gActiveNetworkEntity] parameters into the database
     * @param sa5gActiveNetworkEntity :Sa5gActiveNetworkEntity object
     */
    fun insertSa5gActiveNetworkEntity(sa5gActiveNetworkEntity: Sa5gActiveNetworkEntity) {
        sa5gDao.insertSa5gActiveNetworkEntity(sa5gActiveNetworkEntity)
    }

    /**
     * Calls the insert method defined in DAO to insert [Sa5gWiFiStateEntity] parameters into the database
     * @param sa5gWiFiStateEntity :Sa5gWiFiStateEntity object
     */
    fun insertSa5gWiFiStateEntity(sa5gWiFiStateEntity: Sa5gWiFiStateEntity) {
        sa5gDao.insertSa5gWiFiStateEntity(sa5gWiFiStateEntity)
    }

    /**
     * Calls the insert method defined in DAO to insert [Sa5gCarrierConfigEntity] parameters into the database
     * @param sa5gCarrierConfigEntity :Sa5gCarrierConfigEntity object
     */
    fun insertSa5gCarrierConfigEntity(sa5gCarrierConfigEntity: Sa5gCarrierConfigEntity) {
        sa5gDao.insertSa5gCarrierConfigEntity(sa5gCarrierConfigEntity)
    }

    /**
     * Deletes all the raw data from all the Sa5g tables once it is processed for production build
     * sessionIdList will have all the processed data list, so that data can be deleted from other tables
     */
    fun deleteRawData(baseEchoLocateSa5gEntity: List<BaseEchoLocateSa5gEntity>) {
        sa5gDao.deleteRawDataFromAllTables(baseEchoLocateSa5gEntity)
    }

    /**
     * This function deletes the the report data from db based on the status
     * @param reportStatus
     */
    fun deleteProcessedReports(reportStatus: String) {
        sa5gDao.deleteProcessedReports(reportStatus)
    }

    /**
     * insertAllSa5gSingleSessionReportEntity the insert fun defined in DAO
     * to insert [Sa5gSingleSessionReportEntity] parameters into the database
     * @param sa5gSingleSessionReportEntityList :Sa5gSingleSessionReportEntity
     */
    fun insertAllSa5gSingleSessionReportEntity(sa5gSingleSessionReportEntityList: List<Sa5gSingleSessionReportEntity>): List<Long> {
        return runBlocking(Dispatchers.Default) {
            return@runBlocking withContext(Dispatchers.Default) {
                sa5gDao.insertAllSa5gSingleSessionReportEntity(*sa5gSingleSessionReportEntityList.toTypedArray())
            }
        }
    }

    /**
     * insertAllSa5gDownlinkCarrierLogsEntity the insert fun defined in DAO to insert
     * [Sa5gDownlinkCarrierLogsEntity] parameters into the database
     * @param sa5gDownlinkCarrierLogsEntityList :List<Sa5gDownlinkCarrierLogsEntity> object
     */
    fun insertAllSa5gDownlinkCarrierLogsEntity(sa5gDownlinkCarrierLogsEntityList: List<Sa5gDownlinkCarrierLogsEntity>) {
        sa5gDao.insertAllSa5gDownlinkCarrierLogsEntity(*sa5gDownlinkCarrierLogsEntityList.toTypedArray())
    }

    /**
     * Calls the insert method defined in DAO to insert [Sa5gUplinkCarrierLogsEntity] parameters into the database
     * @param sa5gUplinkCarrierLogsEntityList :Sa5gUplinkCarrierLogsEntity object
     */
    fun insertAllSa5gUplinkCarrierLogsEntity(sa5gUplinkCarrierLogsEntityList: List<Sa5gUplinkCarrierLogsEntity>) {
        sa5gDao.insertAllSa5gUplinkCarrierLogsEntity(*sa5gUplinkCarrierLogsEntityList.toTypedArray())
    }

}