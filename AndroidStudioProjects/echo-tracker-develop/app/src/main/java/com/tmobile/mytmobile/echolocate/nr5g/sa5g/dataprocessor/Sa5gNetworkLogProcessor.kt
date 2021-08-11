package com.tmobile.mytmobile.echolocate.nr5g.sa5g.dataprocessor

import android.content.Context
import com.tmobile.mytmobile.echolocate.nr5g.sa5g.database.entity.Sa5gNetworkLogEntity
import com.tmobile.mytmobile.echolocate.nr5g.sa5g.model.BaseSa5gData
import com.tmobile.mytmobile.echolocate.nr5g.sa5g.model.BaseSa5gMetricsData
import com.tmobile.mytmobile.echolocate.nr5g.sa5g.utils.Sa5gConstants
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Collects Sa5gNetworkLog data and saves the data in the database when focus gain or focus loss event is triggered
 */
class Sa5gNetworkLogProcessor(val context: Context) : Sa5gBaseDataProcessor(context) {

    private var sa5gNetworkLogDisposable: Disposable? = null
    /**
     * Sets the expected size of the source received from data metrics
     * @return Int returns the size of the source
     */
    override fun getExpectedSourceSize(): Int {
        return Sa5gConstants.SA5G_NETWORK_LOG_SIZE
    }
    /**
     * This function processes OEMSV and return disposable
     *  @param baseSa5gMetricsData: BaseSa5gMetricsData
     *  @param baseSa5gData: BaseSa5gData
     */
    override suspend fun processSa5gMetricsData(
        baseSa5gMetricsData: BaseSa5gMetricsData,
        baseSa5gData: BaseSa5gData
    ): Disposable? {
        sa5gNetworkLogDisposable =  Observable.just(saveNetworkLogData(baseSa5gMetricsData,
            baseSa5gData)).subscribeOn(
            Schedulers.io()).subscribe()
        return sa5gNetworkLogDisposable
    }

    /**
     *  This function fetches Sa5gNetworkLogEntity from source and saves it in database
     *  @param baseSa5gMetricsData: total metrics data to be processed
     *  @param baseSa5gData: [BaseSa5gData]
     */
    private fun saveNetworkLogData(baseSa5gMetricsData: BaseSa5gMetricsData, baseSa5gData: BaseSa5gData) {
        val sa5gNetworkLogEntity = baseSa5gMetricsData.source as Sa5gNetworkLogEntity

        sa5gNetworkLogEntity.sessionId = baseSa5gData.sessionId
        sa5gNetworkLogEntity.uniqueId = baseSa5gData.uniqueId

        CoroutineScope(Dispatchers.IO).launch {
            saveSa5gNetworkLogToDatabase(sa5gNetworkLogEntity)
        }
    }

    /**
     * Inserts the data in database
     * @param sa5gNetworkLogEntity:[Sa5gNetworkLogEntity]
     */
    private fun saveSa5gNetworkLogToDatabase(sa5gNetworkLogEntity: Sa5gNetworkLogEntity) {
        sa5gRepository.insertSa5gNetworkLogEntity(sa5gNetworkLogEntity)
    }
}