package com.tmobile.mytmobile.echolocate.nr5g.nsa5g.utils

import android.net.wifi.WifiManager
import android.telephony.TelephonyManager
import com.tmobile.mytmobile.echolocate.nr5g.core.utils.Nr5gReflectionUtil
import com.tmobile.mytmobile.echolocate.nr5g.nsa5g.database.entity.*
import com.tmobile.mytmobile.echolocate.nr5g.nsa5g.model.ConnectedWifiStatus
import com.tmobile.mytmobile.echolocate.standarddatablocks.DeviceInfo
import com.tmobile.mytmobile.echolocate.standarddatablocks.OEMSV
import com.tmobile.mytmobile.echolocate.utils.EchoLocateDateUtils
import com.tmobile.mytmobile.echolocate.utils.EchoLocateDateUtils.Companion.getTriggerTimeStamp
import com.tmobile.mytmobile.echolocate.utils.NumberUtils

class Nsa5gEntityConverter {
    companion object {
        /** Method names from class : com.tmobile.diagnostics.echolocate.nr5g.data.datametrics.EndcLteLog
         *   long getTimestamp()
         *   int  getNetworkType()
         *   long getAnchorLteCid()
         *   int  getAnchorLtePci()
         *   int  getEndcCapability()
         *   int  getLteRrcState()
         */
        const val ENDCLTELOG_GET_TIMESTAMP_METHOD = "getTimestamp"
        const val ENDCLTELOG_GET_NETWORK_TYPE_METHOD = "getNetworkType"
        const val ENDCLTELOG_GET_ANCHOR_LTE_CID_METHOD = "getAnchorLteCid"
        const val ENDCLTELOG_GET_ANCHOR_LTE_PCI_METHOD = "getAnchorLtePci"
        const val ENDCLTELOG_GET_ENDC_CAPABILITY_METHOD = "getEndcCapability"
        const val ENDCLTELOG_GET_LTE_RRC_STATE_METHOD = "getLteRrcState"

        /** Method names from class : com.tmobile.diagnostics.echolocate.nr5g.data.datametrics.NrMmwCellLog
         *   long   getTimestamp()
         *   int    getNetworkType()
         *   int    getNrPscellPci()
         *   int    getSsbBeamIndex()
         *   float  getSsbBrsrp()
         *   float  getSsbBrsrq()
         *   float  getSsbSnr()
         *   int    getPdschBeamIndex()
         *   float  getPdschBrsrp()
         *   float  getPdschBrsrq()
         *   float  getPdschSnr()
         *   String getNrBandName()
         *   int    getNrBandwidth()
         *   int    getNumberOfSsBBeams()
         */
        const val NRMMWCELLLOG_GET_TIMESTAMP_METHOD = "getTimestamp"
        const val NRMMWCELLLOG_GET_NETWORK_TYPE_METHOD = "getNetworkType"
        const val NRMMWCELLLOG_GET_NR_PS_CELL_PCI_METHOD = "getNrPscellPci"
        const val NRMMWCELLLOG_GET_SSB_BEAM_INDEX_METHOD = "getSsbBeamIndex"
        const val NRMMWCELLLOG_GET_SSB_BRSRP_METHOD = "getSsbBrsrp"
        const val NRMMWCELLLOG_GET_SSB_BRSRQ_METHOD = "getSsbBrsrq"
        const val NRMMWCELLLOG_GET_SSB_SNR_METHOD = "getSsbSnr"
        const val NRMMWCELLLOG_GET_PDSCH_BEAM_INDEX_METHOD = "getPdschBeamIndex"
        const val NRMMWCELLLOG_GET_PDSCH_BRSRP_METHOD = "getPdschBrsrp"
        const val NRMMWCELLLOG_GET_PDSCH_BRSRQ_METHOD = "getPdschBrsrq"
        const val NRMMWCELLLOG_GET_PDSCH_SNR_METHOD = "getPdschSnr"
        const val NRMMWCELLLOG_GET_NR_BAND_NAME_METHOD = "getNrBandName"
        const val NRMMWCELLLOG_GET_NR_BAND_WIDTH_METHOD = "getNrBandwidth"
        const val NRMMWCELLLOG_GET_NUMS_OF_SSB_BEAM_METHOD = "getNumberOfSsBBeams"

        /** Method names from class : com.tmobile.diagnostics.echolocate.nr5g.data.datametrics.Nr5gUiLog
         *   long   getTimestamp()
         *   int    getNetworkType()
         *   String getUiNetworkType()
         *   String getUiDataTransmission()
         *   int    getUiNumberOfAntennaBars()
         *   int    getUi5gConfigurationStatus()
         */
        const val NR5GUILOG_GET_TIMESTAMP_METHOD = "getTimestamp"
        const val NR5GUILOG_GET_NETWORK_TYPE_METHOD = "getNetworkType"
        const val NR5GUILOG_GET_UI_NETWORK_TYPE_METHOD = "getUiNetworkType"
        const val NR5GUILOG_GET_UI_DATA_TRANSMISSION_METHOD = "getUiDataTransmission"
        const val NR5GUILOG_GET_UI_NUMS_OF_ANTENNA_BARS_METHOD = "getUiNumberOfAntennaBars"
        const val NR5GUILOG_GET_UI5G_CONFIGURATION_STATUS_METHOD = "getUi5gConfigurationStatus"

        /** Method names from class : com.tmobile.diagnostics.echolocate.nr5g.data.datametrics.EndcUpLinkLog
         *   long getTimestamp()
         *   int  getNetworkType()
         *   int  getUplinkNetwork()
         */
        const val ENDCUPLINKLOG_GET_TIMESTAMP_METHOD = "getTimestamp"
        const val ENDCUPLINKLOG_GET_NETWORK_TYPE_METHOD = "getNetworkType"
        const val ENDCUPLINKLOG_GET_UPLINK_NETWORK_METHOD = "getUplinkNetwork"

        /**
         * Converts [EndcLteLog] to [EndcLteLogEntity]
         * Classname of EndcLteLog : com.tmobile.diagnostics.echolocate.nr5g.data.datametrics.EndcLteLog
         *
         * Method names from EndcLteLog :
         *   long getTimestamp()
         *   int  getNetworkType()
         *   long getAnchorLteCid()
         *   int  getAnchorLtePci()
         *   int  getEndcCapability()
         *   int  getLteRrcState()
         *
         * @param any: [EndcLteLog]
         * @return [EndcLteLogEntity]
         * */
        fun convertEndcLteLogEntityObject(
            endcLteLog: Any?
        ): EndcLteLogEntity? {
            if (endcLteLog == null || !(endcLteLog.javaClass.toString()
                    .contains("EndcLteLog", false))
            ) {
                return null
            }

            val timeStamp = NumberUtils.convertToLong(
                Nr5gReflectionUtil.callMethod(
                    endcLteLog.javaClass,
                    ENDCLTELOG_GET_TIMESTAMP_METHOD,
                    endcLteLog
                )
            )

            val networkType = NumberUtils.convertToInt(
                Nr5gReflectionUtil.callMethod(
                    endcLteLog.javaClass,
                    ENDCLTELOG_GET_NETWORK_TYPE_METHOD,
                    endcLteLog
                )
            )

            val anchorLteCid = NumberUtils.convertToLong(
                Nr5gReflectionUtil.callMethod(
                    endcLteLog.javaClass,
                    ENDCLTELOG_GET_ANCHOR_LTE_CID_METHOD,
                    endcLteLog
                )
            )

            val anchorLtePci = NumberUtils.convertToInt(
                Nr5gReflectionUtil.callMethod(
                    endcLteLog.javaClass,
                    ENDCLTELOG_GET_ANCHOR_LTE_PCI_METHOD,
                    endcLteLog
                )
            )

            val endCapability = NumberUtils.convertToInt(
                Nr5gReflectionUtil.callMethod(
                    endcLteLog.javaClass,
                    ENDCLTELOG_GET_ENDC_CAPABILITY_METHOD,
                    endcLteLog
                )
            )

            val lteRrcState = NumberUtils.convertToInt(
                Nr5gReflectionUtil.callMethod(
                    endcLteLog.javaClass,
                    ENDCLTELOG_GET_LTE_RRC_STATE_METHOD,
                    endcLteLog
                )
            )

            return if (timeStamp == null || networkType == null || anchorLteCid == null || anchorLtePci == null || endCapability == null || lteRrcState == null) {
                null
            } else {
                EndcLteLogEntity(
                    EchoLocateDateUtils.convertToShemaDateFormat(timeStamp.toString()),
                    networkType,
                    anchorLteCid,
                    anchorLtePci,
                    endCapability,
                    lteRrcState
                )
            }
        }

        /**
         * Converts [NrMmwCellLog] to [Nr5gMmwCellLogEntity]
         * Classname of NrMmwCellLog : com.tmobile.diagnostics.echolocate.nr5g.data.datametrics.NrMmwCellLog
         *
         * Method names from NrMmwCellLog :
         *   long   getTimestamp()
         *   int    getNetworkType()
         *   int    getNrPscellPci()
         *   int    getSsbBeamIndex()
         *   float  getSsbBrsrp()
         *   float  getSsbBrsrq()
         *   float  getSsbSnr()
         *   int    getPdschBeamIndex()
         *   float  getPdschBrsrp()
         *   float  getPdschBrsrq()
         *   float  getPdschSnr()
         *   String getNrBandName()
         *   int    getNrBandwidth()
         *   int    getNumberOfSsBBeams()
         *
         * @param any: [NrMmwCellLog]
         * @return [Nr5gMmwCellLogEntity]
         * */
        fun convertNr5gMmwCellLogEntityObject(
            nrMmwCellLog: Any?
        ): Nr5gMmwCellLogEntity? {
            if (nrMmwCellLog == null || !(nrMmwCellLog.javaClass.toString()
                    .contains("NrMmwCellLog", false) || nrMmwCellLog.javaClass.toString()
                    .contains("Nr5gMmwCellLog", false))
            ) {
                return null
            }

            val timeStamp = NumberUtils.convertToLong(
                Nr5gReflectionUtil.callMethod(
                    nrMmwCellLog.javaClass,
                    NRMMWCELLLOG_GET_TIMESTAMP_METHOD,
                    nrMmwCellLog
                )
            )

            val networkType = NumberUtils.convertToInt(
                Nr5gReflectionUtil.callMethod(
                    nrMmwCellLog.javaClass,
                    NRMMWCELLLOG_GET_NETWORK_TYPE_METHOD,
                    nrMmwCellLog
                )
            )

            val nrPscellPci = NumberUtils.convertToInt(
                Nr5gReflectionUtil.callMethod(
                    nrMmwCellLog.javaClass,
                    NRMMWCELLLOG_GET_NR_PS_CELL_PCI_METHOD,
                    nrMmwCellLog
                )
            )

            val ssbBeamIndex = NumberUtils.convertToInt(
                Nr5gReflectionUtil.callMethod(
                    nrMmwCellLog.javaClass,
                    NRMMWCELLLOG_GET_SSB_BEAM_INDEX_METHOD,
                    nrMmwCellLog
                )
            )

            val ssbBrsrp = NumberUtils.convertToFloat(
                Nr5gReflectionUtil.callMethod(
                    nrMmwCellLog.javaClass,
                    NRMMWCELLLOG_GET_SSB_BRSRP_METHOD,
                    nrMmwCellLog
                )
            )

            val ssbBrsrq = NumberUtils.convertToFloat(
                Nr5gReflectionUtil.callMethod(
                    nrMmwCellLog.javaClass,
                    NRMMWCELLLOG_GET_SSB_BRSRQ_METHOD,
                    nrMmwCellLog
                )
            )

            val ssbSnr = NumberUtils.convertToFloat(
                Nr5gReflectionUtil.callMethod(
                    nrMmwCellLog.javaClass,
                    NRMMWCELLLOG_GET_SSB_SNR_METHOD,
                    nrMmwCellLog
                )
            )

            val pdschBeamIndex = NumberUtils.convertToInt(
                Nr5gReflectionUtil.callMethod(
                    nrMmwCellLog.javaClass,
                    NRMMWCELLLOG_GET_PDSCH_BEAM_INDEX_METHOD,
                    nrMmwCellLog
                )
            )

            val pdschBrsrp = NumberUtils.convertToFloat(
                Nr5gReflectionUtil.callMethod(
                    nrMmwCellLog.javaClass,
                    NRMMWCELLLOG_GET_PDSCH_BRSRP_METHOD,
                    nrMmwCellLog
                )
            )

            val pdschBrsrq = NumberUtils.convertToFloat(
                Nr5gReflectionUtil.callMethod(
                    nrMmwCellLog.javaClass,
                    NRMMWCELLLOG_GET_PDSCH_BRSRQ_METHOD,
                    nrMmwCellLog
                )
            )

            val pdschSnr = NumberUtils.convertToFloat(
                Nr5gReflectionUtil.callMethod(
                    nrMmwCellLog.javaClass,
                    NRMMWCELLLOG_GET_PDSCH_SNR_METHOD,
                    nrMmwCellLog
                )
            )

            val nrBandName = Nr5gReflectionUtil.callMethod(
                nrMmwCellLog.javaClass,
                NRMMWCELLLOG_GET_NR_BAND_NAME_METHOD,
                nrMmwCellLog
            )

            val nrBandwidth = NumberUtils.convertToInt(
                Nr5gReflectionUtil.callMethod(
                    nrMmwCellLog.javaClass,
                    NRMMWCELLLOG_GET_NR_BAND_WIDTH_METHOD,
                    nrMmwCellLog
                )
            )

            val numberOfSsbBeams = NumberUtils.convertToInt(
                Nr5gReflectionUtil.callMethod(
                    nrMmwCellLog.javaClass,
                    NRMMWCELLLOG_GET_NUMS_OF_SSB_BEAM_METHOD,
                    nrMmwCellLog
                )
            )

            return if (timeStamp == null || networkType == null || nrPscellPci == null ||
                ssbBeamIndex == null || ssbBrsrp == null || ssbBrsrq == null ||
                ssbSnr == null || pdschBeamIndex == null || pdschBrsrp == null ||
                pdschBrsrq == null || pdschSnr == null || nrBandName == null ||
                nrBandwidth == null
            ) {
                null
            } else {
                Nr5gMmwCellLogEntity(
                    EchoLocateDateUtils.convertToShemaDateFormat(timeStamp.toString()),
                    networkType, nrPscellPci, ssbBeamIndex, ssbBrsrp, ssbBrsrq,
                    ssbSnr, pdschBeamIndex, pdschBrsrp, pdschBrsrq, pdschSnr,
                    nrBandName, nrBandwidth, numberOfSsbBeams ?: -2
                )
            }
        }

        /**
         * Converts [Nr5gUiLog] to [Nr5gUiLogEntity]
         * Classname of EndcLteLog : com.tmobile.diagnostics.echolocate.nr5g.data.datametrics.Nr5gUiLog
         *
         * Method names from EndcLteLog :
         *   long   getTimestamp()
         *   int    getNetworkType()
         *   String getUiNetworkType()
         *   String getUiDataTransmission()
         *   int    getUiNumberOfAntennaBars()
         *   int    getUi5gConfigurationStatus()
         *
         * @param any: [Nr5gUiLog]
         * @return [Nr5gUiLogEntity]
         * */
        fun convertNr5gUiLogEntityObject(
            nr5gUiLog: Any?
        ): Nr5gUiLogEntity? {
            if (nr5gUiLog == null || !(nr5gUiLog.javaClass.toString()
                    .contains("Ui5gLog", false) || nr5gUiLog.javaClass.toString()
                    .contains("Nr5gUiLog", false))
            ) {
                return null
            }

            val timeStamp = NumberUtils.convertToLong(
                Nr5gReflectionUtil.callMethod(
                    nr5gUiLog.javaClass,
                    NR5GUILOG_GET_TIMESTAMP_METHOD,
                    nr5gUiLog
                )
            )

            val networkType = NumberUtils.convertToInt(
                Nr5gReflectionUtil.callMethod(
                    nr5gUiLog.javaClass,
                    NR5GUILOG_GET_NETWORK_TYPE_METHOD,
                    nr5gUiLog
                )
            )

            val uiNetworkType = Nr5gReflectionUtil.callMethod(
                nr5gUiLog.javaClass,
                NR5GUILOG_GET_UI_NETWORK_TYPE_METHOD,
                nr5gUiLog
            )

            val uiDataTransmission = Nr5gReflectionUtil.callMethod(
                nr5gUiLog.javaClass,
                NR5GUILOG_GET_UI_DATA_TRANSMISSION_METHOD,
                nr5gUiLog
            )

            val uiNumberOfAntennaBars = NumberUtils.convertToInt(
                Nr5gReflectionUtil.callMethod(
                    nr5gUiLog.javaClass,
                    NR5GUILOG_GET_UI_NUMS_OF_ANTENNA_BARS_METHOD,
                    nr5gUiLog
                )
            )

            val ui5gConfigurationStatus = NumberUtils.convertToInt(
                Nr5gReflectionUtil.callMethod(
                    nr5gUiLog.javaClass,
                    NR5GUILOG_GET_UI5G_CONFIGURATION_STATUS_METHOD,
                    nr5gUiLog
                )
            )

            return if (timeStamp == null || networkType == null || uiNetworkType == null || uiDataTransmission == null || uiNumberOfAntennaBars == null || ui5gConfigurationStatus == null) {
                null
            } else {
                Nr5gUiLogEntity(
                    EchoLocateDateUtils.convertToShemaDateFormat(timeStamp.toString()),
                    networkType,
                    uiNetworkType,
                    uiDataTransmission,
                    uiNumberOfAntennaBars,
                    ui5gConfigurationStatus
                )
            }
        }


        /**
         * Converts [EndcUplinkLog] to [EndcUplinkLogEntity]
         * Classname of EndcLteLog : com.tmobile.diagnostics.echolocate.nr5g.data.datametrics.EndcUpLinkLog
         *
         * Method names from EndcUplinkLog :
         *   long getTimestamp()
         *   int  getNetworkType()
         *   int getUplinkNetwork()
         *
         * @param any: [EndcUplinkLog]
         * @return [EndcUplinkLogEntity]
         * */
        fun convertEndcUpLinkLogEntityObject(
            endcUplinkLog: Any?
        ): EndcUplinkLogEntity? {
            if (endcUplinkLog == null || !(endcUplinkLog.javaClass.toString()
                    .contains("EndcUplinkLog", false))
            ) {
                return null
            }

            val timeStamp = NumberUtils.convertToLong(
                Nr5gReflectionUtil.callMethod(
                    endcUplinkLog.javaClass,
                    ENDCUPLINKLOG_GET_TIMESTAMP_METHOD,
                    endcUplinkLog
                )
            )

            val networkType = NumberUtils.convertToInt(
                Nr5gReflectionUtil.callMethod(
                    endcUplinkLog.javaClass,
                    ENDCUPLINKLOG_GET_NETWORK_TYPE_METHOD,
                    endcUplinkLog
                )
            )

            val uplinkNetwork = NumberUtils.convertToInt(
                Nr5gReflectionUtil.callMethod(
                    endcUplinkLog.javaClass,
                    ENDCUPLINKLOG_GET_UPLINK_NETWORK_METHOD,
                    endcUplinkLog
                )
            )

            return if (timeStamp == null || networkType == null || uplinkNetwork == null) {
                null
            } else {
                EndcUplinkLogEntity(
                    EchoLocateDateUtils.convertToShemaDateFormat(timeStamp.toString()),
                    networkType,
                    uplinkNetwork
                )
            }
        }

        /**
         * Converts [OEMSV] to [Nr5gOEMSVEntity]
         * @param oemSoftwareVersion : [OEMSV]
         * @return [Nr5gOEMSVEntity]
         */
        fun convertNr5gOEMSVEntity(
            oemSoftwareVersion: OEMSV
        ): Nr5gOEMSVEntity {
            return Nr5gOEMSVEntity(
                oemSoftwareVersion.softwareVersion,
                oemSoftwareVersion.customVersion,
                oemSoftwareVersion.radioVersion,
                oemSoftwareVersion.buildName,
                oemSoftwareVersion.androidVersion
            )
        }


        /**
         * Converts [WifiManager] to [Nr5gWifiStateEntity]
         * @param wifiManager : [WifiManager]
         * @return [Nr5gWifiStateEntity]
         */
        fun convertNr5gWifiStateEntity(
            wifiManager: WifiManager
        ): Nr5gWifiStateEntity {
            return when (wifiManager.wifiState) {
                WifiManager.WIFI_STATE_DISABLING -> Nr5gWifiStateEntity(Nsa5gConstants.WIFI_STATE_DISABLING_INTEGER)
                WifiManager.WIFI_STATE_DISABLED -> Nr5gWifiStateEntity(Nsa5gConstants.WIFI_STATE_DISABLED_INTEGER)
                WifiManager.WIFI_STATE_ENABLING -> Nr5gWifiStateEntity(Nsa5gConstants.WIFI_STATE_ENABLING_INTEGER)
                WifiManager.WIFI_STATE_ENABLED -> Nr5gWifiStateEntity(Nsa5gConstants.WIFI_STATE_ENABLED_INTEGER)
                else -> Nr5gWifiStateEntity(Nsa5gConstants.WIFI_STATE_UNKNOWN_INTEGER)
            }
        }

        /**
         * Converts [TelephonyManager] to [Nr5gDataNetworkTypeEntity]
         * @param telephonyManager : [TelephonyManager]
         * @return [Nr5gDataNetworkTypeEntity]
         */
        fun convertNr5gDataNetworkTypeEntity(
            telephonyManager: TelephonyManager
        ): Nr5gDataNetworkTypeEntity {
            return Nr5gDataNetworkTypeEntity(
                getTriggerTimeStamp(),
                telephonyManager.networkType
            )
        }

        /**
         * Converts [DeviceInfo] to [Nr5gDeviceInfoEntity]
         * @param deviceInfo: [DeviceInfo]
         * @return [Nr5gDeviceInfoEntity]
         */
        fun convertNr5gDeviceInfoEntity(
            deviceInfo: DeviceInfo
        ): Nr5gDeviceInfoEntity {
            return Nr5gDeviceInfoEntity(
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
         * Converts [WifiStatus] to [ConnectedWifiStatus]
         */
        fun convertWifiStatusEntity(
            connectedWifiStatus: ConnectedWifiStatus
        ): ConnectedWifiStatusEntity {
            return ConnectedWifiStatusEntity(
                connectedWifiStatus.bssId,
                connectedWifiStatus.bssLoad,
                connectedWifiStatus.ssId,
                connectedWifiStatus.accessPointUpTime,
                connectedWifiStatus.capabilities,
                connectedWifiStatus.centerFreq0,
                connectedWifiStatus.centerFreq1,
                connectedWifiStatus.channelMode,
                connectedWifiStatus.channelWidth,
                connectedWifiStatus.frequency,
                connectedWifiStatus.operatorFriendlyName,
                connectedWifiStatus.passportNetwork,
                connectedWifiStatus.rssiLevel
            )
        }


    }
}