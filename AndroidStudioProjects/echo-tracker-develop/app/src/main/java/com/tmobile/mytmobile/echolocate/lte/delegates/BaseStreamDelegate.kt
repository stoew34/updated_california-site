package com.tmobile.mytmobile.echolocate.lte.delegates

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_SCREEN_OFF
import android.os.SystemClock
import android.text.TextUtils
import android.text.format.DateUtils
import androidx.annotation.VisibleForTesting
import com.tmobile.mytmobile.echolocate.communicationbus.rxbus.PostTicket
import com.tmobile.mytmobile.echolocate.communicationbus.rxbus.RxBus
import com.tmobile.mytmobile.echolocate.communicationbus.rxbus.SubscribeTicket
import com.tmobile.mytmobile.echolocate.lte.lteevents.ApplicationTriggerLimitEvent
import com.tmobile.mytmobile.echolocate.lte.lteevents.LogcatListenerEvent
import com.tmobile.mytmobile.echolocate.lte.utils.ApplicationState
import com.tmobile.mytmobile.echolocate.lte.utils.ApplicationState.FOCUS_GAIN
import com.tmobile.mytmobile.echolocate.lte.utils.ApplicationState.FOCUS_LOSS
import com.tmobile.mytmobile.echolocate.lte.utils.ApplicationTrigger
import com.tmobile.mytmobile.echolocate.lte.utils.LogcatlistenerItem
import com.tmobile.mytmobile.echolocate.lte.utils.LteConstants
import com.tmobile.mytmobile.echolocate.utils.EchoLocateLog
import com.tmobile.mytmobile.echolocate.lte.utils.logcat.LogcatListener
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlin.random.Random

/**
 * This class responsible to provide the base implementation for all the stream delegates
 * It provides the functions for focus states and related actions for a respective steam delegate
 */

abstract class BaseStreamDelegate(context: Context) : BaseDelegate(context) {

    /**
     * This value will be provided to timer class when the streaming is started and reported in 10 seconds
     */
    private val TEN_SECONDS = 10 * DateUtils.SECOND_IN_MILLIS
    /**
     * This value will be provided to timer class when the streaming is started and reported in 30 seconds
     */
    private val THIRTY_SECONDS = 30 * DateUtils.SECOND_IN_MILLIS
    /**
     * This value will be provided to timer class when the streaming is started and reported in 60 seconds
     */
    private val SIXTY_SECONDS = 60 * DateUtils.SECOND_IN_MILLIS
    /**
     * This value will be provided to timer class when the streaming is started and reported in 300 seconds
     */
    private val THREE_HUNDRED_SECONDS = 300 * DateUtils.SECOND_IN_MILLIS
    /**
     * This value will be provided to timer class when the streaming is started and reported in 600 seconds
     */
    private val SIX_HUNDRED_SECONDS = 600 * DateUtils.SECOND_IN_MILLIS
    /**
     * This value will be provided to timer class when the streaming is started and reported in 1800 seconds
     */
    private val EIGHTEEN_HUNDRED_SECONDS = 1800 * DateUtils.SECOND_IN_MILLIS

    var stateFocused = false

    private var alarmMgr: AlarmManager? = null

    var logcatEventDisposable: Disposable? = null

    private var appTriggerLimitDisposable: Disposable? =null

    private var screenOffDisposable: Disposable? = null

    protected var applicationTrigger: ApplicationTrigger = ApplicationTrigger.getInstance(context)


    /*
    Time out ids will be give to alarm manager as a request code and will be saved in arraylist
    and used for canceling the alarm manager
     */
    private var streamTimeoutId: Int = 0

    @VisibleForTesting
    val timeOutIDList: ArrayList<Int> = ArrayList()

    private val periodicList: ArrayList<Int> = ArrayList()

