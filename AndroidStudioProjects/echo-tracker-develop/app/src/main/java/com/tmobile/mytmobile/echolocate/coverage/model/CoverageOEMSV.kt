package com.tmobile.mytmobile.echolocate.coverage.model


data class CoverageOEMSV(

    /**
     * android os version
     */
    val androidVersion: String,

    /**
     * Custom version is the version is the OEM specific custom version if available
     */
    val buildName: String,

    /**
     * Android SDK Version E.g., "1.0" or "3.4b5".
     */
    val customVersion: String?,

    /**
     * Build name is either a changelist number, or a label like "M4-rc20".
     */
    val radioVersion: String,

    /**
     * two digit device software version
     */
    val sv: String
) {
    constructor() : this (
        "", "", null, "", ""
    )
}