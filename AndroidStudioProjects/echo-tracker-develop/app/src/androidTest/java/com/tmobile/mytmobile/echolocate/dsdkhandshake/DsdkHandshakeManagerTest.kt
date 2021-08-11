package com.tmobile.mytmobile.echolocate.dsdkhandshake

import android.content.Context
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.gson.Gson
import com.tmobile.mytmobile.echolocate.configuration.model.DsdkHandshake
import com.tmobile.mytmobile.echolocate.dsdkHandshake.DsdkHandshakeManager
import com.tmobile.mytmobile.echolocate.dsdkHandshake.database.EcholocateDsdkHandshakeDatabase
import com.tmobile.mytmobile.echolocate.dsdkHandshake.database.dao.DsdkHandshakeDao
import com.tmobile.mytmobile.echolocate.utils.EchoLocateLog
import org.junit.Assert
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DsdkHandshakeManagerTest {
    lateinit var instrumentationContext: Context
    var dsdkHandshake: DsdkHandshake? = null
    private lateinit var db: EcholocateDsdkHandshakeDatabase
    lateinit var dsdkHandshakeDao: DsdkHandshakeDao

    @Before
    fun setUp() {
        instrumentationContext = InstrumentationRegistry.getInstrumentation().targetContext
        db = Room.inMemoryDatabaseBuilder(
            instrumentationContext,
            EcholocateDsdkHandshakeDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()
        dsdkHandshakeDao =
            EcholocateDsdkHandshakeDatabase.getDatabase(instrumentationContext).dsdkHandshakeDao()

    }

    private fun prepareHandshakeConfig(isEnabled: Boolean) {
        try {
            val dsdkHandshakeConfig = if (isEnabled) {
                Gson().fromJson(getDsdkConfigWithIsEnabledTrue(), DsdkConfig::class.java)
            } else {
                Gson().fromJson(getDsdkConfigWithIsEnabledFalse(), DsdkConfig::class.java)
            }
            dsdkHandshake = dsdkHandshakeConfig.dsdkHandshake
            DsdkHandshakeManager.getInstance(instrumentationContext)
                .processHandshakeConfig(dsdkHandshake!!)
        } catch (e: Exception) {
            EchoLocateLog.eLogV("Failed to load the data$e")

        }
    }

    @Test
    fun startDsdkHandShakeWhenIsEnabledIsTrue() {
        prepareHandshakeConfig(true)
        Assert.assertNotNull(db.dsdkHandshakeDao().getCount())
    }

    @Test
    fun stopDsdkHandShakeWhenIsEnabledIsFalse() {
        prepareHandshakeConfig(false)
    }

    @Test
    fun testStopModulesWhenStopDataIsCollectionIsTrue() {
        getDsdkConfigWithIsEnabledTrueAndStopDataCollection()
        Assert.assertTrue(dsdkHandshakeDao.getVoiceStopDataCollectionFlag())
        Assert.assertTrue(dsdkHandshakeDao.getLteStopDataCollectionFlag())
        Assert.assertTrue(dsdkHandshakeDao.getNr5gtopDataCollectionFlag())
    }

    @Test
    fun testStartModulesWhenStopDataIsCollectionIsFalse() {
        getDsdkConfigWithIsEnabledTrueAndStartDataCollection()
        Assert.assertFalse(dsdkHandshakeDao.getVoiceStopDataCollectionFlag())
        Assert.assertFalse(dsdkHandshakeDao.getLteStopDataCollectionFlag())
        Assert.assertFalse(dsdkHandshakeDao.getNr5gtopDataCollectionFlag())
    }

    private fun getDsdkConfigWithIsEnabledTrue(): String {
        return "dsdkHandshake\": {\n" +
                "    \"isEnabled\": true,\n" +
                "    \"featureSupportedTMOAppVersion\": \"\",\n" +
                "    \"blacklistedTmoAppVersion\": \"9.0.0.0\",\n" +
                "    \"dsdkVoiceEligibility\": {\n" +
                "      \"voiceStopDataCollection\": false,\n" +
                "      \"blaklistedTAC\": [\n" +
                "        \"01234567\",\n" +
                "        \"12345678\"\n" +
                "      ]\n" +
                "    },\n" +
                "    \"dsdkLteEligibility\": {\n" +
                "      \"lteStopDataCollection\": false,\n" +
                "      \"blaklistedTAC\": [\n" +
                "        \"01234567\",\n" +
                "        \"12345678\"\n" +
                "      ]\n" +
                "    },\n" +
                "    \"dsdkNr5gEligibility\": {\n" +
                "      \"nr5gStopDataCollection\": false,\n" +
                "      \"blaklistedTAC\": [\n" +
                "        \"01234567\",\n" +
                "        \"12345678\"\n" +
                "      ]\n" +
                "    }\n" +
                "  }"
    }

    private fun getDsdkConfigWithIsEnabledFalse(): String {
        return "\"dsdkHandshake\": {\n" +
                "    \"isEnabled\": false,\n" +
                "    \"featureSupportedTMOAppVersion\": \"\",\n" +
                "    \"blacklistedTmoAppVersion\": \"9.0.0.0\",\n" +
                "    \"dsdkVoiceEligibility\": {\n" +
                "      \"voiceStopDataCollection\": false,\n" +
                "      \"blaklistedTAC\": [\n" +
                "        \"01234567\",\n" +
                "        \"12345678\"\n" +
                "      ]\n" +
                "    },\n" +
                "    \"dsdkLteEligibility\": {\n" +
                "      \"lteStopDataCollection\": false,\n" +
                "      \"blaklistedTAC\": [\n" +
                "        \"01234567\",\n" +
                "        \"12345678\"\n" +
                "      ]\n" +
                "    },\n" +
                "    \"dsdkNr5gEligibility\": {\n" +
                "      \"nr5gStopDataCollection\": false,\n" +
                "      \"blaklistedTAC\": [\n" +
                "        \"01234567\",\n" +
                "        \"12345678\"\n" +
                "      ]\n" +
                "    }\n" +
                "  }"
    }

    private fun getDsdkConfigWithIsEnabledTrueAndStopDataCollection(): String {
        val data = "{\n" +
                "   \"dsdkHandshake\":{\n" +
                "      \"isEnabled\":true,\n" +
                "      \"featureSupportedTMOAppVersion\":\"\",\n" +
                "      \"blacklistedTmoAppVersion\":\"9.0.0.0\",\n" +
                "      \"dsdkVoiceEligibility\":{\n" +
                "         \"voiceStopDataCollection\":true,\n" +
                "         \"blaklistedTAC\":[\n" +
                "            \"01234567\",\n" +
                "            \"12345678\"\n" +
                "         ]\n" +
                "      },\n" +
                "      \"dsdkLteEligibility\":{\n" +
                "         \"lteStopDataCollection\":true,\n" +
                "         \"blaklistedTAC\":[\n" +
                "            \"01234567\",\n" +
                "            \"12345678\"\n" +
                "         ]\n" +
                "      },\n" +
                "      \"dsdkNr5gEligibility\":{\n" +
                "         \"nr5gStopDataCollection\":true,\n" +
                "         \"blaklistedTAC\":[\n" +
                "            \"01234567\",\n" +
                "            \"12345678\"\n" +
                "         ]\n" +
                "      }\n" +
                "   }\n" +
                "}"
        try {
            val dsdkHandshakeConfig = Gson().fromJson(data, DsdkConfig::class.java)
            dsdkHandshake = dsdkHandshakeConfig.dsdkHandshake
            DsdkHandshakeManager.getInstance(instrumentationContext)
                .processHandshakeConfig(dsdkHandshake!!)
        } catch (e: Exception) {
            EchoLocateLog.eLogV("Failed to load the data$e")

        }
        return data
    }

    private fun getDsdkConfigWithIsEnabledTrueAndStartDataCollection(): String {
        val data = "{\n" +
                "   \"dsdkHandshake\":{\n" +
                "      \"isEnabled\":true,\n" +
                "      \"featureSupportedTMOAppVersion\":\"\",\n" +
                "      \"blacklistedTmoAppVersion\":\"9.0.0.0\",\n" +
                "      \"dsdkVoiceEligibility\":{\n" +
                "         \"voiceStopDataCollection\":false,\n" +
                "         \"blaklistedTAC\":[\n" +
                "            \"01234567\",\n" +
                "            \"12345678\"\n" +
                "         ]\n" +
                "      },\n" +
                "      \"dsdkLteEligibility\":{\n" +
                "         \"lteStopDataCollection\":false,\n" +
                "         \"blaklistedTAC\":[\n" +
                "            \"01234567\",\n" +
                "            \"12345678\"\n" +
                "         ]\n" +
                "      },\n" +
                "      \"dsdkNr5gEligibility\":{\n" +
                "         \"nr5gStopDataCollection\":false,\n" +
                "         \"blaklistedTAC\":[\n" +
                "            \"01234567\",\n" +
                "            \"12345678\"\n" +
                "         ]\n" +
                "      }\n" +
                "   }\n" +
                "}"
        try {
            val dsdkHandshakeConfig = Gson().fromJson(data, DsdkConfig::class.java)
            dsdkHandshake = dsdkHandshakeConfig.dsdkHandshake
            DsdkHandshakeManager.getInstance(instrumentationContext)
                .processHandshakeConfig(dsdkHandshake!!)
        } catch (e: Exception) {
            EchoLocateLog.eLogV("Failed to load the data$e")

        }
        return data
    }
}
