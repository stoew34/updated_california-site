package com.tmobile.mytmobile.echolocate.playground.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.tmobile.mytmobile.echolocate.analytics.AnalyticsModuleProvider
import com.tmobile.mytmobile.echolocate.configuration.events.analytics.ELAnalyticActions
import com.tmobile.mytmobile.echolocate.configuration.events.analytics.ELAnalyticsEvent
import com.tmobile.mytmobile.echolocate.configuration.events.analytics.ELModulesEnum
import com.tmobile.mytmobile.echolocate.configuration.events.reportingevents.DIAReportResponseEvent
import io.reactivex.Observable

/**
 * View model class to handle report operations
 */
class AnalyticsViewModel(application: Application) : AndroidViewModel(application) {

    /**
     * Calls the get report api exposed by the [AnalyticsModuleProvider] with the selected date range
     * @return [MutableLiveData] return the live data
     */
    @SuppressLint("CheckResult")
    fun getReports(): Observable<DIAReportResponseEvent>? {
        val analyticsModuleProvider = AnalyticsModuleProvider.getInstance(getApplication())
        if (analyticsModuleProvider != null && analyticsModuleProvider.isAnalyticsModuleReady()) {
            return analyticsModuleProvider.getAnalyticsReport()
        }
        return null
    }

    @SuppressLint("CheckResult")
    fun generateAnalyticsEvent() {

        val elAnalyticsEvent = ELAnalyticsEvent(
            ELModulesEnum.VOICE,
            ELAnalyticActions.EL_DATA_COLLECTION_START,
            "test event from Echo App playground"
        )

        val analyticsModuleProvider = AnalyticsModuleProvider.getInstance(getApplication())
        analyticsModuleProvider.insertMockAnalyticsData(elAnalyticsEvent)

    }
}