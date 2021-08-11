package com.tmobile.mytmobile.echolocate.nr5g.sa5g.reportprocessor

import android.content.Context
import com.google.gson.Gson
import com.tmobile.mytmobile.echolocate.BuildConfig
import com.tmobile.mytmobile.echolocate.communicationbus.rxbus.PostTicket
import com.tmobile.mytmobile.echolocate.communicationbus.rxbus.RxBus
import com.tmobile.mytmobile.echolocate.configuration.events.analytics.ELAnalyticActions
import com.tmobile.mytmobile.echolocate.configuration.events.analytics.ELAnalyticsEvent
import com.tmobile.mytmobile.echolocate.configuration.events.analytics.ELModulesEnum
import com.tmobile.mytmobile.echolocate.nr5g.core.oemdata.Sa5gDataMetricsWrapper
import com.tmobile.mytmobile.echolocate.nr5g.core.utils.Nr5gConstants
import com.tmobile.mytmobile.echolocate.nr5g.sa5g.database.EchoLocateSa5gDatabase
import com.tmobile.mytmobile.echolocate.nr5g.sa5g.database.dao.Sa5gDao
import com.tmobile.mytmobile.echolocate.nr5g.sa5g.database.entity.BaseEchoLocateSa5gEntity
import com.tmobile.mytmobile.echolocate.nr5g.sa5g.database.entity.Sa5gSingleSessionReportEntity
import com.tmobile.mytmobile.echolocate.nr5g.sa5g.database.repository.Sa5gRepository
import com.tmobile.mytmobile.echolocate.nr5g.sa5g.model.*
import com.tmobile.mytmobile.echolocate.nr5g.sa5g.reportprocessor.Sa5gDataStatus.Companion.STATUS_PROCESSED
import com.tmobile.mytmobile.echolocate.nr5g.sa5g.reportprocessor.Sa5gDataStatus.Companion.STATUS_RAW
import com.tmobile.mytmobile.echolocate.utils.EchoLocateDateUtils
import com.tmobile.mytmobile.echolocate.utils.EchoLocateLog
import com.tmobile.mytmobile.echolocate.nr5g.core.utils.Nr5gUtils
import com.tmobile.mytmobile.echolocate.utils.SingletonHolder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by Sudhansu Sekhar on 2020-09-01
 *
 * For processing reports for Sa5g module
 * Generate reports from raw data collected in Sa5g database into reports.
 * Generates JSON file from DataBase
 *
 */

class Sa5gReportProcessor private constructor(val context: Context) {

    // Only single instance of report processor is needed to avoid report/data duplication
    companion object : SingletonHolder<Sa5gReportProcessor, Context>(::Sa5gReportProcessor)

    private val sa5gDao: Sa5gDao =
        EchoLocateSa5gDatabase.getEchoLocateSa5gDatabase(context).sa5gDao()
    var sa5gRepository = Sa5gRepository(context)
    val schemaVersion = "1"
    var status = ""

