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
class CallStateTriggerTest {
    lateinit var context: Context

    @Before
    fun setUp() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
    }


    @Test
    fun testCoverageCallStateEnableTrigger() {
        val coverageConfig =
            Gson().fromJson(configForCallStateEnabled(), CoverageConfig::class.java)
        Assert.assertTrue(CallStateTrigger.getInstance(context).initTrigger(coverageConfig.coverage))

    }

    @Test
    fun testCoverageCallStateDisableTrigger() {
        val coverageConfig =
            Gson().fromJson(configForCallStateDisabled(), CoverageConfig::class.java)
        Assert.assertFalse(CallStateTrigger.getInstance(context).initTrigger(coverageConfig.coverage))
    }

    @Test
    fun testCallStartEnableStopDisableState() {
        val coverageConfig =
            Gson().fromJson(configForCallStartEnableAndStopDisabled(), CoverageConfig::class.java)
        Assert.assertTrue(CallStateTrigger.getInstance(context).initTrigger(coverageConfig.coverage))
    }

    @Test
    fun testCallStartDisableStopEnableState() {
        val coverageConfig =
            Gson().fromJson(configForCallStartDisableAndStopEnabled(), CoverageConfig::class.java)
        Assert.assertTrue(CallStateTrigger.getInstance(context).initTrigger(coverageConfig.coverage))
    }


    private fun configForCallStateEnabled(): String {
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

    private fun configForCallStateDisabled(): String {
        return "{\n" +
                "   \"coverage\":{\n" +
                "      \"isEnabled\":true,\n" +
                "      \"samplingInterval\":6,\n" +
                "      \"screenTrigger\":{\n" +
                "         \"enabled\":true,\n" +
                "         \"eventsPerHour\":1\n" +
                "      },\n" +
                "      \"voiceCallStart\":{\n" +
                "         \"enabled\":false,\n" +
                "         \"eventsPerHour\":1\n" +
                "      },\n" +
                "      \"voiceCallEnd\":{\n" +
                "         \"enabled\":false,\n" +
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

    private fun configForCallStartEnableAndStopDisabled(): String {
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
                "         \"enabled\":false,\n" +
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

    private fun configForCallStartDisableAndStopEnabled(): String {
        return "{\n" +
                "   \"coverage\":{\n" +
                "      \"isEnabled\":true,\n" +
                "      \"samplingInterval\":6,\n" +
                "      \"screenTrigger\":{\n" +
                "         \"enabled\":true,\n" +
                "         \"eventsPerHour\":1\n" +
                "      },\n" +
                "      \"voiceCallStart\":{\n" +
                "         \"enabled\":false,\n" +
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
}
