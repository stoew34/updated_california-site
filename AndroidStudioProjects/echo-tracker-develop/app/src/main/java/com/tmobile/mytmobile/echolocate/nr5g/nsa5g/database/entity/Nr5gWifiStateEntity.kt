package com.tmobile.mytmobile.echolocate.nr5g.nsa5g.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import com.tmobile.mytmobile.echolocate.nr5g.nsa5g.database.Nr5gDatabaseConstants

/**
 * class that declares all variables of Nr5gWifiStateEntity
 * These are columns stored in the room data base for Nr5gWifiStateEntity
 */
@Entity(
    tableName = Nr5gDatabaseConstants.NR5G_GET_WIFI_STATE_TABLE_NAME, foreignKeys = [ForeignKey(
        entity = BaseEchoLocateNr5gEntity::class,
        parentColumns = arrayOf("sessionId"),
        childColumns = arrayOf("sessionId"),
        onDelete = ForeignKey.CASCADE
    )]
)
data class Nr5gWifiStateEntity(

    /**
     * Returns an integer which corresponds to the following states:
    [0] WIFI_STATE_DISABLING
    [1] WIFI_STATE_DISABLED
    [2] WIFI_STATE_ENABLING
    [3] WIFI_STATE_ENABLED
    [4] WIFI_STATE_UNKNOWN
     */
    val getWiFiState: Int
) : BaseEntity("", "")