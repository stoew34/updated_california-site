package com.tmobile.mytmobile.echolocate.lte.delegates

import android.content.Context
import android.content.Intent
import androidx.test.platform.app.InstrumentationRegistry
import com.tmobile.mytmobile.echolocate.lte.utils.ApplicationState
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test


class YoutubeTVDelegateTest {

    private var delegate: YouTubeTvDelegate? = null
    private lateinit var context: Context

    @Before
    fun setUp() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        delegate = YouTubeTvDelegate.getInstance(context)
    }

    @After
    fun tearDown() {
    }


    @Test
    fun whenApplicationInFocusGainRunnableShouldBeAssignedAndNotNull() {
        delegate?.processApplicationState(ApplicationState.FOCUS_GAIN)
        assertEquals(701, delegate?.getFocusGainCode())
    }

    @Test
    fun whenApplicationInFocusGainAssertFocusGainCodeWithUnexpectedCode() {
        delegate?.processApplicationState(ApplicationState.FOCUS_GAIN)
        assertNotEquals(201, delegate?.getFocusGainCode())
    }

    @Test
    fun whenApplicationScreenOffRunnableShouldBeNull() {
        val intent = Intent()
        intent.action = Intent.ACTION_SCREEN_OFF
        delegate?.handleIntent(intent)
        assertEquals(795, delegate?.getScreenOffCode())
    }
}