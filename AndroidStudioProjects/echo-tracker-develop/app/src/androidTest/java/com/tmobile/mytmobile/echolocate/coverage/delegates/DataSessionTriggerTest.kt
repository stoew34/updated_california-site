package com.tmobile.mytmobile.echolocate.coverage.delegates

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.gson.Gson
import com.tmobile.mytmobile.echolocate.coverage.CoverageConfig
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DataSessionTriggerTest {
    lateinit var context: Context

    @Before
    fun setUp() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
    }


    @Test
    fun testCoverageDataSessionEnabledtTrigger() {
        val coverageConfig =
            Gson().fromJson(configForDataSessionStateEnabled(), CoverageConfig::class.java)
        Assert.assertTrue(
            DataSessionTrigger.getInstance(context).initTrigger(coverageConfig.coverage)
        )

    }

    @Test
    fun testCoverageDataSessionDisabledTrigger() {
        val coverageConfig =
            Gson().fromJson(configForDataSessionStateDisabled(), CoverageConfig::class.java)
        Assert.assertFalse(
            DataSessionTrigger.getInstance(context).initTrigger(coverageConfig.coverage)
        )
    }

    @Test
    fun testDataSessionEnableStopDisableState() {
        val coverageConfig =
            Gson().fromJson(configForDataSessionStartEnableAndStopDisabled(), CoverageConfig::class.java)
        Assert.assertTrue(
            DataSessionTrigger.getInstance(context).initTrigger(coverageConfig.coverage)
        )
    }

    @Test
    fun testDataSessionDisableStopEnableState() {
        val coverageConfig =
            Gson().fromJson(configForDataSessionStartDisableAndStopEnabled(), CoverageConfig::class.java)
        Assert.assertTrue(
            DataSessionTrigger.getInstance(context).initTrigger(coverageConfig.coverage)
        )
    }


    private fun configForDataSessionStateEnabled(): String {
        return "{\n" +
                "   \"coverage\":{\n" +
                "      \"isEnabled\":true,\n" +
                "      \"samplingInterval\":6,\n" +
                "      \"screenTrigger\":{\n" +
                "         \"enabled\":true,\n" +
                "         \"eventsPerHour\":1\n" +
                "      },\n" +
                "      \"voiceCallStart\":{\n" +
                "         \"enabled\":true,\n" +
                "         \"eventsPerHour\":1\n" +
                "      },\n" +
                "      \"voiceCallEnd\":{\n" +
                "         \"enabled\":true,\n" +
                "         \"eventsPerHour\":1\n" +
                "      },\n" +
                "      \"dataSessionStart\":{\n" +
                "         \"enabled\":true,\n" +
                "         \"eventsPerHour\":1\n" +
                "      },\n" +
                "      \"dataSessionEnd\":{\n" +
                "         \"enabled\":true,\n" +
                "         \"eventsPerHour\":1\n" +
                "      }\n" +
                "   }\n" +
                "}"

    }

    private fun configForDataSessionStateDisabled(): String {
        return "{\n" +
                "   \"coverage\":{\n" +
                "      \"isEnabled\":true,\n" +
                "      \"samplingInterval\":6,\n" +
                "      \"screenTrigger\":{\n" +
                "         \"enabled\":true,\n" +
                "         \"eventsPerHour\":1\n" +
                "      },\n" +
                "      \"voiceCallStart\":{\n" +
                "         \"enabled\":true,\n" +
                "         \"eventsPerHour\":1\n" +
                "      },\n" +
                "      \"voiceCallEnd\":{\n" +
                "         \"enabled\":true,\n" +
                "         \"eventsPerHour\":1\n" +
                "      },\n" +
                "      \"dataSessionStart\":{\n" +
                "         \"enabled\":false,\n" +
                "         \"eventsPerHour\":1\n" +
                "      },\n" +
                "      \"dataSessionEnd\":{\n" +
                "         \"enabled\":false,\n" +
                "         \"eventsPerHour\":1\n" +
                "      }\n" +
                "   }\n" +
                "}"

    }

    private fun configForDataSessionStartEnableAndStopDisabled(): String {
        return "{\n" +
                "   \"coverage\":{\n" +
                "      \"isEnabled\":true,\n" +
                "      \"samplingInterval\":6,\n" +
                "      \"screenTrigger\":{\n" +
                "         \"enabled\":true,\n" +
                "         \"eventsPerHour\":1\n" +
                "      },\n" +
                "      \"voiceCallStart\":{\n" +
                "         \"enabled\":true,\n" +
                "         \"eventsPerHour\":1\n" +
                "      },\n" +
                "      \"voiceCallEnd\":{\n" +
                "         \"enabled\":true,\n" +
                "         \"eventsPerHour\":1\n" +
                "      },\n" +
                "      \"dataSessionStart\":{\n" +
                "         \"enabled\":true,\n" +
                "         \"eventsPerHour\":1\n" +
                "      },\n" +
                "      \"dataSessionEnd\":{\n" +
                "         \"enabled\":false,\n" +
                "         \"eventsPerHour\":1\n" +
                "      }\n" +
                "   }\n" +
                "}"

    }

    private fun configForDataSessionStartDisableAndStopEnabled(): String {
        return "{\n" +
                "   \"coverage\":{\n" +
                "      \"isEnabled\":true,\n" +
                "      \"samplingInterval\":6,\n" +
                "      \"screenTrigger\":{\n" +
                "         \"enabled\":true,\n" +
                "         \"eventsPerHour\":1\n" +
                "      },\n" +
                "      \"voiceCallStart\":{\n" +
                "         \"enabled\":true,\n" +
                "         \"eventsPerHour\":1\n" +
                "      },\n" +
                "      \"voiceCallEnd\":{\n" +
                "         \"enabled\":true,\n" +
                "         \"eventsPerHour\":1\n" +
                "      },\n" +
                "      \"dataSessionStart\":{\n" +
                "         \"enabled\":false,\n" +
                "         \"eventsPerHour\":1\n" +
                "      },\n" +
                "      \"dataSessionEnd\":{\n" +
                "         \"enabled\":true,\n" +
                "         \"eventsPerHour\":1\n" +
                "      }\n" +
                "   }\n" +
                "}"

    }
}