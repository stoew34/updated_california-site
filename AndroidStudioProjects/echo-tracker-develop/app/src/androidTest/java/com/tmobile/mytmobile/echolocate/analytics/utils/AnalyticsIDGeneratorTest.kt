package com.tmobile.mytmobile.echolocate.analytics.utils

/**
 * Created by Divya Mittal on 4/13/21
 */
import org.junit.Assert
import org.junit.Test

class AnalyticsIDGeneratorTest {

    /**
     * This method is used is app id generating or not
     */
    @Test
    fun testAppIDGenerator() {
        Assert.assertNotNull(AnalyticsIDGenerator.getUuid())
        Assert.assertNotNull(AnalyticsIDGenerator.getTestSessionID())
    }

}