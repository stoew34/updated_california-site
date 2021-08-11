package com.tmobile.mytmobile.echolocate.lte.utils

object LteConstants {

    /**
     * LTE_CONSTANTS_COMPONENT_NAME
     *
     * use with schedule a periodic job
     * value lte_component
     */
    const val LTE_CONSTANTS_COMPONENT_NAME = "lte_component"

    /**
     * LTE_LOCATION_TIME_OUT
     *
     * use if the location is not delivered with in 55 seconds
     * value 55000L
     */
    const val LTE_LOCATION_TIME_OUT = 55000L

    /**
     * LTE_HOURLY_BASIS_CODE
     *
     * use the hourly basis location trigger code (trigger_id)
     * value 100
     */
    const val LTE_HOURLY_BASIS_CODE = "100"

    /**
     * LTE_FOCUS_GAIN_CODE
     *
     * use the focus gain trigger code
     * value x01
     */
    const val LTE_FOCUS_GAIN_CODE = "x01"

    /**
     * LTE_FOCUS_LOSS_CODE
     *
     * use the focus loss trigger code
     * value x90
     */
    const val LTE_FOCUS_LOSS_CODE = "x90"

    /**
     * LTE_SCREEN_OFF_CODE
     *
     * use the screen off trigger code
     * value x95
     */
    const val LTE_SCREEN_OFF_CODE = "x95"

    /**
     * LTE_SCHEDULE_TIME_TEN_SECONDS
     *
     *10 seconds time
     * value 10000
     */
    const val LTE_SCHEDULE_TIME_TEN_SECONDS = 10000L

    /**
     * LTE_SCHEDULE_TIME_THIRTY_SECONDS
     *
     * 30 seconds time
     * value 30000
     */
    const val LTE_SCHEDULE_TIME_THIRTY_SECONDS = 30000L

    /**
     * LTE_SCHEDULE_TIME_SIXTY_SECONDS
     *
     * use the screen off trigger code
     * value 60000
     */
    const val LTE_SCHEDULE_TIME_SIXTY_SECONDS = 60000L

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
     * API_VERSION
     * This is the OEM APIs version supported on this device.
     * value 1
     */
    const val API_VERSION = 1

    const val PRIMARY_CA = 1

    const val SECONDARY_CA = 2

    const val TERTIARY_CA = 3

    const val LOCATION_EXPECTED_SIZE = 0 // 0 - not from api

    const val OEMSV_EXPECTED_SIZE = 0

    /**
     * DOWNLINK_CARRIER_INFO_EXPECTED_SIZE
     * The expected size of carrierInfo
     * value 12
     */
    const val DOWNLINK_CARRIER_INFO_EXPECTED_SIZE = 12

    /**
     * DOWNLINK_CARRIER_INFO_UNKNOWN_VERSION_SIZE
     * The expected size of unknown version carrierInfo
     * value 12
     */
    const val DOWNLINK_CARRIER_INFO_UNKNOWN_VERSION_SIZE = 12

    const val TRIGGER_COUNT_RESET_ACTION = "ResetTriggerCount"

    const val LTE_RESET_TRIGGER_CODE = 402

    const val RESET_TRIGGER_COUNT = 0

    const val RSRP_UNAVAILABLE_VALUE = -150
    const val RSRQ_UNAVAILABLE_VALUE = -50
    const val SINR_UNAVAILABLE_VALUE = -50
    const val RSSI_UNAVAILABLE_VALUE = -150
    const val RACH_POWER_UNAVAILABLE_VALUE = -150
    const val LTE_UL_HEADROOM_UNAVAILABLE_VALUE = -2
    const val QCI_UNAVAILABLE_VALUE = -2
    const val NUMBERS_OF_ACTIVE_BEARERS_UNAVAILABLE_VALUE = -2

    /* The default value will set
     * whenever we get any exception during converting the lteDataMetric source
     */
    const val DEFAULT_VAL: Int = -2

    const val LTE_BASE_ENTITY_NULL = "base entity null"
    const val LTE_INVALID_METRICS_DATA = "invalid metrics data"

}
