package com.tmobile.mytmobile.echolocate.voice.model

import com.google.gson.annotations.SerializedName


/**
 * Model call for network opperator info data
 */
data class NetworkIdentity(
        /**
         * Network operator info data
         * Mobile Country Code
         */
        @SerializedName("MCC")
        val mcc: String,
        /**
         * Network operator info data
         * Mobile Network Code
         */
        @SerializedName("MNC")
        val mnc: String
)