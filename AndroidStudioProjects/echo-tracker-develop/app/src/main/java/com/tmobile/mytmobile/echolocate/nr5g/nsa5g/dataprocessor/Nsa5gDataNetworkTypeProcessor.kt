package com.tmobile.mytmobile.echolocate.nr5g.nsa5g.dataprocessor

import android.content.Context
import android.telephony.TelephonyManager
import com.tmobile.mytmobile.echolocate.nr5g.nsa5g.database.entity.Nr5gDataNetworkTypeEntity
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

/**
 * This Nsa5gDataNetworkTypeProcessor class saves the nr5g Nr5gDataNetworkType in to the database
 */
class Nsa5gDataNetworkTypeProcessor(var context: Context) : Nsa5gBaseDataProcessor(context) {

    private var nsa5gDataNetworkTypeDisposable: Disposable? = null
    /**
     * Sets the expected size of the source received from data metrics
     * If define 0 - not from metrics
     * @return Int returns the size of the source
     */
    override fun getExpectedSourceSize(): Int {
        return Nsa5gConstants.NETWORK_TYPE_EXPECTED_SIZE
    }

    /**
     *  This function processes Data Network Type and return disposable
     *  @param baseNr5gMetricsData: BaseNr5gMetricsData
     *  @param baseNr5gData: BaseNr5gData
     */
    override suspend fun processNr5gMetricsData(
        baseNr5gMetricsData: BaseNr5gMetricsData,
        baseNr5gData: BaseNr5gData
    ): Disposable? {
        nsa5gDataNetworkTypeDisposable =  Observable.just(saveNsa5gDataNetworkTypeData(baseNr5gMetricsData,
            baseNr5gData)).subscribeOn(
            Schedulers.io()).subscribe()
        return nsa5gDataNetworkTypeDisposable
    }

    /**
     *  This function processes the list, converts it to Nr5gDataNetworkTypeEntity and saves it in database
     *  @param baseNr5gMetricsData: total metrics data to be processed
     *  @param baseNr5gData: [BaseNr5gData]
     */
    private fun saveNsa5gDataNetworkTypeData(baseNr5gMetricsData: BaseNr5gMetricsData, baseNr5gData: BaseNr5gData) {
        val telephonyManager =
            context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        val nr5gDataNetworkTypeEntity =
            Nsa5gEntityConverter.convertNr5gDataNetworkTypeEntity(telephonyManager)
        nr5gDataNetworkTypeEntity.sessionId = baseNr5gData.sessionId
        nr5gDataNetworkTypeEntity.uniqueId = baseNr5gData.uniqueId

        CoroutineScope(Dispatchers.IO).launch {
            saveNr5gDataNetworkTypeToDatabase(nr5gDataNetworkTypeEntity)
        }
    }

    /**
     * saves data network object to database
     * @param nr5gDataNetworkTypeEntity: [Nr5gDataNetworkTypeEntity] the object to save
     */
    private fun saveNr5gDataNetworkTypeToDatabase(nr5gDataNetworkTypeEntity: Nr5gDataNetworkTypeEntity) {
        nr5gRepository.insertNr5gDataNetworkTypeEntity(nr5gDataNetworkTypeEntity)
    }
}