package com.tmobile.mytmobile.echolocate.reporting.manager

/**
 * Used for define report completion status from modules Voice, Lte, Nr5g, U5scan etc
 */
class ReportStatusFromModules {
    companion object {
        /**
         * STATUS_COMPLETED
         *
         * uses to prepare DIAReportResponseParameters to create DIAReportResponseEvent
         * value Completed
         */
        const val STATUS_COMPLETED = "Completed"

        /**
         * STATUS_ERROR
         * value Error
         */
        const val STATUS_ERROR = "Error"
    }
}