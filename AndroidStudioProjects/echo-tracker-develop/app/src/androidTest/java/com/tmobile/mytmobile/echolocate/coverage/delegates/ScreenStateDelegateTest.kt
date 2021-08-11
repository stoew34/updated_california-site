package com.tmobile.mytmobile.echolocate.coverage.delegates
/**
 * Created by Mahesh Shetye on 2020-05-12
 *
 */


import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import com.tmobile.mytmobile.echolocate.TestActivity
import com.tmobile.mytmobile.echolocate.configuration.model.Coverage
import com.tmobile.mytmobile.echolocate.configuration.model.CoverageModule.BaseCoverageTriggerModule
import com.tmobile.mytmobile.echolocate.coverage.utils.CoverageSharedPreference
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class ScreenStateDelegateTest {
    private lateinit var context: Context
    private lateinit var screenTrigger: ScreenStateDelegate

    @get:Rule
    private val activityRule = ActivityTestRule(
        TestActivity::class.java, false, false
    )

    @Before
    fun setUp() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        screenTrigger = ScreenStateDelegate.getInstance(context)
    }

    @Test
    fun testScreenTriggerEnabled() {
        // Need to provide higher values of eventsPerHour, as this value is distributed evenly over an hour
        // For safer side, we should use 60*60 seconds, so that there won't be failure even if tested repeatedly and fast
        val triggerData = getTestCoverageData(true)

        val latch = CountDownLatch(1)
        val status = screenTrigger.initTrigger(triggerData)
        latch.await(1, TimeUnit.SECONDS)

        screenTrigger.dispose()

        assertEquals(status, true)
    }

    @Test
    fun testScreenTriggerDisabled() {
        // Need to provide higher values of eventsPerHour, as this value is distributed evenly over an hour
        // For safer side, we should use 60*60 seconds, so that there won't be failure even if tested repeatedly and fast
        val triggerData = getTestCoverageData(false)

        val latch = CountDownLatch(1)
        val status = screenTrigger.initTrigger(triggerData)
        latch.await(1, TimeUnit.SECONDS)

        screenTrigger.dispose()

        assertEquals(status, false)
    }

    @Test
    fun testScreenTriggerCount() {
        // Need to provide higher values of eventsPerHour, as this value is distributed evenly over an hour
        // For safer side, we should use 60*60 seconds, so that there won't be failure even if tested repeatedly and fast
        val triggerData = getTestCoverageData(true)

        CoverageSharedPreference.init(context)
        CoverageSharedPreference.screenTriggerCount = 0
        val latch = CountDownLatch(1)
        screenTrigger.initTrigger(triggerData)

        val screenCount = screenTrigger.getCurrentTriggerCounts()

        val screenCmd = "input keyevent 26"
        InstrumentationRegistry.getInstrumentation().uiAutomation.executeShellCommand(screenCmd)

        latch.await(5, TimeUnit.SECONDS)
        val screenNewCount = screenTrigger.getCurrentTriggerCounts()

        screenTrigger.dispose()

        assertEquals(1 , screenNewCount)
    }

    @Test
    fun testScreenTriggerReset() {
        // Need to provide higher values of eventsPerHour, as this value is distributed evenly over an hour
        // For safer side, we should use 60*60 seconds, so that there won't be failure even if tested repeatedly and fast
        val triggerData = getTestCoverageData(true)


        val latch = CountDownLatch(1)
        screenTrigger.initTrigger(triggerData)

        val screenCmd = "input keyevent 26"
        InstrumentationRegistry.getInstrumentation().uiAutomation.executeShellCommand(screenCmd)

        latch.await(3, TimeUnit.SECONDS)
        screenTrigger.resetTrigger()
        val screenNewCount = screenTrigger.getCurrentTriggerCounts() ?: 0

        screenTrigger.dispose()

        assertEquals(0, screenNewCount)
    }

    private fun getTestCoverageData(enabled: Boolean): Coverage {
        return Coverage(
            true,
            6,
            BaseCoverageTriggerModule(
                enabled = enabled,
                eventsPerHour = 3600
            ),

            BaseCoverageTriggerModule(
                enabled = false,
                eventsPerHour = 1
            ),

            BaseCoverageTriggerModule(
                enabled = false,
                eventsPerHour = 1
            ),

            BaseCoverageTriggerModule(
                enabled = false,
                eventsPerHour = 1
            ),
            BaseCoverageTriggerModule(
                enabled = false,
                eventsPerHour = 1
            )
        )
    }
}