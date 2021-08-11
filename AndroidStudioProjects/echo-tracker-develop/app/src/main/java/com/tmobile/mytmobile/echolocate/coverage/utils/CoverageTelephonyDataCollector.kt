package com.tmobile.mytmobile.echolocate.coverage.utils

/**
 * Created by Mahesh Shetye on 2020-05-06
 *
 * Helper class that contains utilities methods related to telephony module
 *
 */

import android.content.Context
import android.telephony.TelephonyManager
import android.telephony.ServiceState
import android.text.TextUtils
import com.tmobile.mytmobile.echolocate.coverage.model.CoveragePrimaryCell
import com.tmobile.mytmobile.echolocate.utils.SingletonHolder
import com.tmobile.pr.androidcommon.system.SystemService

class CoverageTelephonyDataCollector private constructor(private val context: Context) {

    private val tm: TelephonyManager?

    private val ROAMING_NETWORK_DOMESTIC = "DOMESTIC"
    private val ROAMING_NETWORK_INTERNATIONAL = "INTERNATIONAL"

    // Below are the hidden network type
    private val NETWORK_TYPE_LTE_CA: Int = 19
    private val NETWORK_TYPE_NR: Int = 20


    companion object : SingletonHolder<CoverageTelephonyDataCollector, Context>(::CoverageTelephonyDataCollector)

    init {
        tm = SystemService.getTelephonyManager(context)
    }

    enum class SIM_STATE {
        SIM_STATE_UNKNOWN,           // = 0
        SIM_STATE_ABSENT,            // = 1
        SIM_STATE_PIN_REQUIRED,      // = 2
        SIM_STATE_PUK_REQUIRED,      // = 3
        SIM_STATE_NETWORK_LOCKED,    // = 4
        SIM_STATE_READY,             // = 5
        SIM_STATE_NOT_READY,         // = 6
        SIM_STATE_PERM_DISABLED,     // = 7
        SIM_STATE_CARD_IO_ERROR,     // = 8
        SIM_STATE_CARD_RESTRICTED    // = 9
    }

    enum class NETWORK_TYPE {
        NETWORK_TYPE_UNKNOWN,        // = 0
        NETWORK_TYPE_GPRS,           // = 1
        NETWORK_TYPE_EDGE,           // = 2
        NETWORK_TYPE_UMTS,           // = 3
        NETWORK_TYPE_CDMA,           // = 4
        NETWORK_TYPE_EVDO_0,         // = 5
        NETWORK_TYPE_EVDO_A,         // = 6
        NETWORK_TYPE_1xRTT,          // = 7
        NETWORK_TYPE_HSDPA,          // = 8
        NETWORK_TYPE_HSUPA,          // = 9
        NETWORK_TYPE_HSPA,           // = 10
        NETWORK_TYPE_IDEN,           // = 11
        NETWORK_TYPE_EVDO_B,         // = 12
        NETWORK_TYPE_LTE,            // = 13
        NETWORK_TYPE_EHRPD,          // = 14
        NETWORK_TYPE_HSPAP,          // = 15
        NETWORK_TYPE_GSM,            // = 16
        NETWORK_TYPE_TD_SCDMA,       // = 17
        NETWORK_TYPE_IWLAN,          // = 18
        NETWORK_TYPE_LTE_CA,         // = 19
        NETWORK_TYPE_NR              // = 20
    }

    enum class SERVICE_STATE {
        STATE_IN_SERVICE,            // = 0
        STATE_OUT_OF_SERVICE,        // = 1
        STATE_EMERGENCY_ONLY,        // = 2
        STATE_POWER_OFF              // = 3
    }

    /**
     * Retrieves SIM state name.
     *
     * @return SIM state name from [SIM_STATE]
     */
    fun getSimState(): String? {
        if(tm == null) {
            return null
        }

        return when (tm.simState) {
            TelephonyManager.SIM_STATE_ABSENT          -> SIM_STATE.SIM_STATE_ABSENT.name
            TelephonyManager.SIM_STATE_PIN_REQUIRED    -> SIM_STATE.SIM_STATE_PIN_REQUIRED.name
            TelephonyManager.SIM_STATE_PUK_REQUIRED    -> SIM_STATE.SIM_STATE_PUK_REQUIRED.name
            TelephonyManager.SIM_STATE_NETWORK_LOCKED  -> SIM_STATE.SIM_STATE_NETWORK_LOCKED.name
            TelephonyManager.SIM_STATE_READY           -> SIM_STATE.SIM_STATE_READY.name
            TelephonyManager.SIM_STATE_NOT_READY       -> SIM_STATE.SIM_STATE_NOT_READY.name
            TelephonyManager.SIM_STATE_PERM_DISABLED   -> SIM_STATE.SIM_STATE_PERM_DISABLED.name
            TelephonyManager.SIM_STATE_CARD_IO_ERROR   -> SIM_STATE.SIM_STATE_CARD_IO_ERROR.name
            TelephonyManager.SIM_STATE_CARD_RESTRICTED -> SIM_STATE.SIM_STATE_CARD_RESTRICTED.name
            else -> null
        }
    }

    /**
     * Retrieves status for roaming
     *
     * @return
     * ROAMING_NETWORK_DOMESTIC : connected to home network
     * ROAMING_NETWORK_INTERNATIONAL : connected to roaming network
     *
     */
    fun getRoamingNetwork(): String? {
        if(tm == null) {
            return null
        }

        val networkCountryIso: String = tm.networkCountryIso
        val simCountryIso: String = tm.simCountryIso

        return if (!TextUtils.isEmpty(simCountryIso)) {
            if (simCountryIso == networkCountryIso) ROAMING_NETWORK_DOMESTIC else ROAMING_NETWORK_INTERNATIONAL
        } else null
    }

