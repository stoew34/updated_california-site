package com.tmobile.mytmobile.echolocate.appstart.managing

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.gson.Gson
import com.tmobile.echolocate.autoupdate.UpdateEvent
import com.tmobile.echolocate.autoupdate.database.DatabaseProvider
import com.tmobile.echolocate.heartbeatsdk.heartbeat.HeartBeatSdk
import com.tmobile.mytmobile.echolocate.BuildConfig
import com.tmobile.mytmobile.echolocate.analytics.AnalyticsModuleProvider
import com.tmobile.mytmobile.echolocate.authentication.provider.AuthenticationProvider
import com.tmobile.mytmobile.echolocate.authentication.provider.ITokenReceivedListener
import com.tmobile.mytmobile.echolocate.autoupdate.AutoUpdateManager
import com.tmobile.mytmobile.echolocate.autoupdate.AutoUpdatePreference
import com.tmobile.mytmobile.echolocate.configuration.events.analytics.ELAnalyticActions
import com.tmobile.mytmobile.echolocate.configuration.events.analytics.ELAnalyticActions.EL_HEARTBEAT_SDK_FAILURE
import com.tmobile.mytmobile.echolocate.configuration.events.analytics.ELAnalyticActions.EL_HEARTBEAT_SDK_SUCCESS
import com.tmobile.mytmobile.echolocate.configuration.events.analytics.ELAnalyticsEvent
import com.tmobile.mytmobile.echolocate.configuration.events.analytics.ELModulesEnum
import com.tmobile.mytmobile.echolocate.configuration.events.configurationevents.AllConfigsEvent
import com.tmobile.mytmobile.echolocate.configuration.events.configurationevents.AutoUpdateConfigEvent
import com.tmobile.mytmobile.echolocate.configuration.events.configurationevents.DsdkHandshakeEvent
import com.tmobile.mytmobile.echolocate.communicationbus.rxbus.PostTicket
import com.tmobile.mytmobile.echolocate.communicationbus.rxbus.RxBus
import com.tmobile.mytmobile.echolocate.configuration.ConfigKey
import com.tmobile.mytmobile.echolocate.configmanager.ConfigProvider
import com.tmobile.mytmobile.echolocate.configuration.model.Config
import com.tmobile.mytmobile.echolocate.coverage.CoverageModuleProvider
import com.tmobile.mytmobile.echolocate.dsdkHandshake.DsdkHandshakeManager
import com.tmobile.mytmobile.echolocate.locationmanager.LocationManager
import com.tmobile.mytmobile.echolocate.lte.LteModuleProvider
import com.tmobile.mytmobile.echolocate.nr5g.Nr5gModuleProvider
import com.tmobile.mytmobile.echolocate.reportingmanager.ReportProvider
import com.tmobile.mytmobile.echolocate.schedulermanager.SchedulerComponent
import com.tmobile.mytmobile.echolocate.userconsent.ConsentRequestProvider
import com.tmobile.mytmobile.echolocate.userconsent.userconsentevents.UserConsentFlagsParameters
import com.tmobile.mytmobile.echolocate.utils.EchoLocateLog
import com.tmobile.mytmobile.echolocate.appstart.utils.AppStartUtils
import com.tmobile.mytmobile.echolocate.authentication.utils.TokenSharedPreference
import com.tmobile.mytmobile.echolocate.communicationbus.rxbus.SubscribeTicket
import com.tmobile.mytmobile.echolocate.configuration.events.NonFatalCrashEvent
import com.tmobile.mytmobile.echolocate.utils.SingletonHolder
import com.tmobile.mytmobile.echolocate.networkmanager.NetworkProvider
import com.tmobile.mytmobile.echolocate.utils.FirebaseUtils
import com.tmobile.mytmobile.echolocate.variant.Constants
import com.tmobile.mytmobile.echolocate.voice.VoiceModuleProvider
import com.tmobile.pr.androidcommon.log.TmoLog
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.concurrent.Semaphore
import java.util.concurrent.TimeUnit


class AppStartManager private constructor(val context: Context) {

    companion object : SingletonHolder<AppStartManager, Context>(::AppStartManager)

