package com.tmobile.mytmobile.echolocate.authentication

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.work.Configuration
import androidx.work.testing.SynchronousExecutor
import androidx.work.testing.WorkManagerTestInitHelper
import com.tmobile.mytmobile.echolocate.authentication.utils.TokenSharedPreference
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.Executor
import java.util.concurrent.Executors


@Suppress("DEPRECATION")
@RunWith(AndroidJUnit4::class)
class RefreshTokenSchedulerTest {

    lateinit var instrumentationContext: Context
    lateinit var refreshTokenScheduler: RefreshTokenScheduler
    private lateinit var executor: Executor

    @Before
    fun setUp() {
        instrumentationContext = InstrumentationRegistry.getInstrumentation().targetContext
        TokenSharedPreference.init(instrumentationContext)
        refreshTokenScheduler = RefreshTokenScheduler(instrumentationContext)
        val config = Configuration.Builder()
            // Use a SynchronousExecutor here to make it easier to write tests
            .setExecutor(SynchronousExecutor())
            .build()
        // Initialize WorkManager for instrumentation tests.
        WorkManagerTestInitHelper.initializeTestWorkManager(instrumentationContext, config)
        executor = Executors.newSingleThreadExecutor()
    }

    @Test
    fun testSchedulerJob(){
        refreshTokenScheduler.schedulerJob(1000,true);
        assert(TokenSharedPreference.schedulerResponseEvent.isNullOrEmpty())
    }
}
