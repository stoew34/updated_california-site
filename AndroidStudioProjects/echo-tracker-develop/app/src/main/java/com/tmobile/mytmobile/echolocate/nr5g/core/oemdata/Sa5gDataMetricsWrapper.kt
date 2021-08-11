package com.tmobile.mytmobile.echolocate.nr5g.core.oemdata

import android.content.Context
import androidx.annotation.Nullable
import com.tmobile.mytmobile.echolocate.nr5g.sa5g.utils.Sa5gEntityConverter
import com.tmobile.mytmobile.echolocate.utils.EchoLocateLog
import com.tmobile.pr.androidcommon.system.reflection.TmoBaseReflection
import java.lang.reflect.InvocationTargetException
import java.util.*

/**
 * Class used to deliver Sa5g data metrics from Echo Locate custom API.
 * It's collect data that is not available from the current native Android SW.
 */
class Sa5gDataMetricsWrapper constructor(context: Context) {

    companion object {
        private const val DATA_METRICS_5GSA_CLASS = "com.tmobile.echolocate.DataMetrics5gSa"
        private const val GET_API_VERSION_METHOD = "getApiVersion"
        private const val GET_DOWNLINK_CARRIER_LOG_METHOD = "getDlCarrierLog"
        private const val GET_UPLINK_CARRIER_LOG_METHOD = "getUlCarrierLog"
        private const val GET_RRC_LOG_METHOD = "getRrcLog"
        private const val GET_NETWORK_LOG_METHOD = "getNetworkLog"
        private const val GET_SETTING_LOG_METHOD = "getSettingsLog"
        private const val GET_UI_LOG_METHOD = "getUiLog"
    }

    private lateinit var dataMetricsClass: Class<Any>
    private lateinit var dataMetricsInstance: Any
    private var isDataMetricsAvailable: Boolean = false

    init {
        /*** Creates instance of wrapper for {@value DATA_METRICS_CLASS} */
        initDataMetrics(context,
            DATA_METRICS_5GSA_CLASS
        )
    }

    /**
     * @return true if {@value DATA_METRICS_CLASS} is available, false otherwise
     */
    fun isDataMetricsAvailable(): Boolean {
        return isDataMetricsAvailable
    }

    /**
     * Creates instance of wrapper for specified class
     *
     * @param context                       app context
     * @param dataMetricsCanonicalClassName class path used to mock library instance. Canonical
     *                                      class name
     */
    private fun initDataMetrics(context: Context, dataMetricsCanonicalClassName: String) {
        try {
            this.dataMetricsClass = TmoBaseReflection.findClassByName(dataMetricsCanonicalClassName) as Class<Any>
        } catch (e: ClassNotFoundException) {
            EchoLocateLog.eLogV("Diagnostic $dataMetricsCanonicalClassName is unavailable ")
            this.isDataMetricsAvailable = false
            return
        }
        try {
            this.dataMetricsInstance =
                TmoBaseReflection.newInstance(
                    TmoBaseReflection.getConstructor(
                        dataMetricsClass,
                        Context::class.java
                    ), context
                )
        } catch (e: InstantiationException) {
            EchoLocateLog.eLogE("Exception: $e")
            this.isDataMetricsAvailable = false
            return
        } catch (e: IllegalAccessException) {
            EchoLocateLog.eLogE("Exception: $e")
            this.isDataMetricsAvailable = false
            return
        } catch (e: NoSuchMethodException) {
            EchoLocateLog.eLogE("Exception: $e")
            this.isDataMetricsAvailable = false
            return
        } catch (e: InvocationTargetException) {
            EchoLocateLog.eLogE("Exception: $e")
            this.isDataMetricsAvailable = false
            return
        }
        this.isDataMetricsAvailable = true
    }

    /**
     * @return List of Objects from DataMetrics
     */
    @SuppressWarnings("unchecked")
    fun invokeDataMetricsMethodReturnObjectList(method: String): Any? {
        try {
            return invokeDataMetricsMethodReturnObject(method)
                ?: return Collections.emptyList<Any?>()

        } catch (e: Throwable) {
            EchoLocateLog.eLogE(
                "" +
                        "Diagnostics :Nr5g  $method, " +
                        "Exception: ${e.javaClass.simpleName}, " +
                        "Message: ${e.message}"
            )
        }
        return Collections.emptyList<Any?>()
    }

    /**
     * @return Object from DataMetrics
     */
    @Nullable
    fun invokeDataMetricsMethodReturnObject(method: String): Any? {
        if (!isDataMetricsAvailable) {
            return null
        }
        try {
            val result: Any = TmoBaseReflection.invokeMethod(
                TmoBaseReflection.findMethod(
                    dataMetricsClass,
                    method
                ), dataMetricsInstance
            ) as Any
            EchoLocateLog.eLogD("Diagnostic $method returns: $result")

            return result

        } catch (e: Throwable) {
            EchoLocateLog.eLogE(
                "" +
                        "Diagnostics :Nr5g  $method, " +
                        "Exception: ${e.javaClass.simpleName}, " +
                        "Message: ${e.message}"
            )
        }
        return null
    }

    /**
     * Get API version. Invoke {@value GET_API_VERSION_METHOD} from {@value DATA_METRICS_CLASS}
     *
     * @return api version or ApiVersion.UNKNOWN_VERSION if not available
     */
    fun getApiVersion(): ApiVersion {
        return ApiVersion.valueByCode(
            invokeDataMetricsMethodReturnObject(
                GET_API_VERSION_METHOD
            )
        )
    }

