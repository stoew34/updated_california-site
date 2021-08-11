package com.tmobile.mytmobile.echolocate.nr5g.nsa5g.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import com.tmobile.mytmobile.echolocate.nr5g.nsa5g.database.Nr5gDatabaseConstants

/**
 * class that declares all variables of Nr5gActiveNetworkEntity
 * These are columns stored in the room data base for Nr5gActiveNetworkEntity
 */
@Entity(
    tableName = Nr5gDatabaseConstants.NR5G_GET_ACTIVE_NETWORK_TABLE_NAME, foreignKeys = [ForeignKey(
        entity = BaseEchoLocateNr5gEntity::class,
        parentColumns = arrayOf("sessionId"),
        childColumns = arrayOf("sessionId"),
        onDelete = ForeignKey.CASCADE
    )]
)
data class Nr5gActiveNetworkEntity(

    /**
     * Returns a string for the current default network or null if no default network is currently active, this integer corresponds to the following strings:
    [0] TRANSPORT_CELLULAR
    [1] TRANSPORT_WIFI
    [2] TRANSPORT_BLUETOOTH
    [3] TRANSPORT_ETHERNET
    [4] TRANSPORT_VPN
    [5] TRANSPORT_WIFI_AWARE
    [6] TRANSPORT_LOWPAN
     */
    val getActiveNetwork: Int
) : BaseEntity("", "")