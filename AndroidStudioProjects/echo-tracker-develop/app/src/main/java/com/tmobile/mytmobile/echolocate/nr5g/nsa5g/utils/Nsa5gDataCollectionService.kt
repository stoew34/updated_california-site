package com.tmobile.mytmobile.echolocate.nr5g.nsa5g.utils

import android.Manifest
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.content.pm.PackageManager
import android.net.NetworkCapabilities
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import android.os.Build
import android.telephony.ServiceState
import android.telephony.TelephonyManager
import androidx.core.content.ContextCompat
import com.tmobile.mytmobile.echolocate.nr5g.core.utils.Nr5gUtils
import com.tmobile.mytmobile.echolocate.nr5g.nsa5g.database.entity.Nr5gStatusEntity
import com.tmobile.mytmobile.echolocate.nr5g.nsa5g.model.ConnectedWifiStatus
import com.tmobile.mytmobile.echolocate.utils.DevLogUtils
import com.tmobile.mytmobile.echolocate.utils.ELDeviceUtils
import com.tmobile.mytmobile.echolocate.utils.EchoLocateLog
import com.tmobile.pr.androidcommon.system.SystemService
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.util.concurrent.TimeUnit

class Nsa5gDataCollectionService {

    companion object {
        const val NRSTATUS_METHOD_NAME = "getNrStatus"
        const val NRSTATUS_DEFAULT_VALUE = -999

        /**
         * Checking a permission for ConnectedWifiStatus
         */
        fun checkLocationPermission(context: Context): Boolean {
            return ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    /**
     * Get service state object from [TelephonyManager]
     * First tries to fetch from Sync method  if it returns null tries the async method
     */
    private fun fetchServiceState(context: Context): ServiceState? {

        val serviceState = getServiceStateSync(context)
        return serviceState
    }

    /**
     * Synchronous call for the Service state from telephonyManager
     * Returns service state from telephony manager
     * Returns null if required permission is not present.
     */

    @SuppressLint("MissingPermission")
    @TargetApi(Build.VERSION_CODES.O)
    private fun getServiceStateSync(context: Context): ServiceState? {
        if (isServiceStatePermitted(context)) {
            val telephonyManager = SystemService.getTelephonyManager(context)
            return telephonyManager.serviceState
        }
        return null
    }

    /**
     * Checking a permission for Nr5gStatus
     */
    private fun isServiceStatePermitted(context: Context): Boolean {
        return (ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_PHONE_STATE
        ) == PackageManager.PERMISSION_GRANTED)
    }

    /**
     * Function to get the status of the NR5G
     * this method first fetches latest ServiceState and calls "nr5gStatus" method in ServiceState using reflection.
     * if the ServiceState object is null or if "nr5gStatus" method is not present in ServiceState
     * class then we return default value(-999)
     * If the device is having Android Q, then we return null value
     *
     * @param context   [Context]
     */
    fun getNrStatus(context: Context): Nr5gStatusEntity? {
        //The proprietary API is not supported in Android Q and above
        if (ELDeviceUtils.isQDeviceOrHigher()) {
            return null
        }

        var nrStatus =
            NRSTATUS_DEFAULT_VALUE //NRSTATUS_DEFAULT_VALUE = -999
        val serviceState = fetchServiceState(context)
        if (serviceState != null) {
            val get5gStatus: Method?
            try {
                get5gStatus = serviceState.javaClass.getMethod(NRSTATUS_METHOD_NAME)
                nrStatus = get5gStatus!!.invoke(serviceState) as Int
            } catch (e: IllegalAccessException) {
                EchoLocateLog.eLogE("error: ${e.localizedMessage}")
            } catch (e: InvocationTargetException) {
                EchoLocateLog.eLogE("error: ${e.localizedMessage}")
            } catch (e: NoSuchMethodException) {
                EchoLocateLog.eLogE("error: ${e.localizedMessage}")
            }
        }
        return Nr5gStatusEntity(nrStatus)
    }

    /**
     * Get a list of Wifi ScanResult
     */
    private fun getScanWifiList(
        context: Context?,
        wifiManager: WifiManager?
    ): List<ScanResult?>? {
        if (context != null && wifiManager != null && checkLocationPermission(context))
            return wifiManager.scanResults
        return null
    }

    /**
     * Get a specific params from list of  Wifi ScanResult
     */
    fun processWifiData(context: Context): ConnectedWifiStatus? {

        val connectivityManager = SystemService.getConnectivityManager(context) ?: return null
        val networkInfo = connectivityManager.activeNetworkInfo
        val wifiManager = SystemService.getWiFiManager(context)
        if (connectivityManager.activeNetwork == null || networkInfo == null || !networkInfo.isConnected
            || wifiManager == null || wifiManager.connectionInfo == null
        ) {
            return null
        }

        if (ELDeviceUtils.isQDeviceOrHigher()) {
            if (!checkLocationPermission(context)) {
                EchoLocateLog.eLogD("ACCESS_FINE_LOCATION is not granted")
                return null
            }
        }

        var networkCapabilities: NetworkCapabilities? = null
        try {
            networkCapabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        } catch (ex: SecurityException) {
            Nr5gUtils.sendCrashReportToFirebase(
                "Exception in Nsa5gDataCollectionService : processWifiData()",
                ex.localizedMessage,
                "SecurityException"
            )
        }
        if (networkCapabilities != null &&
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
        ) {

            val scanResult: List<ScanResult?> = getScanWifiList(context, wifiManager) ?: return null
            for (result in scanResult) {
                if (result != null && wifiManager.connectionInfo.bssid == result.BSSID) {
                    return ConnectedWifiStatus(
                        wifiManager.connectionInfo.bssid,
                        "NA",
                        wifiManager.connectionInfo.ssid,
                        TimeUnit.MILLISECONDS.convert(
                            result.timestamp,
                            TimeUnit.MICROSECONDS
                        ).toInt(),
                        result.capabilities,
                        result.centerFreq0,
                        result.centerFreq1,
                        "NA",
                        result.channelWidth,
                        result.frequency,
                        result.operatorFriendlyName.toString(),
                        if (result.isPasspointNetwork) 1 else 0,
                        result.level
                    )
                }
            }
        } else {
            EchoLocateLog.eLogD(
                "ConnectedWifiStatusDataCollector Wifi is not connected. " +
                        "Connected Wifi information not available."
            )
        }
        return null
    }

}