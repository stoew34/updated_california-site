package com.tmobile.mytmobile.echolocate.playground.activities

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.tmobile.mytmobile.echolocate.R
import com.tmobile.mytmobile.echolocate.authentication.provider.AuthenticationProvider
import com.tmobile.mytmobile.echolocate.authentication.provider.ITokenReceivedListener
import com.tmobile.mytmobile.echolocate.nr5g.Nr5gModuleProvider
import com.tmobile.mytmobile.echolocate.reportingmanager.ReportProvider
import com.tmobile.mytmobile.echolocate.utils.EchoLocateLog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ReportActivity : AppCompatActivity() {
    private var count = 0
    private lateinit var btn_dia_generate: Button
    private lateinit var btn_send_5g_report: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reports)
        initViews()
        btn_dia_generate.setOnClickListener {
            requestForReports()
        }
        btn_send_5g_report.setOnClickListener {
            sentReport5g()
        }

    }

    fun initViews() {
        btn_dia_generate = findViewById(R.id.btn_generate_dia)
        btn_send_5g_report = findViewById(R.id.btn_send_5g_report)
    }

    private fun requestForReports() {
        count = 0
        GlobalScope.launch(Dispatchers.IO) {
            if (AuthenticationProvider.getInstance(applicationContext)
                    .isLocallyStoredTokenExpired()
            ) {
                AuthenticationProvider.getInstance(applicationContext).refreshToken(
                    object : ITokenReceivedListener {
                        override fun onReceivedToken(token: String) {
                            count++
                            EchoLocateLog.eLogD("Requesting to send report = $count")
                            runOnUiThread {
                                Toast.makeText(
                                    applicationContext,
                                    "Request to send report = $count",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            ReportProvider.getInstance(applicationContext)
                                .instantRequestReportsFromAllModules()
                        }
                    }
                )
            } else {
                ReportProvider.getInstance(applicationContext).instantRequestReportsFromAllModules(null)
            }
        }
    }

    private fun sentReport5g() {
        GlobalScope.launch(Dispatchers.IO) {
            Nr5gModuleProvider.getInstance(applicationContext).send5gReport(null)
        }
    }
}
