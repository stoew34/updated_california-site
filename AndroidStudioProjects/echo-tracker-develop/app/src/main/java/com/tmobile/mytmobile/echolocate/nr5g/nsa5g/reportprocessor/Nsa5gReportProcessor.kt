package com.tmobile.mytmobile.echolocate.nr5g.nsa5g.reportprocessor

import android.content.Context
import android.os.Build
import com.google.gson.Gson
import com.tmobile.mytmobile.echolocate.BuildConfig
import com.tmobile.mytmobile.echolocate.configuration.events.analytics.ELAnalyticActions
import com.tmobile.mytmobile.echolocate.configuration.events.analytics.ELModulesEnum
import com.tmobile.mytmobile.echolocate.communicationbus.rxbus.PostTicket
import com.tmobile.mytmobile.echolocate.communicationbus.rxbus.RxBus
import com.tmobile.mytmobile.echolocate.configuration.events.analytics.ELAnalyticsEvent
import com.tmobile.mytmobile.echolocate.nr5g.core.utils.Nr5gConstants
import com.tmobile.mytmobile.echolocate.nr5g.nsa5g.database.EchoLocateNr5gDatabase
import com.tmobile.mytmobile.echolocate.nr5g.nsa5g.database.dao.Nr5gDao
import com.tmobile.mytmobile.echolocate.nr5g.nsa5g.database.entity.BaseEchoLocateNr5gEntity
import com.tmobile.mytmobile.echolocate.nr5g.nsa5g.database.entity.Nr5gSingleSessionReportEntity
import com.tmobile.mytmobile.echolocate.nr5g.nsa5g.database.repository.Nr5gRepository
import com.tmobile.mytmobile.echolocate.nr5g.nsa5g.model.*
import com.tmobile.mytmobile.echolocate.nr5g.nsa5g.reportprocessor.Nsa5gDataStatus.Companion.STATUS_PROCESSED
import com.tmobile.mytmobile.echolocate.nr5g.nsa5g.reportprocessor.Nsa5gDataStatus.Companion.STATUS_RAW
import com.tmobile.mytmobile.echolocate.utils.EchoLocateDateUtils
import com.tmobile.mytmobile.echolocate.utils.EchoLocateLog
import com.tmobile.mytmobile.echolocate.nr5g.core.utils.Nr5gUtils
import com.tmobile.mytmobile.echolocate.utils.SingletonHolder
import java.util.*
import kotlin.collections.ArrayList

/**
 * Generated reports from raw data collected from OEM intents into nr5g reports.
 * Generate JSON file from DataBase
 */


class Nsa5gReportProcessor private constructor(val context: Context) {

    // Only single instance of report processor is needed to avoid report/data duplication
    companion object : SingletonHolder<Nsa5gReportProcessor, Context>(::Nsa5gReportProcessor)

    private val nr5gDao: Nr5gDao =
        EchoLocateNr5gDatabase.getEchoLocateNr5gDatabase(context).nr5gDao()
    var nr5gRepository = Nr5gRepository(context)
    val schemaVersion = "1"
    var status = ""

