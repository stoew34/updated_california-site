package com.tmobile.mytmobile.echolocate.playground.activities

import android.annotation.SuppressLint
import android.content.Context
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
import com.tmobile.mytmobile.echolocate.nr5g.core.oemdata.Sa5gDataMetricsWrapper
import com.tmobile.mytmobile.echolocate.playground.viewmodel.Sa5gDataMetricsToolViewModel
import com.tmobile.mytmobile.echolocate.utils.EchoLocateLog
import com.tmobile.mytmobile.echolocate.utils.FileUtils
import kotlinx.android.synthetic.OEMTool.activity_poc_tools_sa5g_datametrics.*
import kotlinx.coroutines.*

@SuppressLint("SetTextI18n")
class OEMToolSa5gDataMetricsActivity : AppCompatActivity() {

    companion object {
        private const val TEST_PARAM = "TEST_PARAM"
        private const val GENERATE_5G_SA_DATAMETRICS_REPORT = "5G_SA_REPORT"
        private const val MY_REQUEST_CODE_FOR_EXT_WRITE_PERM = 101
    }

    private var sa5gDataMetricsWrapper: Sa5gDataMetricsWrapper? = null

    private var isPermissionGranted = false
    private lateinit var tvAdbCommandDev: TextView
    private lateinit var tvAdbCommandProxy: TextView
    private lateinit var etLogView: EditText

    private val viewModel by lazy {
        ViewModelProviders.of(this).get(Sa5gDataMetricsToolViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_poc_tools_sa5g_datametrics)
        sa5gDataMetricsWrapper =
            Sa5gDataMetricsWrapper(
                applicationContext
            )
        EchoLocateLog.eLogD("OEMToolSa5gDataMetricsActivity create called")
        checkPermission()
        CoroutineScope(Dispatchers.IO).launch {
            checkAdbCommand()
        }
        tvAdbCommandDev = findViewById(R.id.tv_adb_dev)
        tvAdbCommandProxy = findViewById(R.id.tv_adb_proxy)
        etLogView = findViewById(R.id.et_logs)
        btn_getSettingLog.setOnClickListener(clickListener)
        btn_lte_all_log.setOnClickListener(clickListener)
        btn_networkLog.setOnClickListener(clickListener)
        btn_getDLCarrierLog.setOnClickListener(clickListener)
        btn_ULCarrierLog.setOnClickListener(clickListener)
        btn_UiLog.setOnClickListener(clickListener)
        btn_rrcLog.setOnClickListener(clickListener)
        btn_getApiVersion.setOnClickListener(clickListener)

        val path:TextView = findViewById(R.id.tv_file_path)
        path.text = "${externalCacheDir?.absolutePath}/${FileUtils.debugFolderName}/${Sa5gDataMetricsToolViewModel.FILE_NAME_DATAMETRICS_SA5G_DIA_LOGS}"
    }

