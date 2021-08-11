package com.tmobile.mytmobile.echolocate.voice.model

/**
 * AppTriggeredCall INTENT ENTRY DATA
 * <p/>
 */
data class AppTriggeredCallData(

        /**
         * Name of Application
         */
        val appName: String,
        /**
         * Package name of application
         */
        val appPackageId: String,
        /**
         * Version code of application
         */
        val appVersionCode: String,
        /**
         * Version name of application
         */
        val appVersionName: String,
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
         * baseeventInfodata
         */
        var eventInfo: EventInfo?
)