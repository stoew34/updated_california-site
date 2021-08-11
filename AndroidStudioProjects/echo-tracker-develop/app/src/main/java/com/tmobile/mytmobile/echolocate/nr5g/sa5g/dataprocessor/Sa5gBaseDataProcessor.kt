package com.tmobile.mytmobile.echolocate.nr5g.sa5g.dataprocessor

import android.content.Context
import com.tmobile.mytmobile.echolocate.nr5g.core.oemdata.Sa5gDataMetricsWrapper
import com.tmobile.mytmobile.echolocate.nr5g.sa5g.database.repository.Sa5gRepository
import com.tmobile.mytmobile.echolocate.nr5g.sa5g.model.BaseSa5gData
import com.tmobile.mytmobile.echolocate.nr5g.sa5g.model.BaseSa5gMetricsData
import com.tmobile.mytmobile.echolocate.nr5g.sa5g.utils.Sa5gConstants
import io.reactivex.disposables.Disposable
import java.util.*

abstract class Sa5gBaseDataProcessor(context: Context) {

    var apiVersion = Sa5gDataMetricsWrapper.ApiVersion.UNKNOWN_VERSION
    var sa5gRepository = Sa5gRepository(context)

    /**
     * Expected size of the source received from data metrics
     * @return Int returns the size of the source
     */
    abstract fun getExpectedSourceSize(): Int

    /**
     *  This fun process Metrics Data
     *  @param baseSa5gMetricsData: BaseSa5gMetricsData
     *  @param baseSa5gData: BaseSa5gData
     */
    abstract suspend fun processSa5gMetricsData(
        baseSa5gMetricsData: BaseSa5gMetricsData,
        baseSa5gData: BaseSa5gData
    ): Disposable?

    /**
     * Checks if the passed data is valid or invalid and processes only if the data is valid
     * @param baseSa5GMetricsData: total metrics data
     */
    suspend fun execute(baseSa5GMetricsData: BaseSa5gMetricsData): Disposable? {
        this.apiVersion = baseSa5GMetricsData.apiVersion
        if (isValid(baseSa5GMetricsData.source)) {
            val baseSa5gData = getBaseSa5gData(baseSa5GMetricsData.sessionId)
            return processSa5gMetricsData(baseSa5GMetricsData, baseSa5gData)
        }
        return null
    }

    /**
     *  Generates [BaseSa5gData] by passing the session id and generates a id for mapping to internal tables
     *  @param sessionId:String session id generated on 5g event
     *  @return [BaseSa5gData] returns the generated 5g data with session id and unique id
     */
    private fun getBaseSa5gData(sessionId: String): BaseSa5gData {
        return BaseSa5gData(sessionId, UUID.randomUUID().toString())
    }

    /**
     * checks if the data is supported by a particular version
     * @return Boolean returns true: if supported
     * false: if not supported
     */
    private fun isSupportedApiVersions(): Boolean {
        return true
    }

    /**
     * Checks if the passed data is valid or invalid
     * @param source: Any? the data to valid
     *        If source is not List<String>, it could be any other object like EndcLteLogEntity
     *        Then, we returns true.
     * @return Boolean returns true:if valid
     * false: if invalid
     */
    private fun isValid(source: Any?): Boolean {
        if (source == null) {
            return false
        } else if (!isSupportedApiVersions()) {
            return false
        } else if (source is List<*>) {
            val srcSize = getExpectedSourceSize()

            if (Sa5gConstants.UNKNOWN_SOURCE_SIZE == srcSize) {
                return false
            }
            if (!arrayOf(source).isArrayOf<String>()) {
                return true
            }
            if ((source as List<String>).size != srcSize) {
                return false
            }
        }
        return true
    }
}
