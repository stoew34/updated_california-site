package com.tmobile.mytmobile.echolocate.reporting.reportsender

import android.content.Context
import android.os.Build
import com.tmobile.mytmobile.echolocate.network.model.DIARequest
import com.tmobile.mytmobile.echolocate.network.model.NetworkRequestHeader
import com.tmobile.mytmobile.echolocate.network.model.NetworkRequestType
import com.tmobile.mytmobile.echolocate.network.model.NetworkRetryPrefs
import com.tmobile.mytmobile.echolocate.reporting.utils.ReportingLog
import com.tmobile.mytmobile.echolocate.reporting.utils.ReportingModuleSharedPrefs
import com.tmobile.pr.androidcommon.log.TmoLog
import java.net.Inet4Address
import java.net.Inet6Address
import java.net.NetworkInterface
import java.util.*
import kotlin.collections.HashMap

/**
 * This class provides necessary requirements like headers, url, request to do the configuration network request
 * this includes retrieving the DIARequest, Headers and Retry Preferences
 */
class ReportNetworkTask {

    companion object {

        /**
         * this url is for the PRODUCTION URL for Configuration Network Request
         */
        private const val DIA_REPORT_URL_PROD =
            "https://trusted-collector-bk.tmocce.com/collector/0.5"

        /**
         * load the default values for DIARequest like URL
         * @return DIARequest
         */
        fun getReportDiaRequest(
            context: Context,
            payloadFile: String,
            reportId: String
        ): DIARequest {
            val diaRequest = DIARequest()

            diaRequest.url = ReportingModuleSharedPrefs.clientAppReportingUrl
            ReportingLog.eLogD("Connecting to ............" + diaRequest.url, context)
            diaRequest.payload = payloadFile
            diaRequest.requestType = NetworkRequestType.POST.type
            diaRequest.id = reportId
            diaRequest.popSignRequest = true

            return diaRequest
        }

        /**
         * putting the values of network headers for request
         * @return NetworkRequestHeader
         */
        fun getReportRequestHeader(): NetworkRequestHeader {
            val headers: HashMap<String, String> = HashMap()
            headers["x-dat"] = ReportingModuleSharedPrefs.tokenObject!!
            headers["Content-Type"] = "application/json; charset=utf-8"
            headers["Accept"] = "application/json; charset=utf-8"
            headers["x-tmo-oem"] = Build.MANUFACTURER
            headers["x-tmo-model"] = Build.MODEL
            headers["x-tmo-oem-id"] = Build.ID
            headers["x-tmo-os-language"] = Locale.getDefault().toString()
            headers["x-tmo-client-version"] = ReportingModuleSharedPrefs.clientAppVersionName!!
            headers["x-tmo-build-number"] = ReportingModuleSharedPrefs.clientAppVersionCode!!
            headers["x-tmo-client-name"] = ReportingModuleSharedPrefs.clientAppApplicationId!!
            headers["x-tmo-device-os"] = "android"
            headers["x-tmo-device-os-version"] = Build.VERSION.RELEASE
            headers["x-tmo-ipv4"] = getIpv4()
            headers["x-tmo-ipv6"] = getIpv6()

            return NetworkRequestHeader(headers)
        }

        /**
         * Retrieving the default network retry preferences for the network request
         *
         * @return NetworkRetryPrefs
         */
        fun getReportRetryPrefs(): NetworkRetryPrefs {
            //TODO assuming the network retry preferences defaults change this later accordingly
            return NetworkRetryPrefs("", 3, 1L, 1L, 1)
        }

        private const val IPV4 = "Ipv4"
        private const val IPV6 = "Ipv6"

        /**
         * Util function which is used to capture Ipv4 and Ipv6 details.
         * @return string based on the IPV4 or IPV6.
         */
        private fun getIpv4AndIpv6(ipAddress: String): String {
            try {
                val networkInterface = NetworkInterface.getNetworkInterfaces()
                while (networkInterface.hasMoreElements()) {
                    val intf = networkInterface.nextElement()
                    val enumIpAddr = intf.inetAddresses
                    while (enumIpAddr.hasMoreElements()) {

                        val inetAddress = enumIpAddr.nextElement()

                        if (!inetAddress.isLoopbackAddress && inetAddress is Inet4Address && ipAddress == IPV4) {
                            return inetAddress.getHostAddress()
                        }
                        if (!inetAddress.isLoopbackAddress && inetAddress is Inet6Address && ipAddress == IPV6) {
                            return inetAddress.getHostAddress()
                        }
                    }
                }
            } catch (ex: Exception) {
                TmoLog.e("IP Address", ex.toString())
            }
            return null.toString()
        }

        /**
         * Return IPV4 address.
         * @return String representation of IPV4.
         */
        private fun getIpv4(): String {
            return getIpv4AndIpv6(IPV4)
        }

        /**
         * Return IPv6 address.
         * @return String representation of IPV6.
         */
        private fun getIpv6(): String {
            return getIpv4AndIpv6(IPV6)
        }
    }
}