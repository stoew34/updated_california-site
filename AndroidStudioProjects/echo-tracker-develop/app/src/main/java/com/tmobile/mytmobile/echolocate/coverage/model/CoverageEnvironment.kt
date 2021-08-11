package com.tmobile.mytmobile.echolocate.coverage.model

import com.google.gson.annotations.SerializedName

data class CoverageEnvironment(

    @SerializedName("settings")
    val settings: CoverageSettings?,

    @SerializedName("OEMSV")
    val oemsv: CoverageOEMSV,

    @SerializedName("location")
    val location: CoverageLocation?,

    @SerializedName("net")
    val net: CoverageNet?,

    @SerializedName("telephony")
    val telephony: CoverageTelephony
) {
    constructor() : this(
        null,
        CoverageOEMSV(),
        null,
        null,
        CoverageTelephony()
    )
}