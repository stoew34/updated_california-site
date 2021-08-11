package com.tmobile.mytmobile.echolocate.authentication


import android.Manifest
import android.content.Context
import android.os.Build
import androidx.test.annotation.UiThreadTest
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.tmobile.mytmobile.echolocate.authentication.provider.AuthenticationProvider
import com.tmobile.mytmobile.echolocate.authentication.provider.ITokenReceivedListener
import com.tmobile.mytmobile.echolocate.authentication.utils.TokenSharedPreference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.junit.Assert
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.Semaphore
import java.util.concurrent.TimeUnit


@RunWith(AndroidJUnit4::class)
class AuthenticationProviderTest {

    private lateinit var context: Context
    private val semaphore: Semaphore = Semaphore(0)

    @Before
    fun setUp() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        TokenSharedPreference.init(context)
        //setting default value
        TokenSharedPreference.tokenObject = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            InstrumentationRegistry.getInstrumentation().uiAutomation
                .grantRuntimePermission(
                    context.packageName,
                    Manifest.permission.READ_PHONE_STATE
                )
        } else {
            InstrumentationRegistry.getInstrumentation().uiAutomation.executeShellCommand(
                "pm grant " + context.packageName
                        + " android.permission.READ_PHONE_STATE"
            )
        }
    }

    @Test
    @UiThreadTest
    fun testGetDATSilent() {
        GlobalScope.launch(Dispatchers.Main) {
            AuthenticationProvider.getInstance(context)
                .getToken(object :
                    ITokenReceivedListener {
                    override fun onReceivedToken(token: String) {
                        Assert.assertTrue(token.isNotEmpty())
                        semaphore.release()
                    }
                })
        }
        semaphore.tryAcquire(5, TimeUnit.SECONDS)
    }


    @Ignore
    @Test
    @UiThreadTest
    fun testGetDATFromPreferences() {
        val token = AuthenticationProvider.getInstance(context).getToken()
        assert(token != null)
    }


    @Test
    fun testisLocallyStoredTokenExpired() {
        TokenSharedPreference.tokenObject =
            "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6IlVGQkVSRUZVTFRJd01Uaz0ifQ.eyJpc3MiOiJodHRwczovL3BwZC5icmFzcy5hY2NvdW50LnQtbW9iaWxlLmNvbSIsImF1ZCI6IlVOS05PV04iLCJuZXR3b3JrIjp7fSwiZGV2aWNlIjp7ImNuZiI6Ii0tLS0tQkVHSU4gUFVCTElDIEtFWS0tLS0tXG5NSUlCSWpBTkJna3Foa2lHOXcwQkFRRUZBQU9DQVE4QU1JSUJDZ0tDQVFFQStTcWkxSVhKTHIrckVvQVY1Z2JXXG5DSXo1cXpPRVpFS0FWQnhRNTBRSnJtRUh0ak1KbjE2SkZPYXFUZG85WERBQWc0ZTRrVG1QL1V5UTlMZXlCQUtzXG5kdU1MMFpvZ081amk4UlBpc2FiL3NLeGJQR3ZNOW92ZWUyWk9rYUlURkYxVy8rbThBNlpDSmxnVEQyWWJpUkpQXG43bkppTW9wUE5JaXdTWWxnYUtCWEFDcEZXUzF6UzVEcEx2UmdRNkJ0U2NidTlvYW84cFFidFRXeWc4d1REaEJEXG5hc3MrRmRyRUM0Umw4VWRDdFhuUkF4dW9IQ3dYWlJhTXlHQWpGQi92c2FCeC9JMkhkWlJJTFQ0MUJHbWxIR25yXG4weHJWM1d2dStsQ3kvMXNNN2lJVklaRkExMGplWE5TNUM5TWdabE5iZ3dzQ3lOa3RSS05HNWJuMWlnMUs4a011XG41UUlEQVFBQlxuLS0tLS1FTkQgUFVCTElDIEtFWS0tLS0tIiwibXNpc2RuIjoiKzE1NTU1MjE1NTU0IiwiaW1laSI6IjM1ODI0MDA1MTExMTExMCIsImltc2kiOiIzMTAyNjAwMDAwMDAwMDAifSwiZXhwIjoxNTY3MDg3MzE0LCJpYXQiOjE1NjcwMDA5MTR9.ugjf_FVVbiALl21ENTGt84m3Og-8XH7Z0jYatZ5InyDbpmMPQAYkTZgueCQRFink-al9vNDYYWwM83wkkJuiaswbigI1hh5hfLidtmY3dKVaqY8Wfvw6myuRHkAiECOpeZtD2_DNajPYwAyyMSyqzmaNM4t9yqAd-DTPSZEcl6v2eZOTHxHObcR5qgkrHS5T0sAPeC18jObT_VQ_sKJWt_Mo7czJRE6CZ4ON0hlFHhnfV4YHjKSqoRmFWFDPBcGN2QjcPpHF2X9BS6eJ15lZi5tCYWa6oGVxssg-J0Ltr8PbqUz9YIhTq2fmochJADWvTQhTctmxex367PvyLgTIqw"
        assert(AuthenticationProvider.getInstance(context).isLocallyStoredTokenExpired())
    }

    @Test
    fun testisTokenExpired() {
        val token =
            "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6IlVGQkVSRUZVTFRJd01Uaz0ifQ.eyJpc3MiOiJodHRwczovL3BwZC5icmFzcy5hY2NvdW50LnQtbW9iaWxlLmNvbSIsImF1ZCI6IlVOS05PV04iLCJuZXR3b3JrIjp7fSwiZGV2aWNlIjp7ImNuZiI6Ii0tLS0tQkVHSU4gUFVCTElDIEtFWS0tLS0tXG5NSUlCSWpBTkJna3Foa2lHOXcwQkFRRUZBQU9DQVE4QU1JSUJDZ0tDQVFFQStTcWkxSVhKTHIrckVvQVY1Z2JXXG5DSXo1cXpPRVpFS0FWQnhRNTBRSnJtRUh0ak1KbjE2SkZPYXFUZG85WERBQWc0ZTRrVG1QL1V5UTlMZXlCQUtzXG5kdU1MMFpvZ081amk4UlBpc2FiL3NLeGJQR3ZNOW92ZWUyWk9rYUlURkYxVy8rbThBNlpDSmxnVEQyWWJpUkpQXG43bkppTW9wUE5JaXdTWWxnYUtCWEFDcEZXUzF6UzVEcEx2UmdRNkJ0U2NidTlvYW84cFFidFRXeWc4d1REaEJEXG5hc3MrRmRyRUM0Umw4VWRDdFhuUkF4dW9IQ3dYWlJhTXlHQWpGQi92c2FCeC9JMkhkWlJJTFQ0MUJHbWxIR25yXG4weHJWM1d2dStsQ3kvMXNNN2lJVklaRkExMGplWE5TNUM5TWdabE5iZ3dzQ3lOa3RSS05HNWJuMWlnMUs4a011XG41UUlEQVFBQlxuLS0tLS1FTkQgUFVCTElDIEtFWS0tLS0tIiwibXNpc2RuIjoiKzE1NTU1MjE1NTU0IiwiaW1laSI6IjM1ODI0MDA1MTExMTExMCIsImltc2kiOiIzMTAyNjAwMDAwMDAwMDAifSwiZXhwIjoxNTY3MDg3MzE0LCJpYXQiOjE1NjcwMDA5MTR9.ugjf_FVVbiALl21ENTGt84m3Og-8XH7Z0jYatZ5InyDbpmMPQAYkTZgueCQRFink-al9vNDYYWwM83wkkJuiaswbigI1hh5hfLidtmY3dKVaqY8Wfvw6myuRHkAiECOpeZtD2_DNajPYwAyyMSyqzmaNM4t9yqAd-DTPSZEcl6v2eZOTHxHObcR5qgkrHS5T0sAPeC18jObT_VQ_sKJWt_Mo7czJRE6CZ4ON0hlFHhnfV4YHjKSqoRmFWFDPBcGN2QjcPpHF2X9BS6eJ15lZi5tCYWa6oGVxssg-J0Ltr8PbqUz9YIhTq2fmochJADWvTQhTctmxex367PvyLgTIqw"
        assert(AuthenticationProvider.getInstance(context).isTokenExpired(token))
    }

}
