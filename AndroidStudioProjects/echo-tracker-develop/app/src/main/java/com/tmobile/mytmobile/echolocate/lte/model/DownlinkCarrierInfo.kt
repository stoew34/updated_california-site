package com.tmobile.mytmobile.echolocate.lte.model

import com.google.gson.annotations.SerializedName
import com.tmobile.mytmobile.echolocate.utils.EchoLocateDateUtils

/**
 * Downlink carrier info should contain the information about the carrier aggregation which helps in determining
 */
data class DownlinkCarrierInfo(

    /**
     * List of Carrier aggregation (CA) that is used in LTE-Advanced in order to increase the bandwidth, and thereby increase the bitrate
     */
    @SerializedName("CA")
    val ca: List<CAData>?,

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
){
    constructor() : this(
        listOf(CAData(0,0,0,0,null,null,
            null,null,null)),
        "",0, EchoLocateDateUtils.getTriggerTimeStamp()
    )
}