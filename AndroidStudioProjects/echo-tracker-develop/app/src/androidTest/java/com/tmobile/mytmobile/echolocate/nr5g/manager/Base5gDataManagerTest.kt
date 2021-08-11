package com.tmobile.mytmobile.echolocate.nr5g.manager

import android.Manifest
import android.content.Context
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import com.tmobile.mytmobile.echolocate.nr5g.core.intentlisteners.BaseNr5gBroadcastReceiver
import com.tmobile.mytmobile.echolocate.nr5g.nsa5g.database.EchoLocateNr5gDatabase
import com.tmobile.mytmobile.echolocate.nr5g.nsa5g.database.dao.Nr5gDao
import com.tmobile.pr.androidcommon.system.reflection.TmoBaseReflection
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.io.IOException

internal class Base5gDataManagerTest {

    private lateinit var nr5gDao: Nr5gDao
    lateinit var context: Context
    private lateinit var broadCastManager: LocalBroadcastManager
    private var nr5gDataManager: Base5gDataManager? = null

    @Before
    fun setUp() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
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
                "isNsa5gSupported"
            )
            isNr5gSupported.isAccessible = true
            isNr5gSupported.setBoolean(nr5gDataManager, true)

        }
    }

    @Test
    fun initManagerTest(){
        nr5gDataManager?.initNr5gDataManager()
    }

    @Test
    fun stopNr5gDataCollection() {
        Assert.assertNotNull(nr5gDataManager?.stopNr5gDataCollection())
    }

    @Test
    fun registerAppReceiver() {
        Assert.assertNotNull(nr5gDataManager?.registerAppReceiver())
    }

    @Test
    fun registerScreenReceiver() {
        Assert.assertNotNull(nr5gDataManager?.registerScreenReceiver())
    }

    @Test
    fun setAppBroadCastRegistered() {
        Assert.assertNotNull(nr5gDataManager?.setAppBroadCastRegistered(false))
    }

    @Test
    fun setScreenBroadCastRegistered() {
        Assert.assertNotNull(nr5gDataManager?.setScreenBroadCastRegistered(false))
    }

    @Test
    fun isAppBroadCastRegistered() {
        Assert.assertNotNull(nr5gDataManager?.isAppBroadCastRegistered())
    }

    @Test
    fun isScreenBroadCastRegistered() {
        Assert.assertNotNull(nr5gDataManager?.isScreenBroadCastRegistered())
    }

    @Test
    fun isManagerInitialized() {
        Assert.assertNotNull(nr5gDataManager?.isManagerInitialized())
    }

}