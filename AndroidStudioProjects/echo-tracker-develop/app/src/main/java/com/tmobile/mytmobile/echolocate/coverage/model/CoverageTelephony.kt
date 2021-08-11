package com.tmobile.mytmobile.echolocate.coverage.model


data class CoverageTelephony(

    val simState: String?,
    val roamingNetwork: String?,
    val roamingVoice: String?,
    val networkType: String,
    val serviceState: String?,
    val primaryCell: CoveragePrimaryCell,
    val nrCell: CoverageNrCell?
) {
    constructor() : this(null, null,
        null, "", null,
        CoveragePrimaryCell(), null
    )
}