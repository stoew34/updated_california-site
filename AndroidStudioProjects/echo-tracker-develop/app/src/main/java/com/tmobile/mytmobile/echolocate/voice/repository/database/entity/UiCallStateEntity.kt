package com.tmobile.mytmobile.echolocate.voice.repository.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import com.tmobile.mytmobile.echolocate.voice.repository.database.VoiceDatabaseConstants

/**
 * Class that declare all the variables of voice ui call state entity.
 * These are columns stored in the room data base for voice call state entity
 */

@Entity(tableName = VoiceDatabaseConstants.VOICE_UI_CALL_STATE_DATA_TABLE_NAME, foreignKeys = [ForeignKey(entity = BaseEchoLocateVoiceEntity::class,
        parentColumns = arrayOf("sessionId"),
        childColumns = arrayOf("callId"),
        onDelete = ForeignKey.CASCADE)]
)
data class UiCallStateEntity(

        /**
         *Reported as String as one of the below mentioned values.
         * CALL_PRESSED
         * RINGING,
         * CALL_CONNECTED,
         * END_PRESSED,
         * CALL_DISCONNECTED
         */
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
        val eventTimestamp: String
) : BaseVoiceEntity("", "")