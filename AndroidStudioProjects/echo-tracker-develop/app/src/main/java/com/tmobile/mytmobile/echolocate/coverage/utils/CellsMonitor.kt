package com.tmobile.mytmobile.echolocate.coverage.utils

import android.content.Context
import android.os.SystemClock
import android.telephony.*
import com.tmobile.mytmobile.echolocate.coverage.model.CoverageCellIdentity
import com.tmobile.mytmobile.echolocate.coverage.model.CoverageCellSignalStrength
import com.tmobile.mytmobile.echolocate.coverage.model.CoveragePrimaryCell
import com.tmobile.mytmobile.echolocate.utils.ELDeviceUtils
import com.tmobile.mytmobile.echolocate.utils.SingletonHolder
import java.util.concurrent.TimeUnit

/**
 * Created by Mahesh Shetye on 2020-05-06
 *
 * Helper class that contains utilities methods related to cell info
 *
 */

class CellsMonitor private constructor(val context: Context) {

    private val tm: TelephonyManager?

    /**
     * Primary cell.
     */
    private var primaryCell: CoveragePrimaryCell? = null

    enum class CELL_INFO_TYPE {
        UNKNOWN,           // = 0
        GSM,          // = 1
        CDMA,         // = 2
        LTE,          // = 3
        WCDMA,        // = 4
        TDSCDMA,      // = 5
        NR            // = 6
    }


    companion object : SingletonHolder<CellsMonitor, Context>(::CellsMonitor)

    init {
        tm = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
    }

    /**
     * @return Primary cell.
     */
    fun getPrimaryCell(): CoveragePrimaryCell? {
        return primaryCell
    }

    /**
     * Refresh cells monitor data.
     */
    @Synchronized
    fun refreshCellsMonitorData() {
        /*
            https://docs.oracle.com/javase/8/docs/api/java/util/ArrayList.html
            to prevent accidental unsynchronized access to the list
         */
        var isPrimaryCellAvailable = false
        if (tm != null && CoverageUtils.checkLocationPermission(context)) {
            if (tm.allCellInfo != null) {
                for (cell in tm.allCellInfo) {
                    if (cell.isRegistered) {
                        parseCell(cell)
                        isPrimaryCellAvailable = true
                    }
                }
            }

            if (!isPrimaryCellAvailable) {
                primaryCell = null
            }
        }
    }

    /**
     * Method to check if cell is registered. It selects whether cell is primary or neighbor.
     */
    private fun parseCell(cellInfo: CellInfo) {
        val covCellIdentity = getCellIdentity(cellInfo)
        val covCellSigStrth = getCellSignalStrength(cellInfo)

        primaryCell = CoveragePrimaryCell(
            cellType = getCellType(cellInfo),
            cellIdentity = covCellIdentity,
            cellSignalStrength = covCellSigStrth
        )
    }

    /**
     * Retrieves cell type
     *
     * @return cell type name from [CELL_INFO_TYPE]
     *
     */
    private fun getCellType(cellInfo: CellInfo): String {
        when {
            cellInfo is CellInfoGsm -> {
                return CELL_INFO_TYPE.GSM.name
            }
            cellInfo is CellInfoCdma -> {
                return CELL_INFO_TYPE.CDMA.name
            }
            cellInfo is CellInfoLte -> {
                return CELL_INFO_TYPE.LTE.name
            }
            cellInfo is CellInfoWcdma -> {
                return CELL_INFO_TYPE.WCDMA.name
            }
            ELDeviceUtils.isQDeviceOrHigher() && cellInfo is CellInfoTdscdma -> {
                // supported in Android Q
                return CELL_INFO_TYPE.TDSCDMA.name
            }
            ELDeviceUtils.isQDeviceOrHigher() && cellInfo is CellInfoNr -> {
                return CELL_INFO_TYPE.NR.name
            }
            else -> return CELL_INFO_TYPE.UNKNOWN.name
        }
    }

