package com.tmobile.mytmobile.echolocate.nr5g.nsa5g.utils

object Nsa5gConstants {
    /**
     * NR5G_CONSTANTS_COMPONENT_NAME
     *
     * use with schedule a periodic job
     * value nr5g_component
     */
    const val NR5G_CONSTANTS_COMPONENT_NAME = "nr5g_component"

    /**
     * SCHEMA_VERSION
     * The version of the schema for which the data is being reported.
     * value 1.0
     */
    const val SCHEMA_VERSION = "1.0"

    /**
     * API_VERSION
     * This is the OEM APIs version supported on this device.
     * value 1
     */
    const val API_VERSION = 1

    const val LOCATION_EXPECTED_SIZE = 0 // 0 - not from api

    const val OEMSV_EXPECTED_SIZE = 0

    const val NR_STATUS_EXPECTED_SIZE = 0

    /**
     * WIFI_EXPECTED_SIZE
     * O if not from the API
     * value 0
     */
    const val WIFI_EXPECTED_SIZE = 0

    /**
     * NETWORK_TYPE_EXPECTED_SIZE
     * O if not from the API
     * value 0
     */
    const val NETWORK_TYPE_EXPECTED_SIZE = 0

    /**
     * WIFI_STATE_DISABLING_INTEGER
     * O if wifi sate is disabling
     * value 0
     */
    const val WIFI_STATE_DISABLING_INTEGER = 0

    /**
     * WIFI_STATE_DISABLED_INTEGER
     * 1 if wifi sate is disabled
     * value 1
     */
    const val WIFI_STATE_DISABLED_INTEGER = 1

    /**
     * WIFI_STATE_ENABLING_INTEGER
     * 2 if wifi sate is enabling
     * value 2
     */
    const val WIFI_STATE_ENABLING_INTEGER = 2

    /**
     * WIFI_STATE_ENABLED_INTEGER
     * 3 if wifi sate is enabled
     * value 3
     */
    const val WIFI_STATE_ENABLED_INTEGER = 3

    /**
     * WIFI_STATE_UNKNOWN_INTEGER
     * 4 if wifi sate is unknown
     * value 4
     */
    const val WIFI_STATE_UNKNOWN_INTEGER = 4

    const val DEVICE_INFO_EXPECTED_SIZE = 0

    const val WIFI_DATA_EXPECTED_SIZE = 0

    const val END_C_LTE_LOG_SIZE = 6

    const val NR5G_UI_LOG_SIZE = 6

    const val NR5G_MMW_CELL_LOG_SIZE = 6

    const val END_C_UPLINK_LOG_SIZE = 3

}