package com.tmobile.mytmobile.echolocate.lte.reportprocessor

import android.content.Context
import com.google.gson.Gson
import com.tmobile.mytmobile.echolocate.BuildConfig
import com.tmobile.mytmobile.echolocate.configuration.events.analytics.ELAnalyticActions
import com.tmobile.mytmobile.echolocate.configuration.events.analytics.ELAnalyticsEvent
import com.tmobile.mytmobile.echolocate.configuration.events.analytics.ELModulesEnum
import com.tmobile.mytmobile.echolocate.communicationbus.rxbus.PostTicket
import com.tmobile.mytmobile.echolocate.communicationbus.rxbus.RxBus
import com.tmobile.mytmobile.echolocate.lte.database.EchoLocateLteDatabase
import com.tmobile.mytmobile.echolocate.lte.database.dao.LteDao
import com.tmobile.mytmobile.echolocate.lte.database.entity.BaseEchoLocateLteEntity
import com.tmobile.mytmobile.echolocate.lte.database.entity.LteSingleSessionReportEntity
import com.tmobile.mytmobile.echolocate.lte.database.repository.LteRepository
import com.tmobile.mytmobile.echolocate.lte.manager.LteDataManager
import com.tmobile.mytmobile.echolocate.lte.model.*
import com.tmobile.mytmobile.echolocate.lte.reportprocessor.LteDataStatus.Companion.STATUS_PROCESSED
import com.tmobile.mytmobile.echolocate.lte.reportprocessor.LteDataStatus.Companion.STATUS_RAW
import com.tmobile.mytmobile.echolocate.lte.utils.LteConstants
import com.tmobile.mytmobile.echolocate.lte.utils.LteIntents
import com.tmobile.mytmobile.echolocate.utils.EchoLocateDateUtils
import com.tmobile.mytmobile.echolocate.utils.EchoLocateLog
import com.tmobile.mytmobile.echolocate.lte.utils.LteUtils
import com.tmobile.mytmobile.echolocate.utils.SingletonHolder
import java.util.*
import kotlin.collections.ArrayList

/**
 * Generated reports from raw data collected from OEM intents into lte reports.
 * Generate JSON file from DataBase
 */


class LteReportProcessor private constructor(val context: Context) {

    // Only single instance of report processor is needed to avoid report/data duplication
    companion object : SingletonHolder<LteReportProcessor, Context>(::LteReportProcessor)

    private val lteDao: LteDao = EchoLocateLteDatabase.getEchoLocateLteDatabase(context).lteDao()
    var lteRepository = LteRepository(context)
    val schemaVersion = "1"
    var status = ""

    /**
     * Process and save raw data to database with status PROCESSED
     * generate new report list for all new record(RAW status) by using generateLteReportFromRawData
     * [saveLteReportToDB] - save new generated reports to database with status RAW
     * [forEach{f -> f] - once proceed -> changed the status of data to PROCESSED
     * [markRawDataAsProcessed] - update status in database
     * [deleteRawData]-deletes the raw data from database
     */
    @Synchronized
    fun processRawData(androidWorkId: String? = null) {

        val baseEchoLocateLteEntityList = lteDao.getBaseEchoLocateLteEntityByStatus(STATUS_RAW)

        val lteSingleSessionReportEntityList =
            getLteSingleSessionReportList(baseEchoLocateLteEntityList)

        val savedList = saveLteReportToDB(lteSingleSessionReportEntityList)

        /** get number of calls for every voice report(6h) and sent to analytics module*/
        if (!lteSingleSessionReportEntityList.isNullOrEmpty()) {
            val analyticsEvent = ELAnalyticsEvent(
                ELModulesEnum.LTE,
                ELAnalyticActions.EL_NUMBER_OF_SESSIONS,
                lteSingleSessionReportEntityList.size.toString()
            )
            analyticsEvent.timeStamp = System.currentTimeMillis()

            val postAnalyticsTicket = PostTicket(analyticsEvent)
            RxBus.instance.post(postAnalyticsTicket)
        }

        // If the processed data is not saved into the database,
        // then do not delete the raw data or do not mark it as processed to avoid data loss.
        if (savedList.isNullOrEmpty()) {
            EchoLocateLog.eLogD("Diagnostic : List empty, could not save LTE processed data to db")

            LteUtils.sendJobCompletedToScheduler(androidWorkId, LteIntents.REPORT_GENERATOR_COMPONENT_NAME)

            return
        }

        // Do not delete data in debug so that it can be tested and data can be viewed.
        if (BuildConfig.DEBUG) {
            baseEchoLocateLteEntityList.forEach { f -> f.status = STATUS_PROCESSED }
            markRawDataAsProcessed(baseEchoLocateLteEntityList)
            EchoLocateLog.eLogD("Diagnostic : LTE report generated and data marks as processed")
        } else {
            deleteRawData(baseEchoLocateLteEntityList)
            EchoLocateLog.eLogD("Diagnostic : LTE report generated and raw data removed from database")
            EchoLocateLog.eLogI("Echo lte processed")
        }

        LteUtils.sendJobCompletedToScheduler(androidWorkId, LteIntents.REPORT_GENERATOR_COMPONENT_NAME)
    }

