package com.tmobile.mytmobile.echolocate.nr5g.nsa5g.datacollector

import android.content.Context
import com.tmobile.mytmobile.echolocate.nr5g.core.oemdata.Nr5gBaseDataMetricsWrapper
import com.tmobile.mytmobile.echolocate.nr5g.core.oemdata.Nr5gDataMetricsWrapper
import com.tmobile.mytmobile.echolocate.nr5g.nsa5g.database.EchoLocateNr5gDatabase
import com.tmobile.mytmobile.echolocate.nr5g.nsa5g.database.dao.Nr5gDao
import com.tmobile.mytmobile.echolocate.nr5g.nsa5g.database.entity.BaseEchoLocateNr5gEntity
import com.tmobile.mytmobile.echolocate.nr5g.nsa5g.database.entity.Nr5gTriggerEntity
import com.tmobile.mytmobile.echolocate.nr5g.nsa5g.dataprocessor.*
import com.tmobile.mytmobile.echolocate.nr5g.nsa5g.model.BaseNr5gMetricsData
import com.tmobile.mytmobile.echolocate.nr5g.nsa5g.reportprocessor.Nsa5gDataStatus
import com.tmobile.mytmobile.echolocate.utils.EchoLocateDateUtils
import com.tmobile.mytmobile.echolocate.utils.EchoLocateLog
import com.tmobile.mytmobile.echolocate.nr5g.core.utils.Nr5gUtils
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeoutOrNull
import java.util.*

/**
 * This class is responsible for init processing and storing data for Nsa5g module
 */
class Nsa5gDataCollector(val context: Context) {

    var nr5gDataMetricsWrapper =
        Nr5gDataMetricsWrapper(
            context
        )

    /**
     * Store Nr5g entity data into room database
     * Logs for triggers info
     * @param triggerCode: trigger code when app focus gain, screen on events called
     */
    @ExperimentalCoroutinesApi
    fun storeNr5gEntity(
        triggerCode: Int,
        packageName: String,
        triggerDelay: Int
    ) {
        EchoLocateLog.eLogD("Nr5g CMS Limit-ApplicationState $triggerCode")

        val sessionId = UUID.randomUUID().toString()
        val uniqueId = UUID.randomUUID().toString()

        /** Saved BaseEchoLocateNr5gEntity in to the database*/
        val baseEchoLocateNr5gEntity = prepareEchoLocateNr5gEntity(triggerCode, sessionId)
        val nr5gDao = EchoLocateNr5gDatabase.getEchoLocateNr5gDatabase(context).nr5gDao()
        nr5gDao.insertBaseEchoLocateNr5gEntity(baseEchoLocateNr5gEntity)

        /** Saved Nr5gTriggerEntity in to the database*/
        val nr5gTriggerEntity = prepareNr5gTriggerEntity(
            baseEchoLocateNr5gEntity.triggerTimestamp,
            triggerCode,
            packageName,
            triggerDelay
        )
        nr5gTriggerEntity.sessionId = sessionId
        nr5gTriggerEntity.uniqueId = uniqueId
        nr5gDao.insertNr5gTriggerEntity(nr5gTriggerEntity)

        /** Log for verify a triggers*/
        EchoLocateLog.eLogD(
            "Trigger Invoked NR5G: " +
                    " TriggerTimestamp: ${baseEchoLocateNr5gEntity.triggerTimestamp}" +
                    " TriggerCode: $triggerCode" +
                    " TriggerApplication: $packageName",
            System.currentTimeMillis()
        )

        /** Execute processor classes to save a data to DataBase */
        val apiVersion = nr5gDataMetricsWrapper.getApiVersion()
        val disposables = CompositeDisposable()
        runBlocking<Any> {
            withTimeoutOrNull(Nr5gUtils.TWENTY_ONE_SECONDS) {
                nsa5gProcessors(
                    sessionId,
                    apiVersion,
                    baseEchoLocateNr5gEntity,
                    context,
                    nr5gDataMetricsWrapper
                )
                    .onCompletion {
                        EchoLocateLog.eLogD("Diagnostics: All LTE processors execution finished")
                    }
                    .collect { value ->
                        value?.let {
                            disposables.add(it)
                        }
                    }
            }
            disposables.dispose()
            updateBaseEchoLocateLteEntity(baseEchoLocateNr5gEntity, nr5gDao)
        }
    }


