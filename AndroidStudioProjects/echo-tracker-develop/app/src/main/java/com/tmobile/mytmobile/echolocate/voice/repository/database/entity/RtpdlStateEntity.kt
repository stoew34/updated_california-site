package com.tmobile.mytmobile.echolocate.voice.repository.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import com.tmobile.mytmobile.echolocate.voice.repository.database.VoiceDatabaseConstants


/**
 * Class that declare all the variables of rtpdl state entity
 * These are columns stored in the room data base for voice rtpdl state entity
 */
@Entity(tableName = VoiceDatabaseConstants.VOICE_RTPDL_STATE_DATA_TABLE_NAME, foreignKeys = [ForeignKey(entity = BaseEchoLocateVoiceEntity::class,
        parentColumns = arrayOf("sessionId"),
        childColumns = arrayOf("callId"),
        onDelete = ForeignKey.CASCADE)]
)
data class RtpdlStateEntity(
        /**
         * delay as the difference between end to end round trip delay
         * between selected packets in a flow with any lost packets being ignored.
         */
        val delay: Double,
        /**
         * Sequence number
         */
        val sequence: Double,
        /**
         * Derived from the sending side, the delay between each packet due
         * to network congestion, improper queuing or configuration errors.
         */
        val jitter: Double,
        /**
         * Loss rate intent for the number of packets expected but actually received from each source.
         * Set to a number derived from the calculated number of RTP packets lost divided by
         * the number of RTP packets that were sent from the source.
         */
        val lossRate: Double,
        /**
         * Defined in RFC 6958. derived from the measurement period in
         * the Measurement information block
         */
        val measuredPeriod: Double,
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