package com.tmobile.mytmobile.echolocate.utils

import android.os.Environment
import android.text.TextUtils
import com.tmobile.mytmobile.echolocate.EchoLocateApplication
import com.tmobile.mytmobile.echolocate.variant.Constants
import java.io.*


/**
 * This class handles all the file creations/deletions
 */
@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class FileUtils {

    companion object {

        private const val DEFAULT_ENCODING = "UTF-8"

        private const val SLASH = "/"

        const val debugFolderName: String = "dia_debug"

        private const val MEGABYTES_VALUE = 1024L

        private const val DATAMETRICS_LOG_FILE = "com.tmobile.echolocate.log.manually_5gsa.log"


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
                saveToExisting: Boolean
        ): Boolean {
            if (!Constants.SAVE_DATA_TO_FILE || TextUtils.isEmpty(filePathAndName)
                || TextUtils.isEmpty(content)) {
                EchoLocateLog.eLogI("No permission to read/write storage")
                return false
            }
            if (!isExternalStorageMounted()) {
                EchoLocateLog.eLogE("Storage not mounted.")
                return false
            }
            try {
                val file = File(EchoLocateApplication.getContext()?.externalCacheDir, debugFolderName + SLASH + filePathAndName)
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
                EchoLocateLog.eLogI(filePathAndName + "saved successfully")
                return true
            } catch (error: IOException) {
                EchoLocateLog.eLogE("Error saving file:" + error.message + " " + filePathAndName)
                return false
            }
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
                stream?.close()
            } catch (e: Exception) {
            }

        }
    }


}