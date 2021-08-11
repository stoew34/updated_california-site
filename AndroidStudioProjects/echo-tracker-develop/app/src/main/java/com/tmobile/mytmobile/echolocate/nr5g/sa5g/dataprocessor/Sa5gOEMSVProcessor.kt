package com.tmobile.mytmobile.echolocate.nr5g.sa5g.dataprocessor

import android.content.Context
import com.tmobile.mytmobile.echolocate.nr5g.core.utils.Nr5gOEMSoftwareVersionCollector
import com.tmobile.mytmobile.echolocate.nr5g.sa5g.database.entity.Sa5gOEMSVEntity
import com.tmobile.mytmobile.echolocate.nr5g.sa5g.model.BaseSa5gData
import com.tmobile.mytmobile.echolocate.nr5g.sa5g.model.BaseSa5gMetricsData
import com.tmobile.mytmobile.echolocate.nr5g.sa5g.utils.Sa5gConstants
import com.tmobile.mytmobile.echolocate.nr5g.sa5g.utils.Sa5gEntityConverter
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class Sa5gOEMSVProcessor(var context: Context) : Sa5gBaseDataProcessor(context) {

    private var sa5gOEMSVDisposable: Disposable? = null
    /**
     * Sets the expected size of the source received from data metrics
     * If define 0 - not from metrics
     * @return Int returns the size of the source
     */
    override fun getExpectedSourceSize(): Int {
        return Sa5gConstants.SA5G_OEMSV_EXPECTED_SIZE
    }
    /**
     * This function processes OEMSV and return disposable
     *  @param baseSa5gMetricsData: BaseSa5gMetricsData
     *  @param baseSa5gData: BaseSa5gData
     */
    override suspend fun processSa5gMetricsData(
        baseSa5gMetricsData: BaseSa5gMetricsData,
        baseSa5gData: BaseSa5gData
    ): Disposable? {
        sa5gOEMSVDisposable =  Observable.just(saveOEMSVData(baseSa5gMetricsData,
            baseSa5gData)).subscribeOn(
            Schedulers.io()).subscribe()
        return sa5gOEMSVDisposable
    }

    /**
     *  This function processes the list, converts it to Sa5gOEMSVEntity and saves it in database
     *  @param baseSa5gMetricsData: total metrics data to be processed
     *  @param baseSa5gData: [BaseSa5gData]
     */
    private fun saveOEMSVData(baseSa5gMetricsData: BaseSa5gMetricsData, baseSa5gData: BaseSa5gData){
        val oemSoftwareVersion = Nr5gOEMSoftwareVersionCollector().getOEMSoftwareVersion(context)

        val sa5gOEMSVEntity = Sa5gEntityConverter.convertSa5gOEMSVEntity(oemSoftwareVersion)

        sa5gOEMSVEntity.sessionId = baseSa5gData.sessionId
        sa5gOEMSVEntity.uniqueId = baseSa5gData.uniqueId

        CoroutineScope(Dispatchers.IO).launch {
            saveSa5gOEMSVToDatabase(sa5gOEMSVEntity)
        }
    }

    /**
     * saves OEMSV object to database
     * @param sa5gOEMSVEntity: [Sa5gOEMSVEntity] the object to save
     */
    private fun saveSa5gOEMSVToDatabase(sa5gOEMSVEntity: Sa5gOEMSVEntity) {
        sa5gRepository.insertSa5gOEMSVEntity(sa5gOEMSVEntity)
    }
}