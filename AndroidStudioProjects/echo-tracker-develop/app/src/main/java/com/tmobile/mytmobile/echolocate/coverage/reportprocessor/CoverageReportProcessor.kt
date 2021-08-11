package com.tmobile.mytmobile.echolocate.coverage.reportprocessor

import android.content.Context
import com.google.gson.Gson
import com.tmobile.mytmobile.echolocate.BuildConfig
import com.tmobile.mytmobile.echolocate.configuration.events.analytics.ELAnalyticActions
import com.tmobile.mytmobile.echolocate.configuration.events.analytics.ELAnalyticsEvent
import com.tmobile.mytmobile.echolocate.configuration.events.analytics.ELModulesEnum
import com.tmobile.mytmobile.echolocate.communicationbus.rxbus.PostTicket
import com.tmobile.mytmobile.echolocate.communicationbus.rxbus.RxBus
import com.tmobile.mytmobile.echolocate.coverage.database.EchoLocateCoverageDatabase
import com.tmobile.mytmobile.echolocate.coverage.database.dao.CoverageDao
import com.tmobile.mytmobile.echolocate.coverage.database.entity.BaseEchoLocateCoverageEntity
import com.tmobile.mytmobile.echolocate.coverage.database.entity.CoverageSingleSessionReportEntity
import com.tmobile.mytmobile.echolocate.coverage.database.repository.CoverageRepository
import com.tmobile.mytmobile.echolocate.coverage.model.*
import com.tmobile.mytmobile.echolocate.coverage.reportprocessor.CoverageDataStatus.Companion.STATUS_PROCESSED
import com.tmobile.mytmobile.echolocate.coverage.reportprocessor.CoverageDataStatus.Companion.STATUS_RAW
import com.tmobile.mytmobile.echolocate.coverage.utils.CoverageConstants
import com.tmobile.mytmobile.echolocate.utils.EchoLocateDateUtils
import com.tmobile.mytmobile.echolocate.utils.EchoLocateLog
import com.tmobile.mytmobile.echolocate.coverage.utils.CoverageUtils
import com.tmobile.mytmobile.echolocate.utils.SingletonHolder
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by Mahesh Shetye on 2020-05-20
 *
 * For processing reports for coverage module
 * Generate reports from raw data collected in coverage database into reports.
 * Generates JSON file from DataBase
 *
 */

class CoverageReportProcessor private constructor(val context: Context) {

    // Only single instance of report processor is needed to avoid report/data duplication
    companion object : SingletonHolder<CoverageReportProcessor, Context>(::CoverageReportProcessor)

    private val coverageDao: CoverageDao = EchoLocateCoverageDatabase.getEchoLocateCoverageDatabase(context).coverageDao()
    var coverageRepository = CoverageRepository(context)
    val schemaVersion = "1"
    var status = ""

    /**
     * Process and save raw data to database with status PROCESSED
     * generate new report list for all new record(RAW status)
     * [saveCoverageReportToDB] - save new generated reports to database with status RAW
     * [forEach{f -> f] - once proceed -> changed the status of data to PROCESSED
     * [markRawDataAsProcessed] - update status in database
     * [deleteRawData]-deletes the raw data from database
     */
    @Synchronized
    fun processRawData(androidWorkId: String? = null) {

        val baseEchoCoverageEntityList = coverageDao.getBaseEchoLocateCoverageEntityByStatus(STATUS_RAW)

        val coverageSingleSessionReportEntityList =
            getCoverageSingleSessionReportList(baseEchoCoverageEntityList)

        val savedList = saveCoverageReportToDB(coverageSingleSessionReportEntityList)

        /** If the processed data is not saved into the database,
         *  then do not delete the raw data or do not mark it as processed to avoid data loss.*/
        if (savedList.isNullOrEmpty()) {
            EchoLocateLog.eLogE("Could not save Coverage processed data to db")

            CoverageUtils.sendJobCompletedToScheduler(androidWorkId, CoverageConstants.REPORT_GENERATOR_COMPONENT_NAME)

            return
        }

        /** get number of triggers for every coverage report(6h) and sent to analytics module */
        if (!coverageSingleSessionReportEntityList.isNullOrEmpty()) {
            val analyticsEvent = ELAnalyticsEvent(
                ELModulesEnum.COVERAGE,
                ELAnalyticActions.EL_NUMBER_OF_SESSIONS,
                coverageSingleSessionReportEntityList.size.toString()
            )
            analyticsEvent.timeStamp = System.currentTimeMillis()

            val postAnalyticsTicket = PostTicket(analyticsEvent)
            RxBus.instance.post(postAnalyticsTicket)
        }

        /** Do not delete data in debug so that it can be tested and data can be viewed.*/
        if (BuildConfig.DEBUG) {
            baseEchoCoverageEntityList.forEach { f -> f.status = STATUS_PROCESSED }
            markRawDataAsProcessed(baseEchoCoverageEntityList)
        } else {
            deleteRawData(baseEchoCoverageEntityList)
        }

        CoverageUtils.sendJobCompletedToScheduler(androidWorkId, CoverageConstants.REPORT_GENERATOR_COMPONENT_NAME)
    }

