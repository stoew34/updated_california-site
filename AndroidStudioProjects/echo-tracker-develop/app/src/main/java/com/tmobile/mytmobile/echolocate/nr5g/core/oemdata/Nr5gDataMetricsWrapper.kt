package com.tmobile.mytmobile.echolocate.nr5g.core.oemdata

import android.content.Context
import com.tmobile.mytmobile.echolocate.nr5g.nsa5g.utils.Nsa5gEntityConverter

/**
 * Class used to deliver Nr5g data metrics from Echo Locate custom API. It's collect data that is
 * not available from the current native Android SW.
 */

class Nr5gDataMetricsWrapper constructor(ctx: Context) : Nr5gBaseDataMetricsWrapper(ctx) {

    companion object {

        //Nr5g Constants
        const val GET_5G_NR_MMW_CELL_LOG_METHOD = "get5gNrMmwCellLog"
        const val GET_5G_UI_LOG_METHOD = "get5gUiLog"
        const val GET_ENDC_UPLINK_LOG_METHOD = "getEndcUplinkLog"
        const val GET_ENDCLTELOG_METHOD = "getEndcLteLog"
        const val DATA_METRICS_CLASS = "com.tmobile.echolocate.DataMetrics"

    }

    init {
        /**
         * Creates instance of wrapper for {@value DATA_METRICS_CLASS}
         */
        initDataMetrics(ctx,
            DATA_METRICS_CLASS
        )
    }

    /**
     * Retruns the information about the 5G mmW NR PSCell and its signal condition on the connected SSB beam (wide beam for control data)
     * and the PDSCH beam (narrow beam for user data).
     * Will returns NrMmwCellLog with default values if device is not connected to a 5G NR Cell (5G NR RRC is in IDLE state),
     *
     */
    fun get5gNrMmwCellLog(): Any? {
        return Nsa5gEntityConverter.convertNr5gMmwCellLogEntityObject(invokeDataMetricsMethodReturnObject(
            GET_5G_NR_MMW_CELL_LOG_METHOD
        ))
    }

    /**
     * Returns the UI status information to understand how users would perceive their 5G coverage.
     * Also t the conditions detected by UE to decide on which type of network icon to display such as 5G vs. 4G LTE
     *
     */
    fun getNr5gUiLog(): Any? {
        return Nsa5gEntityConverter.convertNr5gUiLogEntityObject(invokeDataMetricsMethodReturnObject(
            GET_5G_UI_LOG_METHOD
        ))
    }

    /**
     * Get endcLteLog. Invoke {@value GET_ENDCLTELOG_METHOD} from {@value
     * DATA_METRICS_CLASS}
     *
     * @return EN-DC LTE log as [EndcLteLogEntity]
     */
    fun getEndcLteLog(): Any? {
        return Nsa5gEntityConverter.convertEndcLteLogEntityObject(invokeDataMetricsMethodReturnObject(
            GET_ENDCLTELOG_METHOD
        ))
    }

    /**
     * Returns the information about the uplink status in the 5G EN-DC connection.
     */
    fun getEndcUplinkLog(): Any? {
        return Nsa5gEntityConverter.convertEndcUpLinkLogEntityObject(invokeDataMetricsMethodReturnObject(
            GET_ENDC_UPLINK_LOG_METHOD
        ))
    }

}