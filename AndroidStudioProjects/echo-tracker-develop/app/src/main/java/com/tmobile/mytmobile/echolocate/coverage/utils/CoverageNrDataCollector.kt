package com.tmobile.mytmobile.echolocate.coverage.utils

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.telephony.*
import androidx.annotation.RequiresApi
import com.tmobile.mytmobile.echolocate.coverage.model.CoverageNrCell
import com.tmobile.mytmobile.echolocate.utils.ELDeviceUtils
import com.tmobile.mytmobile.echolocate.utils.EchoLocateLog

/**
 * Helper class that contains utilities methods to work with nrcell data.
 */
class CoverageNrDataCollector {

    companion object {
        fun getNrCellData(context: Context): CoverageNrCell? {
            //no Nr support for devices below Pie
            if (!ELDeviceUtils.isPieDeviceOrHigher()) {
                return null
            }

            val dataCollectionService = DataCollectionService()
            val serviceState = dataCollectionService.getServiceState(context)
            val coverageNrCell = CoverageNrCell()

            serviceState?.let {
                val nrStatus = CoverageUtils.extractValueNRState("nrState", it.toString())
                coverageNrCell.nrStatus = nrStatus
                EchoLocateLog.eLogD("Diagnostic: Coverage Nr DataCollector - $nrStatus")
            }

            if (ELDeviceUtils.isQDeviceOrHigher() && CoverageUtils.checkLocationPermission(context)){
                EchoLocateLog.eLogD("Diagnostic: Coverage Nr DataCollector - No nr info or nrstatus found")
                fetchNrCellInfoFromCellInfoApi(context, coverageNrCell)
            }

            updateNrCellInfoFromSignalStrength(context, coverageNrCell)

            return coverageNrCell
        }

        @RequiresApi(Build.VERSION_CODES.P)
        private fun updateNrCellInfoFromSignalStrength(context: Context, coverageNrCell: CoverageNrCell) {
            val telephonyManager =
                context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            val signalStrength = telephonyManager.signalStrength

            signalStrength?.let {

                val signalStrengthString = it.toString()
                EchoLocateLog.eLogD("Diagnostic: Coverage Nr DataCollector - $signalStrengthString")
                if (signalStrengthString.contains("CellSignalStrengthNr") && !coverageNrCell.nrStatus.isNullOrEmpty()) {
                    val cellNr: String? = CoverageUtils.getNrCell(signalStrengthString)
                    if (cellNr != null) {

                        val valuesMap: Map<String, String>? = CoverageUtils.extractValues(cellNr)

                        with(coverageNrCell) {
                            if (valuesMap != null) {

                                if (nrCsiRsrp.isNullOrEmpty() &&
                                    valuesMap.containsKey("csiRsrp") &&
                                    !valuesMap["csiRsrp"].isNullOrEmpty()
                                ) {

                                    nrCsiRsrp = valuesMap["csiRsrp"]

                                }

                                if (nrCsiRsrq.isNullOrEmpty() &&
                                    valuesMap.containsKey("csiRsrq") &&
                                    !valuesMap["csiRsrq"].isNullOrEmpty()
                                ) {

                                    nrCsiRsrq = valuesMap["csiRsrq"]

                                }

                                if (nrCsiSinr.isNullOrEmpty() &&
                                    valuesMap.containsKey("csiSinr") &&
                                    !valuesMap["csiSinr"].isNullOrEmpty()
                                ) {

                                    nrCsiSinr = valuesMap["csiSinr"]

                                }

                                if (nrLevel.isNullOrEmpty() &&
                                    valuesMap.containsKey("level") &&
                                    !valuesMap["level"].isNullOrEmpty()
                                ) {

                                    nrLevel = valuesMap["level"]

                                }

                                if (nrSsRsrp.isNullOrEmpty() &&
                                    valuesMap.containsKey("ssRsrp") &&
                                    !valuesMap["ssRsrp"].isNullOrEmpty()
                                ) {

                                    nrSsRsrp = valuesMap["ssRsrp"]

                                }

                                if (nrSsRsrq.isNullOrEmpty() &&
                                    valuesMap.containsKey("ssRsrq") &&
                                    !valuesMap["ssRsrq"].isNullOrEmpty()
                                ) {

                                    nrSsRsrq = valuesMap["ssRsrq"]

                                }

                                if (nrSsSinr.isNullOrEmpty() &&
                                    valuesMap.containsKey("ssSinr") &&
                                    !valuesMap["ssSinr"].isNullOrEmpty()
                                ) {

                                    nrSsSinr = valuesMap["ssSinr"]

                                }

                            }
                        }

                    }
                } else {
                    EchoLocateLog.eLogD("Diagnostic: Coverage Nr DataCollector - No nr info or nrstatus found")
                }
            }
        }

        @SuppressLint("MissingPermission", "NewApi")
        private fun fetchNrCellInfoFromCellInfoApi(context: Context, coverageCell: CoverageNrCell){
            try {
                val telephonyManager =
                    context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

                val nrCell = telephonyManager.allCellInfo?.find { it is CellInfoNr } as? CellInfoNr

                nrCell?.let {
                    val signalStrengthNr = it.cellSignalStrength as CellSignalStrengthNr
                    val signalIdentityNr = it.cellIdentity as CellIdentityNr

                    EchoLocateLog.eLogD("--nr trying to fetch values from cellSignalStrengthNr Api ")
                    processIdentityNr(signalIdentityNr, coverageCell)

                    EchoLocateLog.eLogD("--nr trying to fetch values from cellIdentityNr Api ")
                    processStrengthNr(signalStrengthNr, coverageCell)

                }

            }catch (e: Exception){
                EchoLocateLog.eLogD("--nr Error getting NrCell data - $e")
            }
        }

        @SuppressLint("NewApi")
        private fun processIdentityNr(identityNr: CellIdentityNr, coverageCell: CoverageNrCell){
            val nrarfcn: Int = identityNr.nrarfcn
            val nrPci: Int = identityNr.pci
            val nrTac: Int = identityNr.tac
            val nrNci: Long = identityNr.nci

            if (nrarfcn != CellInfo.UNAVAILABLE) {
                coverageCell.nrArfcn = nrarfcn.toString()
            }

            if (nrPci != CellInfo.UNAVAILABLE) {
                coverageCell.nrPci = nrPci.toString()
            }

            if (nrTac != CellInfo.UNAVAILABLE) {
                coverageCell.nrTac = nrTac.toString()
            }

            if (nrNci != CellInfo.UNAVAILABLE.toLong()) {
                coverageCell.nrCi = nrNci.toString()
            }

            EchoLocateLog.eLogD("--nr nrarfcn value from cellIdentityNr Api: $nrarfcn")
            EchoLocateLog.eLogD("--nr nrPci value from cellIdentityNr Api: $nrPci")
            EchoLocateLog.eLogD("--nr nrTac value from cellIdentityNr Api: $nrTac")
            EchoLocateLog.eLogD("--nr nrNci value from cellIdentityNr Api: $nrNci")
        }

        @RequiresApi(Build.VERSION_CODES.Q)
        private fun processStrengthNr(strength: CellSignalStrengthNr, coverageCell: CoverageNrCell){
            val nrDbm: Int = strength.dbm
            val nrAsuLevel: Int = strength.asuLevel
            val nrcsiRsrp: Int = strength.csiRsrp
            val nrcsiRsrq: Int = strength.csiRsrq
            val csiSinr: Int = strength.csiSinr
            val ssRsrp: Int = strength.ssRsrp
            val ssRsrq: Int = strength.ssRsrq
            val ssSinr: Int = strength.ssSinr
            val level: Int = strength.level

            if (nrAsuLevel != CellInfo.UNAVAILABLE) {
                coverageCell.nrAsuLevel = nrAsuLevel.toString()
            }

            if (nrDbm != CellInfo.UNAVAILABLE) {
                coverageCell.nrDbm = nrDbm.toString()
            }

            if (nrcsiRsrp != CellInfo.UNAVAILABLE) {
                coverageCell.nrCsiRsrp = nrcsiRsrp.toString()
            }

            if (nrcsiRsrq != CellInfo.UNAVAILABLE) {
                coverageCell.nrCsiRsrq = nrcsiRsrq.toString()
            }

            if (csiSinr != CellInfo.UNAVAILABLE) {
                coverageCell.nrCsiSinr = csiSinr.toString()
            }

            if (ssRsrp != CellInfo.UNAVAILABLE) {
                coverageCell.nrSsRsrp = ssRsrp.toString()
            }

            if (ssRsrq != CellInfo.UNAVAILABLE) {
                coverageCell.nrSsRsrq = ssRsrq.toString()
            }

            if (ssSinr != CellInfo.UNAVAILABLE) {
                coverageCell.nrSsSinr = ssSinr.toString()
            }

            if (level != CellInfo.UNAVAILABLE) {
                coverageCell.nrLevel = level.toString()
            }

            EchoLocateLog.eLogD("--nr nrDbm value from cellSignalStrengthNr Api $nrDbm")
            EchoLocateLog.eLogD("--nr nrAsuLevel value from cellSignalStrengthNr Api $nrAsuLevel")
            EchoLocateLog.eLogD("--nr nrcsiRsrp value from cellSignalStrengthNr Api $nrcsiRsrp")
            EchoLocateLog.eLogD("--nr nrcsiRsrq value from cellSignalStrengthNr Api $nrcsiRsrq")
            EchoLocateLog.eLogD("--nr csiSinr value from cellSignalStrengthNr Api $csiSinr")
            EchoLocateLog.eLogD("--nr ssRsrp value from cellSignalStrengthNr Api $ssRsrp")
            EchoLocateLog.eLogD("--nr ssRsrq value from cellSignalStrengthNr Api $ssRsrq")
            EchoLocateLog.eLogD("--nr ssSinr value from cellSignalStrengthNr Api $ssSinr")
            EchoLocateLog.eLogD("--nr level value from cellSignalStrengthNr Api $level")
        }
    }
}