    /**
     * This fun generated final Coverage report, next it goes through manager to provider
     * Called from CoverageDataManager
     * Generated CoverageMultiSessionReport from CoverageSingleSessionReport
     * status - status of data RAW or PROCESSED
     * CoverageSingleSessionReportList - List of CoverageSingleSessionReports is contained by all data object describing Echo locate intents.
     * @return List<CoverageSingleSessionReport>
     */
    fun getCoverageMultiSessionReport(coverageSingleSessionReportEntityList: List<CoverageSingleSessionReportEntity>): List<CoverageSingleSessionReport> {

        val coverageSingleSessionReportList = ArrayList<CoverageSingleSessionReport>()

        for (coverageSingleSessionReportEntity in coverageSingleSessionReportEntityList) {
            val gson = Gson()
            val coverageSingleSessionReport =
                gson.fromJson(coverageSingleSessionReportEntity.json, CoverageSingleSessionReport::class.java)
            coverageSingleSessionReportList.add(coverageSingleSessionReport)
        }
        return coverageSingleSessionReportList
    }


    /**
     * This fun generated final Coverage report, next it goes through manager to provider
     * Called from CoverageDataManager
     * Generated CoverageMultiSessionReport from CoverageSingleSessionReport
     * status - status of data RAW or PROCESSED
     * CoverageSingleSessionReportList - List of CoverageSingleSessionReports is contained by all data object describing Echo locate intents.
     * @return List<CoverageSingleSessionReportEntity>
     */
    fun getCoverageMultiSessionReportEntity(): List<CoverageSingleSessionReportEntity> {

        return coverageDao.getCoverageSingleSessionReportEntityList()
    }

    fun updateCoverageReportEntity(coverageReportEntity: List<CoverageSingleSessionReportEntity>) {
        coverageDao.updateCoverageReportEntityList(*coverageReportEntity.toTypedArray())
    }

    /**
     * Generated list of reports in format of [CoverageSingleSessionReportEntity] to save it to DB,
     * Params from CoverageSingleSessionReportEntity:
     *     CoverageReportId - random generated ID
     *     json - JSON generated from raw data
     *     eventTimestamp - current time of event
     */
    private fun getCoverageSingleSessionReportList(baseEchoLocateCoverageEntityList: List<BaseEchoLocateCoverageEntity>): List<CoverageSingleSessionReportEntity> {

        val coverageSingleSessionReportEntityList = ArrayList<CoverageSingleSessionReportEntity>()
        for (baseEchoLocateCoverageEntity in baseEchoLocateCoverageEntityList) {
            val coverageSingleSessionReport = getCoverageSingleSessionReport(baseEchoLocateCoverageEntity)
            val jsonObject = Gson().toJson(coverageSingleSessionReport)
            val coverageSingleSessionReportEntity = CoverageSingleSessionReportEntity(
                coverageReportId = UUID.randomUUID().toString(),
                json = jsonObject,
                eventTimestamp = EchoLocateDateUtils.convertToShemaDateFormat(System.currentTimeMillis().toString()),
                reportStatus = ""
            )

            coverageSingleSessionReportEntityList.add(coverageSingleSessionReportEntity)
        }
        return coverageSingleSessionReportEntityList
    }

    /**
     * Generated [CoverageSingleSessionReport] in accordance with Schema JSON, data get status RAW
     * Params of each Coverage Single Session in accordance with [CoverageSingleSessionReport]
     */
    private fun getCoverageSingleSessionReport(baseEchoLocateCoverageEntity: BaseEchoLocateCoverageEntity): CoverageSingleSessionReport {

        return CoverageSingleSessionReport(
            schemaVersion = baseEchoLocateCoverageEntity.schemaVersion,
            environment = getCoverageEnvironment(baseEchoLocateCoverageEntity.sessionId),
            timestamp = baseEchoLocateCoverageEntity.timestamp,
            trigger = baseEchoLocateCoverageEntity.trigger
        )
    }

