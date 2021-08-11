package com.tmobile.mytmobile.echolocate.lte.database.repository

import android.content.Context
import com.tmobile.mytmobile.echolocate.lte.database.EchoLocateLteDatabase
import com.tmobile.mytmobile.echolocate.lte.database.dao.LteDao
import com.tmobile.mytmobile.echolocate.lte.database.entity.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking

/**
 * The [LteRepository] repository class will be responsible for interacting with the Room database
 * and will need to provide methods that use the DAO to insert, delete and query product records.
 * @param context :Context the context passed from activity
 */
class LteRepository(context: Context) {

    /**
     * gets the lte DAO instance defined as a abstract class in [EchoLocateLteDatabase]
     */
    private val lteDao: LteDao =
        EchoLocateLteDatabase.getEchoLocateLteDatabase(context).lteDao()

    /**
     * Calls the insert method defined in DAO to insert [BaseEchoLocateLteEntity] parameters into the database
     * @param baseEchoLocateLteEntity :BaseEchoLocateLteEntity object
     */
    fun insertBaseEchoLocateLteEntity(baseEchoLocateLteEntity: BaseEchoLocateLteEntity) {
        lteDao.insertBaseEchoLocateLteEntity(baseEchoLocateLteEntity)
    }

    /**
     * Calls the insert method defined in DAO to insert [BearerConfigurationEntity] parameters into the database
     * @param bearerConfigurationEntity :BearerConfigurationEntity object
     */
    fun insertBearerConfiguration(bearerConfigurationEntity: BearerConfigurationEntity) {
        lteDao.insertBearerConfigurationEntity(bearerConfigurationEntity)
    }

    /**
     * Calls the insert method defined in DAO to insert [CommonRFConfigurationEntity] parameters into the database
     * @param commonRFConfigurationEntity :CommonRFConfigurationEntity object
     */
    fun insertCommonRFConfigurationEntity(commonRFConfigurationEntity: CommonRFConfigurationEntity) {
        lteDao.insertCommonRFConfigurationEntity(commonRFConfigurationEntity)
    }


    /**
     * Calls the insert method defined in DAO to insert [LteSettingsEntity] parameters into the database
     * @param lteSettingsEntity :LteSettingsEntity object
     */
    fun insertLteSettingsEntity(lteSettingsEntity: LteSettingsEntity) {
        lteDao.insertLteSettingsEntity(lteSettingsEntity)
    }

    /**
     * Calls the insert method defined in DAO to insert [BaseEchoLocateLteEntity] parameters into the database
     * @param baseEchoLocateLteEntity :BaseEchoLocateLteEntity object
     */
    fun insertUpLinkRFConfiguration(upLinkRFConfigurationEntity: UpLinkRFConfigurationEntity) {
        lteDao.insertUpLinkRFConfigurationEntity(upLinkRFConfigurationEntity)
    }

    /**
     * Calls the insert method defined in DAO to insert [DownlinkRFConfigurationEntity] parameters into the database
     * @param downLinkRFConfigurationEntity
     */
    fun insertDownLinkRFConfiguration(downLinkRFConfigurationEntity: DownlinkRFConfigurationEntity) {
        lteDao.insertDownLinkRFConfigurationEntity(downLinkRFConfigurationEntity)
    }

    /**
     * insertAllCAEntity the insert fun defined in DAO to insert [CAEntity] parameters into the database
     * @param caEntityList :CAEntity object
     */
    fun insertAllCAEntityEntity(caEntityList: List<CAEntity>) {
        lteDao.insertAllCAEntity(*caEntityList.toTypedArray())
    }

    /**
     * insertAllBearerEntity the insert fun defined in DAO to insert [BearerEntity] parameters into the database
     * @param bearerEntityList :BearerEntity object
     */
    fun insertAllBearerEntity(bearerEntityList: List<BearerEntity>) {
        lteDao.insertAllBearerEntity(*bearerEntityList.toTypedArray())
    }

    /**
     * insertUplinkCarrierInfoEntity the insert fun defined in DAO to insert [UplinkCarrierInfoEntity] parameters into the database
     * @param uplinkCarrierInfoEntity :UplinkCarrierInfoEntity object
     */
    fun insertUplinkCarrierInfoEntity(uplinkCarrierInfoEntity: UplinkCarrierInfoEntity) {
        lteDao.insertUplinkCarrierInfoEntity(uplinkCarrierInfoEntity)
    }

    /**
     * insertNetworkIdentityEntity the insert fun defined in DAO to insert [NetworkIdentityEntity] parameters into the database
     * @param networkIdentityEntity: NetworkIdentityEntity object
     */
    fun insertNetworkIdentityEntity(networkIdentityEntity: NetworkIdentityEntity) {
        lteDao.insertNetworkIdentityEntity(networkIdentityEntity)
    }

