package com.tmobile.mytmobile.echolocate.playground.viewmodel

import android.annotation.SuppressLint
import android.net.ConnectivityManager
import android.os.Build
import android.telephony.*
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tmobile.mytmobile.echolocate.coverage.model.CoverageNrCell
import com.tmobile.mytmobile.echolocate.utils.ELDeviceUtils
import com.tmobile.mytmobile.echolocate.utils.EchoLocateLog

class TestPublicAPIViewModel : ViewModel() {

    private val _result = MutableLiveData<String>()
    val result: LiveData<String>
        get() = _result

    init {
        _result.value = ""
        EchoLocateLog.eLogD("TestPublicAPI View Model Created")
    }

    fun clearText() {
        _result.value = ""
    }

    @SuppressLint("MissingPermission", "NewApi", "SetTextI18n")
    fun getCellInfo(telephonyManager: TelephonyManager) {
        if (ELDeviceUtils.isQDeviceOrHigher()) {
            try {
                val nrCell = telephonyManager.allCellInfo?.find {
                    it is CellInfoNr
                } as? CellInfoNr

                nrCell?.let {
                    val signalStrengthNr = it.cellSignalStrength as CellSignalStrengthNr
                    val signalIdentityNr = it.cellIdentity as CellIdentityNr
                    _result.value =
                        "Cell Identity - $signalIdentityNr \n\n" +
                                "Cell Signal Strength -  $signalStrengthNr"
                    return
                }
                resultNotFound(telephonyManager)
            } catch (e: Exception) {
                EchoLocateLog.eLogD("--nr Error getting NrCell data - $e")
                _result.value = "Exception in getting Cell Info may be permission " +
                        "issue or API issue"
            }
        } else {
            _result.value = "Device Version is below Android Q"
        }
    }

