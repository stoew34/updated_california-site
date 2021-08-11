package com.tmobile.mytmobile.echolocate.lte.oemdata

/**
 * Created by Divya Mittal on 4/12/21
 */
import android.content.Context

/**
 * Class used to deliver LTE data metrics from Echo Locate custom API. It's collect data that is
 * not available from the current native Android SW.
 */

class LteDataMetricsWrapper constructor(context: Context) : LteBaseDataMetricsWrapper(context) {

    companion object {

        const val GET_SIGNAL_CONDITION_METHOD = "getSignalCondition"
        const val GET_COMMON_RF_CONFIGURATION_METHOD = "getCommonRFConfiguration"
        const val GET_DOWNLINK_CARRIER_INFO_METHOD = "getDownlinkCarrierInfo"
        const val GET_UPLINK_CARRIER_INFO_METHOD = "getUplinkCarrierInfo"
        const val GET_DOWNLINK_RF_CONFIGURATION_METHOD = "getDownlinkRFConfiguration"
        const val GET_UPLINK_RF_CONFIGURATION_METHOD = "getUplinkRFConfiguration"
        const val GET_BEARER_CONFIGURATION_METHOD = "getBearerConfiguration"
        const val GET_DATA_SETTING_METHOD = "getDataSetting"

    }

    /**
     * Get Signal Condition. Invoke {@value GET_SIGNAL_CONDITION_METHOD} from {@value
     * DATA_METRICS_CLASS}
     *
     * @return Signal condition as list of values. In order:
     * <ul>
     * <li> Timestamp
     * <li> Network type
     * <li> RSRP
     * <li> RSRQ
     * <li> SINR
     * <li> RSSI
     * <li> RACH Power <b>ADDED in v1</b>
     * <li> LTE UL headroom <b>ADDED in v1</b>
     * </ul>
     */
    fun getSignalCondition(): List<String> {
        return invokeDataMetricsMethodReturnStringList(GET_SIGNAL_CONDITION_METHOD)
    }

    /**
     * Get Common RF Configuration. Invoke {@value GET_COMMON_RF_CONFIGURATION_METHOD} from {@value
     * DATA_METRICS_CLASS}
     *
     * @return Common RF Configuration as list of values. In order:
     * <ul>
     * <li> Timestamp
     * <li> Network type
     * <li> Transmission mode
     * <li> Antenna configuration RX
     * <li> Antenna configuration TX
     * <li> Receiver diversity
     * <li> RRC state
     * <li> <s>PCC Scheduling rate</s> <b>REMOVED in v1</b>
     * <li> <s>feICIC</s> <b>REMOVED in v1</b>
     * <li> <s>CRS-IC</s> <b>REMOVED in v1</b>
     * <li> <s>APN name</s> <b>REMOVED in v1</b>
     * <li> LTE-U / LAA
     * </ul>
     */
    fun getCommonRFConfiguration(): List<String> {
        return invokeDataMetricsMethodReturnStringList(GET_COMMON_RF_CONFIGURATION_METHOD)
    }

    /**
     * Get Downlink Carrier Information. Invoke {@value GET_DOWNLINK_CARRIER_INFO_METHOD} from
     * {@value
     * DATA_METRICS_CLASS}
     *
     * @return Downlink Carrier Information as list of values. In order:
     * <ul>
     * <li> Timestamp
     * <li> Network type
     * <li> Number of aggregated channels
     * <li> Primary EARFCN
     * <li> Primary Bandwidth
     * <li> Primary Band number
     * <li> Second EARFCN
     * <li> Second Bandwidth
     * <li> Second Band number
     * <li> Third EARFCN
     * <li> Third Bandwidth
     * <li> Third Band number
     * </ul>
     */
    fun getDownlinkCarrierInfo(): List<String> {
        return invokeDataMetricsMethodReturnStringList(GET_DOWNLINK_CARRIER_INFO_METHOD)
    }

    /**
     * Get Uplink Carrier Information. Invoke {@value GET_UPLINK_CARRIER_INFO_METHOD} from {@value
     * DATA_METRICS_CLASS}
     *
     * @return Uplink Carrier Information as list of values. In order:
     * <ul>
     * <li> Timestamp
     * <li> Network type
     * <li> Number of aggregated channels
     * <li> Primary EARFCN
     * <li> Primary Bandwidth
     * <li> Primary Band number
     * <li> Second EARFCN
     * <li> Second Bandwidth
     * <li> Second Band number
     * </ul>
     */
    fun getUplinkCarrierInfo(): List<String> {
        return invokeDataMetricsMethodReturnStringList(GET_UPLINK_CARRIER_INFO_METHOD)
    }

