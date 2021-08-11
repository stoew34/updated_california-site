package com.tmobile.mytmobile.echolocate.voice.model

/**
 * Model class that has detailed call state data
 * */
data class DetailedCallStateData(

        /**
         * Call code extra delivered with detailed call state intent.
         * Numerical non-access stratum reason for call control as defined in 3GPP 24.008,
         * sections H.1 - H.7, if applicable when the CallState is ENDED. For radio link failure at RRC layer or
         * other type of lower layer failures (i.e., hardware/firmware failure at device),
         * 65535 (0xffff) shall be used. "NA" if unavailable.
         */
        val callCode: String,

        /**
         * State of the call currently being made. Can be:
         * ATTEMPTING, ESTABLISHED,
         * CONNECTED, DISCONNECTING, FAILED, HELD, ENDED,
         * INCOMING, MUTED, UNMUTED, CSFB_STARTED, CSFB_SUCCESSFUL,
         * CSFB_FAILED, SRVCC_STARTED, RVCC_SUCCESSFUL, SRVCC_FAILED,
         * ASRVCC_STARTED, ASRVCC_SUCCESSFUL, ASRVCC_FAILED, EPDG_HO_STARTED,
         * EPDG_HO_SUCCESSFUL, EPDG_HO_FAILED.
         */
        val callState: String,
        /**
         * convert the timestamp as received in the intent from the OEMs to ISO timestamp
         * with format yyyy-MM-dd'T'HH:mm:ss.SSSZ. For example: 2018-05-16T16:14:10.456-0700
         */
        val oemTimestamp: String,
        /**
         * Timestamp when the event is received by the application.
         * Returns ISO timestamp with format yyyy-MM-dd'T'HH:mm:ss.SSSZ. For example: 2018-05-16T16:14:10.456-0700
         */
        var eventTimestamp: String,

        /**
         * event info
         */
        var eventInfo: EventInfo?
)