    /**
     * deletes the raw data for production build
     *
     * @param baseEchoLocateLteEntityList List of entities that needs to be deleted.
     */
    private fun deleteRawData(baseEchoLocateLteEntityList: List<BaseEchoLocateLteEntity>) {
        lteRepository.deleteRawData(baseEchoLocateLteEntityList)
    }


    /**
     * This fun saves the entities to database
     * The function doesn't create a new thread and executes the statement in the same thread
     * in which this function is called
     * @param lteSingleSessionReportEntityList: List<LteSingleSessionReportEntity> received in generateLteReportFromRawData()
     */
    private fun saveLteReportToDB(lteSingleSessionReportEntityList: List<LteSingleSessionReportEntity>): List<Long> {
        return lteRepository.insertAllLteSingleSessionReportEntity(lteSingleSessionReportEntityList)
    }

    /**
     * This fun updates record in database with new data by using [LteDao.updateAllBaseEchoLocateLteEntityStatus]
     * using Variable number of arguments (varargs) - getting latest value of data
     */
    private fun markRawDataAsProcessed(baseEchoLocateLteEntityList: List<BaseEchoLocateLteEntity>) {
        lteDao.updateAllBaseEchoLocateLteEntityStatus(*baseEchoLocateLteEntityList.toTypedArray())
    }

    /**
     * This fun generated final lte report, next it goes through manager to provider
     * Called from [LteDataManager]
     *
     * Generated LteMultiSessionReport from LteSingleSessionReport
     *
     * @param lteSingleSessionReportEntityList - List of LteSingleSessionReports is contained by all data object describing Echo locate intents.
     * @return [LteMultiSessionReport]
     */
    fun getLteMultiSessionReport(lteSingleSessionReportEntityList: List<LteSingleSessionReportEntity>): List<LteSingleSessionReport> {

        val lteSingleSessionReportList = ArrayList<LteSingleSessionReport>()

        for (lteSingleSessionReportEntity in lteSingleSessionReportEntityList) {
            val gson = Gson()
            val lteSingleSessionReport =
                gson.fromJson(lteSingleSessionReportEntity.json, LteSingleSessionReport::class.java)
            lteSingleSessionReportList.add(lteSingleSessionReport)
        }
        return lteSingleSessionReportList
    }


    /**
     * This fun generated final lte report, next it goes through manager to provider
     * Called from [LteDataManager]
     * Generated LteMultiSessionReport from LteSingleSessionReport
     * @params status - status of data RAW or PROCESSED
     * @params lteSingleSessionReportList - List of LteSingleSessionReports is contained by all data object describing Echo locate intents.
     * @return [LteMultiSessionReport]
     */
    fun getLteMultiSessionReportEntity(
        startTime: Long,
        endTime: Long
    ): List<LteSingleSessionReportEntity> {

        return if (startTime == 0L && endTime == 0L) {
            lteDao.getLteSingleSessionReportEntityList()
        } else {
            val startTimeStr =
                EchoLocateDateUtils.convertToShemaDateFormat(startTime.toString())
            val endTimeStr = EchoLocateDateUtils.convertToShemaDateFormat(endTime.toString())
            lteDao.getLteSingleSessionReportEntityList(startTimeStr, endTimeStr)
        }
    }