    val ELKeepAlive: String = "AppStartModule"
    private var userConsentFlagsParameters: UserConsentFlagsParameters? = null
    private var isPanicModeDisabled: Boolean = true
    private val SUCCESS_RESPONSE = "200"
    private var heartBeatSdkAnalyticsDisposal: Disposable? = null

    /* initialize AppStartManager
    *       -get dat
    *       - get configuration
    *       - get user consents
    *       - configuration and user consent determine when data collection starts/stops
    * */
    @SuppressLint("CheckResult")
    fun initializeManager() {

        setupFlavorData()

        if (BuildConfig.DEBUG) {
            TmoLog.setLevel(Log.VERBOSE)
            NetworkProvider.getInstance(context).setLogLevel(Log.VERBOSE)
        } else {
            TmoLog.setLevel(Log.INFO)
            NetworkProvider.getInstance(context).setLogLevel(Log.INFO)
            // All the records should be deleted, before we start new session of the app
            deleteRecordsFromNetworkDB(context)
        }

        // TODO : Add condition to enable Crashlytics in future
        val crashlytics = FirebaseCrashlytics.getInstance()
        crashlytics.setCrashlyticsCollectionEnabled(true)
        subscribeToNonFatalCrashLogs()

        deleteKeepAliveSchedulerWorkIds()

        AuthenticationProvider.getInstance(context).getDatUpdate().subscribe {
            EchoLocateLog.eLogD("Diagnostic : Got dat and passing " + it.datToken)
            EchoLocateLog.eLogI("Authentication token received")
            HeartBeatSdk.getInstance(context).setDat(it.datToken)
        }
        // 1) fetch dat token from authentication module for echoapp, set dat for heartbeat
        getDAT()

        AutoUpdateManager.getInstance().init(context)



        //fetch Config via ConfigProvider
        val configProvider = ConfigProvider.getInstance(context)
        configProvider.setClientAppPreferences(TokenSharedPreference.tokenObject,
            Constants.CONFIGURATION_URL, Constants.ENVIRONMENT, BuildConfig.DEBUG)
        configProvider.initConfigModule(context)

        //setting client app details
        ReportProvider.Builder(context).setToken(TokenSharedPreference.tokenObject)
            .setClientAppReportUrl(Constants.DIA_REQUEST_URL)
            .setClientAppEnvironment(Constants.ENVIRONMENT)
            .setClientAppBuildTypeDebug(BuildConfig.DEBUG)
            .setClientAppVersionName(BuildConfig.VERSION_NAME)
            .setClientAppVersionCode(BuildConfig.VERSION_CODE.toString())
            .setClientAppApplicationId(BuildConfig.APPLICATION_ID)
            .setClientAppSaveToFile(Constants.SAVE_DATA_TO_FILE).build()

        // Start analytics module, as application is started.
        // Later it will be controlled based on the configuration value
        startAnalyticsModule()
        // 3) fetch UserConsents via ConsentRequestProvider
        userConsentFlagsParameters =
            if (context.applicationContext.packageName != AppStartUtils.TMOPROXY_PACKAGE_NAME) {
                ConsentRequestProvider.getInstance(context).getUserConsentFlags()
                    ?.userConsentFlagsParameters
            } else {
                // If EchoApp is using proxy package name, same as TMOApp, we won't be able to get the consent flags
                // And so, we will add those flags as true.
                // Note that, this is not an end user situation, and applicable only for internal testing
                UserConsentFlagsParameters(
                    isAllowedDeviceDataCollection = true,
                    isAllowedIssueAssist = true,
                    isAllowedPersonalizedOffers = false
                )
            }

        if (userConsentFlagsParameters == null) {
            userConsentFlagsParameters = UserConsentFlagsParameters() // Default values are false
        }

        // If Issue Assist is allowed, only then allow application to collect location.
        if (userConsentFlagsParameters!!.isAllowedIssueAssist) {
            LocationManager.getInstance(context).initLocationModule()
        }

        configProvider.getConfigUpdates(ConfigKey.AUTOUPDATE, context, true).subscribe({
            val autoupdateConfig = (it as AutoUpdateConfigEvent).configValue

            //TODO try-catch was added to handle crash from AutoUpdate lib, need remove after proper fix
            try {
                AutoUpdateManager.getInstance().setConfig(Gson().toJson(autoupdateConfig).toString())
            }  catch (e: Exception) {

                crashlytics.log("Crash occurred in AutoUpdate version 1.0.0: ${e.localizedMessage}")
                crashlytics.recordException(Throwable(Exception("Crash occurred in AutoUpdate version 1.0.0")))
                crashlytics.sendUnsentReports()
                EchoLocateLog.eLogE("Diagnostic : Crash occurred in AutoUpdate version 1.0.0 -> ${e.localizedMessage}")
            }

        }, {
            EchoLocateLog.eLogE("error: " + it.localizedMessage)
        })

        configProvider.getConfigUpdates(ConfigKey.DSDKHS, context, true).subscribe({
            val dsdkConfig = (it as DsdkHandshakeEvent).configValue
            DsdkHandshakeManager.getInstance(context).processHandshakeConfig(dsdkConfig)
        }, {
            EchoLocateLog.eLogE("error: " + it.localizedMessage)
        })

        val configObservable = configProvider.getConfigUpdates(ConfigKey.ALL, context, true)

        startAllModules(configObservable)

        // Subscribe to update location provider based on user consent for issue assist
        val userConsentObservable =
            ConsentRequestProvider.getInstance(context).getUserConsentUpdates()
        userConsentObservable.subscribe({
            // Note: Analytics module will not start or stop, based on user consent flag

            EchoLocateLog.eLogD("Diagnostic : User consented ${it.userConsentFlagsParameters.isAllowedDeviceDataCollection}")
            EchoLocateLog.eLogD("Diagnostic : Issue assist set to : ${it.userConsentFlagsParameters.isAllowedIssueAssist}")

            // For proxy package, we always get the consent flag as false,
            // so, no action is required for proxy package
            if (context.applicationContext.packageName != AppStartUtils.TMOPROXY_PACKAGE_NAME) {
                // Check if the user consent flag is changed
                if (userConsentFlagsParameters != null &&
                    userConsentFlagsParameters!!.isAllowedDeviceDataCollection != it.userConsentFlagsParameters.isAllowedDeviceDataCollection
                ) {

                    userConsentFlagsParameters?.isAllowedDeviceDataCollection =
                        it.userConsentFlagsParameters.isAllowedDeviceDataCollection
                    if (it.userConsentFlagsParameters.isAllowedDeviceDataCollection) {
                        if (isPanicModeDisabled) {
                            EchoLocateLog.eLogD("Diagnostic : Starting all modules as user allowed")
                            EchoLocateLog.eLogI("User consented")
                            val allConfig = configProvider.getConfigurationForKey(
                                ConfigKey.ALL,
                                context
                            ) as Config
                            startDataCollectionAllModules(allConfig)
                        } else {
                            EchoLocateLog.eLogD("Diagnostic : User allowed to start all modules but panic mode is ON")
                            EchoLocateLog.eLogI("User consented, pm")
                            stopReportingModule()
                            stopDataCollectionAllModules()
                        }
                    } else {
                        EchoLocateLog.eLogD("Diagnostic : Stopping all modules as user denied")
                        EchoLocateLog.eLogI("User not consented")
                        stopDataCollectionAllModules()
                    }
                }

                // Check if the flag for issue assist is changed
                if (userConsentFlagsParameters != null &&
                    userConsentFlagsParameters!!.isAllowedIssueAssist != it.userConsentFlagsParameters.isAllowedIssueAssist
                ) {

                    userConsentFlagsParameters!!.isAllowedIssueAssist =
                        it.userConsentFlagsParameters.isAllowedIssueAssist

                    if (it.userConsentFlagsParameters.isAllowedIssueAssist) {
                        LocationManager.getInstance(context).initLocationModule()
                    } else {
                        LocationManager.getInstance(context).stopLocationModule()
                    }
                }
            }
        }, {
            EchoLocateLog.eLogE("error: " + it.localizedMessage)
        })

        autoUpdateEventValidation()
    }

