package com.tmobile.mytmobile.echolocate.voice.model


import com.google.gson.annotations.SerializedName

/**
 * Model call for call state data
 */
data class UiCallStateData(

        /**
         *Reported as String as one of the below mentioned values.
         * CALL_PRESSED
         * RINGING,
         * CALL_CONNECTED,
         * END_PRESSED,
         * CALL_DISCONNECTED
         */

        @SerializedName("UICallState")
        val uICallState: String,
        /**
         * convert the timestamp as received in the intent from the OEMs
         * to ISO timestamp with format yyyy-MM-dd'T'HH:mm:ss.SSSZ.
         * For example: 2018-05-16T16:14:10.456-0700
         */
        val oemTimestamp: String,
        /**
         * Timestamp when the event is received by the application.
         *Returns ISO timestamp with format yyyy-MM-dd'T'HH:mm:ss.SSSZ.
         *  For example: 2018-05-16T16:14:10.456-0700
         */
        var eventTimestamp: String,

        /**
         * event info
         */
        var eventInfo: EventInfo?
)