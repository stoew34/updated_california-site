package com.tmobile.mytmobile.echolocate.nr5g.sa5g.model

import com.google.gson.annotations.SerializedName

/**
 * Model class that holds call Sa5gWiFiState data
 *
 * Serialized name annotation for all variables to be in JSON,
 * and it will be helpful while doing progaurding
 */
data class Sa5gWiFiState(

    /**
     * Returns an integer which corresponds to the following states:
    [0] WIFI_STATE_DISABLING
    [1] WIFI_STATE_DISABLED
    [2] WIFI_STATE_ENABLING
    [3] WIFI_STATE_ENABLED
    [4] WIFI_STATE_UNKNOWN
     */
    @SerializedName("getWiFiState")
    val getWiFiState: Int?
)