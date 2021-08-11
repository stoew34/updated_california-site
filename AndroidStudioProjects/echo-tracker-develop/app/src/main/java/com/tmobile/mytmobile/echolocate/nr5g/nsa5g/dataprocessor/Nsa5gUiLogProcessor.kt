package com.tmobile.mytmobile.echolocate.nr5g.nsa5g.dataprocessor

import android.content.Context
import com.tmobile.mytmobile.echolocate.nr5g.nsa5g.database.entity.Nr5gUiLogEntity
import com.tmobile.mytmobile.echolocate.nr5g.nsa5g.model.BaseNr5gData
import com.tmobile.mytmobile.echolocate.nr5g.nsa5g.model.BaseNr5gMetricsData
import com.tmobile.mytmobile.echolocate.nr5g.nsa5g.utils.Nsa5gConstants
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * This class is responsible to collect the Nr5g UI Log data using reflection API
 */
class Nsa5gUiLogProcessor(var context: Context) : Nsa5gBaseDataProcessor(context) {
    private var nsa5gUiLogDisposable: Disposable? = null
    /**
     * Sets the expected size of the source received from data metrics
     * @return Int returns the size of the source
     */
    override fun getExpectedSourceSize(): Int {
        return Nsa5gConstants.NR5G_UI_LOG_SIZE
    }

    /**
     *  This function processes UiLog and return disposable
     *  @param baseNr5gMetricsData: BaseNr5gMetricsData
     *  @param baseNr5gData: BaseNr5gData
     */
    override suspend fun processNr5gMetricsData(
        baseNr5gMetricsData: BaseNr5gMetricsData,
        baseNr5gData: BaseNr5gData
    ): Disposable? {
        nsa5gUiLogDisposable =  Observable.just(saveNsa5gUiLogData(baseNr5gMetricsData,
            baseNr5gData)).subscribeOn(
            Schedulers.io()).subscribe()
        return nsa5gUiLogDisposable
    }

    /**
     *  This function fetches Nr5gUiLogEntity from source and saves it in database
     *  @param baseNr5gMetricsData: total metrics data to be processed
     *  @param baseNr5gData: [BaseNr5gData]
     */
    private fun saveNsa5gUiLogData(baseNr5gMetricsData: BaseNr5gMetricsData, baseNr5gData: BaseNr5gData){
        val nr5gUiLogEntity = baseNr5gMetricsData.source as Nr5gUiLogEntity

        nr5gUiLogEntity.sessionId = baseNr5gData.sessionId
        nr5gUiLogEntity.uniqueId = baseNr5gData.uniqueId

        CoroutineScope(Dispatchers.IO).launch {
            saveNr5gUiLogToDatabase(nr5gUiLogEntity)
        }
    }

    /**
     * Inserts the data in database
     * @param nr5gUiLogEntity:[Nr5gUiLogEntity]
     */
    private fun saveNr5gUiLogToDatabase(nr5gUiLogEntity: Nr5gUiLogEntity) {
        nr5gRepository.insertNr5gUiLog(nr5gUiLogEntity)
    }
}