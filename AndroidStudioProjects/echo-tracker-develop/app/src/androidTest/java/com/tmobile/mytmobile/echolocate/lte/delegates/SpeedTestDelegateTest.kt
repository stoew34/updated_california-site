package com.tmobile.mytmobile.echolocate.lte.delegates

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.test.platform.app.InstrumentationRegistry
import com.tmobile.mytmobile.echolocate.lte.database.EchoLocateLteDatabase
import com.tmobile.mytmobile.echolocate.lte.database.dao.LteDao
import com.tmobile.mytmobile.echolocate.lte.intentlisteners.BaseLteBroadcastReceiver
import com.tmobile.mytmobile.echolocate.lte.manager.LteDataManager
import com.tmobile.mytmobile.echolocate.lte.utils.LTEApplications
import com.tmobile.mytmobile.echolocate.lte.utils.LteIntents
import com.tmobile.mytmobile.echolocate.utils.EchoLocateLog
import org.junit.Assert
import org.junit.Before
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runners.MethodSorters
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

/**
 * This class handles the speed delegate unit test cases.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class SpeedTestDelegateTest {
    lateinit var instrumentationContext: Context
    private lateinit var lteDao: LteDao
    val broadCastManager = LocalBroadcastManager.getInstance(InstrumentationRegistry.getInstrumentation().targetContext)
    private var delegateLte: SpeedTestDelegate? = null

    @Before
    fun setUp() {
        instrumentationContext = InstrumentationRegistry.getInstrumentation().targetContext
        lteDao = EchoLocateLteDatabase.getEchoLocateLteDatabase(instrumentationContext).lteDao()
        delegateLte = SpeedTestDelegate.getInstance(instrumentationContext)
    }


    /**
     * This function is used get trigger application
     */
    @Test
    fun getTriggerApplications(){
        val lteApplications: LTEApplications? = delegateLte?.getTriggerApplication()
        Assert.assertNotNull(lteApplications)
    }


    /**
     * Testcase for focus gain facebook event
     */
    @Test
    fun testFocusGainSendBroadCast() {
        val latch = CountDownLatch(1)
        val PACKAGE_NAME_EXTRA = "ApplicationPackageName"
        val OEM_TIMESTAMP_EXTRA = "oemIntentTimestamp"

        val lteDataManager = LteDataManager(instrumentationContext)
        lteDataManager.baseLteBroadcastReceiver = BaseLteBroadcastReceiver()
        lteDataManager.baseLteBroadcastReceiver!!.setListener(lteDataManager)
        lteDataManager.initLteDataManager()
        val intent = Intent("diagandroid.app.ApplicationState")

        intent.putExtra(PACKAGE_NAME_EXTRA, "org.zwanoo.android.speedtest")
        intent.putExtra(OEM_TIMESTAMP_EXTRA, "oemIntentTimestamp")
        intent.putExtra(LteIntents.APP_STATE_KEY, "FOCUS_GAIN")

        val intentFilter = IntentFilter("diagandroid.app.ApplicationState")
        broadCastManager.registerReceiver(lteDataManager.baseLteBroadcastReceiver!!, intentFilter)
        val status = broadCastManager.sendBroadcast(intent)
        latch.await(1, TimeUnit.SECONDS)
        Assert.assertTrue(status)
        broadCastManager.unregisterReceiver(lteDataManager.baseLteBroadcastReceiver!!)
    }


    /**
     * Testcase for speed test Test ready
     */
    @Test
    fun test_b_ReadyEvent() {
        val latch = CountDownLatch(1)
        val triggerCode = 910
        Log.e("org.zwanoo.android.speedtest",
            "2019-11-08 11:45:04.637 23412-23412/? D/SpeedTestHandler: SpeedTestHandler: state update: new: ENGINE_READY, old: PREPARING_ENGINE")
        latch.await(9, TimeUnit.SECONDS)
        val baseEchoLocateLteEntityList = lteDao.getBaseEchoLocateLteEntity()
        EchoLocateLog.eLogE("ApplicationState ${baseEchoLocateLteEntityList.get(0).sessionId}")
        assert(baseEchoLocateLteEntityList.get(0).trigger == triggerCode)
    }
}