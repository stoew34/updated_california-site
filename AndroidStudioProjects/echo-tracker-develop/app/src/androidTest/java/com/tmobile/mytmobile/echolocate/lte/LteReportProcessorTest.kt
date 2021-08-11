package com.tmobile.mytmobile.echolocate.lte

import android.content.Context
import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import com.tmobile.mytmobile.echolocate.configuration.events.reportingevents.DIAReportResponseParameters
import com.tmobile.mytmobile.echolocate.lte.database.EchoLocateLteDatabase
import com.tmobile.mytmobile.echolocate.lte.database.dao.LteDao
import com.tmobile.mytmobile.echolocate.lte.database.entity.*
import com.tmobile.mytmobile.echolocate.lte.reportprocessor.LteReportProcessor
import com.tmobile.mytmobile.echolocate.utils.EchoLocateLog
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.junit.*
import java.io.IOException

class LteReportProcessorTest {
    private lateinit var lteDao: LteDao
    private lateinit var db: EchoLocateLteDatabase
    lateinit var context: Context
    lateinit var lteReportProcessor: LteReportProcessor

    @Before
    fun setUp() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        db = Room.inMemoryDatabaseBuilder(context, EchoLocateLteDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        lteDao = EchoLocateLteDatabase.getEchoLocateLteDatabase(context).lteDao()
        lteReportProcessor = LteReportProcessor.getInstance(context)
    }

