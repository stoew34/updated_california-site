package com.tmobile.mytmobile.echolocate.lte.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import com.tmobile.mytmobile.echolocate.lte.database.LteDatabaseConstants

/**
 * The Common RF Configuration contains the configuration information of the common radio frequency
 * These are columns stored in the room data base
 */
@Entity(
    tableName = LteDatabaseConstants.LTE_COMMON_RF_CONFIGURATION_TABLE_NAME, foreignKeys = [ForeignKey(
        entity = BaseEchoLocateLteEntity::class,
        parentColumns = arrayOf("sessionId"),
        childColumns = arrayOf("sessionId"),
        onDelete = ForeignKey.CASCADE
    )]
)

data class CommonRFConfigurationEntity(
    /**
     * It is an integer value representing If the device is using the LTE-Unlicensed (LTEu) or Licensed Assisted Access (LAA) configuration. The client should use the following matrix:
     * 1 if LTE-U is in use.
     * 2 if LAA is in use.
     * 0 if not in use.
     * If this device does not support either LTE-U or
     * LAA, report -1
     * If failed to check this info, report -2
     * Equals to ‘-999’ if it’s not LTE
     */
    val lteULaa: Int?,

    /**
     * The Radio Resource Control (RRC) protocol is used in UMTS and LTE on the Air interface
     * 0 if RRC_IDLE or 1 if RRC_CONNECTED
     * If failed to check this information, report -2
     * Equals to ‘-999’ if it’s not LTE
     */
    val rrcState: Int?,

    /**
     * Youtube content ID
     * Value: YouTube content id if applicable.
     */
    val ytContentId: String?,

    /**
     * Youtube link
     * Value: YouTube content URL.
     */
    val ytLink: String?,

    /**
     * For instance, report 2 in case of 4x2 MIMO in use
     * If failed to check this information, report -2
     * -999 if it’s not LTE
     */
    val antennaConfigurationRx: Int?,

    /**
     * For instance, report 4 in case of 4x2 MIMO in use
     * If failed to check this information, report -2
     * -999 if it’s not LTE
     */
    val antennaConfigurationTx: Int?,

    /**
     * This field defines the type of active data connection on the device at the time of data collection
     * 1: LTE, 2: UMTS, 3:EDGE, 4:GPRS, 0:SEARCHING
     * Report -2 if no data available (negative 2)
     */
    val networkType: String?,

    /**
     * convert the timestamp as received in the intent from the OEMs to ISO timestamp with format
     * yyyy-MM-dd'T'HH:mm:ss.SSSZ. For example: 2018-05-16T16:14:10.456-0700

     */
    val oemTimestamp: String?,

    /**
     * Integer value representing the state of the Rx receiver diversity
     * 1 if RX diversity is on, 0 if off.
     * If failed to check this information, report -2
     * -999 if it’s not LTE
     */
    val receiverDiversity: Int?,

    /**
     * Transmission mode is a positive integer representing the transmission mode of the device.
     * If failed to check this information, report -2.
     * -999 if it’s not LTE
     */
    val transmissionMode: Int?
) : BaseLteEntity("", "")
