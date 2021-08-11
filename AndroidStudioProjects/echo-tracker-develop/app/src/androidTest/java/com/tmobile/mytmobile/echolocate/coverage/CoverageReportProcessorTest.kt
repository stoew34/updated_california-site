package com.tmobile.mytmobile.echolocate.coverage

/**
 * Created by Mahesh Shetye on 2020-05-25
 *
 */

import android.content.Context
import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import com.tmobile.mytmobile.echolocate.configuration.events.reportingevents.DIAReportResponseParameters
import com.tmobile.mytmobile.echolocate.coverage.database.EchoLocateCoverageDatabase
import com.tmobile.mytmobile.echolocate.coverage.database.dao.CoverageDao
import com.tmobile.mytmobile.echolocate.coverage.database.entity.*
import com.tmobile.mytmobile.echolocate.coverage.delegates.TriggerSource
import com.tmobile.mytmobile.echolocate.coverage.reportprocessor.CoverageDataStatus
import com.tmobile.mytmobile.echolocate.coverage.reportprocessor.CoverageReportProcessor
import com.tmobile.mytmobile.echolocate.coverage.utils.CoverageConstants
import com.tmobile.mytmobile.echolocate.utils.EchoLocateLog
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.junit.*
import java.io.IOException
import java.util.*

class CoverageReportProcessorTest {
    private lateinit var coverageDao: CoverageDao
    private lateinit var db: EchoLocateCoverageDatabase
    lateinit var context: Context
    lateinit var coverageReportProcessor: CoverageReportProcessor

    @Before
    fun setUp() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        db = Room.inMemoryDatabaseBuilder(context, EchoLocateCoverageDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        coverageDao = EchoLocateCoverageDatabase.getEchoLocateCoverageDatabase(context).coverageDao()
        coverageReportProcessor = CoverageReportProcessor.getInstance(context)
    }

