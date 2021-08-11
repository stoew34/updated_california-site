package com.tmobile.mytmobile.echolocate.lte.dataprocessor

import android.content.Context
import com.tmobile.mytmobile.echolocate.lte.database.entity.LteOEMSVEntity
import com.tmobile.mytmobile.echolocate.lte.model.BaseLteData
import com.tmobile.mytmobile.echolocate.lte.model.LteMetricsData
import com.tmobile.mytmobile.echolocate.lte.utils.LteConstants
import com.tmobile.mytmobile.echolocate.lte.utils.LteEntityConverter
import com.tmobile.mytmobile.echolocate.lte.utils.LteOEMSoftwareVersionCollector
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * class OEMSoftwareVersionProcessor
 * @param context : Context
 *
 * "OEMSV": {
 * "type": "object",
 * "description": "An explanation about the purpose of this instance.",
 * "required": [
 * "SV",
 * "androidVersion",
 * "buildNamae",
 * "customVersion",
 * "radioVersion"
 *
 * extends
 * class  BaseIntentProcessor
 * @param context
 */
class OEMSVProcessor(var context: Context) : BaseLteDataProcessor(context) {

    /**
     * Sets the expected size of the source received from data metrics
     * @return Int returns the size of the source
     */
    override fun getExpectedSourceSize(): Int {
        return LteConstants.OEMSV_EXPECTED_SIZE
    }

    private var oEMSVDisposable: Disposable? = null

    /**
     *  This function processes LteOEMSVEntity and return disposable
     *  @param lteMetricsData: LteMetricsData
     *  @param baseLteData: BaseLteData
     */
    override suspend fun processLteMetricsData(
        lteMetricsData: LteMetricsData,
        baseLteData: BaseLteData
    ): Disposable? {
        oEMSVDisposable = Observable.just(
            saveOEMSVDataSettingsData(
                lteMetricsData,
                baseLteData
            )
        ).subscribeOn(
            Schedulers.io()
        ).subscribe()
        return oEMSVDisposable
    }

    /**
     *  This function processes the list, converts it to LteOEMSVEntity and saves it in database
     *  @param lteMetricsData: total metrics data to be processed
     *  @param baseLteData: BaseLteData
     */
    private fun saveOEMSVDataSettingsData(
        lteMetricsData: LteMetricsData,
        baseLteData: BaseLteData
    ) {
        val oemSoftwareVersion = LteOEMSoftwareVersionCollector().getOEMSoftwareVersion(context)

        val lteOEMSVEntity = LteEntityConverter.convertLteOEMSVEntity(oemSoftwareVersion)
        lteOEMSVEntity.sessionId = baseLteData.sessionId
        lteOEMSVEntity.uniqueId = baseLteData.uniqueId

        CoroutineScope(Dispatchers.IO).launch {
            saveCLteOEMSVToDatabase(lteOEMSVEntity)
        }
    }

    /**
     * saves OEMSV object to database
     * @param lteOEMSVEntity: [LteOEMSVEntity] the object to save
     */
    private fun saveCLteOEMSVToDatabase(lteOEMSVEntity: LteOEMSVEntity) {
        lteRepository.insertLteOEMSVEntity(lteOEMSVEntity)
    }
}