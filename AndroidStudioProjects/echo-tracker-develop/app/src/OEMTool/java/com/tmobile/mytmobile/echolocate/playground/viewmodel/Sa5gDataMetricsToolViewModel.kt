package com.tmobile.mytmobile.echolocate.playground.viewmodel

import android.app.Activity
import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.tmobile.mytmobile.echolocate.R
import com.tmobile.mytmobile.echolocate.nr5g.core.oemdata.Sa5gDataMetricsWrapper
import com.tmobile.mytmobile.echolocate.utils.EchoLocateDateUtils
import com.tmobile.mytmobile.echolocate.utils.FileUtils
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import java.util.*

/**
 * View model class to handle report operations
 */
class Sa5gDataMetricsToolViewModel : ViewModel() {

    companion object {

        private const val GET_API_VERSION_METHOD = "getApiVersion"
        private const val GET_DOWNLINK_CARRIER_LOG_METHOD = "getDlCarrierLog"
        private const val GET_UPLINK_CARRIER_LOG_METHOD = "getUlCarrierLog"
        private const val GET_RRC_LOG_METHOD = "getRrcLog"
        private const val GET_NETWORK_LOG_METHOD = "getNetworkLog"
        const val GET_SETTING_LOG_METHOD = "getSettingsLog"
        private const val GET_UI_LOG_METHOD = "getUiLog"

        private const val TIMESTAMP = "timestamp"
        private const val NEW_LINE = "\n"
        private const val COLON = ":"
        private const val EMPTY = ""
        private const val ZERO = "0"
        private const val COMMA = ","

        const val FILE_NAME_DATAMETRICS_SA5G_DIA_LOGS = "log_sa5g_datametrics.txt"
    }

