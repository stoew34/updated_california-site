package com.tmobile.mytmobile.echolocate.lte.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import com.tmobile.mytmobile.echolocate.lte.database.LteDatabaseConstants

/**
 * The bearer configuration contains the network bearer information of the device.
 * These are columns stored in the room data base for Bearer configuration Entity
 */
@Entity(
    tableName = LteDatabaseConstants.LTE_BEARER_CONFIGURATION_TABLE_NAME, foreignKeys = [ForeignKey(
        entity = BaseEchoLocateLteEntity::class,
        parentColumns = arrayOf("sessionId"),
        childColumns = arrayOf("sessionId"),
        onDelete = ForeignKey.CASCADE
    )]
)

data class BearerConfigurationEntity(

    /**
     * Usually should have values: VOLTE, WFC2, WFC1, 3G, 2G, SEARCHING, AIRPLANE, VIDEO.
     */
    val networkType: String?,

    /**
     * UE in API version 1 shall report the number of active bearers. Report -999 if the network
     * type is not LTE. Report -2 if no data available even in LTE.
     */
    val numberOfBearers: String?,

    /**
     * UE in API version 1 shall return timestamp in UNIX epoch time down to milliseconds
     * format yyyy-MM-dd'T'HH:mm:ss.SSSZ
     */
    val oemTimestamp: String?

) : BaseLteEntity("", "")
