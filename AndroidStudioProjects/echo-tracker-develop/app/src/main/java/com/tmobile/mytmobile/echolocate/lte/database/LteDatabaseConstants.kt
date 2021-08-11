package com.tmobile.mytmobile.echolocate.lte.database

import com.tmobile.mytmobile.echolocate.lte.database.entity.*

/**
 * Class that defines all the String constants used for LTE database
 */
object LteDatabaseConstants {

    /**
     * ECHO_LOCATE_LTE_DB_NAME
     *
     * use with getEchoLocateLteDatabase to create the database
     * value echolocate_lte_db
     */
    const val ECHO_LOCATE_LTE_DB_NAME = "echolocate_lte_db"

    /**
     * LTE_LOCATION_TABLE_NAME
     *
     * use with entity in [LteLocationEntity] data class to create location table
     * value lte_location
     */
    const val LTE_LOCATION_TABLE_NAME = "lte_location"

    /**
     * ECHO_LOCATE_LTE_BASE_TABLE_NAME
     *
     * use with entity in [BaseEchoLocateLteEntity] data class to create base lte table
     * value lte_echo_locate_base_data
     */
    const val ECHO_LOCATE_LTE_BASE_TABLE_NAME = "lte_echo_locate_base_data"

    /**
     * LTE_BEARER_CONFIGURATION_TABLE_NAME
     *
     * use with entity in [BearerConfigurationEntity] data class to create bearer configuration table
     * value lte_bearer_configuration
     */
    const val LTE_BEARER_CONFIGURATION_TABLE_NAME = "lte_bearer_configuration"

    /**
     * LTE_BEARER_TABLE_NAME
     *
     * use with entity in [BearerEntity] data class to create Bearer table
     * value lte_bearer
     */
    const val LTE_BEARER_TABLE_NAME = "lte_bearer"

    /**
     * LTE_CA_TABLE_NAME
     *
     * use with entity in [CAEntity] data class to create CAData table
     * value lte_ca
     */
    const val LTE_CA_TABLE_NAME = "lte_ca"

    /**
     * LTE_COMMON_RF_CONFIGURATION_TABLE_NAME
     *
     * use with entity in [CommonRFConfigurationEntity] data class to create CommonRFConfiguration table
     * value lte_common_rf_configuration
     */
    const val LTE_COMMON_RF_CONFIGURATION_TABLE_NAME = "lte_common_rf_configuration"

    /**
     * LTE_DOWNLINK_CARRIER_INO_TABLE_NAME
     *
     * use with entity in [DownLinkCarrierInfoEntity] data class to create Down Link Carrier Info lte table
     * value lte_downlink_carrier_info
     */
    const val LTE_DOWNLINK_CARRIER_INO_TABLE_NAME = "lte_downlink_carrier_info"

    /**
     * LTE_DOWNLINK_RF_CONFIGURATION_TABLE_NAME
     *
     * use with entity in [DownlinkRFConfigurationEntity] data class to create DownlinkRfConfiguration table
     * value lte_downlink_rf_configuration
     */
    const val LTE_DOWNLINK_RF_CONFIGURATION_TABLE_NAME = "lte_downlink_rf_configuration"

    /**
     * LTE_NETWORK_IDENTITY_TABLE_NAME
     *
     * use with entity in [NetworkIdentityEntity] data class to create Network Identity table
     * value lte_network_identity
     */
    const val LTE_NETWORK_IDENTITY_TABLE_NAME = "lte_network_identity"

    /**
     * LTE_OEMSV_TABLE_NAME
     *
     * use with entity in [LteOEMSVEntity] data class to create OEMSV table
     * value lte_oemsv
     */
    const val LTE_OEMSV_TABLE_NAME = "lte_oemsv"

    /**
     * LTE_SECOND_CARRIER_TABLE_NAME
     *
     * use with entity in [SecondCarrierEntity] data class to create Second Carrier table
     * value lte_second_carrier
     */
    const val LTE_SECOND_CARRIER_TABLE_NAME = "lte_second_carrier"

    /**
     * LTE_SETTINGS_TABLE_NAME
     *
     * use with entity in [LteSettingsEntity] data class to create Settings table
     * value lte_settings
     */
    const val LTE_SETTINGS_TABLE_NAME = "lte_settings"

    /**
     * LTE_SIGNAL_CONDITION_TABLE_NAME
     *
     * use with entity in [SignalConditionEntity] data class to create Signal Condition table
     * value lte_signal_condiftion
     */
    const val LTE_SIGNAL_CONDITION_TABLE_NAME = "lte_signal_condiftion"

    /**
     * LTE_THIRD_CARRIER_TABLE_NAME
     *
     * use with entity in [ThirdCarrierEntity] data class to create Third Carrier table
     * value lte_third_carrier
     */
    const val LTE_THIRD_CARRIER_TABLE_NAME = "lte_third_carrier"

    /**
     * LTE_UPLINK_CARRIER_INFO_TABLE_NAME
     *
     * use with entity in [UplinkCarrierInfoEntity] data class to create Up Link Carrier Info table
     * value lte_uplink_carrier_info
     */
    const val LTE_UPLINK_CARRIER_INFO_TABLE_NAME = "lte_uplink_carrier_info"

    /**
     * LTE_UPLINK_RF_CONFIGURATION_TABLE_NAME
     *
     * use with entity in [UpLinkRFConfigurationEntity] data class to create Up Link Rf Configuration table
     * value lte_uplink_rf_configuration
     */
    const val LTE_UPLINK_RF_CONFIGURATION_TABLE_NAME = "lte_uplink_rf_configuration"

    /**
     * LTE_REPORT_TABLE_NAME
     *
     * use with entity [LteSingleSessionReportEntity] data class to combine all data to reports
     */
    const val LTE_REPORT_TABLE_NAME = "lte_report"

}