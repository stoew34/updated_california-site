package com.tmobile.mytmobile.echolocate.voice.utils

import android.content.Context
import android.telephony.TelephonyManager
import android.text.TextUtils
import com.tmobile.pr.androidcommon.system.SystemService

/**
 *
 * class MccAccess
 *
 *  provides companion function to get MCC mobile country code
 *
 */
class MccAccess {

    companion object {

        /**
         * get MCC from network operator
         *  @param context used to get telephonymanger from system service
         */
        fun getMCC(context: Context): String {

            val tel = SystemService.getTelephonyManager(context) as TelephonyManager
            val networkOperator = tel.networkOperator

            return if (!TextUtils.isEmpty(networkOperator)) {
                networkOperator.substring(0, 3)
            } else
                ""

        }
    }
}