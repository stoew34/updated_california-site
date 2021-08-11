package com.tmobile.mytmobile.echolocate.nr5g.nsa5g.dataprocessor

import android.content.Context
import com.tmobile.mytmobile.echolocate.nr5g.nsa5g.database.entity.Nr5gMmwCellLogEntity
import com.tmobile.mytmobile.echolocate.nr5g.nsa5g.model.BaseNr5gData
import com.tmobile.mytmobile.echolocate.nr5g.nsa5g.model.BaseNr5gMetricsData
import com.tmobile.mytmobile.echolocate.nr5g.nsa5g.utils.Nsa5gConstants
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
//import kotlinx.serialization.ImplicitReflectionSerializer

/**
 * Collects Nr5gMmwCellLog data and saves the data in the database when focus gain or focus loss event is triggered
 */
class Nsa5gMmwCellLogProcessor(val context: Context) : Nsa5gBaseDataProcessor(context) {

    private var nsa5gMmwCellLogDisposable: Disposable? = null
    /**
     * Sets the expected size of the source received from data metrics
     * @return Int returns the size of the source
     */
    override fun getExpectedSourceSize(): Int {
        return Nsa5gConstants.NR5G_MMW_CELL_LOG_SIZE
    }

    /**
     *  This function processes MmwCellLog and return disposable
     *  @param baseNr5gMetricsData: BaseNr5gMetricsData
     *  @param baseNr5gData: BaseNr5gData
     */
//    @ImplicitReflectionSerializer
    override suspend fun processNr5gMetricsData(
        baseNr5gMetricsData: BaseNr5gMetricsData,
        baseNr5gData: BaseNr5gData
    ): Disposable? {
        nsa5gMmwCellLogDisposable =  Observable.just(saveMmwCellLogData(baseNr5gMetricsData,
            baseNr5gData)).subscribeOn(
            Schedulers.io()).subscribe()
        return nsa5gMmwCellLogDisposable
    }

    /**
     *  This function fetches Nr5gMmwCellLogEntity from source and saves it in database
     *  @param baseNr5gMetricsData: total metrics data to be processed
     *  @param baseNr5gData: [BaseNr5gData]
     */
    private fun saveMmwCellLogData(baseNr5gMetricsData: BaseNr5gMetricsData, baseNr5gData: BaseNr5gData){
        val nr5gMmwCellLogEntity = baseNr5gMetricsData.source as Nr5gMmwCellLogEntity

        nr5gMmwCellLogEntity.sessionId = baseNr5gData.sessionId
        nr5gMmwCellLogEntity.uniqueId = baseNr5gData.uniqueId

        CoroutineScope(Dispatchers.IO).launch {
            saveNr5gMmwCellLogToDatabase(nr5gMmwCellLogEntity)
        }
    }

    /**
     * Inserts the data in database
     * @param nr5gMmwCellLogEntity:[Nr5gMmwCellLogEntity]
     */
    private fun saveNr5gMmwCellLogToDatabase(nr5gMmwCellLogEntity: Nr5gMmwCellLogEntity) {
        nr5gRepository.insertMmwCellLogEntity(nr5gMmwCellLogEntity)
    }
}