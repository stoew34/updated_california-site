package com.tmobile.mytmobile.echolocate.playground.activities

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.tmobile.mytmobile.echolocate.R
import com.tmobile.mytmobile.echolocate.lte.manager.LteDataManager
import com.tmobile.mytmobile.echolocate.playground.viewmodel.LteViewModel
import com.tmobile.mytmobile.echolocate.utils.EchoLocateDateUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import com.tmobile.mytmobile.echolocate.lte.utils.ApplicationTrigger
import com.tmobile.mytmobile.echolocate.utils.EchoLocateLog
import kotlinx.android.synthetic.main.activity_lte.*


/**
 * This Activity uses the lteModule API's defined to display the lte reports on the playground
 */
class LteActivity : AppCompatActivity() {

    private lateinit var etToDate: EditText
    private lateinit var etFromDate: EditText
    private lateinit var etReportView: EditText
    private lateinit var applicationTrigger: ApplicationTrigger

    private lateinit var btnHourlyTrigger: AppCompatButton


    private val viewModel by lazy {
        ViewModelProviders.of(this).get(LteViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lte)
        initViews()
        applicationTrigger = ApplicationTrigger.getInstance(this)
        etReportView.movementMethod = ScrollingMovementMethod()
    }

    /**
     * Initialization section of views used in the activity
     */
    private fun initViews() {
        etFromDate = findViewById(R.id.et_fromdate_lte)
        etToDate = findViewById(R.id.et_fromdate_lte)
        etReportView = findViewById(R.id.et_reports_lte)
        btnHourlyTrigger = findViewById(R.id.btn_hourly_trigger)

    }

    fun processRAWData(@Suppress("UNUSED_PARAMETER")view: View?) {
        GlobalScope.launch(Dispatchers.IO) {
            viewModel.processRAWData()
        }
    }

    /**
     * Function to check hourly trigger externally and stops hourly trigger
     */
    fun hourlyTrigger(@Suppress("UNUSED_PARAMETER")view: View?) {
        if (btnHourlyTrigger.text.toString().equals(resources.getString(R.string.btn_hourly_trigger))) {
            viewModel.initHourlyTrigger()
            btnHourlyTrigger.text = resources.getString(R.string.btn_stop_hourly_trigger)
            btnHourlyTrigger.setBackgroundColor(
                ContextCompat.getColor(
                    this,
                    android.R.color.darker_gray
                )
            )
        } else {
            viewModel.stopHourlyTrigger()
            btnHourlyTrigger.text = resources.getString(R.string.btn_hourly_trigger)
            btnHourlyTrigger.setBackgroundColor(ContextCompat.getColor(this, R.color.magenta))
        }
    }

    /**
     * Listener that gets called when getAllReports button is clicked
     * to fetch the lte reports generated between current time and the last 7 days
     * @param view:View view clicked
     */
    fun getAllReports(@Suppress("UNUSED_PARAMETER")view: View?) {

        viewModel.getReports("", "").observe(this, Observer {
            if (it != null && (it as String).isNotEmpty()) {
                etReportView.visibility = View.VISIBLE
                //  etReportView.setText(it as String)
                val jsonObject = JSONObject(it)
                etReportView.setText(jsonObject.toString(2))
            } else {
                Toast.makeText(applicationContext, "Report EMPTY", Toast.LENGTH_SHORT).show()
            }
        })
    }

    /**
     * Listener that gets called when getReports button is clicked
     * to fetch the lte reports generated for the given date range
     * @param view:View view clicked
     */
    fun getReportInDateRange(@Suppress("UNUSED_PARAMETER")view: View?) {
        val startTime = etFromDate.text.toString()
        val endTime = etToDate.text.toString()

        if (etFromDate.text.toString().isEmpty() || etToDate.text.toString().isEmpty()) {
            etFromDate.error = resources.getString(R.string.field_non_empty_msg)
        } else {
            if (!EchoLocateDateUtils.isDateParsable(startTime) || !EchoLocateDateUtils.isDateParsable(
                    endTime
                )
            ) {
                etFromDate.error = resources.getString(R.string.date_parse_msg)
            } else {
                viewModel.getReports(startTime, endTime).observe(this, Observer {
                    if (it != null) {
                        etReportView.visibility = View.VISIBLE
                        // etReportView.setText(it as String)
                        val jsonObject = JSONObject(it as String)
                        etReportView.setText(jsonObject.toString(2))

                    }
                })
            }
        }
    }

    fun saveTriggerCount(@Suppress("UNUSED_PARAMETER")view: View?) {
        if (et_trigger_count.text!!.isNotEmpty()) {
            val count = et_trigger_count.text.toString().toInt()
            if (count > applicationTrigger.getTriggerLimit()) {
                Toast.makeText(
                    this,
                    "Count: $count should not exceed limit: ${applicationTrigger.getTriggerLimit()}",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                if (applicationTrigger.isTriggerLimitReached()) {
                    EchoLocateLog.eLogV("CMS Limit-limit reached so registering again")
                    val lteModuleManager = LteDataManager(this)
                    lteModuleManager.registerReceiver()
                }
                applicationTrigger.saveTriggerCount(count)
                Toast.makeText(this, "saved: $count", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
