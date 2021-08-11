package com.tmobile.mytmobile.echolocate.coverage.dataprocessor

import android.content.Context
import android.database.sqlite.SQLiteDatabaseCorruptException
import com.tmobile.mytmobile.echolocate.coverage.database.entity.CoverageConnectedWifiStatusEntity
import com.tmobile.mytmobile.echolocate.coverage.database.entity.CoverageNetEntity
import com.tmobile.mytmobile.echolocate.coverage.model.BaseCoverageData
import com.tmobile.mytmobile.echolocate.coverage.model.CoverageConnectedWifiStatus
import com.tmobile.mytmobile.echolocate.coverage.utils.CoverageEntityConverter
import com.tmobile.mytmobile.echolocate.coverage.utils.CoverageNetworkDataCollector
import com.tmobile.mytmobile.echolocate.utils.EchoLocateLog
import com.tmobile.mytmobile.echolocate.utils.FirebaseUtils
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CoverageNetProcessor(var context: Context) : BaseCoverageDataProcessor(context) {

    var networkDataCollector = CoverageNetworkDataCollector()
    private var netDisposable: Disposable? = null

    /**
     *  This function processes ConnectedWifiStatusEntity and return disposable
     *  @param baseCoverageData: [BaseCoverageData]
     */
    override suspend fun processCoverageData(baseCoverageData: BaseCoverageData): Disposable? {
        netDisposable =  Observable.just(saveCoverageData(baseCoverageData)).subscribeOn(Schedulers.io()).subscribe()
        return netDisposable
    }

    /**
     *  This function processes the list, converts it to ConnectedWifiStatusEntity and saves it in database
     *  @param baseCoverageData: [BaseCoverageData]
     */
    private fun saveCoverageData(baseCoverageData: BaseCoverageData) {
        val coverageConnectedWifiStatus = networkDataCollector.getConnectedWifiStatus(context)
                ?: CoverageConnectedWifiStatus()

        val coverageConnectedWifiStatusEntity =
                CoverageEntityConverter.convertCoverageConnectedWifiStatusEntity(
                        coverageConnectedWifiStatus
                )

        val coverageNetEntity = CoverageNetEntity(
                networkDataCollector.getActiveNetwork(context),
                networkDataCollector.checkIsRoaming(context).toString()
        )
        coverageNetEntity.sessionId = baseCoverageData.sessionId
        coverageNetEntity.uniqueId = baseCoverageData.uniqueId
        coverageConnectedWifiStatusEntity.sessionId = baseCoverageData.sessionId
        coverageConnectedWifiStatusEntity.uniqueId = baseCoverageData.uniqueId

        CoroutineScope(Dispatchers.IO).launch {
            try{
                saveCoverageConnectedWifiStatusToDatabase(coverageConnectedWifiStatusEntity)
                saveCoverageNetToDatabase(coverageNetEntity)
            } catch (ex: SQLiteDatabaseCorruptException) {
                EchoLocateLog.eLogE("CoverageNetProcessor : saveCoverageData() :: Exception : $ex")
                FirebaseUtils.logCrashToFirebase(
                    "Exception in CoverageNetProcessor :: saveCoverageData()",
                    ex.localizedMessage,
                    "SQLiteDatabaseCorruptException"
                )
            }
        }
    }

    /**
     * saves CoverageConnectedWifiStatus object to database
     * @param coverageConnectedWifiStatusEntity: [CoverageConnectedWifiStatusEntity] the object to save
     */
    private fun saveCoverageConnectedWifiStatusToDatabase(coverageConnectedWifiStatusEntity: CoverageConnectedWifiStatusEntity) {
        coverageRepository.insertCoverageConnectedWifiStatusEntity(coverageConnectedWifiStatusEntity)
    }

    /**
     * saves CoverageNet object to database
     * @param coverageNetEntity: [CoverageNetEntity] the object to save
     */
    private fun saveCoverageNetToDatabase(coverageNetEntity: CoverageNetEntity) {
        coverageRepository.insertCoverageNetEntity(coverageNetEntity)
    }
}