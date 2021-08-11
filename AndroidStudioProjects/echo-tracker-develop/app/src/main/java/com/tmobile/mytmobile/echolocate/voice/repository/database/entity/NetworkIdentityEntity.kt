package com.tmobile.mytmobile.echolocate.voice.repository.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import com.tmobile.mytmobile.echolocate.voice.repository.database.VoiceDatabaseConstants

/**
 * Class that declare all the variables of network identity entity
 * These are columns stored in the room data base for voice network identity entity
 */

@Entity(
        tableName = VoiceDatabaseConstants.VOICE_NETWORK_IDENTITY_TABLE_NAME, foreignKeys = [ForeignKey(
        entity = BaseEchoLocateVoiceEntity::class,
        parentColumns = arrayOf("sessionId"),
        childColumns = arrayOf("callId"),
        onDelete = ForeignKey.CASCADE
)]
)
data class NetworkIdentityEntity(
        /**
         * Network operator info data
         * Mobile Country Code
         */
        val mcc: String,
        /**
         * Network operator info data
         * Mobile Network Code
         */
        val mnc: String

) : BaseVoiceEntity("", "")