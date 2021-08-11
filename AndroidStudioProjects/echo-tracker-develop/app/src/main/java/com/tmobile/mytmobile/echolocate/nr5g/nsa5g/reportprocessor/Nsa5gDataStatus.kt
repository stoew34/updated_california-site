package com.tmobile.mytmobile.echolocate.nr5g.nsa5g.reportprocessor

class Nsa5gDataStatus {
    companion object {
        /**
         * STATUS_RAW
         *
         * uses to prepare BaseEchoLocateNr5gEntity to save in database
         * value RawData
         */
        const val STATUS_RAW = "RawData"

        /**
         * STATUS_PROCESSED
         *
         * uses to prepare BaseEchoLocateNr5gEntity to save in database
         * value ProcessedData
         */
        const val STATUS_PROCESSED = "ProcessedData"

        /**
         * Used to prepare BaseEchoLocateNr5gEntity to save in database
         * while data insertion in progress.
         */
        const val STATUS_EMPTY = ""


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