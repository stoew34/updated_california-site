package com.tmobile.mytmobile.echolocate.reporting.reportsender


import android.content.Context
import android.os.Build
import android.text.TextUtils
import android.widget.Toast
import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonParser
import com.tmobile.myaccount.events.diagnostics.pojos.collector.bundle.ClientSideRequestBundleEnvelope
import com.tmobile.myaccount.events.diagnostics.pojos.collector.event.ClientSideEvent
import com.tmobile.mytmobile.echolocate.reporting.authentication.AuthenticationManager
import com.tmobile.mytmobile.echolocate.reporting.authentication.ITokenReceivedListener
import com.tmobile.mytmobile.echolocate.configuration.model.Report
import com.tmobile.mytmobile.echolocate.network.result.NetworkResponseDetails
import com.tmobile.mytmobile.echolocate.reporting.database.ReportSenderDatabaseConstants
import com.tmobile.mytmobile.echolocate.reporting.database.entities.ReportSenderEntity
import com.tmobile.mytmobile.echolocate.reporting.database.repository.ReportSenderRepository
import com.tmobile.mytmobile.echolocate.reporting.utils.*
import com.tmobile.mytmobile.echolocate.reporting.utils.ReportingLog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.util.*
import kotlin.collections.ArrayList


class ReportSender private constructor(val context: Context) :
    ReportResponseListener {

    private lateinit var reportConfig: Report
    private var reportSenderRepository: ReportSenderRepository = ReportSenderRepository(context)
    private var reportNetworkManager: ReportNetworkManager = ReportNetworkManager(context, this)

    private val JSON_EVENTS_PROPERTY = "events"

    private var workIdForScheduledJob: String? = null

    private var reportsSentToNetwork: Int = 0
    private var reportsFailedToSentToNetwork: Int = 0
    private var numberOfReportsTobeSent: Int = 0

    /**
     * singleton creation for Report Sender
     */
    companion object : SingletonHolder<ReportSender, Context>(::ReportSender) {
        const val DIA_REPORT_PREFIX = "DIA_"

    }

    /**
     * This function convert list of ClientSideEvent data to JSON,
     * save it to storage file and its param to DB by using [saveReport],
     * check roaming and network connection by [isAllowedToSend],
     * and perform network request for all new data by [performNetworkRequest]
     * and deletes sent report files from phone storage via [deleteSentReportsFromInternal]
     * Returns true if the list of ClientSideEvent is processed and the data is stored in the file
     * Returns false if there is an error while processing list of error while storing the data in file     *
     */
    fun processReportSending(clientSideEventList: List<ClientSideEvent>): Boolean {
        /**
         * Setting the date format is important because gson changes the date format on conversion to json element.
         * This date format is a contract between server and client. Wrong date format will be rejected by the server.
         *
         */
        var bListProcessed = false
        if (clientSideEventList.isNotEmpty()) {
            val gson = GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").create()
            val jsonClientSideEventData =
                convertToClientSideRequestBundleEnvelope(gson.toJson(clientSideEventList))

            val filePath = saveReport(jsonClientSideEventData)
            showToastForDebugBuild(filePath)

            if (filePath.isNotEmpty()) {
                bListProcessed = true
            }
        }

        if (isNetworkAvailable() && isAllowedToSendWhileRoaming()) {
            val authManager = AuthenticationManager.getInstance(context)
            if (authManager.isLocallyStoredTokenExpired()) {
                GlobalScope.launch(Dispatchers.IO) {
                    authManager.initAgentAndGetDatSilent(
                        object : ITokenReceivedListener {
                            override fun onReceivedToken(token: String) {
                                if (token.isNotBlank()) {
                                    performNetworkRequest()
                                    deleteSentReportsFromInternal()
                                    ReportingLog.eLogD("Diagnostic : Sent reports removed " +
                                            "from internal storage", context)
                                } else {
                                    ReportingUtils.sendJobCompletedToScheduler(
                                        workIdForScheduledJob,
                                        ReportingModuleConstants.REPORT_GENERATOR_COMPONENT_NAME, context
                                    )
                                    ReportingUtils.postReportSentEvent(
                                        ReportSentEvent(
                                            numberOfReportsTobeSent,
                                            NO_REPORTS_SENT_TO_NETWORK
                                        )
                                    )
                                }
                            }
                        }
                    )
                }
            } else {
                GlobalScope.launch(Dispatchers.IO) {
                    performNetworkRequest()
                    deleteSentReportsFromInternal()
                    ReportingLog.eLogD("Diagnostic : Sent reports removed from internal storage", context)
                }
            }
        } else {
            ReportingUtils.sendJobCompletedToScheduler(
                workIdForScheduledJob,
                ReportingModuleConstants.REPORT_GENERATOR_COMPONENT_NAME, context
            )
            ReportingUtils.postReportSentEvent(
                ReportSentEvent(
                    numberOfReportsTobeSent,
                    NO_REPORTS_SENT_TO_NETWORK
                )
            )
        }
        return bListProcessed
    }

    /**
     * This function perform network request for saved reports with status NOT_SENT
     * -1- Get list of reports from DB with status NOT_SENT by using [getNotSentReportsFromDB]
     * -2- Check roaming and network connection by [isNetworkAvailable] and [isAllowedToSendWhileRoaming]
     * -3- From file storage get saved report(fileData) by fileName and send to network
     * -4- Convert data from file storage to ClientSideRequestBundleEnvelope by [convertToClientSideRequestBundleEnvelope]
     */
    @Synchronized
    private fun performNetworkRequest() {
        val reportList = getNotSentReportsFromDB()
        numberOfReportsTobeSent = reportList.size
        ReportingLog.eLogD("Diagnostic : List of not sent reports requested, " +
                "ReportList size: " + reportList.size, context)

        reportsSentToNetwork = 0
        reportsFailedToSentToNetwork = 0
        for (report in reportList) {
            if (isNetworkAvailable() && isAllowedToSendWhileRoaming()) {
                reportsSentToNetwork++
                val fileData = File(report.fileName).readText(Charsets.UTF_8)
                ReportingLog.eLogD("Diagnostic : Networkmanager - Post report to server: " +
                        "${report.reportId}", context)
//                val payload = convertToClientSideRequestBundleEnvelope(fileData)
                ReportingModuleSharedPrefs.tokenObject?.let {
                    reportNetworkManager.postReportToServer(
                        report.fileName, report.reportId, it
                    )
                }
            } else {
                ReportingLog.eLogD(
                    "Diagnostic : Unable to send DIA Report with " +
                            "reportId ${report.reportId} as network is not available", context
                )
            }
        }
        // If network is not available after entering into for loop, no reports were sent.
        // In this situation, we have to inform scheduler that the job is completed.
        if (reportsSentToNetwork == 0) {
            ReportingUtils.sendJobCompletedToScheduler(
                workIdForScheduledJob,
                ReportingModuleConstants.REPORT_GENERATOR_COMPONENT_NAME, context
            )
            ReportingUtils.postReportSentEvent(
                ReportSentEvent(
                    numberOfReportsTobeSent,
                    reportsSentToNetwork
                )
            )
        }
    }

    /**
     * The string stored in the file is a list of [ClientSideEvent] but it cannot be converted to that
     * since it doesn't recognize the child types of [BaseEventData].
     * So it has to be converted into jsonObject and then add the properties such as "events"
     */
    private fun convertToClientSideRequestBundleEnvelope(jsonClientSideEventData: String): String {

        /**
         * Setting the date format is important because gson changes the date format
         * on conversion to json element.
         * This date format is a contract between server and client.
         * Wrong date format will be rejected by the server.
         */
        val gson =
            GsonBuilder().disableHtmlEscaping().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").create()

        val jsonParser = JsonParser()

        val jsonArray = jsonParser.parse(jsonClientSideEventData) as JsonArray

        val clientSideRequestBundleEnvelope = initClientSideRequestBundleEnvelope()

        val jsonElement = gson.toJsonTree(clientSideRequestBundleEnvelope)

        val jsonEnvelope = jsonElement.asJsonObject

        jsonEnvelope.add(JSON_EVENTS_PROPERTY, jsonArray)

        return gson.toJson(jsonEnvelope)
    }

    /**
     *  This function initialize [ClientSideRequestBundleEnvelope],
     *  and define parameters that will be used in final report sent to server.
     *
     *  @return ClientSideRequestBundleEnvelope
     */
    private fun initClientSideRequestBundleEnvelope(): ClientSideRequestBundleEnvelope {
        val request = ClientSideRequestBundleEnvelope()

        request.timestamp = Date()
        request.deviceId = Build.DEVICE
        request.clientVersion = ReportingModuleSharedPrefs.clientAppVersionName

        return request
    }

    /**
     * This function sends the report by creating a file with the json provided
     * to the application's internal storage in 'dia' folder and saves the same
     * report to the external storage
     * @param dataJson: String the json to store
     *
     * @return String: Returns the fully qualified path of the file saved in the internal storage
     */
    fun saveReport(dataJson: String): String {

        val reportId = UUID.randomUUID().toString()

        val fileName = "${DIA_REPORT_PREFIX}$reportId"

        saveReportToExternalForDebug(dataJson, fileName)

        val filePath = saveReportData(dataJson, fileName)

        saveReportMetaDataToDatabase(reportId, filePath)

        return filePath
    }

    /**
     * saves the report received as string to the application's internal storage in 'dia' folder
     * @param dataJson: String the json to store
     * @param fileName: String the name of the file to be stored
     *
     * @return String: Returns the fully qualified path of the file saved in the internal storage
     */
    private fun saveReportData(dataJson: String, fileName: String): String {

        val filePath = ReportingFileUtils.saveFileToInternalStorage(context, dataJson, fileName)
        ReportingLog.eLogV("Diagnostic : Report saved at : $filePath", context)
        return filePath
    }

    /**
     * gets all records in DB with status SENT
     * @return List<ReportSenderEntity>, a list of records with SENT status
     */
    private fun getSentReportsFromDB(): List<ReportSenderEntity> {
        return reportSenderRepository.getReportsForStatus(ReportSenderDatabaseConstants.REPORT_STATUS_SENT)
    }

    /**
     * deletes all files from internal storage where report status is SENT
     */
    fun deleteSentReportsFromInternal() {
        val sentReportList = getSentReportsFromDB()
        if (sentReportList.isNotEmpty()) {
            ReportingFileUtils.deleteFileFromInternalStorage(sentReportList)
        }
    }

    /**
     * Allows the manager class to pass the reporting module configuration to the report sender.
     *
     * The report sender needs this configuration to get the retention size,
     * retention time and number of retries for which the reports should be kept.
     */
    fun setReportConfiguration(reportConfig: Report) {
        this.reportConfig = reportConfig
    }

    /**
     * This Function is used to take the file path as input and update the message accordingly
     * if the file path is available the success toast will be displayed else failure will be displayed
     */
    private fun showToastForDebugBuild(filePath: String) {
        if (ReportingModuleSharedPrefs.clientAppBuildTypeDebug) {
            if (filePath.isNotEmpty()) {
                showToast(ReportingModuleConstants.REPORT_STATUS_SUCCESS)
            } else {
                showToast(ReportingModuleConstants.REPORT_STATUS_FAILURE)
            }
        }
    }

    private fun showToast(message: String) {
        GlobalScope.launch(Dispatchers.Main) {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }
    }

    /**
     * This function checking roaming and network connection status by using utils class [ConnectionCheckUtils]
     * logic:
     * no roaming -> Allowed To Send report for any type of connection
     * roaming + Wifi -> Allowed To Send report
     * roaming + no Wifi -> Not Allowed To Send report
     */
    private fun isAllowedToSendWhileRoaming(): Boolean {
        return (!ReportingConnectionCheckUtils.checkIsRoaming(context) ||
                ReportingConnectionCheckUtils.checkIsWifi(context))
    }

    /**
     * This function checking if any active network is available before calling the checks for roaming
     */
    private fun isNetworkAvailable(): Boolean {
        return ReportingConnectionCheckUtils.checkIsNetworkAvailable(context)
    }

    /**
     * This function checking select all report with status NOT SENT
     */
    private fun getNotSentReportsFromDB(): List<ReportSenderEntity> {
        val reportsToSend =
            reportSenderRepository.getReportsForStatus(ReportSenderDatabaseConstants.REPORT_STATUS_NOT_SENT)
        reportsToSend.forEach {
            it.status = ReportSenderDatabaseConstants.REPORT_STATUS_SENDING
            it.httpError = "" // Remove the old http error if any
            reportSenderRepository.updateReportSenderEntity(it)
        }
        return reportsToSend
    }

    /**
     * This function checks all report with status SENDING, amd update those to NOT_SENT
     */
    fun cleanReportsDatabase() {
        val reportsInSendingStatus =
            reportSenderRepository.getReportsForStatus(ReportSenderDatabaseConstants.REPORT_STATUS_SENDING)
        for (reportEntity in reportsInSendingStatus) {
            reportEntity.status = ReportSenderDatabaseConstants.REPORT_STATUS_NOT_SENT
            reportEntity.httpError = "" // Remove the old http error if any
            reportSenderRepository.updateReportSenderEntity(reportEntity)
        }
    }

    /**
     * Saves the same report to the external storage
     * @param dataJson: String the json to store
     * @param fileName: String the name of the file to be stored
     *
     * @return Boolean: true if the file was saved successfully, false otherwise
     */
    private fun saveReportToExternalForDebug(dataJson: String, fileName: String): Boolean {
        return ReportingFileUtils.saveFileToExternalStorage(dataJson, fileName, false, context)
    }

    /**
     * Saves the report meta data in the database
     * @param reportId: String the reportId to save
     * @param filePath:String filepath to save
     */
    private fun saveReportMetaDataToDatabase(reportId: String, filePath: String) {
        val reportSenderEntity = getReportSenderEntity(reportId, filePath)
        reportSenderRepository.insertReportSenderEntity(reportSenderEntity)
        ReportingLog.eLogD(
            "Diagnostic : Networkmanager - Inserted report data into reportsender table " +
                    "for reportId $reportId", context
        )
    }

    /**
     * Generate reportSender Entity by using the values passed\
     * @param reportId: String reportId to save
     * @param filePath: String filePath to save
     * @return [ReportSenderEntity] returns converted Entity
     */
    private fun getReportSenderEntity(reportId: String, filePath: String): ReportSenderEntity {
        return ReportSenderEntity(
            reportId,
            Calendar.getInstance().timeInMillis,
            filePath,
            ReportSenderDatabaseConstants.REPORT_STATUS_NOT_SENT,
            null
        )
    }

    /**
     * This function checking if response is success(200)
     * For DEBUG mode sent data marks as SENT in DB and keeps in Internal Storage
     * For other all data with status SENT deleted from Internal Storage and DB
     */
    override fun onNetworkResponse(networkResponse: NetworkResponseDetails) {
        if (networkResponse.httpResponseCode == "200" && TextUtils.isEmpty(networkResponse.errorMsg)) {

            ReportingLog.eLogD(
                "Diagnostic : sending DIA report: Success with response code: ${networkResponse.httpResponseCode}" +
                        " at ${ReportingDateUtils.convertToShemaDateFormat(
                            System.currentTimeMillis().toString()
                        )}", context
            )
            ReportingLog.eLogD("Diagnostic : Report sender request code " +
                    "${networkResponse.requestCode} ", context)
            val reportEntity =
                reportSenderRepository.getReportForReportId(networkResponse.requestCode) ?: return

            // This should never happen but if it happens then do no do anything.
            reportEntity.status = ReportSenderDatabaseConstants.REPORT_STATUS_SENT
            reportEntity.httpError = networkResponse.httpResponseCode
            reportSenderRepository.updateReportSenderEntity(reportEntity)

            if (ReportingModuleSharedPrefs.clientAppBuildTypeDebug) {
                // Job completed status should be sent only after sending all the reports
                // including NOT_SENT reports from previous session
                if (--reportsSentToNetwork <= 0) {
                    ReportingUtils.sendJobCompletedToScheduler(
                        workIdForScheduledJob,
                        ReportingModuleConstants.REPORT_GENERATOR_COMPONENT_NAME, context
                    )
                    ReportingUtils.postReportSentEvent(
                        ReportSentEvent(
                            numberOfReportsTobeSent,
                            (numberOfReportsTobeSent - reportsFailedToSentToNetwork)
                        )
                    )
                }
                return
            }

            deleteReport(networkResponse.requestCode)
            val deleteFileList = ArrayList<String>()

            deleteFileList.add(reportEntity.fileName)
            deleteFileFromInternalStorage(deleteFileList)

        } else {
            reportsFailedToSentToNetwork++
            ReportingLog.eLogE(
                "Diagnostic : sending DIA report: Error sending report: ${networkResponse.errorMsg} " +
                        "with response code ${networkResponse.httpResponseCode}" +
                        " at ${ReportingDateUtils.convertToShemaDateFormat(
                            System.currentTimeMillis().toString()
                        )}"
            )
            val reportEntity =
                reportSenderRepository.getReportForReportId(networkResponse.requestCode)
            if (reportEntity != null) {
                reportEntity.httpError = networkResponse.httpResponseCode + " : " + networkResponse.errorMsg
                reportEntity.status = ReportSenderDatabaseConstants.REPORT_STATUS_NOT_SENT
                reportSenderRepository.updateReportSenderEntity(reportEntity)
            }
        }

        // Job completed status should be sent only after sending all the reports
        // including NOT_SENT reports from previous session
        if (--reportsSentToNetwork <= 0) {
            ReportingUtils.sendJobCompletedToScheduler(
                workIdForScheduledJob,
                ReportingModuleConstants.REPORT_GENERATOR_COMPONENT_NAME, context
            )
            ReportingUtils.postReportSentEvent(
                ReportSentEvent(
                    numberOfReportsTobeSent,
                    (numberOfReportsTobeSent - reportsFailedToSentToNetwork)
                )
            )
        }
    }

    /**
     * Deletes the record from the database based on the reportId passed
     * @param reportId:String the row to delete
     */
    private fun deleteReport(reportId: String) {
        reportSenderRepository.deleteReport(reportId)
    }

    /**
     * This function deletes files from the internal storage and records
     * from the database which are older than the retention internal and which
     * extends the retention size
     */
    fun deleteReportsFromStorage() {
        val retentionSize = reportConfig.retentionSize
        val retentionIntervalInDays =
            ReportingDateUtils.convertHoursToDays(reportConfig.retentionInterval)
        val daysBeforeCurrentDate = ReportingDateUtils.getDateBeforeDays(retentionIntervalInDays)

        deleteReportsBasedOnDate(daysBeforeCurrentDate)
        deleteReportsBasedOnSize(retentionSize)
    }

    /**
     * This method gets the list of reports from database that are not sent
     * and which are older than the retention interval and deletes it from internal
     * storage and from the database
     * @param daysBeforeCurrentDate: Long rentention interval in days
     */
    fun deleteReportsBasedOnDate(daysBeforeCurrentDate: Long) {
        val reportsList = reportSenderRepository.getReports(
            ReportSenderDatabaseConstants.REPORT_STATUS_NOT_SENT,
            daysBeforeCurrentDate
        )

        if (reportsList.isNotEmpty()) {
            val deleteFileList = arrayListOf<String>()
            reportsList.forEach {
                deleteFileList.add(it.fileName)
            }

            deleteFileFromInternalStorage(deleteFileList)

            val folderSizeAfterDeleting =
                ReportingFileUtils.getNumberOfFilesInFolder(ReportingFileUtils.getInternalStorageDirectory(context))

            ReportingLog.eLogV("Diagnostic : days - Number of files after deleting from " +
                    "internal storage: $folderSizeAfterDeleting", context)

            reportSenderRepository.deleteReports(reportsList)

            val dbSizeAfterDeleting = reportSenderRepository.getReports(
                ReportSenderDatabaseConstants.REPORT_STATUS_NOT_SENT,
                daysBeforeCurrentDate
            )
            ReportingLog.eLogV("Diagnostic : days - records deleted from database: " +
                    "$dbSizeAfterDeleting", context)
        }
    }

    /**
     * This method checks the folder size of the internal storage
     * and deletes the files recursively till the folder size reaches the rention size
     * and the same reports are
     * @param retentionSize: Int the maximum of the folder in MB that is saved in the internal storage
     */
    private fun deleteReportsBasedOnSize(retentionSize: Int) {
        val folderSize = ReportingFileUtils.getDirectorySizeInMegaBytes(context)
        ReportingLog.eLogV("size - folder size is: $folderSize", context)

        if (folderSize > retentionSize) {

            val noOfBytesToReduce = folderSize - retentionSize
            val list = reportSenderRepository.getReportsInAscOrder()
            val deleteReportEntityList = arrayListOf<ReportSenderEntity>()
            val deleteFileList = arrayListOf<String>()
            var filesSize = 0.0

            ReportingLog.eLogV("size - no of bytes to reduce: $noOfBytesToReduce", context)
            ReportingLog.eLogV("size - no of records from db: ${list.size}", context)

            for (reportSenderEntity in list) {
                if (filesSize < noOfBytesToReduce) {
                    filesSize += ReportingFileUtils.getFileSize(reportSenderEntity.fileName)
                    deleteFileList.add(reportSenderEntity.fileName)
                    deleteReportEntityList.add(reportSenderEntity)
                    ReportingLog.eLogV("size - file size of each file: $filesSize", context)
                } else
                    break
            }
            ReportingLog.eLogV("size - no of records to delete: ${deleteReportEntityList.size}", context)

            deleteFileFromInternalStorage(deleteFileList)
            reportSenderRepository.deleteReports(deleteReportEntityList)

            val sizeAfterDeleting = ReportingFileUtils.getDirectorySizeInMegaBytes(context)
            ReportingLog.eLogV("size - folder size after deleting is:$sizeAfterDeleting", context)

            val dbSizeAfterDeleting = reportSenderRepository.getReportsForStatus(
                ReportSenderDatabaseConstants.REPORT_STATUS_NOT_SENT
            )
            ReportingLog.eLogV("size - records deleted from database: $dbSizeAfterDeleting", context)
        }
    }

    /**
     * This method deletes file from internal storage
     * @param deleteFileList: ArrayList<String> list of files to delete
     */
    private fun deleteFileFromInternalStorage(deleteFileList: ArrayList<String>) {
        ReportingLog.eLogV("days - Number of files to delete: ${deleteFileList.size}", context)
        if (deleteFileList.isNotEmpty()) {
            if (ReportingFileUtils.deleteFiles(deleteFileList))
                ReportingLog.eLogV("size - files deleted successfully", context)
            else
                ReportingLog.eLogV("size - unable to delete file", context)
        }
    }

    /**
     * When report manager [ReportManager] requests all modules to get the individual report, as part of scheduled job,
     * we have to set this value [workIdForScheduledJob], so that the job-completed-status will be informed.
     * If the parameter is null, it means the request is not coming as part of scheduled job, like requesting
     * reports from debug UI
     */
    fun setWorkIdForScheduledJob(workId: String?) {
        workIdForScheduledJob = workId
    }
}
