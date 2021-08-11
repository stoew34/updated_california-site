package com.tmobile.mytmobile.echolocate.playground.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.tmobile.mytmobile.echolocate.BuildConfig
import com.tmobile.mytmobile.echolocate.R
import com.tmobile.mytmobile.echolocate.utils.EchoLocateLog
import com.tmobile.mytmobile.echolocate.variant.Constants

class FlavorConfigActivity : AppCompatActivity() {

    private lateinit var etReportView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_flavor_config)
        initViews()
        etReportView.movementMethod = ScrollingMovementMethod()
    }

    /**
     * Initialization section of views used in the activity
     */
    @SuppressLint("ClickableViewAccessibility")
    private fun initViews() {
        etReportView = findViewById(R.id.get_flavor_info_report)
        /**
         * enable edit text scroll to view list of events
         */
        etReportView.setOnTouchListener { v, event ->
            if (v.id == R.id.get_flavor_info_report) {
                v.parent.requestDisallowInterceptTouchEvent(true)
                when (event.action and MotionEvent.ACTION_MASK) {
                    MotionEvent.ACTION_UP -> v.parent.requestDisallowInterceptTouchEvent(
                        false
                    )
                }
            }
            false
        }
    }

    /**
     * generate constants for the current flavors
     */
    fun getFlavorUrls(@Suppress("UNUSED_PARAMETER") view: View?) {

        EchoLocateLog.eLogD(">>>> Authentication Env  = " + Constants.ENVIRONMENT)
        EchoLocateLog.eLogD(">>>> Configuration Url = " + Constants.CONFIGURATION_URL)
        EchoLocateLog.eLogD(">>>> Request Url = " + Constants.DIA_REQUEST_URL)
        EchoLocateLog.eLogD(">>>> BuildConfig.FLAVOR  is dolphin  = " + (BuildConfig.FLAVOR == "dolphin"))

            val authenticationEnv = "Authentication Env : "+ Constants.ENVIRONMENT + "\n\n"
            val configurationUrl = "Configuration Url : "+ Constants.CONFIGURATION_URL + "\n\n"
            val requestUrl = "Request Url :  "+ Constants.DIA_REQUEST_URL + "\n\n"

            etReportView.setText(authenticationEnv + configurationUrl + requestUrl )
        }

    /**
     * Listener that gets called when get flavor urls button is clicked
     * to fetch info
     * @param view:View view clicked
     */
    fun clearFlavorInfo(@Suppress("UNUSED_PARAMETER") view: View?) {
        etReportView.setText("")
    }

}
