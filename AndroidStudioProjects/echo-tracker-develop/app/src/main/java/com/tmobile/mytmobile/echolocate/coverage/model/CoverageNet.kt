package com.tmobile.mytmobile.echolocate.coverage.model

data class CoverageNet(

    /**
    Reports the type of network.
     */
    val connectivityType: String?,

    /**
    Indicates whether the device is currently roaming on this network.
    network info from which roaming state is determined. Ex:false
     */
    val roamingData: String?,

    /**
     * Class that declares all variables of CoverageConnectedWifiStatus
     */
    val connectedWifiStatus: CoverageConnectedWifiStatus?
) {
    constructor() : this (
        null, null, null
    )
}