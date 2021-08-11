package com.tmobile.mytmobile.echolocate.nr5g.sa5g.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import com.tmobile.mytmobile.echolocate.nr5g.sa5g.database.Sa5gDatabaseConstants

/**
 * class that declares all variables of Sa5gOEMSVEntity
 * These are columns stored in the room data base for Sa5gOEMSVEntity
 */
@Entity(
    tableName = Sa5gDatabaseConstants.SA5G_RRC_LOG_TABLE_NAME, foreignKeys = [ForeignKey(
        entity = BaseEchoLocateSa5gEntity::class,
        parentColumns = arrayOf("sessionId"),
        childColumns = arrayOf("sessionId"),
        onDelete = ForeignKey.CASCADE
    )]
)
data class Sa5gRrcLogEntity(

    /**
     * Returns [CONNECTED|IDLE]
     * UE in API version 1 shall return the LTE RRC (Radio Resource Control) state as follows:
     * <p>
     * CONNECTED: LTE RRC CONNECTED state
     * IDLE: LTE RRC IDLE state
     */
    val lteRrcState: String?,

    /**
     * Returns [CONNECTED|IDLE|INACTIVE]
     * UE in API version 1 shall return the NR RRC state as follows:
     * <p>
     * CONNECTED: NR RRC CONNECTED state
     * INACTIVE: NR RRC INACTIVE state
     * IDLE: NR RRC IDLE state
     */
    val nrRrcState: String?

) : BaseEntity("", "")