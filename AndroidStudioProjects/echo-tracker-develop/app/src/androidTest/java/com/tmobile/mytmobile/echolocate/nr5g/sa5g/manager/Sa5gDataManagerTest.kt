package com.tmobile.mytmobile.echolocate.nr5g.sa5g.manager

import android.content.Context
import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import com.tmobile.mytmobile.echolocate.nr5g.manager.Sa5gDataManager
import com.tmobile.mytmobile.echolocate.nr5g.sa5g.database.EchoLocateSa5gDatabase
import com.tmobile.mytmobile.echolocate.nr5g.sa5g.database.dao.Sa5gDao
import com.tmobile.mytmobile.echolocate.nr5g.sa5g.database.entity.*
import com.tmobile.mytmobile.echolocate.nr5g.sa5g.reportprocessor.Sa5gReportProcessor
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.io.IOException

class Sa5gDataManagerTest {
    lateinit var context: Context
    private lateinit var sa5gDataManager: Sa5gDataManager
    private lateinit var sa5gReportProcessor: Sa5gReportProcessor
    private lateinit var sa5gDao: Sa5gDao
    private lateinit var db: EchoLocateSa5gDatabase

    @Before
    fun setUp() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        db = Room.inMemoryDatabaseBuilder(context, EchoLocateSa5gDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        sa5gDao = EchoLocateSa5gDatabase.getEchoLocateSa5gDatabase(context).sa5gDao()
        sa5gDataManager = Sa5gDataManager(context)
        sa5gReportProcessor = Sa5gReportProcessor.getInstance(context)
    }

    @Test
    fun processRawDataTest() {
        Assert.assertNotNull(sa5gDataManager.processRawData("workerId"))
    }

    @Test
    fun testIs5gSupported() {
        assert(sa5gDataManager.isSa5gSupported)
    }

    @Test
    fun deleteDatabaseDataTest() {
        insertData()
        val reportDeleted =
            sa5gReportProcessor.deleteProcessedReports("")
        Assert.assertNotNull(reportDeleted)

        sa5gDataManager.get5gReportEntityList(0L,0L)
        Assert.assertNotNull(sa5gDataManager.deleteProcessedReportsFromDatabase())
    }

    @Test
    fun store5gEntity(){
        Assert.assertNotNull(sa5gDataManager.store5gEntity(300,false,"",System.currentTimeMillis().toInt()))
    }

    @Test
    fun	get5gReportListTest(){
        insertData()
        val singleSessionReportEntityList = sa5gDao.getSa5gSingleSessionReportEntityList()
        Assert.assertNotNull(sa5gDataManager.get5gReportList(singleSessionReportEntityList))
    }

    @Test
    fun	get5gReportEntityListTest(){
        insertData()
        val startTime = 0L
        val endTime = 0L
        Assert.assertNotNull(sa5gDataManager.get5gReportEntityList(startTime,endTime))

        val sa5gReportEntityList = sa5gReportProcessor.getSa5gMultiSessionReportEntity(startTime, endTime)
        Assert.assertNotNull(sa5gDataManager.convertToDIAReportResponseEvent(sa5gReportEntityList))

    }

