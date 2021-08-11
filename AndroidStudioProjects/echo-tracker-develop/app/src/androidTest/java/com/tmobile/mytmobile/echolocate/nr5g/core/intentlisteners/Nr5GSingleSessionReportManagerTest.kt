package com.tmobile.mytmobile.echolocate.nr5g.core.intentlisteners

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.test.platform.app.InstrumentationRegistry
import com.tmobile.mytmobile.echolocate.nr5g.manager.Nsa5gDataManager
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class Nr5GSingleSessionReportManagerTest {

    lateinit var instrumentationContext: Context

    @Before
    fun setUp() {
        instrumentationContext = InstrumentationRegistry.getInstrumentation().targetContext
    }

    @Test
    fun testSendBroadCast() {
        val CELL_ID_EXTRA = "cellId"

        val nr5GDataManager = Nsa5gDataManager(instrumentationContext)
        nr5GDataManager.baseNr5gAppBroadcastReceiver = BaseNr5gBroadcastReceiver()
        nr5GDataManager.baseNr5gAppBroadcastReceiver!!.setListener(nr5GDataManager)
        nr5GDataManager.baseNr5gScreenBroadcastReceiver = BaseNr5gBroadcastReceiver()
        nr5GDataManager.baseNr5gScreenBroadcastReceiver!!.setListener((nr5GDataManager))
        val intent = Intent("diagandroid.app.ApplicationState")
        nr5GDataManager.initNr5gDataManager()
        intent.putExtra(CELL_ID_EXTRA, 2)
        val broadCastManager = LocalBroadcastManager.getInstance(instrumentationContext)
        val intentFilter = IntentFilter("diagandroid.app.ApplicationState")

        broadCastManager.registerReceiver(nr5GDataManager.baseNr5gAppBroadcastReceiver!!, intentFilter)
        val status = broadCastManager.sendBroadcast(intent)
        Assert.assertTrue(status)
    }
}
