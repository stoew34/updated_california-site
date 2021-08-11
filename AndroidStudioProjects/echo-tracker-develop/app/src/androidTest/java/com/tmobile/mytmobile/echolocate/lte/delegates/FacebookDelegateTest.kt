package com.tmobile.mytmobile.echolocate.lte.delegates

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.test.platform.app.InstrumentationRegistry
import com.tmobile.mytmobile.echolocate.lte.intentlisteners.BaseLteBroadcastReceiver
import com.tmobile.mytmobile.echolocate.lte.manager.LteDataManager
import com.tmobile.mytmobile.echolocate.lte.utils.LteConstants
import com.tmobile.mytmobile.echolocate.lte.utils.LteIntents
import junit.framework.TestCase
import org.junit.Assert
import org.junit.Before
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runners.MethodSorters
import java.util.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

/**
 * This class handles the facebook delegate unit test cases.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class FacebookDelegateTest {
    lateinit var instrumentationContext: Context
    lateinit var broadCastManager : LocalBroadcastManager
    @Before
    fun setUp() {
        instrumentationContext = InstrumentationRegistry.getInstrumentation().targetContext
        broadCastManager = LocalBroadcastManager.getInstance(instrumentationContext)
    }

    /**
     * This will test teh correct base echo locate lte entity return or not
     */
    @Test
    @kotlinx.serialization.ImplicitReflectionSerializer
    fun getBaseEchoLocateLteEntityTest() {
        val api_version = LteConstants.API_VERSION
        val schema_version = LteConstants.SCHEMA_VERSION
        val triggerCode = 201

        val sessionId = UUID.randomUUID().toString()
        val baseEchoLocateLteEntity = FacebookDelegate.getInstance(instrumentationContext).prepareEchoLocateLteEntity(triggerCode, sessionId)
        TestCase.assertEquals(baseEchoLocateLteEntity?.oemApiVersion, api_version.toString())
        TestCase.assertEquals(baseEchoLocateLteEntity?.schemaVersion, schema_version)
        TestCase.assertEquals(baseEchoLocateLteEntity?.trigger, triggerCode)
        TestCase.assertEquals(baseEchoLocateLteEntity?.sessionId, sessionId)
    }

    /**
     * Testcase for focus gain facebook event
     */
    @Test
    fun testFocusGainSendBroadCast() {
        val latch = CountDownLatch(1)

        val NETWORKBAND_EXTRA = "networkBand"
        val CELL_ID_EXTRA = "cellId"
        val NETWORKTYPE_EXTRA = "networkType"
        val CALL_ID_EXTRA = "CallID"
        val CALL_NUMBER_EXTRA = "CallNumber"
        val PACKAGE_NAME_EXTRA = "ApplicationPackageName"
        val OEM_TIMESTAMP_EXTRA = "oemIntentTimestamp"


        val lteModuleManager = LteDataManager(instrumentationContext)
        lteModuleManager.baseLteBroadcastReceiver = BaseLteBroadcastReceiver()
        lteModuleManager.baseLteBroadcastReceiver!!.setListener(lteModuleManager)
        lteModuleManager.initLteDataManager()

        val intent = Intent("diagandroid.app.ApplicationState")

        intent.putExtra(NETWORKBAND_EXTRA, "NETWORKBAND")
        intent.putExtra(NETWORKTYPE_EXTRA, "NWTYPE")
        intent.putExtra(CELL_ID_EXTRA, 2)
        intent.putExtra(CALL_ID_EXTRA, "CALLID")
        intent.putExtra(PACKAGE_NAME_EXTRA, "com.facebook.katana")
        intent.putExtra(CALL_NUMBER_EXTRA, "CALLNUMBER")
        intent.putExtra(OEM_TIMESTAMP_EXTRA, "oemIntentTimestamp")
        intent.putExtra(LteIntents.APP_STATE_KEY, "FOCUS_GAIN")

        val intentFilter = IntentFilter("diagandroid.app.ApplicationState")
        LocalBroadcastManager.getInstance(instrumentationContext)
            .registerReceiver(lteModuleManager.baseLteBroadcastReceiver!!, intentFilter)
        val status = broadCastManager.sendBroadcast(intent)
        latch.await(1, TimeUnit.SECONDS)
        Assert.assertTrue(status)
        broadCastManager.unregisterReceiver(lteModuleManager.baseLteBroadcastReceiver!!)
    }


    /**
     * Testcase for focus loss facebook event
     */
    @Test
    fun testFocusLossBroadCast() {
        val latch = CountDownLatch(1)

        val NETWORKBAND_EXTRA = "networkBand"
        val CELL_ID_EXTRA = "cellId"
        val NETWORKTYPE_EXTRA = "networkType"
        val CALL_ID_EXTRA = "CallID"
        val CALL_NUMBER_EXTRA = "CallNumber"
        val PACKAGE_NAME_EXTRA = "ApplicationPackageName"
        val OEM_TIMESTAMP_EXTRA = "oemIntentTimestamp"

        val lteModuleManager = LteDataManager(instrumentationContext)
        lteModuleManager.baseLteBroadcastReceiver = BaseLteBroadcastReceiver()
        lteModuleManager.baseLteBroadcastReceiver!!.setListener(lteModuleManager)
        lteModuleManager.initLteDataManager()
        val intent = Intent("diagandroid.app.ApplicationState")

        intent.putExtra(NETWORKBAND_EXTRA, "NETWORKBAND")
        intent.putExtra(NETWORKTYPE_EXTRA, "NWTYPE")
        intent.putExtra(CELL_ID_EXTRA, 2)
        intent.putExtra(CALL_ID_EXTRA, "CALLID")
        intent.putExtra(PACKAGE_NAME_EXTRA, "com.facebook.katana")
        intent.putExtra(CALL_NUMBER_EXTRA, "CALLNUMBER")
        intent.putExtra(OEM_TIMESTAMP_EXTRA, "oemIntentTimestamp")
        intent.putExtra(LteIntents.APP_STATE_KEY, "FOCUS_LOSS")

        val intentFilter = IntentFilter("diagandroid.app.ApplicationState")
        broadCastManager.registerReceiver(lteModuleManager.baseLteBroadcastReceiver!!, intentFilter)
        val status = broadCastManager.sendBroadcast(intent)
        latch.await(1, TimeUnit.SECONDS)
        Assert.assertTrue(status)

    }

    /**
     * Testcase for screen off facebook event
     */
    @Test
    fun testScreenOffBroadCast() {
        val latch = CountDownLatch(1)

        val NETWORKBAND_EXTRA = "networkBand"
        val CELL_ID_EXTRA = "cellId"
        val NETWORKTYPE_EXTRA = "networkType"
        val CALL_ID_EXTRA = "CallID"
        val CALL_NUMBER_EXTRA = "CallNumber"
        val PACKAGE_NAME_EXTRA = "ApplicationPackageName"
        val OEM_TIMESTAMP_EXTRA = "oemIntentTimestamp"

        val lteModuleManager = LteDataManager(instrumentationContext)
        lteModuleManager.baseLteBroadcastReceiver = BaseLteBroadcastReceiver()
        lteModuleManager.baseLteBroadcastReceiver!!.setListener(lteModuleManager)
        lteModuleManager.initLteDataManager()
        val intent = Intent("android.intent.action.SCREEN_OFF")

        intent.putExtra(NETWORKBAND_EXTRA, "NETWORKBAND")
        intent.putExtra(NETWORKTYPE_EXTRA, "NWTYPE")
        intent.putExtra(CELL_ID_EXTRA, 2)
        intent.putExtra(CALL_ID_EXTRA, "CALLID")
        intent.putExtra(PACKAGE_NAME_EXTRA, "com.facebook.katana")
        intent.putExtra(CALL_NUMBER_EXTRA, "CALLNUMBER")
        intent.putExtra(OEM_TIMESTAMP_EXTRA, "oemIntentTimestamp")
        intent.putExtra(LteIntents.APP_STATE_KEY, "FOCUS_LOSS")

        val intentFilter = IntentFilter("android.intent.action.SCREEN_OFF")
        broadCastManager.registerReceiver(lteModuleManager.baseLteBroadcastReceiver!!, intentFilter)
        val status = broadCastManager.sendBroadcast(intent)
        latch.await(1, TimeUnit.SECONDS)
        Assert.assertTrue(status)

    }

}