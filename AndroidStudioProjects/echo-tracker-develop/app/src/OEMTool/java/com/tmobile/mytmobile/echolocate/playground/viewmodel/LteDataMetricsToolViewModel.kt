package com.tmobile.mytmobile.echolocate.playground.viewmodel

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.tmobile.mytmobile.echolocate.R
import com.tmobile.mytmobile.echolocate.lte.oemdata.LteDataMetricsWrapper
import com.tmobile.mytmobile.echolocate.utils.EchoLocateDateUtils
import com.tmobile.mytmobile.echolocate.utils.FileUtils

/**
 * View model class to handle report operations
 */
class LteDataMetricsToolViewModel : ViewModel() {


    companion object {
        private const val GET_API_VERSION_METHOD = "getApiVersion"
        private const val GET_DOWN_LINK_RF_CONFIG_METHOD = "getDownLinkRFConfig"
        private const val GET_UP_LINK_RF_CONFIG_METHOD = "getUpLinkRFConfig"
        private const val GET_BEARER_CONFIG_METHOD = "getBearerConfig"
        private const val GET_DATA_SETTING_METHOD = "getDataSetting"
        private const val GET_NETWORK_IDENTITY_METHOD = "getNetworkIdentity"
        private const val GET_SIGNAL_CONDITION_METHOD = "getSignalCondition"
        private const val GET_COMMON_RF_CONFIG = "getCommonRFConfig"
        private const val GET_DOWN_LINK_CARRIER_INFO ="getDownLinkCarrierInfo"
        private const val GET_UP_LINK_CARRIER_INFO = "getUpLinkCarrierInfo"
        

        private const val TIMESTAMP = "timestamp"
        private const val NEW_LINE = "\n"
        private const val COLON = ":"
        private const val EMPTY = ""

        const val LTE_DATAMETRICS_LOG_FILE = "log_lte_datametrics.txt"
    }

    private lateinit var mLteDataMetricsWrapper: LteDataMetricsWrapper

    fun getAllClicked(context: Context?, lteDataMetricsWrapper: LteDataMetricsWrapper) {
        mLteDataMetricsWrapper = lteDataMetricsWrapper
        if (!mLteDataMetricsWrapper.isDataMetricsAvailable()) {
            Toast.makeText(context, R.string.data_metrics_unavailable, Toast.LENGTH_LONG)
                .show()
        } else {
            val log = context?.let { prepareLog(it) } + NEW_LINE + EMPTY
            FileUtils.saveFileToExternalStorage(
                log,
                LTE_DATAMETRICS_LOG_FILE,
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
                .append(getDownLinkRFConfig())
                .append(getUpLinkRFConfig())
                .append(getBearerConfig())
                .append(getDataSetting())
                .append(getNetworkIdentity())
                .append(getSignalCondition())
                .append(getCommonRFConfig())
                .append(getDownLinkCarrierInfo())
                .append(getUpLinkCarrierInfo())
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
                mLteDataMetricsWrapper.getApiVersion().stringCode +
                NEW_LINE
    }

    private fun getDataInJsonFormat(list: List<String>, methodName: String): String {
        val builder = StringBuilder()
        val data = Gson().toJson(list)
        builder.append(methodName + COLON)
            .append(NEW_LINE)
            .append(data)
            .append(NEW_LINE)
        return builder.toString()
    }

    /**
     * Method to getDownLinkRFConfig
     */
    private fun getDownLinkRFConfig(): String {
        val downLinkRFConfig =
            mLteDataMetricsWrapper.getDownlinkRFConfiguration()
        return getDataInJsonFormat(downLinkRFConfig, GET_DOWN_LINK_RF_CONFIG_METHOD)
    }

    /**
     * Method to getUpLinkRFConfig
     */
    private fun getUpLinkRFConfig(): String {
        val upLinkRFConfig =
            mLteDataMetricsWrapper.getUplinkRFConfiguration()
        return getDataInJsonFormat(upLinkRFConfig, GET_UP_LINK_RF_CONFIG_METHOD)
    }


    /**
     * Method to getBearerConfig
     */
    private fun getBearerConfig(): String {
        val bearerConfig =
            mLteDataMetricsWrapper.getBearerConfiguration()
        return getDataInJsonFormat(bearerConfig, GET_BEARER_CONFIG_METHOD)
    }

    /**
     * Method to getDataSetting
     */
    private fun getDataSetting(): String {
        val dataSetting =
            mLteDataMetricsWrapper.getDataSetting()
        return getDataInJsonFormat(dataSetting, GET_DATA_SETTING_METHOD)
    }

    /**
     * Method to getNetworkIdentity
     */
    private fun getNetworkIdentity(): String {
        val list =
            mLteDataMetricsWrapper.getNetworkIdentity()
        return getDataInJsonFormat(list, GET_NETWORK_IDENTITY_METHOD)
    }

    /**
     * Method to getNetworkIdentity
     */
    private fun getSignalCondition(): String {
        val list =
            mLteDataMetricsWrapper.getSignalCondition()
        return getDataInJsonFormat(list, GET_SIGNAL_CONDITION_METHOD)
    }

    /**
     * Method to getNetworkIdentity
     */
    private fun getCommonRFConfig(): String {
        val list =
            mLteDataMetricsWrapper.getCommonRFConfiguration()
        return getDataInJsonFormat(list, GET_COMMON_RF_CONFIG)
    }

    /**
     * Method to getNetworkIdentity
     */
    private fun getDownLinkCarrierInfo(): String {
        val list =
            mLteDataMetricsWrapper.getDownlinkCarrierInfo()
        return getDataInJsonFormat(list, GET_DOWN_LINK_CARRIER_INFO)
    }

    /**
     * Method to getNetworkIdentity
     */
    private fun getUpLinkCarrierInfo(): String {
        val list =
            mLteDataMetricsWrapper.getUplinkCarrierInfo()
        return getDataInJsonFormat(list, GET_UP_LINK_CARRIER_INFO)
    }


}