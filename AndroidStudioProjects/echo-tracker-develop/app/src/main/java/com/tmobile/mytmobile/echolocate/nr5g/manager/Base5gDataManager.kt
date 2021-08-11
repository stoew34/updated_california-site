package com.tmobile.mytmobile.echolocate.nr5g.manager

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.SystemClock
import android.text.TextUtils
import androidx.annotation.VisibleForTesting
import com.tmobile.mytmobile.echolocate.communicationbus.rxbus.PostTicket
import com.tmobile.mytmobile.echolocate.communicationbus.rxbus.RxBus
import com.tmobile.mytmobile.echolocate.communicationbus.rxbus.SubscribeTicket
import com.tmobile.mytmobile.echolocate.configmanager.ConfigProvider
import com.tmobile.mytmobile.echolocate.configuration.ConfigKey
import com.tmobile.mytmobile.echolocate.configuration.events.configurationevents.Nr5gConfigEvent
import com.tmobile.mytmobile.echolocate.configuration.events.configurationevents.Sa5gConfigEvent
import com.tmobile.mytmobile.echolocate.configuration.model.Base5g
import com.tmobile.mytmobile.echolocate.configuration.model.Nr5gTriggerControl
import com.tmobile.mytmobile.echolocate.nr5g.Nr5gReportProvider
import com.tmobile.mytmobile.echolocate.nr5g.Sa5gReportProvider
import com.tmobile.mytmobile.echolocate.nr5g.core.delegates.AllAppsDelegate
import com.tmobile.mytmobile.echolocate.nr5g.core.delegates.BaseDelegate
import com.tmobile.mytmobile.echolocate.nr5g.core.delegates.Nr5gHourlyDelegate
import com.tmobile.mytmobile.echolocate.nr5g.core.events.Nr5gResetListenerEvent
import com.tmobile.mytmobile.echolocate.nr5g.core.events.Nr5gTriggerLimitEvent
import com.tmobile.mytmobile.echolocate.nr5g.core.intentlisteners.BaseNr5gBroadcastReceiver
import com.tmobile.mytmobile.echolocate.nr5g.core.intentlisteners.IIntentRegistrar
import com.tmobile.mytmobile.echolocate.nr5g.core.scheduler.Nr5gReportScheduler
import com.tmobile.mytmobile.echolocate.nr5g.core.scheduler.Nr5gReportSenderScheduler
import com.tmobile.mytmobile.echolocate.nr5g.core.utils.*
import com.tmobile.mytmobile.echolocate.nr5g.core.utils.Nr5gConstants.DEFAULT_REPORTING_INTERVAL
import com.tmobile.mytmobile.echolocate.nr5g.core.utils.Nr5gUtils.Companion.getBase5g
import com.tmobile.mytmobile.echolocate.nr5g.nsa5g.database.EchoLocateNr5gDatabase
import com.tmobile.mytmobile.echolocate.nr5g.nsa5g.database.dao.Nr5gDao
import com.tmobile.mytmobile.echolocate.nr5g.nsa5g.reportprocessor.Nsa5gDataStatus
import com.tmobile.mytmobile.echolocate.nr5g.sa5g.database.EchoLocateSa5gDatabase
import com.tmobile.mytmobile.echolocate.nr5g.sa5g.database.dao.Sa5gDao
import com.tmobile.mytmobile.echolocate.nr5g.sa5g.reportprocessor.Sa5gDataStatus
import com.tmobile.mytmobile.echolocate.reportingmanager.ReportProvider
import com.tmobile.mytmobile.echolocate.utils.EchoLocateLog
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlin.random.Random

/**
 *
 * This class is the motherboard for the Nr5g data collection module.
 * The manager class is responsible for making the decision on what to do and delegate the responsibility
 * to required classes to perform specific actions.
 *
 * This class should not contain actual execution of work and computation.
 */
abstract class Base5gDataManager(val context: Context) : INr5gIntentHandler {

    @Volatile
    private var isDataCollectionInitialized: Boolean = false

