package com.tmobile.mytmobile.echolocate.authentication

import android.content.Context
import androidx.test.annotation.UiThreadTest
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.tmobile.mytmobile.echolocate.authentication.provider.ITokenReceivedListener
import com.tmobile.mytmobile.echolocate.authentication.utils.TokenSharedPreference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.Semaphore
import java.util.concurrent.TimeUnit


@RunWith(AndroidJUnit4::class)
class AuthenticationManagerTest {

    private lateinit var instrumentationContext: Context
    private val semaphore: Semaphore = Semaphore(0)

    @Before
    fun setUp() {
        instrumentationContext = InstrumentationRegistry.getInstrumentation().targetContext
        TokenSharedPreference.init(instrumentationContext)
        //setting default value
        TokenSharedPreference.tokenObject = null
    }

    @Test
    fun testInitAgentAndGetDatSilent() {

        GlobalScope.launch(Dispatchers.Main) {
            AuthenticationManager.getInstance(instrumentationContext)
                .initAgentAndGetDatSilent(object :
                    ITokenReceivedListener {
                    override fun onReceivedToken(token: String) {
                        Assert.assertTrue(token.isNotEmpty())
                        semaphore.release()
                    }
                })
        }

        semaphore.tryAcquire(5, TimeUnit.SECONDS)
        Assert.assertTrue(TokenSharedPreference.tokenObject != null)

    }

    @Test
    @UiThreadTest
    fun testGetSavedToken() {
        val savedTokenString =
            AuthenticationManager.getInstance(instrumentationContext).getSavedToken()
        Assert.assertTrue(savedTokenString != null)
    }

}
