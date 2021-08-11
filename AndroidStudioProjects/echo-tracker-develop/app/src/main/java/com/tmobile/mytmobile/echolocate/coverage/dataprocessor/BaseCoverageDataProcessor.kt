package com.tmobile.mytmobile.echolocate.coverage.dataprocessor

import android.content.Context
import android.database.sqlite.SQLiteDatabaseCorruptException
import com.tmobile.mytmobile.echolocate.coverage.database.repository.CoverageRepository
import com.tmobile.mytmobile.echolocate.coverage.model.BaseCoverageData
import com.tmobile.mytmobile.echolocate.utils.EchoLocateLog
import com.tmobile.mytmobile.echolocate.utils.FirebaseUtils
import io.reactivex.disposables.Disposable
import java.util.*

abstract class BaseCoverageDataProcessor(context: Context) {

    var coverageRepository = CoverageRepository(context)

    /**
     *  This fun process Coverage Data
     *  @param baseCoverageData: BaseCoverageData
     */
    abstract suspend fun processCoverageData(baseCoverageData: BaseCoverageData): Disposable?

    /**
     * Checks if the passed data is valid or invalid and processes only if the data is valid
     * @param baseCoverageData: total metrics data
     */
    suspend fun execute(baseCoverageData: BaseCoverageData): Disposable? {
        val coverageData = getBaseCoverageData(baseCoverageData.sessionId)
        return processCoverageData(coverageData)
    }

    /**
     *  Generates [BaseCoverageData] by passing the session id and generates a id for mapping to internal tables
     *  @param sessionId:String session id generated on Coverage event
     *  @return [BaseCoverageData] returns the generated Coverage data with session id and unique id
     */
    private fun getBaseCoverageData(sessionId: String): BaseCoverageData {
        return BaseCoverageData(sessionId, UUID.randomUUID().toString())
    }
}

