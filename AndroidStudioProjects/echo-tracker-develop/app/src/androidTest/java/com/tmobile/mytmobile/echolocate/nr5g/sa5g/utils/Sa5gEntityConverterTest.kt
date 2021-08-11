//package com.tmobile.mytmobile.echolocate.nr5g.sa5g.utils
//
//import android.content.Context
//import androidx.test.platform.app.InstrumentationRegistry
//import junit.framework.Assert
//import org.junit.After
//import org.junit.Before
//import org.junit.Ignore
//import org.junit.Test
//
//class Sa5gEntityConverterTest {
//
//    private lateinit var sa5gEntityConverter: Sa5gEntityConverter
//    private lateinit var context: Context
//
//
//    @Before
//    fun setUp() {
//        context = InstrumentationRegistry.getInstrumentation().targetContext
//        sa5gEntityConverter = Sa5gEntityConverter()
//    }
//
//    @After
//    fun tearDown() {
//    }
//
//    @Ignore
//    fun testconvertDownlinkCarrierLogsToEntity() {
//        val sa5gDownlinkCarrierLogs = Sa5gDownlinkCarrierLogs(
//            "NO_SIGNAL",
//            "n71",
//            "1",
//            "20",
//            "2",
//            "2",
//            "256QAM",
//            "9",
//            "8",
//            "20508685",
//            "123",
//            "11316",
//            "123",
//            "2147483647f",
//            "2147483647f",
//            "2147483647f",
//            "2147483647f",
//            "2147483647f",
//            "109f",
//            "14f",
//            "109f",
//            "14f"
//        )
//        val sa5gDownlinkCarrierEntity =
//            Sa5gEntityConverter.convertDownlinkCarrierLogsToEntity(sa5gDownlinkCarrierLogs)
//        Assert.assertEquals(sa5gDownlinkCarrierLogs.techType, sa5gDownlinkCarrierEntity?.techType)
//
//    }
//
//    @Test
//    fun testConvertNetworkLogToEntity() {
//        val sa5gNetworkLog = Sa5gNetworkLog("310", "260", "-999", "-999")
//        val sa5gNetworkLogEntity = Sa5gEntityConverter.convertNetworkLogToEntity(sa5gNetworkLog)
//        Assert.assertEquals(sa5gNetworkLog.mcc, sa5gNetworkLogEntity?.mcc)
//    }
//
//
//    @Test
//    fun testRrcLogToEntity() {
//        val sa5gRrcLog = Sa5gRrcLog("CONNECTED", "INACTIVE")
//        val sa5gRrcLogEntity = Sa5gEntityConverter.convertRrcLogToEntity(sa5gRrcLog)
//        Assert.assertEquals(sa5gRrcLog.lteRrcState, sa5gRrcLogEntity?.lteRrcState)
//    }
//
//    @Ignore
//    fun testsa5gSettingsLog() {
//        val sa5gSettingsLog = Sa5gSettingsLog(
//            "3",
//            "1",
//            "2",
//            "4",
//            "2",
//            "6"
//        )
//        val sa5gSettingsLogEntity = Sa5gEntityConverter.convertSettingsLogToEntity(sa5gSettingsLog)
//        Assert.assertEquals(sa5gSettingsLog.networkMode, sa5gSettingsLogEntity?.networkMode)
//    }
//
//    @Ignore
//    fun testSa5gUiLog() {
//        val sa5gUiLog = Sa5gUiLog(
//            Sa5gDateUtils.getTriggerTimeStamp(),
//            "3",
//            "3",
//            "LTE",
//            "2"
//        )
//        val sa5gUiLogEntity = Sa5gEntityConverter.convertUiLogToEntity(sa5gUiLog)
//        Assert.assertEquals(sa5gUiLog.networkType, sa5gUiLogEntity?.networkType)
//    }
//
//}