    /**
     * Data Metrics API library version
     */
    enum class ApiVersion constructor(
        /*** Version as integer*/
        val intCode: Int,
        /*** Version as string*/
        val stringCode: String
    ) {
        UNKNOWN_VERSION(-1, "0.9.3"),
        VERSION_1(1, "1"),
        VERSION_2(2, "2");

        companion object {
            /**
             * @param code code used to search ApiVersion
             * @return ApiVersion based on int code or UNKNOWN_VERSION if code doesn't exist
             */
            fun valueByCode(code: Any?): ApiVersion {
                if (code is Int) {
                    for (apiVersion in values()) {
                        if (apiVersion.intCode == code) {
                            return apiVersion
                        }
                    }
                } else if (code is String) {
                    for (apiVersion in values()) {
                        if (apiVersion.stringCode == code) {
                            return apiVersion
                        }
                    }
                }
                return UNKNOWN_VERSION
            }
        }
    }

    /**
     * Get list of Downlink Carrier log. Invoke {@value GET_DOWNLINK_CARRIER_LOG_METHOD} from {@value DATA_METRICS_5GSA_CLASS}
     *
     * @return Downlink Carrier log is a collection of DlCarrierLog objects. In order:
     *
     *  *  techType
     *  *  bandNumber
     *  *  arfcn
     *  *  bandWidth
     *  *  isPrimary
     *  *  isEndcAnchor
     *  *  modulationType
     *  *  transmissionMode
     *  *  numberLayers
     *  *  cellId
     *  *  pci
     *  *  tac
     *  *  lac
     *  *  rsrp
     *  *  rsrq
     *  *  rssi
     *  *  rscp
     *  *  sinr
     *  *  csiRsrp
     *  *  csiRsrq
     *  *  csiRssi
     *  *  csiSinr
     *
     */
    fun getDlCarrierLog(): Any? {
        return invokeDataMetricsMethodReturnObjectList(GET_DOWNLINK_CARRIER_LOG_METHOD)
    }

    /**
     * Get list of Uplink Carrier log. Invoke GET_UPLINK_CARRIER_LOG_METHOD from {@value DATA_METRICS_5GSA_CLASS}
     *
     * @return Uplink Carrier log is a collection of UlCarrierLog objects.
     *
     *  *  techType
     *  *  bandNumber
     *  *  arfcn
     *  *  bandWidth
     *  *  isPrimary
     *
     */
    fun getUlCarrierLog(): Any? {
        return invokeDataMetricsMethodReturnObjectList(GET_UPLINK_CARRIER_LOG_METHOD)
    }

    /**
     * Get RRC log. Invoke GET_RRC_LOG_METHOD from {@value DATA_METRICS_5GSA_CLASS}
     *
     * @return RRC log RrcLog object.
     *
     *  *  lteRrcState
     *  *  nrRrcState
     *
     */
    fun getRrcLog(): Any? {
        return Sa5gEntityConverter.convertRrcLogToEntity(
            invokeDataMetricsMethodReturnObject(
                GET_RRC_LOG_METHOD
            )
        )
    }

    /**
     * Get Network log. Invoke GET_NETWORK_LOG_METHOD from {@value DATA_METRICS_5GSA_CLASS}
     *
     * @return Network log NetworkLog object.
     *
     *  *  mcc
     *  *  mnc
     *  *  endcCapability
     *  *  endcConnectionStatus
     *
     */
    fun getNetworkLog(): Any? {
        return Sa5gEntityConverter.convertNetworkLogToEntity(
            invokeDataMetricsMethodReturnObject(
                GET_NETWORK_LOG_METHOD
            )
        )
    }

    /**
     * Get Settings log. Invoke GET_SETTING_LOG_METHOD from {@value DATA_METRICS_5GSA_CLASS}
     *
     * @return Settings log SettingsLog object.
     *
     *  *  wifiCalling
     *  *  wifi
     *  *  roaming
     *  *  rtt
     *  *  rttTranscript
     *  *  networkMode
     *
     */
    fun getSettingsLog(): Any? {
        return Sa5gEntityConverter.convertSettingsLogToEntity(
            invokeDataMetricsMethodReturnObject(
                GET_SETTING_LOG_METHOD
            )
        )
    }

    /**
     * Get UI log. Invoke [GET_UI_LOG_METHOD] from {@value DATA_METRICS_5GSA_CLASS}
     *
     * @return UI log UiLog object.
     *
     *  *  timestamp
     *  *  networkType
     *  *  uiNetworkType
     *  *  uiDataTransmission
     *  *  uiNumberOfAntennaBars
     *
     */
    fun getUiLog(): Any? {
        return Sa5gEntityConverter.convertUiLogToEntity(
            invokeDataMetricsMethodReturnObject(
                GET_UI_LOG_METHOD
            )
        )
    }

    /**
     * UE in API version 2 shall return the file version of the carrier configuration on the UE.
     *
     * @return carrierConfigVersion: The value of the version tag under the configInfo XML tag shall be reported
     * "-1": If the version tag under the configInfo XML tag doesn't exists, report "-1". Also, if the UE doesn't have the carrier configuration XML file, report "-1".
     * "-2": If there was an internal SW function call
     * failure or a SW exception occurred in
     * getting this value, return -2.
     */
    fun getCarrierConfig(): Any? {
        return Sa5gEntityConverter.convertCarrierConfigToEntity(
            invokeDataMetricsMethodReturnObject(
                GET_SETTING_LOG_METHOD
            )
        )
    }
}