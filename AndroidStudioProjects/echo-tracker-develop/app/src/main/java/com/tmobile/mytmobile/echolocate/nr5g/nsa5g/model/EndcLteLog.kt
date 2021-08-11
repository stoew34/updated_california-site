package com.tmobile.mytmobile.echolocate.nr5g.nsa5g.model

import com.google.gson.annotations.SerializedName

/**
 * Model class that holds call EndcLteLog data
 *
 * Serialized name annotation for all variables to be in JSON,
 * and it will be helpful while doing progaurding
 */
data class EndcLteLog(

    /**
     * Returns timestamp in UNIX epoch time as a UTC string, "yyyy-MM-dd'T'HH:mm:ss.SSSZ" e.g.
     * 2019-06-24T18:57:23.567+0000
     * Notes:  This timestamp can be rendered as an integer string which will correspond to the above UTC
     * dates.
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
     * Returns the cell ID (CID) of the LTE cell that is the anchor for the 5G EN-DC connection.

     *  [-999] if connected network is not LTE
     *  [-2] value not available even in LTE

     *  Notes: Returns -999 if ‘networkType’ is not 1 (LTE or LTE EN-DC)
     *  Returns -2 if the value is not available even if ‘networkType’ is 1
     */
    @SerializedName("anchorLteCid")
    val anchorLteCid: Long,

    /**
     *  Returns the Physical Cell ID (PCI) of the LTE cell that is the anchor for the 5G EN-DC connection.

     *  [-999] if connected network is not LTE
     *  [-2] value not available even in LTE

     *  Notes: Returns -999 if ‘networkType’ is not 1 (LTE or LTE EN-DC)
     *  Returns -2 if the value is not available even if ‘networkType’ is 1
     */
    @SerializedName("anchorLtePci")
    val anchorLtePci: Int,

    /**
     *  Returns a value which indicates if the upperLayerIndication-r15 in SystemInformatinoBlockType2 was *
     *  received from the LTE serving cell, indicating that the LTE serving cell is EN-DC capable.

     *  [-999] if connected network is not LTE
     *  [-2] value not available even in LTE
     *  [1] (ON), the UE received the upperLayerIndication-r15 indicating that the LTE serving cell is EN-DC
     *  capable
     *  [2] (OFF), the UE didn’t receive the upperLayerIndication-r15 from the serving cell

     *  Notes: Returns -999 if ‘networkType’ is not 1 (LTE or LTE EN-DC)
     *  Returns -2 if the value is not available even if ‘networkType’ is 1
     */
    @SerializedName("endcCapability")
    val endcCapability: Int,

    /**
     * Returns an integer which indicates the LTE RRC state.

     *  [-999] if connected network is not LTE
     *  [-2] value not available even in LTE
     *  [0] if RRC_IDLE
     *  [1] if RRC_CONNECTED

     *  Notes: Returns -999 if ‘networkType’ is not 1 (LTE or LTE EN-DC)
     *  Returns -2 if the value is not available even if ‘networkType’ is 1
     */
    @SerializedName("lteRrcState")
    val lteRrcState: Int
)