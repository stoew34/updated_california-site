package com.tmobile.mytmobile.echolocate.voice.repository.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import com.tmobile.mytmobile.echolocate.voice.repository.database.VoiceDatabaseConstants

/**
 * Class that declare all the variables of voice location entity.
 * These are columns stored in the voice room data base for voice location entity
 */
@Entity(tableName = VoiceDatabaseConstants.VOICE_LOCATION_TABLE_NAME, foreignKeys = [ForeignKey(entity = BaseEchoLocateVoiceEntity::class,
        parentColumns = arrayOf("sessionId"),
        childColumns = arrayOf("callId"),
        onDelete = ForeignKey.CASCADE)]
)
data class VoiceLocationEntity(

        /**
         * This is the double value representing the altitude of the location of the device at the time of the event.
         */
        var altitude: Double? = 0.0,
        /**
         * This is the double value representing precision of the location altitude of the device at the time of the event.
         */
        var altitudePrecision: Float? = 0.0F,
        /***
         * This is the double value of the user's location altitude at the time of the scan. 'BALANCED_POWER' option may be used to fix the location.
         */
        var latitude: Double? = 0.0,
        /**
         * This is the double value of the user's location longitude at the time of the scan. 'BALANCED_POWER' option may be used to fix the location.
         */
        var longitude: Double? = 0.0,
        /**
         * This is the double value of the user's location precision, also known as the location accuracy at the time of the scan
         */
        var precision: Float? = 0.0F,
        /**
         * This is the value in nanoSeconds representing the difference in the time between the occurrence of the event and the location time.
         */
        var locationAge: Long? = 0
) : BaseVoiceEntity("", "")