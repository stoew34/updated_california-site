package com.tmobile.mytmobile.echolocate.reporting.utils

import android.content.Context
import android.content.ContextWrapper
import android.os.Environment
import android.text.TextUtils
import com.tmobile.mytmobile.echolocate.reporting.database.entities.ReportSenderEntity
import com.tmobile.mytmobile.echolocate.reporting.utils.ReportingDeviceUtils
import com.tmobile.mytmobile.echolocate.reporting.utils.ReportingLog
import java.io.*


/**
 * Created by Divya Mittal on 04/09/2021
 *
 * This class handles all the file creations/deletions
 */
@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class ReportingFileUtils {

    companion object {

        private const val DEFAULT_ENCODING = "UTF-8"

        private const val SLASH = "/"

        private const val debugFolderName: String = "dia_debug"

        private const val MEGABYTES_VALUE = 1024L

        /**
         * Check if the name provided exists, if it exists, append
         * the new content to the existing one. If the file doesn't exist,
         * create a new file and write the content.
         * @param content: String content to be written
         * @param filePathAndName:String file name to save
         * @param saveToExisting: Boolean flag to over write the file complete
         */
        fun saveFileToExternalStorage(
            content: String,
            filePathAndName: String,
            saveToExisting: Boolean,
            context: Context
        ): Boolean {
            if (!ReportingModuleSharedPrefs.clientAppSaveToFile || TextUtils.isEmpty(filePathAndName) || TextUtils.isEmpty(
                    content
                )
            ) {
                ReportingLog.eLogI("No permission to read/write storage")
                return false
            }
            if (!isExternalStorageMounted()) {
                ReportingLog.eLogE("Storage not mounted.")
                return false
            }
            try {
                val file: File = if (ReportingDeviceUtils.isRDeviceOrHigher()) {
                    File(context.externalCacheDir, debugFolderName + SLASH + filePathAndName)
                } else {
                    File(
                        Environment.getExternalStorageDirectory(),
                        debugFolderName + SLASH + filePathAndName
                    )
                }
                val dir = File(file.parent)
                if (!saveToExisting || !isSdCardFileExists(filePathAndName)) {
                    dir.mkdirs()
                    file.createNewFile()
                }
                val fos = FileOutputStream(file, saveToExisting)
                val osw = OutputStreamWriter(fos, DEFAULT_ENCODING)
                val out = BufferedWriter(osw)
                out.write(content)
                out.close()
                ReportingLog.eLogI(filePathAndName + "saved successfully")
                return true
            } catch (error: IOException) {
                ReportingLog.eLogE("Error saving file:" + error.message + " " + filePathAndName)
                return false
            }
        }

        /**
         * writes content to the application's internal storage
         * @param context:context
         * @param fileContent:String content to write
         * @param fileName:String the name of the file to save
         */
        fun saveFileToInternalStorage(
            context: Context,
            fileContent: String,
            fileName: String
        ): String {

            val directory = getInternalStorageDirectory(context)
            val file = File(directory, fileName)
            try {
                val fos = FileOutputStream(file)

                fos.write(fileContent.toByteArray())
                fos.close()
                return file.absolutePath
            } catch (ex: java.lang.Exception) {
                ReportingLog.eLogE("error while writing to file")
                return ""
            }
        }


        /**
         *  deletes all reports that have a report SENT status in db
         *  @param list a list of records with SENT status
         */
        fun deleteFileFromInternalStorage(list: List<ReportSenderEntity>) {

            for (report in list) {
                try {
                    File(report.fileName).delete()
                } catch (ex: Exception) {
                    ReportingLog.eLogE("error: ${ex.localizedMessage}")
                }
            }

        }


        /**
         * Gets the path of internal storage directory
         * @param context:Context the context of the calling module
         * @return File returns the path of the file
         */
        fun getInternalStorageDirectory(context: Context): File {
            val filePath = ReportingModuleConstants.INTERNAL_STORAGE_FOLDER
            val contextWrapper = ContextWrapper(context)
            return contextWrapper.getDir(filePath, Context.MODE_PRIVATE)
        }

        /**
         * calculates the size of the directory in mega bytes
         * @param context:Context the context of the calling module
         * @return Double: returns the calculated directory size in Mega Bytes
         */
        fun getDirectorySizeInMegaBytes(context: Context): Double {
            val size = getDirectorySize(getInternalStorageDirectory(context))
            return convertBytesToMegaBytes(size.toDouble())
        }

        /**
         * Calculates the size of the given folder
         * @param folder:File the folder size to calculate
         * @return Long returns the size of the folder in bytes
         */
        private fun getDirectorySize(folder: File): Long {
            var length: Long = 0
            val files = folder.listFiles()

            val count = files.size

            for (i in 0 until count) {
                if (files[i].isFile) {
                    length += files[i].length()
                } else {
                    length += getDirectorySize(files[i])
                }
            }
            return length
        }

        /**
         * Calculates the size of the file
         * @param fileName:String the file for which the size is to be calculated
         * @return Double returns the size of the file in Mega Bytes
         */
        fun getFileSize(fileName: String): Double {
            val file = File(fileName)
            return convertBytesToMegaBytes(file.length().toDouble())
        }

        /**
         * converts given bytes to mega bytes
         * @param size:Double size in bytes
         * @return Double returns the size in mega bytes
         */
        private fun convertBytesToMegaBytes(size: Double): Double {

            return size / (MEGABYTES_VALUE * MEGABYTES_VALUE)
        }

        /**
         * Deletes the files from the file storage by iterating through each file passed as list
         * @param files: List<String> the list of files to delete
         * @return Boolean returns the file deletion status
         */
        fun deleteFiles(files: List<String>): Boolean {
            var flag = false
            files.forEach { fileName ->
                val file = File(fileName)
                flag = if (!file.exists()) {
                    false
                } else {
                    file.delete()
                }
            }
            return flag
        }

        /**
         * Gets the number of files in folder
         * @param folder:File the folder for which the files are to be calculated
         * @return Int returns the count of files in the folder
         */
        fun getNumberOfFilesInFolder(folder: File): Int {
            return folder.listFiles().size
        }

        /**
         * checks if external storage is mounted
         * @return Boolean
         */
        fun isExternalStorageMounted(): Boolean {
            val status = Environment.getExternalStorageState()
            return Environment.MEDIA_MOUNTED == status
        }

        /**
         * checks if file exists in the sdcard
         * @return Boolean
         */
        fun isSdCardFileExists(fileName: String): Boolean {
            val sdcard = Environment.getExternalStorageDirectory()
            val tmoConfigFile = File(sdcard, fileName)
            return tmoConfigFile.exists()

        }

        /**
         * Provides safe closing for stream {@code closeable}
         *
         * @param stream closeable (stream) to close
         */
        fun closeSafely(stream: Closeable?) {
            try {
                if (stream != null) {
                    stream.close()
                }
            } catch (e: Exception) {
            }

        }
    }


}