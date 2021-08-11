package com.tmobile.mytmobile.echolocate.nr5g.sa5g.model

import com.google.gson.annotations.SerializedName

/**
 * Contains information about the various network and calling settings of the device.
 */
data class Sa5gRrcLog(

    /**
     * Returns [CONNECTED|IDLE]
     * UE in API version 1 shall return the LTE RRC (Radio Resource Control) state as follows:
     * <p>
     * CONNECTED: LTE RRC CONNECTED state
     * IDLE: LTE RRC IDLE state
     */
    @SerializedName("lteRrcState")
    val lteRrcState: String?,

    /**
     * Returns [CONNECTED|IDLE|INACTIVE]
     * UE in API version 1 shall return the NR RRC state as follows:
     * <p>
     * CONNECTED: NR RRC CONNECTED state
     * INACTIVE: NR RRC INACTIVE state
     * IDLE: NR RRC IDLE state
     */
    @SerializedName("nrRrcState")
    val nrRrcState: String?
)