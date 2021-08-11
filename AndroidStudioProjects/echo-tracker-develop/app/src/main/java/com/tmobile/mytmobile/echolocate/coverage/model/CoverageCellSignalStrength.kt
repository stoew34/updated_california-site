package com.tmobile.mytmobile.echolocate.coverage.model


data class CoverageCellSignalStrength(

    val asu: String,
    val dBm: String,
    val bandwidth: String?,
    val rsrp: String,
    val rsrq: String,
    val rssnr: String?,
    val cqi: String?,
    val timingAdvance: String?
) {
    constructor() : this("", "",
        null, "", "",
        null,null, null
    )
}