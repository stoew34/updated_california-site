package com.tmobile.mytmobile.echolocate.nr5g

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import com.tmobile.mytmobile.echolocate.nr5g.core.intentlisteners.BaseNr5gBroadcastReceiver
import com.tmobile.mytmobile.echolocate.nr5g.core.utils.Nr5gIntents
import com.tmobile.mytmobile.echolocate.nr5g.manager.Base5gDataManager
import com.tmobile.mytmobile.echolocate.nr5g.manager.Sa5gDataManager
import com.tmobile.mytmobile.echolocate.nr5g.nsa5g.database.EchoLocateNr5gDatabase
import com.tmobile.mytmobile.echolocate.nr5g.nsa5g.database.dao.Nr5gDao
import com.tmobile.pr.androidcommon.system.reflection.TmoBaseReflection
import org.junit.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@Ignore
class Nr5GModuleTest {

    private lateinit var nr5gDao: Nr5gDao
    private lateinit var db: EchoLocateNr5gDatabase
    lateinit var context: Context
    private lateinit var broadCastManager: LocalBroadcastManager
    private var nr5gDataManager: Base5gDataManager? = null

    @Before
    fun setUp() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        db = Room.inMemoryDatabaseBuilder(context, EchoLocateNr5gDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        InstrumentationRegistry.getInstrumentation().uiAutomation
            .grantRuntimePermission(
                context.packageName,
                Manifest.permission.READ_PHONE_STATE
            )

        nr5gDao = EchoLocateNr5gDatabase.getEchoLocateNr5gDatabase(context).nr5gDao()

        broadCastManager = LocalBroadcastManager.getInstance(context)
        nr5gDataManager =  Sa5gDataManager(context)

       if(nr5gDataManager != null) {
           nr5gDataManager?.baseNr5gAppBroadcastReceiver = BaseNr5gBroadcastReceiver()
           nr5gDataManager?.baseNr5gAppBroadcastReceiver!!.setListener(nr5gDataManager!!)

           val isNr5gSupported = TmoBaseReflection.findField(
                   Base5gDataManager::class.java,
                   "isNr5gSupported"
           )
           isNr5gSupported.isAccessible = true
           isNr5gSupported.setBoolean(nr5gDataManager, true)

           nr5gDataManager?.initNr5gDataManager()
       }
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun testFocusGain() {
        val latch = CountDownLatch(1)
        val NETWORKBAND_EXTRA = "networkBand"
        val CELL_ID_EXTRA = "cellId"
        val NETWORKTYPE_EXTRA = "networkType"
        val CALL_ID_EXTRA = "CallID"
        val CALL_NUMBER_EXTRA = "CallNumber"
        val PACKAGE_NAME_EXTRA = "ApplicationPackageName"
        val OEM_TIMESTAMP_EXTRA = "oemIntentTimestamp"

        val intent = Intent("diagandroid.app.ApplicationState")

        intent.putExtra(NETWORKBAND_EXTRA, "NETWORKBAND")
        intent.putExtra(NETWORKTYPE_EXTRA, "NWTYPE")
        intent.putExtra(CELL_ID_EXTRA, 2)
        intent.putExtra(CALL_ID_EXTRA, "CALLID")
        intent.putExtra(PACKAGE_NAME_EXTRA, "com.facebook.katana")
        intent.putExtra(CALL_NUMBER_EXTRA, "CALLNUMBER")
        intent.putExtra(OEM_TIMESTAMP_EXTRA, "oemIntentTimestamp")
        intent.putExtra(Nr5gIntents.APP_STATE_KEY, "FOCUS_GAIN")

        val intentFilter = IntentFilter("diagandroid.app.ApplicationState")
        broadCastManager.registerReceiver(nr5gDataManager?.baseNr5gAppBroadcastReceiver!!, intentFilter)
        val status = broadCastManager.sendBroadcast(intent)
        latch.await(1, TimeUnit.SECONDS)
        Assert.assertTrue(status)
        broadCastManager.unregisterReceiver(nr5gDataManager?.baseNr5gAppBroadcastReceiver!!)
        nr5gDataManager?.setAppBroadCastRegistered(false)
    }
}