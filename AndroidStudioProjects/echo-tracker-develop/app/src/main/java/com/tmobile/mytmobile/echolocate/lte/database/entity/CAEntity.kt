package com.tmobile.mytmobile.echolocate.lte.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import com.tmobile.mytmobile.echolocate.lte.database.LteDatabaseConstants

/**
 * Carrier aggregation (CA) is used in LTE-Advanced in order to increase the bandwidth, and thereby increase the bitrate
 * These are columns stored in the room data base
 */
@Entity(
    tableName = LteDatabaseConstants.LTE_CA_TABLE_NAME,
    foreignKeys = [ForeignKey(
        entity = BaseEchoLocateLteEntity::class,
        parentColumns = arrayOf("sessionId"),
        childColumns = arrayOf("sessionId"),
        onDelete = ForeignKey.CASCADE
    )]
)

data class CAEntity(
    /**
     * Positive integer representing EUTRA absolute radio-frequency channel number.
     */
    val earfcn: Int?,

    /**
     *Integer value representing band number of the CA.
     * E.g. 4 if Band 4
     * <p>
     * Equals to ‘-999’ if it’s not LTE
     */
    val bandNumber: Int?,

    /**
     * Positive integer representing bandwidth in MHZ.
     */
    val bandWidth: Int?,

    /**
     * The position of the item in the array starting from 1.
     */
    val carrierNum: Int?,

    /**
     * Integer value representing number of layers used.
     */
    val layers: Int?,

    /**
     *  String representing the modulation scheme in use.
     */
    val modulation: String?,

    /**
     * String value representing the physical cell identification.
     */
    val pci: String?,

    /**
     * String value representing the id of the cell.
     */
    val cellId: String?,

    /**
     *  String value representing the id of the location.
     */
    val locationId: String?
) : BaseEntity("", "", "")

