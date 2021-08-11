package com.tmobile.mytmobile.echolocate.voice.repository.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import com.tmobile.mytmobile.echolocate.voice.repository.database.VoiceDatabaseConstants

/**
 * AppTriggeredCall INTENT ENTRY DATA
 * <p/>
 */
@Entity(tableName = VoiceDatabaseConstants.VOICE_APP_TRIGGERED_CALL_DATA_TABLE_NAME, foreignKeys = [ForeignKey(entity = BaseEchoLocateVoiceEntity::class,
        parentColumns = arrayOf("sessionId"),
        childColumns = arrayOf("callId"),
        onDelete = ForeignKey.CASCADE)]
)
data class AppTriggeredCallDataEntity(

        /**
         * Name of Application
         */
        val appName: String,
        /**
         * Package name of application
         */
        val appPackageId: String,
        /**
         * Version code of application
         */
        val appVersionCode: String,
        /**
         * Version name of application
         */
        val appVersionName: String,
        /**
         * convert the timestamp as received in the intent from the OEMs to ISO timestamp
         * with format yyyy-MM-dd'T'HH:mm:ss.SSSZ. For example: 2018-05-16T16:14:10.456-0700
         */
        val oemTimestamp: String,
        /**
         * Timestamp when the event is received by the application.
         * Returns ISO timestamp with format yyyy-MM-dd'T'HH:mm:ss.SSSZ. For example: 2018-05-16T16:14:10.456-0700
         */
        val eventTimestamp: String
) : BaseVoiceEntity("", "")