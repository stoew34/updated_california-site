package com.tmobile.mytmobile.echolocate.lte.dataprocessor

import android.content.Context
import com.tmobile.mytmobile.echolocate.lte.oemdata.LteBaseDataMetricsWrapper
import com.tmobile.mytmobile.echolocate.lte.database.entity.CAEntity
import com.tmobile.mytmobile.echolocate.lte.database.entity.DownLinkCarrierInfoEntity
import com.tmobile.mytmobile.echolocate.lte.model.BaseLteData
import com.tmobile.mytmobile.echolocate.lte.model.LteMetricsData
import com.tmobile.mytmobile.echolocate.lte.utils.LteConstants
import com.tmobile.mytmobile.echolocate.lte.utils.LteConstants.DEFAULT_VAL
import com.tmobile.mytmobile.echolocate.utils.NumberUtils
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
//import kotlinx.serialization.ImplicitReflectionSerializer
import java.util.*

/**
 * Processes downlink carrier data and saves the data in the database when focus gain or focus loss event is triggered
 */
class DownLinkCarrierInfoProcessor(context: Context) : BaseLteDataProcessor(context) {

    private var downLinkCarrierInfoDisposable: Disposable? = null
    /**
     * Sets the expected size of the source received from data metrics
     * @return Int returns the size of the source
     */
    override fun getExpectedSourceSize(): Int {
        return when (apiVersion) {
            LteBaseDataMetricsWrapper.ApiVersion.VERSION_1 -> LteConstants.DOWNLINK_CARRIER_INFO_EXPECTED_SIZE
            LteBaseDataMetricsWrapper.ApiVersion.VERSION_3 -> LteConstants.DOWNLINK_CARRIER_INFO_EXPECTED_SIZE
            LteBaseDataMetricsWrapper.ApiVersion.UNKNOWN_VERSION -> LteConstants.DOWNLINK_CARRIER_INFO_UNKNOWN_VERSION_SIZE
        }
    }

    /**
     *  This function processes DownLinkCarrierInfo and return disposable
     *  @param lteMetricsData: LteMetricsData
     *  @param baseLteData: BaseLteData
     */
//    @ImplicitReflectionSerializer
    override suspend fun processLteMetricsData(
        lteMetricsData: LteMetricsData,
        baseLteData: BaseLteData
    ): Disposable? {
        downLinkCarrierInfoDisposable = Observable.just(
            saveDownLinkCarrierInfoData(
                lteMetricsData,
                baseLteData
            )
        ).subscribeOn(
            Schedulers.io()
        ).subscribe()
        return downLinkCarrierInfoDisposable
    }

    /**
     *  This method processes the list, converts it to upLinkCarrierInfoEntity and saves it in database
     *  @param lteMetricsData: total metrics data to be processed
     *  @param baseLteData: BaseLteData
     */
    private fun saveDownLinkCarrierInfoData(lteMetricsData: LteMetricsData, baseLteData: BaseLteData) {
        val downLinkCarrierInfoEntity = getDownlinkCarrierInfoEntity(lteMetricsData)
        val caEntityList = getCAEntity(lteMetricsData, baseLteData)

        downLinkCarrierInfoEntity.sessionId = baseLteData.sessionId
        downLinkCarrierInfoEntity.uniqueId = baseLteData.uniqueId

        CoroutineScope(Dispatchers.IO).launch {
            saveUpLinkCarrierInfoToDatabase(downLinkCarrierInfoEntity)
            saveCAToDatabase(caEntityList)
        }
    }

    /**
     * Gets CarrierAggregation list from the data metrics source
     * @param lteMetricsData: [LteMetricsData] data from data metrics to convert
     * @param baseLteData: [BaseLteData] base data
     * @return List<[CAEntity]> returns the list of CarrierAggregation
     */
    private fun getCAEntity(
        lteMetricsData: LteMetricsData,
        baseLteData: BaseLteData
    ): List<CAEntity> {
        when (apiVersion) {
            LteBaseDataMetricsWrapper.ApiVersion.UNKNOWN_VERSION,
            LteBaseDataMetricsWrapper.ApiVersion.VERSION_1,
            LteBaseDataMetricsWrapper.ApiVersion.VERSION_3 ->
                return getCarrierAggregationList(lteMetricsData.source, baseLteData)
        }
    }

