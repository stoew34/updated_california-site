package com.tmobile.mytmobile.echolocate.lte.dataprocessor

import android.content.Context
import com.tmobile.mytmobile.echolocate.lte.oemdata.LteBaseDataMetricsWrapper
import com.tmobile.mytmobile.echolocate.lte.database.entity.BaseEchoLocateLteEntity
import com.tmobile.mytmobile.echolocate.lte.database.entity.BearerEntity
import com.tmobile.mytmobile.echolocate.lte.database.entity.CAEntity
import com.tmobile.mytmobile.echolocate.lte.database.repository.LteRepository
import com.tmobile.mytmobile.echolocate.lte.model.BaseLteData
import com.tmobile.mytmobile.echolocate.lte.model.LteMetricsData
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*

abstract class BaseLteDataProcessor(context: Context) {

    val UNKNOWN_SOURCE_SIZE = -1

    var apiVersion = LteBaseDataMetricsWrapper.ApiVersion.UNKNOWN_VERSION

    var lteRepository = LteRepository(context)

    /**
     * Expected size of the source received from data metrics
     * @return Int returns the size of the source
     */
    abstract fun getExpectedSourceSize(): Int

    /**
     *  This method processes the list, converts it to individual entities and saves it in database
     *  @param source: List<String> data to convert
     *  @param currentTimestamp: String the time at which the event is triggered
     *  @param baseData: BaseLteData
     */
    abstract suspend fun processLteMetricsData(
        lteMetricsData: LteMetricsData,
        baseLteData: BaseLteData
    ): Disposable?

    /**
     * Checks if the passed data is valid or invalid and processes only if the data is valid
     * @param lteMetricsData: total metrics data
     */
    suspend fun execute(lteMetricsData: LteMetricsData): Disposable? {
        this.apiVersion = lteMetricsData.apiVersion
        if (isValid(lteMetricsData.source)) {
            val baseLteData = getBaseLteData(lteMetricsData.sessionId)
            return processLteMetricsData(lteMetricsData, baseLteData)
        }
        return null
    }

    /**
     *  Generates [BaseLteData] by passing the session id and generates a id for mapping to internal tables
     *  @param sessionId:String session id generated on lte event
     *  @return [BaseLteData] returns the generated lte data with session id and unique id
     */
    private fun getBaseLteData(sessionId: String): BaseLteData {
        return BaseLteData(sessionId, UUID.randomUUID().toString())
    }

    /**
     * checks if the data is supported by a particular version
     * @return Boolean returns true: if supported
     * false: if not supported
     */
    fun isSupportedApiVersions(): Boolean {
        return true
    }

    /**
     * Checks if the passed data is valid or invalid
     * @param source: List<String> the data to valid
     * @return Boolean returns true:if valid
     * false: if invalid
     */
    private fun isValid(source: List<String>?): Boolean {
        if (source == null) {
            return false
        } else if (!isSupportedApiVersions()) {
            return false
        } else if (UNKNOWN_SOURCE_SIZE == getExpectedSourceSize()) {
            return false
        } else if (source.size != getExpectedSourceSize()) {
            return false
        }
        return true
    }

    /**
     * This method saves the converted entities to database.
     *
     * The function doesn't create a new thread and executes the statement
     * in the same thread in which this function is called.
     *
     * @param baseEchoLocateLteEntity: [BaseEchoLocateLteEntity] object to be saved in the database
     */
    fun saveBaseEchoLocateLteEntityToDatabase(baseEchoLocateLteEntity: BaseEchoLocateLteEntity) {
        GlobalScope.launch(Dispatchers.IO) {
            lteRepository.insertBaseEchoLocateLteEntity(baseEchoLocateLteEntity)
        }
    }

    /**
     * saves the CAEntity to database
     * @param List<CAEntity>
     */
    fun saveCAEntity(list: List<CAEntity>) {
        GlobalScope.launch(Dispatchers.IO) {
            lteRepository.insertAllCAEntityEntity(list)
        }
    }

    /**
     * saves the BearerEntity to database
     * @param List<BearerEntity>
     */
    fun saveBearerEntity(list: List<BearerEntity>) {
        GlobalScope.launch(Dispatchers.IO) {
            lteRepository.insertAllBearerEntity(list)
        }
    }

}
