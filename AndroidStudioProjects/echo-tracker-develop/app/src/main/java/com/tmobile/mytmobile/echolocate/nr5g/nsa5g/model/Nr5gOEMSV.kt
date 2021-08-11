package com.tmobile.mytmobile.echolocate.nr5g.nsa5g.model

import com.google.gson.annotations.SerializedName

/**
 * Model class that holds call Nr5gOEMSV data
 *
 * Serialized name annotation for all variables to be in JSON,
 * and it will be helpful while doing progaurding
 */
data class Nr5gOEMSV(

    /**
     * two digit device software version
     */
    @SerializedName("SV")
    var softwareVersion: String,

    /**
     * Android SDK Version E.g., "1.0" or "3.4b5".
     */
    @SerializedName("customVersion")
    val customVersion: String,

    /**
     * Build name is either a changelist number, or a label like "M4-rc20".
     */
    @SerializedName("radioVersion")
    val radioVersion: String,

    /**
     * Custom version is the version is the OEM specific custom version if available
     */
    @SerializedName("buildName")
    val buildName: String,

    /**
     *Radio version is the the version string for the radio firmware
     */
    @SerializedName("androidVersion")
    val androidVersion: String
)