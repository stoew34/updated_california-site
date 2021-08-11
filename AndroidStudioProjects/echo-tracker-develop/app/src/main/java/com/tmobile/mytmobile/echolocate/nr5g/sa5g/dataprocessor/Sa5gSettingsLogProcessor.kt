package com.tmobile.mytmobile.echolocate.nr5g.sa5g.dataprocessor

import android.content.Context
import com.tmobile.mytmobile.echolocate.nr5g.sa5g.database.entity.Sa5gSettingsLogEntity
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
 * Collects Sa5gSettingsLog data and saves the data in the database when focus gain or focus loss event is triggered
 */
class Sa5gSettingsLogProcessor(val context: Context) : Sa5gBaseDataProcessor(context) {

    private var sa5gSettingsLogDisposable: Disposable? = null
    /**
     * Sets the expected size of the source received from data metrics
     * @return Int returns the size of the source
     */
    override fun getExpectedSourceSize(): Int {
        return Sa5gConstants.SA5G_SETTINGS_LOG_SIZE
    }

    /**
     * This function processes down link carrier logs and return disposable
     *  @param baseSa5gMetricsData: BaseSa5gMetricsData
     *  @param baseSa5gData: BaseSa5gData
     */
    override suspend fun processSa5gMetricsData(
        baseSa5gMetricsData: BaseSa5gMetricsData,
        baseSa5gData: BaseSa5gData
    ): Disposable? {
        sa5gSettingsLogDisposable =  Observable.just(saveSettingsLogData(baseSa5gMetricsData,
            baseSa5gData)).subscribeOn(
            Schedulers.io()).subscribe()
        return sa5gSettingsLogDisposable
    }

    /**
     *  This function fetches Sa5gSettingsLogEntity from source and saves it in database
     *  @param baseSa5gMetricsData: total metrics data to be processed
     *  @param baseSa5gData: [BaseSa5gData]
     */
    private fun saveSettingsLogData(baseSa5gMetricsData: BaseSa5gMetricsData, baseSa5gData: BaseSa5gData) {
        val sa5gSettingsLogEntity = baseSa5gMetricsData.source as Sa5gSettingsLogEntity

        sa5gSettingsLogEntity.sessionId = baseSa5gData.sessionId
        sa5gSettingsLogEntity.uniqueId = baseSa5gData.uniqueId

        CoroutineScope(Dispatchers.IO).launch {
            saveSa5gSettingsLogEntityToDatabase(sa5gSettingsLogEntity)
        }
    }

    /**
     * Inserts the data in database
     * @param sa5gSettingsLogEntity:[Sa5gSettingsLogEntity]
     */
    private fun saveSa5gSettingsLogEntityToDatabase(sa5gSettingsLogEntity: Sa5gSettingsLogEntity) {
        sa5gRepository.insertSa5gSettingsLogEntity(sa5gSettingsLogEntity)
    }
}