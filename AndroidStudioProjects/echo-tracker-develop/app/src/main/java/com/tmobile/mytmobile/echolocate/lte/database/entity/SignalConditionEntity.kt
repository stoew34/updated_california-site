package com.tmobile.mytmobile.echolocate.lte.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import com.tmobile.mytmobile.echolocate.lte.database.LteDatabaseConstants

/**
 * The signal condition helps in analyzing the health of the signal received by the phone
 */
@Entity(
    tableName = LteDatabaseConstants.LTE_SIGNAL_CONDITION_TABLE_NAME, foreignKeys = [ForeignKey(
        entity = BaseEchoLocateLteEntity::class,
        parentColumns = arrayOf("sessionId"),
        childColumns = arrayOf("sessionId"),
        onDelete = ForeignKey.CASCADE
    )]
)

data class SignalConditionEntity(

    /**
     * Power headroom levels based on 3GPP Based on Section 6.1.3.6 in 3GPP TS 36.321 (v.12.5.0).
     * Value varies from 0 to 63
     * Report -2 if not available even in LTE.
     */
    val lteUlHeadroom: Int?,

    /**
     * Random Access Channel
     * The power level in the last successful RACH request in dBm.
     * Report -150 if not available even in LTE.
     */
    val rachPower: Int?,

    /**
     * Reference Signal Received Power
     * RSRP level. Report -150 if RSRP is not available even in LTE
     * <p>
     * Equals to ‘-999’ if it’s not LTE
     */
    val rsrp: Int?,

    /**
     * Reference Signal Received Quality
     * RSRQ level. Report -50 if RSRQ is not available even	 in LTE
     * <p>
     * Equals to ‘-999’ if it’s not LTE
     */
    val rsrq: Int?,

    /**
     * Received Signal Strength Indicator
     * RSSI level. Report -150 if RSSI is not available even in	 LTE
     * <p>
     * Equals to ‘-999’ if it’s not LTE
     */
    val rssi: Int?,

    /**
     * The signal-to-interference-plus-noise ratio
     * SINR level. Report -50 if SINR is not available even in	 LTE.
     * <p>
     * Equals to ‘-999’ if it’s not LTE
     */
    val sinr: Int?,

    /**
     * 1: LTE, 2: UMTS, 3:EDGE, 4:GPRS, 0:SEARCHING
     * Report -2 if no data available (negative 2)
     * NOTE: in general, -1 (negative 1) is to be returned if	 the requested information is not
     * supposed to be available at the time of calling this method. For instance, LAC for SCell is
     * not expected when the device is only a single carrier.
     * NOTE2: -2 (negative 2) is to be returned if the requested information is not available even
     * if it is expected.
     */
    val networkType: String?,

    /**
     * convert the timestamp as received in the intent from the OEMs to ISO timestamp with format
     * yyyy-MM-dd'T'HH:mm:ss.SSSZ. For example: 2018-05-16T16:14:10.456-0700.
     */
    val oemTimestamp: String?


) : BaseLteEntity("", "")
