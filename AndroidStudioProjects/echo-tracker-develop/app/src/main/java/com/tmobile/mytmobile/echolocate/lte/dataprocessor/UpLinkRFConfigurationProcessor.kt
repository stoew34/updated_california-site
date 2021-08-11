package com.tmobile.mytmobile.echolocate.lte.dataprocessor

import android.content.Context
import com.tmobile.mytmobile.echolocate.lte.oemdata.LteBaseDataMetricsWrapper
import com.tmobile.mytmobile.echolocate.lte.database.entity.CAEntity
import com.tmobile.mytmobile.echolocate.lte.database.entity.UpLinkRFConfigurationEntity
import com.tmobile.mytmobile.echolocate.lte.model.BaseLteData
import com.tmobile.mytmobile.echolocate.lte.model.LteMetricsData
import com.tmobile.mytmobile.echolocate.lte.utils.LteConstants
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

/** Json example according to schema
 *
"upLinkRFConfiguration": {
"CA": [
{
"carrierNum": 1,
"modulation": "-2"
},
{
"carrierNum": 2,
"modulation": "-1"
}
],
"networkType": "1",
"oemTimestamp": "2018-05-14T08:54:52.978-0700"
}
 */

/**
 * Processes UpLinkRFConfiguration carrier data and saves the data in the database when focus gain or focus loss event is triggered
 */
class UpLinkRFConfigurationProcessor(context: Context) : BaseLteDataProcessor(context) {

    /**
     * SOURCE_SIZE_VERSION_NA
     *
     * Source size of version not available
     * value 14
     */
    private val SOURCE_SIZE_VERSION_NA = 14

    /**
     * SOURCE_SIZE_VERSION_1
     *
     * Source size of version 1
     * value 4
     */
    private val SOURCE_SIZE_VERSION_1 = 4

    /**
     * SOURCE_SIZE_VERSION_3
     *
     * Source size of version 3
     * value 4
     */
    private val SOURCE_SIZE_VERSION_3 = 4
    private var upLinkRFConfigurationDisposable: Disposable? = null

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
     *  This function processes upLinkRFConfiguration and return disposable
     *  @param lteMetricsData: LteMetricsData
     *  @param baseLteData: BaseLteData
     */
    override suspend fun processLteMetricsData(
        lteMetricsData: LteMetricsData,
        baseLteData: BaseLteData
    ): Disposable? {
        upLinkRFConfigurationDisposable =  Observable.just(saveUpLinkRFConfigurationData(lteMetricsData,
            baseLteData)).subscribeOn(
            Schedulers.io()).subscribe()
        return upLinkRFConfigurationDisposable
    }

    /**
     *  This function processes the list, converts it to upLinkRFConfigurationEntity and saves it in database
     *  @param lteMetricsData: total metrics data to be processed
     *  @param baseLteData: BaseLteData
     */
    private fun saveUpLinkRFConfigurationData(lteMetricsData: LteMetricsData, baseLteData: BaseLteData){
        val upLinkRFConfigurationEntity = UpLinkRFConfigurationEntity(
            networkType = lteMetricsData.source[1],
            oemTimestamp = lteMetricsData.timeStamp
        )

        val caList = getCAEntityList(lteMetricsData.source, baseLteData)
        upLinkRFConfigurationEntity.sessionId = baseLteData.sessionId
        upLinkRFConfigurationEntity.uniqueId = baseLteData.uniqueId

        CoroutineScope(Dispatchers.IO).launch {
            saveUpLinkRFConfigurationToDatabase(upLinkRFConfigurationEntity)
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
                source[2], source[8]
            )
            LteBaseDataMetricsWrapper.ApiVersion.VERSION_1,
            LteBaseDataMetricsWrapper.ApiVersion.VERSION_3 -> createEntityList(
                baseLteData,
                source[2], source[3]
            )
        }
    }


    /**
     * This function is responsible to provide the CAList based on the apiVersion
     * @param baseLteData :which provides the source list
     */
    private fun createEntityList(
        baseLteData: BaseLteData, modulationPrimary: String?,
        modulationSecondary: String?
    ): List<CAEntity> {
        val caEntityList = mutableListOf<CAEntity>()
        val caEntityPrimary = CAEntity(
            null,
            null,
            null,
            LteConstants.PRIMARY_CA,
            null,
            modulationPrimary?: "",
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
            null,
            modulationSecondary?: "",
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
     * saves the UpLinkRFConfigurationEntity object to database
     * @param upLinkRFConfigurationEntity: [UpLinkRFConfiguration] the object to save
     */
    private fun saveUpLinkRFConfigurationToDatabase(upLinkRFConfigurationEntity: UpLinkRFConfigurationEntity) {
        lteRepository.insertUpLinkRFConfiguration(upLinkRFConfigurationEntity)

    }
}