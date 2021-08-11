package com.tmobile.mytmobile.echolocate.nr5g.sa5g.dataprocessor

import android.content.Context
import com.tmobile.mytmobile.echolocate.nr5g.sa5g.database.entity.Sa5gUiLogEntity
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
 * Collects Sa5gUiLog data and saves the data in the database when focus gain or focus loss event is triggered
 */
class Sa5gUiLogProcessor(val context: Context) : Sa5gBaseDataProcessor(context) {
    private var sa5gUiLogDisposable: Disposable? = null
    /**
     * Sets the expected size of the source received from data metrics
     * @return Int returns the size of the source
     */
    override fun getExpectedSourceSize(): Int {
        return Sa5gConstants.SA5G_UI_LOG_SIZE
    }

    /**
     * This function processes Ui Log and return disposable
     *  @param baseSa5gMetricsData: BaseSa5gMetricsData
     *  @param baseSa5gData: BaseSa5gData
     */
    override suspend fun processSa5gMetricsData(
        baseSa5gMetricsData: BaseSa5gMetricsData,
        baseSa5gData: BaseSa5gData
    ): Disposable? {
        sa5gUiLogDisposable =  Observable.just(saveUiLogData(baseSa5gMetricsData,
            baseSa5gData)).subscribeOn(
            Schedulers.io()).subscribe()
        return sa5gUiLogDisposable
    }

    /**
     *  This function fetches Sa5gUiLogEntity from source and saves it in database
     *  @param baseSa5gMetricsData: total metrics data to be processed
     *  @param baseSa5gData: [BaseSa5gData]
     */
    private fun saveUiLogData(baseSa5gMetricsData: BaseSa5gMetricsData, baseSa5gData: BaseSa5gData) {
        val sa5gUiLogEntity = baseSa5gMetricsData.source as Sa5gUiLogEntity

        sa5gUiLogEntity.sessionId = baseSa5gData.sessionId
        sa5gUiLogEntity.uniqueId = baseSa5gData.uniqueId

        CoroutineScope(Dispatchers.IO).launch {
            saveSa5gUiLogEntityToDatabase(sa5gUiLogEntity)
        }
    }

    /**
     * Inserts the data in database
     * @param sa5gUiLogEntity:[Sa5gUiLogEntity]
     */
    private fun saveSa5gUiLogEntityToDatabase(sa5gUiLogEntity: Sa5gUiLogEntity) {
        sa5gRepository.insertSa5gUiLogEntity(sa5gUiLogEntity)
    }
}