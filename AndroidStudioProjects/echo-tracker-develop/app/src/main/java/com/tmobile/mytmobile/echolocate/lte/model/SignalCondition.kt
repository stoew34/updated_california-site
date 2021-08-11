package com.tmobile.mytmobile.echolocate.lte.model

import com.google.gson.annotations.SerializedName
import com.tmobile.mytmobile.echolocate.lte.utils.LteConstants
import com.tmobile.mytmobile.echolocate.utils.EchoLocateDateUtils

/**
 * The signal condition helps in analyzing the health of the signal received by the phone
 */
data class SignalCondition(
    /**
     * Power headroom levels based on 3GPP Based on Section 6.1.3.6 in 3GPP TS 36.321 (v.12.5.0).
     * Value varies from 0 to 63
     * Report -2 if not available even in LTE.
     */
    @SerializedName("LTEULHeadroom")
    val lteUlHeadroom: Int? = LteConstants.LTE_UL_HEADROOM_UNAVAILABLE_VALUE,

    /**
     * Random Access Channel
     * The power level in the last successful RACH request in dBm.
     * Report -150 if not available even in LTE.
     */
    @SerializedName("RACHPower")
    val rachPower: Int? = LteConstants.RACH_POWER_UNAVAILABLE_VALUE,

    /**
     * Reference Signal Received Power
     * RSRP level. Report -150 if RSRP is not available even in LTE
     * <p>
     * Equals to ‘-999’ if it’s not LTE
     */
    @SerializedName("RSRP")
    val rsrp: Int? = LteConstants.RSRP_UNAVAILABLE_VALUE,

    /**
     * Reference Signal Received Quality
     * RSRQ level. Report -50 if RSRQ is not available even	 in LTE
     * <p>
     * Equals to ‘-999’ if it’s not LTE
     */
    @SerializedName("RSRQ")
    val rsrq: Int? = LteConstants.RSRQ_UNAVAILABLE_VALUE,

    /**
     * Received Signal Strength Indicator
     * RSSI level. Report -150 if RSSI is not available even in	 LTE
     * <p>
     * Equals to ‘-999’ if it’s not LTE
     */
    @SerializedName("RSSI")
    val rssi: Int? = LteConstants.RSSI_UNAVAILABLE_VALUE,

    /**
     * The signal-to-interference-plus-noise ratio
     * SINR level. Report -50 if SINR is not available even in	 LTE.
     * <p>
     * Equals to ‘-999’ if it’s not LTE
     */
    @SerializedName("SINR")
    val sinr: Int? = LteConstants.SINR_UNAVAILABLE_VALUE,

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
    val oemTimestamp: String?,

    /**
     * Contains the cell information for the secondary cell.
     */
    val secondCarrier: SecondCarrier?,

    /**
     * Contains the cell information for the tertiary cell.
     */
    val thirdCarrier: ThirdCarrier?
){
    constructor() : this(LteConstants.LTE_UL_HEADROOM_UNAVAILABLE_VALUE,
        LteConstants.RACH_POWER_UNAVAILABLE_VALUE,
        LteConstants.RSRP_UNAVAILABLE_VALUE,
        LteConstants.RSRQ_UNAVAILABLE_VALUE,
        LteConstants.RSSI_UNAVAILABLE_VALUE,
        LteConstants.SINR_UNAVAILABLE_VALUE,
        "", EchoLocateDateUtils.getTriggerTimeStamp(), SecondCarrier(), ThirdCarrier()
    )
}