    val STREAMING_TEN_SECONDS_ACTION = "STREAMING_TEN_SECONDS_ACTION"
    val STREAMING_THIRTY_SECONDS_ACTION = "STREAMING_THIRTY_SECONDS_ACTION"
    val STREAMING_SIXTY_SECONDS_ACTION = "STREAMING_SIXTY_SECONDS_ACTION"
    val STREAMING_THREE_HUNDRED_SECONDS_ACTION = "STREAMING_THREE_HUNDRED_SECONDS_ACTION"
    val STREAMING_SIX_HUNDRED_SECONDS_ACTION = "STREAMING_SIX_HUNDRED_SECONDS_ACTION"
    val STREAMING_EIGHTEEN_HUNDRED_SECONDS_ACTION = "STREAMING_EIGHTEEN_HUNDRED_SECONDS_ACTION"

    /**
     * This function is used to generate the random number and will be given to broadcast receiver as a request code
     */
    private fun getRandomNumber(): Int {
        return Random.nextInt(0, 100)
    }

    private val periodicActions = listOf(
        "STREAMING_TEN_SECONDS_ACTION",
        "STREAMING_THIRTY_SECONDS_ACTION",
        "STREAMING_SIXTY_SECONDS_ACTION",
        "STREAMING_THREE_HUNDRED_SECONDS_ACTION",
        "STREAMING_SIX_HUNDRED_SECONDS_ACTION",
        "STREAMING_EIGHTEEN_HUNDRED_SECONDS_ACTION"
    )

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
     * @return streaming start action
     */
    internal abstract fun getStreamingStartAction(): String

    /**
     * @return streaming start trigger id
     */
    internal abstract fun getStreamingStartTriggerId(): String

    /**
     * @return streaming start regex
     */
    internal abstract fun getStreamingStartRegex(): List<String>

    /**
     * @return streaming start code
     */
    internal abstract fun getStreamingStartCode(): Int

    /**
     * @return streaming end action
     */
    internal abstract fun getStreamingEndAction(): String

    /**
     * @return streaming end trigger id
     */
    internal abstract fun getStreamingEndTriggerId(): String

    /**
     * @return streaming end regex
     */
    internal abstract fun getStreamingEndRegex(): List<String>

    /**
     * @return streaming end code
     */
    internal abstract fun getStreamingEndCode(): Int

    /*
    This provides 10 seconds code
     */
    internal abstract fun getTenSecondsCode(): Int

    /*
       This provides 30 seconds code
        */
    internal abstract fun getThirtySecondsCode(): Int

    /*
       This provides 60 seconds code
        */
    internal abstract fun getSixtyecondsCode(): Int
    /*
   This provides 300 seconds code
    */

    internal abstract fun getThreeHundreSecondsCode(): Int
    /*
   This provides 600 seconds code
    */

    internal abstract fun getSixHundredSecondsCode(): Int
    /*
   This provides 1800 seconds code
    */
    internal abstract fun getEighteenHundredCode(): Int

    /**
     * @param intent it provides data from current event
     * @return content id extracted from intent extra
     */
    internal abstract fun extractContentIdFromIntent(intent: Intent): String

    /**
     * @param intent it provides data from current event
     * @return link extracted from intent extra
     */
    internal abstract fun extractLinkFromLog(intent: Intent): String

