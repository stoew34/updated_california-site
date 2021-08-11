package com.tmobile.mytmobile.echolocate.coverage.utils

/**
 * Created by Mahesh Shetye on 2020-04-23
 *
 * Constant commonly used in Coverage module
 */

object CoverageConstants {

    /**
     * SCHEMA_VERSION
     * The version of the schema for which the data is being reported.
     * value 3.0
     */
    const val SCHEMA_VERSION = "3.0"

    /**
     * ResetTriggerCount
     */
    const val TRIGGER_COUNT_RESET_ACTION = "ResetTriggerCount"

    /**
     * COVERAGE_TRIGGER_ALARM_CODE
     *
     * Hourly coverage trigger alarm code
     * value 1000
     */
    val COVERAGE_TRIGGER_ALARM_CODE = 1000

    /**
     * REPORT_GENERATOR_COMPONENT_NAME
     *
     * Report generator component name for coverage module
     * value CoverageReportScheduler
     */
    val REPORT_GENERATOR_COMPONENT_NAME = "CoverageReportScheduler"

    const val COVERAGE_TRIGGER_NULL = "trigger null"
    const val COVERAGE_BASE_ENTITY_NULL = "baseEntity null"
}