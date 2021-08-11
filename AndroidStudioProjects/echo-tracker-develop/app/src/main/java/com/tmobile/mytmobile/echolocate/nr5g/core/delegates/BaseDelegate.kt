package com.tmobile.mytmobile.echolocate.nr5g.core.delegates

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import android.text.TextUtils
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.tmobile.mytmobile.echolocate.nr5g.core.oemdata.Nr5gDataMetricsWrapper
import com.tmobile.mytmobile.echolocate.communicationbus.rxbus.PostTicket
import com.tmobile.mytmobile.echolocate.communicationbus.rxbus.RxBus
import com.tmobile.mytmobile.echolocate.communicationbus.rxbus.SubscribeTicket
import com.tmobile.mytmobile.echolocate.nr5g.core.events.BaseDelegateListenerEvent
import com.tmobile.mytmobile.echolocate.nr5g.core.events.Nr5gTriggerLimitEvent
import com.tmobile.mytmobile.echolocate.nr5g.core.utils.*
import com.tmobile.mytmobile.echolocate.nr5g.manager.INr5gIntentHandler
import com.tmobile.mytmobile.echolocate.nr5g.nsa5g.database.entity.BaseEchoLocateNr5gEntity
import com.tmobile.mytmobile.echolocate.nr5g.nsa5g.database.entity.Nr5gTriggerEntity
import com.tmobile.mytmobile.echolocate.nr5g.nsa5g.datacollector.Nsa5gDataCollector
import com.tmobile.mytmobile.echolocate.nr5g.nsa5g.model.BaseNr5gTriggerData
import com.tmobile.mytmobile.echolocate.nr5g.sa5g.datacollector.Sa5gDataCollector
import com.tmobile.mytmobile.echolocate.utils.EchoLocateDateUtils
import com.tmobile.mytmobile.echolocate.utils.EchoLocateLog
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.*


/**
 * This class is responsible to provide the implementation logic for all delegates
 * This provides base functions to process the states and actions of a respective delegate
 * and invokes and logcat listener
 */
abstract class BaseDelegate(val context: Context) {

    private var periodicDisposable: Disposable? = null
    internal val bus = RxBus.instance
    private val timeOutIDs: ArrayList<Int> = ArrayList()
    var packageName: String? = ""
    var nr5gDataMetricsWrapper =
        Nr5gDataMetricsWrapper(
            context
        )
    var launchTriggerTimestamp: Long? = 0
    var nr5gHandler: INr5gIntentHandler? = null

    var nsa5gDataCollector: Nsa5gDataCollector? = null
    var sa5gDataCollector: Sa5gDataCollector? = null

    companion object {

        private const val SCREEN_ON_CODE = 300
        const val NR5G_HOURLY_TRIGGER_CODE = 100
        const val NR5G_TRIGGER_PERIODIC_10 = 210
        const val NR5G_TRIGGER_PERIODIC_20 = 220
        const val NR5G_TRIGGER_PERIODIC_30 = 230

        const val NR5G_TEN_SECONDS_ACTION = "NR5G_TEN_SECONDS_ACTION"
        const val NR5G_THIRTY_SECONDS_ACTION = "NR5G_THIRTY_SECONDS_ACTION"
        const val NR5G_SIXTY_SECONDS_ACTION = "NR5G_SIXTY_SECONDS_ACTION"

        const val TEN_SECONDS = 10000L
        const val TWENTY_SECONDS = 30000L
        const val THIRTY_SECONDS = 60000L

    }

    private var nr5gApplicationTrigger: Nr5gApplicationTrigger =
        Nr5gApplicationTrigger.getInstance(context)
    private var nr5gScreenTrigger: Nr5gScreenTrigger = Nr5gScreenTrigger.getInstance(context)

