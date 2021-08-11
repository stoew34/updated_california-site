package com.tmobile.mytmobile.echolocate.voice

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import com.tmobile.mytmobile.echolocate.TestVoiceReceiver
import com.tmobile.mytmobile.echolocate.voice.repository.database.EchoLocateVoiceDatabase
import com.tmobile.mytmobile.echolocate.voice.repository.database.dao.VoiceDao
import com.tmobile.mytmobile.echolocate.voice.manager.VoiceDataManager
import com.tmobile.mytmobile.echolocate.voice.utils.VoiceIntents
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.io.IOException
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class VoiceModuleIntentsTest {

    private lateinit var mEchoLocateVoiceDatabase : EchoLocateVoiceDatabase
    private lateinit var mVoiceDao: VoiceDao
    private lateinit var mContext: Context
    private lateinit var mVoiceDataManager: VoiceDataManager
    private var mIntentFilter = IntentFilter()
    private val CALL_NUMBER_EXTRA = "CallNumber"
    private var CALL_ID = ""
    private lateinit var mTestReceiver: TestVoiceReceiver

    @Before
    fun createDb() {

        mContext = InstrumentationRegistry.getInstrumentation().targetContext

        mEchoLocateVoiceDatabase = Room.inMemoryDatabaseBuilder(mContext, EchoLocateVoiceDatabase::class.java)
            .allowMainThreadQueries()
            .build()

        mVoiceDao = EchoLocateVoiceDatabase.getEchoLocateVoiceDatabase(mContext).voiceDao()
        mVoiceDataManager = VoiceDataManager(mContext)
        mVoiceDataManager.initVoiceDataManager()

        mIntentFilter.addAction(VoiceIntents.DETAILED_CALL_STATE)
        mIntentFilter.addAction(VoiceIntents.CALL_SETTING)
        mIntentFilter.addAction(VoiceIntents.UI_CALL_STATE)
        mIntentFilter.addAction(VoiceIntents.RADIO_HAND_OVER_STATE)
        mIntentFilter.addAction(VoiceIntents.RTPDL_STAT)
        mIntentFilter.addAction(VoiceIntents.IMS_SIGNALING_MESSAGE)
        mIntentFilter.addAction(VoiceIntents.APP_TRIGGERED_CALL)

        mTestReceiver = TestVoiceReceiver()
        LocalBroadcastManager.getInstance(mContext).registerReceiver(mTestReceiver, mIntentFilter)

        mVoiceDataManager = VoiceDataManager(mContext)
        mVoiceDataManager.initVoiceDataManager()
        mTestReceiver.baseVoiceBroadcastReceiver.setListener(mVoiceDataManager)
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        mEchoLocateVoiceDatabase.clearAllTables()
        mEchoLocateVoiceDatabase.close()
    }

    @Test
    fun testDetailedCallState() {
        val latch = CountDownLatch(1)
        val timestamp = System.nanoTime()
        CALL_ID = timestamp.toString()
        val detailedCallStateIntent = prepareDetailedCallStateData(timestamp)
        val status = LocalBroadcastManager.getInstance(mContext).sendBroadcast(detailedCallStateIntent)
        Assert.assertTrue(status)
        latch.await(700, TimeUnit.MILLISECONDS)
        val callIdList = mVoiceDao.getSessionIdList()
        Assert.assertTrue(callIdList.contains(CALL_ID))
        assert(!mVoiceDao.getDetailedCallStateDataEntity(CALL_ID).isNullOrEmpty())
    }

    /**
     * Prepare dummy details call state intent data.
     */
    private fun prepareDetailedCallStateData(timestamp: Long): Intent {
        val CALL_CODE_EXTRA = "CallCode"
        val CALL_STATE_EXTRA = "CallState"
        val CALL_ID_EXTRA = "CallID"
        val OEM_TIMESTAMP_EXTRA = "oemIntentTimestamp"
        val intent = getIntentExtras(Intent(VoiceIntents.DETAILED_CALL_STATE))
        intent.putExtra(CALL_CODE_EXTRA, "${timestamp}-CALLCODE")
        intent.putExtra(CALL_STATE_EXTRA, "${timestamp}-CALLSTATE")
        intent.putExtra(CALL_ID_EXTRA, CALL_ID)
        intent.putExtra(CALL_NUMBER_EXTRA, "8998098088")
        intent.putExtra(OEM_TIMESTAMP_EXTRA, timestamp.toString())
        return intent
    }

    @Test
    fun testCallSettingDataIntent() {
        val latch = CountDownLatch(1)
        val timestamp = System.nanoTime()
        CALL_ID = timestamp.toString()
        val callSettingsIntent = prepareCallSettingIntent(timestamp)
        val status = LocalBroadcastManager.getInstance(mContext).sendBroadcast(callSettingsIntent)
        Assert.assertTrue(status)
        latch.await(700, TimeUnit.MILLISECONDS)
        val callIdList = mVoiceDao.getSessionIdList()
        Assert.assertTrue(callIdList.contains(CALL_ID))
        assert(!mVoiceDao.getDetailedCallStateDataEntity(CALL_ID).isNullOrEmpty())
    }

    /**
     * Prepare dummy call settings data intent data.
     */
    private fun prepareCallSettingIntent(timestamp: Long): Intent {
        val CALL_SETTING_VO_LTE = "CallSettingVoLTE"
        val CALL_SETTING_WFC = "CallSettingWFC"
        val CALL_SETTING_WFC_PREFERENCE = "CallSettingWFCPreference"
        val CALL_ID_EXTRA = "CallID"
        val OEM_TIMESTAMP_EXTRA = "oemIntentTimestamp"
        val intent = getIntentExtras(Intent(VoiceIntents.CALL_SETTING))
        intent.putExtra(CALL_SETTING_VO_LTE, "CALLSETTINGVOLTE-${timestamp}")
        intent.putExtra(CALL_SETTING_WFC, "CALLSETTINGWFC-${timestamp}")
        intent.putExtra(CALL_SETTING_WFC_PREFERENCE, "CALLSETTINGWFCPREFERENCE-${timestamp}")
        intent.putExtra(OEM_TIMESTAMP_EXTRA, timestamp.toString())
        intent.putExtra(CALL_ID_EXTRA, CALL_ID)
        intent.putExtra(CALL_NUMBER_EXTRA, "8998098088")
        return intent
    }

    @Test
    fun testUiCallStateIntent() {
        val latch = CountDownLatch(1)
        val timestamp = System.nanoTime()
        CALL_ID = timestamp.toString()
        val uiCallStateIntent = prepareUiCallState(timestamp)
        val status = LocalBroadcastManager.getInstance(mContext).sendBroadcast(uiCallStateIntent)
        Assert.assertTrue(status)
        latch.await(700, TimeUnit.MILLISECONDS)
        val callIdList = mVoiceDao.getSessionIdList()
        Assert.assertTrue(callIdList.contains(CALL_ID))
        assert(!mVoiceDao.getDetailedCallStateDataEntity(CALL_ID).isNullOrEmpty())
    }

    /**
     * Prepare dummy UI call state intent data.
     */
    private fun prepareUiCallState(timestamp: Long): Intent {
        val CALL_STATE_EXTRA = "UICallState"
        val CALL_ID_EXTRA = "CallID"
        val OEM_TIMESTAMP_EXTRA = "oemIntentTimestamp"
        val intent = getIntentExtras(Intent(VoiceIntents.UI_CALL_STATE))
        intent.putExtra(CALL_STATE_EXTRA, "${timestamp}-CALL_PRESSED")
        intent.putExtra(CALL_ID_EXTRA, CALL_ID)
        intent.putExtra(CALL_NUMBER_EXTRA, "8998098088")
        intent.putExtra(OEM_TIMESTAMP_EXTRA, timestamp.toString())
        return intent
    }

    @Test
    fun testRadioHandOverStateIntent() {
        val latch = CountDownLatch(1)
        val timestamp = System.nanoTime()
        CALL_ID = timestamp.toString()
        val radioHandOverStateIntent = prepareRadioHandOverState(timestamp)
        val status = LocalBroadcastManager.getInstance(mContext).sendBroadcast(radioHandOverStateIntent)
        Assert.assertTrue(status)
        latch.await(700, TimeUnit.MILLISECONDS)
        val callIdList = mVoiceDao.getSessionIdList()
        Assert.assertTrue(callIdList.contains(CALL_ID))
        assert(!mVoiceDao.getDetailedCallStateDataEntity(CALL_ID).isNullOrEmpty())
    }

    /**
     * Prepare dummy radio hand over state intent data.
     */
    private fun prepareRadioHandOverState(timestamp: Long): Intent {
        val HAND_OVER_STATE = "VoiceRadioBearerHandoverState"
        val CALL_ID_EXTRA = "CallID"
        val OEM_TIMESTAMP_EXTRA = "oemIntentTimestamp"
        val intent = getIntentExtras(Intent(VoiceIntents.RADIO_HAND_OVER_STATE))
        intent.putExtra(HAND_OVER_STATE, "${timestamp}-handoverState")
        intent.putExtra(CALL_ID_EXTRA, CALL_ID)
        intent.putExtra(CALL_NUMBER_EXTRA, "8998098088")
        intent.putExtra(OEM_TIMESTAMP_EXTRA, timestamp.toString())
        return intent
    }

    @Test
    fun testRTPDLstatIntent() {
        val latch = CountDownLatch(1)
        val timestamp = System.nanoTime()
        CALL_ID = timestamp.toString()
        val RTPDLstatIntent = prepareRTPDLstat()
        val status = LocalBroadcastManager.getInstance(mContext).sendBroadcast(RTPDLstatIntent)
        Assert.assertTrue(status)
        latch.await(700, TimeUnit.MILLISECONDS)
        val callIdList = mVoiceDao.getSessionIdList()
        Assert.assertTrue(callIdList.contains(CALL_ID))
        assert(!mVoiceDao.getDetailedCallStateDataEntity(CALL_ID).isNullOrEmpty())
    }

    /**
     * Prepare RTPDLstat intent data.
     */
    private fun prepareRTPDLstat(): Intent {
        val time = System.nanoTime()
        val OEM_SEQUENCE_EXTRA =
            "RTPDownlinkStatusSequence"               // intent key may be "sequence"
        val OEM_DELAY_EXTRA = "RTPDownlinkStatusDelay"                 // intent key may be "delay"
        val OEM_JITTER_EXTRA =
            "RTPDownlinkStatusJitter"                   // intent key may be "jitter"
        val OEM_LOSS_RATE_EXTRA =
            "RTPDownlinkStatusLossRate"             // intent key may be "lossRate"
        val OEM_MEASURED_PERIOD_EXTRA =
            "RTPDownlinkStatusMeasuredPeriod"  // intent key may be "measuredPeriod"
        val CALL_ID_EXTRA = "CallID"
        val OEM_TIMESTAMP_EXTRA = "oemIntentTimestamp"
        val intent = getIntentExtras(Intent(VoiceIntents.RTPDL_STAT))
        intent.putExtra(OEM_SEQUENCE_EXTRA, "0.0")
        intent.putExtra(OEM_DELAY_EXTRA, "0.0")
        intent.putExtra(OEM_JITTER_EXTRA, "0.0")
        intent.putExtra(OEM_LOSS_RATE_EXTRA, "0.0")
        intent.putExtra(OEM_MEASURED_PERIOD_EXTRA, "0.0")
        intent.putExtra(CALL_ID_EXTRA, CALL_ID)
        intent.putExtra(CALL_NUMBER_EXTRA, "8998098088")
        intent.putExtra(OEM_TIMESTAMP_EXTRA, time.toString())
        return intent
    }

    @Test
    fun testImsSignallingMessageIntent() {
        val latch = CountDownLatch(1)
        val timestamp = System.nanoTime()
        CALL_ID = timestamp.toString()
        val imsSignallingMessageIntent = prepareImsSignallingMessage(timestamp)
        val status = LocalBroadcastManager.getInstance(mContext).sendBroadcast(imsSignallingMessageIntent)
        Assert.assertTrue(status)
        latch.await(700, TimeUnit.MILLISECONDS)
        val callIdList = mVoiceDao.getSessionIdList()
        Assert.assertTrue(callIdList.contains(CALL_ID))
        assert(!mVoiceDao.getDetailedCallStateDataEntity(CALL_ID).isNullOrEmpty())
    }

    /**
     * Prepare ims signaling message intent data.
     */
    private fun prepareImsSignallingMessage(timestamp: Long): Intent {
        val CALL_ID_EXTRA = "CallID"
        val SIP_CALL_ID = "IMSSignallingMessageCallID"
        val SIP_CSEQ = "IMSSignallingCSeq"
        val SIP_LINE1 = "IMSSignallingMessageLine1"
        val SIP_ORIGIN = "IMSSignallingMessageOrigin"
        val SIP_REASON = "IMSSignallingMessageReason"
        val SIP_SDP = "IMSSignallingMessageSDP"
        val OEM_TIMESTAMP_EXTRA = "oemIntentTimestamp"
        val intent = getIntentExtras(Intent(VoiceIntents.IMS_SIGNALING_MESSAGE))
        intent.putExtra(CALL_ID_EXTRA, CALL_ID)
        intent.putExtra(SIP_CALL_ID, "${timestamp}-sipCallId")
        intent.putExtra(SIP_CSEQ, "${timestamp}-sipCSeq")
        intent.putExtra(SIP_LINE1, "${timestamp}-sipLine1")
        intent.putExtra(SIP_ORIGIN, "${timestamp}-sipOrigin")
        intent.putExtra(SIP_REASON, "${timestamp}-sipReason")
        intent.putExtra(SIP_SDP, "${timestamp}-sipSdp")
        intent.putExtra(CALL_NUMBER_EXTRA, "8998098088")
        intent.putExtra(OEM_TIMESTAMP_EXTRA, timestamp.toString())
        return intent
    }

    @Test
    fun testAppTriggeredIntent() {
        val latch = CountDownLatch(1)
        val timestamp = System.nanoTime()
        CALL_ID = timestamp.toString()
        val appTriggeredIntent = prepareAppTriggeredCall(timestamp)
        val status = LocalBroadcastManager.getInstance(mContext).sendBroadcast(appTriggeredIntent)
        Assert.assertTrue(status)
        latch.await(700, TimeUnit.MILLISECONDS)
        val callIdList = mVoiceDao.getSessionIdList()
        Assert.assertTrue(callIdList.contains(CALL_ID))
        assert(!mVoiceDao.getDetailedCallStateDataEntity(CALL_ID).isNullOrEmpty())
    }

    /**
     * Prepare app triggered call intent data.
     */
    private fun prepareAppTriggeredCall(timestamp: Long): Intent {
        val CALL_ID_EXTRA = "CallID"
        val PACKAGE_NAME_EXTRA = "ApplicationPackageName"
        val OEM_TIMESTAMP_EXTRA = "oemIntentTimestamp"
        val intent = getIntentExtras(Intent(VoiceIntents.APP_TRIGGERED_CALL))
        intent.putExtra(CALL_ID_EXTRA, CALL_ID)
        intent.putExtra(PACKAGE_NAME_EXTRA, "${timestamp}-com.tmobile.pr")
        intent.putExtra(CALL_NUMBER_EXTRA, "8998098088")
        intent.putExtra(OEM_TIMESTAMP_EXTRA, timestamp.toString())
        return intent
    }

    /**
     * Prepare default values for all the intents.
     */
    private fun getIntentExtras(intent: Intent): Intent {
        val time = System.nanoTime()
        val ECIO_EXTRA = "ECIO-${time}"
        val RSCP_EXTRA = "RSCP-${time}"
        val RSRP_EXTRA = "RSRP-${time}"
        val RSRQ_EXTRA = "RSRQ-${time}"
        val RSSI_EXTRA = "RSSI-${time}"
        val SINR_EXTRA = "SINR-${time}"
        val SNR_EXTRA = "SNR-${time}"
        val LAC_EXTRA = "LAC-${time}"
        val NETWORKBAND_EXTRA = "networkBand-${time}"
        val NETWORKTYPE_EXTRA = "networkType-${time}"
        val CALL_NUMBER_EXTRA = "CallNumber-${time}"

        intent.putExtra(ECIO_EXTRA, "ecio-${time}")
        intent.putExtra(RSCP_EXTRA, "RSCP-${time}")
        intent.putExtra(RSRP_EXTRA, "RSRP-${time}")
        intent.putExtra(RSRQ_EXTRA, "RSRQ-${time}")
        intent.putExtra(RSSI_EXTRA, "RSSI-${time}")
        intent.putExtra(SINR_EXTRA, "SINR-${time}")
        intent.putExtra(SNR_EXTRA, "SNR-${time}")
        intent.putExtra(LAC_EXTRA, "LAC-${time}")

        intent.putExtra(NETWORKBAND_EXTRA, "NETWORKBAND-${time}")
        intent.putExtra(NETWORKTYPE_EXTRA, "NWTYPE-${time}")
        intent.putExtra(CALL_NUMBER_EXTRA, "CALLNUMBER-${time}")
        return intent
    }
}