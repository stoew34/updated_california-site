package com.tmobile.mytmobile.echolocate.reporting

import com.tmobile.myaccount.events.diagnostics.pojos.collector.event.ClientSideEvent
import com.tmobile.mytmobile.echolocate.configuration.events.reportingevents.DIAReportResponseEvent
import io.reactivex.Observable

/**
 * Created by Divya Mittal on 5/20/21
 */
interface IBaseReportDataRequestor {
    /**
     * get all the data collection with in the start time and end time
     */
    fun getReports(startTime: Long, endTime: Long): List<ClientSideEvent>

    /**
     *  deletes the reports
     */
    fun deleteReports()
}