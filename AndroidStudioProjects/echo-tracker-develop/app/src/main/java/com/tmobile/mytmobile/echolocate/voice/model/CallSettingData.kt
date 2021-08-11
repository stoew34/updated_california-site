package com.tmobile.mytmobile.echolocate.voice.model

import com.google.gson.annotations.SerializedName


/**
 * Model class that holds call settings data
 * */
data class CallSettingData(

        /**
         * Voice over LTE setting
         */
        val volteStatus: String,
        /**
         * Status of Wi-Fi call state
         */
        @SerializedName("WFCStatus")
        val wfcStatus: String,
        /**
         * WFCPreference for intent set to WIFIONLY | WIFIPREFFERED | CELLULAREPREFERRED | NA
         */
        @SerializedName("WFCPreference")
        val wfcPreference: String,
        /**
         * convert the timestamp as received in the intent from the OEMs to ISO timestamp with format yyyy-MM-dd'T'HH:mm:ss.SSSZ.
         * For example: 2018-05-16T16:14:10.456-0700
         */
        val oemTimestamp: String,
        /**
         *Timestamp when the event is received by the application.
         *Returns ISO timestamp with format yyyy-MM-dd'T'HH:mm:ss.SSSZ. For example: 2018-05-16T16:14:10.456-0700
         */
        var eventTimestamp: String,

        /**
         * event info
         */
        var eventInfo: EventInfo?
)