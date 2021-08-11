package com.tmobile.mytmobile.echolocate.nr5g.core.delegates

import android.content.Context
import android.content.Intent
import androidx.test.platform.app.InstrumentationRegistry
import com.tmobile.mytmobile.echolocate.nr5g.core.oemdata.Nr5gBaseDataMetricsWrapper
import com.tmobile.mytmobile.echolocate.nr5g.core.oemdata.Nr5gDataMetricsWrapper
import com.tmobile.mytmobile.echolocate.nr5g.nsa5g.database.entity.EndcLteLogEntity
import com.tmobile.mytmobile.echolocate.nr5g.nsa5g.database.entity.EndcUplinkLogEntity
import com.tmobile.mytmobile.echolocate.nr5g.nsa5g.database.entity.Nr5gMmwCellLogEntity
import com.tmobile.mytmobile.echolocate.nr5g.nsa5g.database.entity.Nr5gUiLogEntity
import com.tmobile.mytmobile.echolocate.nr5g.core.utils.*
import com.tmobile.mytmobile.echolocate.standarddatablocks.DeviceInfo
import com.tmobile.mytmobile.echolocate.utils.EchoLocateDateUtils.Companion.getTriggerTimeStamp
import io.mockk.every
import io.mockk.mockkClass
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit


class ScreenOnDelegateTest {

    var baseDelegate: BaseDelegate? = null

    private lateinit var context: Context
    private lateinit var nr5gDataMetricsWrapper: Nr5gDataMetricsWrapper
    private lateinit var deviceInfoDataCollector: Nr5gDeviceInfoDataCollector


    @Before
    fun setUp() {
        context = InstrumentationRegistry.getInstrumentation().targetContext

        nr5gDataMetricsWrapper = mockkClass(Nr5gDataMetricsWrapper::class)
        baseDelegate  = AllAppsDelegate.getInstance(context)
        baseDelegate?.nr5gDataMetricsWrapper = nr5gDataMetricsWrapper
        every {
            nr5gDataMetricsWrapper.getNetworkIdentity()
        } returns listOf("")

        every {
            nr5gDataMetricsWrapper.getApiVersion()
        } returns Nr5gBaseDataMetricsWrapper.ApiVersion.VERSION_1

        every {
            nr5gDataMetricsWrapper.get5gNrMmwCellLog()
        } returns Nr5gMmwCellLogEntity(
            getTriggerTimeStamp(),
            10,
            0,
            10,
            10f,
            0f,
            1f,
            1,
            2f,
            3f,
            4f,
            "nrBandName",
            5,
            7
        )

        every {
            nr5gDataMetricsWrapper.getEndcLteLog()
        } returns EndcLteLogEntity(
            getTriggerTimeStamp(),
            2,
            1,
            1,
            1,
            3
        )

        every {
            nr5gDataMetricsWrapper.getEndcUplinkLog()
        } returns EndcUplinkLogEntity(
            "",
            10,
            1
        )

        every {
            nr5gDataMetricsWrapper.getNr5gUiLog()
        } returns Nr5gUiLogEntity(
            getTriggerTimeStamp(),
            10,
            "abc",
            "abc",
            10,
            1
        )

        deviceInfoDataCollector = mockkClass(Nr5gDeviceInfoDataCollector::class)

        every {
            deviceInfoDataCollector.getDeviceInformation(context)
        } returns DeviceInfo("", "imei", "imsi", "", "")

        Nr5gSharedPreference.init(context)
        Nr5gSharedPreference.appTriggerLimit = 2000
    }

    @After
    fun tearDown() {
    }


    @Test
    fun screenOnForAllAppsDelegateTest() {
        val latch = CountDownLatch(1)
        val nr5gScreenTrigger: Nr5gScreenTrigger = Nr5gScreenTrigger.getInstance(context)
        val screenCount = nr5gScreenTrigger.getScreenTriggerCount() ?: 0

        val intent = Intent()
        intent.action = Nr5gIntents.ACTION_SCREEN_ON
        intent.putExtra(Nr5gIntents.TRIGGER_TIMESTAMP,
            System.currentTimeMillis())
        baseDelegate?.processIntent(intent)
        latch.await(1, TimeUnit.SECONDS)

        assertEquals(screenCount + 1, nr5gScreenTrigger.getScreenTriggerCount())
    }

}