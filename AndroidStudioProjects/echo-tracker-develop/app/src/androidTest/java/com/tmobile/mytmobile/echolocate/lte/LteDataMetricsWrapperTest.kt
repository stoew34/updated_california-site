package com.tmobile.mytmobile.echolocate.lte

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import com.tmobile.mytmobile.echolocate.lte.oemdata.LteBaseDataMetricsWrapper
import com.tmobile.mytmobile.echolocate.lte.oemdata.LteDataMetricsWrapper
import com.tmobile.pr.androidcommon.system.reflection.TmoBaseReflection.Companion.findField
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.lang.reflect.Field

class LteDataMetricsWrapperTest {


    private lateinit var instrumentationContext: Context
    private lateinit var lteDataMetricsWrapper: LteDataMetricsWrapper
    private lateinit var isDataMetricsAvailableField: Field

    @Before
    fun setUp() {
        instrumentationContext = InstrumentationRegistry.getInstrumentation().targetContext
        lteDataMetricsWrapper = LteDataMetricsWrapper(instrumentationContext)
        isDataMetricsAvailableField = findField(
            LteDataMetricsWrapper::class.java,
            "isDataMetricsAvailable"
        )
        isDataMetricsAvailableField.isAccessible = true
        isDataMetricsAvailableField.setBoolean(lteDataMetricsWrapper, false)
    }

    @Test
    @Throws(Exception::class)
    fun testApiVersionByInteger() {
        Assert.assertEquals(
            LteBaseDataMetricsWrapper.ApiVersion.VERSION_1, LteBaseDataMetricsWrapper.ApiVersion
                .valueByCode(LteBaseDataMetricsWrapper.ApiVersion.VERSION_1.intCode)
        )
    }

    @Test
    @Throws(Exception::class)
    fun testApiVersionByString() {
        Assert.assertEquals(
            LteBaseDataMetricsWrapper.ApiVersion.VERSION_1, LteBaseDataMetricsWrapper.ApiVersion
                .valueByCode(LteBaseDataMetricsWrapper.ApiVersion.VERSION_1.stringCode)
        )
    }

    @Test
    @Throws(Exception::class)
    fun testApiVersionByUnKnownVersion() {
        Assert.assertEquals(
            LteBaseDataMetricsWrapper.ApiVersion.UNKNOWN_VERSION, LteBaseDataMetricsWrapper.ApiVersion
                .valueByCode(Any())
        )
    }

    @Test
    @Throws(Exception::class)
    fun testGetUplinkCarrierInfo() {
        Assert.assertEquals(0, lteDataMetricsWrapper.getUplinkCarrierInfo().size)
    }

    @Test
    @Throws(Exception::class)
    fun testGetDownlinkRFConfiguration() {
        Assert.assertEquals(0, lteDataMetricsWrapper.getDownlinkRFConfiguration().size)
    }

    @Test
    @Throws(Exception::class)
    fun testGetNetworkIdentity() {
        Assert.assertEquals(0, lteDataMetricsWrapper.getNetworkIdentity().size)
    }

    @Test
    @Throws(Exception::class)
    fun testGetSignalCondition() {
        Assert.assertEquals(0, lteDataMetricsWrapper.getSignalCondition().size)
    }

    @Test
    @Throws(Exception::class)
    fun testGetCommonRFConfiguration() {
        Assert.assertEquals(0, lteDataMetricsWrapper.getCommonRFConfiguration().size)
    }

    @Test
    @Throws(Exception::class)
    fun testGetDownlinkCarrierInfo() {
        Assert.assertEquals(0, lteDataMetricsWrapper.getDownlinkCarrierInfo().size)
    }

    @Test
    @Throws(Exception::class)
    fun testGetUplinkRFConfiguration() {
        Assert.assertEquals(0, lteDataMetricsWrapper.getUplinkRFConfiguration().size)
    }

    @Test
    @Throws(Exception::class)
    fun testGetDataSetting() {
        Assert.assertEquals(0, lteDataMetricsWrapper.getDataSetting().size)
    }

    @Test
    @Throws(Exception::class)
    fun testApiVersion() {
        Assert.assertEquals(
            LteBaseDataMetricsWrapper.ApiVersion.UNKNOWN_VERSION, lteDataMetricsWrapper
                .getApiVersion()
        )
    }

    @Test
    @Throws(Exception::class)
    fun testIsDataMetricsAvailable() {
        Assert.assertFalse(lteDataMetricsWrapper.isDataMetricsAvailable())
    }

    @Test
    @Throws(Exception::class)
    fun testAvailableWithoutClass() {
        // isDataMetricsAvailableField.setBoolean(lteDataMetricsWrapper, true)
        Assert.assertEquals(0, lteDataMetricsWrapper.getNetworkIdentity().size)
    }

}