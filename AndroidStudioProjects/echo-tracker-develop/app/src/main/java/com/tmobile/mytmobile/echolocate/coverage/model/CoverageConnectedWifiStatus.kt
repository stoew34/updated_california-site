package com.tmobile.mytmobile.echolocate.coverage.model

import com.google.gson.annotations.SerializedName

data class CoverageConnectedWifiStatus(

    /**
    An integer which corresponds to the following WiFi setting states WIFI_STATE_DISABLING WIFI_STATE_DISABLED WIFI_STATE_ENABLING WIFI_STATE_ENABLED WIFI_STATE_UNKNOWN
     */
    @SerializedName("wifiState")
    val wifiState: String?,

    /**
    MAC Address of scanned IP. Ex:00:19:92:50:ba:21
     */
    @SerializedName("bssid")
    val bssid: String?,

    /**
    Value of BSSLoad
     */
    @SerializedName("bssLoad")
    val bssLoad: String?,

    /**
    Returns the capabilities of the scanned AP. Ex:[WPA2-PSK-CCMP][ESS]
     */
    @SerializedName("capabilities")
    val capabilities: String?,

    /**
    Value represents centerFreq0 of scanned AP
     */
    @SerializedName("centerFreq0")
    val centerFreq0: String?,

    /**
    Value represents centerFreq1 of scanned AP
     */
    @SerializedName("centerFreq1")
    val centerFreq1: String?,

    /**
    Value representing channel mode
     */
    @SerializedName("channelMode")
    val channelMode: String?,

    /**
    Represents width of the channel
     */
    @SerializedName("channelWidth")
    val channelWidth: String?,

    /**
    Represents frequency of scanned AP
     */
    @SerializedName("frequency")
    val frequency: String?,

    /**
    Returns the received signal strength indicator of the current 802.11network, in dBm. Ex:-57
     */
    @SerializedName("rssiLevel")
    val rssiLevel: String?,

    /**
    Friendly name of operator, NA if not available
     */
    @SerializedName("operatorFriendlyName")
    val operatorFriendlyName: String?,

    /**
    Returns False = 0, True = 1
     */
    @SerializedName("passportNetwork")
    val passportNetwork: String?,

    /**
    epresents beaconID/name of scanned device
     */
    @SerializedName("ssid")
    val ssid: String?,

    /**
    Represents Value of uptime in MS (milliseconds)
     */
    @SerializedName("accessPointUpTime")
    val accessPointUpTime: String?,

    /**
    UTC time at which the wifi data was captured in milliseconds
     */
    @SerializedName("timestamp")
    val timestamp: String?
) {
    constructor() : this(
        null, null, null, null, null,
        null, null, null, null, null,
        null, null, null, null, null
    )
}