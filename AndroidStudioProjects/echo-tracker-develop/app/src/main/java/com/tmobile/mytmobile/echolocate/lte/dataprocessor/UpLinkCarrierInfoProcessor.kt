package com.tmobile.mytmobile.echolocate.lte.dataprocessor

import android.content.Context
import com.tmobile.mytmobile.echolocate.lte.oemdata.LteBaseDataMetricsWrapper
import com.tmobile.mytmobile.echolocate.lte.database.entity.CAEntity
import com.tmobile.mytmobile.echolocate.lte.database.entity.UplinkCarrierInfoEntity
import com.tmobile.mytmobile.echolocate.lte.model.BaseLteData
import com.tmobile.mytmobile.echolocate.lte.model.LteMetricsData
import com.tmobile.mytmobile.echolocate.lte.utils.LteConstants
import com.tmobile.mytmobile.echolocate.lte.utils.LteConstants.DEFAULT_VAL
import com.tmobile.mytmobile.echolocate.utils.EchoLocateLog
import com.tmobile.mytmobile.echolocate.utils.NumberUtils
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

/**
 * Processes up link carrier data and saves the data in the database when focus gain or focus loss event is triggered
 */
class UpLinkCarrierInfoProcessor(context: Context) : BaseLteDataProcessor(context) {

    /**
     * UPLINK_CARRIER_INFO_EXPECTED_SIZE
     * The expected size of carrierInfo
     * value 9
     */
    private val UPLINK_CARRIER_INFO_EXPECTED_SIZE = 9
    private var upLinkCarrierInfoDisposable: Disposable? = null

    /**
     * Sets the expected size of the source received from data metrics
     * @return Int returns the size of the source
     */
    override fun getExpectedSourceSize(): Int {
        return when (apiVersion) {
            LteBaseDataMetricsWrapper.ApiVersion.UNKNOWN_VERSION -> 0
            LteBaseDataMetricsWrapper.ApiVersion.VERSION_1 -> UPLINK_CARRIER_INFO_EXPECTED_SIZE
            LteBaseDataMetricsWrapper.ApiVersion.VERSION_3 -> UPLINK_CARRIER_INFO_EXPECTED_SIZE

        }
    }

    /**
     *  This function processes upLinkCarrierInfo and return disposable
     *  @param lteMetricsData: LteMetricsData
     *  @param baseLteData: BaseLteData
     */
    override suspend fun processLteMetricsData(
        lteMetricsData: LteMetricsData,
        baseLteData: BaseLteData
    ): Disposable? {
        upLinkCarrierInfoDisposable = Observable.just(
            saveUpLinkCarrierInfoData(
                lteMetricsData,
                baseLteData
            )
        ).subscribeOn(
            Schedulers.io()
        ).subscribe()
        return upLinkCarrierInfoDisposable
    }

    /**
     *  This method processes the list, converts it to upLinkCarrierInfoEntity and saves it in database
     *  @param lteMetricsData: total metrics data to be processed
     *  @param baseLteData: BaseLteData
     */
    private fun saveUpLinkCarrierInfoData(
        lteMetricsData: LteMetricsData,
        baseLteData: BaseLteData
    ) {
        when (apiVersion) {
            LteBaseDataMetricsWrapper.ApiVersion.VERSION_1,
            LteBaseDataMetricsWrapper.ApiVersion.VERSION_3 -> {
                val uplinkCarrierInfoEntity = UplinkCarrierInfoEntity(
                    lteMetricsData.source[1],
                    NumberUtils.convertToInt(lteMetricsData.source[2]) ?: DEFAULT_VAL,
                    lteMetricsData.timeStamp
                )
                val caList = getCarrierAggregationList(lteMetricsData.source, baseLteData)
                uplinkCarrierInfoEntity.sessionId = baseLteData.sessionId
                uplinkCarrierInfoEntity.uniqueId = baseLteData.uniqueId

                CoroutineScope(Dispatchers.IO).launch {
                    saveUpLinkCarrierInfoToDatabase(uplinkCarrierInfoEntity)
                    saveCAToDatabase(caList)
                }
            }
            else -> {
                EchoLocateLog.eLogI("apiVersion not supported $apiVersion")
            }
        }
    }

    /**
     * Gets CarrierAggregation list from the data metrics source
     * @param source: List<String> data from data metrics to convert
     * @return List<CAData> returns the list of CarrierAggregation
     */
    private fun getCarrierAggregationList(
        source: List<String>,
        baseLteData: BaseLteData
    ): List<CAEntity> {
        val caEntityList = mutableListOf<CAEntity>()
        val caEntityPrimary = CAEntity(
            NumberUtils.convertToInt(source[3]) ?: DEFAULT_VAL,
            NumberUtils.convertToInt(source[5]) ?: DEFAULT_VAL,
            NumberUtils.convertToInt(source[4]) ?: DEFAULT_VAL,
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
            NumberUtils.convertToInt(source[6]) ?: DEFAULT_VAL,
            NumberUtils.convertToInt(source[8]) ?: DEFAULT_VAL,
            NumberUtils.convertToInt(source[7]) ?: DEFAULT_VAL,
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

        return caEntityList
    }

    /**
     * saves the UpLinkCarrierInfo object to database
     * @param uplinkCarrierInfoEntity: [UpLinkRFConfigurationEntity] the object to save
     */
    private fun saveUpLinkCarrierInfoToDatabase(uplinkCarrierInfoEntity: UplinkCarrierInfoEntity) {
        lteRepository.insertUplinkCarrierInfoEntity(uplinkCarrierInfoEntity)
    }

    /**
     * saves the caEntityList object to database
     * @param caEntityList: [CAEntity] the object to save
     */
    private fun saveCAToDatabase(caEntityList: List<CAEntity>) {
        lteRepository.insertAllCAEntityEntity(caEntityList)
    }


}