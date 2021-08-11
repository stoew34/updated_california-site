package com.tmobile.mytmobile.echolocate.lte.delegates

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_SCREEN_OFF
import android.text.TextUtils
import com.tmobile.mytmobile.echolocate.communicationbus.rxbus.RxBus
import com.tmobile.mytmobile.echolocate.communicationbus.rxbus.SubscribeTicket
import com.tmobile.mytmobile.echolocate.configuration.model.LTE
import com.tmobile.mytmobile.echolocate.lte.lteevents.ApplicationTriggerLimitEvent
import com.tmobile.mytmobile.echolocate.lte.lteevents.LogcatListenerEvent
import com.tmobile.mytmobile.echolocate.lte.utils.ApplicationState
import com.tmobile.mytmobile.echolocate.lte.utils.ApplicationState.FOCUS_GAIN
import com.tmobile.mytmobile.echolocate.lte.utils.ApplicationState.FOCUS_LOSS
import com.tmobile.mytmobile.echolocate.lte.utils.ApplicationTrigger
import com.tmobile.mytmobile.echolocate.lte.utils.LTEApplications
import com.tmobile.mytmobile.echolocate.lte.utils.LogcatlistenerItem
import com.tmobile.mytmobile.echolocate.utils.EchoLocateLog
import com.tmobile.mytmobile.echolocate.utils.SingletonHolder
import com.tmobile.mytmobile.echolocate.lte.utils.logcat.LogcatListener
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.*

/**
 * Delegate for speed test application events.
 */
class SpeedTestDelegate(context: Context) : BaseDelegate(context) {

    companion object : SingletonHolder<SpeedTestDelegate, Context>(::SpeedTestDelegate)

    private var stateFocused = false
    /**
     * FOCUS_GAIN_CODE
     *
     * speed test focus gain code when application launch
     * value 901
     */
    private val FOCUS_GAIN_CODE = 901


    /**
     * READY_TO_TEST_CODE
     *
     * speed test server was selected. Begin test button appears.
     * value 910
     */
    private val READY_TO_TEST_CODE = 910

    /**
     * SPEEDTEST_READY_TO_TEST_TRIGGER_ID
     *
     * ready to test trigger id logcat listener id.
     * value SPEEDTEST_READY_TO_TEST_TRIGGER_ID
     */
    private val READY_TO_TEST_TRIGGER_ID = "SPEEDTEST_READY_TO_TEST_TRIGGER_ID"

    /**
     * READY_TO_TEST_REGEX
     *
     * Regex for filter ready to test in logcat listener speed test application
     */
    lateinit var regexReadyToTest: List<String>

    /**
     * READY_TO_TEST_FOUND_ACTION
     *
     * ready to test logcat listener result action
     * value SPEEDTEST_READY_TO_TEST_FOUND_ACTION
     */
    private val READY_TO_TEST_FOUND_ACTION = "SPEEDTEST_READY_TO_TEST_FOUND_ACTION"


    /**
     * TEST_START_CODE
     *
     * speed test latency test starts. Testing ping label appears.
     * value 920
     */
    private val TEST_START_CODE = 920

    /**
     * TEST_START_TRIGGER_ID
     *
     * test start logcat listener id.
     * value TEST_START_TRIGGER_ID
     */
    private val TEST_START_TRIGGER_ID = "TEST_START_TRIGGER_ID"

    /**
     * TEST_START_REGEX
     *
     * Regex for filter test start in logcat listener speed test application
     */
    lateinit var regexStart: List<String>

    /**
     * TEST_START_FOUND_ACTION
     *
     * ready to test logcat listener result action
     * value SPEEDTEST_TEST_START_FOUND_ACTION
     */
    private val TEST_START_FOUND_ACTION = "SPEEDTEST_TEST_START_FOUND_ACTION"


    /**
     * DOWNLOAD_TEST_START_CODE
     *
     * speed test download test starts
     * value 930
     */
    private val DOWNLOAD_TEST_START_CODE = 930
    /**
     * DOWNLOAD_TEST_START_TRIGGER_ID
     *
     * download test start logcat listener id.
     * value SPEEDTEST_DOWNLOAD_TEST_START_TRIGGER_ID
     */
    private val DOWNLOAD_TEST_START_TRIGGER_ID = "SPEEDTEST_DOWNLOAD_TEST_START_TRIGGER_ID"

