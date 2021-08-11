package com.tmobile.mytmobile.echolocate.lte.model

import com.tmobile.mytmobile.echolocate.utils.EchoLocateDateUtils

/**
 * Contains information about the various network and calling settings of the device.
 */
data class LteSettings(

    /**
     * Provides the ability to make and receive phone calls over a Wi-Fi connection
     * 1 if off
     * 2 if WiFi preferred
     * 3 if cellular preferred
     * 4 if never use cellular
     * -1 if this setting does not exist
     * -2 if failed to check this info
     */
    val wifiCallingSetting: Int?,

    /**
     * Wifi setting represents the state of the Wifi on the device under test.
     * 1 if WiFi setting is off
     * 2 if on
     * -1 if this setting does not exist
     * -2 if failed to check this info
     */
    val wifiSetting: Int?,

    /**
     * Integer value which determines the state of the data connection on the device under test.
     * 1 if the mobile data setting is off.
     * 2 if on.
     * -1 if this setting does not exist
     * -2 if failed to check this info
     */
    val mobileDataSettings: Int?,

    /**
     * Integer value which determines the value of the network mode on the device under test.
     * 1 if LTE/WCDMA/GSM auto
     * 2 if WCDMA/GSM auto
     * 3 if WCDMA only
     * 4 if GSM only
     * 5 if LTE/WCDMA auto
     * 6 if LTE only
     * 7 if LTE/GSM auto
     * -1 if this setting does not exist
     * -2 if failed to check this info
     */
    val networkModeSettings: Int?,

    /**
     * convert the timestamp as received in the intent from the OEMs to ISO timestamp with format
     * yyyy-MM-dd'T'HH:mm:ss.SSSZ.
     * For example: 2018-05-16T16:14:10.456-0700.
     * Unix epoch time in milliseconds
     */
    val oemTimestamp: String?,

    /**
     * Data Roaming setting value
     * 1 if data roaming is off
     * 2 if on
     * -1 if this setting does not exist
     * -2 if failed to check this info
     */
    val roamingSetting: String?,

    /**
     * 1 if off
     * 2 if on with ‘Visible During Calls’ option
     * 3 if on with ‘Always Visible – Manual’ option
     * 4 if on with ‘Always Visible – Automatic’ option
     * -1 if this setting does not exist
     * -2 if failed to check this info
     */
    val rtt: String?,

    /**
     * 1 if off
     * 2 if on
     * -1 if this setting does not exist
     * -2 if failed to check this info
     */
    val rttTranscript: String?,

    /**
     *
     * VoLTE is a HD voice calling Service over 4G LTE rather than 2G/3G network
     * 1 if off or VoLTE not preferred
     * 2 if on or VoLTE preferred
     * 3 if no such setting exists
     * -1 if this setting does not exist
     * -2 if failed to check this info
     */
    val volteSetting: Int?
) {
    constructor() : this(0, 0, 0, 0,
        EchoLocateDateUtils.getTriggerTimeStamp(), "", "", "", 0)
}