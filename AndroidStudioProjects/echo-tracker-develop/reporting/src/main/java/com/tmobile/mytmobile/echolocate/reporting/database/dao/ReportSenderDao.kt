package com.tmobile.mytmobile.echolocate.reporting.database.dao

import androidx.room.*
import com.tmobile.mytmobile.echolocate.reporting.database.ReportSenderDatabaseConstants
import com.tmobile.mytmobile.echolocate.reporting.database.entities.ReportSenderEntity

/**
 * [ReportSenderDao] is responsible for defining the methods that access the database.
 * by using queries annotations
 */
@Dao
interface ReportSenderDao {

    /**
     * Inserts [ReportSenderEntity] parameters into the database in a single transaction.
     * @param reportSenderEntity :ReportSenderEntity object
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertReportSenderEntity(reportSenderEntity: ReportSenderEntity)


    /**
     * Deletes the report from the database based on the report id
     * @param reportId reportId to delete
     */
    @Query("DELETE FROM " + ReportSenderDatabaseConstants.REPORT_SENDER_TABLE_NAME + " WHERE reportId=:reportId")
    fun deleteReport(reportId: String)

    /**
     * gets report id from the database based on the file name passed
     * @param fileName: String the file name for which the report id is retrieved
     * @return String returns the report id for the file path passed
     */
    @Query("SELECT reportId FROM " + ReportSenderDatabaseConstants.REPORT_SENDER_TABLE_NAME + " WHERE fileName = :fileName ")
    fun getReportIdForFile(fileName: String): String

    /**
     *  Fetch ReportSenderEntity by file path
     *  @param fileName: String the file name for which the report sender entity is retrieved
     *  @return: ReportSenderEntity object
     */
    @Query("SELECT * from " + ReportSenderDatabaseConstants.REPORT_SENDER_TABLE_NAME + " WHERE fileName = :fileName ")
    fun getReportSenderEntity(fileName: String): ReportSenderEntity

    /**
     * gets reports based on the status passed from the database
     * @param status: String the status for which the reports should be fetched from the table
     * @return List<ReportSenderEntity> returns the list of all reports matching the status
     */
    @Query("SELECT * FROM " + ReportSenderDatabaseConstants.REPORT_SENDER_TABLE_NAME + " WHERE status = :status ")
    fun getReportsForStatus(status: String): List<ReportSenderEntity>

    /**
     * gets reports based on the status passed from the database
     * @param status: String the status for which the reports should be fetched from the table
     * @return List<ReportSenderEntity> returns the list of all reports matching the status
     */
    @Query("SELECT * FROM " + ReportSenderDatabaseConstants.REPORT_SENDER_TABLE_NAME + " WHERE status = :status AND reportCreationTime < :date")
    fun getReports(status: String, date: Long): MutableList<ReportSenderEntity>

    /**
     * gets reports based on the status passed from the database
     * @param status: String the status for which the reports should be fetched from the table
     * @return List<ReportSenderEntity> returns the list of all reports matching the status
     */
    @Query("SELECT * from " + ReportSenderDatabaseConstants.REPORT_SENDER_TABLE_NAME + " WHERE reportId = :reportId ")
    fun getReportForReportId(reportId: String): ReportSenderEntity

    /**
     * gets report id from the database based on the file name passed
     * @param fileName: String the file name for which the report id is retrieved
     * @return String returns the report id for the file path passed
     */
    @Query("SELECT reportId from " + ReportSenderDatabaseConstants.REPORT_SENDER_TABLE_NAME + " WHERE fileName = :fileName ")
    fun getReportId(fileName: String): String

    /**
     * Update [ReportSenderEntity] parameters into the database in a single transaction.
     * @param reportSenderEntity :[ReportSenderEntity] object
     */
    @Update(onConflict = OnConflictStrategy.IGNORE)
    fun updateReportSenderEntity(reportSenderEntity: ReportSenderEntity)

    /**
     * Deletes the reports from the database
     * @param reportSenderList [ReportSenderEntity] to delete
     */
    @Delete
    fun deleteReports(reportSenderList: List<ReportSenderEntity>)

    /**
     * gets all reports in ascending order from the database based on the timestamp
     * @return List<[ReportSenderEntity]> returns list of reports ordered in ascending order based on the timestamp
     */
    @Query("SELECT * FROM " + ReportSenderDatabaseConstants.REPORT_SENDER_TABLE_NAME + " ORDER BY reportCreationTime ASC ")
    fun getReportsInAscOrder(): List<ReportSenderEntity>

    /**
     * Updates timestamp for a report based on the report id in [ReportSenderEntity]
     * @param updatedTime: Long the timestamp value to update
     * @param reportId:String the report id for which the timestamp should be updated
     */
    @Query("UPDATE " + ReportSenderDatabaseConstants.REPORT_SENDER_TABLE_NAME + " SET reportCreationTime = :updatedTime WHERE reportId = :reportId")
    fun updateReportCreationTime(updatedTime: Long, reportId: String)
}