    /**
     * get BaseEchoLocateLteEntity based on the session id.
     * @param sessionId :String object
     */
    fun getBaseEchoLocateLteEntityBySessionID(sessionId: String): BaseEchoLocateLteEntity? =
        runBlocking(Dispatchers.Default) {
            val result =
                async { lteDao.getBaseEchoLocateLteEntityBySessionID(sessionId) }.await()
            return@runBlocking result
        }

    /**
     * insertSignalCondition the insert fun defined in DAO to insert [SignalConditionEntity] parameters into the database
     * @param signalConditionEntity :SignalConditionEntity object
     */
    fun insertSignalCondition(signalConditionEntity: SignalConditionEntity) {
        lteDao.insertSignalConditionEntity(signalConditionEntity)
    }

    /**
     * insertSecondCarrierEntity the insert fun defined in DAO to insert [SecondCarrierEntity] parameters into the database
     * @param signalConditionEntity :SecondCarrierEntity object
     */
    fun insertSecondCarrierEntity(signalConditionEntity: SecondCarrierEntity) {
        lteDao.insertSecondCarrierEntity(signalConditionEntity)
    }

    /**
     * insertThirdCarrierEntity the insert fun defined in DAO to insert [ThirdCarrierEntity] parameters into the database
     * @param signalConditionEntity :ThirdCarrierEntity object
     */
    fun insertThirdCarrierEntity(signalConditionEntity: ThirdCarrierEntity) {
        lteDao.insertThirdCarrierEntity(signalConditionEntity)
    }

    /**
     * insertAllLteSingleSessionReportEntity the insert fun defined in DAO to insert [LteSingleSessionReportEntity] parameters into the database
     * @param lteSingleSessionReportEntityList :LteSingleSessionReportEntity
     */
    fun insertAllLteSingleSessionReportEntity(lteSingleSessionReportEntityList: List<LteSingleSessionReportEntity>): List<Long> {
        return runBlocking(Dispatchers.Default) {
            val result =
                async { lteDao.insertAllLteSingleSessionReportEntity(*lteSingleSessionReportEntityList.toTypedArray()) }.await()
            return@runBlocking result
        }
    }

    /**
     * insertLteOEMSVEntity the insert fun defined in DAO to insert [LteOEMSVEntity] parameters into the database
     * @param lteOEMSVEntity :LteOEMSVEntity object
     */
    fun insertLteOEMSVEntity(lteOEMSVEntity: LteOEMSVEntity) {
        lteDao.insertLteOEMSVEntity(lteOEMSVEntity)
    }

    /**
     * insertLteLocationEntity the insert fun defined in DAO to insert [LteLocationEntity] parameters into the database
     * @param lteLocationEntity: LteLocationEntity object
     */
    fun insertLteLocationEntity(lteLocationEntity: LteLocationEntity) {
        lteDao.insertLteLocationEntity(lteLocationEntity)
    }

    /**
     * updateLteLocationEntity the insert fun defined in DAO to update [LteLocationEntity] parameters into the database
     * @param lteLocationEntity: LteLocationEntity object
     */
    fun updateLteLocationEntity(lteLocationEntity: LteLocationEntity) {
        lteDao.updateLteLocationEntity(lteLocationEntity)
    }

    /**
     * get LteLocationEntity based on the session id.
     * @param sessionId :String object
     */
    fun getLteLocationEntity(sessionId: String): LteLocationEntity? =
        runBlocking(Dispatchers.Default) {
            val result =
                async { lteDao.getLteLocationEntity(sessionId) }.await()
            return@runBlocking result
        }

    /**
     * insertDownlinkCarrierInfoEntity the insert fun defined in DAO to insert [DownLinkCarrierInfoEntity] parameters into the database
     * @param downLinkCarrierInfoEntity :DownLinkCarrierInfoEntity object
     */
    fun insertDownlinkCarrierInfoEntity(downLinkCarrierInfoEntity: DownLinkCarrierInfoEntity) {
        lteDao.insertDownlinkCarrierInfoEntity(downLinkCarrierInfoEntity)
    }


    /**
     * This function deletes the the report data from db based on the status
     * @param reportStatus
     */
    fun deleteProcessedReports(reportStatus : String) {
        lteDao.deleteProcessedReports(reportStatus)
    }

    /**
     * Deletes all the raw data from all the lte tables once it is processed for production build
     * sessionIdList will have all the processed data list, so that data can be deleted from other tables
     */
    fun deleteRawData(baseEchoLocateLteEntityList: List<BaseEchoLocateLteEntity>) {
        lteDao.deleteRawDataFromAllTables(baseEchoLocateLteEntityList)

    }

}
