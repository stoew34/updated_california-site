package com.tmobile.mytmobile.echolocate.voice.utils

/**
 * Created by Divya Mittal on 4/13/21
 */
import org.junit.Assert
import org.junit.Test

class VoiceIDGeneratorTest {

    /**
     * This method is used is app id generating or not
     */
    @Test
    fun testAppIDGenerator() {
        Assert.assertNotNull(VoiceIDGenerator.getUuid())
        Assert.assertNotNull(VoiceIDGenerator.getTestSessionID())
    }

}