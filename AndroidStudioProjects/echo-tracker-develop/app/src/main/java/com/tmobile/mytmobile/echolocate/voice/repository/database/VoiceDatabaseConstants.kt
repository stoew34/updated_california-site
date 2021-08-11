package com.tmobile.mytmobile.echolocate.voice.repository.database

/**
 * Class that defines all the String constants used for voice database
 */
object VoiceDatabaseConstants {

    /**
     * ECHO_LOCATE_VOICE_DB_NAME
     *
     * use with getEchoLocateVoiceDatabase to create the database
     * value echolocate_voice_db
     */
    const val ECHO_LOCATE_VOICE_DB_NAME = "echolocate_voice_db"


    /**
     * VOICE_LOCATION_TABLE_NAME
     *
     * use with entity in voiceLocationEntity data class to create location table
     * value voice_location
     */
    const val VOICE_LOCATION_TABLE_NAME = "voice_location"

    /**
     * VOICE_RADIO_HANDOVER_DATA_TABLE_NAME
     *
     * use with entity in RadioHandoverEntity data class to create Voice Radio Handover table
     * value voice_radio_handover_data
     */
    const val VOICE_RADIO_HANDOVER_DATA_TABLE_NAME = "voice_radio_handover_data"

    /**
     * VOICE_CELL_INFO_DATA_TABLE_NAME
     *
     * use with entity in VoiceCellInfoEntity data class to create cell info table
     * value voice_cell_info
     */
    const val VOICE_CELL_INFO_DATA_TABLE_NAME = "voice_cell_info"

    /**
     * VOICE_OEM_SOFTWARE_VERSION_TABLE_NAME
     *
     * use with entity in OEMSoftwareVersionEntity data class to create oem software version table
     * value voice_oem_software_version
     */
    const val VOICE_OEM_SOFTWARE_VERSION_TABLE_NAME = "voice_oem_software_version"

    /**
     * VOICE_IMS_SIGNALLING_DATA_TABLE_NAME
     *
     * use with entity in ImsSignallingEntity data class to create Ims Signalling table
     * value voice_ims_signalling_data
     */
    const val VOICE_IMS_SIGNALLING_DATA_TABLE_NAME = "voice_ims_signalling_data"

    /**
     * VOICE_APP_TRIGGERED_CALL_DATA_TABLE_NAME
     *
     * use with entity in AppTriggeredCallDataEntity data class to create app triggered call table
     * value voice_app_triggered_call_data
     */
    const val VOICE_APP_TRIGGERED_CALL_DATA_TABLE_NAME = "voice_app_triggered_call_data"

    /**
     * VOICE_DEVICE_INFO_TABLE_NAME
     *
     * use with entity in DeviceInfoEntity data class to create device info table
     * value voice_device_info
     */
    const val VOICE_DEVICE_INFO_TABLE_NAME = "voice_device_info"

    /**
     * VOICE_CALL_SETTING_DATA_TABLE_NAME
     *
     * use with entity in CallSettingDataEntity data class to create call settings table
     * value voice_call_setting_data
     */
    const val VOICE_CALL_SETTING_DATA_TABLE_NAME = "voice_call_setting_data"

    /**
     * VOICE_DETAILED_CALL_STATE_TABLE_NAME
     *
     * use with entity in DetailedCallStateEntity data class to create detailed call state table
     * value voice_detailed_call_state_data
     */
    const val VOICE_DETAILED_CALL_STATE_TABLE_NAME = "voice_detailed_call_state_data"

    /**
     * VOICE_RTPDL_STATE_DATA_TABLE_NAME
     *
     * use with entity in RtpdlStateEntity data class to create rtpdl state table
     * value voice_rtpdl_state_data
     */
    const val VOICE_RTPDL_STATE_DATA_TABLE_NAME = "voice_rtpdl_state_data"

    /**
     * VOICE_UI_CALL_STATE_DATA_TABLE_NAME
     *
     * use with entity in UiCallStateEntity data class to create ui call state table
     * value voice_ui_call_state_data
     */
    const val VOICE_UI_CALL_STATE_DATA_TABLE_NAME = "voice_ui_call_state_data"

    /**
     * VOICE_ECHO_LOCATE_BASE_TABLE_NAME
     *
     * use with entity in BaseEchoLocateVoiceEntity data class to the base table
     * value voice_echo_locate_base_data
     */
    const val VOICE_ECHO_LOCATE_BASE_TABLE_NAME = "voice_echo_locate_base_data"

    /**
     * VOICE_NETWORK_IDENTITY_TABLE_NAME
     *
     * use with entity in BaseEchoLocateVoiceEntity data class to the base table
     * value voice_echo_locate_base_data
     */
    const val VOICE_NETWORK_IDENTITY_TABLE_NAME = "voice_network_identity"

    /**
     * VOICE_REPORT_TABLE_NAME
     *
     * use with entity VoiceReportEntity data class to combine all data to reports
     */
    const val VOICE_REPORT_TABLE_NAME = "voice_report"

    /**
     * VOICE_EMERGENCY_CALL_TIMER_STATE
     *
     * use with entity in EmergencyCallTimerStateEntity data class to create emergency call table
     * value voice_emergency_call_timer_state
     */
    const val VOICE_EMERGENCY_CALL_TIMER_STATE = "voice_emergency_call_timer_state"

    /**
    * VOICE_CARRIER_CONFIG
    *
    * use with entity in CarrierConfigDataEntity data class to create carrier config table
    * value voice_carrier_config
    */
    const val VOICE_CARRIER_CONFIG = "voice_carrier_config"
}