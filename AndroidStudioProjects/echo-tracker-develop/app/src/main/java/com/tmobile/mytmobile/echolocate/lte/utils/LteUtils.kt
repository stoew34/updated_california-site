package com.tmobile.mytmobile.echolocate.lte.utils

import android.content.Intent
import androidx.annotation.Nullable
import com.tmobile.mytmobile.echolocate.communicationbus.rxbus.PostTicket
import com.tmobile.mytmobile.echolocate.communicationbus.rxbus.RxBus
import com.tmobile.mytmobile.echolocate.lte.lteevents.LogcatListenerEvent
import com.tmobile.mytmobile.echolocate.lte.utils.logcat.LogcatListener
import com.tmobile.mytmobile.echolocate.lte.utils.LteIntents.APP_STATE_KEY
import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings
import android.telephony.TelephonyManager
import android.text.format.DateUtils
import com.google.gson.Gson
import com.tmobile.myaccount.events.diagnostics.pojos.collector.event.ClientSideEvent
import com.tmobile.myaccount.events.diagnostics.pojos.collector.event.eventdata.BaseEventData
import com.tmobile.mytmobile.echolocate.BuildConfig
import com.tmobile.mytmobile.echolocate.lte.model.LteSingleSessionReport
import com.tmobile.mytmobile.echolocate.scheduler.events.ScheduledJobCompletedEvent
import com.tmobile.mytmobile.echolocate.utils.ELDeviceUtils
import com.tmobile.mytmobile.echolocate.utils.EchoLocateLog
import com.tmobile.pr.androidcommon.system.SystemService
import java.util.*
import kotlin.math.min


