package com.tmobile.mytmobile.echolocate.nr5g.sa5g.database

/**
 * Class that defines all the String constants used for SA 5G database
 */
object Sa5gDatabaseConstants {

    /**
     * ECHO_LOCATE_SA5G_DB_NAME
     *
     * use with getEchoLocateSa5gDatabase to create the database
     * value echolocate_sa5g_db
     */
    const val ECHO_LOCATE_SA5G_DB_NAME = "echolocate_sa5g_db"

    /**
     * SA5G_ECHO_LOCATE_BASE_TABLE_NAME
     *
     * use with entity in BaseEchoLocateSa5gEntity data class to create base echolocate Sa 5g table name
     * value sa5g_base_data
     */
    const val SA5G_ECHO_LOCATE_BASE_TABLE_NAME = "sa5g_base_data"

    /**
     * SA5G_DEVICE_INFO_TABLE_NAME
     *
     * use with entity in Sa5gDeviceInfoEntity data class to create Sa 5G device info table name
     * value sa5g_device_info
     */
    const val SA5G_DEVICE_INFO_TABLE_NAME = "sa5g_device_info"

    /**
     * SA5G_OEMSV_TABLE_NAME
     *
     * use with entity in Sa5gOEMSVEntity data class to create Sa5gOEMSVEntity table name
     * value sa5g_oemsv
     */
    const val SA5G_OEMSV_TABLE_NAME = "sa5g_oemsv"

    /**
     * SA5G_LOCATION_TABLE_NAME
     *
     * use with entity in Sa5gLocationEntity data class to create Sa5gLocationEntity table name
     * value sa5g_location
     */
    const val SA5G_LOCATION_TABLE_NAME = "sa5g_location"

    /**
     * SA5G_GET_ACTIVE_NETWORK_TABLE_NAME
     *
     * use with entity in Sa5gActiveNetworkEntity data class to create Sa5gActiveNetworkEntity table name
     * value sa5g_get_active_network
     */
    const val SA5G_GET_ACTIVE_NETWORK_TABLE_NAME = "sa5g_get_active_network"

    /**
     * SA5G_CONNECTED_WIFI_STATUS_TABLE_NAME
     *
     * use with entity in Sa5gConnectedWifiStatusEntity data class to create ConnectedWifiStatus table
     * value sa5g_connected_wifi_status
     */
    const val SA5G_CONNECTED_WIFI_STATUS_TABLE_NAME = "sa5g_connected_wifi_status"

    /**
     * SA5G_DOWNLINK_CARRIER_LOGS_TABLE_NAME
     *
     * use with entity in Sa5gDownlinkCarrierLogsEntity data class to create Sa5gDownlinkCarrierLogsEntity table name
     * value sa5g_downlink_carrier_logs
     */
    const val SA5G_DOWNLINK_CARRIER_LOGS_TABLE_NAME = "sa5g_downlink_carrier_logs"

    /**
     * SA5G_NETWORK_LOG_TABLE_NAME
     *
     * use with entity in Sa5gNetworkLogEntity data class to create Sa5gNetworkLogEntity table name
     * value sa5g_network_log
     */
    const val SA5G_NETWORK_LOG_TABLE_NAME = "sa5g_network_log"

    /**
     * SA5G_RRC_LOG_TABLE_NAME
     *
     * use with entity in Sa5gRrcLogEntity data class to create Sa5gRrcLogEntity table name
     * value sa5g_rrc_log
     */
    const val SA5G_RRC_LOG_TABLE_NAME = "sa5g_rrc_log"

    /**
     * SA5G_SETTINGS_LOG_TABLE_NAME
     *
     * use with entity in Sa5gSettingsLogEntity data class to create Sa5gSettingsLogEntity table name
     * value sa5g_settings_log
     */
    const val SA5G_SETTINGS_LOG_TABLE_NAME = "sa5g_settings_log"

    /**
     * SA5G_TRIGGER_TABLE_NAME
     *
     * use with entity in Sa5gTriggerEntity data class to create Sa5gTriggerEntity table name
     * value sa5g_trigger
     */
    const val SA5G_TRIGGER_TABLE_NAME = "sa5g_trigger"

    /**
     * SA5G_UI_LOG_TABLE_NAME
     *
     * use with entity in Sa5gUiLogEntity data class to create Sa5gUiLogEntity table name
     * value sa5g_ui_log
     */
    const val SA5G_UI_LOG_TABLE_NAME = "sa5g_ui_log"


    /**
     * SA5G_UPLINK_CARRIER_LOGS_TABLE_NAME
     *
     * use with entity in Sa5gUplinkCarrierLogsEntity data class to create Sa5gUplinkCarrierLogsEntity table name
     * sa5g_uplink_carrier_logs
     */
    const val SA5G_UPLINK_CARRIER_LOGS_TABLE_NAME = "sa5g_uplink_carrier_logs"

    /**
     * SA5G_WIFI_STATE_TABLE_NAME
     *
     * use with entity in Sa5gWiFiStateEntity data class to create Sa5gWiFiStateEntity table name
     * value sa5g_wifi_state
     */
    const val SA5G_WIFI_STATE_TABLE_NAME = "sa5g_wifi_state"

    /**
     * SA5G_REPORT_TABLE_NAME
     *
     * use with entity Sa5gSingleSessionReportEntity data class to combine all data to reports
     */
    const val SA5G_REPORT_TABLE_NAME = "sa5g_report"

    /**
     * SA5G_WIFI_STATE_TABLE_NAME
     *
     * use with entity in Sa5gWiFiStateEntity data class to create Sa5gWiFiStateEntity table name
     * value sa5g_wifi_state
     */
    const val SA5G_CARRIER_CONFIG_TABLE_NAME = "sa5g_carrier_config"

}