    /**
     * Process and save raw data to database with status PROCESSED
     * generate new report list for all new record(RAW status)
     * [saveNr5gReportToDB] - save new generated reports to database with status RAW
     * [forEach{f -> f] - once proceed -> changed the status of data to PROCESSED
     * [markRawDataAsProcessed] - update status in database
     * [deleteRawData]-deletes the raw data from database
     */
    @Synchronized
    fun processRawData(androidWorkId: String? = null) {

        val baseEchoLocateNr5gEntityList = nr5gDao.getBaseEchoLocateNr5gEntityByStatus(STATUS_RAW)

        val nr5gSingleSessionReportEntityList =
            getNr5gSingleSessionReportList(baseEchoLocateNr5gEntityList)

        val savedList = saveNr5gReportToDB(nr5gSingleSessionReportEntityList)

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

        /** get number of triggers for every nr5g report(6h) and sent to analytics module*/
        if (!nr5gSingleSessionReportEntityList.isNullOrEmpty()) {
            val analyticsEvent = ELAnalyticsEvent(
                ELModulesEnum.NR5G,
                ELAnalyticActions.EL_NUMBER_OF_SESSIONS,
                nr5gSingleSessionReportEntityList.size.toString()
            )
            analyticsEvent.timeStamp = System.currentTimeMillis()

            val postAnalyticsTicket = PostTicket(analyticsEvent)
            RxBus.instance.post(postAnalyticsTicket)
        }

        /** Do not delete data in debug so that it can be tested and data can be viewed.*/
        if (BuildConfig.DEBUG) {
            baseEchoLocateNr5gEntityList.forEach { f -> f.status = STATUS_PROCESSED }
            markRawDataAsProcessed(baseEchoLocateNr5gEntityList)
        } else {
            deleteRawData(baseEchoLocateNr5gEntityList)
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
     * @param baseEchoLocateNr5gEntityList List of entities that needs to be deleted.
     */
    private fun deleteRawData(
        baseEchoLocateNr5gEntityList: List<BaseEchoLocateNr5gEntity>
    ) {
        nr5gRepository.deleteRawData(baseEchoLocateNr5gEntityList)
    }

    /**
     * This fun saves the entities to database
     * The function doesn't create a new thread and executes the statement in the same thread
     * in which this function is called
     * @param nr5gSingleSessionReportEntityList: List<Nr5gSingleSessionReportEntity>
     */
    private fun saveNr5gReportToDB(
        nr5gSingleSessionReportEntityList: List<Nr5gSingleSessionReportEntity>
    ): List<Long> {
        return nr5gRepository.insertAllNr5gSingleSessionReportEntity(
            nr5gSingleSessionReportEntityList
        )
    }

    /**
     * This fun updates record in database with new data by using updateAllBaseEchoLocateNr5gEntityStatus
     * using Variable number of arguments (varargs) - getting latest value of data
     */
    private fun markRawDataAsProcessed(baseEchoLocateNr5gEntityList: List<BaseEchoLocateNr5gEntity>) {
        nr5gDao.updateAllBaseEchoLocateNr5gEntityStatus(*baseEchoLocateNr5gEntityList.toTypedArray())
    }

    /**
     * This fun generated final Nr5g report, next it goes through manager to provider
     * Called from Base5gDataManager
     * Generated Nr5gMultiSessionReport from Nr5gSingleSessionReport
     * status - status of data RAW or PROCESSED
     * Nr5gSingleSessionReportList - List of Nr5gSingleSessionReports is contained by all data object describing Echo locate intents.
     * @return List<Nr5gSingleSessionReport>
     */
    fun getNr5gMultiSessionReport(nr5gSingleSessionReportEntityList: List<Nr5gSingleSessionReportEntity>): List<Nr5gSingleSessionReport> {

        val nr5gSingleSessionReportList = ArrayList<Nr5gSingleSessionReport>()

        for (nr5gSingleSessionReportEntity in nr5gSingleSessionReportEntityList) {
            val gson = Gson()
            val nr5gSingleSessionReport =
                gson.fromJson(
                    nr5gSingleSessionReportEntity.json,
                    Nr5gSingleSessionReport::class.java
                )
            nr5gSingleSessionReportList.add(nr5gSingleSessionReport)
        }
        return nr5gSingleSessionReportList
    }


    /**
     * This fun generated final Nr5g report, next it goes through manager to provider
     * Called from Base5gDataManager
     * Generated Nr5gMultiSessionReport from Nr5gSingleSessionReport
     * status - status of data RAW or PROCESSED
     * Nr5gSingleSessionReportList - List of Nr5gSingleSessionReports is contained by all data object describing Echo locate intents.
     * @return List<Nr5gSingleSessionReportEntity>
     */
    fun getNr5gMultiSessionReportEntity(
        startTime: Long,
        endTime: Long
    ): List<Nr5gSingleSessionReportEntity> {

        return if (startTime == 0L && endTime == 0L) {
            nr5gDao.getNr5gSingleSessionReportEntityList()
        } else {
            val startTimeStr =
                EchoLocateDateUtils.convertToShemaDateFormat(startTime.toString())
            val endTimeStr = EchoLocateDateUtils.convertToShemaDateFormat(endTime.toString())
            nr5gDao.getNr5gSingleSessionReportEntityList(startTimeStr, endTimeStr)
        }
    }

    fun updateNr5gReportEntity(voiceReportEntity: List<Nr5gSingleSessionReportEntity>) {
        nr5gDao.updateNr5gReportEntityList(*voiceReportEntity.toTypedArray())
    }

    /**
     * Generated list of reports in format of [Nr5gSingleSessionReportEntity] to save it to DB,
     * Params from Nr5gSingleSessionReportEntity:
     * Nr5gReportId - random generated ID
     * json - JSON generated from raw data
     * eventTimestamp - current time of event
     */
    private fun getNr5gSingleSessionReportList(baseEchoLocateNr5gEntityList: List<BaseEchoLocateNr5gEntity>): List<Nr5gSingleSessionReportEntity> {

        val nr5gSingleSessionReportEntityList = ArrayList<Nr5gSingleSessionReportEntity>()
        for (baseEchoLocateNr5gEntity in baseEchoLocateNr5gEntityList) {
            if(getNr5gTrigger(baseEchoLocateNr5gEntity.sessionId)!=null) {
                val nr5gSingleSessionReport = getNr5gSingleSessionReport(baseEchoLocateNr5gEntity)
                val jsonObject = Gson().toJson(nr5gSingleSessionReport)
                val nr5gSingleSessionReportEntity = Nr5gSingleSessionReportEntity(
                    UUID.randomUUID().toString(),
                    jsonObject,
                    EchoLocateDateUtils.convertToShemaDateFormat(
                        System.currentTimeMillis().toString()
                    ),
                    ""
                )

                nr5gSingleSessionReportEntityList.add(nr5gSingleSessionReportEntity)
            }
        }
        return nr5gSingleSessionReportEntityList
    }

    /**
     * Generated [Nr5gSingleSessionReport] in accordance which Schema JSON, data get status RAW
     * Params of each Nr5g Single Session in accordance with [Nr5gSingleSessionReport]
     */
    private fun getNr5gSingleSessionReport(baseEchoLocateNr5gEntity: BaseEchoLocateNr5gEntity): Nr5gSingleSessionReport {

        return Nr5gSingleSessionReport(
            deviceInfo = getNr5gDeviceInfo(baseEchoLocateNr5gEntity.sessionId),
            location = getNr5gLocation(baseEchoLocateNr5gEntity.sessionId),
            connectedWifiStatus = getConnectedWifiStatus(baseEchoLocateNr5gEntity.sessionId),
            oemsv = getNr5gOEMSV(baseEchoLocateNr5gEntity.sessionId),
            endcLteLog = getEndcLteLog(baseEchoLocateNr5gEntity.sessionId),
            nr5GMmwCellLog = getNr5gMmwCellLog(baseEchoLocateNr5gEntity.sessionId),
            endcUplinkLog = getEndcUplinkLog(baseEchoLocateNr5gEntity.sessionId),
            nr5gUiLog = getNr5gUiLog(baseEchoLocateNr5gEntity.sessionId),
            nr5gStatus = getNr5gStatus(baseEchoLocateNr5gEntity.sessionId),
            nr5gNetworkIdentity = getNr5gNetworkIdentity(baseEchoLocateNr5gEntity.sessionId),
            nr5gDataNetworkType = getNr5gDataNetworkType(baseEchoLocateNr5gEntity.sessionId),
            nr5gTrigger = getNr5gTrigger(baseEchoLocateNr5gEntity.sessionId),
            nr5gWiFiState = getNr5gWiFiState(baseEchoLocateNr5gEntity.sessionId),
            nr5gActiveNetwork = getNr5gActiveNetwork(baseEchoLocateNr5gEntity.sessionId)
        )
    }

    /**
     * Generated an object of [Nr5gDeviceInfo]
     * Based on data class [Nr5gDeviceInfo]
     * Entity class Nr5gDeviceInfoEntity
     * Data from DeviceInfo class
     * @param sessionId: String
     * @return object of [Nr5gDeviceInfo]
     */
    private fun getNr5gDeviceInfo(sessionId: String): Nr5gDeviceInfo? {
        val nr5gDeviceInfoEntity = nr5gDao.getNr5gDeviceInfoEntity(sessionId)

        return if (nr5gDeviceInfoEntity == null) {
            Nr5gDeviceInfo("", "", "", "", "",  Build.MODEL, Build.MANUFACTURER)
        } else {
            return Nr5gDeviceInfo(
                    nr5gDeviceInfoEntity.imei,
                    nr5gDeviceInfoEntity.imsi,
                    nr5gDeviceInfoEntity.msisdn,
                    nr5gDeviceInfoEntity.uuid,
                    nr5gDeviceInfoEntity.testSessionID,
                    nr5gDeviceInfoEntity.modelCode,
                    nr5gDeviceInfoEntity.oem
            )
        }
    }

    /**
     * Generated an object of [Nr5gLocation]
     * Based on data class [Nr5gLocation]
     * Entity class Nr5gLocationEntity
     * Data from Location class
     * @param sessionId: String
     * @return object of [Nr5gLocation]
     */
    private fun getNr5gLocation(sessionId: String): Nr5gLocation? {
        val nr5gLocationEntity = nr5gDao.getNr5gLocationEntity(sessionId)

        return if (nr5gLocationEntity == null) {
            null
        } else {
            return Nr5gLocation(
                nr5gLocationEntity.altitude,
                nr5gLocationEntity.altitudePrecision,
                nr5gLocationEntity.latitude,
                nr5gLocationEntity.longitude,
                nr5gLocationEntity.precision,
                nr5gLocationEntity.timestamp,
                nr5gLocationEntity.locationAge
            )
        }
    }

    /**
     * Generated an object of [ConnectedWifiStatus]
     * Based on data class [ConnectedWifiStatus]
     * Entity class ConnectedWifiStatusEntity
     * Data from DataCollectorConnectedWifiStatus class
     * @param sessionId: String
     * @return object of [ConnectedWifiStatus]
     */
    private fun getConnectedWifiStatus(sessionId: String): ConnectedWifiStatus? {
        val connectedWifiStatusEntity = nr5gDao.getConnectedWifiStatusEntity(sessionId)

        return if (connectedWifiStatusEntity == null) {
            null
        } else {
            return ConnectedWifiStatus(
                connectedWifiStatusEntity.bssId,
                connectedWifiStatusEntity.bssLoad,
                connectedWifiStatusEntity.ssId,
                connectedWifiStatusEntity.accessPointUpTime,
                connectedWifiStatusEntity.capabilities,
                connectedWifiStatusEntity.centerFreq0,
                connectedWifiStatusEntity.centerFreq1,
                connectedWifiStatusEntity.channelMode,
                connectedWifiStatusEntity.channelWidth,
                connectedWifiStatusEntity.frequency,
                connectedWifiStatusEntity.operatorFriendlyName,
                connectedWifiStatusEntity.passportNetwork,
                connectedWifiStatusEntity.rssiLevel
            )
        }
    }

    /**
     * Generated an object of [Nr5gOEMSV]
     * Based on data class [Nr5gOEMSV]
     * Entity class Nr5gOEMSVEntity
     * Data from OEMSV class
     * @param sessionId: String
     * @return object of [Nr5gOEMSV]
     */
    private fun getNr5gOEMSV(sessionId: String): Nr5gOEMSV? {
        val nr5gOEMSVEntity = nr5gDao.getNr5gOEMSVEntity(sessionId)

        return if (nr5gOEMSVEntity == null) {
            null
        } else {
            return Nr5gOEMSV(
                nr5gOEMSVEntity.softwareVersion,
                nr5gOEMSVEntity.customVersion,
                nr5gOEMSVEntity.radioVersion,
                nr5gOEMSVEntity.buildName,
                nr5gOEMSVEntity.androidVersion
            )
        }
    }

    /**
     * Generated an object of [EndcLteLog]
     * Based on data class [EndcLteLog]
     * Entity class EndcLteLogEntity
     * Data from Nr5gBaseDataMetricsWrapper class
     * @param sessionId: String
     * @return object of [EndcLteLog]
     */
    private fun getEndcLteLog(sessionId: String): EndcLteLog? {
        val endcLteLogEntity = nr5gDao.getEndcLteLogEntity(sessionId)

        return if (endcLteLogEntity == null) {
            null
        } else {
            return EndcLteLog(
                endcLteLogEntity.timestamp,
                endcLteLogEntity.networkType,
                endcLteLogEntity.anchorLteCid,
                endcLteLogEntity.anchorLtePci,
                endcLteLogEntity.endcCapability,
                endcLteLogEntity.lteRrcState
            )
        }
    }

    /**
     * Generated an object of [EndcUplinkLog]
     * Based on data class [EndcUplinkLog]
     * Entity class EndcUpLinkLogEntity
     * Data from Nr5gBaseDataMetricsWrapper class
     * @param sessionId: String
     * @return object of [EndcUplinkLog]
     */
    private fun getEndcUplinkLog(sessionId: String): EndcUplinkLog? {
        val endcUpLinkLogEntity = nr5gDao.getEndcUplinkLogEntity(sessionId)

        return if (endcUpLinkLogEntity == null) {
            null
        } else {
            return EndcUplinkLog(
                endcUpLinkLogEntity.timestamp,
                endcUpLinkLogEntity.networkType,
                endcUpLinkLogEntity.uplinkNetwork
            )
        }
    }

    /**
     * Generated an object of [Nr5gUiLog]
     * Based on data class [Nr5gUiLog]
     * Entity class Nr5gUiLogEntity
     * Data from Nr5gBaseDataMetricsWrapper class
     * @param sessionId: String
     * @return object of [Nr5gUiLog]
     */
    private fun getNr5gUiLog(sessionId: String): Nr5gUiLog? {
        val nr5gUiLogEntity = nr5gDao.getNr5gUiLogEntity(sessionId)

        return if (nr5gUiLogEntity == null) {
            null
        } else {
            return Nr5gUiLog(
                nr5gUiLogEntity.timestamp,
                nr5gUiLogEntity.networkType,
                nr5gUiLogEntity.uiNetworkType,
                nr5gUiLogEntity.uiDataTransmission,
                nr5gUiLogEntity.uiNumberOfAntennaBars,
                nr5gUiLogEntity.ui5gConfigurationStatus
            )
        }
    }

    /**
     * Generated an object of [Nr5gMmwCellLog]
     * Based on data class [Nr5gMmwCellLog]
     * Entity class Nr5gMmwCellLogEntity
     * Data from Nr5gBaseDataMetricsWrapper class
     * @param sessionId: String
     * @return object of [Nr5gMmwCellLog]
     */
    private fun getNr5gMmwCellLog(sessionId: String): Nr5gMmwCellLog? {
        val nr5gMmwCellLogEntity = nr5gDao.getNr5gMmwCellLogEntity(sessionId)

        return if (nr5gMmwCellLogEntity == null) {
            null
        } else {
            return Nr5gMmwCellLog(
                nr5gMmwCellLogEntity.timestamp,
                nr5gMmwCellLogEntity.networkType,
                nr5gMmwCellLogEntity.nrPscellPci,
                nr5gMmwCellLogEntity.ssbBeamIndex,
                nr5gMmwCellLogEntity.ssbBrsrp,
                nr5gMmwCellLogEntity.ssbBrsrq,
                nr5gMmwCellLogEntity.ssbSnr,
                nr5gMmwCellLogEntity.pdschBeamIndex,
                nr5gMmwCellLogEntity.pdschBrsrp,
                nr5gMmwCellLogEntity.pdschBrsrq,
                nr5gMmwCellLogEntity.pdschSnr,
                nr5gMmwCellLogEntity.nrBandName,
                nr5gMmwCellLogEntity.nrBandwidth,
                nr5gMmwCellLogEntity.numberOfSsBBeams
            )
        }
    }

    /**
     * Generated an object of [Nr5gStatus]
     * Based on data class [Nr5gStatus]
     * Entity class Nr5gStatusEntity
     * Data from Data Collector
     * @param sessionId: String
     * @return object of [Nr5gStatus]
     */
    private fun getNr5gStatus(sessionId: String): Nr5gStatus? {
        val nr5gStatusEntity = nr5gDao.getNr5gStatusEntity(sessionId)

        return if (nr5gStatusEntity == null) {
            null
        } else {
            return Nr5gStatus(
                nr5gStatusEntity.getNrStatus
            )
        }
    }

    /**
     * Generated an object of [Nr5gNetworkIdentity]
     * Based on data class [Nr5gNetworkIdentity]
     * Entity class Nr5gNetworkIdentityEntity
     * Data from Nr5gBaseDataMetricsWrapper class
     * @param sessionId: String
     * @return object of [Nr5gNetworkIdentity]
     */
    private fun getNr5gNetworkIdentity(sessionId: String): Nr5gNetworkIdentity? {
        val nr5gNetworkIdentityEntity = nr5gDao.getNr5gNetworkIdentityEntity(sessionId)

        return if (nr5gNetworkIdentityEntity == null) {
            null
        } else {
            return Nr5gNetworkIdentity(
                nr5gNetworkIdentityEntity.timestamp,
                nr5gNetworkIdentityEntity.networkType,
                nr5gNetworkIdentityEntity.mcc,
                nr5gNetworkIdentityEntity.mnc,
                nr5gNetworkIdentityEntity.primaryCid,
                nr5gNetworkIdentityEntity.primaryPci,
                nr5gNetworkIdentityEntity.secondaryPci,
                nr5gNetworkIdentityEntity.thirdPci
            )
        }
    }

    /**
     * Generated an object of [Nr5gDataNetworkType]
     * Based on data class [Nr5gDataNetworkType]
     * Entity class Nr5gDataNetworkTypeEntity
     * Data from Data Collector
     * @param sessionId: String
     * @return object of [Nr5gDataNetworkType]
     */
    private fun getNr5gDataNetworkType(sessionId: String): Nr5gDataNetworkType? {
        val nr5gDataNetworkTypeEntity = nr5gDao.getNr5gDataNetworkTypeEntity(sessionId)

        return if (nr5gDataNetworkTypeEntity == null) {
            null
        } else {
            return Nr5gDataNetworkType(
                nr5gDataNetworkTypeEntity.timestamp,
                nr5gDataNetworkTypeEntity.getDataNetworkType
            )
        }
    }

    /**
     * Generated an object of [Nr5gTrigger]
     * Based on data class [Nr5gTrigger]
     * Entity class Nr5gDataNetworkTypeEntity
     * Data from Base Delegate
     * @param sessionId: String
     * @return object of [Nr5gTrigger]
     */
    private fun getNr5gTrigger(sessionId: String): Nr5gTrigger? {
        val nr5gTriggerEntity = nr5gDao.getNr5gTriggerEntity(sessionId)

        return if (nr5gTriggerEntity == null) {
            EchoLocateLog.eLogD("nr5gTriggerEntity null")
            null
        } else {
            EchoLocateLog.eLogD("nr5gTriggerEntity triggerId ${nr5gTriggerEntity.triggerId}")
            return Nr5gTrigger(
                nr5gTriggerEntity.timestamp,
                nr5gTriggerEntity.triggerId,
                nr5gTriggerEntity.triggerApp,
                nr5gTriggerEntity.triggerDelay
            )
        }
    }

    /**
     * Generated an object of [Nr5gWiFiState]
     * Based on data class [Nr5gWiFiState]
     * Entity class Nr5gWifiStateEntity
     * Data from Data Collector
     * @param sessionId: String
     * @return object of [Nr5gWiFiState]
     */
    private fun getNr5gWiFiState(sessionId: String): Nr5gWiFiState? {
        val nr5gWifiStateEntity = nr5gDao.getNr5gWifiStateEntity(sessionId)

        return if (nr5gWifiStateEntity == null) {
            null
        } else {
            return Nr5gWiFiState(
                nr5gWifiStateEntity.getWiFiState
            )
        }
    }

    /**
     * Generated an object of [Nr5gActiveNetwork]
     * Based on data class [Nr5gActiveNetwork]
     * Entity class Nr5gActiveNetworkEntity
     * Data from Data Collector
     * @param sessionId: String
     * @return object of [Nr5gActiveNetwork]
     */
    private fun getNr5gActiveNetwork(sessionId: String): Nr5gActiveNetwork? {
        val nr5gActiveNetworkEntity = nr5gDao.getNr5gActiveNetworkEntity(sessionId)

        return if (nr5gActiveNetworkEntity == null) {
            null
        } else {
            return Nr5gActiveNetwork(
                nr5gActiveNetworkEntity.getActiveNetwork
            )
        }
    }

    /**
     * This function deletes the the report data from db based on the status
     * @param status
     */
    fun deleteProcessedReports(status: String) {
        nr5gRepository.deleteProcessedReports(status)
    }
}