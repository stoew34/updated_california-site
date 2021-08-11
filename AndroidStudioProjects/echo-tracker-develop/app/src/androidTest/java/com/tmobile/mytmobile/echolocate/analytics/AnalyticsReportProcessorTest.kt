package com.tmobile.mytmobile.echolocate.analytics

import android.Manifest
import android.content.Context
import android.os.Build
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.tmobile.mytmobile.echolocate.analytics.database.EchoLocateAnalyticsDatabase
import com.tmobile.mytmobile.echolocate.analytics.reportprocessor.AnalyticsReportProcessor
import com.tmobile.mytmobile.echolocate.analytics.utils.AnalyticsDeviceInfoDataCollector
import com.tmobile.mytmobile.echolocate.lte.database.EchoLocateLteDatabase
import com.tmobile.mytmobile.echolocate.lte.database.dao.LteDao
import com.tmobile.mytmobile.echolocate.lte.database.entity.*
import com.tmobile.mytmobile.echolocate.lte.database.entity.NetworkIdentityEntity
import com.tmobile.mytmobile.echolocate.lte.reportprocessor.LteReportProcessor
import com.tmobile.mytmobile.echolocate.nr5g.nsa5g.database.EchoLocateNr5gDatabase
import com.tmobile.mytmobile.echolocate.nr5g.nsa5g.database.dao.Nr5gDao
import com.tmobile.mytmobile.echolocate.nr5g.nsa5g.database.entity.*
import com.tmobile.mytmobile.echolocate.nr5g.nsa5g.reportprocessor.Nsa5gReportProcessor
import com.tmobile.mytmobile.echolocate.standarddatablocks.DeviceInfo
import com.tmobile.mytmobile.echolocate.voice.repository.database.EchoLocateVoiceDatabase
import com.tmobile.mytmobile.echolocate.voice.repository.database.dao.VoiceDao
import com.tmobile.mytmobile.echolocate.voice.repository.database.entity.*
import com.tmobile.mytmobile.echolocate.voice.model.NetworkIdentity
import com.tmobile.mytmobile.echolocate.voice.reportprocessor.VoiceReportProcessor
import io.mockk.every
import io.mockk.mockkClass
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

/**
 * This class is responsible to prepare the data for voice,Lte and Nr5g modules and processes the analytics data into db
 */
@RunWith(AndroidJUnit4::class)
class AnalyticsReportProcessorTest {


    lateinit var context: Context
    private lateinit var lteDao: LteDao
    private lateinit var voiceDao: VoiceDao
    private lateinit var nr5gDao: Nr5gDao
    private lateinit var db: EchoLocateAnalyticsDatabase
    private lateinit var analyticsReportProcessor: AnalyticsReportProcessor
    private lateinit var lteReportProcessor: LteReportProcessor
    private lateinit var voiceReportProcessor: VoiceReportProcessor
    private lateinit var nr5gReportProcessor: Nsa5gReportProcessor
    private lateinit var deviceInfoDataCollector: AnalyticsDeviceInfoDataCollector

    @Before
    fun setUp() {
        context = InstrumentationRegistry.getInstrumentation().targetContext

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            InstrumentationRegistry.getInstrumentation().uiAutomation
                .grantRuntimePermission(
                    context.packageName,
                    Manifest.permission.READ_PHONE_STATE
                )
        } else {
            InstrumentationRegistry.getInstrumentation().uiAutomation.executeShellCommand(
                "pm grant " + context.packageName
                        + " android.permission.READ_PHONE_STATE"
            )
        }
        db = Room.inMemoryDatabaseBuilder(context, EchoLocateAnalyticsDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        voiceDao = EchoLocateVoiceDatabase.getEchoLocateVoiceDatabase(context).voiceDao()
        analyticsReportProcessor = AnalyticsReportProcessor.getInstance(context)
        deviceInfoDataCollector = mockkClass(AnalyticsDeviceInfoDataCollector::class)
        analyticsReportProcessor.deviceInfoDataCollector = deviceInfoDataCollector
        every {
            deviceInfoDataCollector.getDeviceInformation(context)
        } returns DeviceInfo("", "", "", "", "")
        lteDao = EchoLocateLteDatabase.getEchoLocateLteDatabase(context).lteDao()
        lteReportProcessor = LteReportProcessor.getInstance(context)
        voiceReportProcessor = VoiceReportProcessor.getInstance(context)
        nr5gDao = EchoLocateNr5gDatabase.getEchoLocateNr5gDatabase(context).nr5gDao()
        nr5gReportProcessor = Nsa5gReportProcessor.getInstance(context)

    }

