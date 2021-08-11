package com.tmobile.mytmobile.echolocate.playground.viewmodel

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.tmobile.mytmobile.echolocate.R
import com.tmobile.mytmobile.echolocate.lte.oemdata.LteDataMetricsWrapper
import com.tmobile.mytmobile.echolocate.nr5g.core.oemdata.Nr5gDataMetricsWrapper
import com.tmobile.mytmobile.echolocate.utils.EchoLocateDateUtils
import com.tmobile.mytmobile.echolocate.utils.FileUtils

/**
 * View model class to handle report operations
 */
class NSA5GDataMetricsToolViewModel : ViewModel() {


    companion object {
        private const val GET_API_VERSION_METHOD = "getApiVersion"
        private const val GET_NETWORK_IDENTITY_METHOD = "Nsa5gNetworkIdentity"
        private const val GET_MMW_CELL_LOG_METHOD = "get5gNrMmwCellLog"
        private const val GET_5G_UI_LOG_METHOD = "get5gUiLog"
        private const val GET_ENDC_LTE_LOG_METHOD = "getEndcLteLog"
        private const val GET_ENDC_UPLINK_LOG_METHOD = "getEndcUplinkLog"

        

        private const val TIMESTAMP = "timestamp"
        private const val NEW_LINE = "\n"
        private const val COLON = ":"
        private const val EMPTY = ""

        const val NSA5G_DATAMETRICS_LOG_FILE = "log_nsa5g_datametrics.txt"
    }

    private lateinit var mNsa5gDataMetricsWrapper: Nr5gDataMetricsWrapper

    fun getAllClicked(context: Context?, nsa5gDataMetricsWrapper: Nr5gDataMetricsWrapper) {
        mNsa5gDataMetricsWrapper = nsa5gDataMetricsWrapper
        if (!mNsa5gDataMetricsWrapper.isDataMetricsAvailable()) {
            Toast.makeText(context, R.string.data_metrics_unavailable, Toast.LENGTH_LONG)
                .show()
        } else {
            val log = context?.let { prepareLog(it) } + NEW_LINE + EMPTY
            FileUtils.saveFileToExternalStorage(
                log,
                NSA5G_DATAMETRICS_LOG_FILE,
                true
            )
            Toast.makeText(context, R.string.data_metrics_data_generated, Toast.LENGTH_LONG)
                .show()
        }
    }

    /**
     * Method to prepare the desired log
     * @return The log data
     */
    private fun prepareLog(context: Context): String {
        val builder = StringBuilder()
        val dataMetricsWrapper =
            LteDataMetricsWrapper(
                context
            )
        if (dataMetricsWrapper.isDataMetricsAvailable()) {
            builder.append(NEW_LINE)
                .append(TIMESTAMP + COLON).append(
                    EchoLocateDateUtils.convertToShemaDateFormat(
                        System.currentTimeMillis().toString()
                    )
                )
                .append(NEW_LINE)
                .append(getApiVersion())
                .append(getNsa5gNetworkIdentity())
                .append(get5gNrMmwCellLog())
                .append(get5gUiLog())
                .append(getEndcLteLog())
                .append(getEndcUplinkLog())
                .append(NEW_LINE)
        } else {
            builder.append("Data metrics unavailable")
        }
        return builder.toString()
    }

    /**
     * Method to get ApiVersion
     */
    fun getApiVersion(): String {
        return GET_API_VERSION_METHOD + COLON +
                mNsa5gDataMetricsWrapper.getApiVersion().stringCode +
                NEW_LINE
    }

    private fun getDataInJsonFormat(obj: Any?, methodName: String): String {
        val builder = StringBuilder()
        val data = Gson().toJson(obj)
        builder.append(methodName + COLON)
            .append(NEW_LINE)
            .append(data)
            .append(NEW_LINE)
        return builder.toString()
    }

    /**
     * Method to getDownLinkRFConfig
     */
    private fun getNsa5gNetworkIdentity(): String {
        val downLinkRFConfig =
            mNsa5gDataMetricsWrapper.getNetworkIdentity()
        return getDataInJsonFormat(downLinkRFConfig, GET_NETWORK_IDENTITY_METHOD)
    }

    /**
     * Method to getUpLinkRFConfig
     */
    private fun get5gNrMmwCellLog(): String {
        val upLinkRFConfig =
            mNsa5gDataMetricsWrapper.get5gNrMmwCellLog()
        return getDataInJsonFormat(upLinkRFConfig, GET_MMW_CELL_LOG_METHOD)
    }


    /**
     * Method to getBearerConfig
     */
    private fun get5gUiLog(): String {
        val bearerConfig =
            mNsa5gDataMetricsWrapper.getNr5gUiLog()
        return getDataInJsonFormat(bearerConfig, GET_5G_UI_LOG_METHOD)
    }

    /**
     * Method to getDataSetting
     */
    private fun getEndcLteLog(): String {
        val dataSetting =
            mNsa5gDataMetricsWrapper.getEndcLteLog()
        return getDataInJsonFormat(dataSetting, GET_ENDC_LTE_LOG_METHOD)
    }

    /**
     * Method to getNetworkIdentity
     */
    private fun getEndcUplinkLog(): String {
        val list =
            mNsa5gDataMetricsWrapper.getEndcUplinkLog()
        return getDataInJsonFormat(list, GET_ENDC_UPLINK_LOG_METHOD)
    }

}