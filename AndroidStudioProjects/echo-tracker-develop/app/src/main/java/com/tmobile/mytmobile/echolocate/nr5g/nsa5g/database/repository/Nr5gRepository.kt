package com.tmobile.mytmobile.echolocate.nr5g.nsa5g.database.repository

import android.content.Context
import com.tmobile.mytmobile.echolocate.nr5g.nsa5g.database.EchoLocateNr5gDatabase
import com.tmobile.mytmobile.echolocate.nr5g.nsa5g.database.dao.Nr5gDao
import com.tmobile.mytmobile.echolocate.nr5g.nsa5g.database.entity.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking

/**
 * The [Nr5gRepository] repository class will be responsible for interacting with the Room database
 * and will need to provide methods that use the DAO to insert, delete and query product records.
 * @param context :Context the context passed from activity
 */
class Nr5gRepository(context: Context) {

    /***
     * gets the Nr5g DAO instance defined as a abstract class in [EchoLocateNr5gDatabase]
     */
    private val nr5gDao: Nr5gDao =
        EchoLocateNr5gDatabase.getEchoLocateNr5gDatabase(context).nr5gDao()

    /**
     * insertNr5gOEMSVEntity the insert fun defined in DAO to
     * insert [Nr5gOEMSVEntity] parameters into the database
     * @param nr5gOEMSVEntity :Nr5gOEMSVEntity object
     */
    fun insertNr5gOEMSVEntity(nr5gOEMSVEntity: Nr5gOEMSVEntity) {
        nr5gDao.insertNr5gOEMSVEntity(nr5gOEMSVEntity)
    }

    /**
     * insertNr5gDeviceInfoEntity  function is defined in DAO to
     * insert [DeviceInfoEntity] parameters into the database
     * @param deviceInfoEntity :DeviceInfoEntity object
     */
    fun insertNr5gDeviceInfoEntity(deviceInfoEntity: Nr5gDeviceInfoEntity) {
        nr5gDao.insertNr5gDeviceInfoEntity(deviceInfoEntity)
    }

    /**
     * insertNr5gWifiStateEntity the insert fun defined in DAO to
     * insert [Nr5gWifiStateEntity] parameters into the database
     * @param nr5gWifiStateEntity :GetWifiStateEntity object
     */
    fun insertNr5gWifiStateEntity(nr5gWifiStateEntity: Nr5gWifiStateEntity) {
        nr5gDao.insertNr5gWifiStateEntity(nr5gWifiStateEntity)
    }

    /**
     * insertNr5gDataNetworkTypeEntity the insert fun defined in DAO to
     * insert [Nr5gDataNetworkTypeEntity] parameters into the database
     * @param nr5gDataNetworkTypeEntity :Nr5gDataNetworkTypeEntity object
     */
    fun insertNr5gDataNetworkTypeEntity(nr5gDataNetworkTypeEntity: Nr5gDataNetworkTypeEntity) {
        nr5gDao.insertNr5gDataNetworkTypeEntity(nr5gDataNetworkTypeEntity)
    }

    /**
     * insertNr5gStatusEntity the insert fun defined in DAO to
     * insert [Nr5gStatusEntity] parameters into the database
     * @param nr5gStatusEntity :Nr5gStatusEntity object
     */
    fun insertNr5gStatusEntity(nr5gStatusEntity: Nr5gStatusEntity) {
        nr5gDao.insertNr5gStatusEntity(nr5gStatusEntity)
    }

    /**
     * Calls the insert fun defined in DAO to insert [Nr5gLocationEntity] parameters into the database
     * @param nr5GLocationEntity: LocationEntity object
     */
    fun insertNr5GLocationEntity(nr5GLocationEntity: Nr5gLocationEntity) {
        nr5gDao.insertNr5gLocationEntity(nr5GLocationEntity)
    }