    /**
     * This is used for kotlin coroutines flow setting
     */
    private suspend fun nsa5gProcessors(
        sessionId: String,
        apiVersion: Nr5gBaseDataMetricsWrapper.ApiVersion,
        baseEchoLocateNr5gEntity: BaseEchoLocateNr5gEntity,
        context: Context,
        nr5gDataMetricsWrapper: Nr5gDataMetricsWrapper
    ): Flow<Disposable?> = listOf(
        Nsa5gOEMSVProcessor(context).execute(
            BaseNr5gMetricsData(
                ArrayList<String>(),
                baseEchoLocateNr5gEntity.triggerTimestamp,
                apiVersion,
                sessionId
            )
        ),
        Nsa5gStatusProcessor(context).execute(
            BaseNr5gMetricsData(
                ArrayList<String>(),
                baseEchoLocateNr5gEntity.triggerTimestamp,
                apiVersion,
                sessionId
            )
        ),
        Nsa5gWifiProcessor(context).execute(
            BaseNr5gMetricsData(
                ArrayList<String>(),
                baseEchoLocateNr5gEntity.triggerTimestamp,
                apiVersion,
                sessionId
            )
        ),
        Nsa5gLocationDataProcessor(context).execute(
            BaseNr5gMetricsData(
                ArrayList<String>(),
                baseEchoLocateNr5gEntity.triggerTimestamp,
                apiVersion,
                sessionId
            )
        ),
        Nsa5gNetworkIdentityProcessor(context).execute(
            BaseNr5gMetricsData(
                nr5gDataMetricsWrapper.getNetworkIdentity(),
                baseEchoLocateNr5gEntity.triggerTimestamp,
                apiVersion,
                sessionId
            )
        ),
        Nsa5gDeviceInfoProcessor(context).execute(
            BaseNr5gMetricsData(
                ArrayList<String>(),
                baseEchoLocateNr5gEntity.triggerTimestamp,
                apiVersion,
                sessionId
            )
        ),
        Nsa5gActiveNetworkProcessor(context).execute(
            BaseNr5gMetricsData(
                ArrayList<String>(),
                baseEchoLocateNr5gEntity.triggerTimestamp,
                apiVersion,
                sessionId
            )
        ),

        Nsa5gConnectedWifiStatusProcessor(context).execute(
            BaseNr5gMetricsData(
                ArrayList<String>(),
                baseEchoLocateNr5gEntity.triggerTimestamp,
                apiVersion,
                sessionId
            )
        ),
        Nsa5gDataNetworkTypeProcessor(context).execute(
            BaseNr5gMetricsData(
                ArrayList<String>(),
                baseEchoLocateNr5gEntity.triggerTimestamp,
                apiVersion,
                sessionId
            )
        ),

        Nsa5gEndcLteLogProcessor(context).execute(
            BaseNr5gMetricsData(
                nr5gDataMetricsWrapper.getEndcLteLog(),
                baseEchoLocateNr5gEntity.triggerTimestamp,
                apiVersion,
                sessionId
            )
        ),

        Nsa5gMmwCellLogProcessor(context).execute(
            BaseNr5gMetricsData(
                nr5gDataMetricsWrapper.get5gNrMmwCellLog(),
                baseEchoLocateNr5gEntity.triggerTimestamp,
                apiVersion,
                sessionId
            )
        ),

        Nsa5gUiLogProcessor(context).execute(
            BaseNr5gMetricsData(
                nr5gDataMetricsWrapper.getNr5gUiLog(),
                baseEchoLocateNr5gEntity.triggerTimestamp,
                apiVersion,
                sessionId
            )
        ),

        Nsa5gEndcUplinkLogProcessor(context).execute(
            BaseNr5gMetricsData(
                nr5gDataMetricsWrapper.getEndcUplinkLog(),
                baseEchoLocateNr5gEntity.triggerTimestamp,
                apiVersion,
                sessionId
            )
        )
    ).asFlow()

    /**
     * This function is used to update base echo locate lte entity
     */
    private fun updateBaseEchoLocateLteEntity(
        baseEchoLocateNr5gEntity: BaseEchoLocateNr5gEntity,
        nr5gDao: Nr5gDao
    ) {
        baseEchoLocateNr5gEntity.status = Nsa5gDataStatus.STATUS_RAW
        nr5gDao.updateBaseEchoLocateNr5gEntityStatus(baseEchoLocateNr5gEntity)
    }

    /**
     * Returns base echo locate Nr5g entity with current system time
     * @param triggerCode: trigger code of the specific app state
     * */
    private fun prepareEchoLocateNr5gEntity(
        triggerCode: Int,
        sessionId: String
    ): BaseEchoLocateNr5gEntity {
        return BaseEchoLocateNr5gEntity(
            triggerCode,
            "", // While creating the record, the status will be empty (addressed DIA-6523)
            EchoLocateDateUtils.getTriggerTimeStamp(),
            sessionId
        )
    }

    /**
     * Returns Nr5gTriggerEntity with current system time
     * */
    private fun prepareNr5gTriggerEntity(
        timestamp: String,
        triggerId: Int,
        triggerApp: String,
        triggerDelay: Int
    ): Nr5gTriggerEntity {
        return Nr5gTriggerEntity(
            timestamp,
            triggerId,
            triggerApp,
            triggerDelay
        )
    }
}