    @Volatile
    private var isAppBroadcastRegistered: Boolean = false

    @Volatile
    private var isScreenBroadcastRegistered: Boolean = false

    private var nr5gReportScheduler: Nr5gReportScheduler? = null
    private var nr5gReportSenderScheduler: Nr5gReportSenderScheduler? = null
    private var nr5gBaseDelegate: BaseDelegate? = null

    private val bus = RxBus.instance
    private var configUpdateDisposable: Disposable? = null
    private var triggerLimitEventDisposable: Disposable? = null
    private var triggerResetEventDisposable: Disposable? = null

    private var excludedPackages: ArrayList<String> = ArrayList()
    private var whiteListedPackages: ArrayList<String> = ArrayList()

    private var nr5gApplicationTrigger: Nr5gApplicationTrigger =
        Nr5gApplicationTrigger.getInstance(context)
    private var nr5gScreenTrigger: Nr5gScreenTrigger = Nr5gScreenTrigger.getInstance(context)

    /** Instance of Broadcast Receiver,handle all the OEM intents delivered */
    var baseNr5gAppBroadcastReceiver: BaseNr5gBroadcastReceiver? = null
    var baseNr5gScreenBroadcastReceiver: BaseNr5gBroadcastReceiver? = null

    /** Variable to check if Nr5g is supported */
    var isNsa5gSupported = false

    /** Variable to check if Sa5g is supported */
    var isSa5gSupported = false

    /** Initialization block */
    init {
        baseNr5gAppBroadcastReceiver = BaseNr5gBroadcastReceiver()
        baseNr5gAppBroadcastReceiver?.setListener(this)
        baseNr5gScreenBroadcastReceiver = BaseNr5gBroadcastReceiver()
        baseNr5gScreenBroadcastReceiver?.setListener(this)
        EchoLocateLog.eLogD("Diagnostic :Nr5g init Broadcast Receivers ")
    }

    /**
     * initialization data collection for Nr5g by starting Base5gDataManager
     * update(restart data collection) if CMS configurations changes by starting [updateNr5gModuleConfig]
     *
     * controlled by CMS configurations
     */
    fun initNr5gDataManager() {
        EchoLocateLog.eLogD("Diagnostic :Nr5g initNr5gDataManager called")
        val base5gConfig = get5gConfig()
        if (base5gConfig != null) {
            EchoLocateLog.eLogD("Diagnostic :Nr5g manageNr5gDataCollection")
            manageNr5gDataCollection(base5gConfig as Base5g)
        }
        updateNr5gModuleConfig()
        listenTriggerLimitReachedEvent()
    }

    /**
     * Manage data collection for Nr5g by:
     * @param [base5gConfig] from CMS configurations
     *
     * define [isDataCollectionInitialized]
     * controlled by CMS configurations
     */
    private fun manageNr5gDataCollection(base5gConfig: Base5g): Boolean {
        EchoLocateLog.eLogI(
            "Diagnostic :Nr5g config status for Nr5g: ${base5gConfig.isEnabled}" +
                    " and 5G supported: $isNsa5gSupported"
        )
        if (!base5gConfig.isEnabled ||
            Nr5gUtils.isTmoAppVersionBlackListed(context, base5gConfig.blacklistedTMOAppVersion) ||
            Nr5gUtils.checkTacInList(context, base5gConfig.blacklistedTAC)
        ) {

            stopNr5gDataCollection()

            return isDataCollectionInitialized
        }

        startNr5gDataCollection(base5gConfig)

        return isDataCollectionInitialized
    }

