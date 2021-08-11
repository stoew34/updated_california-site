package com.tmobile.mytmobile.echolocate.nr5g.nsa5g.dataprocessor

import android.content.Context
import com.tmobile.mytmobile.echolocate.nr5g.core.oemdata.Nr5gBaseDataMetricsWrapper
import com.tmobile.mytmobile.echolocate.nr5g.nsa5g.database.repository.Nr5gRepository
import com.tmobile.mytmobile.echolocate.nr5g.nsa5g.model.BaseNr5gData
import com.tmobile.mytmobile.echolocate.nr5g.nsa5g.model.BaseNr5gMetricsData
import io.reactivex.disposables.Disposable
import java.util.*

abstract class Nsa5gBaseDataProcessor(context: Context) {

    val UNKNOWN_SOURCE_SIZE = -1

    var apiVersion = Nr5gBaseDataMetricsWrapper.ApiVersion.UNKNOWN_VERSION

    var nr5gRepository = Nr5gRepository(context)

    /**
     * Expected size of the source received from data metrics
     * @return Int returns the size of the source
     */
    abstract fun getExpectedSourceSize(): Int

    /**
     *  This fun process Metrics Data
     *  @param baseNr5gMetricsData: BaseNr5gMetricsData
     *  @param baseNr5gData: BaseNr5gData
     */
    abstract suspend fun processNr5gMetricsData(
        baseNr5gMetricsData: BaseNr5gMetricsData,
        baseNr5gData: BaseNr5gData
    ): Disposable?

    /**
     * Checks if the passed data is valid or invalid and processes only if the data is valid
     * @param baseNr5GMetricsData: total metrics data
     */
    suspend fun execute(baseNr5GMetricsData: BaseNr5gMetricsData): Disposable? {
        this.apiVersion = baseNr5GMetricsData.apiVersion
        if (isValid(baseNr5GMetricsData.source)) {
            val baseNr5gData = getBaseNr5gData(baseNr5GMetricsData.sessionId)
            return processNr5gMetricsData(baseNr5GMetricsData, baseNr5gData)
        }
        return null
    }

    /**
     *  Generates [BaseNr5gData] by passing the session id and generates a id for mapping to internal tables
     *  @param sessionId:String session id generated on 5g event
     *  @return [BaseNr5gData] returns the generated 5g data with session id and unique id
     */
    private fun getBaseNr5gData(sessionId: String): BaseNr5gData {
        return BaseNr5gData(sessionId, UUID.randomUUID().toString())
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
            if (UNKNOWN_SOURCE_SIZE == srcSize) {
                return false
            }

            if ((source as List<String>).size != srcSize) {
                return false
            }
        }
        return true
    }
}
