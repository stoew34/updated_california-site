package com.tmobile.mytmobile.echolocate.voice.repository.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import com.tmobile.mytmobile.echolocate.voice.repository.database.VoiceDatabaseConstants

/**
 * Class that declare all the variables of OEM software version entity
 * These are columns stored in the room data base for OEM software version entity
 */
@Entity(tableName = VoiceDatabaseConstants.VOICE_OEM_SOFTWARE_VERSION_TABLE_NAME, foreignKeys = [ForeignKey(entity = BaseEchoLocateVoiceEntity::class,
        parentColumns = arrayOf("sessionId"),
        childColumns = arrayOf("callId"),
        onDelete = ForeignKey.CASCADE)]
)
data class OEMSoftwareVersionEntity(
        /**
         * Build Id of Android
         */
        val softwareVersion: String,

        /**
         * Custom version of application
         */
        val customVersion: String,
        /**
         * Radio version of application
         */
        val radioVersion: String,
        /**
         * Build name of application
         *
         * Build number of application
         */
        val buildName: String,
        /**
         * Android SDK Version
         */
        val androidVersion: String
) : BaseVoiceEntity("", "")