package com.tmobile.mytmobile.echolocate.lte.dataprocessor

import android.content.Context
import com.tmobile.mytmobile.echolocate.lte.oemdata.LteBaseDataMetricsWrapper
import com.tmobile.mytmobile.echolocate.lte.database.entity.BearerConfigurationEntity
import com.tmobile.mytmobile.echolocate.lte.database.entity.BearerEntity
import com.tmobile.mytmobile.echolocate.lte.model.BaseLteData
import com.tmobile.mytmobile.echolocate.lte.model.LteMetricsData
import com.tmobile.mytmobile.echolocate.utils.EchoLocateLog
import com.tmobile.mytmobile.echolocate.utils.NumberUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

/** Json example according to schema
 *
"bearerConfiguration": {
"bearer": [
{
"APNName": "fast.t-mobile.com",
"QCI": 6
},
{
"APNName": "ims",
"QCI": 5
},
{
"APNName": "-999",
"QCI": -999
}
],
"networkType": "1",
"numberOfBearers": "",
"oemTimestamp": "2018-05-14T08:54:53.007-0700"
}
 */
/**
 * Processes BearerConfiguration carrier data and saves the data in the database when focus gain or focus loss event is triggered
 */
class BearerConfigurationProcessor(context: Context) : BaseLteDataProcessor(context) {

    /**
     * BEARER_CONFIG_EXPECTED_SIZE
     *
     * Source size of version not available
     * value 11
     */
    private val BEARER_CONFIG_EXPECTED_SIZE = 11
    private var bearerConfigurationDisposable: Disposable? = null

    /**
     * Sets the expected size of the source received from data metrics
     * @return Int returns the size of the source
     */
    override fun getExpectedSourceSize(): Int {
        return when (apiVersion) {
            LteBaseDataMetricsWrapper.ApiVersion.UNKNOWN_VERSION -> UNKNOWN_SOURCE_SIZE
            LteBaseDataMetricsWrapper.ApiVersion.VERSION_1 -> BEARER_CONFIG_EXPECTED_SIZE
            LteBaseDataMetricsWrapper.ApiVersion.VERSION_3 -> BEARER_CONFIG_EXPECTED_SIZE
        }
    }

    /**
     *  This function processes bearer configuration and return disposable
     *  @param lteMetricsData: LteMetricsData
     *  @param baseLteData: BaseLteData
     */
    override suspend fun processLteMetricsData(
        lteMetricsData: LteMetricsData,
        baseLteData: BaseLteData
    ): Disposable? {
        bearerConfigurationDisposable =  Observable.just(saveBearerConfigurationData(lteMetricsData,
            baseLteData)).subscribeOn(
            Schedulers.io()).subscribe()
        return bearerConfigurationDisposable
    }


    /**
     *  This function processes the list, converts it to bearerConfigurationEntity and saves it in database
     *  @param lteMetricsData: total metrics data to be processed
     *  @param baseLteData: BaseLteData
     */
    private fun saveBearerConfigurationData(
        lteMetricsData: LteMetricsData,
        baseLteData: BaseLteData
    ){
        when (apiVersion) {
            LteBaseDataMetricsWrapper.ApiVersion.VERSION_1,
            LteBaseDataMetricsWrapper.ApiVersion.VERSION_3 -> {
                val bearerEntityList = getBearerEntityList(lteMetricsData.source, baseLteData)

                val bearerConfigurationEntity = BearerConfigurationEntity(
                    networkType = lteMetricsData.source[1],
                    numberOfBearers = lteMetricsData.source[2],
                    oemTimestamp = lteMetricsData.timeStamp
                )

                bearerConfigurationEntity.sessionId = baseLteData.sessionId
                bearerConfigurationEntity.uniqueId = baseLteData.uniqueId

                CoroutineScope(Dispatchers.IO).launch {
                    saveBearerConfigurationEntityToDatabase(bearerConfigurationEntity)
                    saveBearerEntity(bearerEntityList)
                }
            }else -> {
            EchoLocateLog.eLogI("Diagnostic : apiVersion not supported $apiVersion")
        }
        }

    }

    /**
     * Gets BearerEntity list from the data metrics source
     * @param source: List<String> data from data metrics to convert
     * @return List<CAData> r
     */
    private fun getBearerEntityList(
        source: List<String>,
        baseLteData: BaseLteData
    ): List<BearerEntity> {

        val bearerEntityList = mutableListOf<BearerEntity>()

        var apnNameIndex = 4
        var qciIndex = 3
        val range = NumberUtils.convertToInt(source[2])?: -2

        for (numberOfActiveBearers in range downTo 1 step 1) {
            val bearerEntity: BearerEntity = getBearerEntity(
                apnName = source[apnNameIndex],
                qci = NumberUtils.convertToInt(source[qciIndex])?: -2, // DIA-8993 If failed to check Report -2.
                sessionId = baseLteData.sessionId,
                uniqueId = baseLteData.uniqueId
            )

            bearerEntityList.add(bearerEntity)

            apnNameIndex += 2
            qciIndex += 2

            // if condition has been added to address bug DIA-8524 : App crash due to Fatal Exception
            // Issue : java.lang.ArrayIndexOutOfBoundsException length=11; index=12

            if(apnNameIndex >= source.size) {
                break
            }

        }

        return bearerEntityList
    }



    /**
     * Gets BearerEntity template for function [getBearerEntityList],
     * used for multiple BearerEntity data points
     * @param apnName: String
     * @param qci: Int
     * @param sessionId: String
     * @param uniqueId: String
     * @return BearerEntity
     */
    fun getBearerEntity(
        apnName: String,
        qci: Int,
        sessionId: String,
        uniqueId: String
    ): BearerEntity {

        val bearerEntity = BearerEntity(
            apnName = apnName,
            qci = qci
        )
        bearerEntity.sessionId = sessionId
        bearerEntity.uniqueId = uniqueId
        bearerEntity.baseEntityId = UUID.randomUUID().toString()

        return bearerEntity
    }

    /**
     * saves the BearerConfiguration Entity to database
     * @param bearerConfigurationEntity: [BearerConfigurationEntity] the object to save
     */
    private fun saveBearerConfigurationEntityToDatabase(bearerConfigurationEntity: BearerConfigurationEntity) {
        lteRepository.insertBearerConfiguration(bearerConfigurationEntity)
    }
}