    /**
     * Start data collection for Nr5g by:
     * - Register Report Types
     * - start Nr5g Report Scheduler
     * - Register Receiver
     *
     * define [isDataCollectionInitialized] as true
     * controlled by CMS configurations
     */
    private fun startNr5gDataCollection(base5gConfig: Base5g) {
        updateAllRecordsWithEmptyStatusToRAWStatus()
        if (!isDataCollectionInitialized && (isNsa5gSupported || isSa5gSupported)) {
            EchoLocateLog.eLogD("Diagnostic :Nr5g Data Collection is started")

            if (base5gConfig.reportingInterval == null || base5gConfig.reportingInterval!! <= 0) {
                if (isSa5gSupported) {
                    ReportProvider.getInstance(context).initReportingModule()
                        .registerReportTypes(Sa5gReportProvider.getInstance(context))
                }
                if (isNsa5gSupported) {
                    ReportProvider.getInstance(context).initReportingModule()
                        .registerReportTypes(Nr5gReportProvider.getInstance(context))
                }
            } else {
                if (isSa5gSupported) {
                    ReportProvider.getInstance(context)
                        .unRegisterReportTypes(Sa5gReportProvider.getInstance(context))
                }
                if (isNsa5gSupported) {
                    ReportProvider.getInstance(context)
                        .unRegisterReportTypes(Nr5gReportProvider.getInstance(context))
                }
            }

            registerToReceiver(base5gConfig)
            setupHourlyDelegate(base5gConfig)

            listenResetDelegateActions()
            createApplicationTriggerAlarm()

            isDataCollectionInitialized = true
        }

        nr5gScreenTrigger.saveScreenTriggerLimit(base5gConfig.screenTriggerLimit)
        nr5gApplicationTrigger.saveAppTriggerLimit(base5gConfig.appTriggerLimit)
        excludedPackages.clear()
        if (!base5gConfig.excludePackages.isNullOrEmpty()) excludedPackages.addAll(base5gConfig.excludePackages!!)
        whiteListedPackages.clear()
        if (!base5gConfig.whitelistedPackages.isNullOrEmpty()) whiteListedPackages.addAll(
            base5gConfig.whitelistedPackages!!
        )

        if (nr5gReportScheduler == null) {
            nr5gReportScheduler = Nr5gReportScheduler(context)
            nr5gReportScheduler?.setListener(this)
        }
        nr5gReportScheduler?.schedulerJob(
            getSamplingInterval(base5gConfig),
            base5gConfig.isEnabled
        )
        EchoLocateLog.eLogD("Diagnostic :Nr5g schedulerJob processing")

        if (nr5gReportSenderScheduler == null) {
            nr5gReportSenderScheduler = Nr5gReportSenderScheduler(context)
            nr5gReportSenderScheduler?.setListener(this)
        }
        if (getReportingInterval(base5gConfig) > 0) {
            nr5gReportSenderScheduler?.schedulerJob(
                getReportingInterval(base5gConfig),
                base5gConfig.isEnabled
            )
            EchoLocateLog.eLogD("Diagnostic :Nr5g schedulerJob reporting")
        } else {
            nr5gReportSenderScheduler?.stopScheduler()
        }
    }


    /**
     * This function will update empty status to RAW.
     */
    private fun updateAllRecordsWithEmptyStatusToRAWStatus(){
        if (isSa5gSupported) {
            val sa5gDao: Sa5gDao = EchoLocateSa5gDatabase.getEchoLocateSa5gDatabase(context).sa5gDao()
            val baseEchoLocateSa5gEntityList = sa5gDao.getBaseEchoLocateSa5gEntityByStatus(Sa5gDataStatus.STATUS_EMPTY)
            baseEchoLocateSa5gEntityList.forEach { f -> f.status = Nsa5gDataStatus.STATUS_RAW }
            sa5gDao.updateAllBaseEchoLocateSa5gEntityStatus(*baseEchoLocateSa5gEntityList.toTypedArray())
        } else if (isNsa5gSupported) {
            val nr5gDao: Nr5gDao = EchoLocateNr5gDatabase.getEchoLocateNr5gDatabase(context).nr5gDao()
            val baseEchoLocateNr5gEntityList = nr5gDao.getBaseEchoLocateNr5gEntityByStatus(Nsa5gDataStatus.STATUS_EMPTY)
            baseEchoLocateNr5gEntityList.forEach { f -> f.status = Nsa5gDataStatus.STATUS_RAW }
            nr5gDao.updateAllBaseEchoLocateNr5gEntityStatus(*baseEchoLocateNr5gEntityList.toTypedArray())
        }
    }

