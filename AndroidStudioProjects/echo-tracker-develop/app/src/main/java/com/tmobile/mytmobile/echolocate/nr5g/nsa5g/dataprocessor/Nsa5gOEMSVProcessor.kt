package com.tmobile.mytmobile.echolocate.nr5g.nsa5g.dataprocessor

import android.content.Context
import com.tmobile.mytmobile.echolocate.nr5g.core.utils.Nr5gOEMSoftwareVersionCollector
import com.tmobile.mytmobile.echolocate.nr5g.nsa5g.database.entity.Nr5gOEMSVEntity
import com.tmobile.mytmobile.echolocate.nr5g.nsa5g.model.BaseNr5gData
import com.tmobile.mytmobile.echolocate.nr5g.nsa5g.model.BaseNr5gMetricsData
import com.tmobile.mytmobile.echolocate.nr5g.nsa5g.utils.Nsa5gConstants
import com.tmobile.mytmobile.echolocate.nr5g.nsa5g.utils.Nsa5gEntityConverter
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class Nsa5gOEMSVProcessor(var context: Context) : Nsa5gBaseDataProcessor(context) {

    private var nsa5gOEMSVDisposable: Disposable? = null
    /**
     * Sets the expected size of the source received from data metrics
     * If define 0 - not from metrics
     * @return Int returns the size of the source
     */
    override fun getExpectedSourceSize(): Int {
        return Nsa5gConstants.OEMSV_EXPECTED_SIZE
    }

    /**
     *  This function processes OEMSV and return disposable
     *  @param baseNr5gMetricsData: BaseNr5gMetricsData
     *  @param baseNr5gData: BaseNr5gData
     */
    override suspend fun processNr5gMetricsData(
        baseNr5gMetricsData: BaseNr5gMetricsData,
        baseNr5gData: BaseNr5gData
    ): Disposable? {
        nsa5gOEMSVDisposable =  Observable.just(saveNsa5gOEMSVData(baseNr5gMetricsData,
            baseNr5gData)).subscribeOn(
            Schedulers.io()).subscribe()
        return nsa5gOEMSVDisposable
    }

    /**
     *  This function processes the list, converts it to Nr5gOEMSVEntity and saves it in database
     *  @param baseNr5gMetricsData: total metrics data to be processed
     *  @param baseNr5gData: [BaseNr5gData]
     */
    private fun saveNsa5gOEMSVData(baseNr5gMetricsData: BaseNr5gMetricsData, baseNr5gData: BaseNr5gData){
        val oemSoftwareVersion = Nr5gOEMSoftwareVersionCollector().getOEMSoftwareVersion(context)

        val nr5gOEMSVEntity = Nsa5gEntityConverter.convertNr5gOEMSVEntity(oemSoftwareVersion)
        nr5gOEMSVEntity.sessionId = baseNr5gData.sessionId
        nr5gOEMSVEntity.uniqueId = baseNr5gData.uniqueId

        CoroutineScope(Dispatchers.IO).launch {
            saveNr5gOEMSVToDatabase(nr5gOEMSVEntity)
        }
    }

    /**
     * saves OEMSV object to database
     * @param n5gOEMSVEntity: [Nr5gOEMSVEntity] the object to save
     */
    private fun saveNr5gOEMSVToDatabase(n5gOEMSVEntity: Nr5gOEMSVEntity) {
        nr5gRepository.insertNr5gOEMSVEntity(n5gOEMSVEntity)
    }
}