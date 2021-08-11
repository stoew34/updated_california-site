package com.tmobile.mytmobile.echolocate.lte.model

import com.google.gson.annotations.SerializedName
import com.tmobile.mytmobile.echolocate.lte.utils.LteConstants

/**
 * An array containing information about all the APN name and their corresponding QCI values
 * available on the device. QoS Class Identifier (QCI) is a mechanism used in 3GPP Long Term
 * Evolution (LTE) networks to ensure bearer traffic is allocated appropriate Quality of Service (QoS).
 */
data class Bearer(

    /**
     * The name of the APN for this data connection
     * E.g. fast.t-mobile.com
     * If no data connection exists, report -1.
     * If failed to check this info. Report -2
     * Equals to ‘-999’ if it’s not LTE
     */
    @SerializedName("APNName")
    val apnName: String?,

    /**
     * Type of QCI
     */
    @SerializedName("QCI")
    val qci: Int?
){
    constructor() : this("",LteConstants.QCI_UNAVAILABLE_VALUE)
}