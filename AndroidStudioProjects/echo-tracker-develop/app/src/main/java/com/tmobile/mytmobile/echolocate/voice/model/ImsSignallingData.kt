package com.tmobile.mytmobile.echolocate.voice.model

import com.google.gson.annotations.SerializedName


/**
 * Model call for the IMS Signalling data
 */
data class ImsSignallingData(

        /**
         * Globally unique identifier for active IMS call session.
         * Set to combination of a random string and the host name or IP address.
         * Maximum length could not exceed 128 characters
         */
        @SerializedName("SIPCallId")
        val sipCallId: String,
        /**
         * Signalling message for message initialized at the start of a call,
         * incremented for each new request within a dialog.
         * Set to the Cseq line of the SIP message.
         */
        @SerializedName("SIPCseq")
        val sipCseq: String,
        /**
         * Signalling message for set to SIP messages that is sent or received during an active ims call session.
         * Set to the first line from the SIP message.
         * Maximum length could not exceed 128 characters.
         */
        @SerializedName("SIPLine1")
        val sipLine1: String,
        /**
         * One of:
         *"RECEIVED"
         *"SENT
         */
        @SerializedName("SIPOrigin")
        val sipOrigin: String,
        /**
         * IMS Signalling reason that is generated.
         * Set to CANCEL | BYE. Maximum length could not exceed 64 characters.
         */
        @SerializedName("SIPReason")
        val sipReason: String,
        /**
         * Described in RFC 2327. the content for sessions, including telephony,
         * internet radio, and multimedia applications. Intent set relevant SDP
         * details during active voice call such as Media Streams, Addresses, Ports,
         * Payload Types, Start and Stp Times and Originator.
         */
        @SerializedName("SIPSDP")
        val sipSDP: String,
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