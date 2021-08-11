package com.tmobile.mytmobile.echolocate.coverage.utils

import com.tmobile.mytmobile.echolocate.coverage.database.entity.*
import com.tmobile.mytmobile.echolocate.coverage.model.*
import com.tmobile.mytmobile.echolocate.standarddatablocks.OEMSV


class CoverageEntityConverter {
    companion object {
        /**
         * Converts [CoverageConnectedWifiStatus] to [CoverageConnectedWifiStatusEntity]
         */
        fun convertCoverageConnectedWifiStatusEntity(
            coverageConnectedWifiStatus: CoverageConnectedWifiStatus
        ): CoverageConnectedWifiStatusEntity {
            return CoverageConnectedWifiStatusEntity(
                wifiState = coverageConnectedWifiStatus.wifiState,
                bssid = coverageConnectedWifiStatus.bssid,
                bssLoad = coverageConnectedWifiStatus.bssLoad,
                capabilities = coverageConnectedWifiStatus.capabilities,
                centerFreq0 = coverageConnectedWifiStatus.centerFreq0,
                centerFreq1 = coverageConnectedWifiStatus.centerFreq1,
                channelMode = coverageConnectedWifiStatus.channelMode,
                channelWidth = coverageConnectedWifiStatus.channelWidth,
                frequency = coverageConnectedWifiStatus.frequency,
                rssiLevel = coverageConnectedWifiStatus.rssiLevel,
                operatorFriendlyName = coverageConnectedWifiStatus.operatorFriendlyName,
                passportNetwork = coverageConnectedWifiStatus.passportNetwork,
                ssid = coverageConnectedWifiStatus.ssid,
                accessPointUpTime = coverageConnectedWifiStatus.accessPointUpTime,
                timestamp = coverageConnectedWifiStatus.timestamp
            )
        }

        /**
         * Converts [CoverageNrCell] to [CoverageNrCellEntity]
         */
        fun convertCoverageNrCellEntity(
            coverageNrCell: CoverageNrCell
        ): CoverageNrCellEntity {
            return CoverageNrCellEntity(
                nrCsiRsrp = coverageNrCell.nrCsiRsrp,
                nrCsiRsrq = coverageNrCell.nrCsiRsrq,
                nrCsiSinr = coverageNrCell.nrCsiSinr,
                nrLevel = coverageNrCell.nrLevel,
                nrSsRsrp = coverageNrCell.nrSsRsrp,
                nrSsRsrq = coverageNrCell.nrSsRsrq,
                nrSsSinr = coverageNrCell.nrSsSinr,
                nrStatus = coverageNrCell.nrStatus,
                nrDbm = coverageNrCell.nrDbm,
                nrAsuLevel = coverageNrCell.nrAsuLevel,
                nrArfcn = coverageNrCell.nrArfcn,
                nrCi = coverageNrCell.nrCi,
                nrPci = coverageNrCell.nrPci,
                nrTac = coverageNrCell.nrTac
            )
        }

        /**
         * Converts [OEMSV] to [CoverageOEMSVEntity]
         * @param oemSoftwareVersion : [OEMSV]
         * @return [CoverageOEMSVEntity]
         */
        fun convertCoverageOEMSVEntity(
            oemSoftwareVersion: OEMSV
        ): CoverageOEMSVEntity {
            return CoverageOEMSVEntity(
                sv = oemSoftwareVersion.softwareVersion,
                customVersion = oemSoftwareVersion.customVersion,
                radioVersion = oemSoftwareVersion.radioVersion,
                buildName = oemSoftwareVersion.buildName,
                androidVersion = oemSoftwareVersion.androidVersion
            )
        }

        /**
         * Converts [CoveragePrimaryCell] to [CoveragePrimaryCellEntity]
         * @param covPrimCell: [CoveragePrimaryCell]
         * @return [CoveragePrimaryCellEntity] converted entity
         */
        fun convertCoveragePrimaryCellEntity(
            covPrimCell: CoveragePrimaryCell
        ): CoveragePrimaryCellEntity {
            return CoveragePrimaryCellEntity(
                cellType = covPrimCell.cellType
            )
        }

        /**
         * Converts [CoverageCellIdentity] to [CoverageCellIdentityEntity]
         * @param covCellId: [CoverageCellIdentity]
         * @return [CoverageCellIdentityEntity] converted entity
         */
        fun convertCoverageCellIdentityEntity(
            covCellId: CoverageCellIdentity
        ): CoverageCellIdentityEntity {
            return CoverageCellIdentityEntity(
                cellId = covCellId.cellId,
                cellInfoDelay = covCellId.cellInfoDelay,
                networkName = covCellId.networkName,
                mcc = covCellId.mcc,
                mnc = covCellId.mnc,
                earfcn = covCellId.earfcn,
                tac = covCellId.tac,
                lac = covCellId.lac
            )
        }

        /**
         * Converts [CoverageCellSignalStrength] to [CoverageCellSignalStrengthEntity]
         * @param covCellSigStr: [CoverageCellSignalStrength]
         * @return [CoverageCellSignalStrengthEntity] converted entity
         */
        fun convertCoverageCellSignalStrengthEntity(
            covCellSigStr: CoverageCellSignalStrength
        ): CoverageCellSignalStrengthEntity {
            return CoverageCellSignalStrengthEntity(
                asu = covCellSigStr.asu,
                dBm = covCellSigStr.dBm,
                bandwidth = covCellSigStr.bandwidth,
                rsrp = covCellSigStr.rsrp,
                rsrq = covCellSigStr.rsrq,
                rssnr = covCellSigStr.rssnr,
                cqi = covCellSigStr.cqi,
                timingAdvance = covCellSigStr.timingAdvance
            )
        }

    }
}