    fun updateLteReportEntity(lteReportEntityList: List<LteSingleSessionReportEntity>) {
        lteDao.updateLteReportEntityList(*lteReportEntityList.toTypedArray())
    }

        /**
     * Generated list of reports in format of [LteSingleSessionReportEntity] to save it to DB,
     * Params from LteSingleSessionReportEntity:
     * @params lteReportId - random generated ID
     * @params json - JSON generated from raw data
     * @params eventTimestamp - current time of event
     */
    private fun getLteSingleSessionReportList(baseEchoLocateLteEntityList: List<BaseEchoLocateLteEntity>): List<LteSingleSessionReportEntity> {

        val lteSingleSessionReportEntityList = ArrayList<LteSingleSessionReportEntity>()
        for (baseEchoLocateLteEntity in baseEchoLocateLteEntityList) {
            val lteSingleSessionReport = getLteSingleSessionReport(baseEchoLocateLteEntity)
            val jsonObject = Gson().toJson(lteSingleSessionReport)
            val lteSingleSessionReportEntity = LteSingleSessionReportEntity(
                UUID.randomUUID().toString(),
                jsonObject,
                EchoLocateDateUtils.convertToShemaDateFormat(System.currentTimeMillis().toString()),
                ""
            )

            lteSingleSessionReportEntityList.add(lteSingleSessionReportEntity)
        }
        return lteSingleSessionReportEntityList
    }

    /**
     * Generated [LteSingleSessionReport] in accordance which Schema JSON, data get status RAW
     * Params of each Lte Single Session in accordance with [LteSingleSessionReport]
     * @params trigger - trigger of application
     * @params triggerTimestamp - timestamp when trigger received
     * @params schemaVersion - version of the schema for which the data is being reported
     * @params LteOEMSV - - OEM software version - Ids of Android
     * @params BearerConfiguration - Defines BearerConfiguration type
     * @params CommonRFConfiguration - Defines CommonRFConfiguration type
     * @params DownlinkCarrierInfo - Defines DownlinkCarrierInfo type
     * @params DownlinkRFConfiguration - Defines DownlinkRFConfiguration type
     * @params LteLocation - Defines LteLocation type
     * @params LteNetworkIdentity - Defines LteNetworkIdentity type
     * @params LteSettings - Defines LteSettings type
     * @params SignalCondition - Defines SignalCondition type
     * @params UpLinkRFConfiguration - Defines UpLinkRFConfiguration type
     * @params UplinkCarrierInfo - Defines UplinkCarrierInfo type
     * * */
    private fun getLteSingleSessionReport(baseEchoLocateLteEntity: BaseEchoLocateLteEntity): LteSingleSessionReport {

        return LteSingleSessionReport(
            baseEchoLocateLteEntity.trigger,
            baseEchoLocateLteEntity.triggerTimestamp,
            baseEchoLocateLteEntity.oemApiVersion,
            baseEchoLocateLteEntity.schemaVersion,
            getLteOEMSV(baseEchoLocateLteEntity.sessionId),
            getBearerConfiguration(baseEchoLocateLteEntity.sessionId),
            getCommonRFConfiguration(baseEchoLocateLteEntity.sessionId),
            getDownlinkCarrierInfo(baseEchoLocateLteEntity.sessionId),
            getDownlinkRFConfiguration(baseEchoLocateLteEntity.sessionId),
            getLteLocation(baseEchoLocateLteEntity.sessionId),
            getLteNetworkIdentity(baseEchoLocateLteEntity.sessionId),
            getLteSettings(baseEchoLocateLteEntity.sessionId),
            getSignalCondition(baseEchoLocateLteEntity.sessionId),
            getUpLinkRFConfiguration(baseEchoLocateLteEntity.sessionId),
            getUplinkCarrierInfo(baseEchoLocateLteEntity.sessionId)
        )
    }