    /**
     * Start or stop Nr5gHourlyDelegate depending on the configuration.
     */
    private fun setupHourlyDelegate(base5gConfig: Base5g) {
        val nr5gHourlyDelegate = Nr5gHourlyDelegate.getInstance(context)
        nr5gHourlyDelegate.nr5gHandler = this
        if (base5gConfig.triggerControl?.triggerHourly == true) {
            nr5gHourlyDelegate.startScheduler()
        } else {
            nr5gHourlyDelegate.stopPeriodicActions()
        }
    }

    /**
     * Stop data collection for Nr5g by:
     * - unRegister Report Types
     * - stop Scheduler
     * - unRegister Receiver
     *
     * define [isDataCollectionInitialized] as false
     * controlled by CMS configurations and Panic Mode
     */
    fun stopNr5gDataCollection() {
        if (isDataCollectionInitialized) {
            if (isSa5gSupported) {
                ReportProvider.getInstance(context).unRegisterReportTypes(Sa5gReportProvider.getInstance(context))
            }
            if (isNsa5gSupported) {
                ReportProvider.getInstance(context).unRegisterReportTypes(Nr5gReportProvider.getInstance(context))
            }

            Nr5gHourlyDelegate.getInstance(context).stopPeriodicActions()

            nr5gReportScheduler?.stopScheduler()

            nr5gReportSenderScheduler?.stopScheduler()

            nr5gBaseDelegate?.stopScheduler()

            unRegisterAppReceiver()
            unRegisterScreenReceiver()

            isDataCollectionInitialized = false
            EchoLocateLog.eLogD("Diagnostic :Nr5g Data Collection is stopped")
        }
    }

    /**
     *This fun listens Configuration module and passing new value to fun @runUpdatedConfigForDataCollection
     */
    private fun updateNr5gModuleConfig() {

        val subscribeTicket = SubscribeTicket(RxBus.SubjectType.PUBLISH_SUBJECT)

        configUpdateDisposable?.dispose()

        if (isSa5gSupported) {
            configUpdateDisposable = bus.register<Sa5gConfigEvent>(subscribeTicket).subscribe {
                runUpdatedConfigForSa5g(it)
            }
        }
        if (isNsa5gSupported) {
            configUpdateDisposable = bus.register<Nr5gConfigEvent>(subscribeTicket).subscribe {
                runUpdatedConfigForNr5g(it)
            }
        }
    }

    /**
     * This fun receive new value from Configuration module
     * and restart Nr5g Data Collection with new configuration
     */
    private fun runUpdatedConfigForNr5g(it: Nr5gConfigEvent) {
        if (!it.configValue.excludePackages.isNullOrEmpty()) excludedPackages.addAll(it.configValue.excludePackages!!)
        if (!it.configValue.whitelistedPackages.isNullOrEmpty()) whiteListedPackages.addAll(it.configValue.whitelistedPackages!!)

        if (!it.configValue.isEnabled ||
            it.configValue.panicMode ||
            Nr5gUtils.isTmoAppVersionBlackListed(
                context,
                it.configValue.blacklistedTMOAppVersion
            ) ||
            Nr5gUtils.checkTacInList(context, it.configValue.blacklistedTAC)
        ) {
            EchoLocateLog.eLogD("Diagnostic :Nr5g stopNr5gDataCollection from runUpdatedConfigForNr5g")
            stopNr5gDataCollection()
        } else {
            if (isNsa5gSupported) {
                // If "isDataCollectionInitialized" is already initialized
                // startNr5gDataCollection() will ignore the registering to receiver, it will handled by below block
                if (isDataCollectionInitialized) {
                    EchoLocateLog.eLogD("Diagnostic :Nr5g startNr5gDataCollection from runUpdatedConfigForNr5g")
                    registerToReceiver(it.configValue)
                    setupHourlyDelegate(it.configValue)
                }
                EchoLocateLog.eLogD("Diagnostic :Nr5g startNr5gDataCollection from runUpdatedConfigForNr5g")
                startNr5gDataCollection(it.configValue)
            }
        }
    }

