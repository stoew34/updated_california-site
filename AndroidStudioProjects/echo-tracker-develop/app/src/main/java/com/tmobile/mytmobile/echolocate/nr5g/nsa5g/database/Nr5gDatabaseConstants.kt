package com.tmobile.mytmobile.echolocate.nr5g.nsa5g.database

import com.tmobile.mytmobile.echolocate.nr5g.nsa5g.database.entity.*

/**
 * Class that defines all the String constants used for 5G database
 */
object Nr5gDatabaseConstants {

    /**
     * ECHO_LOCATE_NR5G_DB_NAME
     *
     * use with getEchoLocateNr5gDatabase to create the database
     * value echolocate_nr5g_db
     */
    const val ECHO_LOCATE_NR5G_DB_NAME = "echolocate_nr5g_db"

    /**
     * NR5G_ECHO_LOCATE_BASE_TABLE_NAME
     *
     * use with entity in [BaseEchoLocateNr5gEntity] data class to create base echolocate 5g table name
     * value nr5g_base_data
     */
    const val NR5G_ECHO_LOCATE_BASE_TABLE_NAME = "nr5g_base_data"

    /**
     * NR5G_CONNECTED_WIFI_STATUS_TABLE_NAME
     *
     * use with entity in [ConnectedWifiStatusEntity] data class to create ConnectedWifiStatus table
     * value nr5g_connected_wifi_status
     */
    const val NR5G_CONNECTED_WIFI_STATUS_TABLE_NAME = "nr5g_connected_wifi_status"

    /**
     * NR5G_DEVICE_INFO_TABLE_NAME
     *
     * use with entity in [Nr5gDeviceInfoEntity] data class to create 5G device info table name
     * value nr5g_device_info
     */
    const val NR5G_DEVICE_INFO_TABLE_NAME = "nr5g_device_info"

    /**
     * NR5G_END_C_LTE_LOG_TABLE_NAME
     *
     * use with entity in [EndcLteLogEntity] data class to create EndcLteLogEntity table name
     * value nr5g_end_c_lte_log
     */
    const val NR5G_END_C_LTE_LOG_TABLE_NAME = "nr5g_end_c_lte_log"

    /**
     * NR5G_END_C_UPLINK_LOG_TABLE_NAME
     *
     * use with entity in [EndcUplinkLogEntity] data class to create EndcUpLinkLogEntity table name
     * value nr5g_end_c_uplink_log
     */
    const val NR5G_END_C_UPLINK_LOG_TABLE_NAME = "nr5g_end_c_uplink_log"

    /**
     * NR5G_UI_LOG_TABLE_NAME
     *
     * use with entity in [Nr5gUiLogEntity] data class to create Nr5gUiLogEntity table name
     * value nr5g_ui_log
     */
    const val NR5G_UI_LOG_TABLE_NAME = "nr5g_ui_log"

    /**
     * NR5G_GET_ACTIVE_NETWORK_TABLE_NAME
     *
     * use with entity in [Nr5gActiveNetworkEntity] data class to create Nr5gActiveNetworkEntity table name
     * value nr5g_get_active_network
     */
    const val NR5G_GET_ACTIVE_NETWORK_TABLE_NAME = "nr5g_get_active_network"

    /**
     * NR5G_GET_DATA_NETWORK_TYPE_TABLE_NAME
     *
     * use with entity in [Nr5gDataNetworkTypeEntity] data class to create Nr5gDataNetworkTypeEntity table name
     * value nr5g_get_data_network
     */
    const val NR5G_GET_DATA_NETWORK_TYPE_TABLE_NAME = "nr5g_get_data_network"

    /**
     * NR5G_GET_NETWORK_IDENTITY_TABLE_NAME
     *
     * use with entity in [Nr5gNetworkIdentityEntity] data class to create Nr5gNetworkIdentityEntity table name
     * value nr5g_get_network_identity
     */
    const val NR5G_GET_NETWORK_IDENTITY_TABLE_NAME = "nr5g_get_network_identity"

    /**
     * NR5G_GET_NR_STATUS_TABLE_NAME
     *
     * use with entity in [Nr5gStatusEntity] data class to create Nr5gStatusEntity table name
     * value nr5g_get_nr_status
     */
    const val NR5G_GET_NR_STATUS_TABLE_NAME = "nr5g_get_nr_status"

    /**
     * NR5G_GET_WIFI_STATE_TABLE_NAME
     *
     * use with entity in [Nr5gWifiStateEntity] data class to create Nr5gWifiStateEntity table name
     * value nr5g_get_wifi_state
     */
    const val NR5G_GET_WIFI_STATE_TABLE_NAME = "nr5g_get_wifi_state"

    /**
     * NR5G_LOCATION_TABLE_NAME
     *
     * use with entity in [Nr5gLocationEntity] data class to create Nr5gLocationEntity table name
     * value nr5g_location
     */
    const val NR5G_LOCATION_TABLE_NAME = "nr5g_location"

    /**
     * NR5G_MMW_CELL_LOG_TABLE_NAME
     *
     * use with entity in [Nr5gMmwCellLogEntity] data class to create Nr5gMmwCellLogEntity table name
     * value nr5g_mmw_cell_log
     */
    const val NR5G_MMW_CELL_LOG_TABLE_NAME = "nr5g_mmw_cell_log"

    /**
     * NR5G_OEMSV_TABLE_NAME
     *
     * use with entity in [Nr5gOEMSVEntity] data class to create Nr5gOEMSVEntity table name
     * value nr5g_oemsv
     */
    const val NR5G_OEMSV_TABLE_NAME = "nr5g_oemsv"

    /**
     * NR5G_TRIGGER_TABLE_NAME
     *
     * use with entity in [Nr5gTriggerEntity] data class to create Nr5gTriggerEntity table name
     * value nr5g_trigger
     */
    const val NR5G_TRIGGER_TABLE_NAME = "nr5g_trigger"

    /**
     * NR5G_REPORT_TABLE_NAME
     *
     * use with entity [Nr5gSingleSessionReportEntity] data class to combine all data to reports
     */
    const val NR5G_REPORT_TABLE_NAME = "nr5g_report"

}