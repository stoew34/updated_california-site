package com.tmobile.mytmobile.echolocate.nr5g.sa5g.model

import com.google.gson.annotations.SerializedName

/**
 * Contains information about the various network and calling settings of the device.
 */
data class Sa5gSettingsLog(

    /**
     * Provides the ability to make and receive phone calls over a Wi-Fi connection
     * 1 if off
     * 2 if WiFi preferred
     * 3 if cellular preferred
     * 4 if never use cellular
     * -1 if this setting does not exist
     * -2 if failed to check this info
     */
    @SerializedName("wifiCalling")
    val wifiCalling: String?,

    /**
     * Wifi setting represents the state of the Wifi on the device under test.
     * 1 if WiFi setting is off
     * 2 if on
     * -1 if this setting does not exist
     * -2 if failed to check this info
     */
    @SerializedName("wifi")
    val wifi: String?,

    /**
     * Data Roaming setting value
     * 1 if data roaming is off
     * 2 if on
     * -1 if this setting does not exist
     * -2 if failed to check this info
     * 2.2.12.7 str[6] return - VoLTE setting
     */
    @SerializedName("roaming")
    val roaming: String?,

    /**
     * 1 if off
     * 2 if on with ‘Visible During Calls’ option
     * 3 if on with ‘Always Visible – Manual’ option
     * 4 if on with ‘Always Visible – Automatic’ option
     * -1 if this setting does not exist
     * -2 if failed to check this info
     */
    @SerializedName("rtt")
    val rtt: String?,

    /**
     * 1 if off
     * 2 if on
     * -1 if this setting does not exist
     * -2 if failed to check this info
     */
    @SerializedName("rttTranscript")
    val rttTranscript: String?,

    /**
     * Integer value which determines the value of the network mode on the device under test.
     * 1 if LTE/WCDMA/GSM auto
     * 2 if WCDMA/GSM auto
     * 3 if WCDMA only
     * 4 if GSM only
     * 5 if LTE/WCDMA auto
     * 6 if LTE only
     * 7 if LTE/GSM auto
     * 8 if NR/LTE/WCDMA/GSM auto
     * -1 if this setting does not exist
     * -2 if failed to check for this field
     */
    @SerializedName("networkMode")
    val networkMode: String?,

    /**
     * Returns Carrier Config Version
     */
    @SerializedName("carrierConfigVersion")
    val carrierConfigVersion: String?,

    /**
     * Returns Band Config Keys
     * Example: "SAn2Enabled", "SAn66Enabled", "NONE", "ERROR"
     */
    @SerializedName("carrierSa5gBandConfig")
    val carrierSa5gBandConfig : Map<String, String>?
)