    /**
     * nr5gNetworkIdentityEntity the insert fun defined in DAO to
     * insert [Nr5gNetworkIdentityEntity] parameters into the database
     * @param nr5gNetworkIdentityEntity :Nr5gNetworkIdentityEntity object
     */
    fun insertNr5gNetworkIdentityEntity(nr5gNetworkIdentityEntity: Nr5gNetworkIdentityEntity) {
        nr5gDao.insertNr5gNetworkIdentityEntity(nr5gNetworkIdentityEntity)
    }

    /**
     * pass New Radio 5G current active network to DAO
     * @param activeNetworkEntity: GetActiveNetworkEntity object
     */
    fun insertNr5gActiveNetwork(activeNetworkEntity: Nr5gActiveNetworkEntity) {
        nr5gDao.insertNr5gActiveNetworkEntity(activeNetworkEntity)
    }

    /**
     * nr5gNetworkIdentityEntity the insert fun defined in DAO to
     * insert [Nr5gNetworkIdentityEntity] parameters into the database
     * @param nr5gNetworkIdentityEntity :Nr5gNetworkIdentityEntity object
     */
    fun insertConnectedWifiStatusEnitity(connectedWifiStatusEntity: ConnectedWifiStatusEntity) {
        nr5gDao.insertConnectedWifiStatusEntity(connectedWifiStatusEntity)
    }

    /**
     * Calls the insert method defined in DAO to insert [Nr5gMmwCellLogEntity] parameters into the database
     * @param nr5gMmwCellLogEntity :Nr5gMmwCellLogEntity object
     */
    fun insertMmwCellLogEntity(nr5gMmwCellLogEntity: Nr5gMmwCellLogEntity) {
        nr5gDao.insertNr5gMmwCellLogEntity(nr5gMmwCellLogEntity)
    }

    /**
     * Inserts nr5gUiLogEntity into database
     */
    fun insertNr5gUiLog(nr5gUiLogEntity: Nr5gUiLogEntity) {
        nr5gDao.insertNr5gUiLogEntity(nr5gUiLogEntity)
    }

    /**
     * endcLteLogEntity the insert fun defined in DAO to
     * insert [EndcLteLogEntity] parameters into the database
     * @param endcLteLogEntity :Nr5gNetworkIdentityEntity object
     */
    fun insertEndcLteLogEntity(endcLteLogEntity: EndcLteLogEntity) {
        nr5gDao.insertEndcLteLogEntity(endcLteLogEntity)
    }

    /**
     * Calls the insert method defined in DAO to insert [EndcUplinkLogEntity] parameters into the database
     * @param endcUplinkLogEntity :EndcUplinkLogEntity object
     */
    fun insertEndcUplinkLogEntity(endcUplinkLogEntity: EndcUplinkLogEntity) {
        nr5gDao.insertEndcUplinkLogEntity(endcUplinkLogEntity)
    }

    /**
     * Deletes all the raw data from all the nr5g tables once it is processed for production build
     * sessionIdList will have all the processed data list, so that data can be deleted from other tables
     */
    fun deleteRawData(baseEchoLocateNr5gEntityList: List<BaseEchoLocateNr5gEntity>) {
        nr5gDao.deleteRawDataFromAllTables(baseEchoLocateNr5gEntityList)
    }

    /**
     * This function deletes the the report data from db based on the status
     * @param reportStatus
     */
    fun deleteProcessedReports(reportStatus: String) {
        nr5gDao.deleteProcessedReports(reportStatus)
    }

    /**
     * insertAllNr5gSingleSessionReportEntity the insert fun defined in DAO
     * to insert [Nr5gSingleSessionReportEntity] parameters into the database
     * @param nr5gSingleSessionReportEntityList :Nr5gSingleSessionReportEntity
     */
    fun insertAllNr5gSingleSessionReportEntity(nr5gSingleSessionReportEntityList: List<Nr5gSingleSessionReportEntity>): List<Long> {
        return runBlocking(Dispatchers.Default) {
            val result =
                async { nr5gDao.insertAllNr5gSingleSessionReportEntity(*nr5gSingleSessionReportEntityList.toTypedArray()) }.await()
            return@runBlocking result
        }
    }
}