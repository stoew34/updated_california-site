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
import com.tmobile.mytmobile.echolocate.configuration.model.LTE
import com.tmobile.mytmobile.echolocate.lte.lteevents.LteHourlyListenerEvent
import com.tmobile.mytmobile.echolocate.lte.lteevents.ApplicationTriggerLimitEvent
import com.tmobile.mytmobile.echolocate.lte.utils.ApplicationState
import com.tmobile.mytmobile.echolocate.lte.utils.LTEApplications
import com.tmobile.mytmobile.echolocate.utils.EchoLocateLog
import com.tmobile.mytmobile.echolocate.utils.SingletonHolder
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

/**
 * Delegate for hourly triggers of LTE
 */
class LteHourlyDelegate(context: Context) : BaseDelegate(context) {

    /**
     * HOURLY_TRIGGER_CODE
     *
     * Hourly trigger code for LTE
     * value 100
     */
    private val HOURLY_TRIGGER_CODE = 100

    /**
     * HOURLY_TRIGGER_ACTION
     *
     * Hourly trigger action for LTE
     * value HOURLY_TRIGGER_ACTION
     */
    private val HOURLY_TRIGGER_ACTION = "HOURLY_TRIGGER_ACTION"

    /**
     * AlarmManager
     *
     * Alarm manager variable Used for scheduling hourly trigger
     * value alarmMgr
     */
    private var alarmMgr: AlarmManager? = null

    companion object : SingletonHolder<LteHourlyDelegate, Context>(::LteHourlyDelegate)

    private var appTriggerLimitDisposable: Disposable? = null

    /**
     * Disposable
     *
     * Rx bus disposable for unregister hourly update
     * value hourlyEventDisposable
     */
    var hourlyEventDisposable: Disposable? = null

    init {
        listenApplicationTriggerLimitReachedEvent()
    }

    /**
     * This function is responsible to run the scheduler task with hourly
     * and then calls the function storeLteEntity
     * This function will store the respective trigger code in to an entity
     */
    fun startScheduler() {
        //Canceling previous alarms if scheduled any
        stopPeriodicActions()
        listenDelegateActions()
        createAlarmManagerInstance(
            HOURLY_TRIGGER_ACTION,
            HOURLY_TRIGGER_CODE
        )
    }

    /**
     * This function will check the actions received from the alarm manager through rx bus.
     * and store the data in the local room database
     */
    @SuppressLint("CheckResult")
    private fun listenDelegateActions() {
        val subscribeTicket =
            SubscribeTicket(RxBus.SubjectType.PUBLISH_SUBJECT)
        hourlyEventDisposable = RxBus.instance.register<LteHourlyListenerEvent>(subscribeTicket)
            .observeOn(Schedulers.io())
            .subscribeOn(Schedulers.io())
            .subscribe {
                EchoLocateLog.eLogV("CMS Limit-HourlyDelegate----rx bus action received " + it.intent.action!!)
                createAlarmManagerInstance(HOURLY_TRIGGER_ACTION, HOURLY_TRIGGER_CODE)
                storePeriodicData()
            }
    }


    /**
     * This function will stores the hourly trigger code
     * un register hourly update rx bus disposable
     * and schedule another hour trigger.
     */
    private fun storePeriodicData() {
        EchoLocateLog.eLogV("CMS Limit-HourlyDelegate action received for $HOURLY_TRIGGER_CODE")
        storeLteEntity(HOURLY_TRIGGER_CODE)
    }

    /**
     *  keep this fun just in case we need to do disposable
     */
    private fun unRegisterHourlyUpdate() =
        hourlyEventDisposable?.run {
            RxBus.instance.unregister(this)
        }

    /**
     * This function is responsible for creating alarm
     */
    private fun createAlarmManagerInstance(action: String, timeoutId: Int) {
        val intent = Intent(context, HourlyTriggerReceiver::class.java)
        intent.action = action
        val timeoutPendingIntent = PendingIntent.getBroadcast(
            context, timeoutId, intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        alarmMgr = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmMgr?.setExact(
            AlarmManager.ELAPSED_REALTIME_WAKEUP,
            SystemClock.elapsedRealtime() + AlarmManager.INTERVAL_HOUR,
            timeoutPendingIntent
        )
        EchoLocateLog.eLogV("CMS Limit-HourlyDelegate - alarm scheduled for $action with id $timeoutId")
    }

    /**
     * This class is responsible to receive the timeout events and send back to delegate
     * which again will get the intent through rx bus and calls the process intent method
     */
    class HourlyTriggerReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            EchoLocateLog.eLogV("CMS Limit-hourly delegate -  posting action: ${intent.action}")

            val postTicket = PostTicket(LteHourlyListenerEvent(intent))
            RxBus.instance.post(postTicket)
        }
    }

    /**
     * This function is responsible to stop all periodic triggers
     */
    fun stopPeriodicActions() {
        unRegisterHourlyUpdate()

        val intent = Intent(context, HourlyTriggerReceiver::class.java)
        intent.action = HOURLY_TRIGGER_ACTION
        val timeoutPendingIntent = PendingIntent.getBroadcast(
            context, HOURLY_TRIGGER_CODE, intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        alarmMgr?.cancel(
            timeoutPendingIntent
        )

        timeoutPendingIntent?.cancel()

        RxBus.instance.unregister(appTriggerLimitDisposable)
        //allowing the disposable to register on the next event, thus making it null
        appTriggerLimitDisposable = null

        EchoLocateLog.eLogV("HourlyDelegate Alarm canceled")
    }


    override fun processApplicationState(state: ApplicationState) {
        EchoLocateLog.eLogD("Not required for LTE hourly trigger")
    }

    override fun handleIntent(intent: Intent?) {
        EchoLocateLog.eLogD("Not required for LTE hourly trigger")
    }

    override fun getTriggerApplication(): LTEApplications? {
        return null
    }

    override fun getLogcatListenerIds(): List<String> {
        EchoLocateLog.eLogD("Not required for LTE hourly trigger")
        TODO("Not required for LTE hourly trigger")
    }

    override fun getTimeoutAction(): String {
        EchoLocateLog.eLogD("Not required for LTE hourly trigger")
        TODO("Not required for LTE hourly trigger")
    }

    override fun getTimeoutRequestCode(): Int    {
        EchoLocateLog.eLogD("Not required for LTE hourly trigger")
        TODO("Not required for LTE hourly trigger")
    }

    override fun getApplicationState(triggerCode: Int): ApplicationState? {
        return null
    }

    /**
     * @return BaseDelegate
     */
    override fun setRegexFromConfig(lteConfig: LTE): BaseDelegate? {
        return null
    }

    /**
     * Subscribes to application trigger limit reached event
     */
    @SuppressLint("CheckResult")
    private fun listenApplicationTriggerLimitReachedEvent() {
        val subscribeTicket = SubscribeTicket(RxBus.SubjectType.PUBLISH_SUBJECT)
        if(appTriggerLimitDisposable == null) {
            EchoLocateLog.eLogV("CMS Limit-hourly  delegate subscribing as instance is null")
            appTriggerLimitDisposable =
                RxBus.instance.register<ApplicationTriggerLimitEvent>(subscribeTicket).subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.io()).subscribe {
                        EchoLocateLog.eLogV("CMS Limit-hourly delegate -  rx bus action")
                        stopPeriodicActions()
                    }
        }else{
            EchoLocateLog.eLogV("CMS Limit-hourly delegate appTriggerLimitDisposable is not null")
        }
    }


}
