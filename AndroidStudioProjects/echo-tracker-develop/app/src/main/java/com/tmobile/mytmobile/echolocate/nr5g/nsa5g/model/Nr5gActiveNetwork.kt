package com.tmobile.mytmobile.echolocate.nr5g.nsa5g.model

/**
 * Model class that holds call Nr5gActiveNetwork data
 *
 * Serialized name annotation for all variables to be in JSON,
 * and it will be helpful while doing progaurding
 */
data class Nr5gActiveNetwork(

    /**
     * Returns a string for the current default network or null if no default network is currently active, this integer corresponds to the following strings:
    [0] TRANSPORT_CELLULAR
    [1] TRANSPORT_WIFI
    [2] TRANSPORT_BLUETOOTH
    [3] TRANSPORT_ETHERNET
    [4] TRANSPORT_VPN
    [5] TRANSPORT_WIFI_AWARE
    [6] TRANSPORT_LOWPAN
     */
    val getActiveNetwork: Int
)