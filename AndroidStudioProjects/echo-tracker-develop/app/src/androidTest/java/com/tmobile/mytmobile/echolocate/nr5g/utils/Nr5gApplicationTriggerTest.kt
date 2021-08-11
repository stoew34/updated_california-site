package com.tmobile.mytmobile.echolocate.nr5g.utils

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import com.tmobile.mytmobile.echolocate.nr5g.core.utils.Nr5gApplicationTrigger
import com.tmobile.mytmobile.echolocate.nr5g.core.utils.Nr5gSharedPreference
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test


class Nr5gApplicationTriggerTest {
    private lateinit var context: Context
    private lateinit var nr5gApplicationTrigger: Nr5gApplicationTrigger

    @Before
    fun setUp() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        Nr5gSharedPreference.init(context)
        nr5gApplicationTrigger = Nr5gApplicationTrigger(context)
    }

    /**
     * This method is used to test app trigger count and limit
     */
    @Test
    fun screenTriggerCount() {
        nr5gApplicationTrigger.saveAppTriggerCount(5)
        nr5gApplicationTrigger.saveAppTriggerLimit(10)
        assertEquals(nr5gApplicationTrigger.getAppTriggerCount(), 5)
        assertEquals(nr5gApplicationTrigger.getAppTriggerLimit(), 10)
    }

}