package com.tmobile.mytmobile.echolocate.coverage.reportprocessor

/**
 * Created by Mahesh Shetye on 2020-04-27
 *
 * Model call for coverage data status
 */

class CoverageDataStatus {
    companion object {
        /**
         * STATUS_RAW
         *
         * uses to prepare BaseEchoLocateCoverageEntity to save in database
         * value RawData
         */
        const val STATUS_RAW = "RawData"

        /**
         * STATUS_PROCESSED
         *
         * uses to prepare BaseEchoLocateCoverageEntity to save in database
         * value ProcessedData
         */
        const val STATUS_PROCESSED = "ProcessedData"

        /**
         * STATUS_REPORTING
         *
         * Uses to prepare records from report table to send to report-module
         * Once these records are stored into report file, the records will be deleted from report table
         * value REPORTING
         */
        const val STATUS_REPORTING = "REPORTING"
    }
}