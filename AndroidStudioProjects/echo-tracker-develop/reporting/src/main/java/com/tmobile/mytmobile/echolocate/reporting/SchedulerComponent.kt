package com.tmobile.mytmobile.echolocate.reporting

import android.annotation.SuppressLint
import android.content.Context
import androidx.work.WorkManager
import com.tmobile.mytmobile.echolocate.communicationbus.rxbus.RxBus
import com.tmobile.mytmobile.echolocate.communicationbus.rxbus.SubscribeTicket
import com.tmobile.mytmobile.echolocate.scheduler.IWorkScheduler
import com.tmobile.mytmobile.echolocate.scheduler.WorkParameters
import com.tmobile.mytmobile.echolocate.scheduler.WorkSchedulerManager
import com.tmobile.mytmobile.echolocate.scheduler.events.SchedulerResponseEvent
import io.reactivex.Observable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext

/**
 * SchedulerComponent is a singleton class for exposing API and
 * sending the response back to the client requests
 */

internal class SchedulerComponent private constructor(val context: Context) : IWorkScheduler {

    private val bus = RxBus.instance
    private val schedulerMaintainerContext = newSingleThreadContext("schedulerMaintainerThread")

    companion object {
        @Volatile
        private var INSTANCE: IWorkScheduler? = null

        /***
         * access to singleton ReportProvider object
         */
        fun getInstance(context: Context): IWorkScheduler {
            return INSTANCE
                ?: synchronized(this) {
                    val instance: IWorkScheduler = SchedulerComponent(context)
                    INSTANCE = instance
                    instance
                }
        }
    }

    override fun initialize() {
        WorkSchedulerManager.getInstance().addWorkListenersForAllScheduledJobs(context)
    }

    /**
     * This method is used to schedule different kind of jobs either periodic or one time
     * based on the request from the host client based on the work parameters passed, it creates an observable
     * that observes the work scheduled and returns response on the onNext() method of the observer
     *
     * @param context Context: the context of the calling module
     * @param workParameters WorkParameters: parameters that define the type and the criteria of a job
     * @return an Observable<SchedulerResponseEvent> instance that contains the status, unique id, timestamp
     *
     */
    @SuppressLint("CheckResult")
    override fun scheduleJob(context: Context, workParameters: WorkParameters.Builder): Observable<SchedulerResponseEvent> {
        val subscribeTicket =
            SubscribeTicket(RxBus.SubjectType.PUBLISH_SUBJECT)


        CoroutineScope(schedulerMaintainerContext).launch {
            WorkSchedulerManager.getInstance().initiateSchedulerJob(context, workParameters.build())
        }


        return bus.register(subscribeTicket)
    }

    /**
     * Function which creates an observable for [SchedulerResponseEvent] using [RxBus]
     * so that modules can listen to previously scheduled work jobs
     *
     * This function should be used when the work was already scheduled. This may happen when a calling module schedules a job
     * but the application is killed and calling modules still needs to know when the job is run.
     *
     * @return Observable of type [SchedulerResponseEvent]
     */
    override fun observeSchedulerEvent(): Observable<SchedulerResponseEvent> {
        val subscribeTicket =
            SubscribeTicket(RxBus.SubjectType.PUBLISH_SUBJECT)

        return bus.register(subscribeTicket)
    }

    /**
     * This method is used for retrieving the scheduled work based on the source component
     * this method accesses the getSchedulerWorkIds from the getInstance() of the WorkScheudlerManager
     *
     * @param context Context: the context of the calling module
     * @param sourceComponentName SourceComponent: the source component for which the work id's to be
     *          returned
     * @return an List<Long> list of workIds
     */
    override fun getWorkId(context: Context, sourceComponentName: String): List<Long> {
        return WorkSchedulerManager.getInstance().getSchedulerWorkIds(context, sourceComponentName)
    }

    /**
     * This method deletes the work scheduled based on the work id passed
     *
     * @param workId WorkId: the ID for which the work should be deleted
     */
    override fun deleteWorkByTag(workId: String) {
        CoroutineScope(schedulerMaintainerContext).launch {
            WorkSchedulerManager.getInstance().deleteSchedulerJob(context, workId, WorkManager.getInstance(context))
        }
    }

    override fun getWorkParamsByWorkID(workId: Long, context: Context): WorkParameters? {
        return WorkSchedulerManager.getInstance().getWorkParamsByWorkID(workId, context)

    }
}