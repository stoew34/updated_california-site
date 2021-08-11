package com.tmobile.mytmobile.echolocate.lte.utils

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class LtePeriodicTimerTest {
    lateinit var instrumentationContext: Context
    lateinit var ltePeriodicTimer: LtePeriodicTimer


    @Before
    fun setUp() {
        instrumentationContext = InstrumentationRegistry.getInstrumentation().targetContext
        ltePeriodicTimer = LtePeriodicTimer()
    }

    /**
     * this will check the timer object created or not
     */
    @Test
    fun testTimer() {
        Assert.assertNotNull(ltePeriodicTimer.getTimer())
    }

}