package com.tmobile.mytmobile.echolocate.voice.model

import com.tmobile.mytmobile.echolocate.standarddatablocks.DeviceInfo

/**
 * Base class for all EchoLocate intents. Contains all fields common for all EchoLocate intents
 */
data class VoiceSingleSessionReport(

        /**
         * Marks number of intents that were dropped due to incompatibility with specification.
         */
        val numDiscardedIntents: Int,
        /**
         * The version of the schema for which the data is being reported.
         */
        val schemaVersion: String,

        /**
         * column "Status" with data type String,
         *  so that all the session Ids can be tracked if they are already processed or not.
         */
        @Transient
        val status: String,

        /**
         * call session data list
         */
        val callSessions: List<CallSessions>, // check if we can use VoiceReportEntity
        /**
         * location details
         */
        val location: LocationData?,
        /**
         * device info
         */
        val deviceInfo: DeviceInfo
)