    /**
     * This function will check the actions received from logcat listener
     * and checks with the respective delegate action, if actions are available process intent will be called.
     */
    @SuppressLint("CheckResult")
    protected fun listenDelegateActionsFromLogCatListener() {
        val subscribeTicket = SubscribeTicket(RxBus.SubjectType.PUBLISH_SUBJECT)
        logcatEventDisposable?.dispose()
        logcatEventDisposable =
            RxBus.instance.register<LogcatListenerEvent>(subscribeTicket).subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io()).subscribe {
                    EchoLocateLog.eLogV("Diagnostic :  CMS Limit stream delegate - rx bus action received " + it.intent.action!!)
                    when {
                        YoutubeDelegate(context).ACTIONS.contains(it.intent.action!!) -> handleIntent(it.intent)

                        NetflixDelegate(context).ACTIONS.contains(it.intent.action!!) -> handleIntent(it.intent)

                        periodicActions.contains(it.intent.action!!) -> storePeriodicData(it.intent.action!!)
                    }
                }
    }

    /**
     * This Function handles the focus states [FOCUS_GAIN] and [FOCUS_LOSS] of an application received from lte manager
     */
    override fun processApplicationState(state: ApplicationState) {
        EchoLocateLog.eLogV("Diagnostic : ApplicationState $state")
        listenApplicationTriggerLimitReachedEvent()
        when (state) {
            FOCUS_GAIN -> {
                EchoLocateLog.eLogD("LTE Stream ApplicationState FOCUS_GAIN: Code ${getFocusGainCode()} Application: ${getTriggerApplication()}")
                EchoLocateLog.eLogV("Diagnostic : CMS Limit-stream delegate - Received Focus gain")

                        stateFocused = true

                        // Collect the LTE data for FOCUS_GAIN of the application
                        storeLteEntity(getFocusGainCode())
                        // Remove any previous listeners
                        removeLogcatListeners()
                        if (applicationTrigger.isTriggerLimitReached()) {
                            EchoLocateLog.eLogV("Diagnostic : CMS Limit-stream delegate - focus gain returning as limit reached")
                            return
                        }

                        //Sa5gLogs.eLogV("CMS Limit-stream delegate - listening to events as trigger count dint exceed")
                        // Start listening to internal events generated by the logcat mechanism of EL App.
                        listenDelegateActionsFromLogCatListener()

                        // Start logcat listener for stream start Regex.
                        startStreamingListener(
                            getStreamingStartTriggerId(),
                            LogcatListener.Type.CONTINUOUS,
                            getStreamingStartAction(),
                            getStreamingStartRegex()
                        )

                        // Schedule a timeout. If the application is launched but streaming is not started within the timeout time, then cancel the logcat listeners.
                        scheduleTimeoutAction(TIMEOUT_TEN_MINUTES)
                }
                FOCUS_LOSS -> {
                    stateFocused = false
                    EchoLocateLog.eLogD("LTE Stream ApplicationState FOCUS_LOSS: Code ${getFocusLossCode()} Application: ${getTriggerApplication()}")
                    EchoLocateLog.eLogV("Diagnostic : CMS Limit-stream delegate - Received Focus Loss")
                    // Collect the LTE data for FOCUS_LOSS of the application
                    storeLteEntity(getFocusLossCode())

                    cancelTimeoutsAndLogcatListeners(listOf(getStreamingEndTriggerId()))

                    if (applicationTrigger.isTriggerLimitReached()) {
                        EchoLocateLog.eLogV("Diagnostic : CMS Limit-stream delegate -  focus loss returning as limit reached")
                        return
                    }

                    // Sometimes end streaming appeared in logs after FOCUS_LOSS when logcat listener
                    // is dead. So If we want receive x80_xxx code always after FOCUS_LOSS then this
                    // workaround must be implemented.
                    scheduleTimeoutAction(TIMEOUT_FIVE_SECONDS)
                }
                else -> EchoLocateLog.eLogD("Diagnostic : Application state $state is not recognized by EL App")
            }
    }

    /**
     * This functions process the intents which received through rxbus such [YoutubeDelegate.STREAMING_START_ACTION] and [NetflixDelegate.STREAMING_START_ACTION] and lte manager
     */
    override fun handleIntent(intent: Intent?) {
            EchoLocateLog.eLogV("Diagnostic : CMS Limit-----handle intent called" + intent?.action)
            when {
                TextUtils.isEmpty(intent?.action) -> EchoLocateLog.eLogV("Diagnostic : Empty intent")

                ACTION_SCREEN_OFF == intent?.action -> if (stateFocused) {
                    listenApplicationTriggerLimitReachedEvent()
                    processScreenOffEvent()
                }

                getStreamingStartAction() == intent?.action ->
                    processStreamingStartAction(intent)

                getStreamingEndAction() == intent?.action ->
                    processStreamingEndAction(intent)

                getTimeoutAction() == intent?.action -> {
                    cancelAll()
                }
            }
    }

    protected fun startStreamingListener(
        triggerId: String,
        type: LogcatListener.Type,
        action: String,
        regex: List<String>
    ) {
        logcatListener?.addListener(
            LogcatlistenerItem(
                triggerId,
                action,
                regex,
                type
            )
        )
        logcatListener?.start()
    }

    /**
     * This function handles all the timeouts actions such as ten minutes and seventy minutes etc..
     * It schedules the the timer of particular action and sends those action based on the intent state
     *
     * Please remember, this function cancels all the previous one time timeout alarms and then schedules the new one.
     */
    protected fun scheduleTimeoutAction(delay: Long) {
        cancelTimeout()
        EchoLocateLog.eLogV("CMS Limit-stream delegate -  Scheduling timeout action as limit dint reach")
        createAlarmManagerInstance(delay, getTimeoutAction(), false)
    }

    /**
     * This function is used to create the alarm instance for timeout actions and periodic actions
     */
    private fun createAlarmManagerInstance(delay: Long, action: String, isPeriodic: Boolean) {
        val intent = Intent(context, MyAlarmReceiver::class.java)
        intent.action = action
        streamTimeoutId = getRandomNumber()
        val timeoutPendingIntent = PendingIntent.getBroadcast(
            context, streamTimeoutId, intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        alarmMgr = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmMgr?.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + delay, timeoutPendingIntent!!)
        if (isPeriodic) {
            addOrClearPeriodicList(streamTimeoutId, true)
        } else {
            addOrClearTimeOutList(streamTimeoutId, true)
        }
    }

    /**
     * This function is used to store the trigger code of a particular time intervals in to the entity
     */
    private fun storePeriodicData(action: String) {
        when {
            STREAMING_TEN_SECONDS_ACTION == action -> storeLteEntity(getTenSecondsCode())

            STREAMING_THIRTY_SECONDS_ACTION == action -> storeLteEntity(getThirtySecondsCode())

            STREAMING_SIXTY_SECONDS_ACTION == action -> storeLteEntity(getSixtyecondsCode())

            STREAMING_THREE_HUNDRED_SECONDS_ACTION == action -> storeLteEntity(getThreeHundreSecondsCode())

            STREAMING_SIX_HUNDRED_SECONDS_ACTION == action -> storeLteEntity(getSixHundredSecondsCode())

            STREAMING_EIGHTEEN_HUNDRED_SECONDS_ACTION == action -> storeLteEntity(getEighteenHundredCode())
        }
    }

    /**
     * This function is responsible to schedule the timer based on the time intervals
     * and stores the event codes of each delegate in to an entity
     */
    private fun schedulePeriodicData() {
        stopPeriodicActions()
        schedulePeriodicAction(TEN_SECONDS, STREAMING_TEN_SECONDS_ACTION)
        schedulePeriodicAction(THIRTY_SECONDS, STREAMING_THIRTY_SECONDS_ACTION)
        schedulePeriodicAction(SIXTY_SECONDS, STREAMING_SIXTY_SECONDS_ACTION)
        schedulePeriodicAction(THREE_HUNDRED_SECONDS, STREAMING_THREE_HUNDRED_SECONDS_ACTION)
        schedulePeriodicAction(SIX_HUNDRED_SECONDS, STREAMING_SIX_HUNDRED_SECONDS_ACTION)
        schedulePeriodicAction(EIGHTEEN_HUNDRED_SECONDS, STREAMING_EIGHTEEN_HUNDRED_SECONDS_ACTION)
    }

    /**
     * This function is used to provide the action and the delay
     * and creates forwards the data to createAlarmManagerInstance function to create the alarm instance
     */
    private fun schedulePeriodicAction(delay: Long, action: String) {
        EchoLocateLog.eLogV("Diagnostic : CMS Limit-stream delegate - increasing count on schedule periodic action: $delay")
        createAlarmManagerInstance(delay, action, true)
    }

    /**
     * This function will get the timeout id's from a list and removes the id's and cancels  alarm manager
     * and clears the data from timeout list
     */
    @Synchronized
    private fun cancelTimeout() {
        if (timeOutIDList.isNotEmpty()) {
            try {
                timeOutIDList.forEach { timeOutID ->
                    val intent = Intent(context, MyAlarmReceiver::class.java)
                    intent.action = getTimeoutAction()
                    val timeoutPendingIntent = PendingIntent.getBroadcast(
                        context, timeOutID, intent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                    )
                    alarmMgr?.cancel(
                        timeoutPendingIntent
                    )
                }
            } catch (e: IndexOutOfBoundsException) {
                EchoLocateLog.eLogE("IndexOutOfBoundsException in BaseStreamDelegate's cancelTimeout ")
            }
        }
        addOrClearTimeOutList(add = false)
    }

    /**
     * This function is responsible to stop all periodic triggers
     */
    @Synchronized
    fun stopPeriodicActions() {
        if (periodicList.isNotEmpty()) {
            EchoLocateLog.eLogD("periodicList -> ${periodicList.size}")
            EchoLocateLog.eLogD("periodicActions -> ${periodicActions.size}")
            try {
                for (index in periodicList.indices){
                    val intent = Intent(context, MyAlarmReceiver::class.java)
                    intent.action = periodicActions[index]
                    val action = periodicList[index]

                    val timeoutPendingIntent = PendingIntent.getBroadcast(
                        context, action, intent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                    )

                    alarmMgr?.cancel(
                        timeoutPendingIntent
                    )
                }
            } catch (e: IndexOutOfBoundsException) {
                EchoLocateLog.eLogE("IndexOutOfBoundsException in BaseStreamDelegate's stopPeriodicActions ")
            }
            addOrClearPeriodicList(add = false)
        }

        EchoLocateLog.eLogE("Diagnostic : All periodic alarms stopped")
    }

    /**
     * To make sure we are clearing and adding the item into periodic list so that the events can happen
     * in synchronized way so we dont get concurrent modification exception
     */
    @Synchronized
    fun addOrClearPeriodicList(timeOutId: Int = -1, add: Boolean) {
        if (add) {
            periodicList.add(timeOutId)
        } else {
            periodicList.clear()
        }
    }

    /**
     * To make sure we are clearing and adding the item into timeOutID list so that the events can happen
     * in synchronized way so we dont get concurrent modification exception
     */
    @Synchronized
    fun addOrClearTimeOutList(timeOutId: Int = -1, add: Boolean) {
        if (add) {
            timeOutIDList.add(timeOutId)
        } else {
            timeOutIDList.clear()
        }
    }

    protected fun processScreenOffEvent() {
        EchoLocateLog.eLogV("CMS Limit-stream delegate -  increasing count on screen off event")
        cancelAll()
        storeLteEntity(getScreenOffCode())
    }

    /**
     * This function starts the listener with end action and end regex,then logcat listener will
     * send back the end action when the stream is ended and invokes the scheduleTimeoutAction()
     *
     * This function is used to schedule the timeout action if the video is not started in 10 minutes
     */
    protected fun processStreamingStartAction(intent: Intent) {
        storeLteEntity(getStreamingStartCode())

        // Schedule periodic data collection after stream start.

        EchoLocateLog.eLogV("Diagnostic : CMS Limit-stream delegate -  received start action")
        if (applicationTrigger.isTriggerLimitReached()) {
            EchoLocateLog.eLogV("Diagnostic : CMS Limit-stream delegate -  start action returning as limit reached")
            return
        }
        schedulePeriodicData()

        //storeLteEntity(getStreamingStartCode())

        startStreamingListener(
            getStreamingEndTriggerId(),
            LogcatListener.Type.ONE_SHOT,
            getStreamingEndAction(),
            getStreamingEndRegex()
        )

        // After streaming start, if video is played for more than 70 minutes, stop collecting data and remove all listeners.
        // TODO: The timeout time should be returned by individual delegate and should not be hardcoded here.
        scheduleTimeoutAction(TIMEOUT_SEVENTY_MINUTES)
    }

    /**
     * This function will remove the listener and checks the focus state,if it is false
     * and invokes the scheduleTimeoutAction()
     * This function is used to schedule the timeout action if the video is not started in 10 minutes
     */
    protected fun processStreamingEndAction(intent: Intent) {
        EchoLocateLog.eLogV("CMS Limit-stream delegate -  increasing count on streaming end action")

        // Cancel everything except the start streaming action to detect next streaming start action
        cancelTimeoutsAndLogcatListeners(listOf(getStreamingStartTriggerId()))

        // Store data at the end of streaming.
        storeLteEntity(
            getStreamingEndCode(),
            extractContentIdFromIntent(intent),
            extractLinkFromLog(intent)
        )

        // Schedule a timeout to cancel logcat listeners if the next streaming content doesn't start.
        if (stateFocused) {
            scheduleTimeoutAction(TIMEOUT_TEN_MINUTES)
        }
    }

    /**
     * Stop all timeout, triggers and logcat listeners
     */
    protected fun cancelAll() {
        EchoLocateLog.eLogV("CMS Limit-base delegate in cancell all")
        cancelAll(emptyList())
    }

    private fun cancelAll(excludeIds: List<String>) {
        cancelTimeoutsAndLogcatListeners(excludeIds)

        // Cancel the logcat event listener
        logcatEventDisposable?.run {
            RxBus.instance.unregister(this)
        }
        RxBus.instance.unregister(appTriggerLimitDisposable)
        //allowing the disposable to register on the next event, thus making it null
        appTriggerLimitDisposable = null


    }

    /**
     * Stop all timeout
     *
     * Remove all logcat listeners
     *
     */
    protected fun cancelTimeoutsAndLogcatListeners(excludeIds: List<String>) {
        logcatListener?.shouldBeStoppedWhenEmpty()

        cancelTimeout()
        EchoLocateLog.eLogV("CMS Limit- ----stopping periodic actions from cancel timeout")
        stopPeriodicActions()

        removeLogcatListeners(excludeIds)
    }

    /**
     * Remove all logcat listeners without any exemptions
     */
    protected fun removeLogcatListeners() {
        removeLogcatListeners(emptyList())
    }

    /**
     * Remove logcat listeners except the excluded ones.
     *
     * @param excludeIds list of excluded ids
     */
     fun removeLogcatListeners(excludeIds: List<String>) {
        val logcatListenerIds = getLogcatListenerIds()

        if (logcatListenerIds.isEmpty()) {
            EchoLocateLog.eLogD("Logcat lister Ids are empty, no need to remove listeners", System.currentTimeMillis())
            return
        }

        var excludeIdsSafe: List<String>? = excludeIds
        if (excludeIdsSafe == null) {
            excludeIdsSafe = emptyList()
        }

        for (id in logcatListenerIds) {
            // Skip the excluded ones
            if (excludeIdsSafe.contains(id)) {
                continue
            }
            logcatListener?.removeListener(id)
        }
    }


    /**
     * This class is responsible to receive the timeout events and send back to delegate
     * which again will get the intent through rx bus and calls the process intent method
     */
    class MyAlarmReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            EchoLocateLog.eLogV("CMS Limit-stream delegate -  posting action: ${intent.action}")
            // TODO: Maybe this should send another event and not a logcat event
            val postTicket = PostTicket(LogcatListenerEvent(intent))
            RxBus.instance.post(postTicket)
        }
    }


    /**
     * Get log line from the intent
     * @param intent: LogcatListener's intent data
     */
    internal fun getLogLine(intent: Intent): String {
        return if (!intent.hasExtra(LogcatListener.LINE_EXTRA)) {
            LteConstants.EMPTY
        } else intent.getStringExtra(LogcatListener.LINE_EXTRA)!!
    }

    /**
     * Subscribes to application trigger limit reached event
     */
    @SuppressLint("CheckResult")
    protected fun listenApplicationTriggerLimitReachedEvent() {
        val subscribeTicket = SubscribeTicket(RxBus.SubjectType.PUBLISH_SUBJECT)
        if(appTriggerLimitDisposable == null) {
            EchoLocateLog.eLogV("CMS Limit-stream  delegate subscribing as instance is null")
            appTriggerLimitDisposable =
                RxBus.instance.register<ApplicationTriggerLimitEvent>(subscribeTicket).subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.io()).subscribe {
                        EchoLocateLog.eLogV("CMS Limit-stream delegate -  rx bus action - cancelling all")
                        cancelAll()
                    }
        }else{
            EchoLocateLog.eLogV("CMS Limit-stream delegate appTriggerLimitDisposable is not null")
        }
    }
}
