package com.tmobile.mytmobile.echolocate.voice.repository.database.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tmobile.mytmobile.echolocate.voice.repository.database.VoiceDatabaseConstants
import com.tmobile.mytmobile.echolocate.voice.model.NetworkIdentity

@Entity(tableName = VoiceDatabaseConstants.VOICE_ECHO_LOCATE_BASE_TABLE_NAME)
data class BaseEchoLocateVoiceEntity(

        /**
         * The unique call ID common to all the intents for the same call session.
         * Due to possibility of incrementing the call id from 0 for every reset of the device
         * after reboot additional number will be prepended before call ids of not sent events.
         *This value is retrieved from the OEM intent for call state.
         *Intent extra key "CallID".
         */
        @PrimaryKey(autoGenerate = false)
        var sessionId: String,

        /**
         * column "Status" with data type String,
         *  so that all the session Ids can be tracked if they are already processed or not.
         */
        var status: String,

        /**
         * The phone number of the other party on the call.
         */
        val callNumber: String,

        /**
         * The version of the client at the time of the event.
         */
        val clientVersion: String,

        /**
         * start time of the event.
         */
        val startTime: Long,

        /**
         * end time of the event.
         */
        var endTime: Long,

        /**
         * Number of discarded intents for a specific call session.
         */
        val numDiscardedIntents: Int,

        /**
         * Network operator info data.
         */
        @Embedded
        val networkIdentity: NetworkIdentity
)