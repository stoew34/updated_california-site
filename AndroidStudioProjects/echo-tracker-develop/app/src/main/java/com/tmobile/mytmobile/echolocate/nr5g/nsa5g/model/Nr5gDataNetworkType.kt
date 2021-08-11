package com.tmobile.mytmobile.echolocate.nr5g.nsa5g.model

/**
 * Model class that holds call Nr5gDataNetworkType data
 *
 * Serialized name annotation for all variables to be in JSON,
 * and it will be helpful while doing progaurding
 */
data class Nr5gDataNetworkType(

    /**
     * Returns timestamp in UNIX epoch time as a UTC string, "yyyy-MM-dd'T'HH:mm:ss.SSSZ" e.g. 2019-06-24T18:57:23.567+0000

    Notes:  This timestamp can be rendered as an integer string which will correspond to the above UTC dates.
     */
    val timestamp: String,

    /**
     * Returns an integer which corresponds to the following values:
    [0] – UNKNOWN
    [1] – GPRS
    [2] – EDGE
    [3] – UMTS
    [4] – CDMA
    [5] – EVDO_0
    [6] – EVDO_A
    [7] – 1xRTT
    [8] – HSDPA
    [9] – HSUPA
    [10] – HSPA
    [11] – iDen
    [12] – EVDO_B
    [13] – LTE
    [14] – EHRPD
    [15] – HSPAP
    [16] – GSM
    [17] – SCDMA
    [18] – IWLAN
    [19] – SEARCHING
    [20] – NR5G
     */
    val getDataNetworkType: Int
)