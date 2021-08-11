package com.tmobile.mytmobile.echolocate.nr5g

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.test.platform.app.InstrumentationRegistry
import com.tmobile.mytmobile.echolocate.lte.utils.LteIntents
import com.tmobile.mytmobile.echolocate.nr5g.core.intentlisteners.BaseNr5gBroadcastReceiver
import com.tmobile.mytmobile.echolocate.nr5g.manager.Base5gDataManager
import com.tmobile.mytmobile.echolocate.nr5g.manager.Nsa5gDataManager
import com.tmobile.pr.androidcommon.system.reflection.TmoBaseReflection
import org.junit.Assert
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@Ignore
class ApplicationProcessorTest {

    private lateinit var context: Context
    private lateinit var nr5gDataManager: Base5gDataManager
    private val broadCastManager =
        LocalBroadcastManager.getInstance(InstrumentationRegistry.getInstrumentation().targetContext)

    @Before
    fun setUp() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        nr5gDataManager = Nsa5gDataManager(context)

        nr5gDataManager.baseNr5gAppBroadcastReceiver = BaseNr5gBroadcastReceiver()
        nr5gDataManager.baseNr5gAppBroadcastReceiver!!.setListener(nr5gDataManager)
        nr5gDataManager.baseNr5gScreenBroadcastReceiver = BaseNr5gBroadcastReceiver()
        nr5gDataManager.baseNr5gScreenBroadcastReceiver!!.setListener(nr5gDataManager)

        val isNr5gSupported = TmoBaseReflection.findField(
            Base5gDataManager::class.java,
            "isNr5gSupported"
        )
        isNr5gSupported.isAccessible = true
        isNr5gSupported.setBoolean(nr5gDataManager, true)

        nr5gDataManager.initNr5gDataManager()
    }

    /**
     * Testcase for focus gain  event
     */
    @Test
    fun testFocusGainSendBroadCast() {
        val latch = CountDownLatch(1)
        val PACKAGE_NAME_EXTRA = "ApplicationPackageName"
        val OEM_TIMESTAMP_EXTRA = "oemIntentTimestamp"

        val intent = Intent("diagandroid.app.ApplicationState")
        intent.putExtra(PACKAGE_NAME_EXTRA, "com.google.android.youtube")
        intent.putExtra(OEM_TIMESTAMP_EXTRA, "oemIntentTimestamp")
        intent.putExtra(LteIntents.APP_STATE_KEY, "FOCUS_GAIN")
        val intentFilter = IntentFilter("diagandroid.app.ApplicationState")

        broadCastManager.registerReceiver(nr5gDataManager.baseNr5gAppBroadcastReceiver!!, intentFilter)
        val status = broadCastManager.sendBroadcast(intent)
        latch.await(10, TimeUnit.SECONDS)
        Assert.assertTrue(status)
        broadCastManager.unregisterReceiver(nr5gDataManager.baseNr5gAppBroadcastReceiver!!)
    }

    /**
     * Testcase for focus loss  event
     */
    @Test
    fun testFocusLossSendBroadCast() {
        val latch = CountDownLatch(1)
        val PACKAGE_NAME_EXTRA = "ApplicationPackageName"
        val OEM_TIMESTAMP_EXTRA = "oemIntentTimestamp"

        val intent = Intent("diagandroid.app.ApplicationState")
        intent.putExtra(PACKAGE_NAME_EXTRA, "com.google.android.youtube")
        intent.putExtra(OEM_TIMESTAMP_EXTRA, "oemIntentTimestamp")
        intent.putExtra(LteIntents.APP_STATE_KEY, "FOCUS_LOSS")
        val intentFilter = IntentFilter("diagandroid.app.ApplicationState")

        broadCastManager.registerReceiver(nr5gDataManager.baseNr5gAppBroadcastReceiver!!, intentFilter)
        val status = broadCastManager.sendBroadcast(intent)
        latch.await(10, TimeUnit.SECONDS)
        Assert.assertTrue(status)
        broadCastManager.unregisterReceiver(nr5gDataManager.baseNr5gAppBroadcastReceiver!!)
    }
}