package com.tmobile.mytmobile.echolocate.coverage.model


data class CoveragePrimaryCell(

    val cellType: String,
    val cellSignalStrength: CoverageCellSignalStrength,
    val cellIdentity: CoverageCellIdentity
) {
    constructor() : this(
        "",
        CoverageCellSignalStrength(),
        CoverageCellIdentity()
    )
}