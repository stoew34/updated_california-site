package com.tmobile.mytmobile.echolocate.voice.utils

import com.google.gson.Gson
import com.tmobile.mytmobile.echolocate.BuildConfig
import com.tmobile.mytmobile.echolocate.utils.EchoLocateLog
import org.json.JSONArray
import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings
import android.telephony.TelephonyManager
import com.tmobile.myaccount.events.diagnostics.pojos.collector.event.ClientSideEvent
import com.tmobile.myaccount.events.diagnostics.pojos.collector.event.eventdata.BaseEventData
import com.tmobile.mytmobile.echolocate.communicationbus.rxbus.PostTicket
import com.tmobile.mytmobile.echolocate.communicationbus.rxbus.RxBus
import com.tmobile.mytmobile.echolocate.voice.model.VoiceReport
import com.tmobile.mytmobile.echolocate.scheduler.events.ScheduledJobCompletedEvent
import com.tmobile.mytmobile.echolocate.utils.ELDeviceUtils
import com.tmobile.pr.androidcommon.system.SystemService
import java.util.*
import kotlin.math.min


/**
 * Utils class to hold all voice related operations
 */
class VoiceUtils {

    companion object {
        private const val ECHO_LOCATE_VOICE_EVENT_TYPE = "diagnosticsapp.call.rawdata"

        val defaultCallsPerSession = 20
        /**
         * TMOPROXY_PACKAGE_NAME
         *
         * Package name for TMOProxy build
         */
        const val TMOPROXY_PACKAGE_NAME = "com.tmobile.pr.mytmobile"

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
         * Converts json object to string and prints the log in debug mode
         * @param T:Any
         */
        fun printLog(T: Any) {
            if (BuildConfig.DEBUG) {
                val g = Gson()
                val str = g.toJson(T)
                EchoLocateLog.eLogI("Saving: $str")
            }
        }

        fun getListForSa5GBandKeyValue(data: String) : Array<String?> {
            val jsonArray = JSONArray(data)
            val list:Array<String?> = arrayOfNulls(jsonArray.length())
            for (i in 0 until jsonArray.length()) {
                list[i] = jsonArray.getString(i)
            }
            return list
        }

        fun createClientSideEvent(jsonPayload: String): ClientSideEvent {
            val gson = Gson()
            var clientSideEvent: ClientSideEvent? = null
                    val voiceReport =
                        gson.fromJson(jsonPayload, VoiceReport::class.java) as BaseEventData
                    clientSideEvent = ClientSideEvent().withEventData(voiceReport)
                    clientSideEvent.withEventType(ECHO_LOCATE_VOICE_EVENT_TYPE)
                    clientSideEvent.withClientVersion(BuildConfig.VERSION_NAME)
                    clientSideEvent.withTimestamp(Date())
            return clientSideEvent
                }
    }
}