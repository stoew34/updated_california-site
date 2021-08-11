package com.tmobile.mytmobile.echolocate.lte.manager

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_SCREEN_OFF
import android.content.IntentFilter
import android.os.SystemClock
import android.text.TextUtils
import androidx.annotation.VisibleForTesting
import com.tmobile.mytmobile.echolocate.lte.oemdata.LteDataMetricsWrapper
import com.tmobile.mytmobile.echolocate.configuration.events.configurationevents.LteConfigEvent
import com.tmobile.mytmobile.echolocate.communicationbus.rxbus.PostTicket
import com.tmobile.mytmobile.echolocate.communicationbus.rxbus.RxBus
import com.tmobile.mytmobile.echolocate.communicationbus.rxbus.SubscribeTicket
import com.tmobile.mytmobile.echolocate.configuration.ConfigKey
import com.tmobile.mytmobile.echolocate.configmanager.ConfigProvider
import com.tmobile.mytmobile.echolocate.configuration.model.LTE
import com.tmobile.mytmobile.echolocate.lte.LteReportProvider
import com.tmobile.mytmobile.echolocate.lte.database.EchoLocateLteDatabase
import com.tmobile.mytmobile.echolocate.lte.database.entity.LteSingleSessionReportEntity
import com.tmobile.mytmobile.echolocate.lte.delegates.*
import com.tmobile.mytmobile.echolocate.lte.intentlisteners.BaseLteBroadcastReceiver
import com.tmobile.mytmobile.echolocate.lte.intentlisteners.IIntentRegistrar
import com.tmobile.mytmobile.echolocate.lte.lteevents.ApplicationTriggerLimitEvent
import com.tmobile.mytmobile.echolocate.lte.lteevents.LteResetTriggerCountListenerEvent
import com.tmobile.mytmobile.echolocate.lte.model.LteSingleSessionReport
import com.tmobile.mytmobile.echolocate.lte.reportprocessor.LteDataStatus
import com.tmobile.mytmobile.echolocate.lte.reportprocessor.LteReportProcessor
import com.tmobile.mytmobile.echolocate.lte.reportprocessor.LteReportScheduler
import com.tmobile.mytmobile.echolocate.lte.utils.*
import com.tmobile.mytmobile.echolocate.lte.utils.LTEApplications.*
import com.tmobile.mytmobile.echolocate.reportingmanager.ReportProvider
import com.tmobile.mytmobile.echolocate.utils.EchoLocateLog
import com.tmobile.mytmobile.echolocate.lte.utils.LteUtils
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

/**
 *
 * This class is the motherboard for the LTE data collection module.
 * The manager class is responsible for making the decision on what to do
 * and delegate the responsibility to required classes to perform specific actions.
 *
 * This class should not contain actual execution of work and computation.
 */
class LteDataManager(val context: Context) : ILteIntentHandler {

    @Volatile
    private var isLteInitialized: Boolean = false
    @Volatile
    private var isBroadcastRegistered: Boolean = false

    private var lteReportProcessor: LteReportProcessor? = null
    private var lteReportScheduler: LteReportScheduler? = null
    private var lteBaseDelegate: BaseDelegate? = null
    private var baseNonStreamDelegate: BaseNonStreamDelegate? = null
    private var baseStreamDelegate: BaseStreamDelegate? = null

    private val bus = RxBus.instance
    private var lteConfigUpdateDisposable: Disposable? = null
    private var triggerLimitEventDisposable: Disposable? = null
    private var triggerResetEventDisposable: Disposable? = null

    private var packagesEnabled = listOf<String>()
    private lateinit var lteConfig: LTE

    private var applicationTrigger: ApplicationTrigger = ApplicationTrigger.getInstance(context)

    /** Instance of Broadcast Receiver,handle all the OEM intents delivered */
    var baseLteBroadcastReceiver: BaseLteBroadcastReceiver? = null

    /** Variable to check if Nr5g is supported */
    val isLteSupported = isEchoLocateLTESupported()

    /** Initialization block */
    init {
        if (isLteSupported) {
            baseLteBroadcastReceiver = BaseLteBroadcastReceiver()
            baseLteBroadcastReceiver?.setListener(this)
            lteReportProcessor = LteReportProcessor.getInstance(context)
        }
    }

