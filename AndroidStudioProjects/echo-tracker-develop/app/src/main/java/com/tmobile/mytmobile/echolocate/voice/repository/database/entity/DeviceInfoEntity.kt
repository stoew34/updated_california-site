package com.tmobile.mytmobile.echolocate.voice.repository.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import com.tmobile.mytmobile.echolocate.voice.repository.database.VoiceDatabaseConstants

/**
 * Class that declare all the variables of device info entity
 * These are columns stored in the room data base for voice device info entity
 */
@Entity(tableName = VoiceDatabaseConstants.VOICE_DEVICE_INFO_TABLE_NAME, foreignKeys = [ForeignKey(entity = BaseEchoLocateVoiceEntity::class,
        parentColumns = arrayOf("sessionId"),
        childColumns = arrayOf("callId"),
        onDelete = ForeignKey.CASCADE)]
)
data class DeviceInfoEntity(

        /**
         * UUID value of the device
         */
        val uuid: String,
        /**
         * IMEI (International Mobile Equipment Identifier)
         */
        val imei: String,
        /**
         * IMSI of the device
         */
        val imsi: String,
        /**
         * MSISDN of the device
         */
        val msisdn: String,
        /**
         * sessionID of the device
         */
        val testSessionID: String
) : BaseVoiceEntity("", "")