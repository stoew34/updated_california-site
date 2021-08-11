package com.tmobile.mytmobile.echolocate.nr5g.sa5g.model

import com.google.gson.annotations.SerializedName


data class Sa5gCarrierBandConfig(
    /**
     * Returns Band Config Keys
     * Example: "SAn2Enabled", "SAn66Enabled", "NONE", "ERROR"
     */
    @SerializedName("key")
    val key: String,

    /**
     * Returns Band Config Values
     * Example: "true", "false", "-1", "-2"
     */
    @SerializedName("value")
    val value: String
)