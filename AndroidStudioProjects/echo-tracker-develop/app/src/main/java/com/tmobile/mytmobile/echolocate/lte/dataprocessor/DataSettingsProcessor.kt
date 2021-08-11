package com.tmobile.mytmobile.echolocate.lte.dataprocessor

import android.content.Context
import com.tmobile.mytmobile.echolocate.lte.oemdata.LteBaseDataMetricsWrapper
import com.tmobile.mytmobile.echolocate.lte.database.entity.LteSettingsEntity
import com.tmobile.mytmobile.echolocate.lte.model.BaseLteData
import com.tmobile.mytmobile.echolocate.lte.model.LteMetricsData
import com.tmobile.mytmobile.echolocate.lte.utils.LteConstants.DEFAULT_VAL
import com.tmobile.mytmobile.echolocate.utils.NumberUtils
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Processes data settings data
 * and saves the data in the database when focus gain or focus loss event is triggered
 */
class DataSettingsProcessor(context: Context) : BaseLteDataProcessor(context) {

    /**
     * SOURCE_SIZE_VERSION_NA
     *
     * Source size of version not available
     * value 7
     */
    private val SOURCE_SIZE_VERSION_NA = 7
    /**
     * SOURCE_SIZE_VERSION_1
     *
     * Source size of version 1
     * value 9
     */
    private val SOURCE_SIZE_VERSION_1 = 9

    /**
     * SOURCE_SIZE_VERSION_3
     *
     * Source size of version 3
     * value 9
     */
    private val SOURCE_SIZE_VERSION_3 = 9
    private var dataSettingsDisposable: Disposable? = null

    /**
     * Sets the expected size of the source received from data metrics
     * @return Int returns the size of the source
     */
    override fun getExpectedSourceSize(): Int {
        return when (apiVersion) {
            LteBaseDataMetricsWrapper.ApiVersion.UNKNOWN_VERSION -> SOURCE_SIZE_VERSION_NA
            LteBaseDataMetricsWrapper.ApiVersion.VERSION_1 -> SOURCE_SIZE_VERSION_1
            LteBaseDataMetricsWrapper.ApiVersion.VERSION_3 -> SOURCE_SIZE_VERSION_3

        }
    }

    /**
     *  This function processes Settings Entity and return disposable
     *  @param lteMetricsData: LteMetricsData
     *  @param baseLteData: BaseLteData
     */
    override suspend fun processLteMetricsData(
        lteMetricsData: LteMetricsData,
        baseLteData: BaseLteData
    ): Disposable? {
        dataSettingsDisposable = Observable.just(
            saveDataSettingsData(
                lteMetricsData,
                baseLteData
            )
        ).subscribeOn(
            Schedulers.io()
        ).subscribe()
        return dataSettingsDisposable
    }

    /**
     *  This method processes the list, converts it to lte Settings Entity and saves it in database
     *  @param  lteMetricsData: total metrics data to be processed
     *  @param baseLteData: BaseLteData
     */
    private fun saveDataSettingsData(lteMetricsData: LteMetricsData, baseLteData: BaseLteData) {
        val lteSettingsEntity = getLteSettingsEntity(lteMetricsData)
        lteSettingsEntity.sessionId = baseLteData.sessionId
        lteSettingsEntity.uniqueId = baseLteData.uniqueId

        CoroutineScope(Dispatchers.IO).launch {
            saveLteSettingsToDatabase(lteSettingsEntity)
        }
    }

    /**
     * Get lte settings entity based on api version
     * @param  lteMetricsData: total metrics data to be processed
     */
    private fun getLteSettingsEntity(lteMetricsData: LteMetricsData): LteSettingsEntity {
        when (apiVersion) {
            LteBaseDataMetricsWrapper.ApiVersion.UNKNOWN_VERSION -> return LteSettingsEntity(
                NumberUtils.convertToInt(lteMetricsData.source[4])?: DEFAULT_VAL,
                NumberUtils.convertToInt(lteMetricsData.source[3])?: DEFAULT_VAL,
                NumberUtils.convertToInt(lteMetricsData.source[1])?: DEFAULT_VAL,
                NumberUtils.convertToInt(lteMetricsData.source[2])?: DEFAULT_VAL,
                lteMetricsData.timeStamp,
                lteMetricsData.source[5], "", "",
                NumberUtils.convertToInt(lteMetricsData.source[6])?: DEFAULT_VAL
            )
            LteBaseDataMetricsWrapper.ApiVersion.VERSION_1,
            LteBaseDataMetricsWrapper.ApiVersion.VERSION_3 -> return LteSettingsEntity(
                NumberUtils.convertToInt(lteMetricsData.source[4])?: DEFAULT_VAL,
                NumberUtils.convertToInt(lteMetricsData.source[3])?: DEFAULT_VAL,
                NumberUtils.convertToInt(lteMetricsData.source[1])?: DEFAULT_VAL,
                NumberUtils.convertToInt(lteMetricsData.source[2])?: DEFAULT_VAL,
                lteMetricsData.timeStamp,
                lteMetricsData.source[5],
                lteMetricsData.source[7],
                lteMetricsData.source[8],
                NumberUtils.convertToInt(lteMetricsData.source[6])?: DEFAULT_VAL
            )
            else -> return LteSettingsEntity(
                0,
                0,
                0,
                0,
                lteMetricsData.timeStamp,
                "",
                "",
                "",
                0
            )
        }
    }


    /**
     * saves the data settings object to database
     * @param lteSettingsEntity: [LteSettingsEntity] the object to save
     */
    private fun saveLteSettingsToDatabase(lteSettingsEntity: LteSettingsEntity) {
        lteRepository.insertLteSettingsEntity(lteSettingsEntity)
    }

}