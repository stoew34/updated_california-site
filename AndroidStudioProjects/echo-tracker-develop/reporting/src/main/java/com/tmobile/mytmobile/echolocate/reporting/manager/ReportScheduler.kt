package com.tmobile.mytmobile.echolocate.reporting.manager

import android.annotation.SuppressLint
import android.content.Context
import com.tmobile.mytmobile.echolocate.scheduler.events.SchedulerResponseEvent
import com.tmobile.mytmobile.echolocate.reporting.utils.ReportingModuleConstants.REPORT_GENERATOR_COMPONENT_NAME
import com.tmobile.mytmobile.echolocate.reporting.utils.ReportingModuleSharedPrefs
import com.tmobile.mytmobile.echolocate.reporting.SchedulerComponent
import com.tmobile.mytmobile.echolocate.scheduler.WorkParameters
import com.tmobile.mytmobile.echolocate.scheduler.WorkScheduledStatus
import com.tmobile.mytmobile.echolocate.reporting.utils.ReportingLog
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

/**
 * ReportScheduler is class for exposing scheduler job,
 * subscribe To Scheduler Observable and deleting sampling job
 */

class ReportScheduler(private val context: Context) {
    private var reportManager = ReportManager.getInstance(context)
    private var schedulerDisposable: Disposable? = null
    private val WORK_ID = javaClass.canonicalName.hashCode().toLong()

    /**
     * Schedule a periodic job using Scheduler component.
     * The time interval for the job is available in the configuration under reporting - 24 hours.
     * To prevent a losing dataworkId saved to SharedPreference
     */

    //If the sampling interval has changed from the original one, then cancel the existing job and create a new one
    //interval has changed -> schedulerComponent.deleteWorkByTag ->

    @SuppressLint("CheckResult")
    fun schedulerJob(interval: Long, isEnabled: Boolean) {

        val schedulerComponent = SchedulerComponent.getInstance(context) as SchedulerComponent

        val workParameter = schedulerComponent.getWorkParamsByWorkID(
            ReportingModuleSharedPrefs.scheduledWorkId,
            context
        )
        ReportingLog.eLogD("Diagnostic : Scheduler : REPORT: scheduledWorkId: ${ReportingModuleSharedPrefs.scheduledWorkId}", context)


        // There is no existing job for report sampling, schedule a new one.
        if (workParameter == null) {
            scheduleNewSamplingJob(interval, schedulerComponent)
            ReportingLog.eLogD("Diagnostic : Scheduler : REPORT: workParameter is null, starting new New Sampling Job", context)
            return
        }

        ReportingLog.eLogD("Diagnostic : Scheduler : REPORT existing job found in the db", context)
        if (!isEnabled) {
            stopScheduler()
            return
        }
        // If the interval has changed, then cancel the existing one and schedule a new job.
        val currentInterval = workParameter.periodicInterval
        if (currentInterval != interval) {
            stopScheduler()
            scheduleNewSamplingJob(interval, schedulerComponent)
            ReportingLog.eLogD("Diagnostic : Scheduler : REPORT: periodicInterval changed, starting new New Sampling Job", context)
        } else {
            // The interval has not changed, just subscribe to the existing job.
            if (schedulerDisposable == null || schedulerDisposable?.isDisposed!!) {
                ReportingLog.eLogD("Diagnostic : Scheduler : REPORT: scheduler disposed or null, new subscribe", context)
                subscribeToSchedulerObservable(schedulerComponent.observeSchedulerEvent())
            }
        }
    }

    /**
     * Schedule a periodic job using Scheduler component.
     * The time interval for the job is available in the configuration under reporting - 24 hours.
     */

    private fun scheduleNewSamplingJob(interval: Long, schedulerComponent: SchedulerComponent) {
        cleanUpDanglingJobs()
        val workParameters = WorkParameters.Builder()
            .workId(WORK_ID)
            .periodicInterval(interval)
            .isPeriodic(true)
            .minimumLatency(1) // minimum latency: the minimum delay to trigger the work
            .sourceComponentName(REPORT_GENERATOR_COMPONENT_NAME)
        ReportingLog.eLogD("Diagnostic : Scheduler : REPORT: NewSamplingJob: scheduledWorkId: " +
                "${ReportingModuleSharedPrefs.scheduledWorkId}, periodicInterval: $interval", context)
        val result = schedulerComponent.scheduleJob(context, workParameters)
        subscribeToSchedulerObservable(result)
    }

    /**
     * Any job id which are apart from this class canonicalName hash code will be deleted
     */
    private fun cleanUpDanglingJobs() {
        val schedulerComponent = SchedulerComponent.getInstance(context) as SchedulerComponent
        val workIdList = schedulerComponent.getWorkId(
            context,
            REPORT_GENERATOR_COMPONENT_NAME
        )
        for (workId in workIdList) {
            if (workId != WORK_ID) {
                schedulerComponent.deleteWorkByTag(workId.toString())
            }
        }
    }

    /**
     * Function responsible for subscribing to the scheduler observable for the reporting module and take appropriate action
     *
     * The function checks for two different states of the callback:
     * 1. [WorkScheduledStatus.SCHEDULED] Saves the details of the work job.
     * 2. [WorkScheduledStatus.DISPATCHED] performs the action when the work job is actually fired
     */
    private fun subscribeToSchedulerObservable(reportSchedulerObservable: Observable<SchedulerResponseEvent>) {
        schedulerDisposable = reportSchedulerObservable.subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .filter {
                it.sourceComponent.equals(REPORT_GENERATOR_COMPONENT_NAME)
            }
            .subscribe({ response ->
                ReportingLog.eLogD("Diagnostic : Scheduler : REPORT: Job is received for : " +
                        "${response.sourceComponent} with state: ${response.status} and ID: " +
                        "${response.workParameters.workId}", context)
                if (response.status == WorkScheduledStatus.SCHEDULED.state) {
                    ReportingModuleSharedPrefs.scheduledWorkId = response.workParameters.workId
                } else if (response.status == WorkScheduledStatus.DISPATCHED.state) {
                    reportManager.requestReportsFromModules(response.androidWorkId)
                }
            }, { error ->
                ReportingLog.eLogE("Diagnostic : Scheduler : REPORT: error on schedule : ${error.message}")
            })
    }

    /**
     * delete a periodic job using Scheduler component.
     *  @param schedulerComponent SchedulerComponent received once configuration reporting interval changes.
     */
    private fun deleteReportSamplingJob(schedulerComponent: SchedulerComponent) {
        if (ReportingModuleSharedPrefs.scheduledWorkId != 0L) {
            schedulerComponent.deleteWorkByTag(ReportingModuleSharedPrefs.scheduledWorkId.toString())
        }
    }

    /**
     * This function will delete the scheduler job as well as unregister to listen to the updates.
     */
    fun stopScheduler() {
        deleteReportSamplingJob(SchedulerComponent.getInstance(context) as SchedulerComponent)
        schedulerDisposable?.dispose()
        ReportingLog.eLogI("Diagnostic : Scheduler : REPORT Scheduler STOPPED and periodic job deleted")
    }
}