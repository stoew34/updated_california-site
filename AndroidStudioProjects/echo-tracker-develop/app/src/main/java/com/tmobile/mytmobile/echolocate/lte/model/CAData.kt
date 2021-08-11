package com.tmobile.mytmobile.echolocate.lte.model

import com.google.gson.annotations.SerializedName

/**
 *Carrier aggregation (CA) is used in LTE-Advanced in order to increase the bandwidth, and thereby increase the bitrate
 */
data class CAData(

    /**
     * Positive integer representing EUTRA absolute radio-frequency channel number.
     */
    @SerializedName("EARFCN")
    val earfcn: Int?,

    /**
     *Integer value representing band number of the CA.
     * E.g. 4 if Band 4
     * <p>
     * Equals to ‘-999’ if it’s not LTE
     */
    val bandNumber: Int?,

    /**
     * Positive integer representing bandwidth in MHZ.
     */
    val bandWidth: Int?,

    /**
     * The position of the item in the array starting from 1.
     */
    val carrierNum: Int?,

    /**
     * Integer value representing number of layers used.
     */
    val layers: Int?,

    /**
     *  String representing the modulation scheme in use.
     */
    val modulation: String?,

    /**
     * String value representing the physical cell identification.
     */
    @SerializedName("PCI")
    val pci: String?,

    /**
     * String value representing the id of the cell.
     */
    val cellId: String?,

    /**
     *  String value representing the id of the location.
     */
    val locationId: String?
)
