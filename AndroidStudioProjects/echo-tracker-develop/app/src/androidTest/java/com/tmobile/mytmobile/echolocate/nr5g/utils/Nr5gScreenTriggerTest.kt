package com.tmobile.mytmobile.echolocate.nr5g.utils

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import com.tmobile.mytmobile.echolocate.nr5g.core.utils.Nr5gScreenTrigger
import com.tmobile.mytmobile.echolocate.nr5g.core.utils.Nr5gSharedPreference
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test


class Nr5gScreenTriggerTest {
    private lateinit var context: Context
    private lateinit var nr5gScreenTrigger: Nr5gScreenTrigger

    @Before
    fun setUp() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        Nr5gSharedPreference.init(context)
        nr5gScreenTrigger = Nr5gScreenTrigger(context)
    }

    /**
     * This method is used to test screen trigger count and limit
     */
    @Test
    fun screenTriggerCount() {
        nr5gScreenTrigger.saveScreenTriggerCount(5)
        nr5gScreenTrigger.saveScreenTriggerLimit(10)
        assertEquals(nr5gScreenTrigger.getScreenTriggerCount(), 5)
        assertEquals(nr5gScreenTrigger.getScreenTriggerLimit(), 10)
    }

}