package com.tmobile.mytmobile.echolocate.lte.delegates

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import com.tmobile.mytmobile.echolocate.communicationbus.rxbus.PostTicket
import com.tmobile.mytmobile.echolocate.communicationbus.rxbus.RxBus
import com.tmobile.mytmobile.echolocate.communicationbus.rxbus.SubscribeTicket
import com.tmobile.mytmobile.echolocate.lte.lteevents.ApplicationTriggerLimitEvent
import com.tmobile.mytmobile.echolocate.lte.lteevents.BaseNonStreamEvent
import com.tmobile.mytmobile.echolocate.lte.model.TriggerData
import com.tmobile.mytmobile.echolocate.lte.utils.ApplicationState
import com.tmobile.mytmobile.echolocate.lte.utils.ApplicationTrigger
import com.tmobile.mytmobile.echolocate.lte.utils.LTEApplications
import com.tmobile.mytmobile.echolocate.lte.utils.LteConstants.EMPTY
import com.tmobile.mytmobile.echolocate.utils.EchoLocateLog
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

/**
 * Handles non stream applications focus gain, focus loss and screen off events.
 */
abstract class BaseNonStreamDelegate(context: Context) : BaseDelegate(context) {


    private var alarmMgr: AlarmManager? = null

    private val timeOutIDs: ArrayList<Int> = ArrayList()

    private var applicationTrigger: ApplicationTrigger = ApplicationTrigger.getInstance(context)

    private var appTriggerLimitDisposable: Disposable? = null

    private var periodicActionDisposable: Disposable? = null

    private var stateFocused = false

    /**
     *  Application focus gain or focus loss state listener
     */
    override fun processApplicationState(state: ApplicationState) {
        synchronized(this) {
            EchoLocateLog.eLogE("ApplicationState.processApplicationState $state")
            listenApplicationTriggerLimitReachedEvent()
            when (state) {
                /**
                 * When the application focus gain this will called.
                 * Get's the respective focus gain code and store lte entity into the database
                 */
                ApplicationState.FOCUS_GAIN -> {
                    // This is a safety net to make sure any non-needed alarms from previous triggers are not active anymore.
                    stopScheduler()
                    stateFocused = true
                    EchoLocateLog.eLogD("LTE Non Stream ApplicationState FOCUS_GAIN: Code ${getFocusGainCode()} Application: ${getTriggerApplication()}")
                    if (applicationTrigger.isTriggerLimitReached()) {
                        EchoLocateLog.eLogV("CMS Limit-non stream delegate -  focus gain returning as limit reached")
                        return
                    }
                    storeLteEntity(getFocusGainCode())
                    listenDelegateActions()
                    startScheduler()
                }
                /**
                 * When the application focus loss this will called.
                 * Get's the respective focus loss code and store lte entity into the database
                 */
                ApplicationState.FOCUS_LOSS -> {
                    stateFocused = false
                    EchoLocateLog.eLogD("LTE Non Stream ApplicationState FOCUS_LOSS: Code ${getFocusLossCode()} Application: ${getTriggerApplication()}")
                    stopScheduler()
                    if (applicationTrigger.isTriggerLimitReached()) {
                        EchoLocateLog.eLogV("CMS Limit-non stream delegate -  focus loss returning as limit reached")
                        return
                    }
                    storeLteEntity(getFocusLossCode())
                }
                else -> EchoLocateLog.eLogD("The LTE module does not support application state: $state")
            }
        }
    }

    /**
     * This function will check the actions received from logcat listner
     * and checks with the respective delegate action,
     * if actions is available process intent will be called
     */
    @SuppressLint("CheckResult")
    private fun listenDelegateActions() {
        val subscribeTicket =
            SubscribeTicket(RxBus.SubjectType.PUBLISH_SUBJECT)
        periodicActionDisposable?.dispose()
        periodicActionDisposable = RxBus.instance.register<BaseNonStreamEvent>(subscribeTicket)
            .observeOn(Schedulers.io())
            .subscribeOn(Schedulers.io()).subscribe {
                getPeriodicTriggers().forEach { triggerData: TriggerData ->
                    EchoLocateLog.eLogV("CMS Limit-non stream delegate ----rx bus action received " + it.intent.action!!)
                    if (triggerData.triggerAction == it.intent.action)
                        storePeriodicData(triggerData.triggerCode)
                }
            }
    }

    private fun storePeriodicData(triggerCode: Int) {
        EchoLocateLog.eLogE("action received for $triggerCode")
        storeLteEntity(triggerCode)

    }

    /**
     * This function is responsible to run the scheduler task with delay
     * and then calls the function storeLteEntity,this function will store the respective trigger code in to an entity
     */
    private fun startScheduler() {
            timeOutIDs.clear()
            for (item in getPeriodicTriggers()) {
                createAlarmManagerInstance(item.triggerDelay, item.triggerAction, item.triggerCode)
            }
    }

