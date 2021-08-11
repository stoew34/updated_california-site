//package com.tmobile.mytmobile.echolocate.nr5g.core.delegates
//
//import android.content.Context
//import android.content.Intent
//import androidx.test.platform.app.InstrumentationRegistry
//import com.tmobile.mytmobile.echolocate.commondata.datacollector.DeviceInfoDataCollector
//import com.tmobile.mytmobile.echolocate.commondata.model.DeviceInfo
//import com.tmobile.mytmobile.echolocate.nr5g.core.oemdata.Nr5gBaseDataMetricsWrapper
//import com.tmobile.mytmobile.echolocate.nr5g.core.oemdata.Nr5gDataMetricsWrapper
//import com.tmobile.mytmobile.echolocate.nr5g.nsa5g.database.entity.EndcLteLogEntity
//import com.tmobile.mytmobile.echolocate.nr5g.nsa5g.database.entity.EndcUplinkLogEntity
//import com.tmobile.mytmobile.echolocate.nr5g.nsa5g.database.entity.Nr5gMmwCellLogEntity
//import com.tmobile.mytmobile.echolocate.nr5g.nsa5g.database.entity.Nr5gUiLogEntity
//import com.tmobile.mytmobile.echolocate.nr5g.core.utils.ApplicationState
//import com.tmobile.mytmobile.echolocate.nr5g.core.utils.Nr5gIntents.ACTION_SCREEN_ON
//import com.tmobile.mytmobile.echolocate.nr5g.core.utils.Nr5gSharedPreference
//import com.tmobile.mytmobile.echolocate.nr5g.core.utils.Nr5gUtils
//import io.mockk.every
//import io.mockk.mockkClass
//import junit.framework.Assert
//import org.junit.After
//import org.junit.Assert.assertEquals
//import org.junit.Before
//import org.junit.Test
//
//
//class HourlyDelegateTest {
//
//    var hourlyDelegate: Nr5gHourlyDelegate? = null
//
//    private lateinit var context: Context
//    private lateinit var nr5gDataMetricsWrapper: Nr5gDataMetricsWrapper
//    private lateinit var deviceInfoDataCollector: DeviceInfoDataCollector
//
//
//    @Before
//    fun setUp() {
//        context = InstrumentationRegistry.getInstrumentation().targetContext
//        hourlyDelegate = Nr5gHourlyDelegate.getInstance(context)
//
//
//        nr5gDataMetricsWrapper = mockkClass(Nr5gDataMetricsWrapper::class)
//        hourlyDelegate?.nr5gDataMetricsWrapper = nr5gDataMetricsWrapper
//        every {
//            nr5gDataMetricsWrapper.getNetworkIdentity()
//        } returns listOf<String>("")
//
//        every {
//            nr5gDataMetricsWrapper.getApiVersion()
//        } returns DataMetricsWrapper.ApiVersion.VERSION_1
//
//        every {
//            nr5gDataMetricsWrapper.get5gNrMmwCellLog()
//        } returns Nr5gMmwCellLogEntity(
//            Nr5gUtils.getTriggerTimeStamp(),
//            10,
//            0,
//            10,
//            10f,
//            0f,
//            1f,
//            1,
//            2f,
//            3f,
//            4f,
//            "nrBandName",
//            5,
//            7
//        )
//
//        every {
//            nr5gDataMetricsWrapper.getEndcLteLog()
//        } returns EndcLteLogEntity(
//            Nr5gUtils.getTriggerTimeStamp(),
//            2,
//            1,
//            1,
//            1,
//            3
//        )
//
//        every {
//            nr5gDataMetricsWrapper.getEndcUplinkLog()
//        } returns EndcUplinkLogEntity(
//            "",
//            10,
//            1
//        )
//
//        every {
//            nr5gDataMetricsWrapper.getNr5gUiLog()
//        } returns Nr5gUiLogEntity(
//            Nr5gUtils.getTriggerTimeStamp(),
//            10,
//            "abc",
//            "abc",
//            10,
//            1
//        )
//
//        deviceInfoDataCollector = mockkClass(DeviceInfoDataCollector::class)
//
//        every {
//            deviceInfoDataCollector.getDeviceInformation(context)
//        } returns DeviceInfo("", "imei", "imsi", "", "")
//
//        Nr5gSharedPreference.init(context)
//        Nr5gSharedPreference.appTriggerLimit = 2000
//    }
//
//    @After
//    fun tearDown() {
//    }
//
//
//    @Test
//    fun FocusGainForAllAppsDelegateTest() {
//        hourlyDelegate?.processIntent(Intent(ACTION_SCREEN_ON))
//        Assert.assertNotNull(hourlyDelegate?.timeOutIDList)
//
//        delegate?.processApplicationState(ApplicationState.FOCUS_GAIN)
//        assertEquals(200, delegate?.getFocusGainCode())
//    }
//}