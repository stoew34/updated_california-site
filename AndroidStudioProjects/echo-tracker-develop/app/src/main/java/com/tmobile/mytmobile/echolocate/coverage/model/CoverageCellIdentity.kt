package com.tmobile.mytmobile.echolocate.coverage.model

data class CoverageCellIdentity(

    val cellId: String,
    val cellInfoDelay: String,
    val networkName: String?,
    val mcc: String,
    val mnc: String,
    val earfcn: String?,
    val tac: String?,
    val lac: String?
) {
    constructor() : this("", "",
    null, "", "",
        null,null, null
    )
}