    /**
     * initialization data collection for Lte by starting LteDataManager
     * update(restart data collection) if CMS configurations changes by starting [updateLteModuleConfig]
     *
     * controlled by CMS configurations
     */
    fun initLteDataManager() {
        val configProvider = ConfigProvider.getInstance(context)
        val lteConfig = configProvider.getConfigurationForKey(ConfigKey.LTE, context)

        if (lteConfig != null) {
            manageLteDataCollection(lteConfig as LTE)
        }

        updateLteModuleConfig()
        listenTriggerLimitReachedEvent()
    }

    /**
     * Manage data collection for Lte by:
     * @param [lteConfig] from CMS configurations
     *
     * define [isLteInitialized]
     * controlled by CMS configurations
     */
    private fun manageLteDataCollection(lteConfig: LTE): Boolean {
        EchoLocateLog.eLogI(
            "CMS config status for Lte: ${lteConfig.isEnabled}" +
                    " and LTE supported: $isLteSupported"
        )
        if (!lteConfig.isEnabled || !isLteSupported || LteUtils.isTmoAppVersionBlackListed(context, lteConfig.blacklistedTMOAppVersion) ||
            LteUtils.checkTacInList(context, lteConfig.blacklistedTAC)
        ) {

            stopLteDataCollection()

            return isLteInitialized
        }

        startLteDataCollection(lteConfig)

        return isLteInitialized
    }

    /**
     * Start data collection for Lte by:
     * - Register Report Types
     * - start Lte Report Scheduler
     * - Register Receiver
     *
     * define [isLteInitialized] as true
     * controlled by CMS configurations
     */
    private fun startLteDataCollection(lteConfig: LTE) {
        updateAllRecordsWithEmptyStatusToRAWStatus()
        if (!isLteInitialized && isLteSupported) {
            EchoLocateLog.eLogD("Diagnostic : LTE module init")

            ReportProvider.getInstance(context).initReportingModule()
                .registerReportTypes(LteReportProvider.getInstance(context))

            registerReceiver()

            packagesEnabled = lteConfig.packages_enabled
            this.lteConfig = lteConfig

            applicationTrigger.saveTriggerLimit(lteConfig.triggerLimit)

            LteHourlyDelegate.getInstance(context).startScheduler()

            listenResetDelegateActions()
            createApplicationTriggerAlarm()

            isLteInitialized = true
        }
        else {
            applicationTrigger.saveTriggerLimit(lteConfig.triggerLimit)
            packagesEnabled = lteConfig.packages_enabled
            this.lteConfig = lteConfig
        }

        if (lteReportScheduler == null) {
            lteReportScheduler = LteReportScheduler(context)
            EchoLocateLog.eLogD("Diagnostic : LTE: reportScheduler is null")
        }

        lteReportScheduler?.schedulerJob(getSamplingInterval(lteConfig), lteConfig.isEnabled)
        EchoLocateLog.eLogD("Diagnostic : LTE: new scheduler job started, intervals: ${getSamplingInterval(lteConfig)}")
    }

    /**
     * Stop data collection for Lte by:
     * - unRegister Report Types
     * - stop Scheduler
     * - unRegister Receiver
     *
     * define [isLteInitialized] as false
     * controlled by CMS configurations and Panic Mode
     */
    fun stopLteDataCollection() {
        if (isLteInitialized) {

            ReportProvider.getInstance(context).unRegisterReportTypes(LteReportProvider.getInstance(context))

            LteHourlyDelegate.getInstance(context).stopPeriodicActions()

            lteReportScheduler?.stopScheduler()

            baseNonStreamDelegate?.stopScheduler()

            baseStreamDelegate?.stopPeriodicActions()

            unRegisterReceiver()

            isLteInitialized = false
        }
    }

    /**
     *This fun listens Configuration module and passing new value to fun @runUpdatedConfigForDataCollection
     */
    private fun updateLteModuleConfig() {

        val subscribeTicket = SubscribeTicket(RxBus.SubjectType.PUBLISH_SUBJECT)

        lteConfigUpdateDisposable?.dispose()
        lteConfigUpdateDisposable = bus.register<LteConfigEvent>(subscribeTicket).subscribe {
            runUpdatedConfigForDataCollection(it)
        }
    }