    /**
     * This method is used to get cell signal strength
     */
    @SuppressLint("MissingPermission", "NewApi", "SetTextI18n")
    fun getCellSignalStrength(telephonyManager : TelephonyManager) {
        if (ELDeviceUtils.isQDeviceOrHigher()) {
            try {
                val nrCell = telephonyManager.allCellInfo?.find { it is CellInfoNr } as? CellInfoNr
                val coverageNrCell = CoverageNrCell()
                nrCell?.let {
                    val signalStrengthNr = it.cellSignalStrength as CellSignalStrengthNr
                    val processedCoverageCell = processStrengthNr(signalStrengthNr, coverageNrCell)
                    _result.value = "CellSignalStrength : "+
                            "AsuLevel : ${processedCoverageCell.nrAsuLevel}"+"\n"+
                            "CsiRsrp : ${processedCoverageCell.nrCsiRsrp}"+"\n"+
                            "CsiRsrq : ${processedCoverageCell.nrCsiRsrq}"+"\n"+
                            "CsiSinr : ${processedCoverageCell.nrCsiSinr}"+"\n"+
                            "Dbm : ${processedCoverageCell.nrDbm}"+"\n"+
                            "Level : ${processedCoverageCell.nrLevel}"+"\n"+
                            "SsRsrp : ${processedCoverageCell.nrSsRsrp}"+"\n"+
                            "SsRsrq : ${processedCoverageCell.nrSsRsrq}"+"\n"+
                            "SsSinr : ${processedCoverageCell.nrSsSinr}"
                    return
                }
                resultNotFound(telephonyManager)
            } catch (e: Exception) {
                EchoLocateLog.eLogD("--nr Error getting NrCell data - $e")
                _result.value = "Exception in getting Cell Signal Strength may be permission " +
                        "issue or API issue"
            }
        } else {
            _result.value = "Device Version is below Android Q"
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun processStrengthNr(strength: CellSignalStrengthNr, coverageCell: CoverageNrCell): CoverageNrCell {
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

        return coverageCell
    }

    @SuppressLint("MissingPermission", "NewApi", "SetTextI18n")
    fun getCellIdentity(telephonyManager : TelephonyManager) {
        if (ELDeviceUtils.isQDeviceOrHigher()) {
            try {
                val nrCell = telephonyManager.allCellInfo?.find {
                    it is CellInfoNr
                } as? CellInfoNr

                nrCell?.let {
                    val signalIdentityNr = it.cellIdentity as CellIdentityNr
                    var cellIdentityResult = "Cell Identity : \n " +
                            "MCC - ${signalIdentityNr.mccString}\n " +
                            "MNC - ${signalIdentityNr.mncString}\n " +
                            "Nci - ${signalIdentityNr.nci}\n " +
                            "Nrarfcn - ${signalIdentityNr.nrarfcn}\n " +
                            "Pci - ${signalIdentityNr.pci}\n " +
                            "Tac - ${signalIdentityNr.tac}\n " +
                            "Bands - {"
                    for (i in signalIdentityNr.bands) {
                        cellIdentityResult += "$i, "
                    }
                    cellIdentityResult += "}"
                    _result.value = cellIdentityResult
                    return
                }
                resultNotFound(telephonyManager)
            } catch (e: Exception) {
                EchoLocateLog.eLogD("--nr Error getting NrCell data - $e")
                _result.value = "Exception in getting Cell Info may be permission " +
                        "issue or API issue"
            }
        } else {
            _result.value = "Device Version is below Android Q"
        }
    }

    @SuppressLint("SetTextI18n")
    fun getUplinkAndDownlinkBandwidthSpeed(connectivityManager : ConnectivityManager) {
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        _result.value = "Network Uplink Bandwidth Speed = " + capabilities?.linkUpstreamBandwidthKbps + " Kbps\n" +
                    "Network Downlink Bandwidth Speed = " + capabilities?.linkDownstreamBandwidthKbps + " Kbps"
    }

    @SuppressLint("MissingPermission", "NewApi", "SetTextI18n")
    fun getDataNetworkType(telephonyManager: TelephonyManager) {
        if (ELDeviceUtils.isRDeviceOrHigher()) {
            try {
                val networkTypes = mapOf(
                    Pair(0, "NETWORK_TYPE_UNKNOWN"),
                    Pair(1, "NETWORK_TYPE_GPRS"),
                    Pair(2, "NETWORK_TYPE_EDGE"),
                    Pair(3, "NETWORK_TYPE_UMTS"),
                    Pair(4, "NETWORK_TYPE_HSDPA"),
                    Pair(5, "NETWORK_TYPE_HSUPA"),
                    Pair(6, "NETWORK_TYPE_HSPA"),
                    Pair(7, "NETWORK_TYPE_CDMA"),
                    Pair(8, "NETWORK_TYPE_EVDO_0"),
                    Pair(9, "NETWORK_TYPE_EVDO_A"),
                    Pair(10, "NETWORK_TYPE_EVDO_B"),
                    Pair(11, "NETWORK_TYPE_1xRTT"),
                    Pair(12, "NETWORK_TYPE_IDEN"),
                    Pair(13, "NETWORK_TYPE_LTE"),
                    Pair(14, "NETWORK_TYPE_EHRPD"),
                    Pair(15, "NETWORK_TYPE_HSPAP"),
                    Pair(16, "NETWORK_TYPE_GSM"),
                    Pair(17, "NETWORK_TYPE_TD_SCDMA"),
                    Pair(18, "NETWORK_TYPE_IWLAN"),
                    Pair(19, "NETWORK_TYPE_LTE_CA"),
                    Pair(20, "NETWORK_TYPE_NR"))
                _result.value = networkTypes[telephonyManager.dataNetworkType]
            } catch (e: Exception) {
                EchoLocateLog.eLogD("--nr Error getting NrCell data - $e")
                _result.value = "Exception in getting Cell Info may be permission " +
                        "issue or API issue"
            }
        } else {
            _result.value = "Device Version is below Android R"
        }
    }

    @SuppressLint("MissingPermission", "NewApi", "SetTextI18n")
    fun getNetworkType5g(telephonyManager: TelephonyManager) {
        if (ELDeviceUtils.isRDeviceOrHigher()) {
            try {
                val netInfoList = telephonyManager.serviceState.networkRegistrationInfoList
                var data = ""
                for (netInfo in netInfoList) {
                    data = "Access Network Technology - ${netInfo?.accessNetworkTechnology}\n" +
                            "Available Service - "
                    for (i in netInfo.availableServices) {
                        data += "$i, "
                    }
                    data += "\nRegistered PLMN - ${netInfo?.registeredPlmn}\n"
                }
                _result.value = data
            } catch (e: Exception) {
                EchoLocateLog.eLogD("--nr Error getting NrCell data - $e")
                _result.value = "Exception in getting Cell Info may be permission " +
                        "issue or API issue"
            }
        } else {
            _result.value = "Device Version is below Android R"
        }
    }

    @SuppressLint("MissingPermission", "NewApi", "SetTextI18n")
    private fun resultNotFound(telephonyManager: TelephonyManager) {
        if (telephonyManager.dataNetworkType >= TelephonyManager.NETWORK_TYPE_NR) {
            _result.value = "API Not implemented"
        } else {
            _result.value = "Device not connected to 5G network, " +
                    "Please make sure device is connected to a 5G network and check again" +
                    " Data network type - ${telephonyManager.dataNetworkType}, if the Data" +
                    " network type value is 20 then device is connected to 5G " +
                    "or else LTE and below"
        }
    }


}