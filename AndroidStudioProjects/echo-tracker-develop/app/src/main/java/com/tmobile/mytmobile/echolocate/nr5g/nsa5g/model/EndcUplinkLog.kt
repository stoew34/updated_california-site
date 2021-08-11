package com.tmobile.mytmobile.echolocate.nr5g.nsa5g.model

import com.google.gson.annotations.SerializedName

/**
 * Model class that holds call EndcUplinkLog data
 *
 * Serialized name annotation for all variables to be in JSON,
 * and it will be helpful while doing progaurding
 */
data class EndcUplinkLog(

    /**
     * Returns timestamp in UNIX epoch time as a UTC string, "yyyy-MM-dd'T'HH:mm:ss.SSSZ" e.g.
     * 2019-06-24T18:57:23.567+0000
     */
    @SerializedName("timestamp")
    val timestamp: String,

    /**
     *  Returns the enumerated type of network as an integer which may have the following values:
     *  [-2] – NOT_AVAILABLE
     *  [0] – SEARCHING
     *  [1] – LTE orLTE with EN-DC
     *  [2] – UMTS
     *  [3] – EDGE
     *  [4] – GPRS

     *  Notes: Returns -2 if no data is available
     */
    @SerializedName("networkType")
    val networkType: Int,

    /**
     *  Returns an integer which corresponds to the type of network where uplink data is delivered (PUSCH
     *  channel is established)

     *  [-999] – if connected network is not LTE
     *  [-2] – value not available even in LTE
     *  [1] – PUSCH is on 5G band
     *  [2] – PUSCH is on LTE band
     *  [3] – PUSCH is on both 5G and LTE band simultaneously

     *  Notes: Returns -999 if ‘networkType’ is not 1 (LTE or LTE EN-DC)
     *  Returns -2 if the value is not available even if ‘networkType’ is 1
     */
    @SerializedName("uplinkNetwork")
    val uplinkNetwork: Int
)