    /**
     * DOWNLOAD_TEST_START_REGEX
     *
     * Regex for filter download test in logcat listener speed test application
     */
    lateinit var regexDownload: List<String>

    /**
     * DOWNLOAD_TEST_START_FOUND_ACTION
     *
     * download test logcat listener result action
     * value SPEEDTEST_DOWNLOAD_TEST_START_FOUND_ACTION
     */
    private val DOWNLOAD_TEST_START_FOUND_ACTION =
        "SPEEDTEST_DOWNLOAD_TEST_START_FOUND_ACTION"


    /**
     * UPLOAD_TEST_START_CODE
     *
     * speed test upload test starts code
     * value 940
     */
    private val UPLOAD_TEST_START_CODE = 940
    /**
     * UPLOAD_TEST_START_TRIGGER_ID
     *
     * upload test start logcat listener id.
     * value SPEEDTEST_UPLOAD_TEST_START_TRIGGER_ID
     */
    private val UPLOAD_TEST_START_TRIGGER_ID = "SPEEDTEST_UPLOAD_TEST_START_TRIGGER_ID"
    /**
     * UPLOAD_TEST_START_REGEX
     *
     * Regex for filter upload test in logcat listener speed test application
     */
    lateinit var regexUpload: List<String>

    /**
     * UPLOAD_TEST_START_FOUND_ACTION
     *
     * upload test logcat listener result action
     * value SPEEDTEST_UPLOAD_TEST_START_FOUND_ACTION
     */
    private val UPLOAD_TEST_START_FOUND_ACTION =
        "SPEEDTEST_UPLOAD_TEST_START_FOUND_ACTION"


    /**
     * TEST_END_CODE
     *
     * speed test results are ready code
     * value 950
     */
    private val TEST_END_CODE = 950
    /**
     * TEST_END_TRIGGER_ID
     *
     * test end logcat listener id.
     * value SPEEDTEST_TEST_END_TRIGGER_ID
     */
    private val TEST_END_TRIGGER_ID = "SPEEDTEST_TEST_END_TRIGGER_ID"
    /**
     * TEST_END_REGEX
     *
     * Regex for test end in logcat listener speed test application
     */
    lateinit var regexEnd: List<String>

    /**
     * TEST_END_FOUND_ACTION
     *
     * test end logcat listener result action
     * value SPEEDTEST_TEST_END_FOUND_ACTION
     */
    private val TEST_END_FOUND_ACTION = "SPEEDTEST_TEST_END_FOUND_ACTION"


    /**
     * TIMEOUT_ACTION
     *
     * time out logcat listener result action
     * value SPEEDTEST_TIMEOUT_ACTION
     */
    private val TIMEOUT_ACTION = "SPEEDTEST_TIMEOUT_ACTION"

    /**
     * TIMEOUT_REQUEST_CODE
     *
     * speed test time out request code
     * value 4643542
     */
    private val TIMEOUT_REQUEST_CODE = 4643542

    /**
     * FOCUS_LOSS_CODE
     *
     * speed test focus loss code
     * value 990
     */
    private val FOCUS_LOSS_CODE = 990
    /**
     * SCREEN_OFF_CODE
     *
     * speed test screen off code
     * value 995
     */
    private val SCREEN_OFF_CODE = 995

    /**
     * ACTIONS
     * List of actions , used to compare when we get callback from rx bus event.
     */
    private val ACTIONS = Arrays.asList(
        READY_TO_TEST_FOUND_ACTION, TEST_START_FOUND_ACTION, DOWNLOAD_TEST_START_FOUND_ACTION,
        UPLOAD_TEST_START_FOUND_ACTION, TEST_END_FOUND_ACTION
    )

    /**
     * Rx Bus instance used to listen logcat events.
     */
    private val bus = RxBus.instance
    /**
     * speed test delegate disposable used for un register rx bus events.
     */
    private var speedTestDelegateDisposable: Disposable? = null

