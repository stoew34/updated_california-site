package com.tmobile.mytmobile.echolocate.lte.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import com.tmobile.mytmobile.echolocate.lte.database.LteDatabaseConstants

/**
 * Downlink carrier info should contain the information about the carrier aggregation which helps in determining
 * These are columns stored in the room data base
 */
@Entity(
    tableName = LteDatabaseConstants.LTE_DOWNLINK_CARRIER_INO_TABLE_NAME, foreignKeys = [ForeignKey(
        entity = BaseEchoLocateLteEntity::class,
        parentColumns = arrayOf("sessionId"),
        childColumns = arrayOf("sessionId"),
        onDelete = ForeignKey.CASCADE
    )]
)

data class DownLinkCarrierInfoEntity(

    /**
     * Type of active data connection on the device at the time of data collection
     * 1: LTE, 2: UMTS, 3:EDGE, 4:GPRS, 0:SEARCHING
     * Report -2 if no data available (negative 2)
     */
    val networkType: String?,

    /**
     * Aggregated channel number is a positive integer representing the number of aggregated channels available. For instance, 3 for 3 CA.
     */
    val numberAggregatedChannel: Int?,

    /**
     * convert the timestamp as received in the intent from the OEMs to ISO timestamp with format yyyy-MM-dd'T'HH:mm:ss.SSSZ. For example: 2018-05-16T16:14:10.456-0700.
     */
    val oemTimestamp: String?
) : BaseLteEntity("", "")
