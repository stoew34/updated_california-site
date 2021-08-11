package com.tmobile.mytmobile.echolocate.voice.repository.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import com.tmobile.mytmobile.echolocate.voice.repository.database.VoiceDatabaseConstants

/**
 * Class that declare all the variables of cell info entity
 * These are columns stored in the room data base for voice cell info entity
 */
@Entity(tableName = VoiceDatabaseConstants.VOICE_CELL_INFO_DATA_TABLE_NAME, foreignKeys = [ForeignKey(entity = BaseEchoLocateVoiceEntity::class,
        parentColumns = arrayOf("sessionId"),
        childColumns = arrayOf("callId"),
        onDelete = ForeignKey.CASCADE)]
)
data class CellInfoEntity(

        /**
         *Network signal state for ECIO delivered as an extra from echo locate intents.
         */
        val ecio: String,
        /**
         *Network signal state for RSCP delivered as an extra from echo locate intents.
         */
        val rscp: String,
        /**
         *Network signal state for RSRP delivered as an extra from echo locate intents.
         */
        val rsrp: String,
        /**
         *Network signal state for RSRQ delivered as an extra from echo locate intents.
         */
        val rsrq: String,
        /**
         *Network signal state for RSSI delivered as an extra from echo locate intents.
         */
        val rssi: String,
        /**
         *Network signal state for SINR delivered as an extra from echo locate intents.
         */
        val sinr: String,
        /**
         *Network signal state for SNR delivered as an extra from echo locate intents.
         */
        val snr: String,
        /**
         *16-bit Location Area Code, UNAVAILABLE if unavailable.
         */
        val lac: String,
        /**
         *Extra delivered with every EchoLocateIntent. Usually will contain data about
         * bands states divided by commas: i.e. 2, 4, 5, 12, 66, 71..
         */
        val networkBand: String,
        /**
         *Cell id the phone was connected to when the call took place.
         */
        val cellId: String,
        /**
         * Usually should have values: VOLTE, WFC2, WFC1, 3G, 2G, SEARCHING, AIRPLANE, VIDEO.
         */
        val networkType: String
) : BaseVoiceEntity("", "")