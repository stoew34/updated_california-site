package com.tmobile.mytmobile.echolocate.lte

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.test.platform.app.InstrumentationRegistry
import com.tmobile.mytmobile.echolocate.configuration.events.configurationevents.LteConfigEvent
import com.tmobile.mytmobile.echolocate.communicationbus.rxbus.PostTicket
import com.tmobile.mytmobile.echolocate.communicationbus.rxbus.RxBus
import com.tmobile.mytmobile.echolocate.configuration.model.LTE
import com.tmobile.mytmobile.echolocate.configuration.model.lteregex.NetflixRegex
import com.tmobile.mytmobile.echolocate.configuration.model.lteregex.SpeedTestRegex
import com.tmobile.mytmobile.echolocate.configuration.model.lteregex.YoutubeRegex
import com.tmobile.mytmobile.echolocate.lte.intentlisteners.BaseLteBroadcastReceiver

import com.tmobile.mytmobile.echolocate.lte.manager.LteDataManager
import com.tmobile.mytmobile.echolocate.lte.utils.LteIntents
import com.tmobile.mytmobile.echolocate.utils.EchoLocateLog
import org.junit.*
import org.junit.rules.TestName
import java.lang.Exception
import java.lang.IllegalArgumentException
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class LteDataManagerTest {
    lateinit var instrumentationContext: Context
    private lateinit var broadCastManager: LocalBroadcastManager
    private lateinit var lteDataManager: LteDataManager

    @get:Rule
    val name: TestName = TestName()

    companion object {
        private lateinit var bus: RxBus

        @AfterClass
        fun tearDown() {
            bus.destroy()
        }
    }

    @Before
    fun setUp() {
        instrumentationContext = InstrumentationRegistry.getInstrumentation().targetContext
        broadCastManager =
            LocalBroadcastManager.getInstance(InstrumentationRegistry.getInstrumentation().targetContext)
        lteDataManager = LteDataManager(instrumentationContext)
        lteDataManager.baseLteBroadcastReceiver = BaseLteBroadcastReceiver()
        lteDataManager.baseLteBroadcastReceiver!!.setListener(lteDataManager)
        lteDataManager.initLteDataManager()
        bus = RxBus.instance
    }

    @Test
    fun testSendBroadCast() {
        val NETWORKBAND_EXTRA = "networkBand"
        val CELL_ID_EXTRA = "cellId"
        val NETWORKTYPE_EXTRA = "networkType"
        val CALL_ID_EXTRA = "CallID"
        val CALL_NUMBER_EXTRA = "CallNumber"
        val PACKAGE_NAME_EXTRA = "ApplicationPackageName"
        val OEM_TIMESTAMP_EXTRA = "oemIntentTimestamp"


        val intent = Intent("diagandroid.app.receiveDetailedApplicationState")

        intent.putExtra(NETWORKBAND_EXTRA, "NETWORKBAND")
        intent.putExtra(NETWORKTYPE_EXTRA, "NWTYPE")
        intent.putExtra(CELL_ID_EXTRA, 2)
        intent.putExtra(CALL_ID_EXTRA, "CALLID")
        intent.putExtra(PACKAGE_NAME_EXTRA, instrumentationContext.packageName)
        intent.putExtra(CALL_NUMBER_EXTRA, "CALLNUMBER")
        intent.putExtra(OEM_TIMESTAMP_EXTRA, "oemIntentTimestamp")
        val broadCastManager = LocalBroadcastManager.getInstance(instrumentationContext)
        val intentFilter = IntentFilter("diagandroid.app.receiveDetailedApplicationState")
        broadCastManager.registerReceiver(lteDataManager.baseLteBroadcastReceiver!!, intentFilter)
        val status = broadCastManager.sendBroadcast(intent)
        Assert.assertTrue(status)
    }

    @Test
    fun testLteDisabled() {
        try {
            EchoLocateLog.eLogE("Diagnostic : all receivers are unregistered")
            instrumentationContext.unregisterReceiver(lteDataManager.baseLteBroadcastReceiver)
            lteDataManager.setBroadCastRegistered(false)
        } catch (ex: Exception) {
            assert(ex is IllegalArgumentException)
        }
    }

    @Test
    fun testPublishConfigChanges() {
        val latch = CountDownLatch(1)

        val packages = mutableListOf<String>()
        packages.add("com.google.android.youtube")
        packages.add("app_package_2")
        packages.add("app_package_3")

        val lte1 = LTE(
            true,
            6,
            packages,
            110,
            YoutubeRegex(listOf(), listOf()),
            NetflixRegex(listOf(), listOf()),
            SpeedTestRegex(listOf(), listOf(), listOf(), listOf(), listOf())
        ,"",
            listOf())
        val lteConfigEvent1 = LteConfigEvent(lte1)
        val postTicket1 = PostTicket(lteConfigEvent1)
        bus.post(postTicket1)

        val lte = LTE(
            false,
            6,
            packages,
            100,
            YoutubeRegex(listOf(), listOf()),
            NetflixRegex(listOf(), listOf()),
            SpeedTestRegex(listOf(), listOf(), listOf(), listOf(), listOf())
        ,"",
            listOf())
        val lteConfigEvent = LteConfigEvent(lte)
        val postTicket = PostTicket(lteConfigEvent)
        bus.post(postTicket)

        latch.await(1, TimeUnit.MILLISECONDS)
        try {
            instrumentationContext.unregisterReceiver(lteDataManager.baseLteBroadcastReceiver)
            lteDataManager.setBroadCastRegistered(false)
        } catch (ex: Exception) {
            EchoLocateLog.eLogE("Diagnostic : all receivers are unregistered")
            assert(ex is IllegalArgumentException)
        }
        RxBus.instance.destroy()
    }

    @Test
    fun testLteForPackagesEnabled() {
        val latch = CountDownLatch(1)
        val PACKAGE_NAME_EXTRA = "ApplicationPackageName"
        val OEM_TIMESTAMP_EXTRA = "oemIntentTimestamp"

        val packages = mutableListOf<String>()
        packages.add("com.google.android.youtube")
        packages.add("app_package_2")
        packages.add("app_package_3")
        val lte = LTE(
            true, 6, packages, 100, YoutubeRegex(listOf(), listOf()),
            NetflixRegex(listOf(), listOf()),
            SpeedTestRegex(listOf(), listOf(), listOf(), listOf(), listOf())
        ,"",
            listOf())
        val lteConfigEvent = LteConfigEvent(lte)
        val postTicket = PostTicket(lteConfigEvent)
        bus.post(postTicket)

        latch.await(1, TimeUnit.MILLISECONDS)
        val intent = Intent("diagandroid.app.ApplicationState")

        intent.putExtra(PACKAGE_NAME_EXTRA, "com.google.android.youtube")
        intent.putExtra(OEM_TIMESTAMP_EXTRA, "oemIntentTimestamp")
        intent.putExtra(LteIntents.APP_STATE_KEY, "FOCUS_GAIN")

        val intentFilter = IntentFilter("diagandroid.app.ApplicationState")
        broadCastManager.registerReceiver(lteDataManager.baseLteBroadcastReceiver!!, intentFilter)
        val status = broadCastManager.sendBroadcast(intent)
        latch.await(1, TimeUnit.SECONDS)
        Assert.assertTrue(status)
        broadCastManager.unregisterReceiver(lteDataManager.baseLteBroadcastReceiver!!)
        lteDataManager.setBroadCastRegistered(false)
        RxBus.instance.destroy()
    }

    @Test
    fun testLteForExcludedPackages() {
        val latch = CountDownLatch(1)
        val PACKAGE_NAME_EXTRA = "ApplicationPackageName"
        val OEM_TIMESTAMP_EXTRA = "oemIntentTimestamp"


        val packages = mutableListOf<String>()
        packages.add("com.google.android.youtube")
        packages.add("app_package_2")
        packages.add("app_package_3")
        val lte = LTE(
            true, 6, packages, 100, YoutubeRegex(listOf(), listOf()),
            NetflixRegex(listOf(), listOf()),
            SpeedTestRegex(listOf(), listOf(), listOf(), listOf(), listOf())
        ,"", listOf())
        val lteConfigEvent = LteConfigEvent(lte)
        val postTicket = PostTicket(lteConfigEvent)
        bus.post(postTicket)

        latch.await(1, TimeUnit.MILLISECONDS)
        val intent = Intent("diagandroid.app.ApplicationState")

        intent.putExtra(PACKAGE_NAME_EXTRA, "app_package_4")
        intent.putExtra(OEM_TIMESTAMP_EXTRA, "oemIntentTimestamp")
        intent.putExtra(LteIntents.APP_STATE_KEY, "FOCUS_GAIN")

        val intentFilter = IntentFilter("diagandroid.app.ApplicationState")
        broadCastManager.registerReceiver(lteDataManager.baseLteBroadcastReceiver!!, intentFilter)
        val status = broadCastManager.sendBroadcast(intent)
        latch.await(1, TimeUnit.SECONDS)
        Assert.assertTrue(status)

        RxBus.instance.destroy()
    }
}