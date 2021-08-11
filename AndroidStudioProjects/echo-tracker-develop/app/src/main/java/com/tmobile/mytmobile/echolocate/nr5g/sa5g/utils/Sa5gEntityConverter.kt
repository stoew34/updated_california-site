package com.tmobile.mytmobile.echolocate.nr5g.sa5g.utils

import android.net.wifi.WifiManager
import com.tmobile.mytmobile.echolocate.nr5g.core.utils.Nr5gReflectionUtil
import com.tmobile.mytmobile.echolocate.nr5g.sa5g.database.entity.*
import com.tmobile.mytmobile.echolocate.nr5g.sa5g.model.*
import com.tmobile.mytmobile.echolocate.standarddatablocks.DeviceInfo
import com.tmobile.mytmobile.echolocate.standarddatablocks.OEMSV
import com.tmobile.mytmobile.echolocate.utils.EchoLocateDateUtils
import com.tmobile.mytmobile.echolocate.utils.EchoLocateLog
import java.util.*

class Sa5gEntityConverter {
    companion object {

        /** Method names from class : com.tmobile.diagnostics.echolocate.sa5g.data.datametrics.DlCarrierLog
         *   String techType;
         *   String bandNumber;
         *   int arfcn;
         *   float bandWidth;
         *   int isPrimary;
         *   int isEndcAnchor;
         *   String modulationType;
         *   int transmissionMode;
         *   int numberLayers;
         *   int cellId;
         *   int pci;
         *   int tac;
         *   int lac;
         *   float rsrp;
         *   float rsrq;
         *   float rssi;
         *   float rscp;
         *   float sinr;
         *   float csiRsrp;
         *   float csiRsrq;
         *   float csiRssi;
         *   float csiSinr;
         */
        private const val DOWNLINK_CARRIER_LOGS_GET_TECHTYPE_METHOD = "getTechType"
        private const val DOWNLINK_CARRIER_LOGS_GET_BANDNUMBER_METHOD = "getBandNumber"
        private const val DOWNLINK_CARRIER_LOGS_GET_ARFCN_METHOD = "getArfcn"
        private const val DOWNLINK_CARRIER_LOGS_GET_BANDWIDTH_METHOD = "getBandWidth"
        private const val DOWNLINK_CARRIER_LOGS_GET_ISPRIMARY_METHOD = "getIsPrimary"
        private const val DOWNLINK_CARRIER_LOGS_GET_ISENDCANCHOR_METHOD = "getIsEndcAnchor"
        private const val DOWNLINK_CARRIER_LOGS_GET_MODULATIONTYPE_METHOD = "getModulationType"
        private const val DOWNLINK_CARRIER_LOGS_GET_TRANSMISSIONMODE_METHOD = "getTransmissionMode"
        private const val DOWNLINK_CARRIER_LOGS_GET_NUMBERLAYERS_METHOD = "getNumberLayers"
        private const val DOWNLINK_CARRIER_LOGS_GET_CELLID_METHOD = "getCellId"
        private const val DOWNLINK_CARRIER_LOGS_GET_PCI_METHOD = "getPci"
        private const val DOWNLINK_CARRIER_LOGS_GET_TAC_METHOD = "getTac"
        private const val DOWNLINK_CARRIER_LOGS_GET_LAC_METHOD = "getLac"
        private const val DOWNLINK_CARRIER_LOGS_GET_RSRP_METHOD = "getRsrp"
        private const val DOWNLINK_CARRIER_LOGS_GET_RSRQ_METHOD = "getRsrq"
        private const val DOWNLINK_CARRIER_LOGS_GET_RSSI_METHOD = "getRssi"
        private const val DOWNLINK_CARRIER_LOGS_GET_RSCP_METHOD = "getRscp"
        private const val DOWNLINK_CARRIER_LOGS_GET_SINR_METHOD = "getSinr"
        private const val DOWNLINK_CARRIER_LOGS_GET_CSIRSRP_METHOD = "getCsiRsrp"
        private const val DOWNLINK_CARRIER_LOGS_GET_CSIRSRQ_METHOD = "getCsiRsrq"
        private const val DOWNLINK_CARRIER_LOGS_GET_CSIRSSI_METHOD = "getCsiRssi"
        private const val DOWNLINK_CARRIER_LOGS_GET_CSISINR_METHOD = "getCsiSinr"

        /** Method names from class : com.tmobile.diagnostics.echolocate.sa5g.data.datametrics.NetworkLog
         * String mcc
         * String mnc
         * int endcCapability
         * int endcConnectionStatus
         */
        private const val NETWORKLOG_GET_MCC_METHOD = "getMcc"
        private const val NETWORKLOG_GET_MNC_METHOD = "getMnc"
        private const val NETWORKLOG_GET_ENDCCAPABILITY_METHOD = "getEndcCapability"
        private const val NETWORKLOG_GET_ENDCCONNECTIONSTATUS_METHOD = "getEndcConnectionStatus"

        /** Method names from class : com.tmobile.diagnostics.echolocate.sa5g.data.datametrics.UlCarrierLog
         * String techType;
         * String bandNumber;
         * int arfcn;
         * float bandWidth;
         * int isPrimary;
         */
        private const val ULCARRIERLOG_GET_TECH_TYPE_METHOD = "getTechType"
        private const val ULCARRIERLOG_GET_BAND_NUMBER_METHOD = "getBandNumber"
        private const val ULCARRIERLOG_GET_ARFCN_METHOD = "getArfcn"
        private const val ULCARRIERLOG_GET_BAND_WIDTH_METHOD = "getBandWidth"
        private const val ULCARRIERLOG_GET_IS_PRIMARY_METHOD = "getIsPrimary"

        /** Method names from class : com.tmobile.diagnostics.echolocate.sa5g.data.datametrics.RrcLog
         * String lteRrcState
         * String nrRrcState
         */
        private const val RRCLOG_GET_LTE_RRC_STATE_METHOD = "getLteRrcState"
        private const val RRCLOG_GET_NR_RRC_STATE_METHOD = "getNrRrcState"

        /** Method names from class : com.tmobile.diagnostics.echolocate.sa5g.data.datametrics.SettingsLog
         * String wifiCalling;
         * String wifi;
         * String roaming;
         * String rtt;
         * String rttTranscript;
         * String networkMode;
         */
        private const val SETTINGSLOG_GET_WIFI_CALLING_METHOD = "getWifiCalling"
        private const val SETTINGSLOG_GET_WIFI_METHOD = "getWifi"
        private const val SETTINGSLOG_GET_ROAMING_METHOD = "getRoaming"
        private const val SETTINGSLOG_GET_RTT_METHOD = "getRtt"
        private const val SETTINGSLOG_GET_RTT_TRANSCRIPT_METHOD = "getRttTranscript"
        private const val SETTINGSLOG_GET_NETWORK_MODE_METHOD = "getNetworkMode"
        private const val GET_CARRIER_CONFIG_VERSION_METHOD = "getCarrierConfigVersion"
        private const val GET_CARRIER_BAND_CONFIG_METHOD = "getCarrierSa5gBandConfig"

        /** Method names from class : com.tmobile.diagnostics.echolocate.sa5g.data.datametrics.UiLog
         * long timestamp;
         * String networkType;
         * String uiNetworkType;
         * String uiDataTransmission;
         * int uiNumberOfAntennaBars;
         */
        private const val UI_LOG_GET_NETWORK_TYPE_METHOD = "getNetworkType"
        private const val UI_LOG_GET_TIMESTAMP_METHOD = "getTimestamp"
        private const val UI_LOG_GET_UI_NETWORK_TYPE_METHOD = "getUiNetworkType"
        private const val UI_LOG_GET_UI_DATA_TRANSMISSION_METHOD = "getUiDataTransmission"
        private const val UI_LOG_GET_UI_NUMBEROFANTENNABARS_METHOD = "getUiNumberOfAntennaBars"


        /**
         * Converts [downlinkCarrierLogs] to [Sa5gDownlinkCarrierLogsEntity]
         * Classname of Sa5gDownlinkCarrierLogs : com.tmobile.diagnostics.echolocate.sa5g.data.datametrics.DOWNLINK_CARRIER_LOGSs
         * */
        fun convertDownlinkCarrierLogsToEntity(downlinkCarrierLogs: Any?): Sa5gDownlinkCarrierLogsEntity? {
            if (downlinkCarrierLogs == null || !(downlinkCarrierLogs.javaClass.toString()
                    .contains("DlCarrierLog", false))
            ) {
                return null
            }

            val techType =
                Nr5gReflectionUtil.callMethod(
                    downlinkCarrierLogs.javaClass,
                    DOWNLINK_CARRIER_LOGS_GET_TECHTYPE_METHOD,
                    downlinkCarrierLogs
                )
            val bandNumber =
                Nr5gReflectionUtil.callMethod(
                    downlinkCarrierLogs.javaClass,
                    DOWNLINK_CARRIER_LOGS_GET_BANDNUMBER_METHOD,
                    downlinkCarrierLogs
                )
            val arfcn =
                Nr5gReflectionUtil.callMethod(
                    downlinkCarrierLogs.javaClass,
                    DOWNLINK_CARRIER_LOGS_GET_ARFCN_METHOD,
                    downlinkCarrierLogs
                )
            val bandWidth =
                Nr5gReflectionUtil.callMethod(
                    downlinkCarrierLogs.javaClass,
                    DOWNLINK_CARRIER_LOGS_GET_BANDWIDTH_METHOD,
                    downlinkCarrierLogs
                )
            val isPrimary =
                Nr5gReflectionUtil.callMethod(
                    downlinkCarrierLogs.javaClass,
                    DOWNLINK_CARRIER_LOGS_GET_ISPRIMARY_METHOD,
                    downlinkCarrierLogs
                )
            val isEndcAnchor =
                Nr5gReflectionUtil.callMethod(
                    downlinkCarrierLogs.javaClass,
                    DOWNLINK_CARRIER_LOGS_GET_ISENDCANCHOR_METHOD,
                    downlinkCarrierLogs
                )
            val modulationType =
                Nr5gReflectionUtil.callMethod(
                    downlinkCarrierLogs.javaClass,
                    DOWNLINK_CARRIER_LOGS_GET_MODULATIONTYPE_METHOD,
                    downlinkCarrierLogs
                )
            val transmissionMode =
                Nr5gReflectionUtil.callMethod(
                    downlinkCarrierLogs.javaClass,
                    DOWNLINK_CARRIER_LOGS_GET_TRANSMISSIONMODE_METHOD,
                    downlinkCarrierLogs
                )
            val numberLayers =
                Nr5gReflectionUtil.callMethod(
                    downlinkCarrierLogs.javaClass,
                    DOWNLINK_CARRIER_LOGS_GET_NUMBERLAYERS_METHOD,
                    downlinkCarrierLogs
                )
            val cellId =
                Nr5gReflectionUtil.callMethod(
                    downlinkCarrierLogs.javaClass,
                    DOWNLINK_CARRIER_LOGS_GET_CELLID_METHOD,
                    downlinkCarrierLogs
                )
            val pci =
                Nr5gReflectionUtil.callMethod(
                    downlinkCarrierLogs.javaClass,
                    DOWNLINK_CARRIER_LOGS_GET_PCI_METHOD,
                    downlinkCarrierLogs
                )
            val tac =
                Nr5gReflectionUtil.callMethod(
                    downlinkCarrierLogs.javaClass,
                    DOWNLINK_CARRIER_LOGS_GET_TAC_METHOD,
                    downlinkCarrierLogs
                )
            val lac =
                Nr5gReflectionUtil.callMethod(
                    downlinkCarrierLogs.javaClass,
                    DOWNLINK_CARRIER_LOGS_GET_LAC_METHOD,
                    downlinkCarrierLogs
                )
            val rsrp =
                Nr5gReflectionUtil.callMethod(
                    downlinkCarrierLogs.javaClass,
                    DOWNLINK_CARRIER_LOGS_GET_RSRP_METHOD,
                    downlinkCarrierLogs
                )
            val rsrq =
                Nr5gReflectionUtil.callMethod(
                    downlinkCarrierLogs.javaClass,
                    DOWNLINK_CARRIER_LOGS_GET_RSRQ_METHOD,
                    downlinkCarrierLogs
                )
            val rssi =
                Nr5gReflectionUtil.callMethod(
                    downlinkCarrierLogs.javaClass,
                    DOWNLINK_CARRIER_LOGS_GET_RSSI_METHOD,
                    downlinkCarrierLogs
                )
            val rscp =
                Nr5gReflectionUtil.callMethod(
                    downlinkCarrierLogs.javaClass,
                    DOWNLINK_CARRIER_LOGS_GET_RSCP_METHOD,
                    downlinkCarrierLogs
                )
            val sinr =
                Nr5gReflectionUtil.callMethod(
                    downlinkCarrierLogs.javaClass,
                    DOWNLINK_CARRIER_LOGS_GET_SINR_METHOD,
                    downlinkCarrierLogs
                )
            val csiRsrp =
                Nr5gReflectionUtil.callMethod(
                    downlinkCarrierLogs.javaClass,
                    DOWNLINK_CARRIER_LOGS_GET_CSIRSRP_METHOD,
                    downlinkCarrierLogs
                )
            val csiRsrq =
                Nr5gReflectionUtil.callMethod(
                    downlinkCarrierLogs.javaClass,
                    DOWNLINK_CARRIER_LOGS_GET_CSIRSRQ_METHOD,
                    downlinkCarrierLogs
                )
            val csiRssi =
                Nr5gReflectionUtil.callMethod(
                    downlinkCarrierLogs.javaClass,
                    DOWNLINK_CARRIER_LOGS_GET_CSIRSSI_METHOD,
                    downlinkCarrierLogs
                )
            val csiSinr =
                Nr5gReflectionUtil.callMethod(
                    downlinkCarrierLogs.javaClass,
                    DOWNLINK_CARRIER_LOGS_GET_CSISINR_METHOD,
                    downlinkCarrierLogs
                )

            return if (techType == null || bandNumber == null || arfcn == null || bandWidth == null ||
                isPrimary == null || isEndcAnchor == null || modulationType == null ||
                transmissionMode == null || numberLayers == null || cellId == null ||
                pci == null || tac == null || lac == null || rsrp == null || rsrq == null ||
                rssi == null || rscp == null || sinr == null || csiRsrp == null || csiRsrq == null ||
                csiRssi == null || csiSinr == null
            ) {
                null
            } else {
                Sa5gDownlinkCarrierLogsEntity(
                    techType, bandNumber, arfcn, bandWidth, isPrimary,
                    isEndcAnchor, modulationType, transmissionMode, numberLayers, cellId,
                    pci, tac, lac, rsrp, rsrq, rssi, rscp, sinr, csiRsrp, csiRsrq, csiRssi, csiSinr
                )
            }
        }

        /**
         * Converts [networkLog] to [Sa5gNetworkLogEntity]
         * Classname of Sa5gDownlinkCarrierLogs : com.tmobile.diagnostics.echolocate.sa5g.data.datametrics.NetworkLog
         * */
        fun convertNetworkLogToEntity(networkLog: Any?): Sa5gNetworkLogEntity? {
            if (networkLog == null || !(networkLog.javaClass.toString()
                    .contains("NetworkLog", false))
            ) {
                return null
            }
            val mcc =
                Nr5gReflectionUtil.callMethod(
                    networkLog.javaClass,
                    NETWORKLOG_GET_MCC_METHOD,
                    networkLog
                )
            val mnc =
                Nr5gReflectionUtil.callMethod(
                    networkLog.javaClass,
                    NETWORKLOG_GET_MNC_METHOD,
                    networkLog
                )
            val endcCapability =
                Nr5gReflectionUtil.callMethod(
                    networkLog.javaClass,
                    NETWORKLOG_GET_ENDCCAPABILITY_METHOD,
                    networkLog
                )
            val endcConnectionStatus =
                Nr5gReflectionUtil.callMethod(
                    networkLog.javaClass,
                    NETWORKLOG_GET_ENDCCONNECTIONSTATUS_METHOD,
                    networkLog
                )

            return if (mcc == null || mnc == null || endcCapability == null || endcConnectionStatus == null) {
                null
            } else {
                Sa5gNetworkLogEntity(mcc, mnc, endcCapability, endcConnectionStatus)
            }
        }

        /**
         * Converts [ulCarrierLog] to [Sa5gUplinkCarrierLogsEntity]
         * Classname of Sa5gUplinkCarrierLogs : com.tmobile.diagnostics.echolocate.sa5g.data.datametrics.UlCarrierLog
         * */
        fun convertSa5gUplinkCarrierLogsEntity(ulCarrierLog: Any?): Sa5gUplinkCarrierLogsEntity? {
            if (ulCarrierLog == null || !(ulCarrierLog.javaClass.toString()
                    .contains("UlCarrierLog", false))
            ) {
                return null
            }
            val techType =
                Nr5gReflectionUtil.callMethod(
                    ulCarrierLog.javaClass,
                    ULCARRIERLOG_GET_TECH_TYPE_METHOD,
                    ulCarrierLog
                )
            val bandNumber =
                Nr5gReflectionUtil.callMethod(
                    ulCarrierLog.javaClass,
                    ULCARRIERLOG_GET_BAND_NUMBER_METHOD,
                    ulCarrierLog
                )
            val arfcn =
                Nr5gReflectionUtil.callMethod(
                    ulCarrierLog.javaClass,
                    ULCARRIERLOG_GET_ARFCN_METHOD,
                    ulCarrierLog
                )
            val bandWidth =
                Nr5gReflectionUtil.callMethod(
                    ulCarrierLog.javaClass,
                    ULCARRIERLOG_GET_BAND_WIDTH_METHOD,
                    ulCarrierLog
                )
            val isPrimary =
                Nr5gReflectionUtil.callMethod(
                    ulCarrierLog.javaClass,
                    ULCARRIERLOG_GET_IS_PRIMARY_METHOD,
                    ulCarrierLog
                )

            return if (techType == null || bandNumber == null || arfcn == null || bandWidth == null || isPrimary == null) {
                null
            } else {
                Sa5gUplinkCarrierLogsEntity(techType, bandNumber, arfcn, bandWidth, isPrimary)
            }
        }

        /**
         * Converts networkLog to [Sa5gNetworkLogEntity]
         * Classname of Sa5gDownlinkCarrierLogs : com.tmobile.diagnostics.echolocate.sa5g.data.datametrics.NetworkLog
         * */
        fun convertRrcLogToEntity(rrcLog: Any?): Sa5gRrcLogEntity? {
            if (rrcLog == null || !(rrcLog.javaClass.toString().contains("RrcLog", false))) {
                return null
            }
            val lteRrcState =
                Nr5gReflectionUtil.callMethod(
                    rrcLog.javaClass,
                    RRCLOG_GET_LTE_RRC_STATE_METHOD,
                    rrcLog
                )
            val nrRrcState =
                Nr5gReflectionUtil.callMethod(
                    rrcLog.javaClass,
                    RRCLOG_GET_NR_RRC_STATE_METHOD,
                    rrcLog
                )

            return if (lteRrcState == null || nrRrcState == null) {
                null
            } else {
                Sa5gRrcLogEntity(lteRrcState, nrRrcState)
            }
        }

        /**
         * Converts [settingsLog] to [Sa5gSettingsLogEntity]
         * */
        fun convertSettingsLogToEntity(settingsLog: Any?): Sa5gSettingsLogEntity? {
            if (settingsLog == null || !(settingsLog.javaClass.toString()
                    .contains("SettingsLog", false))
            ) {
                return null
            }
            val wifiCalling =
                Nr5gReflectionUtil.callMethod(
                    settingsLog.javaClass,
                    SETTINGSLOG_GET_WIFI_CALLING_METHOD,
                    settingsLog
                )
            val wifi =
                Nr5gReflectionUtil.callMethod(
                    settingsLog.javaClass,
                    SETTINGSLOG_GET_WIFI_METHOD,
                    settingsLog
                )
            val roaming =
                Nr5gReflectionUtil.callMethod(
                    settingsLog.javaClass,
                    SETTINGSLOG_GET_ROAMING_METHOD,
                    settingsLog
                )
            val rtt =
                Nr5gReflectionUtil.callMethod(
                    settingsLog.javaClass,
                    SETTINGSLOG_GET_RTT_METHOD,
                    settingsLog
                )
            val rttTranscript =
                Nr5gReflectionUtil.callMethod(
                    settingsLog.javaClass,
                    SETTINGSLOG_GET_RTT_TRANSCRIPT_METHOD,
                    settingsLog
                )
            val networkMode =
                Nr5gReflectionUtil.callMethod(
                    settingsLog.javaClass,
                    SETTINGSLOG_GET_NETWORK_MODE_METHOD,
                    settingsLog
                )

            return if (wifiCalling == null || wifi == null || roaming == null || rtt == null || rttTranscript == null || networkMode == null) {
                null
            } else {
                Sa5gSettingsLogEntity(wifiCalling, wifi, roaming, rtt, rttTranscript, networkMode)
            }
        }

        /**
         * Converts [uiLog] to [Sa5gUplinkCarrierLogsEntity]
         * Classname of Sa5gDownlinkCarrierLogs : com.tmobile.diagnostics.echolocate.sa5g.data.datametrics.UlCarrierLog
         * */
        fun convertUiLogToEntity(uiLog: Any?): Sa5gUiLogEntity? {
            if (uiLog == null || !(uiLog.javaClass.toString().contains("UiLog", false))) {
                return null
            }
            val networkType =
                Nr5gReflectionUtil.callMethod(
                    uiLog.javaClass,
                    UI_LOG_GET_TIMESTAMP_METHOD,
                    uiLog
                )
            val timestamp =
                Nr5gReflectionUtil.callMethod(
                    uiLog.javaClass,
                    UI_LOG_GET_NETWORK_TYPE_METHOD,
                    uiLog
                )
            val uiNetworkType =
                Nr5gReflectionUtil.callMethod(
                    uiLog.javaClass,
                    UI_LOG_GET_UI_NETWORK_TYPE_METHOD,
                    uiLog
                )
            val uiDataTransmission =
                Nr5gReflectionUtil.callMethod(
                    uiLog.javaClass,
                    UI_LOG_GET_UI_DATA_TRANSMISSION_METHOD,
                    uiLog
                )
            val uiNumberOfAntennaBars =
                Nr5gReflectionUtil.callMethod(
                    uiLog.javaClass,
                    UI_LOG_GET_UI_NUMBEROFANTENNABARS_METHOD,
                    uiLog
                )

            return if (timestamp == null || networkType == null || uiNetworkType == null || uiDataTransmission == null || uiNumberOfAntennaBars == null) {
                null
            } else {
                Sa5gUiLogEntity(
                    timestamp,
                    networkType,
                    uiNetworkType,
                    uiDataTransmission,
                    uiNumberOfAntennaBars
                )
            }
        }

        /**
         * Converts [settingsLog] to [Sa5gCarrierConfigEntity]
         * carrierConfigVersion and carrierSa5gBandConfig collected from settingsLog method,
         * but reported as separated data block Sa5gCarrierConfig with entity Sa5gCarrierConfigEntity
         * */
        fun convertCarrierConfigToEntity(settingsLog: Any?): Sa5gCarrierConfigEntity? {
            if (settingsLog == null || !(settingsLog.javaClass.toString()
                    .contains("SettingsLog", false))
            ) {
                return null
            }

            val carrierConfigVersion =
                Nr5gReflectionUtil.callMethod(
                    settingsLog.javaClass,
                    GET_CARRIER_CONFIG_VERSION_METHOD,
                    settingsLog
                )
            val carrierSa5gBandConfig =
                Nr5gReflectionUtil.callCarrierConfigMethod(
                    settingsLog.javaClass,
                    GET_CARRIER_BAND_CONFIG_METHOD,
                    settingsLog
                )

            val carrierSa5gConfig = carrierSa5gBandConfig as Map<String,String>?

            var bandConfigKeys: StringBuffer? = null
            var bandConfigValues: StringBuffer? = null
            val delimiter = ","
            carrierSa5gConfig?.let {
                bandConfigKeys = StringBuffer()
                bandConfigValues = StringBuffer()
                val keyList: List<String> =
                    ArrayList(carrierSa5gConfig.keys)
                val valueList: MutableList<String?> =
                    ArrayList()
                for (key in keyList) {
                    valueList.add(carrierSa5gConfig[key])
                }
                for ((index, key) in keyList.withIndex()) {
                    bandConfigKeys?.append(key)
                    if (index < keyList.size - 1) {
                        bandConfigKeys?.append(delimiter)
                    }
                }
                for ((index, value) in valueList.withIndex()) {
                    bandConfigValues?.append(value)
                    if (index < keyList.size - 1) {
                        bandConfigValues?.append(delimiter)
                    }
                }

            }

            return if (carrierConfigVersion == null || bandConfigKeys == null || bandConfigValues == null) {
                null
            } else {
                Sa5gCarrierConfigEntity(
                    carrierConfigVersion,
                    bandConfigKeys.toString(),
                    bandConfigValues.toString()
                )
            }
        }

        /**
         * Converts [Sa5gOEMSV] to [Sa5gOEMSVEntity]
         * @param oemSoftwareVersion : [Sa5gOEMSV]
         * @return [Sa5gOEMSVEntity]
         */
        fun convertSa5gOEMSVEntity(
            oemSoftwareVersion: OEMSV
        ): Sa5gOEMSVEntity {
            return Sa5gOEMSVEntity(
                oemSoftwareVersion.softwareVersion,
                oemSoftwareVersion.customVersion,
                oemSoftwareVersion.radioVersion,
                oemSoftwareVersion.buildName,
                oemSoftwareVersion.androidVersion
            )
        }

        /**
         * Converts [Sa5gDeviceInfo] to [Sa5gDeviceInfoEntity]
         * @param deviceInfo: [Sa5gDeviceInfo]
         * @return [Sa5gDeviceInfoEntity]
         */
        fun convertSa5gDeviceInfoEntity(
            deviceInfo: DeviceInfo
        ): Sa5gDeviceInfoEntity {
            return Sa5gDeviceInfoEntity(
                deviceInfo.imei,
                deviceInfo.imsi,
                deviceInfo.msisdn,
                deviceInfo.uuid,
                deviceInfo.testSessionID,
                deviceInfo.modelCode,
                deviceInfo.oem
            )
        }

        /**
         * Converts [Sa5gLocation] to [Sa5gLocationEntity]
         * @param sa5gLocation: [Sa5gLocation]
         * @return [Sa5gLocationEntity]
         */
        fun convertSa5gLocationEntity(sa5gLocation: Sa5gLocation): Sa5gLocationEntity {
            return Sa5gLocationEntity(
                sa5gLocation.altitude ?: 0.0,
                sa5gLocation.altitudePrecision ?: 0.0f,
                sa5gLocation.latitude ?: 0.0,
                sa5gLocation.longitude ?: 0.0,
                sa5gLocation.precision ?: 0.0f,
                sa5gLocation.timestamp ?: EchoLocateDateUtils.getTriggerTimeStamp(),
                sa5gLocation.locationAge ?: 0
            )
        }

        /**
         * Converts [Sa5gConnectedWifiStatus] to [Sa5gConnectedWifiStatusEntity]
         */
        fun convertSa5gConnectedWifiStatusEntity(
            sa5gConnectedWifiStatus: Sa5gConnectedWifiStatus
        ): Sa5gConnectedWifiStatusEntity {
            return Sa5gConnectedWifiStatusEntity(
                bssId = sa5gConnectedWifiStatus.bssId ?: "",
                bssLoad = sa5gConnectedWifiStatus.bssLoad ?: "",
                ssId = sa5gConnectedWifiStatus.ssId ?: "",
                accessPointUpTime = sa5gConnectedWifiStatus.accessPointUpTime ?: 0,
                capabilities = sa5gConnectedWifiStatus.capabilities ?: "",
                centerFreq0 = sa5gConnectedWifiStatus.centerFreq0 ?: 0,
                centerFreq1 = sa5gConnectedWifiStatus.centerFreq1 ?: 0,
                channelMode = sa5gConnectedWifiStatus.channelMode ?: "",
                channelWidth = sa5gConnectedWifiStatus.channelWidth ?: 0,
                frequency = sa5gConnectedWifiStatus.frequency ?: 0,
                operatorFriendlyName = sa5gConnectedWifiStatus.operatorFriendlyName ?: "",
                passportNetwork = sa5gConnectedWifiStatus.passportNetwork ?: 0,
                rssiLevel = sa5gConnectedWifiStatus.rssiLevel ?: 0
            )
        }

        /**
         * Converts [WifiManager] to [Sa5gWiFiStateEntity]
         * @param wifiManager : [WifiManager]
         * @return [Sa5gWiFiStateEntity]
         */
        fun convertSa5gWiFiStateEntity(
            wifiManager: WifiManager
        ): Sa5gWiFiStateEntity {
            return when (wifiManager.wifiState) {
                WifiManager.WIFI_STATE_DISABLING -> Sa5gWiFiStateEntity(Sa5gConstants.WIFI_STATE_DISABLING_INTEGER)
                WifiManager.WIFI_STATE_DISABLED -> Sa5gWiFiStateEntity(Sa5gConstants.WIFI_STATE_DISABLED_INTEGER)
                WifiManager.WIFI_STATE_ENABLING -> Sa5gWiFiStateEntity(Sa5gConstants.WIFI_STATE_ENABLING_INTEGER)
                WifiManager.WIFI_STATE_ENABLED -> Sa5gWiFiStateEntity(Sa5gConstants.WIFI_STATE_ENABLED_INTEGER)
                else -> Sa5gWiFiStateEntity(Sa5gConstants.WIFI_STATE_UNKNOWN_INTEGER)
            }
        }
    }
}