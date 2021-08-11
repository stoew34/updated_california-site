package com.tmobile.mytmobile.echolocate.playground.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.tmobile.mytmobile.echolocate.lte.LteModuleProvider
import com.tmobile.mytmobile.echolocate.lte.delegates.LteHourlyDelegate
import com.tmobile.mytmobile.echolocate.lte.reportprocessor.LteReportProcessor
import com.tmobile.mytmobile.echolocate.utils.EchoLocateDateUtils
import com.tmobile.mytmobile.echolocate.utils.EchoLocateLog
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * View model class to handle report operations
 */
class LteViewModel(application: Application) : AndroidViewModel(application) {

    private var liveDataParams = MutableLiveData<Any>()
    /**
     * Calls the get report api exposed by the [LteModuleProvider] with the selected date range
     * @param fromDateStr:String selected startTime
     * @param toDateStr:String selected endTime
     * @return [MutableLiveData] return the live data
     */
    @SuppressLint("CheckResult")
    fun getReports(fromDateStr: String, toDateStr: String): MutableLiveData<Any> {
        val lteModuleProvider = LteModuleProvider.getInstance(getApplication())

        val startTime: Long
        val endTime: Long

        if (fromDateStr == "" && toDateStr == "") {
            startTime = EchoLocateDateUtils.getDateBeforeDays(7)
            endTime = EchoLocateDateUtils.getCurrentTimeInMillis()

        } else {
            startTime = EchoLocateDateUtils.convertStringToLong(fromDateStr)
            endTime = EchoLocateDateUtils.convertStringToLong(toDateStr)
        }
        lteModuleProvider.getLteReport(startTime, endTime)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                liveDataParams.postValue(it.get(0).DIAReportResponseParameters.payload)
            }, {
                EchoLocateLog.eLogE("Diagnostic : error in get reports: " + it.printStackTrace())
            })

        return liveDataParams
    }

    @SuppressLint("CheckResult")
    fun processRAWData() {
        val lteReportProcessor = LteReportProcessor.getInstance(getApplication())
        lteReportProcessor.processRawData()
    }

    /**
     * This method will register hourly trigger externally
     */
    @SuppressLint("CheckResult")
    fun initHourlyTrigger() {
        GlobalScope.launch(Dispatchers.Main) {
            Toast.makeText(getApplication(), "Hourly triggered successfully", Toast.LENGTH_LONG).show()
        }
        LteHourlyDelegate.getInstance(getApplication()).startScheduler()
    }

    /**
     * This method will stop hourly trigger externally
     */
    @SuppressLint("CheckResult")
    fun stopHourlyTrigger() {
        GlobalScope.launch(Dispatchers.Main) {
            Toast.makeText(getApplication(), "Hourly triggered stopped successfully", Toast.LENGTH_LONG).show()
        }
        LteHourlyDelegate.getInstance(getApplication()).stopPeriodicActions()
    }
}