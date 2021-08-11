package com.tmobile.mytmobile.echolocate.lte.dataprocessor

import android.content.Context
import com.tmobile.mytmobile.echolocate.lte.oemdata.LteBaseDataMetricsWrapper
import com.tmobile.mytmobile.echolocate.lte.database.entity.CAEntity
import com.tmobile.mytmobile.echolocate.lte.database.entity.NetworkIdentityEntity
import com.tmobile.mytmobile.echolocate.lte.model.BaseLteData
import com.tmobile.mytmobile.echolocate.lte.model.LteMetricsData
import com.tmobile.mytmobile.echolocate.lte.utils.LteConstants
import com.tmobile.mytmobile.echolocate.utils.ConnectionCheckUtils
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
//import kotlinx.serialization.ImplicitReflectionSerializer
import java.util.*

/**
 * Processes network identity data and saves the data in the database
 * when focus gain or focus loss event is triggered
 */
class NetworkIdentityProcessor(val context: Context) : BaseLteDataProcessor(context) {

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
     * value 11
     */
    private val SOURCE_SIZE_VERSION_1 = 11
    /**
     * SOURCE_SIZE_VERSION_3
     *
     * Source size of version 3
     * value 11
     */
    private val SOURCE_SIZE_VERSION_3 = 11
    /**
     * WIFI_CONNECTED_STATE
     * value 2
     */
    private val WIFI_CONNECTED_STATE = "2"
    /**
     * WIFI_NOT_CONNECTED_STATE
     * value 1
     */
    private val WIFI_NOT_CONNECTED_STATE = "1"
    private var networkIdentityDisposable: Disposable? = null

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
     *  This function processes networkIdentityEntity and return disposable
     *  @param lteMetricsData: LteMetricsData
     *  @param baseLteData: BaseLteData
     */
//    @ImplicitReflectionSerializer
    override suspend fun processLteMetricsData(
        lteMetricsData: LteMetricsData,
        baseLteData: BaseLteData
    ): Disposable? {
        networkIdentityDisposable = Observable.just(
            saveNetworkIdentityData(
                lteMetricsData,
                baseLteData
            )
        ).subscribeOn(
            Schedulers.io()
        ).subscribe()
        return networkIdentityDisposable
    }

    /**
     *  This function processes the list, converts it to networkIdentityEntity and saves it in database
     *  @param lteMetricsData: total metrics data to be processed
     *  @param baseLteData: BaseLteData
     */
    private fun saveNetworkIdentityData(lteMetricsData: LteMetricsData, baseLteData: BaseLteData) {
        val networkIdentityEntity = NetworkIdentityEntity(
            lteMetricsData.source[2],
            lteMetricsData.source[3],
            lteMetricsData.source[4],
            lteMetricsData.source[1],
            lteMetricsData.timeStamp,
            if (ConnectionCheckUtils.checkIsWifi(context)) WIFI_CONNECTED_STATE else WIFI_NOT_CONNECTED_STATE
        )
        val caList = getNetworkIdentityList(lteMetricsData.source, baseLteData)

        networkIdentityEntity.sessionId = baseLteData.sessionId
        networkIdentityEntity.uniqueId = baseLteData.uniqueId

        CoroutineScope(Dispatchers.IO).launch {
            saveNetworkIdentityInfoToDatabase(networkIdentityEntity)
            saveCAToDatabase(caList)
        }
    }

    /**
     * Gets CAEntity list from the data metrics source
     * @param source: List<String> data from data metrics to convert
     * @return List<CAData> r
     */
    private fun getNetworkIdentityList(source: List<String>, baseLteData: BaseLteData): List<CAEntity> {
        val caList = mutableListOf<CAEntity>()
        val caEntityPrimary = CAEntity(
            null,
            null,
            null,
            LteConstants.PRIMARY_CA,
            null,
            null,
            source[7]?: "",
            source[6]?: "",
            source[5]?: ""
        )
        caEntityPrimary.sessionId = baseLteData.sessionId
        caEntityPrimary.uniqueId = baseLteData.uniqueId
        caEntityPrimary.baseEntityId = UUID.randomUUID().toString()

        val caEntitySecondary = CAEntity(
            null,
            null,
            null,
            LteConstants.SECONDARY_CA,
            null,
            null,
            source[8]?: "",
            null,
            null
        )
        caEntitySecondary.sessionId = baseLteData.sessionId
        caEntitySecondary.uniqueId = baseLteData.uniqueId
        caEntitySecondary.baseEntityId = UUID.randomUUID().toString()

        val caEntityTertiary = CAEntity(
            null,
            null,
            null,
            LteConstants.TERTIARY_CA,
            null,
            null,
            source[9]?: "",
            source[10]?: "",
            null
        )
        caEntityTertiary.sessionId = baseLteData.sessionId
        caEntityTertiary.uniqueId = baseLteData.uniqueId
        caEntityTertiary.baseEntityId = UUID.randomUUID().toString()

        caList.add(caEntityPrimary)
        caList.add(caEntitySecondary)
        caList.add(caEntityTertiary)

        return caList
    }

    /**
     * saves the networkIdentityInfo object to database
     * @param networkIdentityEntity: [networkIdentityEntity] the object to save
     */
    private fun saveNetworkIdentityInfoToDatabase(networkIdentityEntity: NetworkIdentityEntity) {
        lteRepository.insertNetworkIdentityEntity(networkIdentityEntity)
    }

    /**
     * saves the caEntityList object to database
     * @param caEntityList: [CAEntity] the object to save
     */
    private fun saveCAToDatabase(caEntityList: List<CAEntity>) {
        lteRepository.insertAllCAEntityEntity(caEntityList)
    }
}
