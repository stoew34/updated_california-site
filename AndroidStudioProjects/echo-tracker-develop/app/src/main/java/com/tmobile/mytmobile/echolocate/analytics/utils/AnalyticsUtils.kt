package com.tmobile.mytmobile.echolocate.analytics.utils

/**
 * Created by Divya Mittal on 4/13/21
 */
import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings
import android.telephony.TelephonyManager
import com.google.gson.Gson
import com.tmobile.myaccount.events.diagnostics.pojos.collector.event.ClientSideEvent
import com.tmobile.myaccount.events.diagnostics.pojos.collector.event.eventdata.BaseEventData
import com.tmobile.mytmobile.echolocate.BuildConfig
import com.tmobile.mytmobile.echolocate.analytics.model.AnalyticsReport
import com.tmobile.mytmobile.echolocate.utils.ELDeviceUtils
import com.tmobile.pr.androidcommon.system.SystemService
import java.util.*


/**
 * Utils class to hold all voice related operations
 */
class AnalyticsUtils {

    companion object {
        private const val ECHO_LOCATE_ANALYTICS_EVENT_TYPE = "diagnosticsapp.analytics.rawdata"
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

        fun createClientSideEvent(jsonPayload: String): ClientSideEvent {
            val gson = Gson()
            var clientSideEvent: ClientSideEvent? = null
            val analyticsReport =
                gson.fromJson(jsonPayload, AnalyticsReport::class.java) as BaseEventData
            clientSideEvent = ClientSideEvent().withEventData(analyticsReport)
            clientSideEvent.withEventType(ECHO_LOCATE_ANALYTICS_EVENT_TYPE)
            clientSideEvent.withClientVersion(BuildConfig.VERSION_NAME)
            clientSideEvent.withTimestamp(Date())
            return clientSideEvent
        }
    }
}