    /**
     * Retrieves object of CoverageCellIdentityEntity
     *
     */
    private fun getCellIdentity(cellInfo: CellInfo): CoverageCellIdentity {
        when {
            cellInfo is CellInfoGsm -> {
                return CoverageCellIdentity(
                    cellId = getUpdatedIntValue(cellInfo.cellIdentity.cid),
                    cellInfoDelay = getCellInfoDelay(cellInfo),
                    networkName = getNetworkName(),
                    mcc = getUpdatedMcc(cellInfo),
                    mnc = getUpdatedMnc(cellInfo),
                    earfcn = null,                     // Not required for GSM network
                    tac = null,                        // Not required for GSM network
                    lac = getUpdatedIntValue(cellInfo.cellIdentity.lac)
                )
            }
            cellInfo is CellInfoCdma -> {
                // No parsing is required for CDMA network, will return default object
            }
            cellInfo is CellInfoLte -> {
                return CoverageCellIdentity(
                    cellId = getUpdatedIntValue(cellInfo.cellIdentity.ci),
                    cellInfoDelay = getCellInfoDelay(cellInfo),
                    networkName = getNetworkName(),
                    mcc = getUpdatedMcc(cellInfo),
                    mnc = getUpdatedMnc(cellInfo),
                    earfcn = getUpdatedArfcn(cellInfo),
                    tac = getUpdatedIntValue(cellInfo.cellIdentity.tac),
                    lac = null                         // Not required for LTE network
                )
            }
            cellInfo is CellInfoWcdma -> {
                return CoverageCellIdentity(
                    cellId = getUpdatedIntValue(cellInfo.cellIdentity.cid),
                    cellInfoDelay = getCellInfoDelay(cellInfo),
                    networkName = getNetworkName(),
                    mcc = getUpdatedMcc(cellInfo),
                    mnc = getUpdatedMnc(cellInfo),
                    earfcn = getUpdatedArfcn(cellInfo),
                    tac = null,                        // Not required for Wcdma network
                    lac = getUpdatedIntValue(cellInfo.cellIdentity.lac)
                )
            }
            ELDeviceUtils.isQDeviceOrHigher() && cellInfo is CellInfoTdscdma -> {
//                supported in Android Q
                return CoverageCellIdentity(
                    cellId = getUpdatedIntValue(cellInfo.cellIdentity.cid),
                    cellInfoDelay = getCellInfoDelay(cellInfo),
                    networkName = getNetworkName(),
                    mcc = getUpdatedMcc(cellInfo),
                    mnc = getUpdatedMnc(cellInfo),
                    earfcn = getUpdatedArfcn(cellInfo),
                    tac = null,
                    lac = getUpdatedIntValue(cellInfo.cellIdentity.lac)
                )
            }
            ELDeviceUtils.isQDeviceOrHigher() && cellInfo is CellInfoNr -> {
                val cellIdentity = (cellInfo.cellIdentity as CellIdentityNr)
                return CoverageCellIdentity(
                    cellId = getUpdatedLongValue(cellIdentity.nci),
                    cellInfoDelay = getCellInfoDelay(cellInfo),
                    networkName = getNetworkName(),
                    mcc = getUpdatedMcc(cellInfo),
                    mnc = getUpdatedMnc(cellInfo),
                    earfcn = getUpdatedArfcn(cellInfo),
                    tac = getUpdatedIntValue(cellIdentity.tac),
                    lac = null
                )
            }
        }

        // returning default object
        return CoverageCellIdentity()
    }


