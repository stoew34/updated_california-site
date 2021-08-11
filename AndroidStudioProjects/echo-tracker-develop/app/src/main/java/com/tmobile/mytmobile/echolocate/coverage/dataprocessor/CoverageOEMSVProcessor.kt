package com.tmobile.mytmobile.echolocate.coverage.dataprocessor

import android.content.Context
import android.database.sqlite.SQLiteDatabaseCorruptException
import com.tmobile.mytmobile.echolocate.coverage.database.entity.CoverageOEMSVEntity
import com.tmobile.mytmobile.echolocate.coverage.model.BaseCoverageData
import com.tmobile.mytmobile.echolocate.coverage.utils.CoverageEntityConverter
import com.tmobile.mytmobile.echolocate.coverage.utils.CoverageOEMSoftwareVersionCollector
import com.tmobile.mytmobile.echolocate.utils.EchoLocateLog
import com.tmobile.mytmobile.echolocate.utils.FirebaseUtils
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CoverageOEMSVProcessor(var context: Context) : BaseCoverageDataProcessor(context) {

    var oemsvCollector = CoverageOEMSoftwareVersionCollector()
    private var oEMSVDisposable: Disposable? = null

    /**
     *  This function processes CoverageOEMSVEntity and return disposable
     *  @param baseCoverageData: [BaseCoverageData]
     */
    override suspend fun processCoverageData(baseCoverageData: BaseCoverageData): Disposable? {
        oEMSVDisposable = Observable.just(saveCoverageData(baseCoverageData))
            .subscribeOn(Schedulers.io()).subscribe()
        return oEMSVDisposable
    }

    /**
     *  This function processes the list, converts it to CoverageOEMSVEntity and saves it in database
     *  @param baseCoverageData: [BaseCoverageData]
     */
    private fun saveCoverageData(baseCoverageData: BaseCoverageData) {
        val coverageOEMSV = oemsvCollector.getOEMSoftwareVersion(context)
        val coverageOEMSVEntity = CoverageEntityConverter.convertCoverageOEMSVEntity(coverageOEMSV)
        coverageOEMSVEntity.sessionId = baseCoverageData.sessionId
        coverageOEMSVEntity.uniqueId = baseCoverageData.uniqueId

        CoroutineScope(Dispatchers.IO).launch {
            try {
                saveCoverageOEMSVToDatabase(coverageOEMSVEntity)
            } catch (ex: SQLiteDatabaseCorruptException) {
                EchoLocateLog.eLogE("CoverageOEMSVProcessor : saveCoverageData() :: Exception : $ex")
                FirebaseUtils.logCrashToFirebase(
                    "Exception in CoverageOEMSVProcessor :: saveCoverageData()",
                    ex.localizedMessage,
                    "SQLiteDatabaseCorruptException"
                )
            }
        }
    }

    /**
     * saves OEMSV object to database
     * @param coverageOEMSVEntity: [CoverageOEMSVEntity] the object to save
     */
    private fun saveCoverageOEMSVToDatabase(coverageOEMSVEntity: CoverageOEMSVEntity) {
        coverageRepository.insertCoverageOEMSVEntity(coverageOEMSVEntity)
    }
}