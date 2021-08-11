package com.tmobile.mytmobile.echolocate.analytics.scheduler

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.work.WorkManager
import com.tmobile.mytmobile.echolocate.analytics.utils.AnalyticsConstants
import com.tmobile.mytmobile.echolocate.analytics.utils.AnalyticsConstants.ANALYTICS_DATAMETRIC_SCHEDULER
import com.tmobile.mytmobile.echolocate.analytics.utils.AnalyticsConstants.LOCATION_PERMISSION_AVAILABLE
import com.tmobile.mytmobile.echolocate.analytics.utils.AnalyticsConstants.NULL
import com.tmobile.mytmobile.echolocate.analytics.utils.AnalyticsConstants.PHONE_PERMISSION_AVAILABLE
import com.tmobile.mytmobile.echolocate.analytics.utils.AnalyticsSharedPreference
import com.tmobile.mytmobile.echolocate.communicationbus.rxbus.PostTicket
import com.tmobile.mytmobile.echolocate.communicationbus.rxbus.RxBus
import com.tmobile.mytmobile.echolocate.configuration.events.analytics.ELAnalyticActions
import com.tmobile.mytmobile.echolocate.configuration.events.analytics.ELAnalyticsEvent
import com.tmobile.mytmobile.echolocate.configuration.events.analytics.ELModulesEnum
import com.tmobile.mytmobile.echolocate.nr5g.core.oemdata.Nr5gDataMetricsWrapper
import com.tmobile.mytmobile.echolocate.nr5g.core.oemdata.Sa5gDataMetricsWrapper
import com.tmobile.mytmobile.echolocate.scheduler.WorkParameters
import com.tmobile.mytmobile.echolocate.scheduler.WorkScheduledStatus
import com.tmobile.mytmobile.echolocate.scheduler.WorkSchedulerManager
import com.tmobile.mytmobile.echolocate.scheduler.events.SchedulerResponseEvent
import com.tmobile.mytmobile.echolocate.schedulermanager.SchedulerComponent
import com.tmobile.mytmobile.echolocate.utils.ELDeviceUtils
import com.tmobile.mytmobile.echolocate.utils.EchoLocateLog
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

/**
 * Analytics Scheduler is a class for updating analytics of the
 * availability of data metrics class in Nr5g and Nsa5g.
 */
class AnalyticsScheduler(private val context: Context) {

    private var schedulerDisposable: Disposable? = null
    private val WORK_ID = javaClass.canonicalName.hashCode().toLong()

