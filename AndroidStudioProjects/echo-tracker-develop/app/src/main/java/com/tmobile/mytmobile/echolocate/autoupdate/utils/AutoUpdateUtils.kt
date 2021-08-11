package com.tmobile.mytmobile.echolocate.autoupdate.utils

import android.text.TextUtils
import com.tmobile.mytmobile.echolocate.BuildConfig
import com.tmobile.mytmobile.echolocate.appstart.AppStartSharedPreference
import com.tmobile.mytmobile.echolocate.utils.EchoLocateLog

/**
 * Created by Divya Mittal on 04/09/2021
 */

class AutoUpdateUtils {
    companion object {
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

    }

}
