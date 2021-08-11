package com.tmobile.mytmobile.echolocate.nr5g.core.delegates

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
import com.tmobile.mytmobile.echolocate.nr5g.core.events.Nr5gHourlyListenerEvent
import com.tmobile.mytmobile.echolocate.nr5g.core.utils.ApplicationState
import com.tmobile.mytmobile.echolocate.nr5g.core.utils.Nr5gApplicationTrigger
import com.tmobile.mytmobile.echolocate.nr5g.nsa5g.model.BaseNr5gTriggerData
import com.tmobile.mytmobile.echolocate.utils.EchoLocateLog
import com.tmobile.mytmobile.echolocate.utils.SingletonHolder
import io.reactivex.disposables.Disposable

/**
 * Delegate for hourly triggers of NR5G
 */
class Nr5gHourlyDelegate(context: Context) : BaseDelegate(context) {

    /**
     * NR5G_HOURLY_TRIGGER_CODE
     *
     * Hourly trigger code for NR5G
     * value 100
     */
    private val NR5G_HOURLY_TRIGGER_CODE = 100

    /**
     * NR5G_HOURLY_TRIGGER_ACTION
     *
     * Hourly trigger action for NR5G
     * value NR5G_HOURLY_TRIGGER_ACTION
     */
    private val NR5G_HOURLY_TRIGGER_ACTION = "NR5G_HOURLY_TRIGGER_ACTION"

    /**
     * AlarmManager
     *
     * Alarm manager variable Used for scheduling hourly trigger
     * value alarmMgr
     */
    private var alarmMgr: AlarmManager? = null
    private var nr5gApplicationTrigger: Nr5gApplicationTrigger =
        Nr5gApplicationTrigger.getInstance(context)


    companion object : SingletonHolder<Nr5gHourlyDelegate, Context>(::Nr5gHourlyDelegate)

    /**
     * Disposable
     *
     * Rx bus disposable for unregister hourly update
     * value hourlyEventDisposable
     */
    var hourlyEventDisposable: Disposable? = null

    /**
     * This function is responsible to run the scheduler task with hourly
     */
    fun startScheduler() {
        stopPeriodicActions()
        listenDelegateActions()
        createAlarmManagerInstance(
            NR5G_HOURLY_TRIGGER_ACTION,
            NR5G_HOURLY_TRIGGER_CODE
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
        hourlyEventDisposable = RxBus.instance.register<Nr5gHourlyListenerEvent>(subscribeTicket)
            .observeOn(io.reactivex.schedulers.Schedulers.io())
            .subscribeOn(io.reactivex.schedulers.Schedulers.io())
            .subscribe {
                launchTriggerTimestamp = System.currentTimeMillis()
                createAlarmManagerInstance(NR5G_HOURLY_TRIGGER_ACTION, NR5G_HOURLY_TRIGGER_CODE)
                storePeriodicData()
            }
    }


    /**
     * This function will stores the hourly trigger code
     */
    private fun storePeriodicData() {
        EchoLocateLog.eLogV("Nr5g CMS Limit- Nr5gHourlyDelegate action received for $NR5G_HOURLY_TRIGGER_CODE")
        store5gEntity(NR5G_HOURLY_TRIGGER_CODE, nr5gApplicationTrigger.isAppCountWithinLimit())
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
        EchoLocateLog.eLogV("Nr5g CMS Limit- Nr5gHourlyDelegate Alarm Scheduled")
    }

    /**
     * This class is responsible to receive the timeout events and send back to delegate
     * which again will get the intent through rx bus and calls the process intent method
     */
    class HourlyTriggerReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val postTicket = PostTicket(Nr5gHourlyListenerEvent(intent))
            RxBus.instance.post(postTicket)
        }
    }

    /**
     * This function is responsible to stop all periodic triggers
     */
    fun stopPeriodicActions() {
        unRegisterHourlyUpdate()

        val intent = Intent(context, HourlyTriggerReceiver::class.java)
        intent.action = NR5G_HOURLY_TRIGGER_ACTION
        val timeoutPendingIntent = PendingIntent.getBroadcast(
            context, NR5G_HOURLY_TRIGGER_CODE, intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        alarmMgr?.cancel(
            timeoutPendingIntent
        )

        timeoutPendingIntent?.cancel()

        EchoLocateLog.eLogV("Nr5g CMS Limit- Nr5gHourlyDelegate Alarm canceled")
    }

    override fun getTriggerDataList(): List<BaseNr5gTriggerData> {
        EchoLocateLog.eLogD("Not required for Nr5g hourly trigger")
        TODO("Not required for Nr5g hourly trigger")
    }

    override fun getLogcatListenerIds(): List<String> {
        EchoLocateLog.eLogD("Not required for Nr5g hourly trigger")
        TODO("Not required for Nr5g hourly trigger")
    }

    override fun getTimeoutAction(): String {
        EchoLocateLog.eLogD("Not required for Nr5g hourly trigger")
        TODO("Not required for Nr5g hourly trigger")
    }

    override fun getTimeoutRequestCode(): Int {
        EchoLocateLog.eLogD("Not required for Nr5g hourly trigger")
        TODO("Not required for Nr5g hourly trigger")
    }

    override fun getApplicationState(triggerCode: Int): ApplicationState? {
        return null
    }

    override fun getFocusGainCode(): Int {
        EchoLocateLog.eLogD("Not required for Nr5g hourly trigger")
        TODO("Not required for Nr5g hourly trigger")
    }

    override fun getFocusLossCode(): Int {
        EchoLocateLog.eLogD("Not required for Nr5g hourly trigger")
        TODO("Not required for Nr5g hourly trigger")
    }


}
