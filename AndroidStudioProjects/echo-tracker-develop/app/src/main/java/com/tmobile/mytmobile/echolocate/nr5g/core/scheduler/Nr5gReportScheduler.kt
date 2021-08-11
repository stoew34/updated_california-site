package com.tmobile.mytmobile.echolocate.nr5g.core.scheduler

import android.annotation.SuppressLint
import android.content.Context
import com.tmobile.mytmobile.echolocate.scheduler.events.SchedulerResponseEvent
import com.tmobile.mytmobile.echolocate.nr5g.core.utils.Nr5gConstants.REPORT_PROCESSING_COMPONENT_NAME
import com.tmobile.mytmobile.echolocate.nr5g.core.utils.Nr5gSharedPreference
import com.tmobile.mytmobile.echolocate.nr5g.manager.INr5gIntentHandler
import com.tmobile.mytmobile.echolocate.schedulermanager.SchedulerComponent
import com.tmobile.mytmobile.echolocate.scheduler.WorkParameters
import com.tmobile.mytmobile.echolocate.scheduler.WorkScheduledStatus
import com.tmobile.mytmobile.echolocate.utils.EchoLocateLog
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

/**
 * Nr5gReportScheduler is class for exposing scheduler job,
 * subscribe To Scheduler Observable and deleting sampling job
 */
class Nr5gReportScheduler(private val context: Context) {

    private var schedulerDisposable: Disposable? = null
    private var base5gDataManager: INr5gIntentHandler? = null
    private val WORK_ID = javaClass.canonicalName.hashCode().toLong()

    /** Initialization part for variables used */
    init {
        Nr5gSharedPreference.init(context)
    }

    /**
     * This method sets the listener to handle callback mechanism
     */
    fun setListener(nr5GIntentHandler: INr5gIntentHandler) {
        base5gDataManager = nr5GIntentHandler
    }

    /**
     * Schedule a periodic job using Scheduler component.
     * The time interval for the job is available in the configuration.
     * To prevent a losing dataworkId saved to SharedPreference
     *
     *  If the sampling interval has changed from the original one,
     *  then cancel the existing job and create a new one
     *  interval has changed -> schedulerComponent.deleteWorkByTag
     */
    @SuppressLint("CheckResult")
    fun schedulerJob(interval: Long, isEnabled: Boolean) {

        val schedulerComponent =
            SchedulerComponent.getInstance(context) as SchedulerComponent

        val workParameter =
            schedulerComponent.getWorkParamsByWorkID(
                Nr5gSharedPreference.scheduledWorkId, context
            )
        EchoLocateLog.eLogD("Diagnostic : Scheduler : Nr5g processing scheduledWorkId: ${Nr5gSharedPreference.scheduledWorkId}")

        if (workParameter == null) {
            scheduleNewSamplingJob(interval, schedulerComponent)
            EchoLocateLog.eLogD("Diagnostic : Scheduler : Nr5g processing workParameter is null, starting new New Sampling Job")
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
            EchoLocateLog.eLogD("Diagnostic : Scheduler : Nr5g processing Interval changed, starting new Job")

        } else {
            if (schedulerDisposable == null || schedulerDisposable?.isDisposed!!) {
                EchoLocateLog.eLogD("Diagnostic : Scheduler : Nr5g processing scheduler disposed or null, new subscribe")
                subscribeToSchedulerObservable(schedulerComponent.observeSchedulerEvent())
            }
        }
    }

    /**
     * Schedule a periodic job using Scheduler component.
     * The time interval for the job is available in the configuration.
     */
    private fun scheduleNewSamplingJob(interval: Long, schedulerComponent: SchedulerComponent) {
        cleanUpDanglingJobs()
        val workParameters = WorkParameters.Builder()
            .workId(WORK_ID)
            .periodicInterval(interval)
            .isPeriodic(true)
            .minimumLatency(1) // minimum latency: the minimum delay to trigger the work
            .sourceComponentName(REPORT_PROCESSING_COMPONENT_NAME)
        EchoLocateLog.eLogD(
            "Diagnostic : Scheduler : Nr5g processing NewSamplingJob:" +
                    " scheduledWorkId: ${Nr5gSharedPreference.scheduledWorkId}, " +
                    "periodicInterval: $interval"
        )
        val result = schedulerComponent.scheduleJob(context, workParameters)
        subscribeToSchedulerObservable(result)
    }

    /**
     * Any job id which are apart from this class canonicalName hash code will be deleted
     */
    private fun cleanUpDanglingJobs() {
        val schedulerComponent = SchedulerComponent.getInstance(context) as SchedulerComponent
        val workIdList = schedulerComponent.getWorkId(context, REPORT_PROCESSING_COMPONENT_NAME)
        for (workId in workIdList) {
            if (workId != WORK_ID) {
                schedulerComponent.deleteWorkByTag(workId.toString())
            }
        }
    }

    /**
     * Function responsible for subscribing to the scheduler observable for the 5G module and take appropriate action
     *
     * The function checks for two different states of the callback:
     * 1. [WorkScheduledStatus.SCHEDULED] Saves the details of the work job.
     * 2. [WorkScheduledStatus.DISPATCHED] performs the action when the work job is actually fired
     */
    @SuppressLint("CheckResult")
    private fun subscribeToSchedulerObservable(nr5gSchedulerObservable: Observable<SchedulerResponseEvent>) {
        schedulerDisposable = nr5gSchedulerObservable.subscribeOn(Schedulers.io())
            .observeOn(Schedulers.newThread())
            .filter {
                it.sourceComponent.equals(REPORT_PROCESSING_COMPONENT_NAME)
            }
            .subscribe({ response ->
                EchoLocateLog.eLogD(
                    "Diagnostic : Scheduler : Nr5g processing  Job is received " +
                            "for : ${response.sourceComponent} " +
                            "with state: ${response.status} " +
                            "and ID: ${response.workParameters.workId}"
                )

                if (response.status == WorkScheduledStatus.SCHEDULED.state) {
                    Nr5gSharedPreference.scheduledWorkId = response.workParameters.workId
                } else if (response.status == WorkScheduledStatus.DISPATCHED.state) {

                    base5gDataManager!!.processRawData(response.androidWorkId)
                }
            }, { error ->
                EchoLocateLog.eLogE("Diagnostic : Scheduler : Nr5g processing error on schedule : ${error.message}")
            })
    }

    /**
     * Delete a periodic job using Scheduler component.
     *  @param schedulerComponent SchedulerComponent received
     *  once configuration interval changes.
     */
    private fun deleteNr5gSamplingJob(schedulerComponent: SchedulerComponent) {
        if (Nr5gSharedPreference.scheduledWorkId != 0L) {
            schedulerComponent.deleteWorkByTag(Nr5gSharedPreference.scheduledWorkId.toString())
        }
    }

    /**
     * This function will delete the scheduler job
     * and unregister to listen to the updates.
     */
    fun stopScheduler() {
        deleteNr5gSamplingJob(SchedulerComponent.getInstance(context) as SchedulerComponent)
        schedulerDisposable?.dispose()
        EchoLocateLog.eLogI("Diagnostic : Scheduler : Nr5g processing Scheduler STOPPED and periodic job deleted")
    }
}