    /**
     * This fun receive new value from Configuration module
     * and restart Lte Data Collection with new configuration
     */
    private fun runUpdatedConfigForDataCollection(it: LteConfigEvent) {
        if (!it.configValue.isEnabled || it.configValue.panicMode || LteUtils.isTmoAppVersionBlackListed(
                context,
                it.configValue.blacklistedTMOAppVersion
            ) ||
            LteUtils.checkTacInList(context, it.configValue.blacklistedTAC)
        ) {
            stopLteDataCollection()
        } else {
            if (isEchoLocateLTESupported()) {
                startLteDataCollection(it.configValue)
            }

            //TODO check if need next block
            if (applicationTrigger.getTriggerLimit() < it.configValue.triggerLimit) {
                EchoLocateLog.eLogV(
                    "CMS Limit-LteDataManager - changed trigger limit from config is greater than the pre"
                )
                registerReceiver()
            }
        }
        EchoLocateLog.eLogV("Diagnostic : CMS Limit-LteDataManager -  saving limit from onLteConfigChangedScheduler")
    }

    /**
     * generates report by collecting data on mobile devices and
     * sending of OEM intents delivered to the application. These intents are the custom
     * intents implemented by the OEMS for the TMO Applications and can be listened only
     * by the system application. Those intents deliver detailed log data about Triggers made
     * from or to the device.
     */
    fun getLteReport(lteSingleSessionReportEntityList: List<LteSingleSessionReportEntity>): List<LteSingleSessionReport> {
        return lteReportProcessor!!.getLteMultiSessionReport(lteSingleSessionReportEntityList)
    }

    /**
     * This function returns the list of lte reports entity
     * @param startTime reports start date
     * @param endTime reports end date
     */
    fun getLteReportEntity(startTime: Long, endTime: Long): List<LteSingleSessionReportEntity> {
        val lteReportEntityList = lteReportProcessor!!.getLteMultiSessionReportEntity(startTime, endTime)
        lteReportEntityList.let {
            it.forEach {
                it.reportStatus = LteDataStatus.STATUS_REPORTING
            }
        }
        lteReportProcessor!!.updateLteReportEntity(lteReportEntityList)
        return lteReportEntityList

    }

    /**
     *  Gets interval for LteReportScheduler
     */
    private fun getSamplingInterval(lteConfig: LTE): Long {

        val intervalHours = lteConfig.samplingInterval

        // Convert the hours from config to minutes
        return (intervalHours * 60).toLong()
    }

    /**
     * fun registerLteActions
     *  -adds LteIntents to a mutable list
     *  -mutable list is used to register broadcast receivers
     */
    private fun registerLteActions(intentRegistrar: IIntentRegistrar) {
        val intentActions: MutableList<String> = mutableListOf()
        intentActions.add(LteIntents.APP_INTENT_ACTION)
        intentActions.add(ACTION_SCREEN_OFF)
        intentRegistrar.registerLteReceiver(intentActions)
    }

    /**
     * Initializes the intent registrars by getting the list of the actions required for lte module.
     * converts the actions to intent filters to register receiver dynamically
     */
    private fun initLteRegistration(): IIntentRegistrar {
        return object : IIntentRegistrar {
            override fun registerLteReceiver(intentActions: MutableList<String>) {
                val intentFilter = convertStringToIntentFilter(intentActions)
                context.registerReceiver(baseLteBroadcastReceiver, intentFilter)
                setBroadCastRegistered(true)
            }
        }
    }

    /**
     * Converts the list of actions wwhich is string to intent filter
     * @param intentActions: MutableList<String> - list of all actions supported by lte module
     * @return MutableList<IntentFilter> list of converted intent filters
     */
    private fun convertStringToIntentFilter(intentActions: MutableList<String>): IntentFilter {
        val intentFilter = IntentFilter()

        for (action in intentActions) {
            intentFilter.addAction(action)
        }
        return intentFilter
    }

    /**
     * instance of BaseLteBroadcastReceiver which will handle all the OEM intents
     * delivered
     **/
    override fun onHandleIntent(intent: Intent?, eventTimestamp: Long) {
        if (!applicationTrigger.isTriggerLimitReached()) {
            EchoLocateLog.eLogV("Diagnostic : CMS Limit-LteDataManager -  within limit creating delegate")
            checkApplicationActions(intent)
        } else {
            EchoLocateLog.eLogV("Diagnostic : CMS Limit-LteDataManager -  limit reached so not invoking delegate")
        }
    }

