package com.tmobile.mytmobile.echolocate.nr5g.manager

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import com.tmobile.mytmobile.echolocate.nr5g.core.intentlisteners.BaseNr5gBroadcastReceiver
import com.tmobile.pr.androidcommon.system.reflection.TmoBaseReflection
import org.junit.Assert
import org.junit.Before
import org.junit.Ignore
import org.junit.Test

class Nr5gDataManagerTest {

    lateinit var context: Context
    private lateinit var nr5gDataManager: Nsa5gDataManager

    @Before
    fun setUp() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        nr5gDataManager = Nsa5gDataManager(context)

        nr5gDataManager.baseNr5gAppBroadcastReceiver = BaseNr5gBroadcastReceiver()
        nr5gDataManager.baseNr5gAppBroadcastReceiver!!.setListener(nr5gDataManager)

        val isNr5gSupported = TmoBaseReflection.findField(
            Nsa5gDataManager::class.java,
            "isNr5gSupported"
        )
        isNr5gSupported.isAccessible = true
        isNr5gSupported.setBoolean(nr5gDataManager, true)

        nr5gDataManager.initNr5gDataManager()
    }

    @Ignore
    @Test
    fun testIsManagerInitialized() {
        Assert.assertEquals(nr5gDataManager.isManagerInitialized(),false)
    }


}