    /**
     * Get Downlink RF Configuration. Invoke {@value GET_DOWNLINK_RF_CONFIGURATION_METHOD} from
     * {@value DATA_METRICS_CLASS}
     *
     * @return Downlink RF Configuration as list of values. In order:
     * <ul>
     * <li> Timestamp
     * <li> Network type
     * <li> <s>Primary Carrier - PRB</s> <b>REMOVED in v1</b>
     * <li> Primary Carrier - modulation scheme
     * <li> <s>Primary Carrier – TBS</s> <b>REMOVED in v1</b>
     * <li> <s>Primary Carrier - MCS</s> <b>REMOVED in v1</b>
     * <li> Primary Carrier - Number of Layers
     * <li> <s>Second Carrier - PRB</s> <b>REMOVED in v1</b>
     * <li> Second Carrier – modulation	scheme
     * <li> <s>Second Carrier – TBS</s> <b>REMOVED in v1</b>
     * <li> <s>Second Carrier - MCS</s> <b>REMOVED in v1</b>
     * <li> Second Carrier - Number of Layers
     * <li> <s>Third Carrier - PRB</s> <b>REMOVED in v1</b>
     * <li> Third Carrier – modulation	scheme
     * <li> <s>Third Carrier – TBS</s> <b>REMOVED in v1</b>
     * <li> <s>Third Carrier - MCS</s> <b>REMOVED in v1</b>
     * <li> Third Carrier - Number of Layers
     * </ul>
     */
    fun getDownlinkRFConfiguration(): List<String> {
        return invokeDataMetricsMethodReturnStringList(GET_DOWNLINK_RF_CONFIGURATION_METHOD)
    }

    /**
     * Get Uplink RF Configuration. Invoke {@value GET_UPLINK_RF_CONFIGURATION_METHOD} from
     * {@value DATA_METRICS_CLASS}
     *
     * @return Uplink RF Configuration as list of values. In order:
     * <ul>
     * <li> Timestamp
     * <li> Network type
     * <li> Primary Carrier - modulation scheme
     * <li> <s>Primary Carrier – TBS</s> <b>REMOVED in v1</b>
     * <li> <s>Primary Carrier - MCS</s> <b>REMOVED in v1</b>
     * <li> <s>Primary Carrier - RI</s> <b>REMOVED in v1</b>
     * <li> <s>Primary Carrier - CQI</s> <b>REMOVED in v1</b>
     * <li> <s>Primary Carrier - PMI</s> <b>REMOVED in v1</b>
     * <li> Second Carrier - modulation scheme
     * <li> <s>Second Carrier – TBS</s> <b>REMOVED in v1</b>
     * <li> <s>Second Carrier - MCS</s> <b>REMOVED in v1</b>
     * <li> <s>Second Carrier - RI</s> <b>REMOVED in v1</b>
     * <li> <s>Second Carrier - CQI</s> <b>REMOVED in v1</b>
     * <li> <s>Second Carrier - PMI</s> <b>REMOVED in v1</b>
     * </ul>
     */
    fun getUplinkRFConfiguration(): List<String> {
        return invokeDataMetricsMethodReturnStringList(GET_UPLINK_RF_CONFIGURATION_METHOD)
    }

    /**
     * ADDED IN API v1. On First version of
     * <p>
     * Get Bearer Configuration. Invoke {@value GET_BEARER_CONFIGURATION_METHOD} from {@value
     * DATA_METRICS_CLASS}
     *
     * @return Bearer Configuration as list of values. In order:
     * * <ul>
     * <li> Timestamp
     * <li> Network type
     * <li> Number of active bearers
     * <li> Bearer 1 – Type of QCI
     * <li> Bearer 1 - APN name
     * <li> Bearer 2 – Type of QCI
     * <li> Bearer 2 - APN name
     * <li> Bearer 3 – Type of QCI
     * <li> Bearer 3 - APN name
     * <li> Bearer 4 – Type of QCI
     * <li> Bearer 4 - APN name
     * </ul>
     * If is not supported then return empty list
     */
    fun getBearerConfiguration(): List<String> {
        return invokeDataMetricsMethodReturnStringList(GET_BEARER_CONFIGURATION_METHOD)
    }

    /**
     * Get Data Settings. Invoke {@value GET_DATA_SETTING_METHOD} from {@value
     * DATA_METRICS_CLASS}
     *
     * @return Data settings as list of values. In order:
     * <ul>
     * <li> Timestamp
     * <li> MOBILE_DATA_SETTING
     * <li> NETWORK_MODE_SETTING
     * <li> WIFI_SETTING
     * <li> WIFI_CALLING_SETTING
     * <li> DATA_ROAMING_SETTING
     * <li> VOLTE_SETTING
     * </ul>
     */
    fun getDataSetting(): List<String> {
        return invokeDataMetricsMethodReturnStringList(GET_DATA_SETTING_METHOD)
    }
}