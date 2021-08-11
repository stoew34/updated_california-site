package com.tmobile.mytmobile.echolocate.reporting.database.repository

import android.content.Context
import com.tmobile.mytmobile.echolocate.reporting.database.EchoLocateReportSenderDatabase
import com.tmobile.mytmobile.echolocate.reporting.database.dao.ReportSenderDao
import com.tmobile.mytmobile.echolocate.reporting.database.entities.ReportSenderEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

/**
 * The [ReportSenderRepository] repository class will be responsible for interacting with the Room database
 * and will need to provide methods that use the DAO to insert, delete and query product records.
 * @param context :Context the context passed from activity
 */
class ReportSenderRepository(context: Context) {

    /**
     * gets the reportSender DAO instance defined as a abstract class in [ReportSenderDao]
     */
    private val reportSenderDao: ReportSenderDao =
        EchoLocateReportSenderDatabase.getEchoLocateReportSenderDatabase(context).reportSenderDao()

    /**
     * Calls the insert method defined in DAO to insert [ReportSenderEntity] parameters into the database
     * @param reportSenderEntity :ReportSenderEntity object
     */
    fun insertReportSenderEntity(reportSenderEntity: ReportSenderEntity) {
        reportSenderDao.insertReportSenderEntity(reportSenderEntity)
    }

    /**
     * Calls the delete method defined in DAO to delete reportId from the table
     * @param reportId:String the row to delete based on the reportId passed
     */
    fun deleteReport(reportId: String) {
        reportSenderDao.deleteReport(reportId)
    }

    /**
     * Calls the getReportId method defined in DAO to get report ID from the table
     * @param fileName: String the file name for which the report id is retrieved
     * @return String returns the reportid for the file name passed
     */
    fun getReportIdForFile(fileName: String): String = runBlocking(Dispatchers.Default) {
        return@runBlocking withContext(Dispatchers.Default) {
            reportSenderDao.getReportIdForFile(fileName)
        }
    }

    /**
     * Calls the getReportId method defined in DAO to get report ID from the table
     * @param reportId: String the file name for which the report id is retrieved
     * @return String returns the reportid for the file name passed
     */
    fun getReportForReportId(reportId: String): ReportSenderEntity? =
        runBlocking(Dispatchers.Default) {
            return@runBlocking withContext(Dispatchers.Default) {
                reportSenderDao.getReportForReportId(reportId)
            }
        }

    /**
     *
     * Calls the getReports method defined in DAO to get all reports based on the status from the table
     * @param status: String the status for which the reports should be fetched from the table
     * @return List<ReportSenderEntity> returns the list of reports for which the status is SENT/NOT_SENT
     */
    fun getReportsForStatus(status: String): List<ReportSenderEntity> =
        runBlocking(Dispatchers.Default) {
            return@runBlocking withContext(Dispatchers.Default) {
                reportSenderDao.getReportsForStatus(status)
            }
        }

    /**
     * Calls the getReports method defined in DAO to get reports based on the status passed from the database
     * @param status: String the status for which the reports should be fetched from the table
     * @return List<ReportSenderEntity> returns the list of all reports matching the status
     */
    fun getReports(status: String, date: Long): MutableList<ReportSenderEntity> =
        runBlocking(Dispatchers.Default) {
            return@runBlocking withContext(Dispatchers.Default) {
                reportSenderDao.getReports(status, date)
            }
        }

    /**
     * Deletes the reports from the database
     * @param reportSenderList [ReportSenderEntity] to delete
     */
    fun deleteReports(reportSenderList: List<ReportSenderEntity>) {
        reportSenderDao.deleteReports(reportSenderList)
    }

    /**
     * gets all reports in ascending order from the database based on the timestamp
     * @return List<[ReportSenderEntity]> returns list of reports ordered in ascending order based on the timestamp
     */
    fun getReportsInAscOrder(): List<ReportSenderEntity> = runBlocking(Dispatchers.Default) {
        return@runBlocking withContext(Dispatchers.Default) {
            reportSenderDao.getReportsInAscOrder()
        }
    }

    /**
     * Calls the update method defined in DAO to update [ReportSenderEntity] parameters into the database
     * @param reportSenderEntity :ReportSenderEntity object
     */
    fun updateReportSenderEntity(reportSenderEntity: ReportSenderEntity) {
        reportSenderDao.updateReportSenderEntity(reportSenderEntity)
    }
}