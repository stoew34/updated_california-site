package com.tmobile.mytmobile.echolocate.playground.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.tmobile.mytmobile.echolocate.utils.EchoLocateDateUtils
import com.tmobile.mytmobile.echolocate.utils.EchoLocateLog
import com.tmobile.mytmobile.echolocate.voice.VoiceModuleProvider
import com.tmobile.mytmobile.echolocate.voice.reportprocessor.VoiceReportProcessor
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * View model class to handle report operations
 */
class VoiceViewModel(application: Application) : AndroidViewModel(application) {

    private var liveDataParams = MutableLiveData<Any>()
    /**
     * Calls the get report api exposed by the [VoiceModuleProvider] with the selected date range
     * @param fromDateStr:String selected startTime
     * @param toDateStr:String selected endTime
     * @return [MutableLiveData] return the live data
     */
    @SuppressLint("CheckResult")
    fun getReports(fromDateStr: String, toDateStr: String): MutableLiveData<Any> {
        val voiceModuleProvider = VoiceModuleProvider.getInstance(getApplication())

        val startTime: Long
        val endTime: Long

        if (fromDateStr == "" && toDateStr == "") {
            startTime = EchoLocateDateUtils.getDateBeforeDays(7)
            endTime = EchoLocateDateUtils.getCurrentTimeInMillis()

        } else {
            startTime = EchoLocateDateUtils.convertStringToLong(fromDateStr)
            endTime = EchoLocateDateUtils.convertStringToLong(toDateStr)
        }
        voiceModuleProvider.getVoiceReport(startTime, endTime)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    liveDataParams.postValue(it[0].DIAReportResponseParameters.payload)
                }, {
                    EchoLocateLog.eLogE("error in get reports: " + it.printStackTrace())
                })

        return liveDataParams
    }

    @SuppressLint("CheckResult")
    fun processRAWData() {
        val voiceReportProcessor = VoiceReportProcessor.getInstance(getApplication())
        voiceReportProcessor.processRawData()
    }
}