    /**
     * Generated list of [CAData]
     * Based on data class [CAData]
     * Data from DataMetricsWrapper
     * @param sessionId: String
     * @return caDataList
     */
    private fun getCAData(sessionId: String, uniqueId: String): List<CAData> {
        val caEntityList = lteDao.getCAEntityList(sessionId, uniqueId)
        val caDataList = ArrayList<CAData>()

        for (caEntity in caEntityList) {
            val caData = CAData(
                caEntity.earfcn,
                caEntity.bandNumber,
                caEntity.bandWidth,
                caEntity.carrierNum,
                caEntity.layers,
                caEntity.modulation,
                caEntity.pci,
                caEntity.cellId,
                caEntity.locationId
            )
            caDataList.add(caData)
        }
        return caDataList
    }

    /**
     * Generated list of [Bearer]
     * Based on data class [Bearer]
     * Data from DataMetricsWrapper
     * @param sessionId: String
     * @return bearerList
     */
    private fun getBearer(sessionId: String): List<Bearer> {
        val bearerEntityList = lteDao.getBearerEntityList(sessionId)
        val bearerList = ArrayList<Bearer>()

        for (bearerEntity in bearerEntityList) {
            val bearer = Bearer(
                bearerEntity.apnName,
                bearerEntity.qci
            )
            bearerList.add(bearer)
        }
        return bearerList
    }

    /**
     * Generated an object of [SecondCarrier]
     * Based on data class [SecondCarrier]
     * Entity class SecondCarrierEntity
     * Data from DataMetricsWrapper
     * @param sessionId: String
     * @return [SecondCarrier]
     */
    private fun getSecondCarrier(sessionId: String): SecondCarrier? {
        val secondCarrierEntity = lteDao.getSecondCarrierEntity(sessionId) ?: return null
        return SecondCarrier(
            secondCarrierEntity.rsrp ?: LteConstants.RSRP_UNAVAILABLE_VALUE.toString(),
            secondCarrierEntity.rsrq ?: LteConstants.RSRQ_UNAVAILABLE_VALUE.toString(),
            secondCarrierEntity.rssi ?: LteConstants.RSSI_UNAVAILABLE_VALUE.toString(),
            secondCarrierEntity.sinr ?: LteConstants.SINR_UNAVAILABLE_VALUE.toString()
        )
    }

    /**
     * Generated an object of [ThirdCarrier]
     * Based on data class [ThirdCarrier]
     * Entity class ThirdCarrierEntity
     * Data from DataMetricsWrapper
     * @param sessionId: String
     * @return [ThirdCarrier]
     */
    private fun getThirdCarrier(sessionId: String): ThirdCarrier? {
        val thirdCarrierEntity = lteDao.getThirdCarrierEntity(sessionId) ?: return null
        return ThirdCarrier(
            thirdCarrierEntity.rsrp ?: LteConstants.RSRP_UNAVAILABLE_VALUE.toString(),
            thirdCarrierEntity.rsrq ?: LteConstants.RSRQ_UNAVAILABLE_VALUE.toString(),
            thirdCarrierEntity.rssi ?: LteConstants.RSSI_UNAVAILABLE_VALUE.toString(),
            thirdCarrierEntity.sinr ?: LteConstants.SINR_UNAVAILABLE_VALUE.toString()
        )
    }

    /**
     * Generated an object of [LteOEMSV]
     * Based on data class [LteOEMSV]
     * Entity class LteOEMSVEntity
     * Data from DataMetricsWrapper
     * @param sessionId: String
     * @return [LteOEMSV]
     */
    private fun getLteOEMSV(sessionId: String): LteOEMSV {
        val lteOEMSVEntity = lteDao.getLteOEMSVEntity(sessionId)
        return if (lteOEMSVEntity == null) {
            LteOEMSV()
        } else {
            LteOEMSV(
                lteOEMSVEntity.softwareVersion ?: "",
                lteOEMSVEntity.customVersion ?: "",
                lteOEMSVEntity.radioVersion ?: "",
                lteOEMSVEntity.buildName ?: "",
                lteOEMSVEntity.androidVersion ?: ""
            )
        }
    }

