package com.tmobile.mytmobile.echolocate.dsdkHandshake.utils

import android.content.Context
import android.telephony.TelephonyManager
import android.annotation.SuppressLint
import android.provider.Settings
import com.tmobile.pr.androidcommon.system.SystemService
import com.tmobile.mytmobile.echolocate.utils.ELDeviceUtils
import com.tmobile.mytmobile.echolocate.utils.EchoLocateLog

class DsdkHandshakeUtils {
    companion object {
        const val ECHOLOCATE_DSDK_HANDSHAKE_DB_NAME = "echolocate_dsdk_handshake_db"

        const val DSDK_HANDSHAKE_TABLE_NAME = "dsdk_handshake_table"

        const val DSDK_HANDSHAKE_INTENT_ACTION = "com.tmobile.echolocate.dsdkHandshake"

        const val DSDK_HANDSHAKE_BROADCAST_RECEIVER_PERMISSION = "com.tmobile.echolocate.permission.RECEIVE_BROADCAST_INTENT"

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

    }
}