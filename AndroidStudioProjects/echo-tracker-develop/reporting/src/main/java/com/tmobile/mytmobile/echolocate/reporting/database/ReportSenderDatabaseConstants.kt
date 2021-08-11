package com.tmobile.mytmobile.echolocate.reporting.database

/**
 * Class that defines all the String constants used for ReportSender database
 */
object ReportSenderDatabaseConstants {

    /**
     * REPORT_SENDER_DATABASE_NAME
     *
     * use with {@link #EchoLocateReportSenderDatabase} to create the database
     * value report_sender_db
     */
    const val REPORT_SENDER_DATABASE_NAME = "report_sender_db"

    /**
     * REPORT_SENDER_TABLE_NAME
     *
     * use with {@link #EchoLocateReportSenderDatabase} entity to create the table
     * value report_sender_table
     */
    const val REPORT_SENDER_TABLE_NAME = "report_sender_table"

    /**
     * REPORT_STATUS_SENT
     *
     * Sets the status of the report as SENT
     * value SENT
     */
    const val REPORT_STATUS_SENT = "SENT"

    /**
     * REPORT_SENDER_TABLE_NAME
     *
     * use with {@link #getReportSenderEntity} to set the status of the report as NOT_SENT
     * value NOT_SENT
     */
    const val REPORT_STATUS_NOT_SENT = "NOT_SENT"

    /**
     * REPORT_SENDER_TABLE_NAME
     *
     * When report module request network module to send the report, it will be updated to this status
     * Later, on network response, the status will be updated either to SENT for success or NOT_SENT for failure
     * value SENDING
     */
    const val REPORT_STATUS_SENDING = "SENDING"

}