package com.tmobile.mytmobile.echolocate.nr5g.nsa5g.dataprocessor

import android.content.Context
import com.tmobile.mytmobile.echolocate.nr5g.nsa5g.database.entity.EndcLteLogEntity
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
 * capture the endcLteLog
 */

class Nsa5gEndcLteLogProcessor(val context: Context) : Nsa5gBaseDataProcessor(context) {

    private var nsa5gEndcLteLogDisposable: Disposable? = null
    /**
     * Sets the expected size of the source received from data metrics
     * @return Int returns the size of the source
     */
    override fun getExpectedSourceSize(): Int {
        return Nsa5gConstants.END_C_LTE_LOG_SIZE
    }

    /**
     *  This function processes endcLteLogEntity and return disposable
     *  @param baseNr5gMetricsData: BaseNr5gMetricsData
     *  @param baseNr5gData: BaseNr5gData
     */
    override suspend fun processNr5gMetricsData(
        baseNr5gMetricsData: BaseNr5gMetricsData,
        baseNr5gData: BaseNr5gData
    ): Disposable? {
        nsa5gEndcLteLogDisposable =  Observable.just(saveNsa5gEndcLteLogData(baseNr5gMetricsData,
            baseNr5gData)).subscribeOn(
            Schedulers.io()).subscribe()
        return nsa5gEndcLteLogDisposable
    }

    /**
     *  This function fetches endcLteLogEntity from source and saves it in database
     *  @param baseNr5gMetricsData: total metrics data to be processed
     *  @param baseNr5gData: [BaseNr5gData]
     */
    private fun saveNsa5gEndcLteLogData(baseNr5gMetricsData: BaseNr5gMetricsData, baseNr5gData: BaseNr5gData){
        val endcLteLogEntity = baseNr5gMetricsData.source as EndcLteLogEntity

        endcLteLogEntity.sessionId = baseNr5gData.sessionId
        endcLteLogEntity.uniqueId = baseNr5gData.uniqueId

        CoroutineScope(Dispatchers.IO).launch {
            saveEndcLteLogToDatabase(endcLteLogEntity)
        }
    }

    /**
     * saves the EndcLteLogEntity object to database
     * @param endcLteLogEntity: [EndcLteLogEntity] the object to save
     */
    private fun saveEndcLteLogToDatabase(endcLteLogEntity: EndcLteLogEntity) {
        nr5gRepository.insertEndcLteLogEntity(endcLteLogEntity)
    }
}
