package com.tmobile.mytmobile.echolocate.lte.dataprocessor

import android.content.Context
import com.tmobile.mytmobile.echolocate.lte.oemdata.LteBaseDataMetricsWrapper
import com.tmobile.mytmobile.echolocate.lte.database.entity.SecondCarrierEntity
import com.tmobile.mytmobile.echolocate.lte.database.entity.SignalConditionEntity
import com.tmobile.mytmobile.echolocate.lte.database.entity.ThirdCarrierEntity
import com.tmobile.mytmobile.echolocate.lte.model.BaseLteData
import com.tmobile.mytmobile.echolocate.lte.model.LteMetricsData
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Processes Signal condition data and saves the data in the database when focus gain or focus loss event is triggered
 */
class SignalConditionProcessor(context: Context) : BaseLteDataProcessor(context) {

    /**
     * SOURCE_SIZE_VERSION_NA
     *
     * Source size of version not available
     * value 5
     */
    private val SOURCE_SIZE_VERSION_NA = 6

    /**
     * SOURCE_SIZE_VERSION_1
     *
     * Source size of version 1
     * value 16
     */
    private val SOURCE_SIZE_VERSION_1 = 16

    /**
     * SOURCE_SIZE_VERSION_3
     *
     * Source size of version 3
     * value 16
     */
    private val SOURCE_SIZE_VERSION_3 = 16
    private var signalConditionDisposable: Disposable? = null

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
     *  This function processes Signal Condition Entity and return disposable
     *  @param lteMetricsData: LteMetricsData
     *  @param baseLteData: BaseLteData
     */
    override suspend fun processLteMetricsData(
        lteMetricsData: LteMetricsData,
        baseLteData: BaseLteData
    ): Disposable? {
        signalConditionDisposable = Observable.just(
            saveSignalConditionData(
                lteMetricsData,
                baseLteData
            )
        ).subscribeOn(
            Schedulers.io()
        ).subscribe()
        return signalConditionDisposable
    }

    /**
     *  This method processes the list, converts it to SignalConditionEntity and saves it in database
     *  @param lteMetricsData: total metrics data to be processed
     *  @param baseLteData: BaseLteData
     */
    private fun saveSignalConditionData(lteMetricsData: LteMetricsData, baseLteData: BaseLteData) {
        when (apiVersion) {
            LteBaseDataMetricsWrapper.ApiVersion.UNKNOWN_VERSION -> {
                val signalConditionEntity = SignalConditionEntity(
                    0,
                    0,
                    lteMetricsData.source[2].toInt(),
                    lteMetricsData.source[3].toInt(),
                    lteMetricsData.source[5].toInt(),
                    lteMetricsData.source[4].toInt(),
                    lteMetricsData.source[1],
                    lteMetricsData.timeStamp
                )
                signalConditionEntity.sessionId = baseLteData.sessionId
                signalConditionEntity.uniqueId = baseLteData.uniqueId

                CoroutineScope(Dispatchers.IO).launch {
                    lteRepository.insertSignalCondition(signalConditionEntity)
                }
            }
            LteBaseDataMetricsWrapper.ApiVersion.VERSION_1,
            LteBaseDataMetricsWrapper.ApiVersion.VERSION_3 -> {
                val signalConditionEntity = SignalConditionEntity(
                    lteMetricsData.source[7].toInt(),
                    lteMetricsData.source[6].toInt(),
                    lteMetricsData.source[2].toInt(),
                    lteMetricsData.source[3].toInt(),
                    lteMetricsData.source[5].toInt(),
                    lteMetricsData.source[4].toInt(),
                    lteMetricsData.source[1],
                    lteMetricsData.timeStamp
                )
                val secondCarrierEntity = SecondCarrierEntity(
                    lteMetricsData.source[8],
                    lteMetricsData.source[9],
                    lteMetricsData.source[11],
                    lteMetricsData.source[10]

                )
                secondCarrierEntity.sessionId = baseLteData.sessionId
                secondCarrierEntity.uniqueId = baseLteData.uniqueId

                val thirdCarrierEntity = ThirdCarrierEntity(
                    lteMetricsData.source[12],
                    lteMetricsData.source[13],
                    lteMetricsData.source[15],
                    lteMetricsData.source[14]
                )
                thirdCarrierEntity.sessionId = baseLteData.sessionId
                thirdCarrierEntity.uniqueId = baseLteData.uniqueId

                signalConditionEntity.sessionId = baseLteData.sessionId
                signalConditionEntity.uniqueId = baseLteData.uniqueId

                CoroutineScope(Dispatchers.IO).launch {
                    lteRepository.insertSignalCondition(signalConditionEntity)
                    lteRepository.insertSecondCarrierEntity(secondCarrierEntity)
                    lteRepository.insertThirdCarrierEntity(thirdCarrierEntity)
                }
            }
        }
    }
}