package com.tmobile.mytmobile.echolocate.nr5g.manager

import android.content.Context
import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import com.tmobile.mytmobile.echolocate.nr5g.nsa5g.database.EchoLocateNr5gDatabase
import com.tmobile.mytmobile.echolocate.nr5g.nsa5g.database.dao.Nr5gDao
import com.tmobile.mytmobile.echolocate.nr5g.nsa5g.database.entity.*
import com.tmobile.mytmobile.echolocate.nr5g.nsa5g.reportprocessor.Nsa5gReportProcessor
import com.tmobile.mytmobile.echolocate.nr5g.sa5g.database.entity.*
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.io.IOException

internal class Nsa5gDataManagerTest{
    lateinit var context: Context
    private lateinit var nsa5gDataManager: Nsa5gDataManager
    private lateinit var nsa5gReportProcessor: Nsa5gReportProcessor
    private lateinit var nsa5gDao: Nr5gDao
    private lateinit var db: EchoLocateNr5gDatabase

    @Before
    fun setUp() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        db = Room.inMemoryDatabaseBuilder(context, EchoLocateNr5gDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        nsa5gDao = EchoLocateNr5gDatabase.getEchoLocateNr5gDatabase(context).nr5gDao()
        nsa5gDataManager = Nsa5gDataManager(context)
        nsa5gReportProcessor = Nsa5gReportProcessor.getInstance(context)
    }


    @Test
    fun processRawDataTest() {
        Assert.assertNotNull(nsa5gDataManager.processRawData("workerId"))
    }

    @Test
    fun testIs5gSupported() {
        assert(nsa5gDataManager.isNsa5gSupported)
    }

    @Test
    fun deleteDatabaseDataTest() {
        insertData()
        val nsa5gSingleSessionReportEntityList = nsa5gDao.getNr5gSingleSessionReportEntityList()
        val reportDeleted = nsa5gReportProcessor.deleteProcessedReports("")
        Assert.assertNotNull(reportDeleted)
    }

    @Test
    fun deleteProcessedReportsFromDatabaseTest() {
        insertData()
        nsa5gDataManager.get5gReportEntityList(0L,0L)
        Assert.assertNotNull(nsa5gDataManager.deleteProcessedReportsFromDatabase())
    }


    @Test
    fun store5gEntity(){
        Assert.assertNotNull(nsa5gDataManager.store5gEntity(300,false,"",System.currentTimeMillis().toInt()))
    }

    @Test
    fun	get5gReportListTest(){
//        insertData()
        val singleSessionReportEntityList = nsa5gDao.getNr5gSingleSessionReportEntityList()
        Assert.assertNotNull(nsa5gDataManager.get5gReportList(singleSessionReportEntityList))
    }

    @Test
    fun	get5gReportEntityListTest(){
//        insertData()
        val startTime = 0L
        val endTime = 0L
        Assert.assertNotNull(nsa5gDataManager.get5gReportEntityList(startTime,endTime))

        val sa5gReportEntityList = nsa5gReportProcessor.getNr5gMultiSessionReportEntity(startTime, endTime)
        Assert.assertNotNull(nsa5gDataManager.convertToDIAReportResponseEvent(sa5gReportEntityList))

    }

