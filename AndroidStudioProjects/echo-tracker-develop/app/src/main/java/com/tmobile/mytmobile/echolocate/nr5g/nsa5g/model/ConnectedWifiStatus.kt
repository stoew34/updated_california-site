package com.tmobile.mytmobile.echolocate.nr5g.nsa5g.model

import com.google.gson.annotations.SerializedName

/**
 * Model class that holds call ConnectedWifiStatus data
 *
 * Serialized name annotation for all variables to be in JSON,
 * and it will be helpful while doing progaurding
 */
data class ConnectedWifiStatus(

    /**
     * Returns the BSSID of the scanned device as a string.
     */
    @SerializedName("BSSID")
    val bssId: String,

    /**
     *  Returns the BSSLoad of the scanned device as a string.
     */
    @SerializedName("BSSLoad")
    val bssLoad: String,

    /**
     * Returns the beaconID/name of scanned device as a string.
     */
    @SerializedName("SSID")
    val ssId: String,

    /**
     * Returns the uptime of the scanned device in miliseconds
     */
    @SerializedName("accessPointUpTime")
    val accessPointUpTime: Int,

    /**
     *  Returns the capabilities of the scanned device.
     */
    @SerializedName("capabilities")
    val capabilities: String,

    /**
     * Returns the value of the center frequency as an integer.
     */
    @SerializedName("centerFreq0")
    val centerFreq0: Int,

    /**
     * Returns the value of the second center frequency as an integer.
     */
    @SerializedName("centerFreq1")
    val centerFreq1: Int,

    /**
     * Returns a string value which represents the channel mode.
     */
    @SerializedName("channelMode")
    val channelMode: String,

    /**
     * Returns an integer which represents the width of the channel.
     */
    @SerializedName("channelWidth")
    val channelWidth: Int,

    /**
     *  Returns the frequency of the scanned AP.
     */
    @SerializedName("frequency")
    val frequency: Int,

    /**
     * Returns the friendly name of operator as a string.
     *  Notes: “NA” if not available
     */
    @SerializedName("operatorFriendlyName")
    val operatorFriendlyName: String,

    /**
     *  Returns an integer which corresponds to the following values:
     *  [0] False
     *  [1] True
     */
    @SerializedName("passportNetwork")
    val passportNetwork: Int,

    /**
     *  Returns the received signal level as an integer.
     */
    @SerializedName("rssiLevel")
    val rssiLevel: Int
)