    /**
     *  It subscribes to non-fatal crash event, all the library modules can post the crash.
     */
    private fun subscribeToNonFatalCrashLogs() {
        val subscribeTicket = SubscribeTicket(RxBus.SubjectType.PUBLISH_SUBJECT)
        RxBus.instance.register<NonFatalCrashEvent>(subscribeTicket).subscribe {
            FirebaseUtils.logCrashToFirebase(
                it.logMessage, it.localizedMessage, it.localizedMessage
            )
        }
    }

    @SuppressLint("CheckResult")
    private fun startAllModules (configObservable: Observable<*>) {
        configObservable.subscribe({
            val allConfig = it as AllConfigsEvent
            isPanicModeDisabled = !AppStartUtils.checkPanicMode(allConfig)
            EchoLocateLog.eLogD("Diagnostic : Panic mode updated to : ${allConfig.configValue.panicMode}")

            if (isPanicModeDisabled) {
                // start the analytics module first as panic mode disabled
                startAnalyticsModule()

                if (context.applicationContext.packageName == AppStartUtils.TMOPROXY_PACKAGE_NAME ||
                    (userConsentFlagsParameters != null && userConsentFlagsParameters!!.isAllowedDeviceDataCollection)
                ) {
                    EchoLocateLog.eLogD("Diagnostic : Starting all modules due to panic mode turned OFF")
                    EchoLocateLog.eLogI("User consented")
                    startDataCollectionAllModules(allConfig.configValue)
                } else {
                    EchoLocateLog.eLogD("Diagnostic : panic mode turned OFF, but user consent is denied")
                    EchoLocateLog.eLogI("User not consented")
                    stopDataCollectionAllModules()
                }
            } else {
                EchoLocateLog.eLogD(
                    "Diagnostic : Stopping all modules due to panic mode turned ON",
                    System.currentTimeMillis()
                )
                EchoLocateLog.eLogI("pm is on")
                // Stop the analytics module as panic mode is true
                stopAnalyticsModule()
                stopReportingModule()
                stopDataCollectionAllModules()
            }
        }, {
            EchoLocateLog.eLogE("error: " + it.localizedMessage)
        })
    }

