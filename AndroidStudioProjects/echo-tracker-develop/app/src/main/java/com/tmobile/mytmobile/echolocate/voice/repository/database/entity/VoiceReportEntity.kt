package com.tmobile.mytmobile.echolocate.voice.repository.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tmobile.mytmobile.echolocate.voice.repository.database.VoiceDatabaseConstants

/**
 * Class that declare all the variables of voice report
 */

@Entity(tableName = VoiceDatabaseConstants.VOICE_REPORT_TABLE_NAME)

data class VoiceReportEntity(
        @PrimaryKey(autoGenerate = false)
        var voiceReportId: String,

        /**
         *JSON String
         */
        val json: String,

        /**
         * Number of device intents  -> 0(Int) -> calculation number at VoiceReportProcessor once JSON generated,
         * Number of possible intents listed in data class DeviceIntents
         */
        val numDiscardedIntents: Int,

        /**
         * Timestamp for start
         */
        val startTime: Long,

        /**
         * Timestamp for end
         */
        val endTime: Long,

        /**
         * Timestamp when the event is received by the application.
         */
        val eventTimestamp: String,

        /**
         * Report status if in process of reporting to report module
         */
        var reportStatus: String?
)