    /**
     * Process and save raw data to database with status PROCESSED
     * generate new report list for all new record(RAW status)
     * [saveSa5gReportToDB] - save new generated reports to database with status RAW
     * [forEach{f -> f] - once proceed -> changed the status of data to PROCESSED
     * [markRawDataAsProcessed] - update status in database
     * [deleteRawData]-deletes the raw data from database
     */
    @Synchronized
    fun processRawData(androidWorkId: String? = null) {

        val baseEchoLocateSa5gEntityList = sa5gDao.getBaseEchoLocateSa5gEntityByStatus(STATUS_RAW)

        val sa5gSingleSessionReportEntityList =
            getSa5gSingleSessionReportList(baseEchoLocateSa5gEntityList)

        val savedList = saveSa5gReportToDB(sa5gSingleSessionReportEntityList)

        /** If the processed data is not saved into the database,
         *  then do not delete the raw data or do not mark it as processed to avoid data loss.*/
        if (savedList.isNullOrEmpty()) {
            EchoLocateLog.eLogE("Could not save Nr5g processed data to db")

            Nr5gUtils.sendJobCompletedToScheduler(
                androidWorkId,
                Nr5gConstants.REPORT_PROCESSING_COMPONENT_NAME
            )

            return
        }

        /** get number of triggers for every sa5g report(3h) and sent to analytics module */
        if (!sa5gSingleSessionReportEntityList.isNullOrEmpty()) {
            val analyticsEvent = ELAnalyticsEvent(
                ELModulesEnum.SA5G,
                ELAnalyticActions.EL_NUMBER_OF_SESSIONS,
                sa5gSingleSessionReportEntityList.size.toString()
            )
            analyticsEvent.timeStamp = System.currentTimeMillis()

            val postAnalyticsTicket = PostTicket(analyticsEvent)
            RxBus.instance.post(postAnalyticsTicket)
        }

        /** Do not delete data in debug so that it can be tested and data can be viewed.*/
        if (BuildConfig.DEBUG) {
            baseEchoLocateSa5gEntityList.forEach { f -> f.status = STATUS_PROCESSED }
            markRawDataAsProcessed(baseEchoLocateSa5gEntityList)
        } else {
            deleteRawData(baseEchoLocateSa5gEntityList)
            EchoLocateLog.eLogI("Echo nr processed")
        }

        Nr5gUtils.sendJobCompletedToScheduler(
            androidWorkId,
            Nr5gConstants.REPORT_PROCESSING_COMPONENT_NAME
        )
    }

    /**
     * deletes the raw data for production build
     *
     * @param baseEchoLocateSa5gEntityList List of entities that needs to be deleted.
     */
    private fun deleteRawData(
        baseEchoLocateSa5gEntityList: List<BaseEchoLocateSa5gEntity>
    ) {
        sa5gRepository.deleteRawData(baseEchoLocateSa5gEntityList)
    }

    /**
     * This fun saves the entities to database
     * The function doesn't create a new thread and executes the statement in the same thread
     * in which this function is called
     * @param sa5gSingleSessionReportEntityList: List<Sa5gSingleSessionReportEntity>
     */
    private fun saveSa5gReportToDB(sa5gSingleSessionReportEntityList: List<Sa5gSingleSessionReportEntity>): List<Long> {
        return sa5gRepository.insertAllSa5gSingleSessionReportEntity(
            sa5gSingleSessionReportEntityList
        )
    }

    /**
     * This fun updates record in database with new data by using updateAllBaseEchoLocateSa5gEntityStatus
     * using Variable number of arguments (varargs) - getting latest value of data
     */
    private fun markRawDataAsProcessed(baseEchoSa5gEntityList: List<BaseEchoLocateSa5gEntity>) {
        sa5gDao.updateAllBaseEchoLocateSa5gEntityStatus(*baseEchoSa5gEntityList.toTypedArray())
    }

    /**
     * This fun generated final Sa5g report, next it goes through manager to provider
     * Called from Sa5gDataManager
     * Generated Sa5gMultiSessionReport from Sa5gSingleSessionReport
     * status - status of data RAW or PROCESSED
     * Sa5gSingleSessionReportList - List of Sa5gSingleSessionReports is contained by
     * all data object describing Echo locate intents.
     * @return List<Sa5gSingleSessionReport>
     */
    fun getSa5gMultiSessionReport(sa5gSingleSessionReportEntityList: List<Sa5gSingleSessionReportEntity>): List<Sa5gSingleSessionReport> {

        val sa5gSingleSessionReportList = ArrayList<Sa5gSingleSessionReport>()

        for (sa5gSingleSessionReportEntity in sa5gSingleSessionReportEntityList) {
            val gson = Gson()
            val sa5gSingleSessionReport =
                gson.fromJson(
                    sa5gSingleSessionReportEntity.json,
                    Sa5gSingleSessionReport::class.java
                )
            sa5gSingleSessionReportList.add(sa5gSingleSessionReport)
        }
        return sa5gSingleSessionReportList
    }

