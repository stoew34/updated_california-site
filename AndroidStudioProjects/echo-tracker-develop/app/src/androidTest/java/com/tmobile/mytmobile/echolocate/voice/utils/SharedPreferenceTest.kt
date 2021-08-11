package com.tmobile.mytmobile.echolocate.voice.utils

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Before
import org.junit.Test

class SharedPreferenceTest {
    lateinit var context: Context

    @Before
    fun setUp() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        VoiceSharedPreference.init(context)
    }

    @Test
    fun testSharedPreference() {
        VoiceSharedPreference.configurationObject = "configurationObject"
        assert(VoiceSharedPreference.configurationObject == "configurationObject")
    }

}