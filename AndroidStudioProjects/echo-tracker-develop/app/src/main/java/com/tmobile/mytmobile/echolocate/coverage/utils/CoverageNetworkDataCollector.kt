package com.tmobile.mytmobile.echolocate.coverage.utils

import android.content.Context
import android.net.NetworkCapabilities
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import android.provider.Settings
import android.telephony.TelephonyManager
import com.tmobile.mytmobile.echolocate.coverage.model.CoverageConnectedWifiStatus
import com.tmobile.mytmobile.echolocate.utils.DevLogUtils
import com.tmobile.mytmobile.echolocate.utils.ELDeviceUtils
import com.tmobile.mytmobile.echolocate.utils.EchoLocateDateUtils
import com.tmobile.mytmobile.echolocate.utils.EchoLocateLog
import com.tmobile.pr.androidcommon.system.SystemService

/**
 * Helper class that contains utilities methods to work with available device
 * networks.
 */
class CoverageNetworkDataCollector {

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

        private const val LTE_VOICE_SUPPORT = "ril.ims.ltevoicesupport"
        private const val VOICECALL_TYPE_KEY = "voicecall_type"
        private const val VOICECALL_TYPE_DEFAULT_VALUE = -1

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
            CoverageUtils.sendCrashReportToFirebase(
                "Exception in CoverageNetworkCollector : getActiveNetwork()",
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

    /**
     * Checks that roaming data is enabled in settings by using class Settings.Global
     *
     * @param context application's context
     * @return `True` if roaming data is enabled in settings,
     * `false` otherwise.
     */
    fun isRoamingDataEnabled(context: Context): Boolean {
        return Settings.System.getInt(context.contentResolver, Settings.Global.DATA_ROAMING, 0) != 0
    }

    /**
     * Check the actual roaming status by using TelephonyManager
     * @param context:context
     * @return Boolean
     */
    fun checkIsRoaming(context: Context): Boolean {
        val telephonyManager = SystemService.getTelephonyManager(context) as TelephonyManager
        return telephonyManager.isDataEnabled && telephonyManager.isNetworkRoaming
    }

    private fun getScanWifiList(
        context: Context?,
        wifiManager: WifiManager?
    ): List<ScanResult?>? {
        if (context != null && wifiManager != null && CoverageUtils.checkLocationPermission(context))
            return wifiManager.scanResults
        return null
    }

    /**
     * Returns Wi-Fi's state
     *
     * @param context valid Context
     * @return Wi-Fi's state
     */
    private fun getWifiState(context: Context): String {
        val wifiManager = context.applicationContext
            .getSystemService(Context.WIFI_SERVICE) as WifiManager
        return when (wifiManager.wifiState) {
            WifiManager.WIFI_STATE_DISABLING -> WIFI_STATE_DISABLING
            WifiManager.WIFI_STATE_DISABLED -> WIFI_STATE_DISABLED
            WifiManager.WIFI_STATE_ENABLING -> WIFI_STATE_ENABLING
            WifiManager.WIFI_STATE_ENABLED -> WIFI_STATE_ENABLED
            else -> WIFI_STATE_UNKNOWN
        }
    }

    /**
     * Returns Connected Wifi Status
     *
     * @param context valid Context
     * @return Wi-Fi's state
     */
    fun getConnectedWifiStatus(context: Context): CoverageConnectedWifiStatus? {
        var coverageConnectedWifiStatus: CoverageConnectedWifiStatus? = null
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
            if (!CoverageUtils.checkLocationPermission(context)) {
                EchoLocateLog.eLogD("ACCESS_FINE_LOCATION is not granted")
                return null
            }
        }

        var networkCapabilities: NetworkCapabilities? = null
        try {
            networkCapabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        } catch (ex: SecurityException) {
            CoverageUtils.sendCrashReportToFirebase(
                "Exception in CoverageNetworkCollector : getActiveNetwork()",
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
                        coverageConnectedWifiStatus = CoverageConnectedWifiStatus(
                            wifiState = getWifiState(context),
                            bssid = wifiManager.connectionInfo.bssid,
                            bssLoad = "",
                            capabilities = result.capabilities,
                            centerFreq0 = result.centerFreq0.toString(),
                            centerFreq1 = result.centerFreq1.toString(),
                            channelMode = "",
                            channelWidth = result.channelWidth.toString(),
                            frequency = result.frequency.toString(),
                            rssiLevel = result.level.toString(),
                            operatorFriendlyName = result.operatorFriendlyName.toString(),
                            passportNetwork = result.isPasspointNetwork.toString(),
                            ssid = wifiManager.connectionInfo.ssid,
                            accessPointUpTime = "",
                            timestamp = EchoLocateDateUtils.convertToShemaDateFormat(
                                System.currentTimeMillis().toString()
                            )
                        )
                        break
                    }
                }
            }
        } else {
            EchoLocateLog.eLogD("Wifi is not connected. Connected Wifi information not available.")
        }
        return coverageConnectedWifiStatus
    }

    /**
     * Used for define volteState for CoverageSettings
     */
    fun readVoiceCallType(context: Context): Int {
        var voiceCallType: Int = VOICECALL_TYPE_DEFAULT_VALUE
        try {
            val resolver = context.contentResolver
            voiceCallType = Settings.System.getInt(
                resolver,
                VOICECALL_TYPE_KEY,
                VOICECALL_TYPE_DEFAULT_VALUE
            )
        } catch (e: NullPointerException) {
            EchoLocateLog.eLogE("Null while getting voice call type")
        }
        return voiceCallType
    }
}