package com.tmobile.mytmobile.echolocate.nr5g.core.utils

import android.content.Context
import android.net.wifi.WifiManager
import androidx.test.platform.app.InstrumentationRegistry
import com.tmobile.mytmobile.echolocate.nr5g.core.oemdata.Nr5gDataMetricsWrapper
import com.tmobile.mytmobile.echolocate.nr5g.core.delegates.AllAppsDelegate
import com.tmobile.mytmobile.echolocate.nr5g.nsa5g.database.entity.Nr5gWifiStateEntity
import com.tmobile.mytmobile.echolocate.nr5g.nsa5g.model.EndcLteLog
import com.tmobile.mytmobile.echolocate.nr5g.nsa5g.model.EndcUplinkLog
import com.tmobile.mytmobile.echolocate.nr5g.nsa5g.model.Nr5gMmwCellLog
import com.tmobile.mytmobile.echolocate.nr5g.nsa5g.model.Nr5gUiLog
import com.tmobile.mytmobile.echolocate.nr5g.nsa5g.utils.Nsa5gEntityConverter
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test


class Nr5gEntityConverterTest {

    var delegate: AllAppsDelegate? = null
    private lateinit var context: Context
    private lateinit var nr5gDataMetricsWrapper: Nr5gDataMetricsWrapper
    private lateinit var deviceInfoDataCollector: Nr5gDeviceInfoDataCollector


    @Before
    fun setUp() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
    }

    @After
    fun tearDown() {
    }


    @Test
    fun convertNr5gMmwCellLogEntityObjectTest() {
        val nr5GMmwCellLog1 = Nr5gMmwCellLog(
            "14356778678",
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
        val nr5gMmwCellLogEntity
                = Nsa5gEntityConverter.convertNr5gMmwCellLogEntityObject(nr5GMmwCellLog1)
        assertEquals(nr5GMmwCellLog1.networkType, nr5gMmwCellLogEntity?.networkType)
    }

    @Test
    fun convertEndcLteLogEntityObjectTest() {
        val endcLteLog = EndcLteLog(
            "14356778678",
            2,
            1,
            1,
            1,
            3
        )
        val endcLteLogEntity
                = Nsa5gEntityConverter.convertEndcLteLogEntityObject(endcLteLog)
        assertEquals(endcLteLog.networkType, endcLteLogEntity?.networkType)
    }

    @Test
    fun convertNr5gUiLogEntityObjectTest() {
        val nr5gUiLog = Nr5gUiLog(
            "14356778678",
            10,
            "abc",
            "abc",
            10,
            1
        )
        val nr5gUiLogEntity
                = Nsa5gEntityConverter.convertNr5gUiLogEntityObject(nr5gUiLog)
        assertEquals(nr5gUiLog.networkType, nr5gUiLogEntity?.networkType)
    }

    @Test
    fun convertEndcUpLinkLogEntityObjectTest() {
        val endcUplinkLog = EndcUplinkLog(
            "14356778678",
            10,
            1
        )
        val endcUplinkLogEntity
                = Nsa5gEntityConverter.convertEndcUpLinkLogEntityObject(endcUplinkLog)
        assertEquals(endcUplinkLog.networkType, endcUplinkLogEntity?.networkType)
    }


    /**
     * This method convert Nr5g WifiStateEntity test
     */
    @Test
    fun convertNr5gWifiStateEntityTest() {
        val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val nr5gWifiStateEntity =
            Nr5gWifiStateEntity(
                wifiManager.wifiState
            )
        val nr5gWifiStateEntity1 = Nsa5gEntityConverter.convertNr5gWifiStateEntity(wifiManager)
        assertEquals(
            nr5gWifiStateEntity.getWiFiState,
            nr5gWifiStateEntity1.getWiFiState
        )
    }

}