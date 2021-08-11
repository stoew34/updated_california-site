package com.tmobile.mytmobile.echolocate.coverage.dataprocessor

import android.content.Context
import android.database.sqlite.SQLiteDatabaseCorruptException
import com.tmobile.mytmobile.echolocate.coverage.database.entity.CoverageSettingsEntity
import com.tmobile.mytmobile.echolocate.coverage.model.BaseCoverageData
import com.tmobile.mytmobile.echolocate.coverage.utils.CoverageNetworkDataCollector
import com.tmobile.mytmobile.echolocate.coverage.utils.VolteStateEnum
import com.tmobile.mytmobile.echolocate.utils.EchoLocateLog
import com.tmobile.mytmobile.echolocate.utils.FirebaseUtils
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Processes data settings data
 * and saves the data in the database when focus gain or focus loss event is triggered
 */
class CoverageSettingsProcessor(var context: Context) : BaseCoverageDataProcessor(context) {

    var networkDataCollector = CoverageNetworkDataCollector()
    private var settingsDisposable: Disposable? = null
    /**
     *  This function processes coverage settings and return disposable
     *  @param baseCoverageData: BaseLteData
     */
    override suspend fun processCoverageData(baseCoverageData: BaseCoverageData): Disposable? {
        settingsDisposable =  Observable.just(saveCoverageData(baseCoverageData)).subscribeOn(Schedulers.io()).subscribe()
        return settingsDisposable
    }

    /**
     *  This method processes the list, converts it to Coverage Settings Entity and saves it in database
     *  @param baseCoverageData: BaseLteData
     */
    private fun saveCoverageData(baseCoverageData: BaseCoverageData){
        val coverageSettingsEntity = CoverageSettingsEntity(

                volteState = VolteStateEnum.valueOf(networkDataCollector.readVoiceCallType(context))
                        .toString(),
                dataRoamingEnabled = networkDataCollector.isRoamingDataEnabled(context).toString()
        )
        coverageSettingsEntity.sessionId = baseCoverageData.sessionId
        coverageSettingsEntity.uniqueId = baseCoverageData.uniqueId

        CoroutineScope(Dispatchers.IO).launch {
            try {
                saveCoverageSettingsToDatabase(coverageSettingsEntity)
            } catch (ex: SQLiteDatabaseCorruptException) {
                EchoLocateLog.eLogE("CoverageSettingsProcessor : saveCoverageData() :: Exception : $ex")
                FirebaseUtils.logCrashToFirebase(
                    "Exception in CoverageSettingsProcessor :: saveCoverageData()",
                    ex.localizedMessage,
                    "SQLiteDatabaseCorruptException"
                )
            }
        }
    }

    /**
     * saves the data settings object to database
     * @param coverageSettingsEntity: [CoverageSettingsEntity] the object to save
     */
    private fun saveCoverageSettingsToDatabase(coverageSettingsEntity: CoverageSettingsEntity) {
        coverageRepository.insertCoverageSettingsEntity(coverageSettingsEntity)
    }

}