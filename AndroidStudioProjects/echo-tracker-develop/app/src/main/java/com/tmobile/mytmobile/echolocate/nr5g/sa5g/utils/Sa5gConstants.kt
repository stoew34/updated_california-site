package com.tmobile.mytmobile.echolocate.nr5g.sa5g.utils

object Sa5gConstants {

    /**
     * EMPTY
     *
     * empty value
     * value ""
     */
    const val EMPTY = ""

    /**
     * SCHEMA_VERSION
     * The version of the schema for which the data is being reported.
     * value 1.0
     */
    const val SCHEMA_VERSION = "1.0"

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

    /**
     * API_VERSION
     * This is the OEM APIs version supported on this device.
     * value 1
     */
    const val API_VERSION = 1
    const val UNKNOWN_SOURCE_SIZE = -1


    const val SA5G_LOCATION_EXPECTED_SIZE = 0 // 0 - not from api
    const val SA5G_OEMSV_EXPECTED_SIZE = 0
    const val SA5G_DEVICE_INFO_EXPECTED_SIZE = 0
    const val SA5G_TRIGGER_EXPECTED_SIZE = 0
    const val SA5G_CONNECTED_WIFI_STATUS_EXPECTED_SIZE = 0
    const val SA5G_ACTIVE_NETWORK_EXPECTED_SIZE = 0
    const val SA5G_WIFI_STATE_EXPECTED_SIZE = 0

    const val SA5G_DL_CARRIER_LOGS_SIZE = 1
    const val SA5G_UL_CARRIER_LOGS_SIZE = 1
    const val SA5G_RRC_LOG_SIZE = 2
    const val SA5G_NETWORK_LOG_SIZE = 4
    const val SA5G_SETTINGS_LOG_SIZE = 6
    const val SA5G_UI_LOG_SIZE = 5
    const val SA5G_CARRIER_CONFIG = 11

    const val SA5G_TRIGGER_NULL = "trigger null"
    const val SA5G_BASE_ENTITY_NULL = "base entity null"
    const val SA5G_INVALID_METRICS_DATA = "invalid metrics data"

}