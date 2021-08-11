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
import com.tmobile.mytmobile.echolocate.playground.viewmodel.LteDataMetricsToolViewModel
import com.tmobile.mytmobile.echolocate.utils.EchoLocateLog
import com.tmobile.mytmobile.echolocate.utils.FileUtils


@Suppress("UNUSED_PARAMETER")
@SuppressLint("SetTextI18n")
class LTEDataMetricsActivity : AppCompatActivity() {

    companion object {
        private const val TEST_PARAM = "TEST_PARAM"
        private const val GENERATE_LTE_DATAMETRICS_REPORT = "LTE_REPORT"
        private const val BRACKET_NEW_LINE = "}\n"
        private const val LENGTH_LIMIT = 30
        private const val REQ_CODE_WRITE_EXTERNAL = 101
    }

    private lateinit var lteDataMetricsWrapper: LteDataMetricsWrapper

    private var isPermissionGranted = false
    private lateinit var etLogView: EditText

    private val viewModel by lazy {
        ViewModelProviders.of(this).get(LteDataMetricsToolViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_poc_tools_lte_datametrics)
        lteDataMetricsWrapper =
            LteDataMetricsWrapper(
                applicationContext
            )
        EchoLocateLog.eLogD("OEMToolSa5gDataMetricsActivity create called")
        checkPermission()
        checkAdbCommand()
        etLogView = findViewById(R.id.et_logs)
        val path:TextView = findViewById(R.id.tv_file_path)
        path.text = "${externalCacheDir?.absolutePath}/${FileUtils.debugFolderName}/${LteDataMetricsToolViewModel.LTE_DATAMETRICS_LOG_FILE}"
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
                viewModel.getAllClicked(this, lteDataMetricsWrapper)
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
        viewModel.getAllClicked(this, lteDataMetricsWrapper)
        Toast.makeText(this, "Generating..", Toast.LENGTH_LONG).show()
    }

    fun getDownLinkRFConfig(view: View) {
        showDataInTextView(lteDataMetricsWrapper.getDownlinkRFConfiguration())
    }

    fun getUpLinkRFConfig(view: View) {
        showDataInTextView(lteDataMetricsWrapper.getUplinkRFConfiguration())
    }

    fun getBearerConfig(view: View) {
        showDataInTextView(lteDataMetricsWrapper.getBearerConfiguration())
    }

    fun getDataSetting(view: View) {
        showDataInTextView(lteDataMetricsWrapper.getDataSetting())
    }

    fun getNetworkIdentity(view: View) {
        showDataInTextView(lteDataMetricsWrapper.getNetworkIdentity())
    }

    fun getSignalCondition(view: View) {
        showDataInTextView(lteDataMetricsWrapper.getSignalCondition())
    }

    fun getCommonRFConfig(view: View) {
        showDataInTextView(lteDataMetricsWrapper.getCommonRFConfiguration())
    }

    fun getDownLinkCarrierInfo(view: View) {
        showDataInTextView(lteDataMetricsWrapper.getDownlinkCarrierInfo())
    }

    fun getUpLinkCarrierInfo(view: View) {
        showDataInTextView(lteDataMetricsWrapper.getUplinkCarrierInfo())
    }

    private fun showDataInTextView(list: List<String>?) {
        if (list != null && list.isNotEmpty()) {
            etLogView.setText(Gson().toJson(list))
        } else {
            Toast.makeText(this, R.string.data_metrics_unavailable, Toast.LENGTH_LONG)
                .show()
        }
    }

    fun getApiVersion(view: View) {
        val apiVersion = lteDataMetricsWrapper.getApiVersion()
        EchoLocateLog.eLogD("OEMTool showApiVersion ${apiVersion.name}")
        if (apiVersion != LteBaseDataMetricsWrapper.ApiVersion.UNKNOWN_VERSION) {
            etLogView.setText(apiVersion.stringCode)
        } else {
            Toast.makeText(view.context, R.string.data_metrics_unavailable, Toast.LENGTH_LONG)
                .show()
        }
    }


}