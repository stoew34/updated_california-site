package com.tmobile.mytmobile.echolocate.playground.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.tmobile.mytmobile.echolocate.configuration.events.reportingevents.DIAReportResponseEvent
import com.tmobile.mytmobile.echolocate.nr5g.Nr5gModuleProvider
import com.tmobile.mytmobile.echolocate.nr5g.core.delegates.Nr5gHourlyDelegate
import com.tmobile.mytmobile.echolocate.nr5g.manager.Nsa5gDataManager
import com.tmobile.mytmobile.echolocate.nr5g.manager.Sa5gDataManager
import com.tmobile.mytmobile.echolocate.nr5g.nsa5g.reportprocessor.Nsa5gReportProcessor
import com.tmobile.mytmobile.echolocate.nr5g.sa5g.reportprocessor.Sa5gReportProcessor
import com.tmobile.mytmobile.echolocate.utils.EchoLocateDateUtils
import io.reactivex.Observable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * View model class to handle report operations
 */
class Nr5gViewModel(application: Application) : AndroidViewModel(application) {

    private var liveDataParams = MutableLiveData<Any>()

    /**
     * Calls the get report api exposed by the [Nr5gModuleProvider] with the selected date range
     * @param fromDateStr:String selected startTime
     * @param toDateStr:String selected endTime
     * @return [MutableLiveData] return the live data
     */
    @SuppressLint("CheckResult")
    fun getNsa5gReports(fromDateStr: String, toDateStr: String): MutableLiveData<Any> {

        val nr5gModuleProvider = Nr5gModuleProvider.getInstance(getApplication())
        val nsa5gDataManager = Nsa5gDataManager(getApplication())

        var nsa5gReportEntityList = ArrayList<DIAReportResponseEvent>()
        val startTime: Long
        val endTime: Long

        if (fromDateStr == "" && toDateStr == "") {
            startTime = EchoLocateDateUtils.getDateBeforeDays(7)
            endTime = EchoLocateDateUtils.getCurrentTimeInMillis()

        } else {
            startTime = EchoLocateDateUtils.convertStringToLong(fromDateStr)
            endTime = EchoLocateDateUtils.convertStringToLong(toDateStr)
        }
        GlobalScope.launch(Dispatchers.IO) {
            nsa5gReportEntityList = nsa5gDataManager.get5gReportEntityList(startTime, endTime)
        }
        if (!nsa5gReportEntityList.isNullOrEmpty()) {
            liveDataParams.postValue(nsa5gReportEntityList.get(0).DIAReportResponseParameters.payload)
        }
        return liveDataParams
    }

    /**
     * Calls the get report api exposed by the [Nr5gModuleProvider] with the selected date range
     * @param fromDateStr:String selected startTime
     * @param toDateStr:String selected endTime
     * @return [MutableLiveData] return the live data
     */
    @SuppressLint("CheckResult")
    fun getSa5gReports(fromDateStr: String, toDateStr: String): MutableLiveData<Any> {

        val nr5gModuleProvider = Nr5gModuleProvider.getInstance(getApplication())
        val sa5gDataManager = Sa5gDataManager(getApplication())

        var nsa5gReportEntityList = ArrayList<DIAReportResponseEvent>()
        val startTime: Long
        val endTime: Long

        if (fromDateStr == "" && toDateStr == "") {
            startTime = EchoLocateDateUtils.getDateBeforeDays(7)
            endTime = EchoLocateDateUtils.getCurrentTimeInMillis()

        } else {
            startTime = EchoLocateDateUtils.convertStringToLong(fromDateStr)
            endTime = EchoLocateDateUtils.convertStringToLong(toDateStr)
        }
        GlobalScope.launch(Dispatchers.IO) {
            nsa5gReportEntityList = sa5gDataManager.get5gReportEntityList(startTime, endTime)
        }
        if (!nsa5gReportEntityList.isNullOrEmpty()) {
            liveDataParams.postValue(nsa5gReportEntityList.get(0).DIAReportResponseParameters.payload)
        }
        return liveDataParams
    }

    /**
     * Calls the get report api exposed by the Module Provider with the selected date range
     * @return [Observable<List<DIAReportResponseEvent>>]
     */
    @SuppressLint("CheckResult")
    fun getReports(): Observable<List<DIAReportResponseEvent>>? {

        val nr5gModuleProvider = Nr5gModuleProvider.getInstance(getApplication())
        if (nr5gModuleProvider != null) {
            return nr5gModuleProvider.get5gReport(0, 0)
        }
        return null
    }

    @SuppressLint("CheckResult")
    fun processRAWData() {
        val nr5gReportProcessor = Nsa5gReportProcessor.getInstance(getApplication())
        nr5gReportProcessor.processRawData()
    }

    /**
     * This method will register hourly trigger externally
     */
    @SuppressLint("CheckResult")
    fun initHourlyTrigger() {
        GlobalScope.launch(Dispatchers.Main) {
            Toast.makeText(getApplication(), "Hourly triggered successfully", Toast.LENGTH_LONG)
                .show()
        }
        Nr5gHourlyDelegate.getInstance(getApplication()).startScheduler()
    }

    /**
     * This method will stop hourly trigger externally
     */
    @SuppressLint("CheckResult")
    fun stopHourlyTrigger() {
        GlobalScope.launch(Dispatchers.Main) {
            Toast.makeText(
                getApplication(),
                "Hourly triggered stopped successfully",
                Toast.LENGTH_LONG
            ).show()
        }
        Nr5gHourlyDelegate.getInstance(getApplication()).stopPeriodicActions()
    }

    fun applyChanges() {
        val nr5gModuleProvider = Nr5gModuleProvider.getInstance(getApplication())
        nr5gModuleProvider.initNr5gModule(getApplication())
    }

    /**
     * getSa5gDataCollection
     */
    fun getSa5gProcessData() {
        val sa5gReportProcessor = Sa5gReportProcessor.getInstance(getApplication())

        GlobalScope.launch(Dispatchers.IO) {
            sa5gReportProcessor.processRawData()
        }
    }
}