    private fun setupFlavorData() {
        if (AppStartUtils.isFlavorChanged()) {
            // Note that, even though the flavor is changed, don't set it here.
            // We need to check the same in AutoUpdate module, to resume/cancel the update,
            // and will set the preference there

            // Clear the configuration data, so that you can download the proper config for all modules
            ConfigProvider.getInstance(context).clearConfigPreferences()
        }
    }

    /**
     * This function is to check if the auto update is successful
     */
    private fun autoUpdateEventValidation() {
        val oldVersion = AutoUpdatePreference.appVersion

        if (oldVersion.isNotEmpty()) {
//            val pInfo: PackageInfo =
//                context.packageManager.getPackageInfo(context.packageName, 0)
//            val newVersion: String = pInfo.versionName
            if (AppStartUtils.checkForUpdate(oldVersion, BuildConfig.VERSION_NAME)) {
                EchoLocateLog.eLogD("Auto update success")
                postAnalyticsEvent(
                    BuildConfig.VERSION_NAME,
                    ELAnalyticActions.EL_APP_VERSION_UPDATE_SUCCEEDED
                )
            } else {
                EchoLocateLog.eLogD("Auto Update failed")
                var errorDescription = ""
                val autoUpdateDb = DatabaseProvider(context)
                val events = autoUpdateDb.allEvents
                for (event in events) {
                    // we are trying to get only the last entry that is failed so iterating the whole list
                    // We don't know the updateId but we do know that the last entry will be the failed one
                    if (event.status == UpdateEvent.Action.FAILED.name) {
                        errorDescription = event.description
                    }
                }
                postAnalyticsEvent(
                    "$oldVersion - $errorDescription",
                    ELAnalyticActions.EL_APP_VERSION_UPDATE_FAILED
                )
            }
        }
    }

    /**
     * This function is used to post the auto update analytics event to analytics manager
     * @param status-checks the status of cms config
     * @param payload-stores the status code based on api status
     *
     */
    private fun postAnalyticsEvent(payload: String, status: ELAnalyticActions) {

        EchoLocateLog.eLogD("Auto Update Event version $payload, Event info ${status.name}")
        // resetting the appVersion in preference after the validation
        AutoUpdatePreference.appVersion = ""
        val analyticsEvent = ELAnalyticsEvent(
            ELModulesEnum.AUTOUPDATE,
            status,
            payload
        )
        analyticsEvent.timeStamp = System.currentTimeMillis()

        val postAnalyticsTicket = PostTicket(analyticsEvent)
        RxBus.instance.post(postAnalyticsTicket)
    }

