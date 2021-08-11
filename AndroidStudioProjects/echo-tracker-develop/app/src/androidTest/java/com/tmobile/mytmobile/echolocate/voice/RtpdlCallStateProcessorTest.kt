package com.tmobile.mytmobile.echolocate.voice

import android.content.Context
import android.content.Intent
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.tmobile.mytmobile.echolocate.voice.repository.database.EchoLocateVoiceDatabase
import com.tmobile.mytmobile.echolocate.voice.repository.database.dao.VoiceDao
import com.tmobile.mytmobile.echolocate.voice.dataprocessor.RtpdlCallStateProcessor
import com.tmobile.mytmobile.echolocate.voice.model.RtpdlStateData
import com.tmobile.mytmobile.echolocate.voice.utils.VoiceIntents
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class RtpdlCallStateProcessorTest {


    private val VOICE_ACCESS_NETWORK_STATE_BAND = "VoiceAccessNetworkStateBand"
    private val VOICE_ACCESS_NETWORK_STATE_TYPE = "VoiceAccessNetworkStateType"
    private val CALL_ID_EXTRA = "CallID"
    val OEM_TIMESTAMP_EXTRA = "oemIntentTimestamp"
    private val EVENT_TIMESTAMP_EXTRA = "eventTimestamp"
    private val CALL_NUMBER_EXTRA = "CallNumber"
    private val VOICE_ACCESS_NETWORK_STATE_SIGNAL = "VoiceAccessNetworkStateSignal"


    private lateinit var db: EchoLocateVoiceDatabase
    private lateinit var voiceDao: VoiceDao
    private lateinit var context: Context

    @Before
    fun createDb() {
        context = InstrumentationRegistry.getInstrumentation().targetContext

        db = Room.inMemoryDatabaseBuilder(context, EchoLocateVoiceDatabase::class.java)
            .allowMainThreadQueries()
            .build()

        voiceDao = EchoLocateVoiceDatabase.getEchoLocateVoiceDatabase(context).voiceDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
//        db.close()
    }

    @Test
    suspend fun processIntent() {
        val rtpdlCallStateProcessor = RtpdlCallStateProcessor(context)
        val intent = Intent(VoiceIntents.RTPDL_STAT)

        intent.putExtra(CALL_ID_EXTRA, "111")
        intent.putExtra(
            VOICE_ACCESS_NETWORK_STATE_SIGNAL,
            "VoiceAccessNetworkStateSignal;test_rssi;test_rscp;test_ecio;test_rsrp;test_rsrq;test_sinr;test_snr"
        )
        intent.putExtra(VOICE_ACCESS_NETWORK_STATE_BAND, "test_networkband")
        intent.putExtra(VOICE_ACCESS_NETWORK_STATE_TYPE, "test_networktype")
        intent.putExtra(OEM_TIMESTAMP_EXTRA, "test_oem_timestamp")
        intent.putExtra(EVENT_TIMESTAMP_EXTRA, "test_event_timestamp")
        intent.putExtra(CALL_NUMBER_EXTRA, "test_call_number")

        val rtpdlStateData = rtpdlCallStateProcessor.execute(intent, 100) as RtpdlStateData

        Assert.assertNotNull(rtpdlStateData)
        Assert.assertEquals(rtpdlStateData.delay, 0.0, 0.0)
        Assert.assertEquals(rtpdlStateData.eventTimestamp, "100")
        Assert.assertEquals(rtpdlStateData.jitter, 0.0, 0.0)
        Assert.assertEquals(rtpdlStateData.lossRate, 0.0, 0.0)
        Assert.assertEquals(rtpdlStateData.measuredPeriod, 0.0, 0.0)
        Assert.assertEquals(rtpdlStateData.oemTimestamp, "test_oem_timestamp")
        Assert.assertEquals(rtpdlStateData.sequence, 0.0, 0.0)
        Assert.assertEquals(rtpdlStateData.eventInfo?.cellInfo?.rssi, "test_rssi")
        Assert.assertEquals(rtpdlStateData.eventInfo?.cellInfo?.rscp, "test_rscp")
        Assert.assertEquals(rtpdlStateData.eventInfo?.cellInfo?.ecio, "test_ecio")
        Assert.assertEquals(rtpdlStateData.eventInfo?.cellInfo?.rsrp, "test_rsrp")
        Assert.assertEquals(rtpdlStateData.eventInfo?.cellInfo?.rsrq, "test_rsrq")
        Assert.assertEquals(rtpdlStateData.eventInfo?.cellInfo?.sinr, "test_sinr")
        Assert.assertEquals(rtpdlStateData.eventInfo?.cellInfo?.snr, "test_snr")
        Assert.assertEquals(rtpdlStateData.eventInfo?.cellInfo?.networkBand, "test_networkband")
        Assert.assertEquals(rtpdlStateData.eventInfo?.cellInfo?.networkType, "test_networktype")
    }
}