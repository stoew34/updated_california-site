package com.tmobile.mytmobile.echolocate.voice.repository.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import com.tmobile.mytmobile.echolocate.voice.repository.database.VoiceDatabaseConstants

/**
 * CarrierConfig ENTITY
 */
@Entity(
    tableName = VoiceDatabaseConstants.VOICE_CARRIER_CONFIG,
    foreignKeys = [ForeignKey(
        entity = BaseEchoLocateVoiceEntity::class,
        parentColumns = arrayOf("sessionId"),
        childColumns = arrayOf("callId"),
        onDelete = ForeignKey.CASCADE
    )]
)
data class CarrierConfigDataEntity(

    /**
     * Carrier configuration for VoNR on the device in String Extras
     */
    val carrierVoiceConfig: String,

    /**
     * Carrier configuration for disabling 5G SA connection during active VoWiFi
     * calling on the device in String Extras.
     */
    val carrierVoWiFiConfig: String,

    /**
     * Carrier configuration for the UE to access 5G SA bands in Map Extras
     */
    val standaloneBands5gKeys: String,

    /**
     * Carrier configuration for the UE to access 5G SA bands in Map Extras
     */
    val standaloneBands5gValues: String,

    /**
     * Carrier configuration file version in String Extras
     */
    val carrierConfigVersion: String,

    /**
     * Timestamp when the event is received by the application.
     * Returns ISO timestamp with format yyyy-MM-dd'T'HH:mm:ss.SSSZ. For example: 2018-05-16T16:14:10.456-0700
     */
    val eventTimestamp: String

) : BaseVoiceEntity("", "")