    private val clickListener = View.OnClickListener {
        val view = it
        CoroutineScope(Dispatchers.IO).launch {
            when(view.id) {
                R.id.btn_getSettingLog -> showSettingsLog()
                R.id.btn_lte_all_log -> generate5gLog(view)
                R.id.btn_networkLog -> showNetworkLog()
                R.id.btn_getDLCarrierLog -> showDlCarrierLog()
                R.id.btn_ULCarrierLog -> showUlCarrierLog()
                R.id.btn_UiLog -> showUiLog()
                R.id.btn_rrcLog -> showRrcLog()
                R.id.btn_getApiVersion -> showApiVersion()
            }
        }
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
                MY_REQUEST_CODE_FOR_EXT_WRITE_PERM
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
            MY_REQUEST_CODE_FOR_EXT_WRITE_PERM -> {
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
     * Listener that generate log
     * @param view:View view clicked
     */
    private suspend fun generate5gLog(view: View?) {
        if (view != null) {
            if (view.id == R.id.btn_lte_all_log)
                getClickedFun()
        }
    }

    /**
     * This fun will run if run adb command
     */
    private suspend fun checkAdbCommand() {
        val intent: Intent = intent
        if (intent.hasExtra(TEST_PARAM)) {
            val param = intent.getStringExtra(TEST_PARAM)
            EchoLocateLog.eLogD("-- Executing for param for adb command:")
            if (param.equals(GENERATE_5G_SA_DATAMETRICS_REPORT, ignoreCase = true)) {
                getClickedFun()
            }
        } else {
            EchoLocateLog.eLogD("-- Not Executing for param for adb command:")
        }
    }

    /**
     * This fun will run if run adb command
     */
    private suspend fun getClickedFun() {
        if (!isPermissionGranted) {
            showToastOnUiThread(this, "Storage permission is needed")
            return
        }
        viewModel.getAllClicked(this)
        showToastOnUiThread(this, "Generating..")
    }

    private suspend fun showApiVersion() {
        var apiVersion: Sa5gDataMetricsWrapper.ApiVersion? = null
        val value = GlobalScope.async {
            apiVersion = withContext(Dispatchers.Default) {
                sa5gDataMetricsWrapper?.getApiVersion()
            } as Sa5gDataMetricsWrapper.ApiVersion
        }
        value.await()
        EchoLocateLog.eLogD("OEMTool showApiVersion ${apiVersion?.name}")
        if (apiVersion != null && java.lang.String.valueOf(apiVersion).isNotEmpty()) {
            if (apiVersion != Sa5gDataMetricsWrapper.ApiVersion.UNKNOWN_VERSION) {
                setTextInUiThread(Gson().toJson(apiVersion!!.name))
            } else {
                showToastOnUiThread(this, "UNKNOWN_APIVERSION")
            }
        } else {
            showToastOnUiThread(this, getString(R.string.data_metrics_unavailable))
        }
    }

    private suspend fun showDlCarrierLog() {
        var dlCarrierLog: Any? = ""
        val value = GlobalScope.async {
            dlCarrierLog = withContext(Dispatchers.Default) {
                sa5gDataMetricsWrapper?.getDlCarrierLog()
            }
        }
        value.await()
        if (dlCarrierLog != null && java.lang.String.valueOf(dlCarrierLog).isNotEmpty()) {
            setTextInUiThread(Gson().toJson(dlCarrierLog))
        } else {
            showToastOnUiThread(this, getString(R.string.data_metrics_unavailable))
        }
    }

    private suspend fun showSettingsLog() {
        var settingsLog: Any? = ""
        val value = GlobalScope.async {
            settingsLog = withContext(Dispatchers.Default) {
                sa5gDataMetricsWrapper?.invokeDataMetricsMethodReturnObject(
                    Sa5gDataMetricsToolViewModel.GET_SETTING_LOG_METHOD
                )
            }
        }
        value.await()
        if (settingsLog != null && java.lang.String.valueOf(settingsLog).isNotEmpty()) {
            setTextInUiThread(Gson().toJson(settingsLog))
        } else {
            showToastOnUiThread(this, getString(R.string.data_metrics_unavailable))
        }
    }

    private suspend fun showNetworkLog() {
        var networkLog: Any? = ""
        val value = GlobalScope.async {
            networkLog = withContext(Dispatchers.Default) {
                sa5gDataMetricsWrapper?.getNetworkLog()
            }
        }
        value.await()
        if (networkLog != null && java.lang.String.valueOf(networkLog).isNotEmpty()) {
            setTextInUiThread(Gson().toJson(networkLog))
        } else {
            showToastOnUiThread(this, getString(R.string.data_metrics_unavailable))
        }
    }

    private suspend fun showUlCarrierLog() {
        var ulCarrierLog: Any? = ""
        val value = GlobalScope.async {
            ulCarrierLog = withContext(Dispatchers.Default) {
                sa5gDataMetricsWrapper?.getUlCarrierLog()
            }
        }
        value.await()
        if (ulCarrierLog != null && java.lang.String.valueOf(ulCarrierLog).isNotEmpty()) {
            setTextInUiThread(Gson().toJson(ulCarrierLog))
        } else {
            showToastOnUiThread(this, getString(R.string.data_metrics_unavailable))
        }
    }

    private suspend fun showUiLog() {
        var uiLog: Any? = ""
        val value = GlobalScope.async {
            uiLog = withContext(Dispatchers.Default) {
                sa5gDataMetricsWrapper?.getUiLog()
            }
        }
        value.await()
        if (uiLog != null && java.lang.String.valueOf(uiLog).isNotEmpty()) {
            setTextInUiThread(Gson().toJson(uiLog))
        } else {
            showToastOnUiThread(this, getString(R.string.data_metrics_unavailable))
        }
    }

    private suspend fun showRrcLog() {
        var rrcLog: Any? = ""
        val value = GlobalScope.async {
            rrcLog = withContext(Dispatchers.Default) {
                sa5gDataMetricsWrapper?.getRrcLog()
            }
        }
        value.await()
        if (rrcLog != null && java.lang.String.valueOf(rrcLog).isNotEmpty()) {
            setTextInUiThread(Gson().toJson(rrcLog))
        } else {
            showToastOnUiThread(this, getString(R.string.data_metrics_unavailable))
        }
    }

    private fun showToastOnUiThread(context: Context, message: String) {
        runOnUiThread {
            Toast.makeText(context, message, Toast.LENGTH_LONG)
                .show()
        }
    }

    private fun setTextInUiThread(data: String) {
        runOnUiThread {
            etLogView.setText(data)
        }
    }

}