    /**
     * This fun generated final Sa5g report, next it goes through manager to provider
     * Called from Sa5gDataManager
     * Generated Sa5gMultiSessionReport from Sa5gSingleSessionReport
     * status - status of data RAW or PROCESSED
     * Sa5gSingleSessionReportList - List of Sa5gSingleSessionReports is contained by all data object describing Echo locate intents.
     * @return List<Sa5gSingleSessionReportEntity>
     */
    fun getSa5gMultiSessionReportEntity(
        startTime: Long,
        endTime: Long
    ): List<Sa5gSingleSessionReportEntity> {

        return if (startTime == 0L && endTime == 0L) {
            sa5gDao.getSa5gSingleSessionReportEntityList()
        } else {
            val startTimeStr =
                EchoLocateDateUtils.convertToShemaDateFormat(startTime.toString())
            val endTimeStr = EchoLocateDateUtils.convertToShemaDateFormat(endTime.toString())
            sa5gDao.getNr5gSingleSessionReportEntityList(startTimeStr, endTimeStr)
        }
    }

    fun updateSa5gReportEntity(voiceReportEntity: List<Sa5gSingleSessionReportEntity>) {
        sa5gDao.updateSa5gReportEntityList(*voiceReportEntity.toTypedArray())
    }

    /**
     * Generated list of reports in format of [Sa5gSingleSessionReportEntity] to save it to DB,
     * Params from Sa5gSingleSessionReportEntity:
     *     Sa5gReportId - random generated ID
     *     json - JSON generated from raw data
     *     eventTimestamp - current time of event
     */
    private fun getSa5gSingleSessionReportList(baseEchoSa5gEntityList: List<BaseEchoLocateSa5gEntity>): List<Sa5gSingleSessionReportEntity> {

        val sa5gSingleSessionReportEntityList = ArrayList<Sa5gSingleSessionReportEntity>()
        for (baseEchoSa5gEntity in baseEchoSa5gEntityList) {
            if(getSa5gTrigger(baseEchoSa5gEntity.sessionId)!=null) {
                val sa5gSingleSessionReport = getSa5gSingleSessionReport(baseEchoSa5gEntity)
                val jsonObject = Gson().toJson(sa5gSingleSessionReport)
                val sa5gSingleSessionReportEntity = Sa5gSingleSessionReportEntity(
                    sa5gReportId = UUID.randomUUID().toString(),
                    json = jsonObject,
                    eventTimestamp = EchoLocateDateUtils.convertToShemaDateFormat(
                        System.currentTimeMillis().toString()
                    ),
                    reportStatus = ""
                )

                sa5gSingleSessionReportEntityList.add(sa5gSingleSessionReportEntity)
            }
        }
        return sa5gSingleSessionReportEntityList
    }

    /**
     * Generated [Sa5gSingleSessionReport] in accordance with Schema JSON, data get status RAW
     * Params of each Sa5g Single Session in accordance with [Sa5gSingleSessionReport]
     */
    private fun getSa5gSingleSessionReport(baseEchoSa5gEntity: BaseEchoLocateSa5gEntity): Sa5gSingleSessionReport? {
        val sa5gDataMetricsWrapper =
            Sa5gDataMetricsWrapper(
                context
            )

        return Sa5gSingleSessionReport(
            deviceInfo = getSa5gDeviceInfo(baseEchoSa5gEntity.sessionId),
            location = getSa5gLocation(baseEchoSa5gEntity.sessionId),
            trigger = getSa5gTrigger(baseEchoSa5gEntity.sessionId),
            datametricsVersion = sa5gDataMetricsWrapper.getApiVersion().intCode.toString(),
            getWiFiState = getSa5gWifiState(baseEchoSa5gEntity.sessionId),
            getActiveNetwork = getSa5gActiveNetwork(baseEchoSa5gEntity.sessionId),
            connectedWifiStatus = getSa5gConnectedWifiStatus(baseEchoSa5gEntity.sessionId),
            oemsv = getSa5gOEMSV(baseEchoSa5gEntity.sessionId),
            downlinkCarrierLogs = getSa5gDownlinkCarrierLogs(baseEchoSa5gEntity.sessionId),
            uplinkCarrierLogs = getSa5gUplinkCarrierLogs(baseEchoSa5gEntity.sessionId),
            rrcLog = getSa5gRrcLog(baseEchoSa5gEntity.sessionId),
            networkLog = getSa5gnetworkLog(baseEchoSa5gEntity.sessionId),
            settingsLog = getSa5gSettingsLog(baseEchoSa5gEntity.sessionId),
            uiLog = getSa5gUiLog(baseEchoSa5gEntity.sessionId),
            carrierConfig = getSa5gCarrierConfig(baseEchoSa5gEntity.sessionId)
        )
    }