    @SuppressLint("StaticFieldLeak")
    @Volatile
    private var INSTANCE: SpeedTestDelegate? = null

    private var applicationTrigger: ApplicationTrigger = ApplicationTrigger.getInstance(context)

    private var appTriggerLimitDisposable: Disposable? = null

    private var screenOffDisposable: Disposable? = null


    init {
        listenApplicationTriggerLimitReachedEvent()
    }

    /**
     * Singleton instance of a speed test delegate class
     */
    fun getInstance(context: Context): SpeedTestDelegate? {
        return INSTANCE ?: synchronized(this) {
            val instance = SpeedTestDelegate(context)
            INSTANCE = instance
            instance
        }
    }

    /**
     * Get triggered application.
     * Speed test delegate always return speed test package name
     */
    override fun getTriggerApplication(): LTEApplications {
        return LTEApplications.SPEED_TEST
    }


    /**
     *  Application focus gain or focus loss state listener
     */
    override fun processApplicationState(state: ApplicationState) {
        when (state) {
            /**
             * When the speed test application focus gain this will be called.
             * Get's the respective focus gain code and store lte entity into the database
             */
            FOCUS_GAIN -> {
                EchoLocateLog.eLogV("CMS Limit-SpeedTest - ApplicationState.FOCUS_GAIN $FOCUS_GAIN_CODE")

                EchoLocateLog.eLogE("Diagnostic : ApplicationState.FOCUS_GAIN $FOCUS_GAIN_CODE")
                stateFocused = true
                // Collect the LTE data for FOCUS_GAIN of the application
                storeLteEntity(FOCUS_GAIN_CODE)

                // Remove any previous listeners
                removeListeners(emptyList())

                if (applicationTrigger.isTriggerLimitReached()) {
                    EchoLocateLog.eLogV("CMS Limit-speedtest delegate -  focus gain returning as limit reached")
                    return
                }
                // Start listening to internal events generated by the logcat mechanism of EL App.
                listenDelegateActionsFromLogCatListener()

                // Add regex and events to listen to for Speed test delegate
                addLogcatListenerEvent(
                    LogcatlistenerItem(
                        READY_TO_TEST_TRIGGER_ID, READY_TO_TEST_FOUND_ACTION, regexReadyToTest,
                        LogcatListener.Type.ONE_SHOT
                    )
                )

                addLogcatListenerEvent(
                    LogcatlistenerItem(
                        TEST_START_TRIGGER_ID, TEST_START_FOUND_ACTION, regexStart,
                        LogcatListener.Type.ONE_SHOT
                    )
                )
                logcatListener?.shouldBeAliveWhenEmpty()
                logcatListener?.start()
            }
            /**
             * When the speed test application focus loss this will be called.
             * Get's the respective focus loss code and store lte entity into the database
             */
            FOCUS_LOSS -> {
                EchoLocateLog.eLogE("CMS Limit-ApplicationState.FOCUS_LOSS $FOCUS_LOSS_CODE")
                stateFocused = false

                // Collect the LTE data for FOCUS_LOSS of the application
                storeLteEntity(FOCUS_LOSS_CODE)

                cancelTimeoutsAndLogcatListeners()

                speedTestDelegateDisposable?.run {
                    bus.unregister(this)
                    bus.unregister(screenOffDisposable)
                }
            }
            else -> EchoLocateLog.eLogD("Application state $state is not recognized by EL App")
        }
    }

    /**
     * This method will add the actions to the logcat listener
     */
    private fun addLogcatListenerEvent(logcatListenerItem: LogcatlistenerItem) {
        logcatListener?.addListener(logcatListenerItem)
    }


    /**
     * Stop all timeout and logcat listeners
     */
    private fun cancelTimeoutsAndLogcatListeners() {
        logcatListener?.shouldBeStoppedWhenEmpty()
        removeListeners(emptyList())
    }

    /**
     * Remove logcat listeners
     * @param excludeIds list of excluded ids
     */
    private fun removeListeners(excludeIds: List<String>) {
        var excludeIdsSafe: List<String>? = excludeIds
        if (excludeIdsSafe == null) {
            excludeIdsSafe = emptyList()
        }
        for (id in getLogcatListenerIds()) {
            if (excludeIdsSafe.contains(id)) {
                continue
            }
            logcatListener?.removeListener(id)
        }
    }


