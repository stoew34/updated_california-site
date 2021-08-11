package com.tmobile.mytmobile.echolocate.nr5g.sa5g.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import com.tmobile.mytmobile.echolocate.nr5g.sa5g.database.Sa5gDatabaseConstants

/**
 * class that declares all variables of Sa5gOEMSVEntity
 * These are columns stored in the room data base for Sa5gOEMSVEntity
 */
@Entity(
    tableName = Sa5gDatabaseConstants.SA5G_WIFI_STATE_TABLE_NAME, foreignKeys = [ForeignKey(
        entity = BaseEchoLocateSa5gEntity::class,
        parentColumns = arrayOf("sessionId"),
        childColumns = arrayOf("sessionId"),
        onDelete = ForeignKey.CASCADE
    )]
)
data class Sa5gWiFiStateEntity(

    /**
     * Returns an integer which corresponds to the following states:
    [0] WIFI_STATE_DISABLING
    [1] WIFI_STATE_DISABLED
    [2] WIFI_STATE_ENABLING
    [3] WIFI_STATE_ENABLED
    [4] WIFI_STATE_UNKNOWN
     */
    val getWiFiState: Int?

) : BaseEntity("", "")