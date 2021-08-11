package com.tmobile.mytmobile.echolocate.autoupdate

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.text.TextUtils
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.tmobile.echolocate.autoupdate.AutoUpdateSDK
import com.tmobile.echolocate.autoupdate.Builder
import com.tmobile.echolocate.autoupdate.UpdateEvent
import com.tmobile.mytmobile.echolocate.BuildConfig
import com.tmobile.mytmobile.echolocate.configuration.events.analytics.ELAnalyticActions
import com.tmobile.mytmobile.echolocate.configuration.events.analytics.ELAnalyticsEvent
import com.tmobile.mytmobile.echolocate.configuration.events.analytics.ELModulesEnum
import com.tmobile.mytmobile.echolocate.communicationbus.rxbus.PostTicket
import com.tmobile.mytmobile.echolocate.communicationbus.rxbus.RxBus
import com.tmobile.mytmobile.echolocate.configuration.model.AutoUpdate
import com.tmobile.mytmobile.echolocate.utils.EchoLocateLog
import com.tmobile.mytmobile.echolocate.autoupdate.utils.AutoUpdateUtils


class AutoUpdateManager private constructor() {

    companion object {
        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var INSTANCE: AutoUpdateManager? = null
        private var initialized = false

        /***
         * creates AutoUpdateManager instance
         */
        fun getInstance(): AutoUpdateManager {
            return INSTANCE ?: synchronized(this) {
                val instance = AutoUpdateManager()
                INSTANCE = instance
                instance
            }
        }
    }

    private var autoUpdateSDK: AutoUpdateSDK? = null
    private var config: AutoUpdate? = null
    private var context: Context? = null
    private var installUpdate = false
    /**
     * @return Last known update Status as [UpdateEvent.Action]
     */
    /**
     * @param updateStatus [UpdateEvent.Action]
     */
    var updateStatus: UpdateEvent.Action? = null
    /**
     * @return [OnUpdateChangedListener]
     */
    /**
     * @param onUpdateChangedListener [OnUpdateChangedListener]
     */
    var onUpdateChangedListener: OnUpdateChangedListener? = null

    /**
     * Checks if an updated version is available via config
     * @return True if there is an updated version in config.
     * */
    val isUpdateAvailable: Boolean
        get() {
            return AutoUpdateUtils.checkForUpdate(BuildConfig.VERSION_NAME, (config?.appVersion ?: ""))
        }

    /**
     * Helper method to install update
     */
    fun installUpdate() {
        autoUpdateSDK?.install(null)
        postAnalyticsEvent((config?.appVersion ?: ""), ELAnalyticActions.EL_APP_VERSION_UPDATE_REQUESTED)
    }

    /**
     * Helper method to cancel update
     */
    fun cancelUpdate() {
        autoUpdateSDK?.cancel()
    }

    /**
     * Initialize the [AutoUpdateManager]
     *
     * @param context [Context]
     */
    fun init(context: Context) {
        this.context = context
        if (!initialized) {
            initialized = true
            autoUpdateSDK = AutoUpdateSDK.getInstance()
            autoUpdateSDK?.init(context)

            autoUpdateSDK?.setOnUpdateEventListener { updateEvent ->
                updateStatus = updateEvent.action
                EchoLocateLog.eLogD("onUpdateEvent: " + updateStatus)
                if (UpdateEvent.Action.INSTALL_READY == updateEvent.action) {
                    EchoLocateLog.eLogD("onUpdateEvent: Install ready")
                    //during testing with ui installUpdate will be false and hence won't install
                    // right away. For other conditions update gets installed on the fly
                    if (installUpdate) {
                        autoUpdateSDK?.install(null)
                    }
                }

                onUpdateChangedListener?.onUpdateChanged(updateEvent)
            }

            //Resume is called every time AutoUpdateManager is initialized to make sure that
            // there is no pending update action from last app launch. If there
            // is any pending action we should continue the action
            if (AutoUpdateUtils.isFlavorChanged()) {
                cancelUpdate()
                AutoUpdateUtils.updateAppFlavor()
                EchoLocateLog.eLogD("AutoUpdateEcho : cancelling the update if any")
            } else {
                EchoLocateLog.eLogD("AutoUpdateEcho : resuming the update if any")
                autoUpdateSDK?.resume()
            }

        }
    }

    /**
     * Save the config and update if an update is available
     *
     * @param rawConfig
     */
    fun setConfig(rawConfig: String) {
        saveConfig(rawConfig)
        updateIfAvailable()
        installUpdate = true
    }

    /**
     * Save the config
     *
     * @param rawConfig
     */
    fun saveConfig(rawConfig: String) {
        installUpdate = false
        try {
            this.config = Gson().fromJson(rawConfig, AutoUpdate::class.java)
        } catch (e: JsonSyntaxException) {
            EchoLocateLog.eLogE("error: ${e.localizedMessage}")
        }

    }

    /**
     * Invoke Autoupdate sdk to download the updated apk if there is an updated version in config
     * */
    fun updateIfAvailable() {
        if (config == null ||
            TextUtils.isEmpty(config?.sourceUrl) ||
            TextUtils.isEmpty(config?.appVersion) ||
            TextUtils.isEmpty(config?.fingerprintHash )
        ) {
            EchoLocateLog.eLogD("More info needed to initiate update")
            return
        }
        if (isUpdateAvailable) {
            var networkTypes = DownloadManager.Request.NETWORK_MOBILE
            if (!TextUtils.isEmpty(config?.connectionType) && config?.connectionType.equals(
                    "Wifi",
                    ignoreCase = true
                )
            ) {
                networkTypes = DownloadManager.Request.NETWORK_WIFI
            }
            val builder = Builder().setUri(Uri.parse(config?.sourceUrl))
                .setVersion(getAppversionCode(config?.appVersion ?: BuildConfig.VERSION_NAME))   // has to be version code (single number)
                .setHash(config?.fingerprintHash)
                .setShouldInstallAfterDownload(false)
                .setAllowedNetworkTypes(networkTypes)
            autoUpdateSDK?.update(builder)
            postAnalyticsEvent(
                (config?.appVersion ?: ""),
                ELAnalyticActions.EL_APP_VERSION_UPDATE_REQUESTED
            )
        }
    }



    /**
     * @return The calculated version code from the version name
     */
    fun getAppversionCode(appVersion: String): String {
        val appVersionArray =
            appVersion.split("\\.".toRegex())
        val versionCode =
            Integer.parseInt(appVersionArray[0]) * 10000 + Integer.parseInt(appVersionArray[1]) * 100 + Integer.parseInt(
                appVersionArray[2]
            )
        return versionCode.toString()
    }

    /**
     * This function is used to post the auto update analytics event to analytics manager
     * @param status-checks the status of cms config
     * @param payload-stores the status code based on api status
     *
     */
    private fun postAnalyticsEvent(payload: String, status: ELAnalyticActions) {
        EchoLocateLog.eLogD("Auto Update Event version $payload, Event info ${status.name}")
        AutoUpdatePreference.appVersion = payload
        val analyticsEvent = ELAnalyticsEvent(
            ELModulesEnum.AUTOUPDATE,
            status,
            payload
        )
        analyticsEvent.timeStamp = System.currentTimeMillis()

        val postAnalyticsTicket = PostTicket(analyticsEvent)
        RxBus.instance.post(postAnalyticsTicket)
    }

}