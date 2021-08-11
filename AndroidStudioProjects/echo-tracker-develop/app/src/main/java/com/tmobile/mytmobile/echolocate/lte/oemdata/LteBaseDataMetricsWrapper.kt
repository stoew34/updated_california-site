package com.tmobile.mytmobile.echolocate.lte.oemdata

/**
 * Created by Divya Mittal on 4/12/21
 */
import android.content.Context
import androidx.annotation.Nullable
import com.tmobile.mytmobile.echolocate.utils.EchoLocateLog
import com.tmobile.pr.androidcommon.system.reflection.TmoBaseReflection
import java.lang.reflect.InvocationTargetException
import java.util.*

/**
 * Base class used to deliver data metrics.
 */

abstract class LteBaseDataMetricsWrapper constructor(context: Context) {

    companion object {
        const val DATA_METRICS_CLASS = "com.tmobile.echolocate.DataMetrics"

        const val GET_API_VERSION_METHOD = "getAPIversion"
        const val GET_NETWORK_IDENTITY_METHOD = "getNetworkIdentity"
    }

    private lateinit var dataMetricsClass: Class<Any>
    private lateinit var dataMetricsInstance: Any
    private var isDataMetricsAvailable: Boolean = false


    init {
        /**
         * Creates instance of wrapper for {@value DATA_METRICS_CLASS}
         */
        initDataMetrics(context, DATA_METRICS_CLASS)

    }

    /**
     * Creates instance of wrapper for specified class
     *
     * @param context                       app context
     * @param dataMetricsCanonicalClassName class path used to mock library instance. Canonical
     *                                      class name
     */
    fun initDataMetrics(context: Context, dataMetricsCanonicalClassName: String) {
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
     * Get API version. Invoke {@value GET_API_VERSION_METHOD} from {@value DATA_METRICS_CLASS}
     *
     * @return api version or ApiVersion.UNKNOWN_VERSION if not available
     */
    fun getApiVersion(): ApiVersion {
        return ApiVersion.valueByCode(invokeDataMetricsMethodReturnObject(
            GET_API_VERSION_METHOD
        ))
    }

    /**
     * @return true if {@value DATA_METRICS_CLASS} is available, false otherwise
     */
    fun isDataMetricsAvailable(): Boolean {
        return isDataMetricsAvailable
    }

    @SuppressWarnings("unchecked")
    fun invokeDataMetricsMethodReturnStringList(method: String): List<String> {
        try {
            val resultObject: Any =
                invokeDataMetricsMethodReturnObject(method) ?: return Collections.emptyList()
            val result: List<String> = resultObject as List<String>
            EchoLocateLog.eLogV("Diagnostic $method returns: " + Arrays.toString(result.toTypedArray()))
            return result
        } catch (e: Throwable) {
            EchoLocateLog.eLogE("Diagnostic Method: $method, Exception: ${e.javaClass.simpleName}, Message: ${e.message}")
        }

        return Collections.emptyList()
    }

    @Nullable
    fun invokeDataMetricsMethodReturnObject(method: String): Any? {
        if (!isDataMetricsAvailable) {
            return null
        }
        try {
            val result: Any =
                TmoBaseReflection.invokeMethod(
                    TmoBaseReflection.findMethod(
                        dataMetricsClass,
                        method
                    ), dataMetricsInstance
                ) as Any
            EchoLocateLog.eLogV("Diagnostic $method returns: $result")
            return result
        } catch (e: Throwable) {
            EchoLocateLog.eLogE("Diagnostic Method: $method, Exception: ${e.javaClass.simpleName}, Message: ${e.message}")
        }
        return null
    }

    /**
     * Get Network Identity. Invoke {@value GET_NETWORK_IDENTITY_METHOD} from {@value
     * DATA_METRICS_CLASS}
     *
     * @return Network identity as list of values. In order:
     * <ul>
     * <li> Timestamp
     * <li> Network type
     * <li> MCC
     * <li> MNC
     * <li> TAC
     * <li> LAC for PCell
     * <li> CID for PCell
     * <li> PCI for PCell
     * <li> <s>LAC for SCell</s> <b>REMOVED in v1</b>
     * <li> <s>CID for SCell</s> <b>REMOVED in v1</b>
     * <li> PCI for SCell
     * <li> <s>LAC for SCell2</s> <b>REMOVED in v1</b>
     * <li> CID for SCell2
     * <li> PCI for SCell2
     * </ul>
     */
    fun getNetworkIdentity(): List<String> {
        return invokeDataMetricsMethodReturnStringList(GET_NETWORK_IDENTITY_METHOD)
    }

    /**
     * Data Metrics API library version
     */
    enum class ApiVersion constructor(
        /**
         * Version as integer
         */
        val intCode: Int,
        /**
         * Version as string
         */
        val stringCode: String
    ) {
        UNKNOWN_VERSION(-1, "0.9.3"),
        VERSION_1(1, "1"),
        VERSION_3(3, "3");


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

}