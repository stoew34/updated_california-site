package com.tmobile.mytmobile.echolocate.voice.repository.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import com.tmobile.mytmobile.echolocate.voice.repository.database.VoiceDatabaseConstants

/**
 * Class that declare all the variables of radio hand over entity
 * These are columns stored in the room data base for voice hand over entity
 */
@Entity(tableName = VoiceDatabaseConstants.VOICE_RADIO_HANDOVER_DATA_TABLE_NAME, foreignKeys = [ForeignKey(entity = BaseEchoLocateVoiceEntity::class,
        parentColumns = arrayOf("sessionId"),
        childColumns = arrayOf("callId"),
        onDelete = ForeignKey.CASCADE)]
)
data class RadioHandoverEntity(
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
        val eventTimestamp: String
) : BaseVoiceEntity("", "")