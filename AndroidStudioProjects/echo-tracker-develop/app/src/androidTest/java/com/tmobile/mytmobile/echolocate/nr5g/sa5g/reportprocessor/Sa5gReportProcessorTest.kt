import android.content.Context
import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import com.tmobile.mytmobile.echolocate.nr5g.sa5g.database.EchoLocateSa5gDatabase
import com.tmobile.mytmobile.echolocate.nr5g.sa5g.database.dao.Sa5gDao
import com.tmobile.mytmobile.echolocate.nr5g.sa5g.database.entity.*
import com.tmobile.mytmobile.echolocate.nr5g.sa5g.reportprocessor.Sa5gDataStatus
import com.tmobile.mytmobile.echolocate.nr5g.sa5g.reportprocessor.Sa5gReportProcessor
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.io.IOException


class Sa5gReportProcessorTest {

    private lateinit var sa5gDao: Sa5gDao
    private lateinit var db: EchoLocateSa5gDatabase
    private var context: Context? = null
    lateinit var sa5gReportProcessor: Sa5gReportProcessor


    @Before
    fun setUp() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        db = Room.inMemoryDatabaseBuilder(context!!, EchoLocateSa5gDatabase::class.java)
                .allowMainThreadQueries()
                .build()
        sa5gDao = EchoLocateSa5gDatabase.getEchoLocateSa5gDatabase(context!!).sa5gDao()
        sa5gReportProcessor = Sa5gReportProcessor(context!!)
    }


    @Test
    fun processRawDataWhenDataAvailableInDB() {
        sa5gReportProcessor.processRawData()
        val baseEchoLocateSa5gEntityList = sa5gDao.getBaseEchoLocateSa5gEntityByStatus(Sa5gDataStatus.STATUS_RAW)
        assert(baseEchoLocateSa5gEntityList.isNullOrEmpty())
    }

    @Test
    fun processRawDataTest() {
        //common data
        val sessionId = "56aa1c58-6967-4ea4-94cb-90ea37880f08"
        val uniqueId = "94aa1c58-6967-4ea4-94cb-90ea37880f08"
        val timeStamp = "2019-12-19T15:07:58.454-0800"
        val status = ""

//        BaseEchoLocateSa5gEntity
        val baseEchoLocateSa5gEntity = BaseEchoLocateSa5gEntity(
                200,
                status,
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
                1233,
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

        //Sa5gSettingsLogEntity
        val sa5gSettingsLogEntity = Sa5gSettingsLogEntity(
                "wificalling", "wifi", "false", "rtt", "rttTransScript",
                "wifi"
        )

        sa5gSettingsLogEntity.sessionId = sessionId
        sa5gSettingsLogEntity.uniqueId = uniqueId

        //Sa5gTriggerEntity
        val sa5gTriggerEntity = Sa5gTriggerEntity(
                timeStamp,
                200,
                "app",
                2
        )
        sa5gTriggerEntity.sessionId = sessionId
        sa5gTriggerEntity.uniqueId = uniqueId

        //Sa5gUpLinkCarrierLogsEntity
        val upLinkCarrierLogsEntityList = mutableListOf<Sa5gUplinkCarrierLogsEntity>()
        val sa5gUpLinkCarrierLogsEntity = Sa5gUplinkCarrierLogsEntity(
                "techType", "bandNumber", "afcn",
                "5g", "yes"
        )
        sa5gUpLinkCarrierLogsEntity.sessionId = sessionId
        sa5gUpLinkCarrierLogsEntity.uniqueId = uniqueId

        upLinkCarrierLogsEntityList.add(sa5gUpLinkCarrierLogsEntity)

        //Sa5gWiFiStateEntity
        val sa5gWiFiStateEntity = Sa5gWiFiStateEntity(
                3
        )
        sa5gWiFiStateEntity.sessionId = sessionId
        sa5gWiFiStateEntity.uniqueId = uniqueId

        //Sa5gDownlinkCarrierLogsEntity
        val downlinkCarrierLogsEntityList = mutableListOf<Sa5gDownlinkCarrierLogsEntity>()
        val sa5gDownlinkCarrierLogsEntity = Sa5gDownlinkCarrierLogsEntity(
                "techType", "bandNumber", "arfcn", "bandWidth", "isPrimary",
                "isEndcAnchor", "modulationType", "transmissionMode", "numberLayers", "cellId",
                "pci", "tac", "lac", "rsrp", "rsrq", "rssi", "rscp", "sinr", "csiRsrp", "csiRsrq", "csiRssi", "csiSinr"
        )

        sa5gDownlinkCarrierLogsEntity.sessionId = sessionId
        sa5gDownlinkCarrierLogsEntity.uniqueId = uniqueId
        downlinkCarrierLogsEntityList.add(sa5gDownlinkCarrierLogsEntity)


        // Sa5gUiLogEntity
        val sa5gUiLogEntity = Sa5gUiLogEntity("SEARCHING",
                "",
                "3G", "DOWN_ON", "LTE"
        )

        sa5gUiLogEntity.sessionId = sessionId
        sa5gUiLogEntity.uniqueId = uniqueId

       //Sa5gCarrierConfigEntity
       val sa5gCarrierConfigEntity =  Sa5gCarrierConfigEntity(
                "1",
                "SAn2Enabled",
                "true"
        )
        sa5gCarrierConfigEntity.sessionId = sessionId
        sa5gCarrierConfigEntity.uniqueId = uniqueId


//       Insert dummy data to DataBase
//        CoroutineScope(Dispatchers.Default).launch {
        sa5gDao.insertBaseEchoLocateSa5gEntity(baseEchoLocateSa5gEntity)
        sa5gDao.insertSa5gActiveNetworkEntity(sa5gActiveNetworkEntity)
        sa5gDao.insertSa5gDeviceInfoEntity(sa5gDeviceInfoEntity)
        sa5gDao.insertSa5gConnectedWifiStatusEntity(connectedWifiStatusEntity)
        sa5gDao.insertAllSa5gDownlinkCarrierLogsEntity(*downlinkCarrierLogsEntityList.toTypedArray())
        sa5gDao.insertSa5gLocationEntity(sa5gLocationEntity)
        sa5gDao.insertSa5gOEMSVEntity(sa5gOEMSVEntity)
        sa5gDao.insertSa5gNetworkLogEntity(sa5gNetworkLogEntity)
        sa5gDao.insertSa5gRrcLogEntity(sa5gRrcLogEntity)
        sa5gDao.insertSa5gSettingsLogEntity(sa5gSettingsLogEntity)
        sa5gDao.insertAllSa5gSingleSessionReportEntity()
        sa5gDao.insertSa5gTriggerEntity(sa5gTriggerEntity)
        sa5gDao.insertSa5gCarrierConfigEntity(sa5gCarrierConfigEntity)
        sa5gDao.insertSa5gUiLogEntity(sa5gUiLogEntity)
        sa5gDao.insertAllSa5gUplinkCarrierLogsEntity(*upLinkCarrierLogsEntityList.toTypedArray())
        sa5gDao.insertSa5gWiFiStateEntity(sa5gWiFiStateEntity)

        baseEchoLocateSa5gEntity.status = Sa5gDataStatus.STATUS_RAW
        sa5gDao.updateBaseEchoLocateSa5gEntityStatus(baseEchoLocateSa5gEntity)
        sa5gReportProcessor.processRawData()

        val reportEntity = sa5gReportProcessor.getSa5gMultiSessionReportEntity(0L, 0L)
        val sa5gMultiSessionReport = sa5gReportProcessor.getSa5gMultiSessionReport(reportEntity)
        assert(reportEntity.isNullOrEmpty())
        Assert.assertNotNull(sa5gMultiSessionReport)
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
        db.clearAllTables()
    }

}



