package com.tmobile.mytmobile.echolocate.coverage.model

data class CoverageNrCell(

    var nrCsiRsrp: String?,
    var nrCsiRsrq: String?,
    var nrCsiSinr: String?,
    var nrSsRsrp: String?,
    var nrSsRsrq: String?,
    var nrSsSinr: String?,
    var nrStatus: String?,
    var nrDbm: String?,
    var nrLevel: String?,
    var nrAsuLevel: String?,
    var nrArfcn: String?,
    var nrCi: String?,
    var nrPci: String?,
    var nrTac: String?
) {
    constructor() : this(null, null,
        null, null, null,
        null, null, null,
        null, null, null, null, null, null
    )
}