    /**
     * Generated an object of [CoverageEnvironment]
     * Based on data class [CoverageEnvironment]
     * Entity class CoverageEnvironmentEntity
     * @param sessionId: String
     * @return [CoverageEnvironment]
     */
    private fun getCoverageEnvironment(sessionId: String): CoverageEnvironment {

        return CoverageEnvironment(
            settings = getCoverageSettings(sessionId),
            oemsv = getCoverageOEMSV(sessionId),
            location = getCoverageLocation(sessionId),
            net = getCoverageNet(sessionId),
            telephony = getCoverageTelephony(sessionId)
        )
    }

    /**
     * Generated an object of [CoverageSettings]
     * Based on data class [CoverageSettings]
     * Entity class CoverageSettingsEntity
     * @param sessionId: String
     * @return [CoverageSettings]
     */
    private fun getCoverageSettings(sessionId: String): CoverageSettings? {
        val coverageSettingsEntity = coverageDao.getCoverageSettingsEntityBySessionID(sessionId)

        return if (coverageSettingsEntity == null) {
            null
        } else {
            CoverageSettings(
                volteState = coverageSettingsEntity.volteState,
                dataRoamingEnabled = coverageSettingsEntity.dataRoamingEnabled
            )
        }
    }

    /**
     * Generated an object of [CoverageOEMSV]
     * Based on data class [CoverageOEMSV]
     * Entity class CoverageOEMSVEntity
     * @param sessionId: String
     * @return [CoverageOEMSV]
     */
    private fun getCoverageOEMSV(sessionId: String): CoverageOEMSV {
        val coverageOEMSVEntity = coverageDao.getCoverageOEMSVEntityBySessionID(sessionId)

        return if (coverageOEMSVEntity == null) {
            CoverageOEMSV()
        } else {
            CoverageOEMSV(
                androidVersion = coverageOEMSVEntity.androidVersion ?: "",
                buildName = coverageOEMSVEntity.buildName ?: "",
                customVersion = coverageOEMSVEntity.customVersion,
                radioVersion = coverageOEMSVEntity.radioVersion ?: "",
                sv = coverageOEMSVEntity.sv ?: ""
            )
        }
    }

    /**
     * Generated an object of [CoverageLocation]
     * Based on data class [CoverageLocation]
     * Entity class CoverageLocationEntity
     * @param sessionId: String
     * @return [CoverageLocation]
     */
    private fun getCoverageLocation(sessionId: String): CoverageLocation? {
        val coverageLocationEntity = coverageDao.getCoverageLocationEntityBySessionID(sessionId)

        return if (coverageLocationEntity == null) {
            null
        } else {
            CoverageLocation(
                latitude = coverageLocationEntity.latitude,
                longitude = coverageLocationEntity.longitude,
                accuracy = coverageLocationEntity.accuracy,
                altitude = coverageLocationEntity.altitude,
                bearing = coverageLocationEntity.bearing,
                bearingAccuracy = coverageLocationEntity.bearingAccuracy,
                activityType = coverageLocationEntity.activityType,
                activityConfidence = coverageLocationEntity.speed,
                speed = coverageLocationEntity.speed,
                provider = coverageLocationEntity.provider,
                locationAge = coverageLocationEntity.locationAge,
                speedAccuracyMetersPerSecond = coverageLocationEntity.speedAccuracyMetersPerSecond,
                verticalAccuracyMeters = coverageLocationEntity.verticalAccuracyMeters,
                locationStatus = coverageLocationEntity.locationStatus,
                timestamp = coverageLocationEntity.timestamp
            )
        }
    }

    /**
     * Generated an object of [CoverageNet]
     * Based on data class [CoverageNet]
     * Entity class CoverageNetEntity
     * @param sessionId: String
     * @return [CoverageNet]
     */
    private fun getCoverageNet(sessionId: String): CoverageNet? {
        val coverageNetEntity = coverageDao.getCoverageNetEntityBySessionID(sessionId)

        return if (coverageNetEntity == null) {
            null
        } else {
            CoverageNet(
                connectivityType = coverageNetEntity.connectivityType,
                roamingData = coverageNetEntity.roamingData,
                connectedWifiStatus = getCoverageConnectedWifiStatus(sessionId)
            )
        }
    }

