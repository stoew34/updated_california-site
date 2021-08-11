package com.tmobile.mytmobile.echolocate.nr5g

import android.content.Context
import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import com.tmobile.mytmobile.echolocate.configuration.events.reportingevents.DIAReportResponseParameters
import com.tmobile.mytmobile.echolocate.nr5g.nsa5g.database.EchoLocateNr5gDatabase
import com.tmobile.mytmobile.echolocate.nr5g.nsa5g.database.dao.Nr5gDao
import com.tmobile.mytmobile.echolocate.nr5g.nsa5g.database.entity.*
import com.tmobile.mytmobile.echolocate.nr5g.nsa5g.reportprocessor.Nsa5gReportProcessor
import com.tmobile.mytmobile.echolocate.utils.EchoLocateLog
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.io.IOException

class Nr5gReportProcessorTest {
    private lateinit var nr5gDao: Nr5gDao
    private lateinit var db: EchoLocateNr5gDatabase
    lateinit var context: Context
    lateinit var nr5gReportProcessor: Nsa5gReportProcessor

    @Before
    fun setUp() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        db = Room.inMemoryDatabaseBuilder(context, EchoLocateNr5gDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        nr5gDao = EchoLocateNr5gDatabase.getEchoLocateNr5gDatabase(context).nr5gDao()
        nr5gReportProcessor = Nsa5gReportProcessor.getInstance(context)
    }

    @Test
    fun processRawDataTest() {
        //common data
        val sessionId = "56aa1c58-6967-4ea4-94cb-90ea37880f08"
        val uniqueId = "94aa1c58-6967-4ea4-94cb-90ea37880f08"
        val timeStamp = "2019-12-19T15:07:58.454-0800"
        val status = "RawData"
        val startTime = 100L
        val endTime = 1000L

//        BaseEchoLocateNr5gEntity
        val baseEchoLocateNr5gEntity = BaseEchoLocateNr5gEntity(
            200,
            status,
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


//       Insert dummy data to DataBase
        nr5gDao.insertBaseEchoLocateNr5gEntity(baseEchoLocateNr5gEntity)
        nr5gDao.insertNr5gDeviceInfoEntity(nr5gDeviceInfoEntity)
        nr5gDao.insertNr5gLocationEntity(nr5gLocationEntity)
        nr5gDao.insertConnectedWifiStatusEntity(connectedWifiStatusEntity)
        nr5gDao.insertNr5gOEMSVEntity(nr5gOEMSVEntity)
        nr5gDao.insertEndcLteLogEntity(endcLteLogEntity)
        nr5gDao.insertNr5gMmwCellLogEntity(nr5gMmwCellLogEntity)
        nr5gDao.insertEndcUplinkLogEntity(endcUplinkLogEntity)
        nr5gDao.insertNr5gUiLogEntity(nr5gUiLogEntity)
        nr5gDao.insertNr5gStatusEntity(nr5gStatusEntity)
        nr5gDao.insertNr5gNetworkIdentityEntity(nr5gNetworkIdentityEntity)
        nr5gDao.insertNr5gDataNetworkTypeEntity(nr5gDataNetworkTypeEntity)
        nr5gDao.insertNr5gTriggerEntity(nr5gTriggerEntity)
        nr5gDao.insertNr5gWifiStateEntity(nr5gWifiStateEntity)
        nr5gDao.insertNr5gActiveNetworkEntity(nr5gActiveNetworkEntity)



        nr5gReportProcessor.processRawData()
        val reportEntity = nr5gReportProcessor.getNr5gMultiSessionReportEntity(0L, 0L)
        val nr5gMultiSessionReport = nr5gReportProcessor.getNr5gMultiSessionReport(reportEntity)
        val nr5gSingleSessionReportEntityList = nr5gDao.getNr5gSingleSessionReportEntityList()
        val nr5gSingleSessionReportEntity = nr5gSingleSessionReportEntityList.get(0)

        assert(nr5gSingleSessionReportEntityList.isNullOrEmpty())
        Assert.assertNotNull(nr5gSingleSessionReportEntity)
        Assert.assertNotNull(nr5gMultiSessionReport)
    }

    @Test
    fun nr5gReportResponseEventTest() {
        val nr5gModuleProvider = Nr5gModuleProvider.getInstance(context)
        var nr5gReportResponseParameters = DIAReportResponseParameters("", "", "", "")
        nr5gReportProcessor.processRawData()
        val reportEntity = nr5gReportProcessor.getNr5gMultiSessionReportEntity(0L, 0L)
        nr5gReportProcessor.getNr5gMultiSessionReport(reportEntity)

        nr5gModuleProvider.get5gReport(0L, 0L)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                nr5gReportResponseParameters = it.get(0).DIAReportResponseParameters
            }, {
                EchoLocateLog.eLogE("Diagnostic : error in get reports: " + it.printStackTrace())
            })
        Assert.assertNotNull(nr5gReportResponseParameters)
        assert(nr5gReportResponseParameters.requestReportStatus == "Completed")
        assert(nr5gReportResponseParameters.reportType == "ReportNr5g")
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.clearAllTables()
        db.close()
    }

}