    /**
     * Generated an object of [BearerConfiguration]
     * Based on data class [BearerConfiguration]
     * Entity class BearerConfigurationEntity
     * Data from DataMetricsWrapper
     * @param sessionId: String
     * @return object of [BearerConfiguration]
     */
    private fun getBearerConfiguration(sessionId: String): BearerConfiguration {
        val bearerConfigurationEntity = lteDao.getBearerConfigurationEntity(sessionId)
        return if (bearerConfigurationEntity == null) {
            BearerConfiguration()
        } else {
            var bearerList = getBearer(sessionId)
            if(bearerList.isEmpty()) {
                bearerList = listOf(Bearer())
            }
            return BearerConfiguration(
                bearerList,
                bearerConfigurationEntity.networkType ?: "",
                bearerConfigurationEntity.numberOfBearers ?:
                LteConstants.NUMBERS_OF_ACTIVE_BEARERS_UNAVAILABLE_VALUE.toString(),
                bearerConfigurationEntity.oemTimestamp ?: EchoLocateDateUtils.getTriggerTimeStamp()
            )
        }
    }

    /**
     * Generated an object of [CommonRFConfiguration]
     * Based on data class [CommonRFConfiguration]
     * Entity class CommonRFConfigurationEntity
     * Data from DataMetricsWrapper
     * @param sessionId: String
     * @return object of [CommonRFConfiguration]
     */
    private fun getCommonRFConfiguration(sessionId: String): CommonRFConfiguration {
        val commonRFConfigurationEntity = lteDao.getCommonRFConfigurationEntity(sessionId)
        return if (commonRFConfigurationEntity == null) {
            CommonRFConfiguration()
        } else {
            return CommonRFConfiguration(
                commonRFConfigurationEntity.lteULaa ?: 0,
                commonRFConfigurationEntity.rrcState ?: 0,
                commonRFConfigurationEntity.ytContentId ?: "",
                commonRFConfigurationEntity.ytLink ?: "",
                commonRFConfigurationEntity.antennaConfigurationRx ?: 0,
                commonRFConfigurationEntity.antennaConfigurationTx ?: 0,
                commonRFConfigurationEntity.networkType ?: "",
                commonRFConfigurationEntity.oemTimestamp ?: EchoLocateDateUtils.getTriggerTimeStamp(),
                commonRFConfigurationEntity.receiverDiversity ?: 0,
                commonRFConfigurationEntity.transmissionMode ?: 0
            )
        }
    }

    /**
     * Generated an object of [DownlinkCarrierInfo]
     * Based on data class [DownlinkCarrierInfo]
     * Entity class DownlinkCarrierInfoEntity
     * Data from DataMetricsWrapper
     * @param sessionId: String
     * @return object of [DownlinkCarrierInfo]
     */
    private fun getDownlinkCarrierInfo(sessionId: String): DownlinkCarrierInfo {
        val downLinkCarrierInfoEntity = lteDao.getDownlinkCarrierInfoEntity(sessionId)
        return if (downLinkCarrierInfoEntity == null) {
            DownlinkCarrierInfo()
        } else {
            var caDataList = getCAData(sessionId, downLinkCarrierInfoEntity.uniqueId)
            if(caDataList.isEmpty()) {
                caDataList = listOf(CAData(0,0,0,0,null,
                    null,null,null,null))
            }
            return DownlinkCarrierInfo(
                caDataList,
                downLinkCarrierInfoEntity.networkType ?: "",
                downLinkCarrierInfoEntity.numberAggregatedChannel ?: 0,
                downLinkCarrierInfoEntity.oemTimestamp ?: EchoLocateDateUtils.getTriggerTimeStamp()
            )
        }
    }