    private fun insertData() {
        val sessionId = "56aa1c58-6967-4ea4-94cb-90ea37880f08"
        val uniqueId = "94aa1c58-6967-4ea4-94cb-90ea37880f08"
        val timeStamp = "2019-12-19T15:07:58.454-0800"

//        BaseEchoLocateSa5gEntity
        val baseEchoLocateSa5gEntity = BaseEchoLocateSa5gEntity(
            200,
            "",
            timeStamp,
            sessionId
        )


        // Active network
        val sa5gActiveNetworkEntity = Sa5gActiveNetworkEntity(
            2
        )
        sa5gActiveNetworkEntity.sessionId = sessionId
        sa5gActiveNetworkEntity.uniqueId = uniqueId


        val connectedWifiStatusEntity = Sa5gConnectedWifiStatusEntity(
            "bssId",
            "bssLoad",
            "ssId",
            0,
            " capabilities",
            12345,
            12345,
            "channelMode",
            1234,
            12345,
            " operatorFriendlyName",
            1234,
            12345
        )
        connectedWifiStatusEntity.sessionId = sessionId
        connectedWifiStatusEntity.uniqueId = uniqueId


        // Device Info
        val sa5gDeviceInfoEntity = Sa5gDeviceInfoEntity(
            "imei",
            "imsi",
            "msisdn",
            "uuid",
            "testSessionID",
            "modelCode",
            "oem"
        )
        sa5gDeviceInfoEntity.sessionId = sessionId
        sa5gDeviceInfoEntity.uniqueId = uniqueId

        //Sa5gLocationEntity
        val sa5gLocationEntity = Sa5gLocationEntity(
            10.0,
            0.0f,
            10.0,
            10.0,
            0.0f,
            timeStamp,
            10
        )
        sa5gLocationEntity.sessionId = sessionId
        sa5gLocationEntity.uniqueId = uniqueId

        //Sa5gNetworkLogEntity
        val sa5gNetworkLogEntity =
            Sa5gNetworkLogEntity("310", "160", "endCapability", "endConnections")

        sa5gNetworkLogEntity.sessionId = sessionId
        sa5gNetworkLogEntity.uniqueId = uniqueId

        //Sa5gOEMSVEntity
        val sa5gOEMSVEntity = Sa5gOEMSVEntity(
            "1",
            "customVersion",
            "radioVersion",
            "buildName",
            "androidVersion"
        )
        sa5gOEMSVEntity.sessionId = sessionId
        sa5gOEMSVEntity.uniqueId = uniqueId

        //Sa5gRrcLogEntity
        val sa5gRrcLogEntity = Sa5gRrcLogEntity("rrcState", "nrRrcState")
        sa5gRrcLogEntity.sessionId = sessionId
        sa5gRrcLogEntity.uniqueId = uniqueId

        //
        val sa5gSettingsLogEntity = Sa5gSettingsLogEntity(
            "wificalling", "wifi", "false", "rtt", "rttTransScript",
            "wifi"
        )

        sa5gSettingsLogEntity.sessionId = sessionId
        sa5gSettingsLogEntity.uniqueId = uniqueId

        val sa5gTriggerEntity = Sa5gTriggerEntity(
            timeStamp,
            200,
            "app",
            2
        )
        sa5gTriggerEntity.sessionId = sessionId
        sa5gTriggerEntity.uniqueId = uniqueId

        //Sa5gUplinkCarrierLogsEntity
        val sa5gUplinkCarrierLogsEntity = Sa5gUplinkCarrierLogsEntity(
            "techType", "bandNumber", "afcn",
            "5g", "yes"
        )
        sa5gUplinkCarrierLogsEntity.sessionId = sessionId
        sa5gUplinkCarrierLogsEntity.uniqueId = uniqueId


        val sa5gWiFiStateEntity = Sa5gWiFiStateEntity(
            3
        )
        sa5gWiFiStateEntity.sessionId = sessionId
        sa5gWiFiStateEntity.uniqueId = uniqueId

        sa5gDao.insertBaseEchoLocateSa5gEntity(baseEchoLocateSa5gEntity)
        sa5gDao.insertSa5gActiveNetworkEntity(sa5gActiveNetworkEntity)
        sa5gDao.insertSa5gDeviceInfoEntity(sa5gDeviceInfoEntity)
        sa5gDao.insertSa5gLocationEntity(sa5gLocationEntity)
        sa5gDao.insertSa5gOEMSVEntity(sa5gOEMSVEntity)
        sa5gDao.insertSa5gRrcLogEntity(sa5gRrcLogEntity)
        sa5gDao.insertSa5gSettingsLogEntity(sa5gSettingsLogEntity)
        sa5gDao.insertSa5gTriggerEntity(sa5gTriggerEntity)
        sa5gDao.insertAllSa5gUplinkCarrierLogsEntity(sa5gUplinkCarrierLogsEntity)
        sa5gDao.insertSa5gWiFiStateEntity(sa5gWiFiStateEntity)

    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
        db.clearAllTables()
    }

}