package com.tmobile.mytmobile.echolocate.playground.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.tmobile.mytmobile.echolocate.configuration.events.reportingevents.DIAReportResponseEvent
import com.tmobile.mytmobile.echolocate.coverage.CoverageModuleProvider
import com.tmobile.mytmobile.echolocate.coverage.reportprocessor.CoverageReportProcessor
import io.reactivex.Observable

/**
 * View model class to handle report operations
 */
class CoverageViewModel(application: Application) : AndroidViewModel(application) {

    @SuppressLint("CheckResult")
    fun processRAWData() {
        val coverageReportProcessor = CoverageReportProcessor.getInstance(getApplication())
        coverageReportProcessor.processRawData()
    }

    /**
     * Calls the get report api exposed by the Module Provider with the selected date range
     * @return [Observable<List<DIAReportResponseEvent>>]
     */
    @SuppressLint("CheckResult")
    fun getReports(): Observable<List<DIAReportResponseEvent>>? {

        val coverageModuleProvider = CoverageModuleProvider.getInstance(getApplication())
        if (coverageModuleProvider != null && coverageModuleProvider.isCoverageModuleReady()) {
            return coverageModuleProvider.getCoverageReport(0, 0)
        }
        return null
    }


}