    /**
     *  This function will check the actions received from logcat listener
     *  and checks with the respective delegate action,
     *  if actions is available process intent will be called
     */
    @SuppressLint("CheckResult")
    private fun listenDelegateActionsFromLogCatListener() {
        val subscribeTicket =
            SubscribeTicket(RxBus.SubjectType.PUBLISH_SUBJECT)
        speedTestDelegateDisposable = bus.register<LogcatListenerEvent>(subscribeTicket).subscribe {
            EchoLocateLog.eLogV("CMS Limit----- speedtest delegate - rx bus action received " + it.intent.action!!)
            if (ACTIONS.contains(it.intent.action)) {
                handleIntent(it.intent)
            }
        }
    }

    /**
     * Handle intent when screen off event called
     */
    override fun handleIntent(intent: Intent?) {
        if (intent == null || TextUtils.isEmpty(intent.action)) {
            return
        }
        when (intent.action) {
            ACTION_SCREEN_OFF -> {
                if (stateFocused) {
                    EchoLocateLog.eLogE("CMS Limit-Speedtest - ApplicationState.SCREEN_OFF $SCREEN_OFF_CODE")

                    storeLteEntity(SCREEN_OFF_CODE)

                    cancelTimeoutsAndLogcatListeners()

                    speedTestDelegateDisposable?.run {
                        bus.unregister(this)
                    }
                }
            }
            READY_TO_TEST_FOUND_ACTION -> {
                EchoLocateLog.eLogE("CMS Limit-SpeedTest - ApplicationState.READY_TO_TEST_FOUND_ACTION $READY_TO_TEST_CODE")
                storeLteEntity(READY_TO_TEST_CODE)

                if (applicationTrigger.isTriggerLimitReached()) {
                    EchoLocateLog.eLogV("CMS Limit-speedtest delegate -  READY_TO_TEST_FOUND_ACTION returning as limit reached")
                    return
                }
                addLogcatListenerEvent(
                    LogcatlistenerItem(
                        TEST_START_TRIGGER_ID,
                        TEST_START_FOUND_ACTION,
                        regexStart,
                        LogcatListener.Type
                            .ONE_SHOT
                    )
                )
                logcatListener?.start()
            }
            TEST_START_FOUND_ACTION -> {
                EchoLocateLog.eLogE("CMS Limit-Speed test - ApplicationState.TEST_START_FOUND_ACTION $TEST_START_CODE")

                storeLteEntity(TEST_START_CODE)

                if (applicationTrigger.isTriggerLimitReached()) {
                    EchoLocateLog.eLogV("CMS Limit-speedtest delegate -  TEST_START_FOUND_ACTION returning as limit reached")
                    return
                }

                addLogcatListenerEvent(
                    LogcatlistenerItem(
                        DOWNLOAD_TEST_START_TRIGGER_ID, DOWNLOAD_TEST_START_FOUND_ACTION,
                        regexDownload, LogcatListener.Type.ONE_SHOT
                    )
                )
                logcatListener?.removeListener(READY_TO_TEST_TRIGGER_ID)
                logcatListener?.start()
            }
            DOWNLOAD_TEST_START_FOUND_ACTION -> {
                EchoLocateLog.eLogE("CMS Limit-SpeedTest - ApplicationState.DOWNLOAD_TEST_START_FOUND_ACTION $DOWNLOAD_TEST_START_CODE")

                storeLteEntity(DOWNLOAD_TEST_START_CODE)

                if (applicationTrigger.isTriggerLimitReached()) {
                    EchoLocateLog.eLogV("CMS Limit-speedtest delegate -  DOWNLOAD_TEST_START_FOUND_ACTION returning as limit reached")
                    return
                }

                addLogcatListenerEvent(
                    LogcatlistenerItem(
                        UPLOAD_TEST_START_TRIGGER_ID,
                        UPLOAD_TEST_START_FOUND_ACTION,
                        regexUpload,
                        LogcatListener.Type.ONE_SHOT
                    )
                )
                logcatListener?.start()
            }
            UPLOAD_TEST_START_FOUND_ACTION -> {
                EchoLocateLog.eLogE("CMS Limit-SpeedTest - ApplicationState.UPLOAD_TEST_START_FOUND_ACTION $UPLOAD_TEST_START_CODE")

                storeLteEntity(UPLOAD_TEST_START_CODE)

                if (applicationTrigger.isTriggerLimitReached()) {
                    EchoLocateLog.eLogV("CMS Limit-speedtest delegate -  DOWNLOAD_TEST_START_FOUND_ACTION returning as limit reached")
                    return
                }

                addLogcatListenerEvent(
                    LogcatlistenerItem(
                        TEST_END_TRIGGER_ID,
                        TEST_END_FOUND_ACTION,
                        regexEnd,
                        LogcatListener.Type.ONE_SHOT
                    )
                )
                logcatListener?.start()
            }
            TEST_END_FOUND_ACTION -> {
                EchoLocateLog.eLogE("CMS Limit-SpeedTest - ApplicationState.TEST_END_FOUND_ACTION $TEST_END_CODE")

                storeLteEntity(TEST_END_CODE)

                if (applicationTrigger.isTriggerLimitReached()) {
                    EchoLocateLog.eLogV("CMS Limit-speedtest delegate -  TEST_END_FOUND_ACTION returning as limit reached")
                    return
                }

                addLogcatListenerEvent(
                    LogcatlistenerItem(
                        TEST_START_TRIGGER_ID,
                        TEST_START_FOUND_ACTION,
                        regexStart,
                        LogcatListener.Type
                            .ONE_SHOT
                    )
                )
                logcatListener?.start()
            }

        }
    }