class LteUtils {
    companion object {
        private const val ECHO_LOCATE_LTE_EVENT_TYPE = "diagnosticsapp.lte.rawdata"
        /**
         * TMOPROXY_PACKAGE_NAME
         *
         * Package name for TMOProxy build
         */
        const val TMOPROXY_PACKAGE_NAME = "com.tmobile.pr.mytmobile"

        /**
         * TWENTY_ONE_SECONDS
         */
        const val TWENTY_ONE_SECONDS = 21 * DateUtils.SECOND_IN_MILLIS

        /**
         *  fun getIMEI
         *      getter method for device imei
         *  @param context
         *
         *  @return String
         *      this is the imei as a string,
         *      cases where there is no imei returns a ""
         */
        @SuppressLint("MissingPermission", "HardwareIds")
        fun getImei(context: Context): String {

            val imei: String?
            val tm: TelephonyManager =
                SystemService.getTelephonyManager(context) as TelephonyManager

            imei = try {
                when {
                    ELDeviceUtils.isOreoDeviceOrHigher() -> when (tm.phoneType) {
                        TelephonyManager.PHONE_TYPE_NONE -> ""
                        TelephonyManager.PHONE_TYPE_GSM -> tm.imei
                        TelephonyManager.PHONE_TYPE_CDMA -> tm.meid
                        else -> ""
                    }
                    else -> tm.imei ?: Settings.Secure.getString(
                        context.contentResolver,
                        Settings.Secure.ANDROID_ID
                    )
                }
            } catch (ex: Exception) {
                // Do nothing
                ""
            }
            return imei ?: ""
        }

        fun isTmoAppVersionBlackListed(context: Context, appVersion: String?): Boolean {
            return checkIfTmoAppVersionIsBlackListed(context, appVersion)
        }

        /**
         *    This function checks the TMOApp version against blacklistedTmoAppVersion
         *    from config file
         *  @return Boolean
         *    True : If the current TMOApp version is equal or less than specified version
         *    False : If the current TMOApp version is greater than or equal to the specified version
         */
        private fun checkIfTmoAppVersionIsBlackListed(
            context: Context,
            appVersion: String?
        ): Boolean {
            val currVersionTmo = getApplicationVersionName(context, TMOPROXY_PACKAGE_NAME)
            if (appVersion.isNullOrBlank() || currVersionTmo.isEmpty())
                return false
            val versionCodes = appVersion.split(".")
            val currVersionCodes = currVersionTmo.split(".")

            // To avoid IndexOutOfBounds while accessing elements from list
            val maxIndex: Int = min(versionCodes.size, currVersionCodes.size)
            for (index in 0 until maxIndex) {
                val currValue = Integer.valueOf(currVersionCodes[index])
                val specifiedValue = Integer.valueOf(versionCodes[index])
                if (currValue < specifiedValue) {
                    // current version of TMOApp is smaller
                    return true
                } else if (currValue > specifiedValue) {
                    // current version of TMOApp is greater
                    return false
                }
            }

            // Current TMOApp version matches with the specified version
            return false
        }

        /**
         * This function is used to get the TMO application version name
         */
        fun getApplicationVersionName(context: Context, packageName: String): String {
            try {
                val packageInfo = context.packageManager.getPackageInfo(packageName, 0)
                // If there is any exception, it will get handled in callee
                return if (packageInfo == null) {
                    EchoLocateLog.eLogE("Error getting application's version name")
                    ""
                    // throw java.lang.IllegalStateException("Application should know it's own package!")
                } else {
                    if (packageInfo.versionName.contains("-")) {
                        packageInfo.versionName.substring(0, packageInfo.versionName.indexOf('-'))
                    } else {
                        packageInfo.versionName
                    }
                }
            } catch (e: Exception) {
                return ""
            }
        }


        /**
         *  fun checkTacInList
         *      checks if the list has device's TAC number
         *  @param context
         *  @param tacList
         *
         *  @return Boolean
         *      True : If TAC exist in the list
         *      False : If TAC does not exist in the list
         */
        fun checkTacInList(context: Context, tacList: List<String>?): Boolean {
            if (tacList.isNullOrEmpty()) return false
            val deviceTAC = getTac(context)
            if (deviceTAC.isNullOrEmpty()) return false
            return tacList.contains(deviceTAC)
        }

        private fun getTac(context: Context): String? {
            var imei: String? = ""
            try {
                imei = getImei(context)
                if (imei.isNotEmpty()) {
                    // TAC is first 8 digit of IMEI
                    // Ref : https://www.3gpp.org/ftp/tsg_sa/TSG_SA/TSGS_16/Docs/PDF/SP-020237.pdf
                    if (imei.length > 8) {
                        imei = imei.substring(0, 8)
                        return imei
                    } else {
                        return imei
                    }
                }
            } catch (e: SecurityException) {
                EchoLocateLog.eLogE("Unable to retrieve IMEI ${e.localizedMessage} ")
            }
            return imei
        }

        /**
         * This function will broadcast the job completed status using [ScheduledJobCompletedEvent]
         * If workId is null, it means the job is not requested by scheduler
         */
        fun sendJobCompletedToScheduler(androidWorkId: String?, moduleName: String?) {
            if (!androidWorkId.isNullOrEmpty()) {
                /** Scheduler job is completed */
                val postJobCompletedTicket = PostTicket(
                    ScheduledJobCompletedEvent(androidWorkId!!)
                )
                RxBus.instance.post(postJobCompletedTicket)
                EchoLocateLog.eLogD("Diagnostic : ScheduleWorker job completed for $moduleName : $androidWorkId")
            }
        }

        /**
         * This function receives the intent from receiver and using LTEApplications and returns the application name
         */
        @Nullable
        fun isPackageEligibleForDataCollection(
            intent: Intent,
            packagesEnabled: List<String>
        ): LTEApplications? {
            if (!intent.hasExtra(LteIntents.APP_PACKAGE)) {
                return null
            }
            val appPackage = intent.getStringExtra(LteIntents.APP_PACKAGE)
            if (packagesEnabled.contains(appPackage)) {
                for (application in LTEApplications.values()) {
                    if (application.getPackageName().equals(appPackage)) {
                        return application
                    }
                }
            }
            return null
        }


        /**
         * This function checks the application state,and returns the state
         */
        fun extractApplicationStateFromIntent(intent: Intent): ApplicationState {
            return if (!intent.hasExtra(APP_STATE_KEY)) {
                ApplicationState.UNSUPPORTED
            } else {
                val appState = intent.getStringExtra(APP_STATE_KEY)

                // The application only supports focus gain/loss states
                if (appState == ApplicationState.FOCUS_GAIN.name || appState == ApplicationState.FOCUS_LOSS.name) {
                    ApplicationState.valueOf(appState)
                } else {
                    ApplicationState.UNSUPPORTED
                }
            }

        }

        /**
         * This function is responsible to post the actions to lte delegates
         */
        fun postIntent(
            item: LogcatlistenerItem,
            line: String
        ) {
            val intent = Intent(item.resultAction)
            intent.putExtra(LogcatListener.TRIGGER_ID_EXTRA, item.id)
            intent.putExtra(LogcatListener.LINE_EXTRA, line)
            val postTicket = PostTicket(LogcatListenerEvent(intent))
            RxBus.instance.post(postTicket)

        }

        fun createClientSideEvent(jsonPayload: String): ClientSideEvent {
            val gson = Gson()
            var clientSideEvent: ClientSideEvent? = null
            val lteReport =
                gson.fromJson(jsonPayload, LteSingleSessionReport::class.java) as BaseEventData
            clientSideEvent = ClientSideEvent().withEventData(lteReport)
            clientSideEvent.withEventType(ECHO_LOCATE_LTE_EVENT_TYPE)
            clientSideEvent.withClientVersion(BuildConfig.VERSION_NAME)
            clientSideEvent.withTimestamp(Date())
            return clientSideEvent
        }
    }

}