    /**
     * Generated an object of [Sa5gDeviceInfo]
     * Based on data class [Sa5gDeviceInfo]
     * Entity class Sa5gDeviceInfoEntity
     * @param sessionId: String
     * @return [Sa5gDeviceInfo]
     */
    private fun getSa5gDeviceInfo(sessionId: String): Sa5gDeviceInfo? {
        val sa5gDeviceInfoEntity = sa5gDao.getSa5gDeviceInfoEntity(sessionId)

        return if (sa5gDeviceInfoEntity == null) {
            Sa5gDeviceInfo()
        } else {
            return Sa5gDeviceInfo(
                sa5gDeviceInfoEntity.imei ?: "",
                sa5gDeviceInfoEntity.imsi ?: "",
                sa5gDeviceInfoEntity.msisdn ?: "",
                sa5gDeviceInfoEntity.uuid ?: "",
                sa5gDeviceInfoEntity.testSessionID ?: "",
                    sa5gDeviceInfoEntity.modelCode ?: "",
                    sa5gDeviceInfoEntity.oem ?: ""
            )
        }
    }

    /**
     * Generated an object of [Sa5gLocation]
     * Based on data class [Sa5gLocation]
     * Entity class Sa5gLocationEntity
     * @param sessionId: String
     * @return [Sa5gLocation]
     */

    private fun getSa5gLocation(sessionId: String): Sa5gLocation? {
        val sa5gLocationEntity = sa5gDao.getSa5gLocationEntity(sessionId)

        return if (sa5gLocationEntity == null) {
            null
        } else {
            return Sa5gLocation(
                sa5gLocationEntity.altitude ?: 0.0,
                sa5gLocationEntity.altitudePrecision ?: 0.0F,
                sa5gLocationEntity.latitude ?: 0.0,
                sa5gLocationEntity.longitude ?: 0.0,
                sa5gLocationEntity.precision ?: 0.0F,
                sa5gLocationEntity.timestamp ?: EchoLocateDateUtils.getTriggerTimeStamp(),
                sa5gLocationEntity.locationAge ?: 0
            )
        }
    }

    /**
     * Generated an object of [Sa5gTrigger]
     * Based on data class [Sa5gTrigger]
     * Entity class Sa5gTriggerEntity
     * @param sessionId: String
     * @return [Sa5gTrigger]
     */
    private fun getSa5gTrigger(sessionId: String): Sa5gTrigger? {
        val sa5gTriggerEntity = sa5gDao.getSa5gTriggerEntity(sessionId)

        return if (sa5gTriggerEntity == null) {
            EchoLocateLog.eLogD("sa5gTriggerEntity null")
            null
        } else {
            EchoLocateLog.eLogD("sa5gTriggerEntity triggerId ${sa5gTriggerEntity.triggerId}")
            return Sa5gTrigger(
                sa5gTriggerEntity.timestamp ?: EchoLocateDateUtils.getTriggerTimeStamp(),
                sa5gTriggerEntity.triggerId,
                sa5gTriggerEntity.triggerApp ?: "",
                sa5gTriggerEntity.triggerDelay ?: 0
            )
        }
    }

    /**
     * Generated an object of [Sa5gWiFiState]
     * Based on data class [Sa5gWiFiState]
     * Entity class Sa5gWiFiStateEntity
     * @param sessionId: String
     * @return [Sa5gWiFiState]
     */
    private fun getSa5gWifiState(sessionId: String): Sa5gWiFiState? {
        val sa5gWiFiStateEntity = sa5gDao.getSa5gWiFiStateEntity(sessionId)

        return if (sa5gWiFiStateEntity == null) {
            null
        } else {
            return Sa5gWiFiState(
                sa5gWiFiStateEntity.getWiFiState ?: 4
            )
        }
    }

    /**
     * Generated an object of [Sa5gActiveNetwork]
     * Based on data class [Sa5gActiveNetwork]
     * Entity class Sa5gActiveNetworkEntity
     * @param sessionId: String
     * @return [Sa5gActiveNetwork]
     */

