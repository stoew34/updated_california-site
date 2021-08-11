package com.tmobile.mytmobile.echolocate.voice.repository.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import com.tmobile.mytmobile.echolocate.voice.repository.database.VoiceDatabaseConstants

/**
 * Class that declare all the variables of IMS signalling entity
 * These are columns stored in the room data base for voice ims signalling entity
 */
@Entity(tableName = VoiceDatabaseConstants.VOICE_IMS_SIGNALLING_DATA_TABLE_NAME, foreignKeys = [ForeignKey(entity = BaseEchoLocateVoiceEntity::class,
        parentColumns = arrayOf("sessionId"),
        childColumns = arrayOf("callId"),
        onDelete = ForeignKey.CASCADE)]
)
data class ImsSignallingEntity(

        /**
         * Globally unique identifier for active IMS call session.
         * Set to combination of a random string and the host name or IP address.
         * Maximum length could not exceed 128 characters
         */
        val sipCallId: String,
        /**
         * Signalling message for message initialized at the start of a call,
         * incremented for each new request within a dialog.
         * Set to the Cseq line of the SIP message.
         */
        val sipCseq: String,
        /**
         * Signalling message for set to SIP messages that is sent or received during an active ims call session.
         * Set to the first line from the SIP message.
         * Maximum length could not exceed 128 characters.
         */
        val sipLine1: String,
        /**
         * One of:
         *"RECEIVED"
         *"SENT
         */
        val sipOrigin: String,
        /**
         * IMS Signalling reason that is generated.
         * Set to CANCEL | BYE. Maximum length could not exceed 64 characters.
         */
        val sipReason: String,
        /**
         * Described in RFC 2327. the content for sessions, including telephony,
         * internet radio, and multimedia applications. Intent set relevant SDP
         * details during active voice call such as Media Streams, Addresses, Ports,
         * Payload Types, Start and Stp Times and Originator.
         */
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
        val eventTimestamp: String
) : BaseVoiceEntity("", "")