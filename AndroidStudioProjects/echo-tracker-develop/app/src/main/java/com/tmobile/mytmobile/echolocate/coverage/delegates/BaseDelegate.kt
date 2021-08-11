package com.tmobile.mytmobile.echolocate.coverage.delegates

import android.content.BroadcastReceiver
import android.content.Context
import android.database.sqlite.SQLiteDatabaseCorruptException
import android.telephony.TelephonyManager
import com.tmobile.mytmobile.echolocate.communicationbus.rxbus.PostTicket
import com.tmobile.mytmobile.echolocate.communicationbus.rxbus.RxBus
import com.tmobile.mytmobile.echolocate.configuration.events.analytics.ELAnalyticActions
import com.tmobile.mytmobile.echolocate.configuration.events.analytics.ELAnalyticsEvent
import com.tmobile.mytmobile.echolocate.configuration.events.analytics.ELModulesEnum
import com.tmobile.mytmobile.echolocate.configuration.model.Coverage
import com.tmobile.mytmobile.echolocate.coverage.database.EchoLocateCoverageDatabase
import com.tmobile.mytmobile.echolocate.coverage.database.dao.CoverageDao
import com.tmobile.mytmobile.echolocate.coverage.database.entity.BaseEchoLocateCoverageEntity
import com.tmobile.mytmobile.echolocate.coverage.dataprocessor.*
import com.tmobile.mytmobile.echolocate.coverage.model.BaseCoverageData
import com.tmobile.mytmobile.echolocate.coverage.reportprocessor.CoverageDataStatus
import com.tmobile.mytmobile.echolocate.coverage.utils.CoverageConstants
import com.tmobile.mytmobile.echolocate.coverage.utils.CoverageConstants.COVERAGE_BASE_ENTITY_NULL
import com.tmobile.mytmobile.echolocate.utils.EchoLocateDateUtils
import com.tmobile.mytmobile.echolocate.utils.EchoLocateLog
import com.tmobile.mytmobile.echolocate.coverage.utils.CoverageUtils
import com.tmobile.mytmobile.echolocate.nr5g.sa5g.utils.Sa5gConstants
import com.tmobile.mytmobile.echolocate.utils.FirebaseUtils
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onCompletion
import java.util.*

/**
 * Created by Mahesh Shetye on 2020-04-23
 *
 * Base class for coverage triggers
 */


abstract class BaseDelegate(val context: Context) : ICoverageTriggerHandler {

    protected var triggerReceiver: BroadcastReceiver? = null

    protected var isReceiverRegistered: Boolean = false

    var telephonyManager: TelephonyManager? = null


    /**
     * Register the trigger actions for trigger source
     */
    abstract fun initTrigger(coverage: Coverage): Boolean

    /**
     * Register the trigger actions
     */
    abstract fun registerReceiver(): Boolean

    /**
     * Reset the trigger count to zero
     * and re-register the broadcast if required
     */
    abstract fun resetTrigger()

    init {
        telephonyManager =
            context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
    }

    /**
     * Store Coverage entity data into room database
     * Logs for triggers info
     * @param triggerCode: trigger source from TriggerSource enumeration
     */
    @ExperimentalCoroutinesApi
    fun storeCoverageEntity(triggerCode: TriggerSource) {
        EchoLocateLog.eLogI("Coverage : storeCoverageEntity for : $triggerCode")

        val sessionId = UUID.randomUUID().toString()

        /** Saved BaseEchoLocateCoverageEntity in to the database*/
        val baseEchoLocateCoverageEntity =
            prepareEchoLocateCoverageEntity(triggerCode, sessionId)
        val coverageDao =
            EchoLocateCoverageDatabase.getEchoLocateCoverageDatabase(context).coverageDao()
        val isDataInserted = runBlocking {
            coverageDao.insertBaseEchoLocateCoverageEntity(baseEchoLocateCoverageEntity)
        }
        val disposables = CompositeDisposable()
        /** Execute processor classes to save a data to DataBase */
        if (isDataInserted > 0) {
            runBlocking<Any> {
                withTimeoutOrNull(CoverageUtils.TWENTY_ONE_SECONDS) {
                    coverageProcessors(sessionId)
                        .onCompletion {
                            EchoLocateLog.eLogD("Diagnostics: All coverage processors execution finished")
                        }
                        .collect { value ->
                            value.let {
                                disposables.add(it as Disposable)
                            }
                        }
                }
                disposables.dispose()
                insertEchoLocateCoverageEntity(baseEchoLocateCoverageEntity, coverageDao)
            }
        } else {
            postAnalyticsEventForCoverageFailed(
                COVERAGE_BASE_ENTITY_NULL,
                ELAnalyticActions.EL_DATA_COLLECTION_FAILED
            )
            EchoLocateLog.eLogD("Diagnostics: BaseEchoLocateCoverageEntity not inserted")
        }
    }