    private fun createAlarmManagerInstance(delay: Long, action: String, timeoutId: Int) {
        val intent = Intent(context, MyAlarmReceiver::class.java)
        intent.action = action
        val timeoutPendingIntent = PendingIntent.getBroadcast(
            context, timeoutId, intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        alarmMgr = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmMgr?.setExact(
            AlarmManager.ELAPSED_REALTIME_WAKEUP,
            SystemClock.elapsedRealtime() + delay,
            timeoutPendingIntent!!
        )

        timeOutIDs.add(timeoutId)
        EchoLocateLog.eLogE("CMS Limit-Alarm Scheduled with $delay and $action")

    }

    /**
     * This function is responsible to stop all the scheduled tasks
     */
    @Synchronized
    fun stopAlarmManagerInstance() {
        if (timeOutIDs.isNotEmpty()) {

            try {
                for (index in timeOutIDs.indices){
                    val intent = Intent(context, MyAlarmReceiver::class.java)
                    val triggerAction = getPeriodicTriggers()[index].triggerAction
                    intent.action = triggerAction
                    val action = timeOutIDs[index]

                    val timeoutPendingIntent = PendingIntent.getBroadcast(
                        context, action, intent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                    )

                    alarmMgr?.cancel(
                        timeoutPendingIntent
                    )
                }
            } catch (e: IndexOutOfBoundsException) {
                EchoLocateLog.eLogE("IndexOutOfBoundsException in BaseNonStreamDelegate's stopAlarmManagerInstance")
            }

            timeOutIDs.clear()

        }
    }

    /**
    This class is responsible to receive the timeout events and send back to delegate
    which again will get the intent through rx bus and calls the process intent method
     */
    class MyAlarmReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            EchoLocateLog.eLogV("CMS Limit-non stream delegate -  posting action: ${intent.action}")
            val postTicket = PostTicket(
                BaseNonStreamEvent(intent)
            )
            RxBus.instance.post(postTicket)
        }
    }

    /**
     * This function is responsible to stop all the scheduled tasks against each delegates
     */
    fun stopScheduler() {
        EchoLocateLog.eLogV("CMS Limit-non stream delegate -  stop scheduler")
        stopAlarmManagerInstance()
        // Do not listen for periodic events. If we don't do this, subsequent app periodic actions will also be received by this and will generate fake triggers
        periodicActionDisposable.run {
            RxBus.instance.unregister(periodicActionDisposable)
        }
            RxBus.instance.unregister(appTriggerLimitDisposable)
        //allowing the disposable to register on the next event, thus making it null
        appTriggerLimitDisposable = null
    }

    /**
     * Stores the periodic trigger and cancel if timer scheduled.
     * @param intent intent from the broadcast trigger
     */
    override fun handleIntent(intent: Intent?) {
        if (stateFocused) {
            listenApplicationTriggerLimitReachedEvent()
            EchoLocateLog.eLogE("ApplicationState.SCREEN_OFF " + getScreenOffCode())
            storeLteEntity(getScreenOffCode())
            stopScheduler()
        }
    }

    override fun getLogcatListenerIds(): List<String> {
        return emptyList()
    }

    override fun getTimeoutAction(): String {
        return EMPTY
    }

    override fun getTimeoutRequestCode(): Int {
        return 0
    }

    /**
     * @return get triggered application
     */
    abstract override fun getTriggerApplication(): LTEApplications

    /**
     * @return focus gain code
     */
    internal abstract fun getFocusGainCode(): Int

    /**
     * @return focus loss code
     */
    internal abstract fun getFocusLossCode(): Int

    /**
     * @return screen off code
     */
    internal abstract fun getScreenOffCode(): Int

    /**
     * @return run schedulers
     */
    internal abstract fun getPeriodicTriggers(): List<TriggerData>


    /**
     * Subscribes to application trigger limit reached event
     */
    @SuppressLint("CheckResult")
    private fun listenApplicationTriggerLimitReachedEvent() {
        val subscribeTicket = SubscribeTicket(RxBus.SubjectType.PUBLISH_SUBJECT)

        if (appTriggerLimitDisposable == null) {
            EchoLocateLog.eLogV("CMS Limit-non stream delegate subscribing as instance is null")
            appTriggerLimitDisposable =
                RxBus.instance.register<ApplicationTriggerLimitEvent>(subscribeTicket)
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.io()).subscribe {
                        EchoLocateLog.eLogV("CMS Limit-non stream delegate -  rx bus action")
                        stopScheduler()
                    }
        } else {
            EchoLocateLog.eLogV("CMS Limit-non stream delegate appTriggerLimitDisposable is not null")
        }
    }
}
