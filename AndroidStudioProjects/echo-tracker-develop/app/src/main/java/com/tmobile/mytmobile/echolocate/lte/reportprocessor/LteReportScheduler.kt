package com.tmobile.mytmobile.echolocate.lte.reportprocessor

import android.annotation.SuppressLint
import android.content.Context
import com.tmobile.mytmobile.echolocate.scheduler.events.SchedulerResponseEvent
import com.tmobile.mytmobile.echolocate.lte.utils.LteIntents.REPORT_GENERATOR_COMPONENT_NAME
import com.tmobile.mytmobile.echolocate.lte.utils.LteSharedPreference
import com.tmobile.mytmobile.echolocate.schedulermanager.SchedulerComponent
import com.tmobile.mytmobile.echolocate.scheduler.WorkParameters
import com.tmobile.mytmobile.echolocate.scheduler.WorkScheduledStatus
import com.tmobile.mytmobile.echolocate.utils.EchoLocateLog
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers


/**
 * LteReportScheduler is class for exposing scheduler job,
 * subscribe To Scheduler Observable and deleting sampling job
 */
class LteReportScheduler(private val context: Context) {

    private val lteReportProcessor = LteReportProcessor.getInstance(context)
    private var schedulerDisposable: Disposable? = null
    private val WORK_ID = javaClass.canonicalName.hashCode().toLong()

    /**
     * initialization part for variables used
     */
    init {
        LteSharedPreference.init(context)
    }

    /**
     * Schedule a periodic job using Scheduler component.
     * The time interval for the job is available in the configuration under lte - 6 hours.
     * To prevent a losing dataworkId saved to SharedPreference
     */

    //If the sampling interval has changed from the original one, then cancel the existing job and create a new one
    //interval has changed -> schedulerComponent.deleteWorkByTag ->

    @SuppressLint("CheckResult")
    fun schedulerJob(interval: Long, isEnabled: Boolean) {

        val schedulerComponent = SchedulerComponent.getInstance(context) as SchedulerComponent

        val workParameter = schedulerComponent.getWorkParamsByWorkID(LteSharedPreference.scheduledWorkId, context)
        EchoLocateLog.eLogD("Diagnostic : LTE: scheduledWorkId: ${LteSharedPreference.scheduledWorkId}")

        if (workParameter == null) {
            scheduleNewSamplingJob(interval, schedulerComponent)
            EchoLocateLog.eLogD("Diagnostic : LTE: workParameter is null, starting new New Sampling Job")
            return
        }
        if (!isEnabled) {
            stopScheduler()
            return
        }
        val currentInterval = workParameter.periodicInterval
        if (currentInterval != interval) {
            stopScheduler()
            scheduleNewSamplingJob(interval, schedulerComponent)
            EchoLocateLog.eLogD("Diagnostic : LTE: periodicInterval changed, starting new New Sampling Job")

        } else {
            if (schedulerDisposable == null || schedulerDisposable?.isDisposed!!) {
                EchoLocateLog.eLogD("Diagnostic : LTE: scheduler disposed or null, new subscribe")
                subscribeToSchedulerObservable(schedulerComponent.observeSchedulerEvent())
            }
        }
    }

    /**
     * Schedule a periodic job using Scheduler component.
     * The time interval for the job is available in the configuration under lte - 6 hours.
     */

    private fun scheduleNewSamplingJob(interval: Long, schedulerComponent: SchedulerComponent) {
        cleanUpDanglingJobs()
        val workParameters = WorkParameters.Builder()
            .workId(WORK_ID)
            .periodicInterval(interval)
            .isPeriodic(true)
            .minimumLatency(1) // minimum latency: the minimum delay to trigger the work
            .sourceComponentName(REPORT_GENERATOR_COMPONENT_NAME)
        EchoLocateLog.eLogD("Diagnostic : LTE: NewSamplingJob: scheduledWorkId: ${LteSharedPreference.scheduledWorkId}, periodicInterval: $interval")

        val result = schedulerComponent.scheduleJob(context, workParameters)
        subscribeToSchedulerObservable(result)
    }

    /**
     * Any job id which are apart from this class canonicalName hash code will be deleted
     */
    private fun cleanUpDanglingJobs() {
        val schedulerComponent = SchedulerComponent.getInstance(context) as SchedulerComponent
        val workIdList = schedulerComponent.getWorkId(context,
            REPORT_GENERATOR_COMPONENT_NAME
        )
        for(workId in workIdList){
            if(workId != WORK_ID){
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
    @SuppressLint("CheckResult")
    private fun subscribeToSchedulerObservable(lteSchedulerObservable: Observable<SchedulerResponseEvent>) {
        schedulerDisposable = lteSchedulerObservable.subscribeOn(Schedulers.io())
            .observeOn(Schedulers.newThread())
            .filter {
                it.sourceComponent.equals(REPORT_GENERATOR_COMPONENT_NAME)
            }
            .subscribe({ response ->
                EchoLocateLog.eLogD("Diagnostic : LTE: Job is received for : ${response.sourceComponent} with state: ${response.status} and ID: ${response.workParameters.workId}")
                if (response.status == WorkScheduledStatus.SCHEDULED.state) {
                    LteSharedPreference.scheduledWorkId = response.workParameters.workId
                } else if (response.status == WorkScheduledStatus.DISPATCHED.state) {
                    lteReportProcessor.processRawData(response.androidWorkId)
                }
            }, { error ->
                EchoLocateLog.eLogE("Diagnostic : LTE: error on schedule : ${error.message}")
            })
    }

    /**
     * delete a periodic job using Scheduler component.
     *  @param schedulerComponent SchedulerComponent received once configuration lte interval changes.
     */
    private fun deleteLteSamplingJob(schedulerComponent: SchedulerComponent) {
        if (LteSharedPreference.scheduledWorkId != 0L) {
            schedulerComponent.deleteWorkByTag(LteSharedPreference.scheduledWorkId.toString())
        }
    }

    /**
     * This function will delete the scheduler job as well as unregister to listen to the updates.
     */
    fun stopScheduler() {
        EchoLocateLog.eLogD("Diagnostic : LTE: Scheduler stopped")
        deleteLteSamplingJob(SchedulerComponent.getInstance(context) as SchedulerComponent)
        schedulerDisposable?.dispose()
    }
}