    /**
     * This fun receive new value from Configuration module
     * and restart Nr5g Data Collection with new configuration
     */
    private fun runUpdatedConfigForSa5g(it: Sa5gConfigEvent) {
        if (!it.configValue.excludePackages.isNullOrEmpty()) excludedPackages.addAll(it.configValue.excludePackages!!)
        if (!it.configValue.whitelistedPackages.isNullOrEmpty()) whiteListedPackages.addAll(it.configValue.whitelistedPackages!!)

        if (!it.configValue.isEnabled ||
            it.configValue.panicMode ||
            Nr5gUtils.isTmoAppVersionBlackListed(
                context,
                it.configValue.blacklistedTMOAppVersion
            ) ||
            Nr5gUtils.checkTacInList(context, it.configValue.blacklistedTAC)
        ) {
            EchoLocateLog.eLogD("Diagnostic :Sa5g stopNr5gDataCollection from runUpdatedConfigForSa5g")
            stopNr5gDataCollection()
        } else {
            if (isSa5gSupported) {
                // if "isDataCollectionInitialized" is already initialized,
                // startNr5gDataCollection() function will ignore the registering to receiver, it will be handled by below block
                if (isDataCollectionInitialized) {
                    registerToReceiver(it.configValue)
                    setupHourlyDelegate(it.configValue)
                }
                EchoLocateLog.eLogD("Diagnostic :Sa5g startNr5gDataCollection from runUpdatedConfigForSa5g")
                startNr5gDataCollection(it.configValue)
            }
        }
    }

    /**
     *  Gets interval for Nr5gReportScheduler
     */
    private fun getSamplingInterval(base5gConfig: Base5g): Long {

        val intervalHours = base5gConfig.samplingInterval

        /**Convert the hours from config to minutes*/
        return (intervalHours * 60).toLong()
    }

    /**
     *  Gets interval for Report Sender Scheduler
     */
    private fun getReportingInterval(base5gConfig: Base5g): Long {

        val intervalHours = base5gConfig.reportingInterval

        /**Convert the hours from config to minutes*/
        return if (intervalHours != null) {
            (intervalHours * 60).toLong()
        } else {
            DEFAULT_REPORTING_INTERVAL.toLong()
        }
    }

    /**
     * This is used to listen the actions for resetting trigger count in every 24 hours.
     */
    @SuppressLint("CheckResult")
    private fun listenResetDelegateActions() {
        val subscribeTicket =
            SubscribeTicket(RxBus.SubjectType.PUBLISH_SUBJECT)
        triggerResetEventDisposable =
            RxBus.instance.register<Nr5gResetListenerEvent>(subscribeTicket)
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .subscribe {
                    EchoLocateLog.eLogV("Nr5g CMS Limit-in on receive , resetting the trigger")
                    //reset the trigger count
                    resetTriggerCount()

                    //register the nr5g actions only if the broad cast is not registered
                    registerToReceiver(get5gConfig() as Base5g)
                }
    }

    /**
     * Register to App and screen the receiver according the
     */
    private fun registerToReceiver(base5gConfig: Base5g) {

        EchoLocateLog.eLogV("APP_INTENT_ACTION :: In registerToReceiver()")
        if ((base5gConfig.triggerControl?.triggerAllApps == true) && (nr5gApplicationTrigger.getAppTriggerCount() <= base5gConfig.appTriggerLimit)) {
            registerAppReceiver()
            EchoLocateLog.eLogV("APP_INTENT_ACTION :: registered to APP_INTENT_ACTION")
        } else {
            unRegisterAppReceiver()
            EchoLocateLog.eLogV("APP_INTENT_ACTION :: unregistered to APP_INTENT_ACTION")
        }

        if ((base5gConfig.triggerControl?.triggerScreenON == true) && (nr5gScreenTrigger.getScreenTriggerCount() <= base5gConfig.screenTriggerLimit)) {
            registerScreenReceiver()
        } else {
            unRegisterScreenReceiver()
        }
    }

