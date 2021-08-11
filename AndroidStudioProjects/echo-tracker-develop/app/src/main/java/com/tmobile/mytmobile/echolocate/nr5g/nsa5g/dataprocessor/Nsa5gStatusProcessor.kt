package com.tmobile.mytmobile.echolocate.nr5g.nsa5g.dataprocessor

import android.content.Context
import com.tmobile.mytmobile.echolocate.nr5g.nsa5g.database.entity.Nr5gStatusEntity
import com.tmobile.mytmobile.echolocate.nr5g.nsa5g.model.BaseNr5gData
import com.tmobile.mytmobile.echolocate.nr5g.nsa5g.model.BaseNr5gMetricsData
import com.tmobile.mytmobile.echolocate.nr5g.nsa5g.utils.Nsa5gConstants
import com.tmobile.mytmobile.echolocate.nr5g.nsa5g.utils.Nsa5gDataCollectionService
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class Nsa5gStatusProcessor(var context: Context) : Nsa5gBaseDataProcessor(context) {

    var dataCollectionService =
        Nsa5gDataCollectionService()
    private var nsa5gStatusDisposable: Disposable? = null

    /**
     * Sets the expected size of the source received from data metrics
     * If define 0 - not from metrics
     * @return Int returns the size of the source
     */
    override fun getExpectedSourceSize(): Int {
        return Nsa5gConstants.NR_STATUS_EXPECTED_SIZE
    }

    /**
     *  This function processes Status and return disposable
     *  @param baseNr5gMetricsData: BaseNr5gMetricsData
     *  @param baseNr5gData: BaseNr5gData
     */
    override suspend fun processNr5gMetricsData(
        baseNr5gMetricsData: BaseNr5gMetricsData,
        baseNr5gData: BaseNr5gData
    ): Disposable? {
        nsa5gStatusDisposable =  Observable.just(saveNsa5gStatusData(baseNr5gMetricsData,
            baseNr5gData)).subscribeOn(
            Schedulers.io()).subscribe()
        return nsa5gStatusDisposable
    }

    /**
     *  This function processes the data, converts it to Nr5gStatusEntity and saves it in database
     *  @param baseNr5gMetricsData: total metrics data to be processed
     *  @param baseNr5gData: [BaseNr5gData]
     */
    private fun saveNsa5gStatusData(baseNr5gMetricsData: BaseNr5gMetricsData, baseNr5gData: BaseNr5gData){
        val nr5gStatusEntity = dataCollectionService.getNrStatus(context)
        if (nr5gStatusEntity != null) {
            nr5gStatusEntity.sessionId = baseNr5gData.sessionId
            nr5gStatusEntity.uniqueId = baseNr5gData.uniqueId

            CoroutineScope(Dispatchers.IO).launch {
                saveNrStatusToDatabase(nr5gStatusEntity)
            }
        }
    }

    /**
     * saves Nr5gOEMSVEntity object to database
     * @param nr5gStatusEntity: [Nr5gStatusEntity] the object to save
     */
    private fun saveNrStatusToDatabase(nr5gStatusEntity: Nr5gStatusEntity) {
        nr5gRepository.insertNr5gStatusEntity(nr5gStatusEntity)
    }


}