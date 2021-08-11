package com.tmobile.mytmobile.echolocate.nr5g.sa5g.datacollector

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import com.tmobile.mytmobile.echolocate.nr5g.core.oemdata.Sa5gDataMetricsWrapper
import org.junit.Assert
import org.junit.Before
import org.junit.Ignore
import org.junit.Test

class DataMetricsSa5gWrapperTest {

    private lateinit var instrumentationContext: Context
    private lateinit var dataMetricsSa5gWrapper: Sa5gDataMetricsWrapper

    @Before
    fun setUp() {
        instrumentationContext = InstrumentationRegistry.getInstrumentation().targetContext
        dataMetricsSa5gWrapper =
            Sa5gDataMetricsWrapper(
                instrumentationContext
            )
    }

    @Test
    @Throws(Exception::class)
    fun testApiVersionByInteger() {
        Assert.assertEquals(
            Sa5gDataMetricsWrapper.ApiVersion.VERSION_1, Sa5gDataMetricsWrapper.ApiVersion
                .valueByCode(Sa5gDataMetricsWrapper.ApiVersion.VERSION_1.intCode)
        )
    }

    @Test
    @Throws(Exception::class)
    fun testApiVersionByString() {
        Assert.assertEquals(
            Sa5gDataMetricsWrapper.ApiVersion.VERSION_1, Sa5gDataMetricsWrapper.ApiVersion
                .valueByCode(Sa5gDataMetricsWrapper.ApiVersion.VERSION_1.stringCode)
        )
    }

    @Test
    @Throws(Exception::class)
    fun testApiVersionByUnKnownVersion() {
        Assert.assertEquals(
            Sa5gDataMetricsWrapper.ApiVersion.UNKNOWN_VERSION, Sa5gDataMetricsWrapper.ApiVersion
                .valueByCode(Any())
        )
    }

    @Ignore
    @Test
    @Throws(Exception::class)
    fun testGetUplinkCarrierInfo() {
        Assert.assertEquals(null, dataMetricsSa5gWrapper.getUlCarrierLog())
    }

    @Ignore
    @Test
    @Throws(Exception::class)
    fun testGetDownlinkRFConfiguration() {
        Assert.assertEquals(null, dataMetricsSa5gWrapper.getDlCarrierLog())
    }

    @Ignore
    @Test
    @Throws(Exception::class)
    fun testGetNetworkLog() {
        Assert.assertEquals(null, dataMetricsSa5gWrapper.getNetworkLog())
    }

    @Test
    @Throws(Exception::class)
    fun testGetUiLog() {
        Assert.assertEquals(null, dataMetricsSa5gWrapper.getUiLog())
    }

    @Test
    @Throws(Exception::class)
    fun testGetRRcLog() {
        Assert.assertEquals(null, dataMetricsSa5gWrapper.getRrcLog())
    }

    @Test
    @Throws(Exception::class)
    fun testGetSettingsLog() {
        Assert.assertEquals(null, dataMetricsSa5gWrapper.getSettingsLog())
    }
}