    @Test
    fun processAnalyticsData() {
        prepareVoiceData()
        prepareLteRawData()
        prepareNr5gData()
        analyticsReportProcessor.getAnalyticsReport()
    }

    private fun prepareLteRawData() {
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
        val latch = CountDownLatch(1)
        latch.await(1, TimeUnit.SECONDS)
        lteReportProcessor.processRawData()
    }


    private fun prepareVoiceData() {
        val callId = "123567"
        val uniqueId = "321"

        val networkIdentity = NetworkIdentity("", "")
        val status = "RawData"
        val callNumber = ""
        val clientVersion = ""
        val startTime = 100L
        val endTime = 1000L
        val numDiscardedIntents = 0
        val baseEchoLocateVoiceEntity = BaseEchoLocateVoiceEntity(
            callId,
            status,
            callNumber,
            clientVersion,
            startTime,
            endTime,
            numDiscardedIntents,
            networkIdentity
        )

        //common data
        val oemTimestamp = "1570746592195"
        val eventTimestamp = "1570746592195"

        //appTriggeredCallDataEntity
        val appName = "Phone"
        val appPackageId = "com.android.phone"
        val appVersionCode = "1"
        val appVersionName = "1.0.0"
        //oemTimestamp
        //eventTimestamp
        val appTriggeredCallDataEntity = AppTriggeredCallDataEntity(
            appName,
            appPackageId,
            appVersionCode,
            appVersionName,
            oemTimestamp,
            eventTimestamp
        )
        appTriggeredCallDataEntity.callId = callId
        appTriggeredCallDataEntity.uniqueId = uniqueId

        //CallSettingDataEntity
        val volteStatus = "UNREGISTERED"
        val wfcStatus = "REGISTERED"
        val wfcPreference = "WIFIPREFFERED"
        //oemTimestamp
        //eventTimestamp
        val callSettingDataEntity = CallSettingDataEntity(
            volteStatus,
            wfcStatus,
            wfcPreference,
            oemTimestamp,
            eventTimestamp
        )
        callSettingDataEntity.callId = callId
        callSettingDataEntity.uniqueId = uniqueId

        //CellInfoEntity
        val ecio = ""
        val rscp = ""
        val rsrp = ""
        val rsrq = ""
        val rssi = ""
        val sinr = ""
        val snr = ""
        val lac = ""
        val networkBand = ""
        val cellId = "24961293"
        val networkType = "WFC2"
        val cellInfoEntity = CellInfoEntity(
            ecio,
            rscp,
            rsrp,
            rsrq,
            rssi,
            sinr,
            snr,
            lac,
            networkBand,
            cellId,
            networkType
        )
        cellInfoEntity.callId = callId
        cellInfoEntity.uniqueId = uniqueId

        //DetailedCallStateEntity
        val callCode = "NA"
        val callState = "ATTEMPTING"
        //oemTimestamp
        //eventTimestamp
        val detailedCallStateEntity =
            DetailedCallStateEntity(callCode, callState, oemTimestamp, eventTimestamp)
        detailedCallStateEntity.callId = callId
        detailedCallStateEntity.uniqueId = uniqueId

        //DeviceInfoEntity
        val uuid = ""
        val imei = ""
        val imsi = ""
        val msisdn = ""
        val testSessionID = ""
        val deviceInfoEntity = DeviceInfoEntity(uuid, imei, imsi, msisdn, testSessionID)
        deviceInfoEntity.callId = callId
        deviceInfoEntity.uniqueId = uniqueId

        //ImsSignallingEntity
        val sipCallId = "iyydYbN77oc6esu315KRqA..@2607:fc20:82a8:5dfb:0:4a:7445:3101"
        val sipCseq = "CSeq: 1 INVITE"
        val sipLine1 =
            "INVITE sip:9174155811;phone-context=msg.pc.t-mobile.com@msg.pc.t-mobile.com;user=phone SIP/2.0"
        val sipOrigin = "SENT"
        val sipReason = "NA"
        val sipSDP =
            ";\"c=IN IP6 2607:fc20:82a8:5dfb:0:4a:7445:3101\";\"a=rtpmap:127 EVS/16000\";\"a=rtpmap:114 AMR-WB/16000/1\";\"a=rtpmap:113 AMR-WB/16000/1\";\"a=rtpmap:102 AMR/8000/1\";\"a=rtpmap:115 AMR/8000/1\";\"a=rtpmap:105 telephone-event/16000\";\"a=rtpmap:101 telephone-event/8000\";\"a=sendrecv\";"
        //oemTimestamp
        //eventTimestamp
        val imsSignallingEntity = ImsSignallingEntity(
            sipCallId,
            sipCseq,
            sipLine1,
            sipOrigin,
            sipReason,
            sipSDP,
            oemTimestamp,
            eventTimestamp
        )
        imsSignallingEntity.callId = callId
        imsSignallingEntity.uniqueId = uniqueId

        //OEMSoftwareVersionEntity
        val softwareVersion = "01"
        val customVersion = ""
        val radioVersion = ""
        val buildName = ""
        val androidVersion = ""
        val oEMSoftwareVersionEntity = OEMSoftwareVersionEntity(
            softwareVersion,
            customVersion,
            radioVersion,
            buildName,
            androidVersion
        )
        oEMSoftwareVersionEntity.callId = callId
        oEMSoftwareVersionEntity.uniqueId = uniqueId

        //RadioHandoverEntity
        val handoverState = ""
        //oemTimestamp
        //eventTimestamp
        val radioHandoverEntity = RadioHandoverEntity(handoverState, oemTimestamp, eventTimestamp)
        radioHandoverEntity.callId = callId
        radioHandoverEntity.uniqueId = uniqueId

        //RtpdlStateEntity
        val delay = 0.0
        val sequence = 0.0
        val jitter = 0.0
        val lossRate = 64.67
        val measuredPeriod = 5000.0
        //oemTimestamp
        //eventTimestamp
        val rtpdlStateEntity = RtpdlStateEntity(
            delay,
            sequence,
            jitter,
            lossRate,
            measuredPeriod,
            oemTimestamp,
            eventTimestamp
        )
        rtpdlStateEntity.callId = callId
        rtpdlStateEntity.uniqueId = uniqueId

        //UiCallStateEntity

        val uICallState = "CALL_PRESSED"
        //oemTimestamp
        //eventTimestamp
        val uiCallStateEntity = UiCallStateEntity(uICallState, oemTimestamp, eventTimestamp)
        uiCallStateEntity.callId = callId
        uiCallStateEntity.uniqueId = uniqueId

        //VoiceLocationEntity
        val altitude = -122.1650322
        val altitudePrecision = 20.433000564575195F
        val latitude = 47.5778284
        val locationAge = 0L
        val longitude = -122.1650322
        val precision = 20.433000564575195F
        val viceLocationEntity = VoiceLocationEntity(
            altitude,
            altitudePrecision,
            latitude,
            longitude,
            precision,
            locationAge
        )
        viceLocationEntity.callId = callId
        viceLocationEntity.uniqueId = uniqueId

        //NetworkIdintityEntity

        voiceDao.insertBaseEchoLocateVoiceEntity(baseEchoLocateVoiceEntity)
        voiceDao.insertAppTriggeredCallDataEntity(appTriggeredCallDataEntity)
        voiceDao.insertCallSettingEntity(callSettingDataEntity)
        voiceDao.insertCellInfoEntity(cellInfoEntity)
        voiceDao.insertDetialedCallStateEntity(detailedCallStateEntity)
//        voiceDao.insertDeviceInfoEntity(deviceInfoEntity)
        voiceDao.insertImsSignallingEntity(imsSignallingEntity)
        voiceDao.insertOEMSoftwareVersionEntity(oEMSoftwareVersionEntity)
        voiceDao.insertRadioHandOverEntity(radioHandoverEntity)
        voiceDao.insertRtpdlStateEntity(rtpdlStateEntity)
        voiceDao.insertUiCallStateEntity(uiCallStateEntity)
        voiceDao.insertLocationEntity(viceLocationEntity)
        val latch = CountDownLatch(1)
        latch.await(1, TimeUnit.SECONDS)
        voiceReportProcessor.processRawData()
    }

    private fun prepareNr5gData() {
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
//        nr5gDao.insertNr5gDeviceInfoEntity(nr5gDeviceInfoEntity)
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
        val latch = CountDownLatch(1)
        latch.await(1, TimeUnit.SECONDS)
        nr5gReportProcessor.processRawData()
    }
}