    /**
     * Generated an object of [CoverageConnectedWifiStatus]
     * Based on data class [CoverageConnectedWifiStatus]
     * Entity class CoverageConnectedWifiStatusEntity
     * @param sessionId: String
     * @return [CoverageConnectedWifiStatus]
     */
    private fun getCoverageConnectedWifiStatus(sessionId: String): CoverageConnectedWifiStatus? {
        val coverageConnectedWifiStatusEntity = coverageDao.getCoverageConnectedWifiStatusEntityEntityBySessionID(sessionId)

        return if (coverageConnectedWifiStatusEntity == null) {
            null
        } else {
            CoverageConnectedWifiStatus(
                wifiState = coverageConnectedWifiStatusEntity.wifiState,
                bssid = coverageConnectedWifiStatusEntity.bssid,
                bssLoad = coverageConnectedWifiStatusEntity.bssLoad,
                capabilities = coverageConnectedWifiStatusEntity.capabilities,
                centerFreq0 = coverageConnectedWifiStatusEntity.centerFreq0,
                centerFreq1 = coverageConnectedWifiStatusEntity.centerFreq1,
                channelMode = coverageConnectedWifiStatusEntity.channelMode,
                channelWidth = coverageConnectedWifiStatusEntity.channelWidth,
                frequency = coverageConnectedWifiStatusEntity.frequency,
                rssiLevel = coverageConnectedWifiStatusEntity.rssiLevel,
                operatorFriendlyName = coverageConnectedWifiStatusEntity.operatorFriendlyName,
                passportNetwork = coverageConnectedWifiStatusEntity.passportNetwork,
                ssid = coverageConnectedWifiStatusEntity.ssid,
                accessPointUpTime = coverageConnectedWifiStatusEntity.accessPointUpTime,
                timestamp = coverageConnectedWifiStatusEntity.timestamp
            )
        }
    }

    /**
     * Generated an object of [CoverageTelephony]
     * Based on data class [CoverageTelephony]
     * Entity class CoverageTelephonyEntity
     * @param sessionId: String
     * @return [CoverageTelephony]
     */
    private fun getCoverageTelephony(sessionId: String): CoverageTelephony {
        val coverageTelephonyEntity = coverageDao.getCoverageTelephonyEntityBySessionID(sessionId)

        return if (coverageTelephonyEntity == null) {
            CoverageTelephony()
        } else {
            CoverageTelephony(
                simState = coverageTelephonyEntity.simState,
                roamingNetwork = coverageTelephonyEntity.roamingNetwork,
                roamingVoice = coverageTelephonyEntity.roamingVoice,
                networkType = coverageTelephonyEntity.networkType ?: "",
                serviceState = coverageTelephonyEntity.serviceState,
                primaryCell = getCoveragePrimaryCell(sessionId),
                nrCell = getCoverageNrCell(sessionId)
            )
        }
    }

    /**
     * Generated an object of [CoveragePrimaryCell]
     * Based on data class [CoveragePrimaryCell]
     * Entity class CoveragePrimaryCellEntity
     * @param sessionId: String
     * @return [CoveragePrimaryCell]
     */
    private fun getCoveragePrimaryCell(sessionId: String): CoveragePrimaryCell {
        val coveragePrimaryCellEntity = coverageDao.getCoveragePrimaryCellEntityBySessionID(sessionId)

        return if (coveragePrimaryCellEntity == null) {
            CoveragePrimaryCell()
        } else {
            CoveragePrimaryCell(
                cellType = coveragePrimaryCellEntity.cellType ?: "",
                cellSignalStrength = getCoverageCellSignalStrength(sessionId),
                cellIdentity = getCoverageCellIdentity(sessionId)
            )
        }
    }

    /**
     * Generated an object of [CoverageCellSignalStrength]
     * Based on data class [CoverageCellSignalStrength]
     * Entity class CoverageCellSignalStrengthEntity
     * @param sessionId: String
     * @return [CoverageCellSignalStrength]
     */
    private fun getCoverageCellSignalStrength(sessionId: String): CoverageCellSignalStrength {
        val coverageCellSignalStrengthEntity = coverageDao.getCoverageCellSignalStrengthEntityBySessionID(sessionId)

        return if (coverageCellSignalStrengthEntity == null) {
            CoverageCellSignalStrength()
        } else {
            CoverageCellSignalStrength(
                asu = coverageCellSignalStrengthEntity.asu ?: "",
                dBm = coverageCellSignalStrengthEntity.dBm ?: "",
                bandwidth = coverageCellSignalStrengthEntity.bandwidth,
                rsrp = coverageCellSignalStrengthEntity.rsrp ?: "",
                rsrq = coverageCellSignalStrengthEntity.rsrq ?: "",
                rssnr = coverageCellSignalStrengthEntity.rssnr,
                cqi = coverageCellSignalStrengthEntity.cqi,
                timingAdvance = coverageCellSignalStrengthEntity.timingAdvance
            )
        }
    }

