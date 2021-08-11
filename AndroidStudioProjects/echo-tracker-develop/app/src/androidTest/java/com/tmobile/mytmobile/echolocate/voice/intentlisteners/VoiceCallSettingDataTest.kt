package com.tmobile.mytmobile.echolocate.voice.intentlisteners

import android.content.Context
import android.content.Intent
import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import com.tmobile.mytmobile.echolocate.voice.repository.database.EchoLocateVoiceDatabase
import com.tmobile.mytmobile.echolocate.voice.repository.database.dao.VoiceDao
import com.tmobile.mytmobile.echolocate.voice.manager.VoiceDataManager
import com.tmobile.mytmobile.echolocate.voice.utils.VoiceIntents
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.io.IOException

class VoiceCallSettingDataTest {

    private lateinit var mEchoLocateVoiceDatabase: EchoLocateVoiceDatabase
    private lateinit var mVoiceDao: VoiceDao
    private lateinit var mContext: Context
    private lateinit var mVoiceDataManager: VoiceDataManager

    // Constants
    private val CALL_SETTING_VO_LTE = "CallSettingVoLTE"
    private val CALL_SETTING_WFC = "CallSettingWFC"
    private val CALL_SETTING_WFC_PREFERENCE = "CallSettingWFCPreference"
    private val CALL_ID_EXTRA = "CallID"
    private val OEM_TIMESTAMP_EXTRA = "oemIntentTimestamp"
    private val CALL_NUMBER_EXTRA = "CallNumber"

    @Before
    fun createDb() {
        mContext = InstrumentationRegistry.getInstrumentation().targetContext

        mEchoLocateVoiceDatabase = Room.inMemoryDatabaseBuilder(mContext, EchoLocateVoiceDatabase::class.java)
            .allowMainThreadQueries()
            .build()

        mVoiceDao = EchoLocateVoiceDatabase.getEchoLocateVoiceDatabase(mContext).voiceDao()
        mVoiceDataManager = VoiceDataManager(mContext)
        mVoiceDataManager.initVoiceDataManager()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        mEchoLocateVoiceDatabase.clearAllTables()
        mEchoLocateVoiceDatabase.close()
    }

    /**
     * Tests the database when the all call settings extras available in the intent
     */
    @Test
    fun testCallSettingIntentWithValidIntent() {

        val callID = System.currentTimeMillis().toString()
        val oemTimeStamp = System.nanoTime().toString()
        val callNumber = "9999999999"
        val registeredState = "REGISTERED"
        val wifiOnly = "WIFIONLY"

        val intent = Intent(VoiceIntents.CALL_SETTING)
        intent.putExtra(CALL_ID_EXTRA, callID)
        intent.putExtra(CALL_NUMBER_EXTRA, callNumber)
        intent.putExtra(CALL_SETTING_VO_LTE, registeredState)
        intent.putExtra(CALL_SETTING_WFC, registeredState)
        intent.putExtra(CALL_SETTING_WFC_PREFERENCE, wifiOnly)
        intent.putExtra(OEM_TIMESTAMP_EXTRA, oemTimeStamp)

        runBlocking {
            mVoiceDataManager.onHandleIntent(intent, System.currentTimeMillis())
        }

        Thread.sleep(500)

        val voiceBaseTable = mVoiceDao.getBaseEchoLocateVoiceEntityBySessionID(callID)
        Assert.assertNotNull(voiceBaseTable)
        Assert.assertTrue(voiceBaseTable.callNumber == callNumber)
        Assert.assertTrue(voiceBaseTable.sessionId == callID)

        val callSettingsEntityList = mVoiceDao.getCallSettingDataEntity(callID)
        Assert.assertNotNull(callSettingsEntityList)
        Assert.assertFalse(callSettingsEntityList.isEmpty())

        val callSettingsEntity = callSettingsEntityList[0] //Assuming there will be only one for callID.
        Assert.assertTrue(callSettingsEntity.callId == callID)
        Assert.assertTrue(callSettingsEntity.oemTimestamp == oemTimeStamp)
        Assert.assertTrue(callSettingsEntity.volteStatus == registeredState)
        Assert.assertTrue(callSettingsEntity.wfcStatus == registeredState)
        Assert.assertTrue(callSettingsEntity.wfcPreference == wifiOnly)
        Assert.assertNotNull(callSettingsEntity.uniqueId)
    }

    /**
     * Tests the database when some of the call settings intent extras are missed.
     * CALL_SETTING_VO_LTE, CALL_SETTING_WFC, CALL_SETTING_WFC_PREFERENCE will be passed null in the intent.
     */
    @Test
    fun testCallSettingIntentWithValidInIntent() {
        val callID = System.currentTimeMillis().toString()
        val callNumber = "9999999999"

        val intent = Intent(VoiceIntents.CALL_SETTING)
        intent.putExtra(CALL_ID_EXTRA, callID)
        intent.putExtra(CALL_NUMBER_EXTRA, callNumber)
        runBlocking {
            mVoiceDataManager.onHandleIntent(intent, System.currentTimeMillis())
        }

        Thread.sleep(500)
        val voiceBaseTable = mVoiceDao.getBaseEchoLocateVoiceEntityBySessionID(callID)
        Assert.assertNotNull(voiceBaseTable)
        Assert.assertTrue(voiceBaseTable.callNumber == callNumber)
        Assert.assertTrue(voiceBaseTable.sessionId == callID)

        val callSettingsEntityList = mVoiceDao.getCallSettingDataEntity(callID)
        Assert.assertNotNull(callSettingsEntityList)
        Assert.assertFalse(callSettingsEntityList.isEmpty())

        val callSettingsEntity = callSettingsEntityList[0] //Assuming there will be only one for callID.
        Assert.assertTrue(callSettingsEntity.callId == callID)
        Assert.assertTrue(callSettingsEntity.volteStatus == "")
        Assert.assertTrue(callSettingsEntity.wfcStatus == "")
        Assert.assertTrue(callSettingsEntity.wfcPreference == "")
        Assert.assertNotNull(callSettingsEntity.uniqueId)
        Assert.assertNotNull(callSettingsEntity.eventTimestamp)
        Assert.assertNotNull(callSettingsEntity.oemTimestamp)
    }
}