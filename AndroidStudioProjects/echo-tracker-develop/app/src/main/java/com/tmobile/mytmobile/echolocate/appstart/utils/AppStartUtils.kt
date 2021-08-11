package com.tmobile.mytmobile.echolocate.appstart.utils

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.provider.Settings
import android.telephony.TelephonyManager
import android.text.TextUtils
import com.tmobile.mytmobile.echolocate.BuildConfig
import com.tmobile.mytmobile.echolocate.EchoLocateApplication
import com.tmobile.mytmobile.echolocate.appstart.AppStartSharedPreference
import com.tmobile.mytmobile.echolocate.configuration.events.configurationevents.AllConfigsEvent
import com.tmobile.mytmobile.echolocate.configuration.model.CustomPanicMode
import com.tmobile.mytmobile.echolocate.utils.ELDeviceUtils
import com.tmobile.mytmobile.echolocate.utils.EchoLocateLog
import com.tmobile.pr.androidcommon.system.SystemService

/**
 * Created by Divya Mittal on 04/09/2021
 */

class AppStartUtils {
    companion object {
        /**
         * TMOPROXY_PACKAGE_NAME
         *
         * Package name for TMOProxy build
         */
        const val TMOPROXY_PACKAGE_NAME = "com.tmobile.pr.mytmobile"

        /**
         * Constant for main flavor
         * This should be used as default value in AppStartSharedPreference
         */
        const val BUILD_FLAVOR_DEV = 0

        /**
         * Constant for dolphin flavor
         */
        const val BUILD_FLAVOR_DOLPHIN = 1

        /**
         * Constant for TMOProxy flavor
         */
        const val BUILD_FLAVOR_TMOPROXY = 2

        /**
         * Constant for TMOProxy flavor
         */
        const val BUILD_FLAVOR_OEMTOOL = 3

        fun isFlavorChanged(): Boolean {
            if (BuildConfig.FLAVOR.equals("dev", true) && AppStartSharedPreference.currentAppFlavor != BUILD_FLAVOR_DEV) {
                EchoLocateLog.eLogD("Diagnostic : flavor is changed to Main")
                return true
            }
            if (BuildConfig.FLAVOR.equals("dolphin", true) && AppStartSharedPreference.currentAppFlavor != BUILD_FLAVOR_DOLPHIN) {
                EchoLocateLog.eLogD("Diagnostic : flavor is changed to dolphin")
                return true
            }
            if (BuildConfig.FLAVOR.equals("TMOProxy", true) && AppStartSharedPreference.currentAppFlavor != BUILD_FLAVOR_TMOPROXY) {
                EchoLocateLog.eLogD("Diagnostic : flavor is changed to TMOProxy")
                return true
            }
            if (BuildConfig.FLAVOR.equals("OEMTool", true) && AppStartSharedPreference.currentAppFlavor != BUILD_FLAVOR_OEMTOOL) {
                EchoLocateLog.eLogD("Diagnostic : flavor is changed to OEMTool")
                return true
            }
            EchoLocateLog.eLogD("Diagnostic : flavor is not changed. Current flavor = ${AppStartSharedPreference.currentAppFlavor}")
            return false
        }

        fun updateAppFlavor() {
            if (BuildConfig.FLAVOR.equals("dev", true)) {
                AppStartSharedPreference.currentAppFlavor = BUILD_FLAVOR_DEV
            }
            if (BuildConfig.FLAVOR.equals("dolphin", true)) {
                AppStartSharedPreference.currentAppFlavor = BUILD_FLAVOR_DOLPHIN
            }
            if (BuildConfig.FLAVOR.equals("TMOProxy", true)) {
                AppStartSharedPreference.currentAppFlavor = BUILD_FLAVOR_TMOPROXY
            }
            if (BuildConfig.FLAVOR.equals("OEMTool", true)) {
                AppStartSharedPreference.currentAppFlavor = BUILD_FLAVOR_OEMTOOL
            }
            EchoLocateLog.eLogD("Diagnostic : flavor is updated to ${AppStartSharedPreference.currentAppFlavor}")
        }

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
         * Check if the new versionName given is higher than the existing version.
         * Example versionName will be "1.2.3" "2.3.4"etc
         * Version name weight is as 1.1.2<1.2.1<2.1.1
         *
         * @param existingVersion
         * @param newVersion
         * @return {@`true` if new version is greater than older one, {@`false`}otherwise}
         */
        fun checkForUpdate(existingVersion: String, newVersion: String): Boolean {
            if (TextUtils.isEmpty(existingVersion) || TextUtils.isEmpty(newVersion)) {
                return false
            }
            var newVersionIsGreater = false
            val existingVersionArray =
                existingVersion.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val newVersionArray =
                newVersion.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

            val maxIndex = Math.max(existingVersionArray.size, newVersionArray.size)
            for (i in 0 until maxIndex) {
                var newValue: Int
                var oldValue: Int
                try {
                    oldValue = Integer.parseInt(existingVersionArray[i])
                } catch (e: Exception) {
                    oldValue = 0
                }

                try {
                    newValue = Integer.parseInt(newVersionArray[i])
                } catch (e: Exception) {
                    newValue = 0
                }

                if (oldValue < newValue) {
                    newVersionIsGreater = true
                    break
                }
                if (oldValue > newValue) {
                    break
                }
            }
            return newVersionIsGreater
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
         * This function checks the blacklisted oem and tac list and returns if the tac or oem matches
         * with the device
         */
        fun checkPanicMode(allConfig: AllConfigsEvent): Boolean {
            val isPanicModeDisabled = !allConfig.configValue.panicMode
            return if (isPanicModeDisabled) {
                val panicModeOem = allConfig.configValue.customPanicMode
                isPanicModeForOemAndTac(panicModeOem)
            } else {
                true
            }
        }

        fun isPanicModeForOemAndTac(panicModeOem: CustomPanicMode?): Boolean {
            if (panicModeOem != null) {
                if (panicModeOem.isEnabled) {
                    var isPanicModeOem = false
                    val blackListedOemList = panicModeOem.blacklistedOEM
                    blackListedOemList?.forEach { model ->
                        if (Build.MANUFACTURER == model) {
                            EchoLocateLog.eLogD("Diagnostic : Panic mode for OEM is turned ON")
                            isPanicModeOem = true
                        }
                    }
                    return if (!isPanicModeOem) {
                        checkTacInList(
                            EchoLocateApplication.getContext()!!.applicationContext,
                            panicModeOem.blacklistedTAC
                        )
                    } else true
                }
            }
            return false
        }
    }


}