    /**
     * Gets DownLinkCarrierInfoEntity list from the data metrics source
     * @param lteMetricsData: [LteMetricsData] data from data metrics to convert
     * @return DownLinkCarrierInfoEntity returns the data generated
     */
    private fun getDownlinkCarrierInfoEntity(lteMetricsData: LteMetricsData): DownLinkCarrierInfoEntity {
        when (apiVersion) {
            LteBaseDataMetricsWrapper.ApiVersion.UNKNOWN_VERSION,
            LteBaseDataMetricsWrapper.ApiVersion.VERSION_1,
            LteBaseDataMetricsWrapper.ApiVersion.VERSION_3 ->
                return DownLinkCarrierInfoEntity(
                    lteMetricsData.source[1],
                    NumberUtils.convertToInt(lteMetricsData.source[2])!!,
                    lteMetricsData.timeStamp
                )
        }
    }

    /**
     * Gets CarrierAggregation list from the data metrics source
     * @param source: List<String> data from data metrics to convert
     * @return List<CAData> returns the list of CarrierAggregation
     */
    private fun getCarrierAggregationList(source: List<String>, baseLteData: BaseLteData): List<CAEntity> {
        val caEntityList = mutableListOf<CAEntity>()
        val caEntityPrimary = CAEntity(
            NumberUtils.convertToInt(source[3])?: DEFAULT_VAL,
            NumberUtils.convertToInt(source[5])?: DEFAULT_VAL,
            NumberUtils.convertToInt(source[4])?: DEFAULT_VAL,
            LteConstants.PRIMARY_CA,
            null,
            null,
            null,
            null,
            null
        )
        caEntityPrimary.sessionId = baseLteData.sessionId
        caEntityPrimary.uniqueId = baseLteData.uniqueId
        caEntityPrimary.baseEntityId = UUID.randomUUID().toString()
        caEntityList.add(caEntityPrimary)

        val caEntitySecondary = CAEntity(
            NumberUtils.convertToInt(source[6])?: DEFAULT_VAL,
            NumberUtils.convertToInt(source[8])?: DEFAULT_VAL,
            NumberUtils.convertToInt(source[7])?: DEFAULT_VAL,
            LteConstants.SECONDARY_CA,
            null,
            null,
            null,
            null,
            null
        )
        caEntitySecondary.sessionId = baseLteData.sessionId
        caEntitySecondary.uniqueId = baseLteData.uniqueId
        caEntitySecondary.baseEntityId = UUID.randomUUID().toString()
        caEntityList.add(caEntitySecondary)

        val caEntityThird = CAEntity(
            NumberUtils.convertToInt(source[9])?: DEFAULT_VAL,
            NumberUtils.convertToInt(source[11])?: DEFAULT_VAL,
            NumberUtils.convertToInt(source[10])?: DEFAULT_VAL,
            LteConstants.TERTIARY_CA,
            null,
            null,
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

    /**
     * saves the downLinkCarrierInfoEntity object to database
     * @param downLinkCarrierInfoEntity: [downLinkCarrierInfoEntity] the object to save
     */
    private fun saveUpLinkCarrierInfoToDatabase(downLinkCarrierInfoEntity: DownLinkCarrierInfoEntity) {
        lteRepository.insertDownlinkCarrierInfoEntity(downLinkCarrierInfoEntity)
    }

    /**
     * saves the caEntityList object to database
     * @param caEntityList: [CAEntity] the object to save
     */
    private fun saveCAToDatabase(caEntityList: List<CAEntity>) {
        lteRepository.insertAllCAEntityEntity(caEntityList)
    }


}