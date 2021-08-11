package com.tmobile.mytmobile.echolocate.voice.intentlisteners

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.test.platform.app.InstrumentationRegistry
import com.tmobile.mytmobile.echolocate.TestVoiceReceiver
import com.tmobile.mytmobile.echolocate.voice.manager.VoiceDataManager
import com.tmobile.mytmobile.echolocate.voice.utils.VoiceIntents
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class AppTriggeredCallTest {

    lateinit var instrumentationContext: Context
    private lateinit var testReceiver: TestVoiceReceiver

    @Before
    fun setUp() {
        instrumentationContext = InstrumentationRegistry.getInstrumentation().targetContext
        testReceiver = TestVoiceReceiver()
        LocalBroadcastManager.getInstance(instrumentationContext).registerReceiver(testReceiver,
            IntentFilter(VoiceIntents.APP_TRIGGERED_CALL))
    }

    @Test
    fun testSendBroadCast() {
        val ECIO_EXTRA = "ECIO"
        val RSCP_EXTRA = "RSCP"
        val RSRP_EXTRA = "RSRP"
        val RSRQ_EXTRA = "RSRQ"
        val RSSI_EXTRA = "RSSI"
        val SINR_EXTRA = "SINR"
        val SNR_EXTRA = "SNR"
        val LAC_EXTRA = "LAC"
        val NETWORKBAND_EXTRA = "networkBand"
        val NETWORKTYPE_EXTRA = "networkType"
        val CALL_ID_EXTRA = "CallID"
        val CALL_NUMBER_EXTRA = "CallNumber"
        val PACKAGE_NAME_EXTRA = "ApplicationPackageName"
        val OEM_TIMESTAMP_EXTRA = "oemIntentTimestamp"

        val voiceDataManager = VoiceDataManager(instrumentationContext)
        voiceDataManager.initVoiceDataManager()
        val intent = Intent("diagandroid.phone.AppTriggeredCall")
        intent.putExtra(ECIO_EXTRA, "ecio")
        intent.putExtra(RSCP_EXTRA, "RSCP")
        intent.putExtra(RSRP_EXTRA, "RSRP")
        intent.putExtra(RSRQ_EXTRA, "RSRQ")
        intent.putExtra(RSSI_EXTRA, "RSSI")
        intent.putExtra(SINR_EXTRA, "SINR")
        intent.putExtra(SNR_EXTRA, "SNR")
        intent.putExtra(LAC_EXTRA, "LAC")
        intent.putExtra(NETWORKBAND_EXTRA, "NETWORKBAND")
        intent.putExtra(NETWORKTYPE_EXTRA, "NWTYPE")
        intent.putExtra(CALL_ID_EXTRA, "CALLID")
        intent.putExtra(PACKAGE_NAME_EXTRA, instrumentationContext.packageName)
        intent.putExtra(CALL_NUMBER_EXTRA, "CALLNUMBER")
        intent.putExtra(OEM_TIMESTAMP_EXTRA, "oemIntentTimestamp")
        val status = LocalBroadcastManager.getInstance(instrumentationContext).sendBroadcast(intent)
        Assert.assertTrue(status)
    }
}