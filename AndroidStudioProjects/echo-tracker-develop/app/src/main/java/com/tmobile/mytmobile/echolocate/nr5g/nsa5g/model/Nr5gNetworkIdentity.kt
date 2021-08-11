package com.tmobile.mytmobile.echolocate.nr5g.nsa5g.model

import com.google.gson.annotations.SerializedName

/**
 * Model class that holds call Nr5gNetworkIdentity data
 *
 * Serialized name annotation for all variables to be in JSON,
 * and it will be helpful while doing progaurding
 */
data class Nr5gNetworkIdentity(

    /**
     * Returns timestamp in UNIX epoch time as a UTC string, "yyyy-MM-dd'T'HH:mm:ss.SSSZ" e.g.
     * 2019-06-24T18:57:23.567+0000
     * Notes:  This timestamp can be rendered as an integer string which will correspond to the above UTC
     * dates.
     */
    val timestamp: String,

    /**
     * Returns the enumerated type of network as an integer which may have the following values:

    [-2] – NOT_AVAILABLE
    [0] – SEARCHING
    [1] – LTE orLTE with EN-DC
    [2] – UMTS
    [3] – EDGE
    [4] – GPRS
     */
    val networkType: Int,

    /**
     * Returns the 3-digit MCC of the connected network.
     * Notes: Returns -2 if no data is available
     */
    @SerializedName("MCC")
    val mcc: String,

    /**
     * Returns the MNC of the connected network. MNC is either a 1, 2 or 3 digit number.
     * Notes: Returns -2 if no data is available
     */
    @SerializedName("MNC")
    val mnc: String,
    /**
     * Returns the cell ID (CID) of the primary cell for the 1st carrier

    Notes: Returns -2 if no data is available
     */
    val primaryCid: Int,

    /**
     * Returns the physical cell identification (PCI) of the primary cell for the first carrier.

    Notes: Returns -2 if no data is available
     */
    val primaryPci: Int,

    /**
     * Returns the physical cell identification (PCI) of the primary cell for the second carrier.

    Notes: Returns -2 if no data is available
     */
    val secondaryPci: Int,

    /**
     * Returns the physical cell identification (PCI) of the primary cell for the third carrier.

    Notes: Returns -2 if no data is available
     */
    val thirdPci: Int
)