    /** Initialization part for variables used */
    init {
        AnalyticsSharedPreference.init(context)
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
    fun schedulerJob(interval: Long) {

        val schedulerComponent =
            SchedulerComponent.getInstance(context) as SchedulerComponent

        val workParameter =
            schedulerComponent.getWorkParamsByWorkID(
                AnalyticsSharedPreference.dataMetricsSchedulerWorkId, context
            )
        EchoLocateLog.eLogD("Diagnostic : Scheduler : Analytics processing dataMetricsSchedulerWorkId: ${AnalyticsSharedPreference.dataMetricsSchedulerWorkId}")

        if (workParameter == null) {
            scheduleNewSamplingJob(interval, schedulerComponent)
            EchoLocateLog.eLogD("Diagnostic : Scheduler : Analytics processing workParameter is null, starting new New Sampling Job")
            return
        }
        val currentInterval = workParameter.periodicInterval
        if (currentInterval != interval) {
            stopScheduler(context)
            scheduleNewSamplingJob(interval, schedulerComponent)
            Log.d("Diagnostic", "periodicInterval changed, starting new New Sampling Job")

        } else {
            if (schedulerDisposable == null || schedulerDisposable?.isDisposed!!) {
                Log.d("Diagnostic", "scheduler disposed or null, new subscribe")
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
            .sourceComponentName(ANALYTICS_DATAMETRIC_SCHEDULER)
        EchoLocateLog.eLogD(
            "Diagnostic : Scheduler : Analytics processing NewSamplingJob:" +
                    " dataMetricsSchedulerWorkId: ${AnalyticsSharedPreference.dataMetricsSchedulerWorkId}, " +
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
        val workIdList = schedulerComponent.getWorkId(context, ANALYTICS_DATAMETRIC_SCHEDULER)
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
    private fun subscribeToSchedulerObservable(analyticsSchedulerObservable: Observable<SchedulerResponseEvent>) {
        schedulerDisposable = analyticsSchedulerObservable.subscribeOn(Schedulers.io())
            .observeOn(Schedulers.newThread())
            .filter {
                it.sourceComponent.equals(ANALYTICS_DATAMETRIC_SCHEDULER)
            }
            .subscribe({ response ->
                EchoLocateLog.eLogD(
                    "Diagnostic : Scheduler : Analytics processing  Job is received " +
                            "for : ${response.sourceComponent} " +
                            "with state: ${response.status} " +
                            "and ID: ${response.workParameters.workId}"
                )

                if (response.status == WorkScheduledStatus.SCHEDULED.state) {
                    AnalyticsSharedPreference.dataMetricsSchedulerWorkId = response.workParameters.workId
                } else if (response.status == WorkScheduledStatus.DISPATCHED.state) {
                    postAnalyticsData()
                }
            }, { error ->
                EchoLocateLog.eLogE("Diagnostic : Scheduler : Analytics processing error on schedule : ${error.message}")
            })
    }

    /**
     * This function will post data to analytics
     */
    private fun postAnalyticsData(){
        postAnalyticEventForDataMetricsResult()
        postAnalyticEventForPermissionResult()
    }

    private fun postAnalyticEventForDataMetricsResult() {
        val nr5gDataMetricsWrapper = Nr5gDataMetricsWrapper(context)
        val sa5gDataMetricsWrapper = Sa5gDataMetricsWrapper(context)
        val nr5gDataMetricsApiVersion = nr5gDataMetricsWrapper.getApiVersion()
        val sa5gDataMetricsApiVersion = sa5gDataMetricsWrapper.getApiVersion()
        val isNr5gDataMetricsAvailable = nr5gDataMetricsWrapper.isDataMetricsAvailable()
        val isSa5gDataMetricsAvailable = sa5gDataMetricsWrapper.isDataMetricsAvailable()

        val payload = when {
            isNr5gDataMetricsAvailable && isSa5gDataMetricsAvailable ->
                "${AnalyticsConstants.LTE}$nr5gDataMetricsApiVersion ${AnalyticsConstants.SA5G}$sa5gDataMetricsApiVersion"
            !isNr5gDataMetricsAvailable && isSa5gDataMetricsAvailable ->
                "${AnalyticsConstants.SA5G}$sa5gDataMetricsApiVersion"
            isNr5gDataMetricsAvailable && !isSa5gDataMetricsAvailable ->
                "${AnalyticsConstants.LTE}$nr5gDataMetricsApiVersion"
            else ->
                NULL
        }
        postAnalyticsEvent(payload = payload,action = ELAnalyticActions.EL_DATAMETRICS_AVAILABILITY)
    }

    private fun postAnalyticEventForPermissionResult() {
        val isLocationPermissionGranted = isLocationPermissionAvailable()
        val isPhonePermissionGranted = isPhonePermissionAvailable()
        EchoLocateLog.eLogD("Diagnostic : Location Availability $isLocationPermissionGranted \n " +
                "Phone Permission availability $isPhonePermissionGranted")
        val payload = when {
            isLocationPermissionGranted && isPhonePermissionGranted -> {
                 "$PHONE_PERMISSION_AVAILABLE $LOCATION_PERMISSION_AVAILABLE"
            }
            !isLocationPermissionGranted && isPhonePermissionGranted -> {
                PHONE_PERMISSION_AVAILABLE
            }
            isLocationPermissionGranted && !isPhonePermissionGranted -> {
                LOCATION_PERMISSION_AVAILABLE
            }
            else -> NULL
        }
        postAnalyticsEvent(payload = payload,action = ELAnalyticActions.EL_PERMISSION_AVAILABILITY)
    }

    /**
     * Check location permission availability
     * @return if the Android API level is >=29 (P) check the background location and FINE and COARSE location
     * else check FINE and COARSE location
     */
    private fun isLocationPermissionAvailable(): Boolean {
        return if(ELDeviceUtils.isQDeviceOrHigher()){
            ( context.checkSelfPermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED && context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    && context.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        } else {
            (context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    && context.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        }
    }

    /**
     * Check Phone permission availability
     */
    private fun isPhonePermissionAvailable(): Boolean {
        return (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_PHONE_NUMBERS
        )) or
                (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.READ_PHONE_STATE
                ))
    }

    /**
     * This function is used to post new event to analytics manager
     * @param payload-stores the status code based on api status
     */

    private fun postAnalyticsEvent(payload: String,action:ELAnalyticActions) {
        val analyticsEvent = ELAnalyticsEvent(
            moduleName = ELModulesEnum.ANALYTICS,
            action = action,
            payload = payload
        )
        analyticsEvent.timeStamp = System.currentTimeMillis()

        val postAnalyticsTicket = PostTicket(analyticsEvent)
        RxBus.instance.post(postAnalyticsTicket)
    }

    /**
     * delete a periodic job using Scheduler component.
     */
    private fun deleteSamplingJob(context: Context, workId: String) {
        if (AnalyticsSharedPreference.dataMetricsSchedulerWorkId != 0L) {
            WorkSchedulerManager.getInstance()
                .deleteSchedulerJob(context, workId, WorkManager.getInstance(context))
        }
    }

    /**
     * This function will delete the scheduler job as well as unregister to listen to the updates.
     */
    fun stopScheduler(context: Context) {
        Log.d("Diagnostic", "Scheduler stopped")
        deleteSamplingJob(context, AnalyticsSharedPreference.dataMetricsSchedulerWorkId.toString())
        schedulerDisposable?.dispose()
    }
}