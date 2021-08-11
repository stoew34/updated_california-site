package com.tmobile.mytmobile.echolocate.playground.activities

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.tmobile.mytmobile.echolocate.R
import com.tmobile.mytmobile.echolocate.analytics.utils.AnalyticsSharedPreference
import com.tmobile.mytmobile.echolocate.playground.viewmodel.AnalyticsViewModel
import com.tmobile.mytmobile.echolocate.utils.EchoLocateLog
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.json.JSONObject


/**
 * This Activity uses the VoiceModule API's defined to display the voice reports on the playground
 */
class AnalyticsActivity : AppCompatActivity() {

    private lateinit var etReportView: EditText
    private lateinit var tvStartCount: TextView
    private lateinit var tvCrashCount: TextView
    private lateinit var tvAnrCount: TextView
    private lateinit var tvRebootCount: TextView
    private lateinit var btnGetAppStats: Button
    private val viewModel by lazy {
        ViewModelProviders.of(this).get(AnalyticsViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_analytics)
        initViews()
        etReportView.movementMethod = ScrollingMovementMethod()
    }

    /**
     * Initialization section of views used in the activity
     */
    private fun initViews() {
        etReportView = findViewById(R.id.et_analytics_reports)
        /**
         * enable edit text scroll to view lsit of events
         */
        etReportView.setOnTouchListener { v, event ->
            if (v.id == R.id.et_analytics_reports) {
                v.parent.requestDisallowInterceptTouchEvent(true)
                when (event.action and MotionEvent.ACTION_MASK) {
                    MotionEvent.ACTION_UP -> v.parent.requestDisallowInterceptTouchEvent(
                        false
                    )
                }
            }
            false
        }
        tvStartCount = findViewById(R.id.tv_app_start_count)
        tvCrashCount = findViewById(R.id.tv_app_crash_count)
        tvAnrCount = findViewById(R.id.tv_app_anr_count)
        tvRebootCount = findViewById(R.id.tv_app_reboot_count)
        btnGetAppStats = findViewById(R.id.btn_Get_App_Stats)
        btnGetAppStats.setOnClickListener{
            getAppHealthStats()
        }
    }

    fun generateAnalyticsEvent(@Suppress("UNUSED_PARAMETER")view: View?) {
        Toast.makeText(applicationContext, "Not Applicable", Toast.LENGTH_SHORT).show()
    }

    /**
     * Listener that gets called when generateAnalyticsReport button is clicked
     * to fetch the Analytics reports generated
     * @param view:View view clicked
     */
    fun generateAnalyticsReport(@Suppress("UNUSED_PARAMETER")view: View?) {

        var analyticsReportDisposable: Disposable? = null
        var analyticsReportObservable = viewModel.getReports()
        if (analyticsReportObservable != null) {
            analyticsReportDisposable = analyticsReportObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    if (it != null && it.DIAReportResponseParameters.payload.isNotEmpty()) {
                        etReportView.visibility = View.VISIBLE
                        val jsonObject = JSONObject(it.DIAReportResponseParameters.payload)
                        etReportView.setText(jsonObject.toString(2))
                    } else {
                        etReportView.setText("")
                        Toast.makeText(applicationContext, "Report EMPTY", Toast.LENGTH_SHORT)
                            .show()
                    }
                    analyticsReportDisposable?.dispose()
                    analyticsReportDisposable = null
                    analyticsReportObservable = null
                }, {
                    EchoLocateLog.eLogE("Diagnostic error in get reports: " + it.printStackTrace())
                })
        } else {
            Toast.makeText(applicationContext, "Analytics module is not initialized", Toast.LENGTH_LONG).show()
        }
    }

    /**
     * Fetch app stats from HealthStats API
     */
    fun getAppHealthStats() {

        tvStartCount.text = "The app start count is: "+AnalyticsSharedPreference.numOfStarts
        tvCrashCount.text = "The app crash count is: "+AnalyticsSharedPreference.numOfCrashes
        tvAnrCount.text = "The app anr count is: "+AnalyticsSharedPreference.numOfAnrs
        tvRebootCount.text = "The app reboot count is: "+ AnalyticsSharedPreference.numOfReboots
    }
}
