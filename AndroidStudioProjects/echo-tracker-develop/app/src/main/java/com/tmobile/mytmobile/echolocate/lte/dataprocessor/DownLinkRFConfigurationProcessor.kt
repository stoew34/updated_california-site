package com.tmobile.mytmobile.echolocate.lte.dataprocessor

import android.content.Context
import com.tmobile.mytmobile.echolocate.lte.oemdata.LteBaseDataMetricsWrapper
import com.tmobile.mytmobile.echolocate.lte.database.entity.CAEntity
import com.tmobile.mytmobile.echolocate.lte.database.entity.DownlinkRFConfigurationEntity
import com.tmobile.mytmobile.echolocate.lte.model.BaseLteData
import com.tmobile.mytmobile.echolocate.lte.model.LteMetricsData
import com.tmobile.mytmobile.echolocate.lte.utils.LteConstants
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
//import kotlinx.serialization.ImplicitReflectionSerializer
import java.util.*


/**
 * This class is responsible to process DownLinkRFConfiguration carrier data and saves the data in the database when focus gain or focus loss event is triggered
 */
class DownLinkRFConfigurationProcessor(context: Context) : BaseLteDataProcessor(context) {
    private val SOURCE_SIZE_VERSION_NA = 17
    private val SOURCE_SIZE_VERSION_1 = 8
    private val SOURCE_SIZE_VERSION_3 = 8
    private var downLinkRFConfigurationDisposable: Disposable? = null
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
     *  This function processes down Link RF Configuration and return disposable
     *  @param lteMetricsData: LteMetricsData
     *  @param baseLteData: BaseLteData
     */
//    @ImplicitReflectionSerializer
    override suspend fun processLteMetricsData(
        lteMetricsData: LteMetricsData,
        baseLteData: BaseLteData
    ): Disposable? {
        downLinkRFConfigurationDisposable = Observable.just(
            saveDownLinkRFConfigurationData(
                lteMetricsData,
                baseLteData
            )
        ).subscribeOn(
            Schedulers.io()
        ).subscribe()
        return downLinkRFConfigurationDisposable
    }

    /**
     *  This method processes the list, converts it to downLinkRFConfigurationEntity and saves in tox database
     *  @param  lteMetricsData: total metrics data to be processed
     *  @param baseLteData: BaseLteData
     */
    private fun saveDownLinkRFConfigurationData(lteMetricsData: LteMetricsData, baseLteData: BaseLteData){
        val downLinkRFConfigurationEntity = DownlinkRFConfigurationEntity(
            networkType = lteMetricsData.source[1],
            oemTimestamp = lteMetricsData.timeStamp
        )
        val caList = getCAEntityList(lteMetricsData.source, baseLteData)
        downLinkRFConfigurationEntity.sessionId = baseLteData.sessionId
        downLinkRFConfigurationEntity.uniqueId = baseLteData.uniqueId

        CoroutineScope(Dispatchers.IO).launch {
            lteRepository.insertDownLinkRFConfiguration(downLinkRFConfigurationEntity)
            saveCAEntity(caList)
        }
    }

    /**
     * Gets CAEntity list from the data metrics source
     * @param source: List<String> data from data metrics to convert
     * @return List<CAEntity> returns CA entity list
     */

    private fun getCAEntityList(source: List<String>, baseLteData: BaseLteData): List<CAEntity> {
        return when (apiVersion) {
            LteBaseDataMetricsWrapper.ApiVersion.UNKNOWN_VERSION -> createEntityList(
                baseLteData,
                source[6].toInt(),
                source[3], source[11].toInt(), source[8], source[16].toInt(), source[13]
            )
            LteBaseDataMetricsWrapper.ApiVersion.VERSION_1,
            LteBaseDataMetricsWrapper.ApiVersion.VERSION_3 -> createEntityList(
                baseLteData,
                source[3].toInt(),
                source[2], source[5].toInt(), source[4], source[7].toInt(), source[6]
            )
        }
    }

    /**
     * This function is responsible to provide the CAList based on the apiVersion
     * @param baseLteData :which provides the source list
     */
    private fun createEntityList(
        baseLteData: BaseLteData, layersPrimary: Int?, modulationPrimary: String?,
        layersSecondary: Int?, modulationSecondary: String?,
        layersThird: Int?, modulationThird: String?
    ): List<CAEntity> {
        val caEntityList = mutableListOf<CAEntity>()
        val caEntityPrimary = CAEntity(
            null,
            null,
            null,
            LteConstants.PRIMARY_CA,
            layersPrimary ?: 0,
            modulationPrimary ?: "",
            null,
            null,
            null
        )
        caEntityPrimary.sessionId = baseLteData.sessionId
        caEntityPrimary.uniqueId = baseLteData.uniqueId
        caEntityPrimary.baseEntityId = UUID.randomUUID().toString()
        caEntityList.add(caEntityPrimary)

        val caEntitySecondary = CAEntity(
            null,
            null,
            null,
            LteConstants.SECONDARY_CA,
            layersSecondary ?: 0,
            modulationSecondary ?: "",
            null,
            null,
            null
        )
        caEntitySecondary.sessionId = baseLteData.sessionId
        caEntitySecondary.uniqueId = baseLteData.uniqueId
        caEntitySecondary.baseEntityId = UUID.randomUUID().toString()
        caEntityList.add(caEntitySecondary)

        val caEntityThird = CAEntity(
            null,
            null,
            null,
            LteConstants.TERTIARY_CA,
            layersThird ?: 0,
            modulationThird ?: "",
            null,
            null,
            null
        )
        caEntityThird.sessionId = baseLteData.sessionId
        caEntityThird.uniqueId = baseLteData.uniqueId
        caEntityThird.baseEntityId = UUID.randomUUID().toString()
        caEntityList.add(caEntityThird)
        return caEntityList
    }


}