    /**
     * Method checks the application actions depends on the intent triggered.
     * navigates to respective delegate and perform events actions.
     */
    private fun checkApplicationActions(intent: Intent?) {
        val action = intent?.action
        EchoLocateLog.eLogV("Diagnostic : action received: $action")
        if (TextUtils.isEmpty(action)) {
            return
        }
        when {
            LteIntents.APP_INTENT_ACTION == action -> {
                checkApplicationState(intent)
            }
            ACTION_SCREEN_OFF == action -> {
                handleScreenOffEvent(intent)
            }
        }
    }

    /**
     * This function handles screen off events for respective applications
     * @param intent: Intent from the broadcast event triggered.
     */
    private fun handleScreenOffEvent(intent: Intent) {
        lteBaseDelegate?.handleIntent(intent)
    }

    /**
     * This function checks the application state from intent received in onhandle intent,
     * and returns if it is unsupported type
     */
    private fun checkApplicationState(intent: Intent) {
        val applicationState = LteUtils.extractApplicationStateFromIntent(intent)

        if (applicationState == ApplicationState.UNSUPPORTED) {
            EchoLocateLog.eLogI("Diagnostic : LTE application state is unsupported, returning...")
            return
        }
        invokeDelegate(intent, applicationState)
    }

    /**
     * This function gets the application type and invokes the handler process
     * and processes the data based on state such as focus_gain,focus_loss
     */
    private fun invokeDelegate(intent: Intent, applicationState: ApplicationState) {
        val triggerApplication =
            LteUtils.isPackageEligibleForDataCollection(intent, packagesEnabled) ?: return
        lteBaseDelegate = createDelegateFromApplication(triggerApplication) ?: return
        EchoLocateLog.eLogV("Diagnostic : CMS Limit delegate invoked: " + lteBaseDelegate?.getTriggerApplication()?.getKey())
        lteBaseDelegate?.processApplicationState(applicationState)
    }

    /**
     * This function creates the handler type based on trigger application and assigns the handler using enum
     */
    private fun createDelegateFromApplication(triggerApplication: LTEApplications): BaseDelegate? {
        return when (triggerApplication) {
            YOUTUBE -> YoutubeDelegate.getInstance(context).setRegexFromConfig(lteConfig)
            NETFLIX -> NetflixDelegate.getInstance(context).setRegexFromConfig(lteConfig)
            SPEED_TEST -> SpeedTestDelegate.getInstance(context).setRegexFromConfig(lteConfig)
            FACEBOOK -> FacebookDelegate.getInstance(context)
            INSTAGRAM -> InstagramDelegate.getInstance(context)
            YOUTUBE_TV -> YouTubeTvDelegate.getInstance(context)
        }
    }

    /**
     * Sets the boolean to true if the broadcast is registered
     * @param flag: Boolean
     */
    @VisibleForTesting
    fun setBroadCastRegistered(flag: Boolean) {
        isBroadcastRegistered = flag
    }

    /**
     *  This method checks is the device lte supported or not.
     *  If Data metrics class is available and the data metrics version >=1
     */
    private fun isEchoLocateLTESupported(): Boolean {
        val lteDataMetricsWrapper = LteDataMetricsWrapper(context)
        EchoLocateLog.eLogV("Diagnostic : EchoLocate ApiVersion " + lteDataMetricsWrapper.getApiVersion().intCode)
        return lteDataMetricsWrapper.isDataMetricsAvailable() && lteDataMetricsWrapper.getApiVersion().intCode >= 1
    }

    /**
     * This function unregisters the broadcast
     */

    private fun unRegisterReceiver() {
        if (isBroadcastRegistered) {
            context.unregisterReceiver(baseLteBroadcastReceiver)
            setBroadCastRegistered(false)
        }
    }

    /**
     * This function registers the broadcast
     */
    fun registerReceiver() {
        if (!isBroadcastRegistered) {
            val intentRegistrar = initLteRegistration()
            registerLteActions(intentRegistrar)
            setBroadCastRegistered(true)
        }
    }