    /**
     * Generated an object of [CoverageCellIdentity]
     * Based on data class [CoverageCellIdentity]
     * Entity class CoverageCellIdentityEntity
     * @param sessionId: String
     * @return [CoverageCellIdentity]
     */
    private fun getCoverageCellIdentity(sessionId: String): CoverageCellIdentity {
        val coverageCellIdentityEntity = coverageDao.getCoverageCellIdentityEntityBySessionID(sessionId)

        return if (coverageCellIdentityEntity == null) {
            CoverageCellIdentity()
        } else {
            CoverageCellIdentity(
                cellId = coverageCellIdentityEntity.cellId ?: "",
                cellInfoDelay = coverageCellIdentityEntity.cellInfoDelay ?: "",
                networkName = coverageCellIdentityEntity.networkName,
                mcc = coverageCellIdentityEntity.mcc ?: "",
                mnc = coverageCellIdentityEntity.mnc ?: "",
                earfcn = coverageCellIdentityEntity.earfcn,
                tac = coverageCellIdentityEntity.tac,
                lac = coverageCellIdentityEntity.lac
            )
        }
    }

    /**
     * Generated an object of [CoverageNrCell]
     * Based on data class [CoverageNrCell]
     * Entity class CoverageNrCellEntity
     * @param sessionId: String
     * @return [CoverageNrCell]
     */
    private fun getCoverageNrCell(sessionId: String): CoverageNrCell? {
        val coverageNrCellEntity = coverageDao.getCoverageNrCellEntityBySessionID(sessionId)

        return if (coverageNrCellEntity == null) {
            CoverageNrCell()
        } else {
            CoverageNrCell(
                nrCsiRsrp = coverageNrCellEntity.nrCsiRsrp,
                nrCsiRsrq = coverageNrCellEntity.nrCsiRsrq,
                nrCsiSinr = coverageNrCellEntity.nrCsiSinr,
                nrSsRsrp = coverageNrCellEntity.nrSsRsrp,
                nrSsRsrq = coverageNrCellEntity.nrSsRsrq,
                nrSsSinr = coverageNrCellEntity.nrSsSinr,
                nrStatus = coverageNrCellEntity.nrStatus,
                nrDbm = coverageNrCellEntity.nrDbm,
                nrLevel = coverageNrCellEntity.nrLevel,
                nrAsuLevel = coverageNrCellEntity.nrAsuLevel,
                nrArfcn = coverageNrCellEntity.nrArfcn,
                nrCi = coverageNrCellEntity.nrCi,
                nrPci = coverageNrCellEntity.nrPci,
                nrTac = coverageNrCellEntity.nrTac
            )
        }
    }

    /**
     * deletes the raw data for production build
     *
     * @param baseEchoLocateCoverageEntityList List of entities that needs to be deleted.
     */
    private fun deleteRawData(baseEchoLocateCoverageEntityList: List<BaseEchoLocateCoverageEntity>) {
        coverageRepository.deleteRawData(baseEchoLocateCoverageEntityList)
    }


    /**
     * This function deletes the the report data from db based on status
     * @param status: Status of reports to be deleted
     */
    fun deleteProcessedReports(status: String) {
        coverageRepository.deleteProcessedReports(status)
    }

    /**
     * This fun saves the entities to database
     * The function doesn't create a new thread and executes the statement in the same thread
     * in which this function is called
     * @param coverageSingleSessionReportEntityList: List<CoverageSingleSessionReportEntity>
     */
    private fun saveCoverageReportToDB(coverageSingleSessionReportEntityList: List<CoverageSingleSessionReportEntity>): List<Long> {
        return coverageRepository.insertAllCoverageSingleSessionReportEntity(coverageSingleSessionReportEntityList)
    }

    /**
     * This fun updates record in database with new data by using updateAllBaseEchoLocateCoverageEntityStatus
     * using Variable number of arguments (varargs) - getting latest value of data
     */
    private fun markRawDataAsProcessed(baseEchoLocateCoverageEntityList: List<BaseEchoLocateCoverageEntity>) {
        coverageDao.updateAllBaseEchoLocateCoverageEntityStatus(*baseEchoLocateCoverageEntityList.toTypedArray())
    }

}
