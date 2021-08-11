package com.tmobile.mytmobile.echolocate.nr5g.nsa5g.dataprocessor

import android.content.Context
import com.tmobile.mytmobile.echolocate.nr5g.core.oemdata.Nr5gBaseDataMetricsWrapper
import com.tmobile.mytmobile.echolocate.nr5g.nsa5g.database.entity.Nr5gNetworkIdentityEntity
import com.tmobile.mytmobile.echolocate.nr5g.nsa5g.model.BaseNr5gData
import com.tmobile.mytmobile.echolocate.nr5g.nsa5g.model.BaseNr5gMetricsData
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Processes network identity data and saves the data in the database
 * when focus gain or focus loss event is triggered
 */
class Nsa5gNetworkIdentityProcessor(val context: Context) : Nsa5gBaseDataProcessor(context) {
    private var nsa5gNetworkIdentityDisposable: Disposable? = null
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
     * SOURCE_SIZE_VERSION_1
     *
     * Source size of version 1
     * value 11
     */
    private val SOURCE_SIZE_VERSION_2 = 11

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

    /**
     * Sets the expected size of the source received from data metrics
     * @return Int returns the size of the source
     */
    override fun getExpectedSourceSize(): Int {
        return when (apiVersion) {
            Nr5gBaseDataMetricsWrapper.ApiVersion.UNKNOWN_VERSION -> SOURCE_SIZE_VERSION_NA
            Nr5gBaseDataMetricsWrapper.ApiVersion.VERSION_1 -> SOURCE_SIZE_VERSION_1
            Nr5gBaseDataMetricsWrapper.ApiVersion.VERSION_3 -> SOURCE_SIZE_VERSION_3
        }
    }
    /**
     *  This function processes network identity and return disposable
     *  @param baseNr5gMetricsData: BaseNr5gMetricsData
     *  @param baseNr5gData: BaseNr5gData
     */
    override suspend fun processNr5gMetricsData(
        baseNr5gMetricsData: BaseNr5gMetricsData,
        baseNr5gData: BaseNr5gData
    ): Disposable? {
        nsa5gNetworkIdentityDisposable =  Observable.just(saveNetworkIdentityData(baseNr5gMetricsData,
            baseNr5gData)).subscribeOn(
            Schedulers.io()).subscribe()
        return nsa5gNetworkIdentityDisposable
    }

    /**
     *  This function processes the list, converts it to networkIdentityEntity and saves it in database
     *  @param baseNr5gMetricsData: total metrics data to be processed
     *  @param baseNr5gData: BaseNr5gData
     */
    private fun saveNetworkIdentityData(baseNr5gMetricsData: BaseNr5gMetricsData, baseNr5gData: BaseNr5gData) {
        val strData: List<String> = baseNr5gMetricsData.source as List<String>
        val nr5gNetworkIdentityEntity = Nr5gNetworkIdentityEntity(
            baseNr5gMetricsData.timeStamp,
            strData[1].toInt(),
            strData[2],
            strData[3],
            strData[4].toInt(),
            strData[5].toInt(),
            strData[6].toInt(),
            strData[7].toInt()
        )
        nr5gNetworkIdentityEntity.sessionId = baseNr5gData.sessionId
        nr5gNetworkIdentityEntity.uniqueId = baseNr5gData.uniqueId

        CoroutineScope(Dispatchers.IO).launch {
            saveNetworkIdentityInfoToDatabase(nr5gNetworkIdentityEntity)
        }
    }

    /**
     * saves the Nr5gNetworkIdentityEntity object to database
     * @param nr5gNetworkIdentityEntity: [Nr5gNetworkIdentityEntity] the object to save
     */
    private fun saveNetworkIdentityInfoToDatabase(nr5gNetworkIdentityEntity: Nr5gNetworkIdentityEntity) {
        nr5gRepository.insertNr5gNetworkIdentityEntity(nr5gNetworkIdentityEntity)
    }
}