    private fun getSa5gActiveNetwork(sessionId: String): Sa5gActiveNetwork? {
        val sa5gActiveNetworkEntity = sa5gDao.getSa5gActiveNetworkEntity(sessionId)

        return if (sa5gActiveNetworkEntity == null) {
            null
        } else {
            return Sa5gActiveNetwork(
                sa5gActiveNetworkEntity.getActiveNetwork ?: -1
            )
        }
    }

    /**
     * Generated an object of [Sa5gConnectedWifiStatus]
     * Based on data class [Sa5gConnectedWifiStatus]
     * Entity class Sa5gConnectedWifiStatusEntity
     * @param sessionId: String
     * @return [Sa5gConnectedWifiStatus]
     */

    private fun getSa5gConnectedWifiStatus(sessionId: String): Sa5gConnectedWifiStatus? {
        val sa5gConnectedWifiStatusEntity = sa5gDao.getSa5gConnectedWifiStatusEntity(sessionId)

        return if (sa5gConnectedWifiStatusEntity == null) {
            null
        } else {
            return Sa5gConnectedWifiStatus(
                sa5gConnectedWifiStatusEntity.bssId ?: "",
                sa5gConnectedWifiStatusEntity.bssLoad ?: "",
                sa5gConnectedWifiStatusEntity.ssId ?: "",
                sa5gConnectedWifiStatusEntity.accessPointUpTime ?: 0,
                sa5gConnectedWifiStatusEntity.capabilities ?: "",
                sa5gConnectedWifiStatusEntity.centerFreq0 ?: 0,
                sa5gConnectedWifiStatusEntity.centerFreq1 ?: 0,
                sa5gConnectedWifiStatusEntity.channelMode ?: "",
                sa5gConnectedWifiStatusEntity.channelWidth ?: 0,
                sa5gConnectedWifiStatusEntity.frequency ?: 0,
                sa5gConnectedWifiStatusEntity.operatorFriendlyName ?: "",
                sa5gConnectedWifiStatusEntity.passportNetwork ?: 0,
                sa5gConnectedWifiStatusEntity.rssiLevel ?: 0
            )
        }
    }

    /**
     * Generated an object of [Sa5gOEMSV]
     * Based on data class [Sa5gOEMSV]
     * Entity class Sa5gOEMSVEntity
     * @param sessionId: String
     * @return [Sa5gOEMSV]
     */
    private fun getSa5gOEMSV(sessionId: String): Sa5gOEMSV? {
        val sa5gOEMSVEntity = sa5gDao.getSa5gOEMSVEntity(sessionId)

        return if (sa5gOEMSVEntity == null) {
            Sa5gOEMSV()
        } else {
            return Sa5gOEMSV(
                sa5gOEMSVEntity.softwareVersion ?: "",
                sa5gOEMSVEntity.customVersion ?: "",
                sa5gOEMSVEntity.radioVersion ?: "",
                sa5gOEMSVEntity.buildName ?: "unknown",
                sa5gOEMSVEntity.androidVersion ?: "unknown"
            )
        }
    }

    /**
     * Generated an object of [Sa5gDownlinkCarrierLogs]
     * Based on data class [Sa5gDownlinkCarrierLogs]
     * Entity class Sa5gDownlinkCarrierLogsEntity
     * @param sessionId: String
     * @return [Sa5gDownlinkCarrierLogs]
     */