    fun store5gEntity(triggerCode: Int, isCountWithinLimit: Boolean) {

        nsa5gDataCollector = Nsa5gDataCollector(context)
        sa5gDataCollector = Sa5gDataCollector(context)

        if (triggerCode == SCREEN_ON_CODE) {
            val screenCount = nr5gScreenTrigger.getScreenTriggerCount()
            EchoLocateLog.eLogV("Nr5g CMS Screen Trigger Limit-base delegate -  before increase: $screenCount")
            nr5gScreenTrigger.increaseScreenTriggerCount()

            val increasedCount = nr5gScreenTrigger.getScreenTriggerCount()
            EchoLocateLog.eLogV("Nr5g CMS Screen Trigger Limit-base delegate -  after increase: $increasedCount")
        } else {
            val appCount = nr5gApplicationTrigger.getAppTriggerCount()
            EchoLocateLog.eLogV("Nr5g CMS App Trigger Limit-base delegate -  before increase: $appCount")
            nr5gApplicationTrigger.increaseAppTriggerCount()

            val increasedCount = nr5gApplicationTrigger.getAppTriggerCount()
            EchoLocateLog.eLogV("Nr5g CMS App Trigger Limit-base delegate -  after increase: $increasedCount")
        }

        if (isCountWithinLimit) {
            nr5gHandler?.store5gEntity(
                triggerCode,
                isCountWithinLimit,
                packageName!!,
                (System.currentTimeMillis() - launchTriggerTimestamp!!).toInt()
            )


            if (nr5gApplicationTrigger.isAppTriggerLimitReached()) {
                EchoLocateLog.eLogD("Nr5g CMS Limit-app trigger limit reached so posting event")
                val postTicket = PostTicket(
                    Nr5gTriggerLimitEvent(
                        true,
                        Nr5gConstants.TRIGGER_LIMIT_TYPE_APP
                    )
                )
                RxBus.instance.post(postTicket)
                return
            }
            if (nr5gScreenTrigger.isScreenTriggerLimitReached()) {
                EchoLocateLog.eLogD("Nr5g CMS Limit-screen trigger limit reached so posting event")
                val postTicket = PostTicket(
                    Nr5gTriggerLimitEvent(
                        true,
                        Nr5gConstants.TRIGGER_LIMIT_TYPE_SCREEN
                    )
                )
                RxBus.instance.post(postTicket)
                return
            }
        }
    }

    /**
     *  Application focus gain or focus loss state listener
     */
    fun processApplicationState(state: ApplicationState) {
        synchronized(this) {
            EchoLocateLog.eLogE("ApplicationState.processApplicationState $state")
            when (state) {
                /**
                 * When the application focus gain this will called.
                 * Get's the respective focus gain code and store Nr5g entity into the database
                 */
                ApplicationState.FOCUS_GAIN -> {
                    stopScheduler()
                    EchoLocateLog.eLogE("ApplicationState.FOCUS_GAIN " + getFocusGainCode())
                    EchoLocateLog.eLogE("TriggerDataList " + getTriggerDataList())
                    if (nr5gApplicationTrigger.isAppTriggerLimitReached()) {
                        EchoLocateLog.eLogV("Nr5g CMS Limit - app trigger limit reached")
                        return
                    }
                    store5gEntity(
                        getFocusGainCode(),
                        nr5gApplicationTrigger.isAppCountWithinLimit()
                    )
                    listenDelegateActionsFromLogCatListener()
                    startScheduler()
                }
                /**
                 * When the application focus loss this will called.
                 * Get's the respective focus loss code and store Nr5g entity into the database
                 */
                ApplicationState.FOCUS_LOSS -> {
                    EchoLocateLog.eLogD("ApplicationState.FOCUS_LOSS " + getFocusLossCode())
//                storeNr5gEntity(getFocusLossCode(), nr5gApplicationTrigger.isAppCountWithinLimit())
                    stopScheduler()
                }
                else -> {
                    EchoLocateLog.eLogE("ApplicationState do nothing")
                }
            }
        }
    }