    private fun insertData() {
        val sessionId = "56aa1c58-6967-4ea4-94cb-90ea37880f08"
        val uniqueId = "94aa1c58-6967-4ea4-94cb-90ea37880f08"
        val timeStamp = "2019-12-19T15:07:58.454-0800"

//        BaseEchoLocateNr5gEntity
        val baseEchoLocateNr5gEntity = BaseEchoLocateNr5gEntity(
            200,
            "status",
            timeStamp,
            sessionId
        )

        val nr5gOEMSVEntity = Nr5gOEMSVEntity(
            "1",
            "customVersion",
            "radioVersion",
            "buildName",
            "androidVersion"
        )
        nr5gOEMSVEntity.sessionId = sessionId
        nr5gOEMSVEntity.uniqueId = uniqueId

        val nr5gDeviceInfoEntity = Nr5gDeviceInfoEntity(
            "imei",
            "imsi",
            "msisdn",
            "uuid",
            "testSessionID",
            "modelCode",
            "oem"
        )
        nr5gDeviceInfoEntity.sessionId = sessionId
        nr5gDeviceInfoEntity.uniqueId = uniqueId


        val nr5gLocationEntity = Nr5gLocationEntity(
            10.0,
            0.0f,
            10.0,
            10.0,
            0.0f,
            timeStamp,
            10
        )
        nr5gLocationEntity.sessionId = sessionId
        nr5gLocationEntity.uniqueId = uniqueId

        val connectedWifiStatusEntity = ConnectedWifiStatusEntity(
            "bssid",
            "bssLoad",
            "ssId",
            3,
            "capabil",
            0,
            1,
            "mode",
            2,
            3,
            "name",
            1,
            1
        )
        connectedWifiStatusEntity.sessionId = sessionId
        connectedWifiStatusEntity.uniqueId = uniqueId

        val endcLteLogEntity = EndcLteLogEntity(
            timeStamp,
            2,
            1,
            1,
            1,
            3
        )
        endcLteLogEntity.sessionId = sessionId
        endcLteLogEntity.uniqueId = uniqueId



        val nr5gMmwCellLogEntity = Nr5gMmwCellLogEntity(
            timeStamp,
            1,
            2,
            4,
            999f,
            999f,
            999f,
            4,
            999f,
            999f,
            999f,
            "name",
            1,
            1
        )
        nr5gMmwCellLogEntity.sessionId = sessionId
        nr5gMmwCellLogEntity.uniqueId = uniqueId



        val endcUplinkLogEntity = EndcUplinkLogEntity(
            timeStamp,
            2,
            1
        )
        endcUplinkLogEntity.sessionId = sessionId
        endcUplinkLogEntity.uniqueId = uniqueId


        val nr5gUiLogEntity = Nr5gUiLogEntity(
            timeStamp,
            2,
            "type",
            "data",
            4,
            1
        )
        nr5gUiLogEntity.sessionId = sessionId
        nr5gUiLogEntity.uniqueId = uniqueId


        val nr5gStatusEntity = Nr5gStatusEntity(
            2
        )
        nr5gStatusEntity.sessionId = sessionId
        nr5gStatusEntity.uniqueId = uniqueId


        val nr5gNetworkIdentityEntity = Nr5gNetworkIdentityEntity(
            timeStamp,
            1,
            "310",
            "160",
            2222,
            21,
            22,
            23
        )
        nr5gNetworkIdentityEntity.sessionId = sessionId
        nr5gNetworkIdentityEntity.uniqueId = uniqueId


        val nr5gDataNetworkTypeEntity = Nr5gDataNetworkTypeEntity(
            timeStamp,
            3
        )
        nr5gDataNetworkTypeEntity.sessionId = sessionId
        nr5gDataNetworkTypeEntity.uniqueId = uniqueId


        val nr5gTriggerEntity = Nr5gTriggerEntity(
            timeStamp,
            200,
            "app",
            2
        )
        nr5gTriggerEntity.sessionId = sessionId
        nr5gTriggerEntity.uniqueId = uniqueId


        val nr5gWifiStateEntity = Nr5gWifiStateEntity(
            3
        )
        nr5gWifiStateEntity.sessionId = sessionId
        nr5gWifiStateEntity.uniqueId = uniqueId


        val nr5gActiveNetworkEntity = Nr5gActiveNetworkEntity(
            2
        )
        nr5gActiveNetworkEntity.sessionId = sessionId
        nr5gActiveNetworkEntity.uniqueId = uniqueId


        nsa5gDao.insertBaseEchoLocateNr5gEntity(baseEchoLocateNr5gEntity)
        nsa5gDao.insertConnectedWifiStatusEntity(connectedWifiStatusEntity)
        nsa5gDao.insertEndcLteLogEntity(endcLteLogEntity)
        nsa5gDao.insertEndcUplinkLogEntity(endcUplinkLogEntity)
        nsa5gDao.insertNr5gActiveNetworkEntity(nr5gActiveNetworkEntity)
        nsa5gDao.insertNr5gDataNetworkTypeEntity(nr5gDataNetworkTypeEntity)
        nsa5gDao.insertNr5gDeviceInfoEntity(nr5gDeviceInfoEntity)
        nsa5gDao.insertNr5gLocationEntity(nr5gLocationEntity)
        nsa5gDao.insertNr5gMmwCellLogEntity(nr5gMmwCellLogEntity)
        nsa5gDao.insertNr5gNetworkIdentityEntity(nr5gNetworkIdentityEntity)
        nsa5gDao.insertNr5gOEMSVEntity(nr5gOEMSVEntity)
        nsa5gDao.insertNr5gTriggerEntity(nr5gTriggerEntity)
        nsa5gDao.insertNr5gUiLogEntity(nr5gUiLogEntity)
        nsa5gDao.insertNr5gStatusEntity(nr5gStatusEntity)
        nsa5gDao.insertNr5gWifiStateEntity(nr5gWifiStateEntity)

    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
        db.clearAllTables()
    }

}