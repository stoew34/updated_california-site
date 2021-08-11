package com.tmobile.mytmobile.echolocate.voice.utils

import android.content.Context
import android.telephony.TelephonyManager
import android.text.TextUtils
import com.tmobile.pr.androidcommon.system.SystemService

/**
 *
 * class MncAccess
 *
 *  provides companion function to get MNC mobile network code
 *
 */
class MncAccess {
    companion object {

        /**
         * get MNC from network operator
         *  @param context used to get telephonymanger from system service
         */
        fun getMNC(context: Context): String {
            val tel = SystemService.getTelephonyManager(context) as TelephonyManager
            val networkOperator = tel.networkOperator

            if (!TextUtils.isEmpty(networkOperator)) {
                return networkOperator.substring(3)
            } else
                return ""
        }
    }
}