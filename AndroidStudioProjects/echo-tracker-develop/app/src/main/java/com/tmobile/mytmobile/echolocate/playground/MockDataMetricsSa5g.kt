package com.tmobile.mytmobile.echolocate.playground

import android.content.Context
import com.tmobile.mytmobile.echolocate.nr5g.sa5g.model.*


/**
 * This test DataMetrics file used for validate reflection implementation.
 */
class MockDataMetricsSa5g(val context: Context) {

    private val techType = "NO_SIGNAL"
    private val bandNumber = "n71"
    private val arfcn = "1"
    private val bandWidth = "20f"
    private val isPrimary = "2"
    private val isEndcAnchor = "2"
    private val modulationType = "256QAM"
    private val transmissionMode = "9"
    private val numberLayers = "8"
    private val cellId = "20508685"
    private val pci = "123"
    private val tac = "11316"
    private val lac = "123"
    private val rsrp = "2147483647f"
    private val rsrq = "2147483647f"
    private val rssi = "2147483647f"
    private val rscp = "2147483647f"
    private val sinr = "2147483647f"
    private val csiRsrp = "109f"
    private val csiRsrq = "14f"
    private val csiRssi = "109f"
    private val csiSinr = "14f"

    private val mcc = "310"
    private val mnc = "260"
    private val endcCapability = "-999"
    private val endcConnectionStatus = "-999"

    private val lteRrcState = "CONNECTED"
    private val nrRrcState = "INACTIVE"

    private val wifiCalling = "3"
    private val wifi = "1"
    private val roaming = "2"
    private val rtt = "4"
    private val rttTranscript = "2"
    private val networkMode = "6"

    private val networkType = "3"
    private val UiNetworkType = "3"
    private val UiDataTransmission = "LTE"
    private val UiNumberOfAntennas = "2"

    //carrier config
    private val carrierConfigVersion = "1"
    private val KEY_SAn2Enabled = "SAn2Enabled"
    private val KEY_SAn66Enabled = "SAn66Enabled"
    private val KEY_SAn71Enabled = "SAn71Enabled"
    private val KEY_NONE = "NONE"
    private val KEY_ERROR = "ERROR"
    private val VALUE_TRUE = "true"
    private val VALUE_FALSE = "false"
    private val VALUE_NONE = "-1"
    private val VALUE_ERROR = "-2"
    private var ctx : Context

    init {
        ctx = context
    }
    
    fun getDlCarrierLog(): List<Sa5gDownlinkCarrierLogs>? {
        val dlCarrierLog = Sa5gDownlinkCarrierLogs(
            techType,
            bandNumber,
            arfcn,
            bandWidth,
            isPrimary,
            isEndcAnchor,
            modulationType,
            transmissionMode,
            numberLayers,
            cellId,
            pci,
            tac,
            lac,
            rsrp,
            rsrq,
            rssi,
            rscp,
            sinr,
            csiRsrp,
            csiRsrq,
            csiRssi,
            csiSinr
        )
        val sa5gDownlinkCarrierLogsList: MutableList<Sa5gDownlinkCarrierLogs> =
            ArrayList<Sa5gDownlinkCarrierLogs>()
        sa5gDownlinkCarrierLogsList.add(dlCarrierLog)
        return sa5gDownlinkCarrierLogsList
    }

    fun getUlCarrierLog(): List<Sa5gUplinkCarrierLogs>? {
        val ulCarrierLog =
            Sa5gUplinkCarrierLogs(techType, bandNumber, arfcn, bandWidth, isPrimary)
        val sa5gUplinkCarrierLogsList: MutableList<Sa5gUplinkCarrierLogs> = ArrayList()
        sa5gUplinkCarrierLogsList.add(ulCarrierLog)
        return sa5gUplinkCarrierLogsList
    }

    fun getRrcLog(): Sa5gRrcLog? {
        return Sa5gRrcLog(lteRrcState, nrRrcState)
    }

    fun getNetworkLog(): Sa5gNetworkLog? {
        return Sa5gNetworkLog(mcc, mnc, endcCapability, endcConnectionStatus)
    }

    fun getSettingsLog(): Sa5gSettingsLog? {
        return Sa5gSettingsLog(wifiCalling, wifi, roaming, rtt, rttTranscript, networkMode,carrierConfigVersion,carrierSa5gBandConfig())
    }

    fun getUiLog(): Sa5gUiLog? {
        return Sa5gUiLog(
            System.currentTimeMillis().toString(),
            networkType,
            UiNetworkType,
            UiDataTransmission,
            UiNumberOfAntennas
        )
    }

    fun getApiVersion(): Int {
        return 1
    }


    fun carrierConfig(): Sa5gCarrierConfig? {
        val map = carrierSa5gBandConfig()
        val keyList: List<String> = java.util.ArrayList(map.keys)
        val valueList = java.util.ArrayList<String>()
        for (key in keyList) {
            valueList.add(map[key]!!)
        }

        val bandConfig = ArrayList<Sa5gCarrierBandConfig>()
        //bandKeys and bandValues always be equal in size
        for ((index, keyValue) in keyList.withIndex()) {
            bandConfig.add(Sa5gCarrierBandConfig(keyValue, valueList[index]))
        }

        return Sa5gCarrierConfig(carrierConfigVersion(),bandConfig)
    }

    fun carrierConfigVersion(): String? {
        return carrierConfigVersion
    }

    private fun bandConfigKeys(): List<String> {
        val configKeys: MutableList<String> = java.util.ArrayList<String>()
        configKeys.add(KEY_SAn2Enabled)
        configKeys.add(KEY_SAn66Enabled)
        configKeys.add(KEY_SAn71Enabled)
        configKeys.add(KEY_ERROR)
        return configKeys
    }

    private fun bandConfigValues(): List<String> {
        val configValues: MutableList<String> = java.util.ArrayList<String>()
        configValues.add(VALUE_TRUE)
        configValues.add(VALUE_FALSE)
        configValues.add(VALUE_NONE)
        configValues.add(VALUE_ERROR)
        return configValues
    }

    fun carrierSa5gBandConfig(): Map<String, String> {
        val keys = bandConfigKeys()
        val values = bandConfigValues()
        val map: MutableMap<String, String> = java.util.LinkedHashMap<String, String>()
        for (i in keys.indices) {
            map[keys[i]] = values[i]
        }
        return map
    }
}
