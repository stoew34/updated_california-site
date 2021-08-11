package com.tmobile.mytmobile.echolocate.voice.reportprocessor

import android.annotation.SuppressLint
import android.content.Context
import com.tmobile.mytmobile.echolocate.scheduler.events.SchedulerResponseEvent
import com.tmobile.mytmobile.echolocate.schedulermanager.SchedulerComponent
import com.tmobile.mytmobile.echolocate.scheduler.WorkParameters
import com.tmobile.mytmobile.echolocate.scheduler.WorkScheduledStatus
import com.tmobile.mytmobile.echolocate.utils.EchoLocateLog
import com.tmobile.mytmobile.echolocate.voice.utils.VoiceIntents.REPORT_GENERATOR_COMPONENT_NAME
import com.tmobile.mytmobile.echolocate.voice.utils.VoiceSharedPreference
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers


/**
 * VoiceReportScheduler is class for exposing scheduler job,
 * subscribe To Scheduler Observable and deleting sampling job
 */
class VoiceReportScheduler(private val context: Context) {

    private val voiceReportProcessor = VoiceReportProcessor.getInstance(context)

    private var schedulerDisposable: Disposable? = null
    private val WORK_ID = javaClass.canonicalName.hashCode().toLong()

    /**
     * initialization part for variables used
     */
    init {
        VoiceSharedPreference.init(context)
    }

    /**
     * Schedule a periodic job using Scheduler component.
     * The time interval for the job is available in the configuration under voice - 6 hours.
     * To prevent a losing dataworkId saved to SharedPreference
     *
     * interval logic:
     *  If the sampling interval has changed from the original one,
     *  then cancel the existing job and create a new one
     *  interval has changed -> schedulerComponent.deleteWorkByTag ->
     */
    @SuppressLint("CheckResult")
    fun schedulerJob(interval: Long, isEnabled: Boolean) {

        val schedulerComponent = SchedulerComponent.getInstance(context) as SchedulerComponent

        val workParameter = schedulerComponent.getWorkParamsByWorkID(VoiceSharedPreference.scheduledWorkId, context)
        EchoLocateLog.eLogD("Diagnostic : Scheduler : Voice scheduledWorkId: ${VoiceSharedPreference.scheduledWorkId}")

        /** There is no existing job for voice sampling, schedule a new one.*/
        if (workParameter == null) {
            scheduleNewSamplingJob(interval, schedulerComponent)
            EchoLocateLog.eLogD("Diagnostic : Scheduler : Voice workParameter is null, starting new New Sampling Job")
            return
        }

        EchoLocateLog.eLogD("Diagnostic : Scheduler : Voice existing job found in the db")
        if (!isEnabled) {
            deleteVoiceSamplingJob(schedulerComponent)
            return
        }

        /** If the interval has changed, then cancel the existing one and schedule a new job.*/
        val currentInterval = workParameter.periodicInterval
        if (currentInterval != interval) {
            stopScheduler()
            scheduleNewSamplingJob(interval, schedulerComponent)
            EchoLocateLog.eLogD("Diagnostic : Scheduler : Voice periodicInterval changed, starting new New Sampling Job")
        } else {
            /** The interval has not changed, just subscribe to the existing job.*/
            if (schedulerDisposable == null || schedulerDisposable?.isDisposed!!) {
                EchoLocateLog.eLogD("Diagnostic : Scheduler : Voice scheduler disposed or null, new subscribe")
                subscribeToSchedulerObservable(schedulerComponent.observeSchedulerEvent())
            }
        }
    }

    /**
     * Schedule a periodic job using Scheduler component.
     * The time interval for the job is available in the configuration under voice - 6 hours.
     */
    private fun scheduleNewSamplingJob(interval: Long, schedulerComponent: SchedulerComponent) {
        cleanUpDanglingJobs()
        subscribeToSchedulerObservable(schedulerComponent.observeSchedulerEvent())

        val workParameters = WorkParameters.Builder()
            .workId(WORK_ID)
            .periodicInterval(interval)
            .isPeriodic(true)
            .minimumLatency(1) // minimum latency: the minimum delay to trigger the work
            .sourceComponentName(REPORT_GENERATOR_COMPONENT_NAME)
        EchoLocateLog.eLogD("Diagnostic : Scheduler : Voice NewSamplingJob: scheduledWorkId: ${VoiceSharedPreference.scheduledWorkId}, periodicInterval: $interval")
        schedulerComponent.scheduleJob(context, workParameters)
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
    private fun subscribeToSchedulerObservable(voiceSchedulerObservable: Observable<SchedulerResponseEvent>) {
        schedulerDisposable = voiceSchedulerObservable.subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .filter {
                it.sourceComponent.equals(REPORT_GENERATOR_COMPONENT_NAME)
            }
            .subscribe({ response ->
                EchoLocateLog.eLogD("Diagnostic : Scheduler : Voice: Job is received for : ${response.sourceComponent} with state: ${response.status} and ID: ${response.workParameters.workId}")
                if (response.status == WorkScheduledStatus.SCHEDULED.state) {
                    VoiceSharedPreference.scheduledWorkId = response.workParameters.workId
                } else if (response.status == WorkScheduledStatus.DISPATCHED.state) {
                    voiceReportProcessor.processRawData(response.androidWorkId)
                }
            }, { error ->
                EchoLocateLog.eLogE("Diagnostic : Scheduler : Voice error on schedule : ${error.message}")
            })
    }

    /**
     * delete a periodic job using Scheduler component.
     *  @param schedulerComponent SchedulerComponent received once configuration voice interval changes.
     */
    private fun deleteVoiceSamplingJob(schedulerComponent: SchedulerComponent) {
        if (VoiceSharedPreference.scheduledWorkId != 0L) {
            schedulerComponent.deleteWorkByTag(VoiceSharedPreference.scheduledWorkId.toString())
        }
    }

    /**
     * This function will delete the scheduler job as well as unregister to listen to the updates.
     */
    fun stopScheduler() {
        deleteVoiceSamplingJob(SchedulerComponent.getInstance(context) as SchedulerComponent)
        schedulerDisposable?.dispose()
        EchoLocateLog.eLogI("Diagnostic : Scheduler : Voice Scheduler STOPPED and periodic job deleted")
    }
}