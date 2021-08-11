package com.tmobile.mytmobile.echolocate.playground.activities

import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.tmobile.mytmobile.echolocate.EchoLocateApplication
import com.tmobile.mytmobile.echolocate.R
import com.tmobile.mytmobile.echolocate.utils.ELDeviceUtils
import com.tmobile.mytmobile.echolocate.utils.EchoLocateDateUtils
import com.tmobile.mytmobile.echolocate.utils.EchoLocateLog
import java.io.File
import java.util.*

class CopyAppDataActivity : AppCompatActivity() {

    private val debugFolderName: String = "dia_debug"
    private val MY_REQUEST_CODE_FOR_EXT_WRITE_PERM = 100
    private var mPermGrant = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_copy_data)
    }

    override fun onResume() {
        super.onResume()
        val permissionState = this.checkSelfPermission(
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        mPermGrant = permissionState == PackageManager.PERMISSION_GRANTED
        if (!mPermGrant) {
            this.requestPermissions(arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), MY_REQUEST_CODE_FOR_EXT_WRITE_PERM)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            MY_REQUEST_CODE_FOR_EXT_WRITE_PERM -> {
                mPermGrant = (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                if (!mPermGrant) {
                    Toast.makeText(this, "Storage permission is needed", Toast.LENGTH_LONG).show()
                    finish()
                }
                return
            }
        }
    }

    private fun createFolderName() : String {
        if (ELDeviceUtils.isRDeviceOrHigher()) {
            return EchoLocateApplication.getContext()?.externalCacheDir?.absolutePath + "/" + debugFolderName + "/" + EchoLocateDateUtils.convertToFileNameFormat(Date())
        } else {
            return Environment.getExternalStorageDirectory().absolutePath + "/" + debugFolderName + "/" + EchoLocateDateUtils.convertToFileNameFormat(Date())
        }
    }

    fun onCopyAllDataClicked(@Suppress("UNUSED_PARAMETER")view: View) {
        if (!mPermGrant) {
            Toast.makeText(this, "Storage permission is needed", Toast.LENGTH_LONG).show()
            return
        }

        val appInternalDir = applicationContext.dataDir
        EchoLocateLog.eLogD("Data dir = $appInternalDir")
        try {
            appInternalDir.copyRecursively(File(createFolderName()), true)
        } catch (ex : Exception) {
            // do nothing
        }
    }

    fun onCopyDbFilesClicked(@Suppress("UNUSED_PARAMETER")view: View) {
        if (!mPermGrant) {
            Toast.makeText(this, "Storage permission is needed", Toast.LENGTH_LONG).show()
            return
        }

        val dbDir = File(applicationContext.dataDir.absolutePath + "/databases")
        try {
            dbDir.copyRecursively(File(createFolderName() + "/databases"), true)
        } catch (ex : Exception) {
            // do nothing
        }
    }
}
