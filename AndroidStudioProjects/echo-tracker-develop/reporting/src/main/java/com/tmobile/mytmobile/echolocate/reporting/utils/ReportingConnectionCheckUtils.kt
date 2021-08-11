package com.tmobile.mytmobile.echolocate.reporting.utils

/**
 * Created by Divya Mittal on 5/19/21
 */
import android.content.Context
import android.net.NetworkCapabilities
import android.telephony.TelephonyManager
import com.tmobile.pr.androidcommon.system.SystemService

/**
 * This class handles all the connection check operations like internet status check, roaming status check etc
 */

internal class ReportingConnectionCheckUtils {

    companion object {

        /**
         * Check the roaming status
         * @param context:context
         * @return Boolean
         */
        fun checkIsRoaming(context: Context): Boolean {
            val telephonyManager = SystemService.getTelephonyManager(context) as TelephonyManager?
                ?: return false
            return try {
                !telephonyManager.isDataEnabled  || telephonyManager.isNetworkRoaming
            } catch (ex: Exception) {
                false
            }
        }

        /**
         * Check internet connection type
         * @param context:context
         * @return Boolean
         */
        fun checkIsWifi(context: Context): Boolean {
            val connectivityManager =
                SystemService.getConnectivityManager(context) ?: return false
            if (connectivityManager.activeNetwork == null) {
                return false
            }
            return try {
                val networkCapabilities =
                    connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork) ?: return false
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
            } catch (ex: Exception) {
                false
            }
        }

        /**
         * Check the roaming status
         * @param context:context
         * @return Boolean
         */
        fun checkIsNetworkAvailable(context: Context): Boolean {
            val connectivityManager =
                SystemService.getConnectivityManager(context) ?: return false

            return try {
                val networkCapabilities =
                    connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
                        ?: return false
                networkCapabilities.run {
                    when {
                        hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                        hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                        hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                        else -> false
                    }
                }
            } catch (ex: Exception) {
                false
            }
        }
    }
}