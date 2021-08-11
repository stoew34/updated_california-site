package com.tmobile.mytmobile.echolocate.playground.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.telephony.TelephonyManager
import android.text.method.ScrollingMovementMethod
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.tmobile.mytmobile.echolocate.R
import com.tmobile.mytmobile.echolocate.utils.ELDeviceUtils
import com.tmobile.mytmobile.echolocate.playground.utils.PlaygroundUtils
import com.tmobile.pr.androidcommon.system.SystemService

class DeviceInfoActivity : AppCompatActivity() {

    private lateinit var etReportView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_device_info)
        initViews()
        etReportView.movementMethod = ScrollingMovementMethod()
    }

    /**
     * Initialization section of views used in the activity
     */
    @SuppressLint("ClickableViewAccessibility")
    private fun initViews() {
        etReportView = findViewById(R.id.et_device_info_report)
        /**
         * enable edit text scroll to view list of events
         */
        etReportView.setOnTouchListener { v, event ->
            if (v.id == R.id.et_device_info_report) {
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
     * Process and save raw data to database with status PROCESSED
     * generate new report list for all new record(RAW status)
     */
    fun getDeviceInfo(@Suppress("UNUSED_PARAMETER") view: View?) {
        if (ELDeviceUtils.isOreoDeviceOrHigher()
            && PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.READ_PHONE_STATE)) {
            // need to handle permissions in the calling handler, otherwise will return null, as the permissions are not given
            etReportView.setText("Missing phone permission. No device information.")
        } else {

            val telephonyManager: TelephonyManager =
                SystemService.getTelephonyManager(applicationContext) as TelephonyManager
            val imei =
                try {
                    "IMEI = ${PlaygroundUtils.getImei(applicationContext)}\n"
                } catch (ex: java.lang.Exception) {
                    "IMEI : Error getting info : ${ex.message}\n"
                }

            val imsi = "IMSI = ${getImsi(telephonyManager)}\n"
            val msisdn = "MSISDN = ${getMsisdn(telephonyManager)}"
            val otherInfo = "\n\nOEM Name = ${Build.MANUFACTURER}\n" +
                    "Model Code = ${Build.MODEL}\n" +
                    "Model Name = ${Build.DEVICE}\n" +
                    "PRODUCT Name = ${Build.PRODUCT}\n"
                    "Binary = ${Build.TYPE}\n" +
                    "Android OS = ${Build.VERSION.RELEASE}\n" +
                    "SDK level = ${Build.VERSION.SDK_INT}\n"

            etReportView.setText(imei + imsi + msisdn + otherInfo)
        }
    }

    /**
     * Listener that gets called when generate Report button is clicked
     * to fetch reports generated
     * @param view:View view clicked
     */
    fun clearDeviceInfo(@Suppress("UNUSED_PARAMETER") view: View?) {
        etReportView.setText("")
    }

    /**
     * fun getImsi
     *  IMSI getter function
     *
     * @param TelephonyManager
     * @return String
     */
    @SuppressLint("MissingPermission")
    private fun getImsi(telephonyManager: TelephonyManager?): String {

        return try {
            if (telephonyManager != null) {
                telephonyManager.subscriberId ?: ""
            } else {
                "TelephonyManager is null"
            }
        } catch (ex : Exception) {
            "Error getting info : ${ex.message}"
        }
    }

    /**
     *  fun getMsisdn
     *
     *    gets device phone number/msisdn
     *
     *   @param TelephonyManager
     *
     *   @return String
     */
    @SuppressLint("MissingPermission", "HardwareIds")
    private fun getMsisdn(telephonyManager: TelephonyManager?): String {

        return try {
            if (telephonyManager != null) {
                telephonyManager.line1Number ?: ""
            } else {
                "TelephonyManager is null"
            }
        } catch (ex : Exception) {
            "Error getting info : ${ex.message}"
        }
    }
}
