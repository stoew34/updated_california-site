package com.tmobile.mytmobile.echolocate.nr5g.nsa5g.dataprocessor

import android.content.Context
import com.tmobile.mytmobile.echolocate.nr5g.nsa5g.database.entity.EndcUplinkLogEntity
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
 * Collects EndcUplinkLog data and saves the data in the database when focus gain or focus loss event is triggered
 */
class Nsa5gEndcUplinkLogProcessor(context: Context) : Nsa5gBaseDataProcessor(context) {

    private var nsa5gEndcUplinkLogDisposable: Disposable? = null
    /**
     * Sets the expected size of the source received from data metrics
     * @return Int returns the size of the source
     */
    override fun getExpectedSourceSize(): Int {
        return Nsa5gConstants.END_C_UPLINK_LOG_SIZE
    }

    /**
     *  This function processes Endc Uplink Log and return disposable
     *  @param baseNr5gMetricsData: BaseNr5gMetricsData
     *  @param baseNr5gData: BaseNr5gData
     */
    override suspend fun processNr5gMetricsData(
        baseNr5gMetricsData: BaseNr5gMetricsData,
        baseNr5gData: BaseNr5gData
    ): Disposable? {
        nsa5gEndcUplinkLogDisposable =  Observable.just(saveNsa5gEndcUplinkLogData(baseNr5gMetricsData,
            baseNr5gData)).subscribeOn(
            Schedulers.io()).subscribe()
        return nsa5gEndcUplinkLogDisposable
    }

    /**
     *  This function fetches EndcUplinkLogEntity from source and saves it in database
     *  @param baseNr5gMetricsData: total metrics data to be processed
     *  @param baseNr5gData: [BaseNr5gData]
     */
    private fun saveNsa5gEndcUplinkLogData(baseNr5gMetricsData: BaseNr5gMetricsData, baseNr5gData: BaseNr5gData){
        val endcUplinkLogEntity = baseNr5gMetricsData.source as EndcUplinkLogEntity

        endcUplinkLogEntity.sessionId = baseNr5gData.sessionId
        endcUplinkLogEntity.uniqueId = baseNr5gData.uniqueId

        CoroutineScope(Dispatchers.IO).launch {
            saveEndcLteLogToDatabase(endcUplinkLogEntity)
        }
    }

    /**
     * saves the EndcUplinkLogEntity object to database
     * @param endcUplinkLogEntity: [EndcUplinkLogEntity] the object to save
     */
    private fun saveEndcLteLogToDatabase(endcUplinkLogEntity: EndcUplinkLogEntity) {
        nr5gRepository.insertEndcUplinkLogEntity(endcUplinkLogEntity)
    }
}