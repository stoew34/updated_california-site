package com.tmobile.mytmobile.echolocate.lte.dataprocessor

import android.content.Context
import com.tmobile.mytmobile.echolocate.lte.oemdata.LteBaseDataMetricsWrapper
import com.tmobile.mytmobile.echolocate.lte.database.entity.CommonRFConfigurationEntity
import com.tmobile.mytmobile.echolocate.lte.model.BaseLteData
import com.tmobile.mytmobile.echolocate.lte.model.LteMetricsData
import com.tmobile.mytmobile.echolocate.lte.utils.LteConstants.DEFAULT_VAL
import com.tmobile.mytmobile.echolocate.lte.utils.LteConstants.EMPTY
import com.tmobile.mytmobile.echolocate.utils.NumberUtils
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Processes common rf configuration carrier data
 * and saves the data in the database when focus gain or focus loss event is triggered
 */
class CommonRFConfigurationProcessor(context: Context) : BaseLteDataProcessor(context) {

    /**
     * SOURCE_SIZE_VERSION_NA
     *
     * Source size of version not available
     * value 12
     */
    private val SOURCE_SIZE_VERSION_NA = 12
    /**
     * SOURCE_SIZE_VERSION_1
     *
     * Source size of version 1
     * value 8
     */
    private val SOURCE_SIZE_VERSION_1 = 8
    /**
     * SOURCE_SIZE_VERSION_3
     *
     * Source size of version 1
     * value 8
     */
    private val SOURCE_SIZE_VERSION_3 = 8
    private var commonRFConfigurationDisposable: Disposable? = null

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
     *  This function processes upLinkCarrierInfo and return disposable
     *  @param lteMetricsData: LteMetricsData
     *  @param baseLteData: BaseLteData
     */
    override suspend fun processLteMetricsData(
        lteMetricsData: LteMetricsData,
        baseLteData: BaseLteData
    ): Disposable? {
        commonRFConfigurationDisposable = Observable.just(
            saveCommonRFConfigurationData(
                lteMetricsData,
                baseLteData
            )
        ).subscribeOn(
            Schedulers.io()
        ).subscribe()
        return commonRFConfigurationDisposable
    }

    /**
     *  This method processes the list, converts it to commonRFConfigurationData and saves it in database
     *  @param  lteMetricsData: total metrics data to be processed
     *  @param baseLteData: BaseLteData
     */
    private fun saveCommonRFConfigurationData(lteMetricsData: LteMetricsData, baseLteData: BaseLteData){
        val commonRFConfigurationEntity = getCommonRFConfigurationEntity(lteMetricsData)
        commonRFConfigurationEntity.sessionId = baseLteData.sessionId
        commonRFConfigurationEntity.uniqueId = baseLteData.uniqueId

        CoroutineScope(Dispatchers.IO).launch {
            saveCommonRFConfigurationToDatabase(commonRFConfigurationEntity)
        }
    }

    /**
     * Get correct common rf configuration entity based on api version
     * @param  lteMetricsData: total metrics data to be processed
     */
    private fun getCommonRFConfigurationEntity(lteMetricsData: LteMetricsData): CommonRFConfigurationEntity {
        when (apiVersion) {
            LteBaseDataMetricsWrapper.ApiVersion.UNKNOWN_VERSION -> return CommonRFConfigurationEntity(
                lteULaa = NumberUtils.convertToInt(lteMetricsData.source[11])?: DEFAULT_VAL,
                rrcState = NumberUtils.convertToInt(lteMetricsData.source[6])?: DEFAULT_VAL,
                ytContentId = lteMetricsData.yTContentId,
                ytLink = lteMetricsData.yTLink,
                antennaConfigurationRx = NumberUtils.convertToInt(lteMetricsData.source[3])?: DEFAULT_VAL,
                antennaConfigurationTx = NumberUtils.convertToInt(lteMetricsData.source[4])?: DEFAULT_VAL,
                networkType = lteMetricsData.source[1],
                oemTimestamp = lteMetricsData.timeStamp,
                receiverDiversity = NumberUtils.convertToInt(lteMetricsData.source[5])?: DEFAULT_VAL,
                transmissionMode = NumberUtils.convertToInt(lteMetricsData.source[2])?: DEFAULT_VAL
            )
            LteBaseDataMetricsWrapper.ApiVersion.VERSION_1,
            LteBaseDataMetricsWrapper.ApiVersion.VERSION_3 -> return CommonRFConfigurationEntity(
                lteULaa = NumberUtils.convertToInt(lteMetricsData.source[7])?: DEFAULT_VAL,
                rrcState = NumberUtils.convertToInt(lteMetricsData.source[6])?: DEFAULT_VAL,
                ytContentId = lteMetricsData.yTContentId,
                ytLink = lteMetricsData.yTLink,
                antennaConfigurationRx = NumberUtils.convertToInt(lteMetricsData.source[3])?: DEFAULT_VAL,
                antennaConfigurationTx = NumberUtils.convertToInt(lteMetricsData.source[4])?: DEFAULT_VAL,
                networkType = lteMetricsData.source[1],
                oemTimestamp = lteMetricsData.timeStamp,
                receiverDiversity = NumberUtils.convertToInt(lteMetricsData.source[5])?: DEFAULT_VAL,
                transmissionMode = NumberUtils.convertToInt(lteMetricsData.source[2])?: DEFAULT_VAL
            )
            else -> return CommonRFConfigurationEntity(
                0,
                0,
                EMPTY,
                EMPTY,
                0,
                0,
                "",
                lteMetricsData.timeStamp,
                0,
                0
            )
        }
    }

    /**
     * saves the common rf configuration object to database
     * @param commonRFConfigurationEntity: [CommonRFConfigurationEntity] the object to save
     */
    private fun saveCommonRFConfigurationToDatabase(commonRFConfigurationEntity: CommonRFConfigurationEntity) {
        lteRepository.insertCommonRFConfigurationEntity(commonRFConfigurationEntity)
    }

}