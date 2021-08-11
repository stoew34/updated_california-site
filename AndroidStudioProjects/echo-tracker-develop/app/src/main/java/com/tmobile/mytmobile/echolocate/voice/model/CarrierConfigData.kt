package com.tmobile.mytmobile.echolocate.voice.model

data class CarrierConfigData(

    /**
     * Carrier configuration for VoNR on the device in String Extras
     */
    val carrierVoiceConfig: String,

    /**
     * Carrier configuration for disabling 5G SA connection during active VoWiFi
     * calling on the device in String Extras.
     */
    val carrierVoWiFiConfig: String,

    /**
     * Carrier configuration for the UE to access 5G SA bands in Map Extras
     */
    val bandConfig: List<BandConfig>,

    /**
     * Carrier configuration file version in String Extras
     */
    val carrierConfigVersion: String,

    /**
     * Timestamp when the event is received by the application.
     * Returns ISO timestamp with format yyyy-MM-dd'T'HH:mm:ss.SSSZ. For example: 2018-05-16T16:14:10.456-0700
     */
    val eventTimestamp: String
)