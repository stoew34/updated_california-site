package com.tmobile.mytmobile.echolocate.analytics

import android.content.Context
import com.tmobile.mytmobile.echolocate.configuration.events.analytics.ELAnalyticsEvent
import com.tmobile.mytmobile.echolocate.configuration.events.reportingevents.DIAReportResponseEvent
import io.reactivex.Observable

/**
 * The interface class for Analytics data collection [AnalyticsModuleProvider]. The functions defined here are the ones which will be responsible to external world.
 *
 * All the other functions and classes are hidden from the external world.
 */
interface IAnalytics{

    fun initAnalyticsModule(context: Context)

    fun getAnalyticsReport(): Observable<DIAReportResponseEvent>

    fun stopAnalyticsModule()

    fun insertMockAnalyticsData(elAnalyticsEvent: ELAnalyticsEvent)

    /**
     * Public API to check if analytics module is ready to collect the data.
     * Returns true if ready
     */
    fun isAnalyticsModuleReady(): Boolean

    /**
     * This function deletes the processed reports from db
     */
    fun deleteReportFromDatabase()
}