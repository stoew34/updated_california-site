package com.tmobile.mytmobile.echolocate.coverage.model


data class CoverageSettings(

    /**
     *
     * Voice over LTE status.
     * ENABLED(channelCode = 0),
     * DISABLED(channelCode = 1),
     * UNSUPPORTED(channelCode = -1);
     */
    val volteState: String?,

    /**
     * Data Roaming setting value
     * Settings.Global.DATA_ROAMING. Example: true
     */
    val dataRoamingEnabled: String?
) {
    constructor() : this (
        null, null
    )
}