    /**
     * This function will check the actions received from broadcast receiver
     * and stores the trigger code into entity
     */
    @SuppressLint("CheckResult")
    fun listenDelegateActionsFromLogCatListener() {
        val subscribeTicket = SubscribeTicket(RxBus.SubjectType.PUBLISH_SUBJECT)
        periodicDisposable?.dispose()
        periodicDisposable = bus.register<BaseDelegateListenerEvent>(subscribeTicket)
            .observeOn(Schedulers.io())
            .subscribeOn(Schedulers.io())
            .subscribe {
                if (!nr5gApplicationTrigger.isAppTriggerLimitReached()) {
                    launchTriggerTimestamp = it.intent.getLongExtra(
                        Nr5gIntents.TRIGGER_TIMESTAMP,
                        System.currentTimeMillis()
                    )
                    getTriggerDataList().forEach { triggerDataBase: BaseNr5gTriggerData ->
                        if (triggerDataBase.triggerAction == it.intent.action)
                            store5gEntity(
                                triggerDataBase.triggerCode,
                                nr5gApplicationTrigger.isAppCountWithinLimit()
                            )
                    }
                }
            }
    }

    /**
     * Returns base echo locate Nr5g entity with current system time
     * @param triggerCode: trigger code of the specific app state
     * */
    private fun prepareEchoLocateNr5gEntity(
        triggerCode: Int,
        sessionId: String
    ): BaseEchoLocateNr5gEntity {
        return BaseEchoLocateNr5gEntity(
            triggerCode,
            "", // While creating the record, the status will be empty (addressed DIA-6523)
            EchoLocateDateUtils.getTriggerTimeStamp(),
            sessionId
        )
    }

    /**
     * Returns Nr5gTriggerEntity with current system time
     * */
    private fun prepareNr5gTriggerEntity(
        timestamp: String,
        triggerId: Int,
        triggerApp: String,
        triggerDelay: Int
    ): Nr5gTriggerEntity {
        return Nr5gTriggerEntity(
            timestamp,
            triggerId,
            triggerApp,
            triggerDelay
        )
    }

    /**
     * This function prepares the intent from timeout action value
     * and intent will be passed to alarm manager
     */
    fun prepareTimeoutIntent(action: String): Intent {
        return Intent(action)
    }

    /**
     * This function is responsible to run the scheduler task with delay
     * and then calls the function storeNr5gEntity,
     * this function will store the respective trigger code in to an entity
     */
    @Synchronized
    private fun startScheduler() {
        try {
            stopAlarmManagerInstance()
            for (item in getTriggerDataList()) {
                createAlarmManagerInstance(
                    item.triggerDelay,
                    item.triggerAction,
                    item.triggerCode
                )
            }
        } catch (ex: Exception) {
            EchoLocateLog.eLogV("startScheduler() :: Exception while crating the Alarm:: ${ex.localizedMessage}")
            logCrashToFirebase("Exception while crating the Alarm in the 5G module", ex, "Crash occurred in startScheduler function of 5g module - BaseDelegate")
        }
    }

    /**
     * This function is responsible to stop all the scheduled tasks against each delegates
     */
    @Synchronized
    fun stopScheduler() {
        stopAlarmManagerInstance()
        //allowing the disposable to register on the next event, thus making it null
        periodicDisposable?.run {
            bus.unregister(this)
        }
    }

