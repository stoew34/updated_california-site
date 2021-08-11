package com.tmobile.mytmobile.echolocate.nr5g.sa5g.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.NetworkCapabilities
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import androidx.core.content.ContextCompat
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.tmobile.mytmobile.echolocate.nr5g.core.utils.Nr5gUtils
import com.tmobile.mytmobile.echolocate.nr5g.sa5g.model.Sa5gConnectedWifiStatus
import com.tmobile.mytmobile.echolocate.utils.DevLogUtils
import com.tmobile.mytmobile.echolocate.utils.ELDeviceUtils
import com.tmobile.mytmobile.echolocate.utils.EchoLocateLog
import com.tmobile.pr.androidcommon.system.SystemService

/**
 * Helper class that contains utilities methods to work with available device
 * networks.
 */
class Sa5gDataCollectionService {

    companion object {
        const val TRANSPORT_CELLULAR = "TRANSPORT_CELLULAR"
        const val TRANSPORT_WIFI = "TRANSPORT_WIFI"
        const val TRANSPORT_ETHERNET = "TRANSPORT_ETHERNET"
        const val TRANSPORT_LOWPAN = "TRANSPORT_LOWPAN"
        const val TRANSPORT_VPN = "TRANSPORT_VPN"
        const val TRANSPORT_BLUETOOTH = "TRANSPORT_BLUETOOTH"
        const val TRANSPORT_WIFI_AWARE = "TRANSPORT_WIFI_AWARE"

        const val WIFI_STATE_DISABLED = "WIFI_STATE_DISABLED"
        const val WIFI_STATE_DISABLING = "WIFI_STATE_DISABLING"
        const val WIFI_STATE_ENABLED = "WIFI_STATE_ENABLED"
        const val WIFI_STATE_ENABLING = "WIFI_STATE_ENABLING"
        const val WIFI_STATE_UNKNOWN = "WIFI_STATE_UNKNOWN"
    }

    /**
     * Checking a permission for Location
     */
    private fun checkLocationPermission(context: Context): Boolean {
        return (ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED) &&
                (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED)
    }

    /**
     * Method is being used by Net data to set the Active Network (connectivityType)
     * @param context
     * @return String of which network type is currently active
     * */
    fun getActiveNetwork(context: Context): String? {
        val connectivityManager =
            SystemService.getConnectivityManager(context) ?: return null
        var capabilities: NetworkCapabilities? = null
        try {
            capabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        } catch (ex: SecurityException) {
            Nr5gUtils.sendCrashReportToFirebase(
                "Exception in sa5gDataCollectionService : getActiveNetwork()",
                ex.localizedMessage,
                "SecurityException"
            )
        }
        if (capabilities != null) {
            when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                    return TRANSPORT_CELLULAR
                }
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                    return TRANSPORT_WIFI
                }
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {
                    return TRANSPORT_ETHERNET
                }
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_LOWPAN) -> {
                    return TRANSPORT_LOWPAN
                }
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN) -> {
                    return TRANSPORT_VPN
                }
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> {
                    return TRANSPORT_BLUETOOTH
                }
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI_AWARE) -> {
                    return TRANSPORT_WIFI_AWARE
                }
            }
        }
        return null
    }

    private fun getScanWifiList(
        context: Context?,
        wifiManager: WifiManager?
    ): List<ScanResult?>? {
        if (context != null && wifiManager != null && checkLocationPermission(context))
            return wifiManager.scanResults
        return null
    }

    /**
     * Returns Connected Wifi Status
     *
     * @param context valid Context
     * @return Wi-Fi's state
     */
    fun getConnectedWifiStatus(context: Context): Sa5gConnectedWifiStatus? {

        var sa5gConnectedWifiStatus: Sa5gConnectedWifiStatus? = null
        val connectivityManager =
            SystemService.getConnectivityManager(context) ?: return null
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
                "Exception in sa5gDataCollectionService : getConnectedWifiStatus()",
                ex.localizedMessage,
                "SecurityException"
            )
        }
        if (networkCapabilities != null &&
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
        ) {
            val scanResult: List<ScanResult?>? =
                getScanWifiList(context, wifiManager)
            if (scanResult != null) {
                for (result in scanResult) {
                    if (result != null && wifiManager.connectionInfo.bssid == result.BSSID) {
                        sa5gConnectedWifiStatus = Sa5gConnectedWifiStatus(
                            bssId = wifiManager.connectionInfo.bssid,
                            bssLoad = "",
                            ssId = wifiManager.connectionInfo.ssid,
                            accessPointUpTime = 0,
                            capabilities = result.capabilities,
                            centerFreq0 = result.centerFreq0,
                            centerFreq1 = result.centerFreq1,
                            channelMode = "",
                            channelWidth = result.channelWidth,
                            frequency = result.frequency,
                            operatorFriendlyName = result.operatorFriendlyName.toString(),
                            passportNetwork = if (result.isPasspointNetwork) 1 else 0,
                            rssiLevel = result.level
                        )
                        break
                    }
                }
            }
        } else {
            EchoLocateLog.eLogD("Wifi is not connected. Connected Wifi information not available.")
        }
        return sa5gConnectedWifiStatus
    }
}