    /**
     * Checks if current voice network is in roaming
     *
     * @return
     * True : if device is in roaming network,
     * False otherwise.
     *
     */
    fun isRoamingVoice(): String? {
        return if (tm != null) tm.isNetworkRoaming.toString() else null
    }

    /**
     * Retrieves network type
     *
     * @return netowrk type name from [NETWORK_TYPE]
     *
     */
    fun getNetworkType(): String? {
        if(tm == null) {
            return null
        }

        val nwType: Int = tm.networkType

        return when (nwType) {
            TelephonyManager.NETWORK_TYPE_UNKNOWN  -> NETWORK_TYPE.NETWORK_TYPE_UNKNOWN.name
            TelephonyManager.NETWORK_TYPE_GPRS     -> NETWORK_TYPE.NETWORK_TYPE_GPRS.name
            TelephonyManager.NETWORK_TYPE_EDGE     -> NETWORK_TYPE.NETWORK_TYPE_EDGE.name
            TelephonyManager.NETWORK_TYPE_UMTS     -> NETWORK_TYPE.NETWORK_TYPE_UMTS.name
            TelephonyManager.NETWORK_TYPE_CDMA     -> NETWORK_TYPE.NETWORK_TYPE_CDMA.name
            TelephonyManager.NETWORK_TYPE_EVDO_0   -> NETWORK_TYPE.NETWORK_TYPE_EVDO_0.name
            TelephonyManager.NETWORK_TYPE_EVDO_A   -> NETWORK_TYPE.NETWORK_TYPE_EVDO_A.name
            TelephonyManager.NETWORK_TYPE_1xRTT    -> NETWORK_TYPE.NETWORK_TYPE_1xRTT.name
            TelephonyManager.NETWORK_TYPE_HSDPA    -> NETWORK_TYPE.NETWORK_TYPE_HSDPA.name
            TelephonyManager.NETWORK_TYPE_HSUPA    -> NETWORK_TYPE.NETWORK_TYPE_HSUPA.name
            TelephonyManager.NETWORK_TYPE_HSPA     -> NETWORK_TYPE.NETWORK_TYPE_HSPA.name
            TelephonyManager.NETWORK_TYPE_IDEN     -> NETWORK_TYPE.NETWORK_TYPE_IDEN.name
            TelephonyManager.NETWORK_TYPE_EVDO_B   -> NETWORK_TYPE.NETWORK_TYPE_EVDO_B.name
            TelephonyManager.NETWORK_TYPE_LTE      -> NETWORK_TYPE.NETWORK_TYPE_LTE.name
            TelephonyManager.NETWORK_TYPE_EHRPD    -> NETWORK_TYPE.NETWORK_TYPE_EHRPD.name
            TelephonyManager.NETWORK_TYPE_HSPAP    -> NETWORK_TYPE.NETWORK_TYPE_HSPAP.name
            TelephonyManager.NETWORK_TYPE_GSM      -> NETWORK_TYPE.NETWORK_TYPE_GSM.name
            TelephonyManager.NETWORK_TYPE_TD_SCDMA -> NETWORK_TYPE.NETWORK_TYPE_TD_SCDMA.name
            TelephonyManager.NETWORK_TYPE_IWLAN    -> NETWORK_TYPE.NETWORK_TYPE_IWLAN.name
//            TelephonyManager.NETWORK_TYPE_LTE_CA   -> NETWORK_TYPE.NETWORK_TYPE_LTE_CA.name
//            TelephonyManager.NETWORK_TYPE_NR       -> NETWORK_TYPE.NETWORK_TYPE_NR.name
            // Above 2 types are declared as hidden in TelephonyManager, so we are adding local mapping as below.
            NETWORK_TYPE_LTE_CA                    -> NETWORK_TYPE.NETWORK_TYPE_LTE_CA.name
            NETWORK_TYPE_NR                        -> NETWORK_TYPE.NETWORK_TYPE_NR.name
            else -> nwType.toString()
        }
    }

    /**
     * Retrieves service state
     *
     * @return service state name from [SERVICE_STATE]
     *
     */
    fun getServiceState(): String? {
        val servState = DataCollectionService().getServiceState(context) ?: return null

        return when (servState.state) {
            ServiceState.STATE_IN_SERVICE      -> SERVICE_STATE.STATE_IN_SERVICE.name
            ServiceState.STATE_OUT_OF_SERVICE  -> SERVICE_STATE.STATE_OUT_OF_SERVICE.name
            ServiceState.STATE_EMERGENCY_ONLY  -> SERVICE_STATE.STATE_EMERGENCY_ONLY.name
            ServiceState.STATE_POWER_OFF       -> SERVICE_STATE.STATE_POWER_OFF.name
            else -> null
        }
    }

    /**
     * Refresh the cell information and stores in cache
     *
     */
    fun refreshCellData() {
        val cm = CellsMonitor.getInstance(context)
        cm.refreshCellsMonitorData()
    }

    /**
     * Retrieves primary cell if available
     *
     */
    fun getPrimaryCell(): CoveragePrimaryCell? {
        val cm = CellsMonitor.getInstance(context)
        return cm.getPrimaryCell()
    }

}
