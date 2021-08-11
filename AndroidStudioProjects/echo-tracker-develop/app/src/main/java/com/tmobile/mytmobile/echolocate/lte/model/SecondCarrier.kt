package com.tmobile.mytmobile.echolocate.lte.model

import com.google.gson.annotations.SerializedName
import com.tmobile.mytmobile.echolocate.lte.utils.LteConstants

/**
 * Contains the cell information for the secondary cell.
 */
data class SecondCarrier(
    /**
     * RSRP value of SCell in 2CA or 3CA in dbm. Report -150 if RSRP is not available even in LTE.
     * Report -999 if it's not LTE.
     */
    @SerializedName("RSRP")
    val rsrp: String?,

    /**
     * RSRQ value of SCell in 2CA or 3CA in dbm. Report -50 if RSRQ is not available even in LTE.
     * Report -999 if it's not LTE.
     */
    @SerializedName("RSRQ")
    val rsrq: String?,

    /**
     * RSSI value of SCell in 2CA or 3CA in dbm. Report -150 if SINR is not available even in LTE.
     */
    @SerializedName("RSSI")
    val rssi: String?,

    /**
     * SINR value of SCell in 2CA or 3CA in db. Report -50 if SINR is not available even in LTE.
     */
    @SerializedName("SINR")
    val sinr: String?
){
    constructor() : this(LteConstants.RSRP_UNAVAILABLE_VALUE.toString(),
        LteConstants.RSRQ_UNAVAILABLE_VALUE.toString(),
        LteConstants.RSSI_UNAVAILABLE_VALUE.toString(),
        LteConstants.SINR_UNAVAILABLE_VALUE.toString()
    )
}
