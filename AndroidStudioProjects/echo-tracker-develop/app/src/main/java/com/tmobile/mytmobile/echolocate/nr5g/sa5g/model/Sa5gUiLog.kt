package com.tmobile.mytmobile.echolocate.nr5g.sa5g.model

import com.google.gson.annotations.SerializedName


/**
 * Model class that holds call Sa5gUiLog data
 *
 * Serialized name annotation for all variables to be in JSON,
 * and it will be helpful while doing progaurding
 */
data class Sa5gUiLog(

    /**
     * Returns the enumerated type of network as an integer which may have the following values:

     * [-2] – NOT_AVAILABLE
     * [0] – SEARCHING
     * [1] – LTE orLTE with EN-DC
     * [2] – UMTS
     * [3] – EDGE
     * [4] – GPRS

     * Notes: Returns -2 if no data is available
     */
    @SerializedName("networkType")
    val networkType: String?,

    /**
     * Returns timestamp in UNIX epoch time as a UTC string, "yyyy-MM-dd'T'HH:mm:ss.SSSZ" e.g.
     * 2019-06-24T18:57:23.567+0000

     * Notes:  This timestamp can be rendered as an integer string which will correspond to the above UTC
     * dates.
     */
    @SerializedName("timestamp")
    val timestamp: String?,

    /**
     * Returns the network type on the signal indicator displayed on screen. Even if the signal indicator is
     * hidden behind other app screen (such as full screen Netflix play), device shall report the signal
     * indicator type that is supposed to be displayed when the signal indicator screen is on foreground.
     * Values are as follows:

     * NO_SIGNAL no signal detected (WiFi or cellular)
     * NO_ICON no cellular network indicator displayed on the screen (such as WiFi only)
     * [2G] the network is connected to 2G
     * [3G] the network is connected to 3G
     * [4G] the network is connected to 4G
     * [4G-LTE] the network is connected to 4G-LTE
     * [5G] the network is connected to 5G (5G-NR)
     * NA the UE failed to detect its UI status
     */
    @SerializedName("uiNetworkType")
    val uiNetworkType: String?,

    /**
     * Returns the data transmission indicator with up and down arrows that are turned on or off based on the data transmission status.
     *
     * This is not only for 5G but also for 2G, 3G, 4G and 4G LTE conditions.
     * Format: string enumeration {UP_ON | DOWN_ON | BOTH_ON | BOTH_OFF}
     *
     */
    @SerializedName("uiDataTransmission")
    val uiDataTransmission: String?,

    /**
     * Returns the number of antenna bars on the UI indicator in 2G, 3G, 4G, 4G LTE or 5G.
     * [-999] the UE is not connected to a cellular network
     * Notes: Returns -999 if ‘networkType’ is not 1 (LTE or LTE EN-DC)
     *      * UE in API version 1 shall report the number of antenna bars on the UI indicator in 2G, 3G, 4G, 4G LTE or 5G as follows:
     *
     * uiNumberOfAntennaBars: The number of antenna bars is displayed on the screen
     *
     * -1: If the UE is not connected to any cellular network at the time of this API call, the uiNumberOfAntennaBars is not applicable. Hence, report -1.
     * -2: If there was an internal SW function call failure or a SW exception occurred in getting this value, return -2.
     */
    @SerializedName("uiNumberAntennaBars")
    val uiNumberAntennaBars: String?
)