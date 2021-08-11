package com.tmobile.mytmobile.echolocate.nr5g.nsa5g.model

import com.google.gson.annotations.SerializedName

/**
 * Model class that holds call Nr5gUiLog data
 *
 * Serialized name annotation for all variables to be in JSON,
 * and it will be helpful while doing progaurding
 */
data class Nr5gUiLog(
    /**
     * Returns timestamp in UNIX epoch time as a UTC string, "yyyy-MM-dd'T'HH:mm:ss.SSSZ" e.g.
     * 2019-06-24T18:57:23.567+0000

     * Notes:  This timestamp can be rendered as an integer string which will correspond to the above UTC
     * dates.
     */
    @SerializedName("timestamp")
    val timestamp: String,

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
    val networkType: Int,

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
    val uiNetworkType: String,

    /**
     * Returns the data transmission indicator with up and down arrows that are turned on or off based on the
     * data transmission status. This is not only for 5G but also for 2G, 3G, 4G and 4G LTE conditions.

     * [UP_ON] the network up arrow is turned on
     * [DOWN_ON] the network down arrow is turned on
     * [BOTH_ON] the network down and up arrows are both on
     * [BOTH_OFF] the network down and up arrows are both off
     * [NA] the UE failed to detect its UI status
     */
    @SerializedName("uiDataTransmission")
    val uiDataTransmission: String,

    /**
     * Returns the number of antenna bars on the UI indicator in 2G, 3G, 4G, 4G LTE or 5G.
     * [-999] the UE is not connected to a cellular network

     * Notes: Returns -999 if ‘networkType’ is not 1 (LTE or LTE EN-DC)
     */
    @SerializedName("uiNumberOfAntennaBars")
    val uiNumberOfAntennaBars: Int,

    /**
     * Returns the state of the network connection used to determine whether to display the 5G icon or the 4G LTE icon

     * [-999] if connected network is not LTE
     * [-2] value not available even in LTE
     * [1] UE is IDLE under or connected to LTE cell not supporting NSA
     * [2] UE is IDLE under or connected to LTE cell supporting NSA and no detection of NR coverage
     * [3] UE is connected to LTE only under LTE cell supporting NSA and detection of NR coverage
     * [4] UE is IDLE under LTE cell supporting NSA and detection of NR coverage
     * [5] UE is connected to LTE as well as NR under LTE cell supporting NSA
     * [6] UE is IDLE under or connected to NG-RAN while attached to 5GC
     */
    @SerializedName("ui5gConfigurationStatus")
    val ui5gConfigurationStatus: Int

)