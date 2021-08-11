package com.tmobile.mytmobile.echolocate.coverage.database

object CoverageDatabaseConstants {

    /**
     * ECHO_LOCATE_COVERAGE_DB_NAME
     *
     * use with getEchoLocateCoverageDatabase to create the database
     * value echolocate_coverage_db
     */
    const val ECHO_LOCATE_COVERAGE_DB_NAME = "echolocate_coverage_db"
    /**
     * COVERAGE_BASE_TABLE_NAME
     *
     * use with entity in BaseCoverageEntity data class to create location table
     * value coverage_base_data
     */
    const val COVERAGE_BASE_TABLE_NAME = "coverage_base_data"
    /**
     * COVERAGE_CELL_IDENTITY_TABLE_NAME
     *
     * use with entity in CellIdentityEntity data class to create location table
     * value coverage_cell_identity
     */
    const val COVERAGE_CELL_IDENTITY_TABLE_NAME = "coverage_cell_identity"
    /**
     * COVERAGE_CELL_SIGNAL_STRENGTH_TABLE_NAME
     *
     * use with entity in CellSignalStrengthEntity data class to create location table
     * value coverage_cell_signal_strength
     */
    const val COVERAGE_CELL_SIGNAL_STRENGTH_TABLE_NAME = "coverage_cell_signal_strength"
    /**
     * COVERAGE_EVENT_DATA_TABLE_NAME
     *
     * use with entity in CoverageSingleSessionReportEntity data class to create location table
     * value coverage_event_data
     */
    const val COVERAGE_REPORT_TABLE_NAME = "coverage_report"
    /**
     * COVERAGE_LOCATION_TABLE_NAME
     *
     * use with entity in LocationEntity data class to create location table
     * value coverage_location
     */
    const val COVERAGE_LOCATION_TABLE_NAME = "coverage_location"
    /**
     * COVERAGE_NET_TABLE_NAME
     *
     * use with entity in CoverageEntity data class to create location table
     * value coverage_net
     */
    const val COVERAGE_NET_TABLE_NAME = "coverage_net"
    /**
     * COVERAGE_NR_CELL_TABLE_NAME
     *
     * use with entity in NrCellEntity data class to create location table
     * value coverage_nr_cell
     */
    const val COVERAGE_NR_CELL_TABLE_NAME = "coverage_nr_cell"
    /**
     * COVERAGE_OEMSV_TABLE_NAME
     *
     * use with entity in CoverageOemSvEntity data class to create location table
     * value coverage_oemsv
     */
    const val COVERAGE_OEMSV_TABLE_NAME = "coverage_oemsv"
    /**
     * COVERAGE_PRIMARY_CELL_TABLE_NAME
     *
     * use with entity in PrimaryCellEntity data class to create location table
     * value coverage_primary_cell
     */
    const val COVERAGE_PRIMARY_CELL_TABLE_NAME = "coverage_primary_cell"
    /**
     * COVERAGE_SETTINGS_TABLE_NAME
     *
     * use with entity in SettingsEntity data class to create location table
     * value coverage_settings
     */
    const val COVERAGE_SETTINGS_TABLE_NAME = "coverage_settings"
    /**
     * COVERAGE_TELEPHONY_TABLE_NAME
     *
     * use with entity in TelephonyEntity data class to create location table
     * value coverage_telephony
     */
    const val COVERAGE_TELEPHONY_TABLE_NAME = "coverage_telephony"
    /**
     * COVERAGE_WIFI_STATUS__TABLE_NAME
     *
     * use with entity in  WifiStatusEntity data class to create location table
     * value coverage_wifi_status
     */
    const val COVERAGE_WIFI_STATUS__TABLE_NAME = "coverage_wifi_status"
}