    /**
     * Generated an object of [DownlinkRFConfiguration]
     * Based on data class [DownlinkRFConfiguration]
     * Entity class DownlinkRFConfigurationEntity
     * Data from DataMetricsWrapper
     * @param sessionId: String
     * @return object of [DownlinkRFConfiguration]
     */
    private fun getDownlinkRFConfiguration(sessionId: String): DownlinkRFConfiguration {
        val downlinkRFConfigurationEntity = lteDao.getDownlinkRFConfigurationEntity(sessionId)
        return if (downlinkRFConfigurationEntity == null) {
            DownlinkRFConfiguration()
        } else {
            var caDataList = getCAData(sessionId, downlinkRFConfigurationEntity.uniqueId)
            if(caDataList.isEmpty()) {
                caDataList = listOf(CAData(null,null,null,0,0,
                    "",null,null,null))
            }
            return DownlinkRFConfiguration(
                caDataList,
                downlinkRFConfigurationEntity.networkType ?: "",
                downlinkRFConfigurationEntity.oemTimestamp ?: EchoLocateDateUtils.getTriggerTimeStamp()
            )
        }
    }

    /**
     * Generated an object of [LteLocation]
     * Based on data class [LteLocation]
     * Entity class LteLocationEntity
     * Data from LocationDataProcessor
     * @param sessionId: String
     * @return object of [LteLocation]
     */
    private fun getLteLocation(sessionId: String): LteLocation {
        val lteLocationEntity = lteDao.getLteLocationEntity(sessionId)

        return if (lteLocationEntity == null) {
            LteLocation()
        } else {
            return LteLocation(
                lteLocationEntity.altitude ?: 0.0,
                lteLocationEntity.altitudePrecision ?: 0.0F,
                lteLocationEntity.latitude ?: 0.0,
                lteLocationEntity.longitude ?: 0.0,
                lteLocationEntity.precision ?: 0.0F,
                lteLocationEntity.locationAge ?: 0,
                lteLocationEntity.timestamp ?: EchoLocateDateUtils.getTriggerTimeStamp()
            )
        }
    }

    /**
     * Generated an object of [LteNetworkIdentity]
     * Based on data class [LteNetworkIdentity]
     * Entity class NetworkIdentityEntity
     * Data from DataMetricsWrapper
     * @param sessionId: String
     * @return object of [LteNetworkIdentity]
     */
    private fun getLteNetworkIdentity(sessionId: String): LteNetworkIdentity {
        val networkIdentityEntity = lteDao.getNetworkIdentityEntity(sessionId)
        return if (networkIdentityEntity == null) {
            LteNetworkIdentity()
        } else {
            var caDataList = getCAData(sessionId, networkIdentityEntity.uniqueId)
            if(caDataList.isEmpty()) {
                caDataList = listOf(CAData(null,null,null,0,null,
                    null,"","",""))
            }
            return LteNetworkIdentity(
                caDataList,
                networkIdentityEntity.mcc ?: "",
                networkIdentityEntity.mnc ?: "",
                networkIdentityEntity.tac ?: "",
                networkIdentityEntity.networkType ?: "",
                networkIdentityEntity.oemTimestamp ?: EchoLocateDateUtils.getTriggerTimeStamp(),
                networkIdentityEntity.wifiConnectionStatus ?: ""
            )
        }
    }

    /**
     * Generated an object of [LteSettings]
     * Based on data class [LteSettings]
     * Entity class LteSettingsEntity
     * Data from DataMetricsWrapper
     * @param sessionId: String
     * @return object of [LteSettings]
     */
    private fun getLteSettings(sessionId: String): LteSettings {
        val lteSettingsEntity = lteDao.getLteSettingsEntity(sessionId)
        return if (lteSettingsEntity == null) {
            LteSettings()
        } else {
            return LteSettings(
                lteSettingsEntity.wifiCallingSetting ?: 0,
                lteSettingsEntity.wifiSetting ?: 0,
                lteSettingsEntity.mobileDataSettings ?: 0,
                lteSettingsEntity.networkModeSettings ?: 0,
                lteSettingsEntity.oemTimestamp ?: EchoLocateDateUtils.getTriggerTimeStamp(),
                lteSettingsEntity.roamingSetting ?: "",
                lteSettingsEntity.rtt ?: "",
                lteSettingsEntity.rttTranscript ?: "",
                lteSettingsEntity.volteSetting ?: 0
            )
        }
    }