    /**
     * fun registerNr5gActions
     *  -adds Nr5gIntents to a mutable list
     *  -mutable list is used to register broadcast receivers
     */
    private fun registerNr5gAppActions(intentRegistrar: IIntentRegistrar) {
        val appIntentActions: MutableList<String> = mutableListOf()
        appIntentActions.add(Nr5gIntents.APP_INTENT_ACTION)
        intentRegistrar.registerNr5gAppReceiver(appIntentActions)
    }

    private fun registerNr5gScreenActions(intentRegistrar: IIntentRegistrar) {
        val screenIntentActions: MutableList<String> = mutableListOf()
        screenIntentActions.add(Nr5gIntents.ACTION_SCREEN_ON)
        screenIntentActions.add(Nr5gIntents.ACTION_SCREEN_OFF)
        intentRegistrar.registerNr5gScreenReceiver(screenIntentActions)
    }

    /**
     * Initializes the intent registrars by getting the list of the actions
     * required for Nr5g module. Converts the actions to intent filters to
     * register receiver dynamically
     */
    private fun initNr5gIntentRegistration(): IIntentRegistrar {
        return object : IIntentRegistrar {
            override fun registerNr5gAppReceiver(intentActions: MutableList<String>) {
                val intentFilter = convertStringToIntentFilter(intentActions)
                context.registerReceiver(baseNr5gAppBroadcastReceiver, intentFilter)
                setAppBroadCastRegistered(true)
            }

            override fun registerNr5gScreenReceiver(intentActions: MutableList<String>) {
                val intentFilter = convertStringToIntentFilter(intentActions)
                context.registerReceiver(baseNr5gScreenBroadcastReceiver, intentFilter)
                setScreenBroadCastRegistered(true)
            }
        }
    }

    /**
     * Converts the list of actions which is string to intent filter
     * @param intentActions: MutableList<String> - list of all actions supported by Nr5g module
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
     * instance of BaseNr5gBroadcastReceiver which will handle all the OEM intents
     * delivered
     **/
    override fun onHandleIntent(intent: Intent?, eventTimestamp: Long) {
        checkApplicationActions(intent)
    }

    /**
     * Method checks the application actions depends on the intent triggered.
     * navigates to respective delegate and perform events actions.
     */
    private fun checkApplicationActions(intent: Intent?) {
//        nr5gBaseDelegate = createDelegateFromApplication()
//        nr5gBaseDelegate!!.nr5gHandler = this

        val action = intent?.action
        if (TextUtils.isEmpty(action)) {
            return
        }

        when {

            Nr5gIntents.APP_INTENT_ACTION == action -> {
                if (filterNr5gIntents(intent.getStringExtra(Nr5gIntents.APP_PACKAGE)) && !nr5gApplicationTrigger.isAppTriggerLimitReached()) {
                    val currTriggerControl: Nr5gTriggerControl? = getBase5g(context).triggerControl
                    if (currTriggerControl != null && !currTriggerControl.triggerAllApps) {
                        return
                    }
                    checkApplicationState(intent)
                } else {
                    EchoLocateLog.eLogV("Nr5g CMS Limit- app trigger limit reached")
                }
            }

            (Nr5gIntents.ACTION_SCREEN_OFF == action) -> {
                nr5gBaseDelegate?.processIntent(intent)
            }

            (Nr5gIntents.ACTION_SCREEN_ON == action) -> {
                if (!nr5gScreenTrigger.isScreenTriggerLimitReached()) {
                    EchoLocateLog.eLogV("Nr5g CMS Limit- screen invoking delegate as trigger limit dint reach")
                    nr5gBaseDelegate?.processIntent(intent)
                } else {
                    EchoLocateLog.eLogV("Nr5g CMS Limit- screen trigger limit reached")
                }
            }

            else -> {
                EchoLocateLog.eLogV("checkApplicationActions() :: Nr5g CMS Limit intent not matched with any action")
            }

        }
    }

