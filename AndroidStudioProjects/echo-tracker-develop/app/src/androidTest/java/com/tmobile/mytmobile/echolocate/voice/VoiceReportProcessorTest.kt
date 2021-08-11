package com.tmobile.mytmobile.echolocate.voice

import android.content.Context
import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import com.google.gson.Gson
import com.tmobile.mytmobile.echolocate.configuration.events.reportingevents.DIAReportResponseParameters
import com.tmobile.mytmobile.echolocate.standarddatablocks.DeviceInfo
import com.tmobile.mytmobile.echolocate.utils.EchoLocateLog
import com.tmobile.mytmobile.echolocate.voice.repository.database.EchoLocateVoiceDatabase
import com.tmobile.mytmobile.echolocate.voice.repository.database.dao.VoiceDao
import com.tmobile.mytmobile.echolocate.voice.repository.database.entity.*
import com.tmobile.mytmobile.echolocate.voice.model.NetworkIdentity
import com.tmobile.mytmobile.echolocate.voice.reportprocessor.VoiceReportProcessor
import com.tmobile.mytmobile.echolocate.voice.utils.VoiceDeviceInfoDataCollector
import io.mockk.every
import io.mockk.mockkClass
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.json.JSONObject
import org.junit.*
import java.io.IOException

class VoiceReportProcessorTest {
    private lateinit var voiceDao: VoiceDao
    private lateinit var db: EchoLocateVoiceDatabase
    lateinit var context: Context
    private lateinit var voiceReportProcessor: VoiceReportProcessor
    private lateinit var deviceInfoDataCollector: VoiceDeviceInfoDataCollector

    @Before
    fun setUp() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        db = Room.inMemoryDatabaseBuilder(context, EchoLocateVoiceDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        voiceDao = EchoLocateVoiceDatabase.getEchoLocateVoiceDatabase(context).voiceDao()
        voiceReportProcessor = VoiceReportProcessor.getInstance(context)

        deviceInfoDataCollector = mockkClass(VoiceDeviceInfoDataCollector::class)
        voiceReportProcessor.deviceInfoDataCollector = deviceInfoDataCollector
        every {
            deviceInfoDataCollector.getDeviceInformation(context)
        } returns DeviceInfo("", "", "", "", "")

        InstrumentationRegistry.getInstrumentation().uiAutomation.executeShellCommand(
                "pm grant " + context.packageName
                        + " android.permission.READ_PRIVILEGED_PHONE_STATE"
            )

    }