    private fun getSa5gDownlinkCarrierLogs(sessionId: String): List<Sa5gDownlinkCarrierLogs>? {
        val sa5gDownlinkCarrierLogsEntityList = sa5gDao.getSa5gDownlinkCarrierLogsEntity(sessionId)

        val sa5gDownlinkCarrierLogsList = ArrayList<Sa5gDownlinkCarrierLogs>()

        for (sa5gDownlinkCarrierLogsEntity in sa5gDownlinkCarrierLogsEntityList) {
            val sa5gDownlinkCarrierLogs = Sa5gDownlinkCarrierLogs(
                sa5gDownlinkCarrierLogsEntity.techType ?: "",
                sa5gDownlinkCarrierLogsEntity.bandNumber ?: "",
                sa5gDownlinkCarrierLogsEntity.arfcn ?: "",
                sa5gDownlinkCarrierLogsEntity.bandwidth ?: "",
                sa5gDownlinkCarrierLogsEntity.isPrimary ?: "",
                sa5gDownlinkCarrierLogsEntity.isEndcAnchor ?: "",
                sa5gDownlinkCarrierLogsEntity.modulationType ?: "",
                sa5gDownlinkCarrierLogsEntity.transmissionMode ?: "",
                sa5gDownlinkCarrierLogsEntity.numberLayers ?: "",
                sa5gDownlinkCarrierLogsEntity.cellId ?: "",
                sa5gDownlinkCarrierLogsEntity.pci ?: "",
                sa5gDownlinkCarrierLogsEntity.tac ?: "",
                sa5gDownlinkCarrierLogsEntity.lac ?: "",
                sa5gDownlinkCarrierLogsEntity.rsrp ?: "",
                sa5gDownlinkCarrierLogsEntity.rsrq ?: "",
                sa5gDownlinkCarrierLogsEntity.rssi ?: "",
                sa5gDownlinkCarrierLogsEntity.rscp ?: "",
                sa5gDownlinkCarrierLogsEntity.sinr ?: "",
                sa5gDownlinkCarrierLogsEntity.csiRsrp ?: "",
                sa5gDownlinkCarrierLogsEntity.csiRsrq ?: "",
                sa5gDownlinkCarrierLogsEntity.csiRssi ?: "",
                sa5gDownlinkCarrierLogsEntity.csiSinr ?: ""
            )
            sa5gDownlinkCarrierLogsList.add(sa5gDownlinkCarrierLogs)
        }
        return if (sa5gDownlinkCarrierLogsList.isNullOrEmpty()) {
            null
        } else {
            return sa5gDownlinkCarrierLogsList
        }
    }

    /**
     * Generated an object of [Sa5gUplinkCarrierLogs]
     * Based on data class [Sa5gUplinkCarrierLogs]
     * Entity class Sa5gUplinkCarrierLogsEntity
     * @param sessionId: String
     * @return [Sa5gUplinkCarrierLogs]
     */
    private fun getSa5gUplinkCarrierLogs(sessionId: String): List<Sa5gUplinkCarrierLogs>? {
        val sa5gUplinkCarrierLogsEntityList = sa5gDao.getSa5gUplinkCarrierLogsEntity(sessionId)
        val sa5gUplinkCarrierLogsList = ArrayList<Sa5gUplinkCarrierLogs>()
        for (sa5gUplinkCarrierLogsEntity in sa5gUplinkCarrierLogsEntityList) {
            val sa5gUplinkCarrierLogs = Sa5gUplinkCarrierLogs(
                sa5gUplinkCarrierLogsEntity.techType ?: "",
                sa5gUplinkCarrierLogsEntity.bandNumber ?: "",
                sa5gUplinkCarrierLogsEntity.arfcn ?: "",
                sa5gUplinkCarrierLogsEntity.bandwidth ?: "",
                sa5gUplinkCarrierLogsEntity.isPrimary ?: ""
            )
            sa5gUplinkCarrierLogsList.add(sa5gUplinkCarrierLogs)
        }
        return if (sa5gUplinkCarrierLogsList.isNullOrEmpty()) {
            null
        } else {
            return sa5gUplinkCarrierLogsList
        }
    }

    /**
     * Generated an object of [Sa5gRrcLog]
     * Based on data class [Sa5gRrcLog]
     * Entity class Sa5gRrcLogEntity
     * @param sessionId: String
     * @return [Sa5gRrcLog]
     */
    private fun getSa5gRrcLog(sessionId: String): Sa5gRrcLog? {
        val sa5gRrcLogEntity = sa5gDao.getSa5gRrcLogEntity(sessionId)

        return if (sa5gRrcLogEntity == null) {
            null
        } else {
            return Sa5gRrcLog(
                sa5gRrcLogEntity.lteRrcState ?: "",
                sa5gRrcLogEntity.nrRrcState ?: ""
            )
        }
    }

