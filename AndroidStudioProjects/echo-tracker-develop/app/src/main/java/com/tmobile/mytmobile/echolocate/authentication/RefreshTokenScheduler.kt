package com.tmobile.mytmobile.echolocate.authentication

import android.annotation.SuppressLint
import android.content.Context
import com.tmobile.mytmobile.echolocate.authentication.provider.AuthenticationProvider
import com.tmobile.mytmobile.echolocate.scheduler.WorkParameters
import com.tmobile.mytmobile.echolocate.scheduler.WorkScheduledStatus
import com.tmobile.mytmobile.echolocate.scheduler.events.SchedulerResponseEvent
import com.tmobile.mytmobile.echolocate.schedulermanager.SchedulerComponent
import com.tmobile.mytmobile.echolocate.utils.EchoLocateLog
import com.tmobile.mytmobile.echolocate.authentication.utils.TokenSharedPreference
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

/**
 * This class is responsible to schedule job for refresh Authentication token.[DAT Token]
 */
class RefreshTokenScheduler(private val context: Context) {
    private var schedulerDisposable: Disposable? = null
    private val WORK_ID = javaClass.canonicalName.hashCode().toLong()

    /**
     * initialization part for variables used
     */
    init {
        TokenSharedPreference.init(context)
    }

    /**
     * Schedule a onr time job using Scheduler component.
     * The time interval for the job is available will be 1 hour before on Auth Token expired time.
     * To prevent a losing dataworkId saved to SharedPreference
     */

    //If the sampling interval has changed from the original one, then cancel the existing job and create a new one
    //interval has changed -> schedulerComponent.deleteWorkByTag ->

    @SuppressLint("CheckResult")
    fun schedulerJob(interval: Long, isEnabled: Boolean) {

        val schedulerComponent =
            SchedulerComponent.getInstance(context) as SchedulerComponent

        val workParameter =
            schedulerComponent.getWorkParamsByWorkID(
                TokenSharedPreference.scheduledWorkId, context
            )
        EchoLocateLog.eLogD("Diagnostic : Scheduler : DAT token scheduledWorkId: ${TokenSharedPreference.scheduledWorkId}")

        if (workParameter == null) {
            scheduleRefreshTokenJob(interval, schedulerComponent)
            EchoLocateLog.eLogD("Diagnostic : Scheduler : DAT token workParameter is null, starting new New Sampling Job")
            return
        }
        val currentInterval = workParameter.periodicInterval
        if (currentInterval != interval || !isEnabled) {
            scheduleRefreshTokenJob(interval, schedulerComponent)
        } else {
            subscribeToSchedulerObservable(schedulerComponent.observeSchedulerEvent())
        }
    }

    /**
     * Schedule one time job using Scheduler component.
     * The time interval for the job will be - One hour before the token Expires.
     */
    private fun scheduleRefreshTokenJob(interval: Long, schedulerComponent: SchedulerComponent) {
        cleanUpDanglingJobs()
        val workParameters = WorkParameters.Builder()
            .workId(WORK_ID)
            .minimumLatency(interval)
            .isPeriodic(false)
            .oneTimeRequest(true)
            .sourceComponentName(AuthenticationProvider.REFRESH_TOKEN_COMPONENT_NAME)

        EchoLocateLog.eLogD(
            "Diagnostic : Scheduler : DAT token NewSamplingJob: " +
                    "scheduledWorkId: ${TokenSharedPreference.scheduledWorkId}, " +
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
        val workIdList = schedulerComponent.getWorkId(
            context,
            AuthenticationProvider.REFRESH_TOKEN_COMPONENT_NAME
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
    private fun subscribeToSchedulerObservable(schedulerObservable: Observable<SchedulerResponseEvent>) {
        schedulerDisposable = schedulerObservable.subscribeOn(Schedulers.io())
            .observeOn(Schedulers.newThread())
            .filter {
                it.sourceComponent.equals(
                    AuthenticationProvider.REFRESH_TOKEN_COMPONENT_NAME
                )
            }
            .subscribe({ response ->

                EchoLocateLog.eLogD(
                    "Diagnostic : Scheduler : DAT token Job is received " +
                            "for : ${response.sourceComponent} " +
                            "with state: ${response.status} " +
                            "and ID: ${response.workParameters.workId}"
                )

                if (response.status == WorkScheduledStatus.SCHEDULED.state) {
                    TokenSharedPreference.scheduledWorkId = response.workParameters.workId
                } else if (response.status == WorkScheduledStatus.DISPATCHED.state) {
                    AuthenticationManager.getInstance(context)
                        .setWorkIdForScheduledJob(response.androidWorkId)
                    AuthenticationProvider.getInstance(context).refreshToken()
                    schedulerDisposable!!.dispose()
                }
                //save the response
                TokenSharedPreference.schedulerResponseEvent = response.toString()

            }, { error ->
                EchoLocateLog.eLogE(
                    "Diagnostic : Scheduler : " +
                            "DAT token error on schedule refresh token: ${error.message}")
            })
    }
}