    @Test
    fun processRawDataTest() { //    java.lang.IllegalStateException: tm.imei must not be null

        val callId = "123567"
        val uniqueId = "321"

        val networkIdentity = NetworkIdentity("", "")
        val status = "RawData"
        val callNumber = ""
        val clientVersion = ""
        val startTime = 100L
        val endTime = 1000L
        val numDiscardedIntents = 0
        val baseEchoLocateVoiceEntity = BaseEchoLocateVoiceEntity(callId, status, callNumber, clientVersion, startTime, endTime, numDiscardedIntents, networkIdentity)

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
        val appTriggeredCallDataEntity = AppTriggeredCallDataEntity(appName, appPackageId, appVersionCode, appVersionName, oemTimestamp, eventTimestamp)
        appTriggeredCallDataEntity.callId = callId
        appTriggeredCallDataEntity.uniqueId = uniqueId

        //CallSettingDataEntity
        val volteStatus = "UNREGISTERED"
        val wfcStatus = "REGISTERED"
        val wfcPreference = "WIFIPREFFERED"
        //oemTimestamp
        //eventTimestamp
        val callSettingDataEntity = CallSettingDataEntity(volteStatus, wfcStatus, wfcPreference, oemTimestamp, eventTimestamp)
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
        val networkBand  = ""
        val cellId = "24961293"
        val networkType = "WFC2"
        val cellInfoEntity = CellInfoEntity(ecio, rscp, rsrp, rsrq, rssi, sinr, snr, lac, networkBand, cellId, networkType)
        cellInfoEntity.callId = callId
        cellInfoEntity.uniqueId = uniqueId

        //DetailedCallStateEntity
        val callCode = "NA"
        val callState = "ATTEMPTING"
        //oemTimestamp
        //eventTimestamp
        val detailedCallStateEntity = DetailedCallStateEntity(callCode, callState, oemTimestamp, eventTimestamp)
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
        val sipLine1 = "INVITE sip:9174155811;phone-context=msg.pc.t-mobile.com@msg.pc.t-mobile.com;user=phone SIP/2.0"
        val sipOrigin = "SENT"
        val sipReason = "NA"
        val sipSDP = ";\"c=IN IP6 2607:fc20:82a8:5dfb:0:4a:7445:3101\";\"a=rtpmap:127 EVS/16000\";\"a=rtpmap:114 AMR-WB/16000/1\";\"a=rtpmap:113 AMR-WB/16000/1\";\"a=rtpmap:102 AMR/8000/1\";\"a=rtpmap:115 AMR/8000/1\";\"a=rtpmap:105 telephone-event/16000\";\"a=rtpmap:101 telephone-event/8000\";\"a=sendrecv\";"
        //oemTimestamp
        //eventTimestamp
        val imsSignallingEntity = ImsSignallingEntity(sipCallId, sipCseq, sipLine1, sipOrigin, sipReason, sipSDP, oemTimestamp, eventTimestamp)
        imsSignallingEntity.callId = callId
        imsSignallingEntity.uniqueId = uniqueId

        //OEMSoftwareVersionEntity
        val softwareVersion = "01"
        val customVersion = ""
        val radioVersion = ""
        val buildName = ""
        val androidVersion = ""
        val oEMSoftwareVersionEntity = OEMSoftwareVersionEntity(softwareVersion, customVersion, radioVersion, buildName, androidVersion)
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
        val rtpdlStateEntity = RtpdlStateEntity(delay, sequence, jitter, lossRate, measuredPeriod, oemTimestamp, eventTimestamp)
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
        val viceLocationEntity = VoiceLocationEntity(altitude, altitudePrecision, latitude, longitude, precision, locationAge)
        viceLocationEntity.callId = callId
        viceLocationEntity.uniqueId = uniqueId

        //VoiceReportEntity
        val voiceReportId = "123567"
        val json = "{\"callId\":\"123567\",\"callId\":\"123567\",\"OEMSV\":{\"softwareVersion\":\"01\"},\"DeviceIntents\":{\"appTriggeredCallData\":[]}}"
        val voiceReportEntity = VoiceReportEntity(voiceReportId, json, numDiscardedIntents, startTime, endTime, eventTimestamp, "")

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
        voiceDao.insertAllVoiceReportEntity(voiceReportEntity)

        voiceReportProcessor.processRawData()
        val reportEntity = voiceReportProcessor.getVoiceReportEntity(0L, 0L)
        val reportEntity2 = voiceReportProcessor.getVoiceReportEntity(101L, 109L)
        val voiceData = voiceReportProcessor.getVoiceSingleSessionReport(reportEntity)
        val voiceData2 = voiceReportProcessor.getVoiceSingleSessionReport(reportEntity2)
        val gson = Gson()
        val jsonData = gson.toJson(voiceData)
        val jsonObject = JSONObject(jsonData)
        val voiceReportEntitys = voiceDao.getVoiceReportEntity()
        val voiceReportEntityItem = voiceReportEntitys[0]

        assert(voiceReportEntitys.isNullOrEmpty())
        Assert.assertNotNull(voiceReportEntityItem)
        Assert.assertNotNull(voiceData)

        assert(voiceData.numDiscardedIntents == 0)
        assert(voiceData.schemaVersion == "1")
        assert(voiceData.callSessions.isNullOrEmpty())
        assert(voiceData2.callSessions.isNullOrEmpty())
        var callSession = voiceData.callSessions[0]
        assert(callSession.callId == "123567")
        Assert.assertNotNull(callSession.OEMSV)
        assert(callSession.OEMSV.softwareVersion == "c")


        val voiceDataTimeCheck = voiceDao.getVoiceReportEntity(101L, 109L)
        val baseEchoLocataVoiceEntities = voiceDao.getAllBaseEchoLocateVoiceEntity()
        val baseEchoLocateVoiceEntity2 = baseEchoLocataVoiceEntities[0]

        assert(baseEchoLocataVoiceEntities.isNullOrEmpty())
        Assert.assertNotNull(baseEchoLocateVoiceEntity2)
        Assert.assertNotNull(voiceData)
        Assert.assertNotNull(voiceDataTimeCheck)

        assert(voiceData.numDiscardedIntents == 0)
        assert(voiceData.schemaVersion == "1")
        assert(voiceData.callSessions.isNullOrEmpty())
        callSession = voiceData.callSessions[0]
        assert(callSession.callId == "123567")
        Assert.assertNotNull(callSession.OEMSV)
        assert(callSession.OEMSV.softwareVersion == "01")
        //Assert.assertNotNull(callSession.deviceIntents.appTriggeredCallData) Data not inserted
    }

    @Test
    fun voiceReportResponseEventTest() {
        val voiceModuleProvider = VoiceModuleProvider.getInstance(context)
        var voiceReportResponseParameters = DIAReportResponseParameters("", "", "", "")
        voiceReportProcessor.processRawData()
        val reportEntity = voiceReportProcessor.getVoiceReportEntity(0L, 0L)
        voiceReportProcessor.getVoiceSingleSessionReport(reportEntity)

        voiceModuleProvider.getVoiceReport(0L, 0L)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                voiceReportResponseParameters = it[0].DIAReportResponseParameters
            }, {
                EchoLocateLog.eLogE("error in get reports: "+it.printStackTrace())
            })
        Assert.assertNotNull(voiceReportResponseParameters)
        assert(voiceReportResponseParameters.requestReportStatus == "Completed")
        assert(voiceReportResponseParameters.reportType == "ReportVoice")

    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.clearAllTables()
        db.close()
    }

    @Ignore
    @Test
    fun voiceISVoiceReady() {
        // TODO:: Ignoring this test as we are unable to mock the configuration in the VoiceModuleProvider.
        val voiceModuleProvider = VoiceModuleProvider.getInstance(context)
        voiceModuleProvider.initVoiceModule(context)
        Assert.assertFalse(voiceModuleProvider.isVoiceModuleReady())
    }


}