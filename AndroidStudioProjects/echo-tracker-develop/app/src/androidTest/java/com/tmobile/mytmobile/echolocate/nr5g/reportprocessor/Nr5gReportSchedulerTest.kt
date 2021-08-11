package com.tmobile.mytmobile.echolocate.nr5g.reportprocessor

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import androidx.work.Configuration
import androidx.work.testing.SynchronousExecutor
import androidx.work.testing.WorkManagerTestInitHelper
import com.tmobile.mytmobile.echolocate.TestActivity
import com.tmobile.mytmobile.echolocate.scheduler.events.SchedulerResponseEvent
import com.tmobile.mytmobile.echolocate.nr5g.core.scheduler.Nr5gReportScheduler
import com.tmobile.mytmobile.echolocate.nr5g.core.utils.Nr5gSharedPreference
import io.reactivex.observers.TestObserver
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


@Suppress("DEPRECATION")
@RunWith(AndroidJUnit4::class)
class Nr5gReportSchedulerTest {

    lateinit var instrumentationContext: Context
    lateinit var nr5gReportScheduler: Nr5gReportScheduler
    private lateinit var executor: Executor

    @Before
    fun setUp() {
        instrumentationContext = InstrumentationRegistry.getInstrumentation().targetContext
        Nr5gSharedPreference.init(instrumentationContext)
        nr5gReportScheduler = Nr5gReportScheduler(instrumentationContext)
        val config = Configuration.Builder()
            // Use a SynchronousExecutor here to make it easier to write tests
            .setExecutor(SynchronousExecutor())
            .build()

        // Initialize WorkManager for instrumentation tests.
        WorkManagerTestInitHelper.initializeTestWorkManager(instrumentationContext, config)
        executor = Executors.newSingleThreadExecutor()
    }

    @Test
    fun testReportCompilerScheduler() {
        Nr5gSharedPreference.scheduledWorkId = 0L
        val latch = CountDownLatch(1)
        nr5gReportScheduler.schedulerJob(24, true)
        latch.await(30, TimeUnit.SECONDS)
        Assert.assertEquals(0L, Nr5gSharedPreference.scheduledWorkId)
    }

}