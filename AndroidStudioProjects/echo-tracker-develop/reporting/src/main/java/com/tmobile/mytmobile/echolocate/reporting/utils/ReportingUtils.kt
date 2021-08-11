package com.tmobile.mytmobile.echolocate.reporting.utils

import android.content.Context
import com.tmobile.mytmobile.echolocate.communicationbus.rxbus.PostTicket
import com.tmobile.mytmobile.echolocate.communicationbus.rxbus.RxBus
import com.tmobile.mytmobile.echolocate.reporting.reportsender.ReportSentEvent
import com.tmobile.mytmobile.echolocate.scheduler.events.ScheduledJobCompletedEvent

/**
 * Created by Divya Mittal on 04/09/2021
 *
 * ReportingUtils : Utility class EchoApp
 */

class ReportingUtils {
    companion object {
        /**
         * This function will broadcast the job completed status using [ScheduledJobCompletedEvent]
         * If workId is null, it means the job is not requested by scheduler
         */
        fun sendJobCompletedToScheduler(androidWorkId: String?, moduleName: String?, context: Context) {
            if (!androidWorkId.isNullOrEmpty()) {
                /** Scheduler job is completed */
                val postJobCompletedTicket = PostTicket(
                    ScheduledJobCompletedEvent(androidWorkId!!)
                )
                RxBus.instance.post(postJobCompletedTicket)
                ReportingLog.eLogD("Diagnostic : ScheduleWorker job completed for $moduleName : $androidWorkId", context)
            }
        }

        fun postReportSentEvent(reportSentEvent: ReportSentEvent) {
            val postTicket = PostTicket(reportSentEvent)
            RxBus.instance.post(postTicket)
        }
    }

}
