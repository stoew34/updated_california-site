package com.tmobile.mytmobile.echolocate.utils

import android.os.Build

/**
 * Created by Hitesh K Gupta on 2019-12-23
 */
class ELDeviceUtils {

    companion object {

        /**
         * Util method for checking if device is working on Android Oreo (API26) or higher
         *
         * @return true if device has Oreo or higher
         */
        fun isOreoDeviceOrHigher(): Boolean {
            return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
        }

        /**
         * Util method for checking if device is working on Android Pie (API28) or higher
         *
         * @return true if device has Pie or higher
         */
        fun isPieDeviceOrHigher(): Boolean {
            return Build.VERSION.SDK_INT >= Build.VERSION_CODES.P
        }

        /**
         * Util method for checking if device is working on Android 10 (API29) or higher
         *
         * @return true if device has Android 10 or higher
         */
        fun isQDeviceOrHigher(): Boolean {
            return Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
        }

        /**
         * Util method for checking if device is working on Android 11 (API30) or higher
         *
         * @return true if device has Android 11 or higher
         */
        fun isRDeviceOrHigher(): Boolean {
            return Build.VERSION.SDK_INT >= Build.VERSION_CODES.R
        }
    }
}