    /**
     * Generated an object of [SignalCondition]
     * Based on data class [SignalCondition]
     * Entity class SignalConditionEntity
     * Data from DataMetricsWrapper
     * @param sessionId: String
     * @return object of [SignalCondition]
     */
    private fun getSignalCondition(sessionId: String): SignalCondition {
        val signalConditionEntity = lteDao.getSignalConditionEntity(sessionId)

        return if (signalConditionEntity == null) {
            SignalCondition()
        } else {
            val secondCarrierList = getSecondCarrier(sessionId)
            val thirdCarrierList = getThirdCarrier(sessionId)
            return SignalCondition(
                signalConditionEntity.lteUlHeadroom ?: LteConstants.LTE_UL_HEADROOM_UNAVAILABLE_VALUE ,
                signalConditionEntity.rachPower ?: LteConstants.RACH_POWER_UNAVAILABLE_VALUE,
                signalConditionEntity.rsrp ?: LteConstants.RSRP_UNAVAILABLE_VALUE,
                signalConditionEntity.rsrq ?: LteConstants.RSRQ_UNAVAILABLE_VALUE,
                signalConditionEntity.rssi ?: LteConstants.RSSI_UNAVAILABLE_VALUE,
                signalConditionEntity.sinr ?: LteConstants.SINR_UNAVAILABLE_VALUE,
                signalConditionEntity.networkType ?: "",
                signalConditionEntity.oemTimestamp ?: EchoLocateDateUtils.getTriggerTimeStamp(),
                secondCarrierList ?: SecondCarrier(),
                thirdCarrierList ?: ThirdCarrier()
            )
        }
    }

    /**
     * Generated an object of [UpLinkRFConfiguration]
     * Based on data class [UpLinkRFConfiguration]
     * Entity class UpLinkRFConfigurationEntity
     * Data from DataMetricsWrapper
     * @param sessionId: String
     * @return object of [UpLinkRFConfiguration]
     */
    private fun getUpLinkRFConfiguration(sessionId: String): UpLinkRFConfiguration {
        val upLinkRFConfigurationEntity = lteDao.getUpLinkRFConfigurationEntity(sessionId)
        return if (upLinkRFConfigurationEntity == null) {
            UpLinkRFConfiguration()
        } else {
            var caDataList = getCAData(sessionId, upLinkRFConfigurationEntity.uniqueId)
            if(caDataList.isEmpty()) {
                caDataList = listOf(CAData(null,null,null,0,null,
                    "",null,null,null))
            }
            return UpLinkRFConfiguration(
                caDataList,
                upLinkRFConfigurationEntity.networkType ?: "",
                upLinkRFConfigurationEntity.oemTimestamp ?: EchoLocateDateUtils.getTriggerTimeStamp()
            )
        }
    }

    /**
     * Generated an object of [UplinkCarrierInfo]
     * Based on data class [UplinkCarrierInfo]
     * Entity class UplinkCarrierInfoEntity
     * Data from DataMetricsWrapper
     * @param sessionId: String
     * @return object of [UplinkCarrierInfo]
     */
    private fun getUplinkCarrierInfo(sessionId: String): UplinkCarrierInfo {
        val uplinkCarrierInfoEntity = lteDao.getUplinkCarrierInfoEntity(sessionId)
        return if (uplinkCarrierInfoEntity == null) {
            UplinkCarrierInfo()
        } else {
            var caDataList = getCAData(sessionId, uplinkCarrierInfoEntity.uniqueId)
            if(caDataList.isEmpty()) {
                caDataList = listOf(CAData(0,0,0,0,null,
                    null,null,null,null))
            }
            return UplinkCarrierInfo(
                caDataList,
                uplinkCarrierInfoEntity.networkType ?: "",
                uplinkCarrierInfoEntity.numberAggregateChannel ?: 0,
                uplinkCarrierInfoEntity.oemTimestamp ?: EchoLocateDateUtils.getTriggerTimeStamp()
            )
        }
    }


    /**
     * This function deletes the the report data from db based on the status
     * @param reportStatus
     */
    fun deleteProcessedReports(reportStatus : String) {
        lteRepository.deleteProcessedReports(reportStatus)
    }
}

