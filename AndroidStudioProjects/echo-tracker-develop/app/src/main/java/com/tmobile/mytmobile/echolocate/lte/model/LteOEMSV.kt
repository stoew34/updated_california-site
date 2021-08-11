package com.tmobile.mytmobile.echolocate.lte.model

import com.google.gson.annotations.SerializedName

/**
 * Data class that declare all the variables of LteOEMSV model
 */
data class LteOEMSV(

    /**
     * two digit device software version
     */
    @SerializedName("SV")
    var softwareVersion: String?,

    /**
     * Android SDK Version E.g., "1.0" or "3.4b5".
     */
    val customVersion: String?,

    /**
     * Build name is either a changelist number, or a label like "M4-rc20".
     */
    val radioVersion: String?,

    /**
     * Custom version is the version is the OEM specific custom version if available
     */
    val buildName: String?,

    /**
     *Radio version is the the version string for the radio firmware
     */
    val androidVersion: String?
){
    constructor() : this("",
        "",
        "",
        "",
        ""
    )
}