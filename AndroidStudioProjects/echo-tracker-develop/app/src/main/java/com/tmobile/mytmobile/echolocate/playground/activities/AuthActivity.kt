package com.tmobile.mytmobile.echolocate.playground.activities

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.tmobile.mytmobile.echolocate.R
import com.tmobile.mytmobile.echolocate.authentication.AuthenticationManager
import com.tmobile.mytmobile.echolocate.authentication.provider.AuthenticationProvider
import com.tmobile.mytmobile.echolocate.authentication.provider.ITokenReceivedListener
import com.tmobile.mytmobile.echolocate.utils.EchoLocateLog

/**
 * Activity to fetch the token / Refresh the token  - fetch from Asdk
 */
class AuthActivity : AppCompatActivity(), ITokenReceivedListener {

    private lateinit var btnAuthToken: Button
    private lateinit var btnRefreshAuthToken: Button
    private lateinit var btnClearAuthToken: Button
    private var authManager: AuthenticationManager? = null
    private var tokenText: TextView? = null
    var count = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth_token)
        initViews()

        if (authManager == null) {
            authManager = AuthenticationManager(this)
        }

        btnAuthToken.setOnClickListener {
            getAuthToken()
        }

        btnRefreshAuthToken.setOnClickListener {
            refreshAuthToken()
        }

        btnClearAuthToken.setOnClickListener {
            clearAuthToken()
        }
    }

    private fun getAuthToken() {
        count = 0
        authManager!!.initAgentAndGetDatSilent(this)
    }

    private fun refreshAuthToken() {
        EchoLocateLog.eLogD("refresh token clicked", System.currentTimeMillis())
        count = 0
        AuthenticationProvider.getInstance(this).refreshToken(
            object : ITokenReceivedListener {
                override fun onReceivedToken(token: String) {
                    ++count
                    runOnUiThread {
                        tokenText?.text = "Total token refreshed : $count \n$token"
                    }

                }
            }
        )
//        Thread.sleep(5000)
//        EchoLocateLog.eLogD("Total tokens Refresh = $count")
    }

    private fun clearAuthToken() {
        tokenText?.text = "Token Cleared"
        EchoLocateLog.eLogD("clear auth token clicked", System.currentTimeMillis())
    }

    private fun initViews() {
        btnAuthToken = findViewById(R.id.btn_get_auth_token)
        btnRefreshAuthToken = findViewById(R.id.btn_refresh_auth_token)
        btnClearAuthToken = findViewById(R.id.btn_clear_auth_token)
        tokenText = findViewById(R.id.auth_token)
    }

    override fun onReceivedToken(token: String) {
        EchoLocateLog.eLogD("handleToken call back = " + token, System.currentTimeMillis())
        count++
        runOnUiThread {
            tokenText?.text = "Total token get : $count \n$token"
        }
        EchoLocateLog.eLogD("Total tokens get = $count")
    }
}