    /**
     *  This function subscribes heartbeat sdk to capture number of times app checked in with server
     */
    private fun subscribeForHeartBeatSdk() {
        if (heartBeatSdkAnalyticsDisposal == null) {
            heartBeatSdkAnalyticsDisposal =
                HeartBeatSdk.getInstance(context).networkResponseObservable.subscribe {
                    if (it.isSuccess) {
                        postAnalyticsEventForHeartBeat(SUCCESS_RESPONSE, EL_HEARTBEAT_SDK_SUCCESS)
                    } else {
                        postAnalyticsEventForHeartBeat(it.error.toMsg(), EL_HEARTBEAT_SDK_FAILURE)
                    }
                }
        }
    }

    /**
     * This function is used to post the heartbeat config event to analytics manager
     * @param status-checks the status of heartbeat sdk
     * @param payload-stores the status code or error message based on api status
     */
    private fun postAnalyticsEventForHeartBeat(payload: String, status: ELAnalyticActions) {
        val analyticsEvent = ELAnalyticsEvent(ELModulesEnum.HEARTBEAT, status, payload)
        analyticsEvent.timeStamp = System.currentTimeMillis()
        val postAnalyticsTicket = PostTicket(analyticsEvent)
        RxBus.instance.post(postAnalyticsTicket)
    }

    /**
     * get DAT/authentication token
     *    gets and saves DAT token locally.
     * @return String?
     */
    private fun getDAT(): String? {
        val semaphore: Semaphore = Semaphore(0)
        var datToken: String? = null
        GlobalScope.launch(Dispatchers.Main) {
            AuthenticationProvider.getInstance(context).getToken(object :
                ITokenReceivedListener {
                override fun onReceivedToken(token: String) {
                    datToken = token
                    semaphore.release()
                }
            })
        }

        semaphore.tryAcquire(10, TimeUnit.SECONDS)
        return datToken
    }

    /**
     *  starts data collection on all modules for echoapp
     */
    private fun startDataCollectionAllModules(allConfig: Config) {
        synchronized(this) {
            EchoLocateLog.eLogD("Diagnostic : Starting EL data collection modules")

//            startHeartBeatModule(allConfig)

            startReportingModule()

            startVoiceDataCollectionModule()

//            startNr5gDataCollectionModule()

//            startCoverageDataCollectionModule()

//            startLteDataCollectionModule()
        }
    }

    /**
     *  stop data collection for all modules in echo app
     */
    private fun stopDataCollectionAllModules() {
        synchronized(this) {
            EchoLocateLog.eLogD("Diagnostic : Stopping EL data collection modules")

            HeartBeatSdk.getInstance(context).stopDataCollection()

            stopVoiceDataCollectionModule()

            stopLteDataCollectionModule()

            stopNr5gDataCollectionModule()

            stopCoverageDataCollectionModule()
        }
    }

    /**
     * starts Heartbeat Module for echoapp
     */
    private fun startHeartBeatModule(allConfig: Config) {
        EchoLocateLog.eLogD("Diagnostic : Start HeartBeat Module")

        val heartbeatSdk = HeartBeatSdk.getInstance(context)
        // Update the config for heartbeat
        heartbeatSdk.setConfig(Gson().toJson(allConfig).toString())

        // Add the environment and request URL for heartbeat module
        heartbeatSdk.setHeartbeatData(Constants.ENVIRONMENT, Constants.DIA_REQUEST_URL)

        // When flavor is changed from regular to dolphin and then back to regular,
        // Diagnostics app might have the authentication token for old flavor
        // We are resetting the dat token in HB when flavor is changed, we need to
        // add the token for rebounding to main/regular flavor
        heartbeatSdk.setDat(TokenSharedPreference.tokenObject)
    }

    /**
     * starts Analytics Module for echoapp
     */
    private fun startAnalyticsModule() {
        EchoLocateLog.eLogD("Diagnostic : Start Analytics Module")

        AnalyticsModuleProvider.getInstance(context).initAnalyticsModule(context)
        subscribeForHeartBeatSdk()
    }

