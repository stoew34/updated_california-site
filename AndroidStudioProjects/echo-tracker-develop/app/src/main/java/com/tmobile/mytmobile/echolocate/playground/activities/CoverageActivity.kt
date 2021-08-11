package com.tmobile.mytmobile.echolocate.playground.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.tmobile.mytmobile.echolocate.R
import com.tmobile.mytmobile.echolocate.playground.viewmodel.CoverageViewModel
import com.tmobile.mytmobile.echolocate.utils.EchoLocateLog
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject

class CoverageActivity : AppCompatActivity() {

    private lateinit var etReportView: EditText
    private val viewModel by lazy {
        ViewModelProviders.of(this).get(CoverageViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_coverage)
        initViews()
        etReportView.movementMethod = ScrollingMovementMethod()
    }

    /**
     * Initialization section of views used in the activity
     */
    @SuppressLint("ClickableViewAccessibility")
    private fun initViews() {
        etReportView = findViewById(R.id.et_report)
        /**
         * enable edit text scroll to view list of events
         */
        etReportView.setOnTouchListener { v, event ->
            if (v.id == R.id.et_report) {
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
    fun processCoverageRAWData(@Suppress("UNUSED_PARAMETER") view: View?) {
        GlobalScope.launch(Dispatchers.IO) {
            viewModel.processRAWData()
        }
    }

    /**
     * Listener that gets called when generate Report button is clicked
     * to fetch reports generated
     * @param view:View view clicked
     */
    fun generateCoverageReport(@Suppress("UNUSED_PARAMETER") view: View?) {

        var coverageReportDisposable: Disposable? = null
        var coverageReportObservable = viewModel.getReports()
        if (coverageReportObservable != null) {
            coverageReportDisposable = coverageReportObservable
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
                    coverageReportDisposable?.dispose()
                    coverageReportDisposable = null
                    coverageReportObservable = null
                }, {
                    EchoLocateLog.eLogE("Diagnostic error in get reports: " + it.printStackTrace())
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
