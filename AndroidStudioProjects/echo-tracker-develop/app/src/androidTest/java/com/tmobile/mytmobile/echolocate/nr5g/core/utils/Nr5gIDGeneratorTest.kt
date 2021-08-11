package com.tmobile.mytmobile.echolocate.nr5g.core.utils

/**
 * Created by Divya Mittal on 4/13/21
 */
import org.junit.Assert
import org.junit.Test

class Nr5gIDGeneratorTest {

    /**
     * This method is used is app id generating or not
     */
    @Test
    fun testAppIDGenerator() {
        Assert.assertNotNull(Nr5gIDGenerator.getUuid())
        Assert.assertNotNull(Nr5gIDGenerator.getTestSessionID())
    }

}