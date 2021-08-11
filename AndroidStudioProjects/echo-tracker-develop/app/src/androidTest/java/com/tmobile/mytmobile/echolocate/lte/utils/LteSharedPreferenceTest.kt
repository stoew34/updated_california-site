package com.tmobile.mytmobile.echolocate.lte.utils

import io.mockk.every
import io.mockk.mockkObject
import org.junit.Assert
import org.junit.Before
import org.junit.Test


class LteSharedPreferenceTest {

    @Before
    fun beforeTEsts() {
        mockkObject(LteSharedPreference)
        every { LteSharedPreference.triggerCount } returns 10
        every { LteSharedPreference.triggerLimit } returns 10
        every { LteSharedPreference.scheduledWorkId } returns 10
    }

    @Test
    fun test(){
        Assert.assertEquals(10, LteSharedPreference.triggerCount)
        Assert.assertEquals(10, LteSharedPreference.triggerLimit)
        Assert.assertEquals(10, LteSharedPreference.scheduledWorkId)
    }
}