    /**
     * This function checks the application state from intent received in onhandle intent,
     * and returns if it is unsupported type
     */
    private fun checkApplicationState(intent: Intent) {
        val applicationState = Nr5gUtils.extractApplicationStateFromIntent(intent)
        if (applicationState == ApplicationState.UNSUPPORTED) {
            return
        }
        invokeDelegate(intent, applicationState)
    }


    /**
     * This function gets the application type and invokes the handler process
     * and processes the data based on state such as focus_gain,focus_loss
     */
    private fun invokeDelegate(intent: Intent, applicationState: ApplicationState) {
        val appPackage = intent.getStringExtra(Nr5gIntents.APP_PACKAGE)
        if (!excludedPackages.contains(appPackage)) {
            nr5gBaseDelegate = createDelegateFromApplication()
            nr5gBaseDelegate!!.nr5gHandler = this
            EchoLocateLog.eLogV("Nr5g CMS Limit-----delegate invoked ")

            if (applicationState == ApplicationState.FOCUS_GAIN) {
                nr5gBaseDelegate?.packageName = intent.getStringExtra(Nr5gIntents.APP_PACKAGE)
            }

            nr5gBaseDelegate?.launchTriggerTimestamp =
                intent.getLongExtra(Nr5gIntents.TRIGGER_TIMESTAMP, System.currentTimeMillis())
            nr5gBaseDelegate?.processApplicationState(applicationState)
        }
    }

    /**
     * The function is responsible for checking if the package name received in the intent is an exempt package name for which data should not be captured.
     *
     * @param packageName The name of the package to be checked if exempted or not.
     *
     * @return [Boolean] Return false if package is exempted, True otherwise.
     */
    private fun filterNr5gIntents(packageName: String?): Boolean {
        // If "whitelistedPackages" is not empty, then we have to ignore the list from "blacklistedPackages".
        if (whiteListedPackages.isNotEmpty()) {
            for (pack in whiteListedPackages) {
                if (packageName == pack) {
                    return true
                }
            }
            // If whitelist is not empty and the package is not in the whitelist we have to exclude it, that why this return statement
            return false
        } else {
            for (pack in excludedPackages) {
                if (packageName == pack) {
                    return false
                }
            }
            return true
        }
    }

    /**
     * This function creates the handler type based on trigger application
     * and assigns the handler using enum
     */
    private fun createDelegateFromApplication(): BaseDelegate? {
        return AllAppsDelegate.getInstance(context)
    }

    /**
     * Schedules alarm to reset the application trigger every 24 hours
     */
    private fun createApplicationTriggerAlarm() {
        EchoLocateLog.eLogV("Nr5g CMS Limit-scheduling application trigger alarm")
        val intent = Intent(context, Nr5gApplicationTriggerResetReceiver::class.java)
        intent.action = Nr5gConstants.NR5G_TRIGGER_COUNT_RESET_ACTION

        val timeoutPendingIntent = PendingIntent.getBroadcast(
            context, Nr5gConstants.NR5G_TRIGGER_COUNT_RESET_ACTION_REQUEST_CODE, intent,
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
     * This function listens to the trigger limit event to unregister receiver when the limit is reached
     */
    @SuppressLint("CheckResult")
    private fun listenTriggerLimitReachedEvent() {
        val subscribeTicket = SubscribeTicket(RxBus.SubjectType.PUBLISH_SUBJECT)
        triggerLimitEventDisposable =
            RxBus.instance.register<Nr5gTriggerLimitEvent>(subscribeTicket).subscribeOn(
                Schedulers.io()
            ).observeOn(Schedulers.io()).subscribe {
                if (it.triggerType == Nr5gConstants.TRIGGER_LIMIT_TYPE_APP) {
                    EchoLocateLog.eLogV("Nr5g CMS App Trigger Limit-rx bus action received in Nr5gModule manager: " + it.isMaxTriggerLimitReached)
                    EchoLocateLog.eLogV("Nr5g CMS App Trigger Limit-isbroadcast registered: $isAppBroadcastRegistered")
                    nr5gBaseDelegate?.stopScheduler()
                    unRegisterAppReceiver()
                }
                if (it.triggerType == Nr5gConstants.TRIGGER_LIMIT_TYPE_SCREEN) {
                    EchoLocateLog.eLogV("Nr5g CMS Screen Trigger Limit-rx bus action received in Nr5gModule manager: " + it.isMaxTriggerLimitReached)
                    EchoLocateLog.eLogV("Nr5g CMS Screen Trigger Limit-isbroadcast registered: $isScreenBroadcastRegistered")
                    unRegisterScreenReceiver()
                }
            }
    }

    /**
     * Returns the random number generated
     * @return Int
     */
    private fun getRandomNumber(): Int {
        return Random.nextInt(0, 10000)
    }

    /**
     * Receiver for the alarm manager that triggers every 24 hours to reset the trigger count
     */
    class Nr5gApplicationTriggerResetReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val postTicket = PostTicket(
                Nr5gResetListenerEvent(intent)
            )
            RxBus.instance.post(postTicket)
        }
    }

