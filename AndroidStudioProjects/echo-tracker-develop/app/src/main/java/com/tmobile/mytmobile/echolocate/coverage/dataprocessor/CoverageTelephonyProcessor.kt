package com.tmobile.mytmobile.echolocate.coverage.dataprocessor
/**
 * Created by Mahesh Shetye on 2020-05-06
 *
 */

import android.content.Context
import android.database.sqlite.SQLiteDatabaseCorruptException
import com.tmobile.mytmobile.echolocate.coverage.database.entity.*
import com.tmobile.mytmobile.echolocate.coverage.model.BaseCoverageData
import com.tmobile.mytmobile.echolocate.coverage.utils.CoverageEntityConverter
import com.tmobile.mytmobile.echolocate.coverage.utils.CoverageNrDataCollector
import com.tmobile.mytmobile.echolocate.coverage.utils.CoverageTelephonyDataCollector
import com.tmobile.mytmobile.echolocate.utils.EchoLocateLog
import com.tmobile.mytmobile.echolocate.utils.FirebaseUtils
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class CoverageTelephonyProcessor(val context: Context) : BaseCoverageDataProcessor(context) {

    private var telephonyDisposable: Disposable? = null
    /**
     *  This function processes coverage telephony and return disposable
     *  @param baseCoverageData: BaseLteData
     */
    override suspend fun processCoverageData(baseCoverageData: BaseCoverageData): Disposable? {
        telephonyDisposable =  Observable.just(saveCoverageData(baseCoverageData)).subscribeOn(
            Schedulers.io()).subscribe()
        return telephonyDisposable
    }
    /**
     *  This method processes the list, converts it to Coverage Telephony Entity and saves it in database
     *  @param baseCoverageData: BaseLteData
     */
    private fun saveCoverageData(baseCoverageData: BaseCoverageData) {
        val telephonyData = CoverageTelephonyDataCollector.getInstance(context)
        telephonyData.refreshCellData()

        // Create object for [CoverageTelephonyEntity]
        val coverageTelephonyEntity = CoverageTelephonyEntity(
            simState = telephonyData.getSimState(),
            roamingNetwork = telephonyData.getRoamingNetwork(),
            roamingVoice = telephonyData.isRoamingVoice(),
            networkType = telephonyData.getNetworkType(),
            serviceState = telephonyData.getServiceState()
        )
        coverageTelephonyEntity.sessionId = baseCoverageData.sessionId
        coverageTelephonyEntity.uniqueId = baseCoverageData.uniqueId

        // Check if primary cell available
        val covPrimCell = telephonyData.getPrimaryCell()

        // Create object for [CoveragePrimaryCellEntity]
        val coveragePrimaryCellEntity =
            if (covPrimCell != null)
                CoverageEntityConverter.convertCoveragePrimaryCellEntity(covPrimCell)
            else
                null
        if (coveragePrimaryCellEntity != null) {
            coveragePrimaryCellEntity.sessionId = baseCoverageData.sessionId
            coveragePrimaryCellEntity.uniqueId = UUID.randomUUID().toString()
            coveragePrimaryCellEntity.baseEntityId =
                coverageTelephonyEntity.uniqueId  // assign UUID of parent-CoverageTelephonyEntity
        }

        // Create object for [CoverageCellIdentityEntity]
        // if primary cell is available
        val coverageCellIdentityEntity =
            if (covPrimCell != null)
                CoverageEntityConverter.convertCoverageCellIdentityEntity(covPrimCell.cellIdentity)
            else
                null
        if (coverageCellIdentityEntity != null && coveragePrimaryCellEntity != null) {
            coverageCellIdentityEntity.uniqueId = UUID.randomUUID().toString()
            coverageCellIdentityEntity.sessionId = baseCoverageData.sessionId
            coverageCellIdentityEntity.baseEntityId =
                coveragePrimaryCellEntity.uniqueId  // assign UUID of parent-CoveragePrimaryCellEntity
        }

        // Create object for [CoverageCellSignalStrengthEntity]
        // if primary cell is available
        val coverageCellSignalStrengthEntity =
            if (covPrimCell != null)
                CoverageEntityConverter.convertCoverageCellSignalStrengthEntity(covPrimCell.cellSignalStrength)
            else
                null
        if (coverageCellSignalStrengthEntity != null && coveragePrimaryCellEntity != null) {
            coverageCellSignalStrengthEntity.uniqueId = UUID.randomUUID().toString()
            coverageCellSignalStrengthEntity.sessionId = baseCoverageData.sessionId
            coverageCellSignalStrengthEntity.baseEntityId =
                coveragePrimaryCellEntity.uniqueId  // assign UUID of parent-CoveragePrimaryCellEntity
        }

        // Create object for [CoverageNrCellEntity]
        val coverageNrCell = CoverageNrDataCollector.getNrCellData(context)
        val coverageNrCellEntity =
            if (coverageNrCell != null)
                CoverageEntityConverter.convertCoverageNrCellEntity(coverageNrCell)
            else
                null
        if (coverageNrCellEntity != null) {
            coverageNrCellEntity.sessionId = baseCoverageData.sessionId
            coverageNrCellEntity.uniqueId = UUID.randomUUID().toString()
            coverageNrCellEntity.baseEntityId =
                coverageTelephonyEntity.uniqueId  // assign UUID of parent-CoverageTelephonyEntity
        }

        // Save objects in database
        CoroutineScope(Dispatchers.IO).launch {

            try {
                // Save object for [CoverageTelephonyEntity]
                saveCoverageTelephonyEntityToDatabase(coverageTelephonyEntity)

                // Save object for [CoveragePrimaryCellEntity]
                if (coveragePrimaryCellEntity != null) {
                    saveCoveragePrimaryCellEntityToDatabase(coveragePrimaryCellEntity)
                }

                // Save object for [CoverageCellIdentityEntity]
                if (coverageCellIdentityEntity != null) {
                    saveCoverageCellIdentityEntityToDatabase(coverageCellIdentityEntity)
                }

                // Save object for [CoverageCellSignalStrengthEntity]
                if (coverageCellSignalStrengthEntity != null) {
                    saveCoverageCellSignalStrengthEntityToDatabase(coverageCellSignalStrengthEntity)
                }

                // Save object for [CoverageNrCellEntity]
                if (coverageNrCellEntity != null) {
                    saveCoverageNrCellEntityToDatabase(coverageNrCellEntity)
                }
            } catch (ex: SQLiteDatabaseCorruptException) {
                EchoLocateLog.eLogE("CoverageTelephonyProcessor : saveCoverageData() :: Exception : $ex")
                FirebaseUtils.logCrashToFirebase(
                    "Exception in CoverageTelephonyProcessor :: saveCoverageData()",
                    ex.localizedMessage,
                    "SQLiteDatabaseCorruptException"
                )
            }
        }
    }

    /**
     * saves the CoverageCellIdentityEntity object to database
     * @param coverageCellIdentityEntity: [CoverageCellIdentityEntity] the object to save
     */
    private fun saveCoverageCellIdentityEntityToDatabase(coverageCellIdentityEntity: CoverageCellIdentityEntity) {
        coverageRepository.insertCoverageCellIdentityEntity(coverageCellIdentityEntity)
    }

    /**
     * saves the CoverageCellSignalStrengthEntity object to database
     * @param coverageCellSignalStrengthEntity: [CoverageCellSignalStrengthEntity] the object to save
     */
    private fun saveCoverageCellSignalStrengthEntityToDatabase(coverageCellSignalStrengthEntity: CoverageCellSignalStrengthEntity) {
        coverageRepository.insertCoverageCellSignalStrengthEntity(coverageCellSignalStrengthEntity)
    }

    /**
     * saves the CoveragePrimaryCellEntity object to database
     * @param coveragePrimaryCellEntity: [CoveragePrimaryCellEntity] the object to save
     */
    private fun saveCoveragePrimaryCellEntityToDatabase(coveragePrimaryCellEntity: CoveragePrimaryCellEntity) {
        coverageRepository.insertCoveragePrimaryCellEntity(coveragePrimaryCellEntity)
    }

    /**
     * saves the CoverageNrCellEntity object to database
     * @param coverageNrCellEntity: [CoverageNrCellEntity] the object to save
     */
    private fun saveCoverageNrCellEntityToDatabase(coverageNrCellEntity: CoverageNrCellEntity) {
        coverageRepository.insertCoverageNrCellEntity(coverageNrCellEntity)
    }

    /**
     * saves the CoverageTelephonyEntity object to database
     * @param coverageTelephonyEntity: [CoverageTelephonyEntity] the object to save
     */
    private fun saveCoverageTelephonyEntityToDatabase(coverageTelephonyEntity: CoverageTelephonyEntity) {
        coverageRepository.insertCoverageTelephonyEntity(coverageTelephonyEntity)
    }
}
