
package com.tmobile.mytmobile.echolocate.appstartmanager

import android.Manifest
import android.content.Context
import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.tmobile.mytmobile.echolocate.authentication.AuthenticationManager
import org.junit.Assert
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@Suppress("DEPRECATION")
@RunWith(AndroidJUnit4::class)
class AppStartManagerTest {

    lateinit var instrumentationContext: Context

    @Before
    fun setUp() {
        instrumentationContext = InstrumentationRegistry.getInstrumentation().targetContext
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            InstrumentationRegistry.getInstrumentation().uiAutomation
                .grantRuntimePermission(
                    instrumentationContext.packageName, Manifest.permission.READ_PHONE_STATE)
        } else {
            InstrumentationRegistry.getInstrumentation().uiAutomation.executeShellCommand(
                "pm grant " + instrumentationContext.packageName
                        + " android.permission.READ_PHONE_STATE"
            )
        }
    }

    @Ignore
    @Test
    fun initializeManagerEnableTest() {
        val latch = CountDownLatch(1)
        latch.await(1, TimeUnit.MINUTES)
        Assert.assertTrue(AuthenticationManager.getInstance(instrumentationContext).getSavedToken().length > 4)
    }
}

