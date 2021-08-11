package com.tmobile.mytmobile.echolocate.nr5g.sa5g.dataprocessor

import android.content.Context
import com.tmobile.mytmobile.echolocate.nr5g.sa5g.database.entity.Sa5gRrcLogEntity
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
 * Collects Sa5gRrcLog data and saves the data in the database when focus gain or focus loss event is triggered
 */
class Sa5gRrcLogProcessor(val context: Context) : Sa5gBaseDataProcessor(context) {

    private var sa5gRrcLogDisposable: Disposable? = null
    /**
     * Sets the expected size of the source received from data metrics
     * @return Int returns the size of the source
     */
    override fun getExpectedSourceSize(): Int {
        return Sa5gConstants.SA5G_RRC_LOG_SIZE
    }

    /**
     * This function processes RrcLog and return disposable
     *  @param baseSa5gMetricsData: BaseSa5gMetricsData
     *  @param baseSa5gData: BaseSa5gData
     */
    override suspend fun processSa5gMetricsData(
        baseSa5gMetricsData: BaseSa5gMetricsData,
        baseSa5gData: BaseSa5gData
    ): Disposable? {
        sa5gRrcLogDisposable =  Observable.just(saveRrcLogData(baseSa5gMetricsData,
            baseSa5gData)).subscribeOn(
            Schedulers.io()).subscribe()
        return sa5gRrcLogDisposable
    }

    /**
     *  This function fetches Sa5gRrcLogEntity from source and saves it in database
     *  @param baseSa5gMetricsData: total metrics data to be processed
     *  @param baseSa5gData: [BaseSa5gData]
     */
    private fun saveRrcLogData(baseSa5gMetricsData: BaseSa5gMetricsData, baseSa5gData: BaseSa5gData){
        val sa5gRrcLogEntity = baseSa5gMetricsData.source as Sa5gRrcLogEntity

        sa5gRrcLogEntity.sessionId = baseSa5gData.sessionId
        sa5gRrcLogEntity.uniqueId = baseSa5gData.uniqueId

        CoroutineScope(Dispatchers.IO).launch {
            saveSa5gRrcLogToDatabase(sa5gRrcLogEntity)
        }
    }

    /**
     * Inserts the data in database
     * @param sa5gRrcLogEntity:[Sa5gRrcLogEntity]
     */
    private fun saveSa5gRrcLogToDatabase(sa5gRrcLogEntity: Sa5gRrcLogEntity) {
        sa5gRepository.insertSa5gRrcLogEntity(sa5gRrcLogEntity)
    }
}