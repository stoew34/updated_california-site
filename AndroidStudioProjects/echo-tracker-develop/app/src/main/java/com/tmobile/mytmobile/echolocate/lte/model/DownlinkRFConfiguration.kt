package com.tmobile.mytmobile.echolocate.lte.model

import com.google.gson.annotations.SerializedName
import com.tmobile.mytmobile.echolocate.utils.EchoLocateDateUtils

/**
 * Downlink radio frequency represents the information about the device's downlink radio frequency
 * configuration such as number of layers used, the name of the modulation schema used and network type.
 */
data class DownlinkRFConfiguration(

    /**
     * List of Carrier aggregation (CA) that is used in LTE-Advanced in order to increase the bandwidth, and thereby increase the bitrate
     */
    @SerializedName("CA")
    val ca: List<CAData>?,

    /**
     * This field defines the type of active data connection on the device at the time of data
     * collection
     * 1: LTE, 2: UMTS, 3:EDGE, 4:GPRS, 0:SEARCHING
     * Report -2 if no data available (negative 2)
     */
    val networkType: String?,

    /**
     * convert the timestamp as received in the intent from the OEMs to ISO timestamp with format
     * yyyy-MM-dd'T'HH:mm:ss.SSSZ. For example: 2018-05-16T16:14:10.456-0700.
     */
    val oemTimestamp: String?
){
    constructor() : this(
        listOf(CAData(null,null,null,0,0,"",
            null,null,null)),
        "", EchoLocateDateUtils.getTriggerTimeStamp()
    )
}