    @Test
    fun processRawDataTest() {
        //common data
        val sessionId = UUID.randomUUID().toString()
        val timeStamp = "2019-12-19T15:07:58.454-0800"
        val status = CoverageDataStatus.STATUS_RAW
        val schemaVer = CoverageConstants.SCHEMA_VERSION

//        BaseEchoLocateNr5gEntity
        val baseEchoLocateCoverageEntity = BaseEchoLocateCoverageEntity(
            TriggerSource.SCREEN_ACTIVITY.name,
            status,
            timeStamp,
            schemaVer,
            sessionId
        )

        val coverageConnectedWifiStatus = CoverageConnectedWifiStatusEntity(
            "WIFI_STATE_ENABLED",
            "00:19:92:50:ba:21",
            "bssLoad",
            "[WPA2-PSK-CCMP][ESS]",
            "0",
            "0",
            "",
            "mode",
            "2462",
            "-78",
            "tmobile",
            "tmobile",
            "tmobile",
            "tmobile",
            "2018-05-16T16:14:10.456-0700"
        )
        coverageConnectedWifiStatus.sessionId = sessionId
        coverageConnectedWifiStatus.uniqueId = UUID.randomUUID().toString()

        val coverageNetEntity = CoverageNetEntity(
            "TRANSPORT_WIFI",
            "false"
        )
        coverageNetEntity.sessionId = sessionId
        coverageNetEntity.uniqueId = UUID.randomUUID().toString()

        val coverageOEMSVEntity = CoverageOEMSVEntity(
            "9",
            "PKQ1.181105.001",
            "G710TM20f",
            "MPSS.AT.4.0.c2.9-00061-SDM845_GEN_PACK-1.198357.15.211944.2",
            "09"
        )
        coverageOEMSVEntity.sessionId = sessionId
        coverageOEMSVEntity.uniqueId = UUID.randomUUID().toString()


        val coverageSettingsEntity = CoverageSettingsEntity(
            "ENABLED",
            "true"
        )
        coverageSettingsEntity.sessionId = sessionId
        coverageSettingsEntity.uniqueId = UUID.randomUUID().toString()

        val coverageTelephonyEntity = CoverageTelephonyEntity(
            "SIM_STATE_READY",
            "DOMESTIC",
            "0",
            "NETWORK_TYPE_LTE",
            "STATE_IN_SERVICE"
        )
        coverageTelephonyEntity.sessionId = sessionId
        coverageTelephonyEntity.uniqueId = UUID.randomUUID().toString()

        val coveragePrimaryCellEntity = CoveragePrimaryCellEntity(
            "LTE"
        )
        coveragePrimaryCellEntity.sessionId = sessionId
        coveragePrimaryCellEntity.uniqueId = UUID.randomUUID().toString()
        coveragePrimaryCellEntity.baseEntityId = coverageTelephonyEntity.uniqueId

        val coverageCellIdentityEntity = CoverageCellIdentityEntity(
            "21666306",
            "0",
            "T-Mobile",
            "310",
            "260",
            "66786",
            "11316",
            ""
        )
        coverageCellIdentityEntity.sessionId = sessionId
        coverageCellIdentityEntity.uniqueId = UUID.randomUUID().toString()
        coverageCellIdentityEntity.baseEntityId = coveragePrimaryCellEntity.uniqueId

        val coverageCellSignalStrengthEntity = CoverageCellSignalStrengthEntity(
            "45",
            "-93",
            null,
            "-94",
            "-10",
            "0",
            "0",
            "2"
        )
        coverageCellSignalStrengthEntity.sessionId = sessionId
        coverageCellSignalStrengthEntity.uniqueId = UUID.randomUUID().toString()
        coverageCellSignalStrengthEntity.baseEntityId = coverageCellIdentityEntity.uniqueId


        val coverageNrCellEntity = CoverageNrCellEntity(
            "0",
            "0",
            "0",
            "0",
            "0",
            "0",
            "None",
            "0",
            "0",
            "0",
            "0",
            "0",
            "0",
            "0"
        )
        coverageNrCellEntity.sessionId = sessionId
        coverageNrCellEntity.uniqueId = UUID.randomUUID().toString()
        coverageNrCellEntity.baseEntityId = coverageTelephonyEntity.uniqueId

//       Insert dummy data to DataBase
        coverageDao.insertBaseEchoLocateCoverageEntity(baseEchoLocateCoverageEntity)
        coverageDao.insertCoverageConnectedWifiStatusEntity(coverageConnectedWifiStatus)
        coverageDao.insertCoverageNetEntity(coverageNetEntity)
        coverageDao.insertCoverageOEMSVEntity(coverageOEMSVEntity)
        coverageDao.insertCoverageSettingsEntity(coverageSettingsEntity)
        coverageDao.insertCoverageTelephonyEntity(coverageTelephonyEntity)
        coverageDao.insertCoveragePrimaryCellEntity(coveragePrimaryCellEntity)
        coverageDao.insertCoverageCellIdentityEntity(coverageCellIdentityEntity)
        coverageDao.insertCoverageCellSignalStrengthEntity(coverageCellSignalStrengthEntity)
        coverageDao.insertCoverageNrCellEntity(coverageNrCellEntity)



        coverageReportProcessor.processRawData()
        val reportEntity = coverageReportProcessor.getCoverageMultiSessionReportEntity()
        val coverageMultiSessionReport = coverageReportProcessor.getCoverageMultiSessionReport(reportEntity)
        val coverageSingleSessionReportEntityList = coverageDao.getCoverageSingleSessionReportEntityList()
        val coverageSingleSessionReportEntity = coverageSingleSessionReportEntityList.get(0)

        assert(coverageSingleSessionReportEntityList.isNullOrEmpty())
        Assert.assertNotNull(coverageSingleSessionReportEntity)
        Assert.assertNotNull(coverageMultiSessionReport)
    }

    @Test
    fun coverageReportResponseEventTest() {
        val coverageModuleProvider = CoverageModuleProvider.getInstance(context)
        var coverageReportResponseParameters = DIAReportResponseParameters("", "", "", "")
        coverageReportProcessor.processRawData()
        val reportEntity = coverageReportProcessor.getCoverageMultiSessionReportEntity()
        coverageReportProcessor.getCoverageMultiSessionReport(reportEntity)

        coverageModuleProvider.getCoverageReport(0L, 0L)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                coverageReportResponseParameters = it.get(0).DIAReportResponseParameters
            }, {
                EchoLocateLog.eLogE("Diagnostic : error in get reports: " + it.printStackTrace())
            })
        Assert.assertNotNull(coverageReportResponseParameters)
        assert(coverageReportResponseParameters.requestReportStatus == "Completed")
        assert(coverageReportResponseParameters.reportType == "ReportNr5g")
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.clearAllTables()
        db.close()
    }
    @Ignore
    @Test
    fun isCoverageModuleReady() {
        // TODO:: Ignoring this test as we are unable to mock the configuration in the CoverageModuleProvider.
        val coverageModuleProvider = CoverageModuleProvider.getInstance(context)
        coverageModuleProvider.initCoverageModule(context)
        Assert.assertTrue(coverageModuleProvider.isCoverageModuleReady())
    }

}