    /**
     * Retrieves object of CoverageCellSignalStrengthEntity
     *
     */
    private fun getCellSignalStrength(cellInfo: CellInfo): CoverageCellSignalStrength {
        when {
            cellInfo is CellInfoGsm -> {
                return CoverageCellSignalStrength(
                    asu = getUpdatedIntValue(cellInfo.cellSignalStrength.asuLevel),
                    dBm = getUpdatedIntValue(cellInfo.cellSignalStrength.dbm),
                    bandwidth = getBandWidth(cellInfo),
                    rsrp = "",                       // Not required for GSM network
                    rsrq = "",                       // Not required for GSM network
                    rssnr = null,                   // Not required for GSM network
                    cqi = null,                     // Not required for GSM network
                    timingAdvance = getUpdatedIntValue(cellInfo.cellSignalStrength.timingAdvance)
                )
            }
            cellInfo is CellInfoCdma -> {
                // No parsing is required for CDMA network, will return default object
            }
            cellInfo is CellInfoLte -> {
                return CoverageCellSignalStrength(
                    asu = getUpdatedIntValue(cellInfo.cellSignalStrength.asuLevel),
                    dBm = getUpdatedIntValue(cellInfo.cellSignalStrength.dbm),
                    bandwidth = getBandWidth(cellInfo),
                    rsrp = getUpdatedIntValue(cellInfo.cellSignalStrength.rsrp),
                    rsrq = getUpdatedIntValue(cellInfo.cellSignalStrength.rsrq),
                    rssnr = getUpdatedIntValue(cellInfo.cellSignalStrength.rssnr),
                    cqi = getUpdatedIntValue(cellInfo.cellSignalStrength.cqi),
                    timingAdvance = getUpdatedIntValue(cellInfo.cellSignalStrength.timingAdvance)
                )
            }
            cellInfo is CellInfoWcdma -> {
                // No parsing is required for WCDMA network, will return default object
            }
            ELDeviceUtils.isQDeviceOrHigher() && cellInfo is CellInfoTdscdma -> {
//                 supported in Android Q
                return CoverageCellSignalStrength(
                    asu = getUpdatedIntValue(cellInfo.cellSignalStrength.asuLevel),
                    dBm = getUpdatedIntValue(cellInfo.cellSignalStrength.dbm),
                    bandwidth = getBandWidth(cellInfo),
                    rsrp = "",
                    rsrq = "",
                    rssnr = null,
                    cqi = null,
                    timingAdvance = null
                )
            }
            ELDeviceUtils.isQDeviceOrHigher() && cellInfo is CellInfoNr -> {
//               supported in Android Q
                val cellSignalStrength = cellInfo.cellSignalStrength as CellSignalStrengthNr
                return CoverageCellSignalStrength(
                    asu = getUpdatedIntValue(cellSignalStrength.asuLevel),
                    dBm = getUpdatedIntValue(cellSignalStrength.dbm),
                    bandwidth = getBandWidth(cellInfo),
                    rsrp = getUpdatedIntValue(cellSignalStrength.ssRsrp),
                    rsrq = getUpdatedIntValue(cellSignalStrength.ssRsrq),
                    rssnr = getUpdatedIntValue(cellSignalStrength.ssSinr),
                    cqi = null,
                    timingAdvance = null
                )
            }
        }

        // returning default object
        return CoverageCellSignalStrength()
    }

    @Suppress("DEPRECATION")
    private fun getUpdatedMcc(cellInfo: CellInfo) : String {
        val mccValue =
            if (cellInfo is CellInfoGsm) {
                if (ELDeviceUtils.isPieDeviceOrHigher())
                    cellInfo.cellIdentity.mccString?.toInt()
                else
                    cellInfo.cellIdentity.mcc
            } else if (cellInfo is CellInfoCdma) {
                return CoverageUtils.EMPTY_STRING
            } else if (cellInfo is CellInfoLte) {
                if (ELDeviceUtils.isPieDeviceOrHigher())
                    cellInfo.cellIdentity.mccString?.toInt()
                else
                    cellInfo.cellIdentity.mcc
            } else if (cellInfo is CellInfoWcdma) {
                if (ELDeviceUtils.isPieDeviceOrHigher())
                    cellInfo.cellIdentity.mccString?.toInt()
                else
                    cellInfo.cellIdentity.mcc
            } else if (ELDeviceUtils.isQDeviceOrHigher() && cellInfo is CellInfoTdscdma) {
                // supported in Android Q
                cellInfo.cellIdentity.mccString?.toInt()
            } else if (ELDeviceUtils.isQDeviceOrHigher() && cellInfo is CellInfoNr) {
                val cellIdentityNr = cellInfo.cellIdentity as CellIdentityNr
                cellIdentityNr.mccString?.toInt()
            } else {
                return CoverageUtils.EMPTY_STRING
            }

        return if (mccValue == null) CoverageUtils.EMPTY_STRING
            else getUpdatedIntValue(mccValue)
    }

