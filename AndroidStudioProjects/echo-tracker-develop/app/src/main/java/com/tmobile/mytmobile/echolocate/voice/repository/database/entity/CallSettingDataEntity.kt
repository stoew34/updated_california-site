package com.tmobile.mytmobile.echolocate.voice.repository.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import com.tmobile.mytmobile.echolocate.voice.repository.database.VoiceDatabaseConstants

/**
 * Class that declare all the variables of call setting data entity
 * These are columns stored in the room data base for voice call setting data entity
 */
@Entity(tableName = VoiceDatabaseConstants.VOICE_CALL_SETTING_DATA_TABLE_NAME, foreignKeys = [ForeignKey(entity = BaseEchoLocateVoiceEntity::class,
        parentColumns = arrayOf("sessionId"),
        childColumns = arrayOf("callId"),
        onDelete = ForeignKey.CASCADE)]
)
data class CallSettingDataEntity(

        /**
         * Voice over LTE setting
         */
        val volteStatus: String,
        /**
         * Status of Wi-Fi call state
         */
        val wfcStatus: String,
        /**
         * WFCPreference for intent set to WIFIONLY | WIFIPREFFERED | CELLULAREPREFERRED | NA
         */
        val wfcPreference: String,
        /**
         * convert the timestamp as received in the intent from the OEMs to ISO timestamp with format yyyy-MM-dd'T'HH:mm:ss.SSSZ.
         * For example: 2018-05-16T16:14:10.456-0700
         */
        val oemTimestamp: String,
        /**
         *Timestamp when the event is received by the application.
         *Returns ISO timestamp with format yyyy-MM-dd'T'HH:mm:ss.SSSZ. For example: 2018-05-16T16:14:10.456-0700
         */
        val eventTimestamp: String

) : BaseVoiceEntity("", "")