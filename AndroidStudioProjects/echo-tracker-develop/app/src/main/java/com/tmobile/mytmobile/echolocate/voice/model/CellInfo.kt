package com.tmobile.mytmobile.echolocate.voice.model

import com.google.gson.annotations.SerializedName

/**
 * Model class that holds cell info data
 * */
data class CellInfo(

        /**
         *Network signal state for ECIO delivered as an extra from echo locate intents.
         */
        @SerializedName("ECIO")
        var ecio: String,
        /**
         *Network signal state for RSCP delivered as an extra from echo locate intents.
         */
        @SerializedName("RSCP")
        var rscp: String,
        /**
         *Network signal state for RSRP delivered as an extra from echo locate intents.
         */
        @SerializedName("RSRP")
        var rsrp: String,
        /**
         *Network signal state for RSRQ delivered as an extra from echo locate intents.
         */
        @SerializedName("RSRQ")
        var rsrq: String,
        /**
         *Network signal state for RSSI delivered as an extra from echo locate intents.
         */
        @SerializedName("RSSI")
        var rssi: String,
        /**
         *Network signal state for SINR delivered as an extra from echo locate intents.
         */
        @SerializedName("SINR")
        var sinr: String,
        /**
         *Network signal state for SNR delivered as an extra from echo locate intents.
         */
        @SerializedName("SNR")
        var snr: String,
        /**
         *16-bit Location Area Code, UNAVAILABLE if unavailable.
         */
        @SerializedName("LAC")
        var lac: String,
        /**
         *Extra delivered with every EchoLocateIntent. Usually will contain data about
         * bands states divided by commas: i.e. 2, 4, 5, 12, 66, 71..
         */
        var networkBand: String,
        /**
         *Cell id the phone was connected to when the call took place.
         */
        var cellId: String,
        /**
         * Usually should have values: VOLTE, WFC2, WFC1, 3G, 2G, SEARCHING, AIRPLANE, VIDEO.
         */
        var networkType: String
)