package com.tmobile.mytmobile.echolocate.playground.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.google.gson.Gson
import com.tmobile.mytmobile.echolocate.R
import com.tmobile.mytmobile.echolocate.lte.oemdata.LteBaseDataMetricsWrapper
import com.tmobile.mytmobile.echolocate.lte.oemdata.LteDataMetricsWrapper
import com.tmobile.mytmobile.echolocate.nr5g.core.oemdata.Nr5gBaseDataMetricsWrapper
import com.tmobile.mytmobile.echolocate.nr5g.core.oemdata.Nr5gDataMetricsWrapper
import com.tmobile.mytmobile.echolocate.playground.viewmodel.LteDataMetricsToolViewModel
import com.tmobile.mytmobile.echolocate.playground.viewmodel.NSA5GDataMetricsToolViewModel
import com.tmobile.mytmobile.echolocate.utils.EchoLocateLog
import com.tmobile.mytmobile.echolocate.utils.FileUtils


@Suppress("UNUSED_PARAMETER")
@SuppressLint("SetTextI18n")
class NSA5GDataMetricsActivity : AppCompatActivity() {

    companion object {
        private const val TEST_PARAM = "TEST_PARAM"
        private const val GENERATE_LTE_DATAMETRICS_REPORT = "NSA5G_REPORT"
        private const val REQ_CODE_WRITE_EXTERNAL = 101
    }

    private lateinit var nsa5gDataMetricsWrapper: Nr5gDataMetricsWrapper

    private var isPermissionGranted = false
    private lateinit var etLogView: EditText

    private val viewModel by lazy {
        ViewModelProviders.of(this).get(NSA5GDataMetricsToolViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_poc_tools_nsa5g_datametrics)
        nsa5gDataMetricsWrapper =
            Nr5gDataMetricsWrapper(
                applicationContext
            )
        EchoLocateLog.eLogD("OEMToolSa5gDataMetricsActivity create called")
        checkPermission()
        checkAdbCommand()
        etLogView = findViewById(R.id.et_logs)
        val path:TextView = findViewById(R.id.tv_file_path)
        path.text = "${externalCacheDir?.absolutePath}/${FileUtils.debugFolderName}/${NSA5GDataMetricsToolViewModel.NSA5G_DATAMETRICS_LOG_FILE}"
    }

    /**
     * This method is used for check permission
     */
    private fun checkPermission() {
        val permissionState = this.checkSelfPermission(
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        isPermissionGranted = permissionState == PackageManager.PERMISSION_GRANTED
        if (!isPermissionGranted) {
            this.requestPermissions(
                arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                REQ_CODE_WRITE_EXTERNAL
            )
        }
    }

    /**
     * This method is used for onRequestPermissionsResult
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        when (requestCode) {
            REQ_CODE_WRITE_EXTERNAL -> {
                isPermissionGranted =
                    (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                if (!isPermissionGranted) {
                    Toast.makeText(this, "Storage permission is needed", Toast.LENGTH_LONG).show()
                    finish()
                }
                return
            }
        }
    }

    /**
     * This fun will run if run adb command
     */
    private fun checkAdbCommand() {
        val intent: Intent = intent
        if (intent.hasExtra(TEST_PARAM)) {
            val param = intent.getStringExtra(TEST_PARAM)
            EchoLocateLog.eLogD("-- Executing for param for adb command:")
            if (param.equals(GENERATE_LTE_DATAMETRICS_REPORT, ignoreCase = true)) {
                viewModel.getAllClicked(this, nsa5gDataMetricsWrapper)
            }
        } else {
            EchoLocateLog.eLogD("-- Not Executing for param for adb command:")
        }
    }

    fun getAllLog(view: View) {
        if (!isPermissionGranted) {
            Toast.makeText(this, "Storage permission is needed", Toast.LENGTH_LONG).show()
            return
        }
        viewModel.getAllClicked(this, nsa5gDataMetricsWrapper)
        Toast.makeText(this, "Generating..", Toast.LENGTH_LONG).show()
    }

    fun getNsa5gNetworkIdentity(view: View) {
        showDataInTextView(nsa5gDataMetricsWrapper.getNetworkIdentity())
    }

    fun get5gNrMmwCellLog(view: View) {
        showDataInTextView(nsa5gDataMetricsWrapper.get5gNrMmwCellLog())
    }

    fun get5gUiLog(view: View) {
        showDataInTextView(nsa5gDataMetricsWrapper.getNr5gUiLog())
    }

    fun getEndcLteLog(view: View) {
        showDataInTextView(nsa5gDataMetricsWrapper.getEndcLteLog())
    }

    fun getEndcUplinkLog(view: View) {
        showDataInTextView(nsa5gDataMetricsWrapper.getEndcUplinkLog())
    }

    private fun showDataInTextView(obj: Any?) {
        if (obj != null) {
            etLogView.setText(Gson().toJson(obj))
        } else {
            Toast.makeText(this, R.string.data_metrics_unavailable, Toast.LENGTH_LONG)
                .show()
        }
    }

    fun getNsa5gApiVersion(view: View) {
        val apiVersion = nsa5gDataMetricsWrapper.getApiVersion()
        EchoLocateLog.eLogD("OEMTool showApiVersion ${apiVersion.name}")
        if (apiVersion != Nr5gBaseDataMetricsWrapper.ApiVersion.UNKNOWN_VERSION) {
            etLogView.setText(apiVersion.stringCode)
        } else {
            Toast.makeText(view.context, R.string.data_metrics_unavailable, Toast.LENGTH_LONG)
                .show()
        }
    }


}