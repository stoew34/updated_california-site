package com.tmobile.mytmobile.echolocate.userconsent.consentreader

import android.content.Context
import com.tmobile.mytmobile.echolocate.configuration.events.analytics.ELAnalyticActions
import com.tmobile.mytmobile.echolocate.scheduler.events.SchedulerResponseEvent
import com.tmobile.mytmobile.echolocate.schedulermanager.SchedulerComponent
import com.tmobile.mytmobile.echolocate.scheduler.WorkParameters
import com.tmobile.mytmobile.echolocate.scheduler.WorkScheduledStatus
import com.tmobile.mytmobile.echolocate.userconsent.ConsentManager
import com.tmobile.mytmobile.echolocate.userconsent.utils.ConsentSharedPreference
import com.tmobile.mytmobile.echolocate.userconsent.utils.UserConsentStringUtils
import com.tmobile.mytmobile.echolocate.userconsent.utils.UserConsentUtils
import com.tmobile.mytmobile.echolocate.utils.EchoLocateLog
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class DiagnosticFlagScheduler(private val context: Context) {

    private var schedulerDisposable: Disposable? = null
    private val WORK_ID = javaClass.canonicalName.hashCode().toLong()

    init {
        ConsentSharedPreference.init(context)
    }

    fun schedulerJob() {

        // In case device is rebooted, we need to check if there is already a stored attempt
        if (ConsentSharedPreference.attemptNumber == UserConsentUtils.CONSENT_ATTEMPT_DEFAULT_UNASSIGNED_VALUE) {
            // Store the attempt number to preferences, so that it can be read in case of reboot
            ConsentSharedPreference.attemptNumber = UserConsentUtils.consentFlagAttemptsDuration.size - 1
        }

        // Make sure the attemptNumber is within the index limit of array [UserConsentUtils.consentFlagAttemptsDuration]
        if (ConsentSharedPreference.attemptNumber < 0 ||
            ConsentSharedPreference.attemptNumber >= UserConsentUtils.consentFlagAttemptsDuration.size) {
            return
        }

        EchoLocateLog.eLogD(
            "Diagnostic : DiagnosticFlagScheduler : scheduling job for " +
                    "${UserConsentUtils.consentFlagAttemptsDuration[ConsentSharedPreference.attemptNumber]} minutes : " +
                    "attempt = ${(UserConsentUtils.consentFlagAttemptsDuration.size - ConsentSharedPreference.attemptNumber)}"
        )

        val schedulerComponent = SchedulerComponent.getInstance(context) as SchedulerComponent

        val workParameter = schedulerComponent.getWorkParamsByWorkID(
            ConsentSharedPreference.scheduledWorkId,
            context
        )

        /** There is no existing job for consent flag recheck, schedule a new one.*/
        if (workParameter == null) {
            EchoLocateLog.eLogD("Diagnostic : DiagnosticFlagScheduler: workParameter is null, starting a new scheduling job")
            scheduleNewJob(
                UserConsentUtils.consentFlagAttemptsDuration[ConsentSharedPreference.attemptNumber],
                schedulerComponent
            )
        } else {
            subscribeToSchedulerObservable(schedulerComponent.observeSchedulerEvent())
        }
    }

    /**
     * Schedule one time job using Scheduler component.
     * The time interval for the job will be - One hour before the token Expires.
     */

    private fun scheduleNewJob(interval: Long, schedulerComponent: SchedulerComponent) {
        cleanUpDanglingJobs()
        val workParameters = WorkParameters.Builder()
            .workId(WORK_ID)
            .minimumLatency(interval)
            .isPeriodic(false)
            .oneTimeRequest(true)
            .sourceComponentName(UserConsentUtils.RECONFIRM_CONSENT_FLAGS_COMPONENT_NAME)

        val result = schedulerComponent.scheduleJob(context, workParameters)
        subscribeToSchedulerObservable(result)
    }

    /**
     * Any job id which are apart from this class canonicalName hash code will be deleted
     */
    private fun cleanUpDanglingJobs() {
        val schedulerComponent = SchedulerComponent.getInstance(context) as SchedulerComponent
        val workIdList = schedulerComponent.getWorkId(context,
            UserConsentUtils.RECONFIRM_CONSENT_FLAGS_COMPONENT_NAME
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
    private fun subscribeToSchedulerObservable(schedulerObservable: Observable<SchedulerResponseEvent>) {
        if (schedulerDisposable == null || schedulerDisposable!!.isDisposed) {
            EchoLocateLog.eLogD("Diagnostic : DiagnosticFlagScheduler: scheduler disposed or null, new subscriber")
            schedulerDisposable = schedulerObservable.subscribeOn(Schedulers.io())
                .observeOn(Schedulers.newThread())
                .filter {
                    it.sourceComponent.equals(
                        UserConsentUtils.RECONFIRM_CONSENT_FLAGS_COMPONENT_NAME
                    )
                }
                .subscribe({ response ->
                    EchoLocateLog.eLogD("Diagnostic : DiagnosticFlagScheduler: Job is received for : ${response.sourceComponent} with state: ${response.status} and ID: ${response.workParameters.workId}")
                    if (response.status == WorkScheduledStatus.SCHEDULED.state) {
                        ConsentSharedPreference.scheduledWorkId = response.workParameters.workId
                    } else if (response.status == WorkScheduledStatus.DISPATCHED.state) {
                        // First delete the existing scheduled job, as we don't need it anymore
                        val schedulerComponent =
                            SchedulerComponent.getInstance(context) as SchedulerComponent
                        schedulerComponent.deleteWorkByTag(ConsentSharedPreference.scheduledWorkId.toString())

                        // Decrement the attempt number in shared prefs as the previous one is done
                        ConsentSharedPreference.attemptNumber = (ConsentSharedPreference.attemptNumber - 1)

                        // Check the consent flags from content provider
                        checkFlagsWithContentProvider(context)

                        schedulerDisposable!!.dispose()
                        schedulerDisposable = null
                    }
                }, { error ->
                    EchoLocateLog.eLogE("Diagnostic : error on DiagnosticFlagScheduler: ${error.message}")
                })
        }
    }

    private fun checkFlagsWithContentProvider(context: Context) {
        val userConsentResponseEvent = DiagnosticFlagsResolver(context).fetchDiagnosticFlags()
        if (userConsentResponseEvent.sourceComponent != UserConsentStringUtils.SRC_CONSENT_FLAG_DEFAULT) {
            val consentManager: ConsentManager = ConsentManager.getInstance(context)
            userConsentResponseEvent.timeStamp = System.currentTimeMillis()

            consentManager.saveFlagstoDB(userConsentResponseEvent)

            consentManager.postAnalyticsEventForUserConsent(
                userConsentResponseEvent.userConsentFlagsParameters.isAllowedDeviceDataCollection.toString(),
                ELAnalyticActions.EL_USER_CONSENT_DATA_COLLECTION
            )
            consentManager.postAnalyticsEventForUserConsent(
                userConsentResponseEvent.userConsentFlagsParameters.isAllowedIssueAssist.toString(),
                ELAnalyticActions.EL_USER_CONSENT_ISSUE_ASSIST
            )
        }
    }
}
