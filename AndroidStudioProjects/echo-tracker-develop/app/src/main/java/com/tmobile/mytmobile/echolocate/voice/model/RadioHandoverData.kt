package com.tmobile.mytmobile.echolocate.voice.model

/**
 * Model call for radio handover data
 */
data class RadioHandoverData(
        /**
         * can be one of the following
         * INTER_HO_STARTED, INTER_HO_FAILED, INTER_HO_SUCCESSFUL, INTRA_HO_STARTED,
         * INTRA_HO_FAILED, INTRA_HO_SUCCESSFUL, MEASUREMENT_REPORT_DELIVERED
         */
        val handoverState: String,
        /**
         *convert the timestamp as received in the intent from the OEMs to ISO timestamp
         * with format yyyy-MM-dd'T'HH:mm:ss.SSSZ. For example: 2018-05-16T16:14:10.456-0700
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