    /**
     *  stop Analytics Module for echoapp
     */
    private fun stopAnalyticsModule() {
        EchoLocateLog.eLogD("Diagnostic : Stop Analytics Module")

        AnalyticsModuleProvider.getInstance(context).stopAnalyticsModule()
        heartBeatSdkAnalyticsDisposal?.dispose()
        heartBeatSdkAnalyticsDisposal = null
    }

    /**
     *  starts Reporting Module for echoapp
     */
    private fun startReportingModule() {
        EchoLocateLog.eLogD("Diagnostic : Starting Reporting Module")

        ReportProvider.getInstance(context).initReportingModule()
    }

    /**
     *  stop Reporting Module for echoapp
     */
    private fun stopReportingModule() {
        EchoLocateLog.eLogD("Diagnostic : Stop Reporting Module")

        ReportProvider.getInstance(context).stopReportingModule()
    }

    /**
     *  starts Voice data collection module for echoapp
     */
    private fun startVoiceDataCollectionModule() {
        EchoLocateLog.eLogD("Diagnostic : Requesting to init Voice module")

        VoiceModuleProvider.getInstance(context).initVoiceModule(context)
    }

    /**
     *  stop Voice data collection module for echoapp
     */
    private fun stopVoiceDataCollectionModule() {
        EchoLocateLog.eLogD("Diagnostic : Stop Voice data collection module")

        VoiceModuleProvider.getInstance(context).stopVoiceModule()
    }

    /**
     *  starts Lte data collection module for echoapp
     */
    private fun startLteDataCollectionModule() {
        EchoLocateLog.eLogD("Diagnostic : Starting Lte data collection module")
        EchoLocateLog.eLogI("Start Lte")
        LteModuleProvider.getInstance(context).initLteModule(context)
    }

    /**
     *  stop Lte data collection module for echoapp
     */
    private fun stopLteDataCollectionModule() {
        EchoLocateLog.eLogD("Diagnostic : Stop Lte data collection module")

        LteModuleProvider.getInstance(context).stopLteModule()
    }

    /**
     *  starts Nr5g data collection module for echoapp
     */
    private fun startNr5gDataCollectionModule() {
        EchoLocateLog.eLogD("Diagnostic : Starting Nr5g data collection module")
        EchoLocateLog.eLogI("Start five")
        Nr5gModuleProvider.getInstance(context).initNr5gModule(context)
    }

    /**
     *  stop Nr5g data collection module for echoapp
     */
    private fun stopNr5gDataCollectionModule() {
        EchoLocateLog.eLogD("Diagnostic : Stop Nr5g data collection module")

        Nr5gModuleProvider.getInstance(context).stopNr5gModule()
    }

    /**
     *  starts Coverage data collection module for echoapp
     */
    private fun startCoverageDataCollectionModule() {
        EchoLocateLog.eLogD("Diagnostic : Starting Coverage data collection module")

        CoverageModuleProvider.getInstance(context).initCoverageModule(context)
    }

    /**
     *  stop Coverage data collection module for echoapp
     */
    private fun stopCoverageDataCollectionModule() {
        EchoLocateLog.eLogD("Diagnostic : Stop Coverage data collection module")

        CoverageModuleProvider.getInstance(context).stopCoverageModule()
    }

    /**
     * delete existing keepAliveScheduler worker ids in db for existing devices in market
     */
    private fun deleteKeepAliveSchedulerWorkIds() {
        val schedulerComponent = SchedulerComponent.getInstance(context) as SchedulerComponent
        val workIdList = schedulerComponent.getWorkId(context, ELKeepAlive)

        // check if workIdList is present in db
        if (workIdList.isNotEmpty()) {
            for (id in workIdList) {
                schedulerComponent.deleteWorkByTag(id.toString())
            }
        }
    }

    /**
     * Clean up network database from previous session
     */
    private fun deleteRecordsFromNetworkDB(ctx: Context) {
        val networkProvider = NetworkProvider.getInstance(context) as NetworkProvider
        networkProvider.cleanOldFailedNetworkRequests(ctx)
    }

    private fun throwDummyException() {
        throw Exception("Exception for release build test")
    }
}