    @Suppress("DEPRECATION")
    private fun getUpdatedMnc(cellInfo: CellInfo) : String {
        val mncValue =
            if (cellInfo is CellInfoGsm) {
                if (ELDeviceUtils.isPieDeviceOrHigher())
                    cellInfo.cellIdentity.mncString?.toInt()
                else
                    cellInfo.cellIdentity.mnc
            } else if (cellInfo is CellInfoCdma) {
                return CoverageUtils.EMPTY_STRING
            } else if (cellInfo is CellInfoLte) {
                if (ELDeviceUtils.isPieDeviceOrHigher())
                    cellInfo.cellIdentity.mncString?.toInt()
                else
                    cellInfo.cellIdentity.mnc
            } else if (cellInfo is CellInfoWcdma) {
                if (ELDeviceUtils.isPieDeviceOrHigher())
                    cellInfo.cellIdentity.mncString?.toInt()
                else
                    cellInfo.cellIdentity.mnc
            } else if (ELDeviceUtils.isQDeviceOrHigher() && cellInfo is CellInfoTdscdma) {
                cellInfo.cellIdentity.mncString?.toInt()
            } else if (ELDeviceUtils.isQDeviceOrHigher() && cellInfo is CellInfoNr) {
                val infoIdentityNr = cellInfo.cellIdentity as CellIdentityNr
                infoIdentityNr.mncString?.toInt()
            } else {
                return CoverageUtils.EMPTY_STRING
            }

        return if (mncValue == null) CoverageUtils.EMPTY_STRING
            else getUpdatedIntValue(mncValue)
    }

    private fun getUpdatedIntValue(value: Int) : String {
        return if (value != Int.MAX_VALUE)
            value.toString()
        else
            CoverageUtils.EMPTY_STRING
    }

    private fun getUpdatedLongValue(value: Long) : String {
        return if (value != Long.MAX_VALUE)
            value.toString()
        else
            CoverageUtils.EMPTY_STRING
    }

    private fun getCellInfoDelay(cellInfo: CellInfo) : String {
        return (TimeUnit.SECONDS.convert(
            SystemClock.elapsedRealtimeNanos() - cellInfo.timeStamp,
            TimeUnit.NANOSECONDS
        )).toString()

    }

    private fun getUpdatedArfcn(cellInfo: CellInfo) : String {
        val arfcnValue =
            if (cellInfo is CellInfoGsm) {
                return CoverageUtils.EMPTY_STRING
            } else if (cellInfo is CellInfoCdma) {
                return CoverageUtils.EMPTY_STRING
            } else if (cellInfo is CellInfoLte) {
                val cellIdentityLte = (cellInfo as CellInfoLte).cellIdentity
                cellIdentityLte.earfcn
            } else if (cellInfo is CellInfoWcdma) {
                val cellIdentityWcdma = (cellInfo as CellInfoWcdma).cellIdentity
                cellIdentityWcdma.uarfcn
            } else if (ELDeviceUtils.isQDeviceOrHigher() && cellInfo is CellInfoTdscdma) {
                cellInfo.cellIdentity.uarfcn
            } else if (ELDeviceUtils.isQDeviceOrHigher() && cellInfo is CellInfoNr) {
                val cellIdentityNr = cellInfo.cellIdentity as CellIdentityNr
                cellIdentityNr.nrarfcn
            } else {
                return CoverageUtils.EMPTY_STRING
            }

        return getUpdatedIntValue(arfcnValue)
    }

    private fun getNetworkName() : String? {
        return tm?.networkOperatorName
    }

    /**
     * This method is used for get bandwidth
     */
    private fun getBandWidth(cellInfo: CellInfo) : String? {
        if (cellInfo is CellInfoLte) {
            val cellIdentityLte: CellIdentityLte = cellInfo.cellIdentity
            return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                cellIdentityLte.bandwidth.toString()
            } else {
                return null
            }
        }
        return null
    }

}