    @Test
    fun processRawDataTest() {
        //common data
        val sessionId = "56aa1c58-6967-4ea4-94cb-90ea37880f08"
        val uniqueId = "94aa1c58-6967-4ea4-94cb-90ea37880f08"
        val status = "RawData"
        val startTime = 100L
        val endTime = 1000L

//        BaseEchoLocateLteEntity
        val baseEchoLocateLteEntity = BaseEchoLocateLteEntity(
            501,
            status,
            "1570746592195",
            "26",
            "1",
            sessionId
        )

//        LteOEMSVEntity
        val lteOEMSVEntity = LteOEMSVEntity(
            "1",
            "androidVersion",
            "buildName",
            "customVersion",
            "radioVersion"
        )
        lteOEMSVEntity.sessionId = sessionId
        lteOEMSVEntity.uniqueId = uniqueId

//        BearerEntity       
        val bearerEntity = BearerEntity(
            "apnName",
            1
        )
        bearerEntity.sessionId = sessionId
        bearerEntity.uniqueId = uniqueId
        val bearerList = mutableListOf<BearerEntity>()
        bearerList.add(bearerEntity)

//        BearerConfiguration
        val bearerConfigurationEntity = BearerConfigurationEntity(
            "1",
            "numOfBearers",
            "1570746592195"
        )
        bearerConfigurationEntity.sessionId = sessionId
        bearerConfigurationEntity.uniqueId = uniqueId

//        CommonRFConfigurationEntity
        val commonRFConfigurationEntity = CommonRFConfigurationEntity(
            1,
            0,
            "ytContentId",
            "ytLink",
            2,
            3,
            "4",
            "1570746592195",
            5,
            6
        )
        commonRFConfigurationEntity.sessionId = sessionId
        commonRFConfigurationEntity.uniqueId = uniqueId

//        DownLinkCarrierInfoEntity
        val downLinkCarrierInfoEntity = DownLinkCarrierInfoEntity(
            "1",
            2,
            "1570746592195"
        )
        downLinkCarrierInfoEntity.sessionId = sessionId
        downLinkCarrierInfoEntity.uniqueId = uniqueId

//        DownlinkRFConfigurationEntity
        val downlinkRFConfigurationEntity = DownlinkRFConfigurationEntity(
            "2",
            "1570746592195"
        )
        downlinkRFConfigurationEntity.sessionId = sessionId
        downlinkRFConfigurationEntity.uniqueId = uniqueId

//        LteLocationEntity        
        val lteLocationEntity = LteLocationEntity(
            10.0,
            0.0f,
            10.0,
            10.0,
            0.0f,
            1
        )
        lteLocationEntity.sessionId = sessionId
        lteLocationEntity.uniqueId = uniqueId

//        NetworkIdentityEntity         
        val networkIdentityEntity = NetworkIdentityEntity(
            "2",
            "3",
            "4",
            "5",
            "1570746592195",
            "wifiConnectionstatus"
        )
        networkIdentityEntity.sessionId = sessionId
        networkIdentityEntity.uniqueId = uniqueId

//        LteSettingsEntity        
        val lteSettingsEntity = LteSettingsEntity(
            1,
            2,
            3,
            4,
            "1570746592195",
            "roamingSettings",
            "rtt",
            "rttTranscript",
            5
        )
        lteSettingsEntity.sessionId = sessionId
        lteSettingsEntity.uniqueId = uniqueId

//        SecondCarrierEntity         
        val secondCarrierEntity = SecondCarrierEntity(
            "rsrp",
            "rsrq",
            "rssi",
            "sinr"
        )
        secondCarrierEntity.sessionId = sessionId
        secondCarrierEntity.uniqueId = uniqueId

//        ThirdCarrierEntity         
        val thirdCarrierEntity = ThirdCarrierEntity(
            "rsrp",
            "rsrq",
            "rssi",
            "sinr"
        )
        thirdCarrierEntity.sessionId = sessionId
        thirdCarrierEntity.uniqueId = uniqueId

//        SignalConditionEntity         
        val signalConditionEntity = SignalConditionEntity(
            1,
            2,
            3,
            4,
            5,
            6,
            "networkType",
            "1570746592195"
        )
        signalConditionEntity.sessionId = sessionId
        signalConditionEntity.uniqueId = uniqueId

//        UpLinkRFConfigurationEntity          
        val upLinkRFConfigurationEntity = UpLinkRFConfigurationEntity(
            "1",
            "1570746592195"
        )
        upLinkRFConfigurationEntity.sessionId = sessionId
        upLinkRFConfigurationEntity.uniqueId = uniqueId

//        UplinkCarrierInfoEntity          
        val uplinkCarrierInfoEntity = UplinkCarrierInfoEntity(
            "2",
            3,
            "1570746592195"
        )
        uplinkCarrierInfoEntity.sessionId = sessionId
        uplinkCarrierInfoEntity.uniqueId = uniqueId

//        CAEntity
        val caEntity = CAEntity(
            1,
            1,
            2,
            3,
            1,
            "modulation",
            "pci",
            "cellId",
            "locationId"
        )
        caEntity.sessionId = sessionId
        caEntity.uniqueId = uniqueId

//       Insert dummy data to DataBase
        lteDao.insertBaseEchoLocateLteEntity(baseEchoLocateLteEntity)
        lteDao.insertSecondCarrierEntity(secondCarrierEntity)
        lteDao.insertThirdCarrierEntity(thirdCarrierEntity)
        lteDao.insertAllBearerEntity(bearerEntity)
        lteDao.insertAllCAEntity(caEntity)
        lteDao.insertLteOEMSVEntity(lteOEMSVEntity)
        lteDao.insertBearerConfigurationEntity(bearerConfigurationEntity)
        lteDao.insertCommonRFConfigurationEntity(commonRFConfigurationEntity)
        lteDao.insertDownLinkCarrierInfoEntity(downLinkCarrierInfoEntity)
        lteDao.insertDownlinkRFConfigurationEntity(downlinkRFConfigurationEntity)
        lteDao.insertLteLocationEntity(lteLocationEntity)
        lteDao.insertNetworkIdentityEntity(networkIdentityEntity)
        lteDao.insertLteSettingsEntity(lteSettingsEntity)
        lteDao.insertSignalConditionEntity(signalConditionEntity)
        lteDao.insertUpLinkRFConfigurationEntity(upLinkRFConfigurationEntity)
        lteDao.insertUplinkCarrierInfoEntity(uplinkCarrierInfoEntity)



        lteReportProcessor.processRawData()
        val reportEntity = lteReportProcessor.getLteMultiSessionReportEntity(0L, 0L)
        val lteMultiSessionReport = lteReportProcessor.getLteMultiSessionReport(reportEntity)
        val lteSingleSessionReportEntityList = lteDao.getLteSingleSessionReportEntityList()
        val lteSingleSessionReportEntity = lteSingleSessionReportEntityList.get(0)

        assert(lteSingleSessionReportEntityList.isNullOrEmpty())
        Assert.assertNotNull(lteSingleSessionReportEntity)
        Assert.assertNotNull(lteMultiSessionReport)
    }

    @Test
    fun lteReportResponseEventTest() {
        val lteModuleProvider = LteModuleProvider.getInstance(context)
        var lteReportResponseParameters = DIAReportResponseParameters("", "", "", "")
        lteReportProcessor.processRawData()
        val reportEntity = lteReportProcessor.getLteMultiSessionReportEntity(0L, 0L)
        lteReportProcessor.getLteMultiSessionReport(reportEntity)

        lteModuleProvider.getLteReport(0L, 0L)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                lteReportResponseParameters = it.get(0).DIAReportResponseParameters
            }, {
                EchoLocateLog.eLogE("error in get reports: " + it.printStackTrace())
            })
        Assert.assertNotNull(lteReportResponseParameters)
        assert(lteReportResponseParameters.requestReportStatus == "Completed")
        assert(lteReportResponseParameters.reportType == "ReportVoice")
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.clearAllTables()
        db.close()
    }

    @Ignore
    @Test
    fun voiceLteModuleReady() {
        // TODO:: Ignoring this test as we are unable to mock the configuration in the LteModuleProvider.
        val lteModuleProvider = LteModuleProvider.getInstance(context)
        lteModuleProvider.initLteModule(context)
        Assert.assertFalse(lteModuleProvider.isLteModuleReady())
    }

}