    /**
     * Receiver for the alarm manager that triggers every 24 hours to reset the trigger count
     */
    class LteApplicationTriggerResetReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val postTicket = PostTicket(
                LteResetTriggerCountListenerEvent(intent)
            )
            RxBus.instance.post(postTicket)
        }
    }

    /**
     * Resets the trigger count
     */
    private fun resetTriggerCount() {
        applicationTrigger.saveTriggerCount(LteConstants.RESET_TRIGGER_COUNT)
    }

    /**
     * This function listens to the trigger limit event to unregister receiver when the limit is reached
     */
    @SuppressLint("CheckResult")
    private fun listenTriggerLimitReachedEvent() {
        val subscribeTicket = SubscribeTicket(RxBus.SubjectType.PUBLISH_SUBJECT)

        triggerLimitEventDisposable =
            RxBus.instance.register<ApplicationTriggerLimitEvent>(subscribeTicket).subscribeOn(
                Schedulers.io()
            ).observeOn(Schedulers.io()).subscribe {
                EchoLocateLog.eLogV("Diagnostic : CMS Limit-rx bus action received in ltemodule manager: " + it.isMaxTriggerLimitReached)
                EchoLocateLog.eLogV("Diagnostic : CMS Limit-isbroadcast registered: $isBroadcastRegistered")
                baseNonStreamDelegate?.stopScheduler()
                baseStreamDelegate?.stopPeriodicActions()
                unRegisterReceiver()
            }
    }

    /**
     * This function deletes the processed report data from db
     */
    fun deleteProcessedReportsFromDatabase() {
        lteReportProcessor?.deleteProcessedReports(LteDataStatus.STATUS_REPORTING)
    }

    /**
     * This is used to listen the actions for resetting trigger count in every 24 hours.
     */
    @SuppressLint("CheckResult")
    private fun listenResetDelegateActions() {
        val subscribeTicket =
            SubscribeTicket(RxBus.SubjectType.PUBLISH_SUBJECT)
        triggerResetEventDisposable =
            RxBus.instance.register<LteResetTriggerCountListenerEvent>(subscribeTicket)
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .subscribe {
                    EchoLocateLog.eLogV("Diagnostic : CMS Limit-in on receive , resetting the trigger")
                    //reset the trigger count
                    resetTriggerCount()

                    //register the lte actions only if the broad cast is not registered
                    registerReceiver()
                }
    }

    /**
     * Schedules alarm to reset the application trigger every 24 hours
     */
    private fun createApplicationTriggerAlarm() {
        EchoLocateLog.eLogV("Diagnostic : CMS Limit-scheduling application trigger alarm")

        val intent = Intent(context, LteApplicationTriggerResetReceiver::class.java)
        intent.action = LteConstants.TRIGGER_COUNT_RESET_ACTION

        val timeoutPendingIntent = PendingIntent.getBroadcast(
            context, LteConstants.LTE_RESET_TRIGGER_CODE, intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setInexactRepeating(
            AlarmManager.ELAPSED_REALTIME_WAKEUP,
            SystemClock.elapsedRealtime() + AlarmManager.INTERVAL_DAY,
            AlarmManager.INTERVAL_DAY,
            timeoutPendingIntent
        )
    }

    /**
     * Tells if the lte manager is up and running.
     */
    fun isManagerInitialized(): Boolean {
        return isLteInitialized
    }
    /**
     * Get all records from DB which are having the empty status and updates them with "STATUS_RAW"
     */
    private fun updateAllRecordsWithEmptyStatusToRAWStatus() {
        // If data collection process get interrupted the status will be empty and records exists there forever in DB.
        // "STATUS_RAW" will be updated to database after all child tables gets filled with the data.

        val lteDao = EchoLocateLteDatabase.getEchoLocateLteDatabase(context).lteDao()
        val baseEchoLocateLTEEntityList = lteDao.getBaseEchoLocateLteEntityByStatus(LteDataStatus.STATUS_EMPTY)
        baseEchoLocateLTEEntityList.forEach { f -> f.status = LteDataStatus.STATUS_RAW }
        lteDao.updateAllBaseEchoLocateLteEntityStatus(*baseEchoLocateLTEEntityList.toTypedArray())
    }
}
