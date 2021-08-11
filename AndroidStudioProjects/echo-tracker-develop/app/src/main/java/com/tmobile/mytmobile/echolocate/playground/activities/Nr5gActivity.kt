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
import com.tmobile.mytmobile.echolocate.lte.utils.ApplicationTrigger
import com.tmobile.mytmobile.echolocate.nr5g.core.utils.Nr5gApplicationTrigger
import com.tmobile.mytmobile.echolocate.nr5g.core.utils.Nr5gScreenTrigger
import com.tmobile.mytmobile.echolocate.nr5g.core.utils.Nr5gSharedPreference
import com.tmobile.mytmobile.echolocate.playground.viewmodel.Nr5gViewModel
import com.tmobile.mytmobile.echolocate.utils.EchoLocateDateUtils
import com.tmobile.mytmobile.echolocate.utils.EchoLocateLog
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_lte.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject

/**
 * This Activity uses the lteModule API's defined to display the lte reports on the playground
 */
class Nr5gActivity : AppCompatActivity() {

    private lateinit var etToDate: EditText
    private lateinit var etFromDate: EditText
    private lateinit var etReportView: EditText
    private lateinit var applicationTrigger: ApplicationTrigger

    private lateinit var btnHourlyTrigger: AppCompatButton
    private lateinit var edt_screen_trigger_count: EditText
    private lateinit var edt_app_trigger_count: EditText

    private lateinit var nr5gApplicationTrigger: Nr5gApplicationTrigger
    private lateinit var nr5gScreenTrigger: Nr5gScreenTrigger

    private val viewModel by lazy {
        ViewModelProviders.of(this).get(Nr5gViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nr5g)
        initViews()
        Nr5gSharedPreference.init(this)
        applicationTrigger = ApplicationTrigger.getInstance(this)
        etReportView.movementMethod = ScrollingMovementMethod()
        nr5gApplicationTrigger = Nr5gApplicationTrigger.getInstance(this)
        nr5gScreenTrigger = Nr5gScreenTrigger.getInstance(this)
    }

    /**
     * Initialization section of views used in the activity
     */
    private fun initViews() {
        etFromDate = findViewById(R.id.et_fromdate_nr5g)
        etToDate = findViewById(R.id.et_fromdate_nr5g)
        etReportView = findViewById(R.id.et_reports_nr5g)
        btnHourlyTrigger = findViewById(R.id.btn_hourly_trigger)

        edt_screen_trigger_count = findViewById(R.id.edt_screen_trigger_count)
        edt_app_trigger_count = findViewById(R.id.edt_app_trigger_count)

    }

    fun processRAWData(@Suppress("UNUSED_PARAMETER") view: View?) {
        GlobalScope.launch(Dispatchers.IO) {
            viewModel.processRAWData()
        }
    }

    /**
     * Function to check hourly trigger externally and stops hourly trigger
     */
    fun hourlyTrigger(@Suppress("UNUSED_PARAMETER") view: View?) {
        if (btnHourlyTrigger.text.toString()
                .equals(resources.getString(R.string.btn_hourly_trigger))
        ) {
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
    fun getAllReports(@Suppress("UNUSED_PARAMETER") view: View?) {

        viewModel.getNsa5gReports("", "").observe(this, Observer {
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
     * Listener that gets called when getAllReports button is clicked
     * to fetch the lte reports generated between current time and the last 7 days
     * @param view:View view clicked
     */
    fun getAllSa5gReports(@Suppress("UNUSED_PARAMETER") view: View?) {

        viewModel.getSa5gReports("", "").observe(this, Observer {
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
    fun getReportInDateRange(@Suppress("UNUSED_PARAMETER") view: View?) {
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
                viewModel.getNsa5gReports(startTime, endTime).observe(this, Observer {
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

    fun saveTriggerCount(@Suppress("UNUSED_PARAMETER") view: View?) {
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

    fun saveScreenTriggerCount(@Suppress("UNUSED_PARAMETER") view: View?) {
        if (edt_screen_trigger_count.text!!.isNotEmpty()) {
            val count = edt_screen_trigger_count.text.toString().toInt()
            if (count > nr5gScreenTrigger.getScreenTriggerLimit()) {
                Toast.makeText(
                    this,
                    "Count: $count should not exceed limit: ${nr5gScreenTrigger.getScreenTriggerLimit()}",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                nr5gScreenTrigger.saveScreenTriggerCount(count)
                Toast.makeText(this, "saved: $count", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun saveAppTriggerCount(@Suppress("UNUSED_PARAMETER") view: View?) {
        if (edt_app_trigger_count.text!!.isNotEmpty()) {
            val count = edt_app_trigger_count.text.toString().toInt()
            if (count > nr5gApplicationTrigger.getAppTriggerLimit()) {
                Toast.makeText(
                    this,
                    "Count: $count should not exceed limit: ${nr5gApplicationTrigger.getAppTriggerLimit()}",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                nr5gApplicationTrigger.saveAppTriggerCount(count)
                Toast.makeText(this, "saved: $count", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Listener that gets called when Sa5g process data button is clicked
     * @param view:View view clicked
     */
    fun getProcessData(@Suppress("UNUSED_PARAMETER") view: View?) {

        viewModel.getSa5gProcessData()
    }

    fun applyChanges(view: View?) {
        viewModel.applyChanges()
    }

    /**
     * Listener that gets called when generate Report button is clicked
     * to fetch reports generated
     * @param view:View view clicked
     */
    fun generate5gReport(@Suppress("UNUSED_PARAMETER") view: View?) {

        var nr5gReportDisposable: Disposable? = null
        var nr5gReportObservable = viewModel.getReports()
        if (nr5gReportObservable != null) {
            nr5gReportDisposable = nr5gReportObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    if (it != null && it[0].DIAReportResponseParameters.payload.isNotEmpty()) {
                        etReportView.visibility = View.VISIBLE
                        val jsonObject = JSONObject(it[0].DIAReportResponseParameters.payload)
                        etReportView.setText(jsonObject.toString(2))
                    } else {
                        etReportView.setText("")
                        Toast.makeText(applicationContext, "Report EMPTY", Toast.LENGTH_SHORT)
                            .show()
                    }
                    nr5gReportDisposable?.dispose()
                    nr5gReportDisposable = null
                    nr5gReportObservable = null
                }, {
                    EchoLocateLog.eLogE("Diagnostic error in get reports: " + it.localizedMessage)
                })
        } else {
            Toast.makeText(
                applicationContext,
                "Coverage module is not initialized",
                Toast.LENGTH_LONG
            )
                .show()
        }
    }
}