    /**
     * This function is used to insert base echo locate coverage entity
     */
    private fun insertEchoLocateCoverageEntity(baseEchoLocateCoverageEntity: BaseEchoLocateCoverageEntity, coverageDao: CoverageDao) {
        EchoLocateLog.eLogD("Diagnostics: Data inserted")
        baseEchoLocateCoverageEntity.status = CoverageDataStatus.STATUS_RAW
        coverageDao.updateBaseEchoLocateCoverageEntityStatus(baseEchoLocateCoverageEntity)
    }

    /**
     * This is used for kotlin coroutines flow setting
     */
    private suspend fun coverageProcessors(sessionId: String): Flow<Disposable?> = listOf(
        CoverageNetProcessor(context).execute(BaseCoverageData(sessionId, "")),
        CoverageOEMSVProcessor(context).execute(BaseCoverageData(sessionId, "")),
        CoverageSettingsProcessor(context).execute(BaseCoverageData(sessionId, "")),
        CoverageTelephonyProcessor(context).execute(BaseCoverageData(sessionId, "")),
        CoverageLocationProcessor(context).execute(BaseCoverageData(sessionId, ""))).asFlow()

    /**
     * Returns base echo locate lte entity with current system time
     * @param triggerCode: trigger code of the specific app state
     * */
    private fun prepareEchoLocateCoverageEntity(
        triggerCode: TriggerSource,
        sessionId: String
    ): BaseEchoLocateCoverageEntity {
        return BaseEchoLocateCoverageEntity(
            trigger = triggerCode.toString(),
            status = "",//addressed DIA-6523
            timestamp = EchoLocateDateUtils.getTriggerTimeStamp(),
            schemaVersion = CoverageConstants.SCHEMA_VERSION,
            sessionId = sessionId
        )
    }

    /**
     * Disposes the object.
     */
    open fun dispose() {
        if (triggerReceiver != null) {
            context.unregisterReceiver(triggerReceiver)
            triggerReceiver = null
            isReceiverRegistered = false
        }
    }

    /**
     * this function stores the trigger states into entity
     */
    protected fun storeTriggerState(triggrState: TriggerSource) {
        GlobalScope.launch(Dispatchers.IO) {
            runBlocking {
                try {
                    storeCoverageEntity(triggrState)
                } catch (ex: SQLiteDatabaseCorruptException) {
                    EchoLocateLog.eLogE("BaseDelegate : storeTriggerState() :: Exception : $ex")
                    FirebaseUtils.logCrashToFirebase(
                        "Exception in BaseDelegate : storeTriggerState()",
                        ex.localizedMessage,
                        "SQLiteDatabaseCorruptException"
                    )
                }
            }
        }
    }

    /**
     * This function is used to post new event to analytics manager
     * @param status-checks the status of cms config
     * @param payload-stores the status code based on api status
     */
    private fun postAnalyticsEventForCoverageFailed(payload: String, status: ELAnalyticActions) {
        val analyticsEvent = ELAnalyticsEvent(
            moduleName = ELModulesEnum.SA5G,
            action = status,
            payload = payload
        )
        analyticsEvent.timeStamp = System.currentTimeMillis()

        val postAnalyticsTicket = PostTicket(analyticsEvent)
        RxBus.instance.post(postAnalyticsTicket)
    }

}