    /**
     * Resets the trigger count
     */
    private fun resetTriggerCount() {
        nr5gScreenTrigger.saveScreenTriggerCount(Nr5gConstants.RESET_TRIGGER_COUNT)
        nr5gApplicationTrigger.saveAppTriggerCount(Nr5gConstants.RESET_TRIGGER_COUNT)
    }

    /**
     * This function unregisters the broadcast
     */
    private fun unRegisterAppReceiver() {
        if (isAppBroadcastRegistered) {
            context.unregisterReceiver(baseNr5gAppBroadcastReceiver)
            setAppBroadCastRegistered(false)
        }
    }

    private fun unRegisterScreenReceiver() {
        if (isScreenBroadcastRegistered) {
            context.unregisterReceiver(baseNr5gScreenBroadcastReceiver)
            setScreenBroadCastRegistered(false)
        }
    }

    /**
     * This function registers the broadcast
     */
    fun registerAppReceiver() {
        if (!isAppBroadcastRegistered) {
            EchoLocateLog.eLogV("registerAppReceiver() :: Nr5g CMS registering for App Receiver")
            val intentRegistrar = initNr5gIntentRegistration()
            registerNr5gAppActions(intentRegistrar)
            setAppBroadCastRegistered(true)
        }
    }

    fun registerScreenReceiver() {
        if (!isScreenBroadcastRegistered) {
            EchoLocateLog.eLogV("registerScreenReceiver() :: Nr5g CMS registering for screen Receiver")
            val intentRegistrar = initNr5gIntentRegistration()
            registerNr5gScreenActions(intentRegistrar)
            setScreenBroadCastRegistered(true)
        }
    }

    /**
     * Sets the boolean to true if the broadcast is registered
     * @param flag: Boolean
     */
    @VisibleForTesting
    fun setAppBroadCastRegistered(flag: Boolean) {
        isAppBroadcastRegistered = flag
    }

    @VisibleForTesting
    fun setScreenBroadCastRegistered(flag: Boolean) {
        isScreenBroadcastRegistered = flag
    }

    /**
     * Returns true/false based on the broadcast registered state
     * @return [Boolean]
     */
    fun isAppBroadCastRegistered(): Boolean {
        return isAppBroadcastRegistered
    }

    fun isScreenBroadCastRegistered(): Boolean {
        return isScreenBroadcastRegistered
    }

    /**
     * Tells if the nr5g manager is up and running.
     */
    fun isManagerInitialized(): Boolean {
        return isDataCollectionInitialized
    }

    /**
     * Gets the 5g configuration based on the supported type[SA5G or NR5G]
     */
    private fun get5gConfig(): Any? {
        return when {
            isSa5gSupported -> ConfigProvider.getInstance(context)
                .getConfigurationForKey(ConfigKey.SA5G, context)
            isNsa5gSupported -> ConfigProvider.getInstance(context)
                .getConfigurationForKey(ConfigKey.NR5G, context)
            else -> null
        }
    }

}
