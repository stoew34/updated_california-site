package com.tmobile.mytmobile.echolocate.nr5g.sa5g.dataprocessor

import android.content.Context
import com.tmobile.mytmobile.echolocate.nr5g.sa5g.database.entity.Sa5gCarrierConfigEntity
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
 * Collects Sa5gCarrierConfig data and saves the data in the database when event triggered
 */
class Sa5gCarrierConfigProcessor(val context: Context) : Sa5gBaseDataProcessor(context) {

    private var sa5gCarrierConfigDisposable: Disposable? = null

    /**
     * Sets the expected size of the source received from data metrics
     * @return Int returns the size of the source
     */
    override fun getExpectedSourceSize(): Int {
        return Sa5gConstants.SA5G_CARRIER_CONFIG
    }

    /**
     * This function processes active network and return disposable
     *  @param baseSa5gMetricsData: BaseSa5gMetricsData
     *  @param baseSa5gData: BaseSa5gData
     */
    override suspend fun processSa5gMetricsData(
        baseSa5gMetricsData: BaseSa5gMetricsData,
        baseSa5gData: BaseSa5gData
    ): Disposable? {
        sa5gCarrierConfigDisposable =  Observable.just(saveCarrierConfigData(baseSa5gMetricsData,
            baseSa5gData)).subscribeOn(
            Schedulers.io()).subscribe()
        return sa5gCarrierConfigDisposable
    }

    /**
     *  This function fetches Sa5gUiLogEntity from source and saves it in database
     *  @param baseSa5gMetricsData: total metrics data to be processed
     *  @param baseSa5gData: [BaseSa5gData]
     */
    //TODO complete immplementtion when Classname will be available from OEM
    private fun saveCarrierConfigData(baseSa5gMetricsData: BaseSa5gMetricsData, baseSa5gData: BaseSa5gData){
        val sa5gCarrierConfigEntity = baseSa5gMetricsData.source as Sa5gCarrierConfigEntity

        sa5gCarrierConfigEntity.sessionId = baseSa5gData.sessionId
        sa5gCarrierConfigEntity.uniqueId = baseSa5gData.uniqueId

        CoroutineScope(Dispatchers.IO).launch {
            saveSa5gUiLogEntityToDatabase(sa5gCarrierConfigEntity)
        }
    }

    /**
     * Inserts the data in database
     * @param sa5gCarrierConfigEntity:[Sa5gCarrierConfigEntity]
     */
    private fun saveSa5gUiLogEntityToDatabase(sa5gCarrierConfigEntity: Sa5gCarrierConfigEntity) {
        sa5gRepository.insertSa5gCarrierConfigEntity(sa5gCarrierConfigEntity)
    }
}