    /**
     * Generated an object of [Sa5gNetworkLog]
     * Based on data class [Sa5gNetworkLog]
     * Entity class Sa5gNetworkLogEntity
     * @param sessionId: String
     * @return [Sa5gNetworkLog]
     */
    private fun getSa5gnetworkLog(sessionId: String): Sa5gNetworkLog? {
        val sa5gNetworkLogEntity = sa5gDao.getSa5gNetworkLogEntity(sessionId)

        return if (sa5gNetworkLogEntity == null) {
            null
        } else {
            return Sa5gNetworkLog(
                sa5gNetworkLogEntity.mcc ?: "",
                sa5gNetworkLogEntity.mnc ?: "",
                sa5gNetworkLogEntity.endcCapability ?: "",
                sa5gNetworkLogEntity.endcConnectionStatus ?: ""
            )
        }
    }

    /**
     * Generated an object of [Sa5gSettingsLog]
     * Based on data class [Sa5gSettingsLog]
     * Entity class Sa5gSettingsLogEntity
     * @param sessionId: String
     * @return [Sa5gSettingsLog]
     */
    private fun getSa5gSettingsLog(sessionId: String): Sa5gSettingsLog? {
        val sa5gSettingsLogEntity = sa5gDao.getSa5gSettingsLogEntity(sessionId)

        return if (sa5gSettingsLogEntity == null) {
            null
        } else {
            return Sa5gSettingsLog(
                sa5gSettingsLogEntity.wifiCalling ?: "",
                sa5gSettingsLogEntity.wifi ?: "",
                sa5gSettingsLogEntity.roaming ?: "",
                sa5gSettingsLogEntity.rtt ?: "",
                sa5gSettingsLogEntity.rttTranscript ?: "",
                sa5gSettingsLogEntity.networkMode ?: "",
                null, //part of carrierConfig data block
                null //part of carrierConfig data block
            )
        }
    }

    /**
     * Generated an object of [Sa5gUiLog]
     * Based on data class [Sa5gUiLog]
     * Entity class Sa5gUiLogEntity
     * @param sessionId: String
     * @return [Sa5gUiLog]
     */

    private fun getSa5gUiLog(sessionId: String): Sa5gUiLog? {
        val sa5gUiLogEntity = sa5gDao.getSa5gUiLogEntity(sessionId)

        return if (sa5gUiLogEntity == null) {
            null
        } else {
            return Sa5gUiLog(
                sa5gUiLogEntity.networkType ?: "",
                EchoLocateDateUtils.convertToShemaDateFormat(sa5gUiLogEntity.timestamp!!)
                    ?: EchoLocateDateUtils.getTriggerTimeStamp(),
                sa5gUiLogEntity.uiNetworkType ?: "",
                sa5gUiLogEntity.uiDataTransmission ?: "",
                sa5gUiLogEntity.uiNumberAntennaBars ?: ""
            )
        }
    }

    /**
     * Generated an object of [Sa5gCarrierConfig]
     * Based on data class [Sa5gCarrierConfig]
     * Entity class sa5gCarrierConfigEntity
     * @param sessionId: String
     * @return [Sa5gCarrierConfig]
     */
    private fun getSa5gCarrierConfig(sessionId: String): Sa5gCarrierConfig? {
        val sa5gCarrierConfigEntity = sa5gDao.getSa5gCarrierConfigEntity(sessionId)
        val delimiter = ","
        val bandConfig = ArrayList<Sa5gCarrierBandConfig>()
        val bandKeys = sa5gCarrierConfigEntity?.bandConfigKeys?.split(delimiter)
        val bandValues = sa5gCarrierConfigEntity?.bandConfigValues?.split(delimiter)

        //bandKeys and bandValues always be equal in size
        if (bandKeys != null && bandValues != null) {
            for ((index, keyValue) in bandKeys.withIndex()) {
                bandConfig.add(Sa5gCarrierBandConfig(keyValue, bandValues[index]))
            }
        }

        return if (sa5gCarrierConfigEntity == null) {
            null
        } else {
            return Sa5gCarrierConfig(
                sa5gCarrierConfigEntity.carrierConfigVersion ?: "",
                bandConfig
            )
        }
    }

    /**
     * This function deletes the the report data from db based on the status
     * @param status
     */
    fun deleteProcessedReports(status: String) {
        sa5gRepository.deleteProcessedReports(status)
    }
}
