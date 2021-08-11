package com.tmobile.mytmobile.echolocate.lte.model

import com.tmobile.mytmobile.echolocate.lte.utils.LteConstants
import com.tmobile.mytmobile.echolocate.utils.EchoLocateDateUtils

/**
 * The bearer configuration contains the network bearer information of the device.
 */
data class BearerConfiguration(

    /**
     * Holds the list of bearers
     */
    val bearer: List<Bearer>?,

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
){
    constructor() : this(
        listOf(Bearer("",0)),"",
        LteConstants.NUMBERS_OF_ACTIVE_BEARERS_UNAVAILABLE_VALUE.toString(), EchoLocateDateUtils.getTriggerTimeStamp()
    )
}