    suspend fun getAllClicked(context: Context?) {
        if (!context?.let { Sa5gDataMetricsWrapper(
                it
            ).isDataMetricsAvailable() }!!) {
            (context as Activity).runOnUiThread {
                Toast.makeText(context, R.string.data_metrics_unavailable, Toast.LENGTH_LONG)
                    .show()
            }
        } else {
            val log = prepareLog(context) + NEW_LINE + EMPTY
            FileUtils.saveFileToExternalStorage(
                log,
                FILE_NAME_DATAMETRICS_SA5G_DIA_LOGS,
                true
            )
            (context as Activity).runOnUiThread {
                Toast.makeText(context, R.string.data_metrics_data_generated, Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    /**
     * Method to prepare the desired log
     * @return The log data
     */
    private suspend fun prepareLog(context: Context): String {
        val builder = StringBuilder()
        val dataMetricsWrapper =
            Sa5gDataMetricsWrapper(
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
                .append(getApiVersion(dataMetricsWrapper))
                .append(getNetworkLog(dataMetricsWrapper))
                .append(getRrcLog(dataMetricsWrapper))
                .append(getUiLog(dataMetricsWrapper))
                .append(getDlCarrierLog(dataMetricsWrapper))
                .append(getUlCarrierLog(dataMetricsWrapper))
                .append(getSettingsLog(dataMetricsWrapper))
                .append(NEW_LINE)
        } else {
            builder.append("Data metrics unavailable")
        }
        return builder.toString()
    }

    /**
     * Method to get ApiVersion
     */
    suspend fun getApiVersion(wrapper: Sa5gDataMetricsWrapper): String {
        return GET_API_VERSION_METHOD + COLON + getApiReflect(GET_API_VERSION_METHOD, wrapper) +
                NEW_LINE
    }

    private suspend fun getApiReflect(param: String, sa5gDataMetricsWrapper: Sa5gDataMetricsWrapper) : Any? {
        var result : Any? = ""
        val job = GlobalScope.async {
            result = sa5gDataMetricsWrapper.invokeDataMetricsMethodReturnObject(param)
        }
        job.await()
        return result
    }

    /**
     * Method to get settings log
     */
    private suspend fun getSettingsLog(wrapper: Sa5gDataMetricsWrapper): String {
        val settingsLogObject =
            getApiReflect(GET_SETTING_LOG_METHOD, wrapper)
        val builder = java.lang.StringBuilder()
        val settingsLogString = Gson().toJson(settingsLogObject)
        builder.append(GET_SETTING_LOG_METHOD + COLON)
            .append(NEW_LINE)
            .append(settingsLogString)
            .append(NEW_LINE)
        return builder.toString()
    }

    /**
     * Method to get Ui log
     */
    private suspend fun getUiLog(wrapper: Sa5gDataMetricsWrapper): String {
        val settingsLogObject =
            getApiReflect(GET_UI_LOG_METHOD, wrapper)
        val builder = java.lang.StringBuilder()
        val settingsLogString = Gson().toJson(settingsLogObject)
        builder.append(GET_UI_LOG_METHOD + COLON)
            .append(NEW_LINE)
            .append(settingsLogString)
            .append(NEW_LINE)
        return builder.toString()
    }

    /**
     * Method to get Rrc Log
     */
    private suspend fun getRrcLog(wrapper: Sa5gDataMetricsWrapper): String {
        val settingsLogObject =
            getApiReflect(GET_RRC_LOG_METHOD, wrapper)
        val builder = java.lang.StringBuilder()
        val settingsLogString = Gson().toJson(settingsLogObject)
        builder.append(GET_RRC_LOG_METHOD + COLON)
            .append(NEW_LINE)
            .append(settingsLogString)
            .append(NEW_LINE)
        return builder.toString()
    }

    /**
     * Method to get network log
     */
    private suspend fun getNetworkLog(wrapper: Sa5gDataMetricsWrapper): String {
        val settingsLogObject =
            getApiReflect(GET_NETWORK_LOG_METHOD, wrapper)
        val builder = java.lang.StringBuilder()
        val settingsLogString = Gson().toJson(settingsLogObject)
        builder.append(GET_NETWORK_LOG_METHOD + COLON)
            .append(NEW_LINE)
            .append(settingsLogString)
            .append(NEW_LINE)
        return builder.toString()
    }

    /**
     * Method to get UpLink Carrier log
     */
    private fun getUlCarrierLog(sa5gDataMetricsWrapper: Sa5gDataMetricsWrapper): String {
        val ulCarrierLogList = sa5gDataMetricsWrapper.invokeDataMetricsMethodReturnObjectList(
            GET_UPLINK_CARRIER_LOG_METHOD
        ) as List<Any?>
        val builder = java.lang.StringBuilder()
        if (ulCarrierLogList.isEmpty()) {
            builder.append(GET_UPLINK_CARRIER_LOG_METHOD + COLON).append(NEW_LINE)
                .append(ZERO).append(NEW_LINE)
        } else {
            builder.append(GET_UPLINK_CARRIER_LOG_METHOD + COLON).append(NEW_LINE)
            val ulCarrierStringList: MutableList<String> = ArrayList()
            val ulCarrierBuilder = java.lang.StringBuilder()
            var i = 0
            val carrierLogSize = ulCarrierLogList.size
            while (i < carrierLogSize) {
                val ulCarrierLog: Any? = ulCarrierLogList[i]
                val ulCarrierLogString = Gson().toJson(ulCarrierLog)
                ulCarrierBuilder.append(ulCarrierLogString)
                if (i == carrierLogSize - 1) {
                    ulCarrierBuilder.append(NEW_LINE)
                } else {
                    ulCarrierBuilder.append(COMMA).append(NEW_LINE)
                }
                i++
            }
            ulCarrierStringList.add(ulCarrierBuilder.toString())
            builder.append(ulCarrierStringList.toString()).append(NEW_LINE)
        }
        return builder.toString()
    }

    /**
     * Method to get DownLink carrier log
     */
    private fun getDlCarrierLog(sa5gDataMetricsWrapper: Sa5gDataMetricsWrapper): String {
        val builder = java.lang.StringBuilder()

        val dlCarrierLogList = sa5gDataMetricsWrapper.invokeDataMetricsMethodReturnObjectList(
            GET_DOWNLINK_CARRIER_LOG_METHOD
        ) as List<Any?>
        if (dlCarrierLogList.isEmpty()) {
            builder.append(GET_DOWNLINK_CARRIER_LOG_METHOD + COLON).append(NEW_LINE)
                .append(ZERO).append(NEW_LINE)
        } else {
            builder.append(GET_DOWNLINK_CARRIER_LOG_METHOD + COLON).append(NEW_LINE)
            val dlCarrierStringList: MutableList<String> = ArrayList()
            val dlCarrierBuilder = java.lang.StringBuilder()
            var i = 0
            val carrierLogSize = dlCarrierLogList.size
            while (i < carrierLogSize) {
                val dlCarrierLog: Any? = dlCarrierLogList[i]
                val dlCarrierLogString = Gson().toJson(dlCarrierLog)
                dlCarrierBuilder.append(dlCarrierLogString)
                if (i == carrierLogSize - 1) {
                    dlCarrierBuilder.append(NEW_LINE)
                } else {
                    dlCarrierBuilder.append(COMMA).append(NEW_LINE)
                }
                i++
            }
            dlCarrierStringList.add(dlCarrierBuilder.toString())
            builder.append(dlCarrierStringList.toString()).append(NEW_LINE)
        }
        return builder.toString()
    }

}