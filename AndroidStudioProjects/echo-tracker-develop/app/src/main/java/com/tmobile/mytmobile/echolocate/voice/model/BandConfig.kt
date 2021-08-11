package com.tmobile.mytmobile.echolocate.voice.model

import com.google.gson.annotations.SerializedName

data class BandConfig(
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