    /**
     * Get all the logcat listener ids for speed test delegate
     */
    override fun getLogcatListenerIds(): List<String> {
        return listOf(
            READY_TO_TEST_TRIGGER_ID,
            TEST_START_TRIGGER_ID,
            DOWNLOAD_TEST_START_TRIGGER_ID,
            UPLOAD_TEST_START_TRIGGER_ID,
            TEST_END_TRIGGER_ID
        )
    }

    /**
     * This function returns speed test time out action
     */
    override fun getTimeoutAction(): String {
        return TIMEOUT_ACTION
    }

    /**
     * This function returns speed test time out request code
     */
    override fun getTimeoutRequestCode(): Int {
        return TIMEOUT_REQUEST_CODE
    }

    override fun getApplicationState(triggerCode: Int): ApplicationState {
        return when (triggerCode) {
            FOCUS_GAIN_CODE -> FOCUS_GAIN
            FOCUS_LOSS_CODE -> FOCUS_LOSS
            SCREEN_OFF_CODE -> ApplicationState.SCREEN_OFF
            else -> ApplicationState.PERIODIC
        }
    }

    /**
     * Subscribes to application trigger limit reached event
     */
    @SuppressLint("CheckResult")
    private fun listenApplicationTriggerLimitReachedEvent() {
        val subscribeTicket = SubscribeTicket(RxBus.SubjectType.PUBLISH_SUBJECT)

        appTriggerLimitDisposable =
            RxBus.instance.register<ApplicationTriggerLimitEvent>(subscribeTicket)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io()).subscribe {
                    EchoLocateLog.eLogV("CMS Limit-speedtest delegate -  rx bus action")
                    cancelTimeoutsAndLogcatListeners()
                }
    }

    /**
     * Get current config from Manager to define regex
     * @param lteConfig: get config from configuration module and validate in LteDataManager
     */
    override fun setRegexFromConfig(lteConfig: LTE): BaseDelegate {
        regexReadyToTest = lteConfig.speedtestRegex?.regexReadyToTest?: listOf("")
        regexStart = lteConfig.speedtestRegex?.regexStart?: listOf("")
        regexDownload = lteConfig.speedtestRegex?.regexDownload?: listOf("")
        regexUpload = lteConfig.speedtestRegex?.regexUpload?: listOf("")
        regexEnd = lteConfig.speedtestRegex?.regexEnd?: listOf("")
        return this
    }
}