    /**
     * This function is responsible to create the alarm instance with
     * @param triggerDelay which provides the trigger delay
     * @param triggerAction which provides the action to perform
     * @param triggerCode which provides the trigger to identify the action performed and stores in to db
     */
    private fun createAlarmManagerInstance(
        triggerDelay: Long,
        triggerAction: String,
        triggerCode: Int
    ) {
        val intent = Intent(context, Nr5GAlarmReceiver::class.java)
        intent.action = triggerAction
        intent.putExtra(Nr5gIntents.APP_PACKAGE, packageName)
        intent.putExtra(Nr5gIntents.TRIGGER_TIMESTAMP, System.currentTimeMillis())
        val timeoutPendingIntent = PendingIntent.getBroadcast(
            context,
            triggerCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        getAlarmManager()?.setExact(
            AlarmManager.ELAPSED_REALTIME_WAKEUP,
            SystemClock.elapsedRealtime() + triggerDelay,
            timeoutPendingIntent!!
        )

        timeOutIDs.add(triggerCode)
        EchoLocateLog.eLogV("Alarm Scheduled with triggerDelay:: $triggerDelay, triggerAction:: $triggerAction, triggerCode$triggerCode, timeOutIDs list size $timeOutIDs")
    }

    /**
     * This function is responsible to stop all the scheduled tasks
     */
    @Synchronized
    private fun stopAlarmManagerInstance() {
        if (timeOutIDs.isNotEmpty()) {
            try {
                for (index in timeOutIDs.indices) {

                    val intent = Intent(context, Nr5GAlarmReceiver::class.java)
                    val triggerAction = getTriggerDataList()[index].triggerAction
                    val triggerCode = timeOutIDs[index]
                    intent.action = triggerAction

                    val timeoutPendingIntent = PendingIntent.getBroadcast(
                        context, triggerCode, intent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                    )

                    getAlarmManager()?.cancel(
                        timeoutPendingIntent
                    )
                    EchoLocateLog.eLogV("stopAlarmManagerInstance() :: Index $index, triggerCode :: $triggerCode, triggerAction :: $triggerAction")
                }
            } catch (exception: IndexOutOfBoundsException) {
                EchoLocateLog.eLogE("IndexOutOfBoundsException in BaseDelegate 5G module exception : ${exception.localizedMessage}")
                logCrashToFirebase("IndexOutOfBoundsException in BaseDelegate 5G module exception", exception, "Crash occurred in stopAlarmManagerInstance function of 5g module - BaseDelegate")
            }
        }
        timeOutIDs.clear()
        EchoLocateLog.eLogV(" All Nr5g Periodic Alarms Stopped ")
    }

    /**
     * This class is responsible to receive the periodic events
     * and send back to delegate which again will get the intent
     * through rx bus and calls the process intent method
     */
    class Nr5GAlarmReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            intent.putExtra(Nr5gIntents.TRIGGER_TIMESTAMP, System.currentTimeMillis())
            val postTicket = PostTicket(
                BaseDelegateListenerEvent(intent)
            )
            RxBus.instance.post(postTicket)
        }
    }

    /**
     * @return list of triggers
     */
    abstract fun getTriggerDataList(): List<BaseNr5gTriggerData>

    /**
     * @return logcat listeners ids
     */
    internal abstract fun getLogcatListenerIds(): List<String>

    /**
     * @return timeout intent action
     */
    internal abstract fun getTimeoutAction(): String

    /**
     * @return timeout intent request code
     */
    internal abstract fun getTimeoutRequestCode(): Int

    /**
     * @return nr5gTrigger application
     */
    internal abstract fun getApplicationState(triggerCode: Int): ApplicationState?

    /**
     * @return focus gain code
     */
    internal abstract fun getFocusGainCode(): Int

    /**
     * @return focus loss code
     */
    internal abstract fun getFocusLossCode(): Int

    /**
     * This function processes screen on/off event
     * @param intent: [Intent]
     */
    fun processIntent(intent: Intent) {
        val action = intent.action
        when {
            TextUtils.isEmpty(action) -> return
            else -> when (action) {
                Nr5gIntents.ACTION_SCREEN_OFF -> stopScheduler()
                Nr5gIntents.ACTION_SCREEN_ON -> {
                    launchTriggerTimestamp = intent.getLongExtra(
                        Nr5gIntents.TRIGGER_TIMESTAMP,
                        System.currentTimeMillis()
                    )
                    EchoLocateLog.eLogV("Nr5g CMS Limit - received screen on event")
                    store5gEntity(
                        SCREEN_ON_CODE, nr5gScreenTrigger.isScreenCountWithinLimit()
                    )
                }
            }
        }
    }

    /**
     * Returns the instance of the alarm manager
     */
    private fun getAlarmManager() : AlarmManager? {
        return context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    }

    /**
     * Log the crash to firebase crashlytics console with the custom message.
     */
   private fun logCrashToFirebase(logMessage: String, exception: Exception, throwableMessage: String) {
        val firebaseCrashlytics = FirebaseCrashlytics.getInstance()
        firebaseCrashlytics.log("$logMessage : ${exception.localizedMessage}")
        firebaseCrashlytics.recordException(Throwable(Exception(throwableMessage)))
        firebaseCrashlytics.sendUnsentReports()
    }
}