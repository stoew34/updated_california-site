package com.tmobile.mytmobile.echolocate.lte.delegates

import android.content.Context
import android.content.Intent
import androidx.test.platform.app.InstrumentationRegistry
import com.tmobile.mytmobile.echolocate.lte.utils.ApplicationState
import com.tmobile.mytmobile.echolocate.lte.utils.LteSharedPreference
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test


class InstagramDelegateTest {

    private var delegate: InstagramDelegate? = null
    private lateinit var context: Context

    @Before
    fun setUp() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        delegate = InstagramDelegate.getInstance(context)
        LteSharedPreference.init(context)
        LteSharedPreference.triggerLimit = 2000
    }

    @After
    fun tearDown() {
    }


    @Test
    fun whenApplicationInFocusGainRunnableShouldBeAssignedAndNotNull() {
        delegate?.processApplicationState(ApplicationState.FOCUS_GAIN)
        assertEquals(301, delegate?.getFocusGainCode())
    }

    @Test
    fun whenApplicationInFocusGainAssertFocusGainCodeWithUnexpectedCode() {
        delegate?.processApplicationState(ApplicationState.FOCUS_GAIN)
        assertNotEquals(201, delegate?.getFocusGainCode())

    }

    @Test
    fun whenApplicationInFocusLossStateRunnableShouldBeNull() {
        delegate?.processApplicationState(ApplicationState.FOCUS_LOSS)
        assertEquals(390, delegate?.getFocusLossCode())

    }

    @Test
    fun whenApplicationInFocusLossStateSessionIDShouldNotBeNull() {
        delegate?.processApplicationState(ApplicationState.FOCUS_LOSS)
        assertEquals(390, delegate?.getFocusLossCode())

    }

    @Test
    fun whenApplicationScreenOffRunnableShouldBeNull() {
        val intent = Intent()
        intent.action = Intent.ACTION_SCREEN_OFF
        delegate?.handleIntent(intent)
        assertEquals(395, delegate?.getScreenOffCode())

    }
}