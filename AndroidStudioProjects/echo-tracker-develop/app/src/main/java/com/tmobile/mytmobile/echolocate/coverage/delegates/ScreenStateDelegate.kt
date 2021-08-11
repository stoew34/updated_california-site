package com.tmobile.mytmobile.echolocate.coverage.delegates

/**
 * Created by Mahesh Shetye on 2020-04-23
 *
 * Triggers data collection when screen state changes.
 *
 */

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.sqlite.SQLiteDatabaseCorruptException
import android.text.format.DateUtils
import com.tmobile.mytmobile.echolocate.configuration.model.Coverage
import com.tmobile.mytmobile.echolocate.coverage.utils.CoverageSharedPreference
import com.tmobile.mytmobile.echolocate.utils.EchoLocateLog
import com.tmobile.mytmobile.echolocate.utils.SingletonHolder
import androidx.annotation.VisibleForTesting
import com.tmobile.mytmobile.echolocate.utils.FirebaseUtils
import kotlinx.coroutines.*

class ScreenStateDelegate(ctx: Context) : BaseDelegate(ctx) {

    companion object : SingletonHolder<ScreenStateDelegate, Context>(::ScreenStateDelegate)

    override fun initTrigger(coverage: Coverage): Boolean {
        val triggerData = coverage.screenTrigger
        EchoLocateLog.eLogD("Coverage : Screen trigger value : ${triggerData.enabled}")
        if (triggerData.enabled && !isReceiverRegistered) {
            CoverageSharedPreference.init(context)

            registerReceiver()

            if (CoverageSharedPreference.screenTriggerLimit != triggerData.eventsPerHour) {
                CoverageSharedPreference.screenTriggerLimit = triggerData.eventsPerHour
            }
        } else if (!triggerData.enabled && isReceiverRegistered) {
            dispose()
        }

        return isReceiverRegistered
    }

    override fun registerReceiver(): Boolean {
        if (!isReceiverRegistered) {

            triggerReceiver = ScreenStateReceiver(this)
            val filter = IntentFilter()
            filter.addAction(Intent.ACTION_SCREEN_ON)
            filter.addAction(Intent.ACTION_SCREEN_OFF)
            context.registerReceiver(triggerReceiver, filter)
            isReceiverRegistered = true
        }
        return isReceiverRegistered
    }

    /**
     * Reset the trigger count to zero
     * and re-register the broadcast if required
     */
    override fun resetTrigger() {
        EchoLocateLog.eLogD("Coverage : Screen resetTrigger")
        CoverageSharedPreference.screenTriggerCount = 0
        registerReceiver()
    }

    /**
     * Get the current trigger count
     */
    @VisibleForTesting
    fun getCurrentTriggerCounts(): Int {
        return CoverageSharedPreference.screenTriggerCount
    }

    override fun onHandleTrigger(triggerSource: TriggerSource) {

        GlobalScope.launch(Dispatchers.IO) {
            runBlocking {
                try {
                    storeCoverageEntity(triggerSource)
                } catch (ex: SQLiteDatabaseCorruptException) {
                    EchoLocateLog.eLogE("ScreenStateDelegate : onHandleTrigger() :: Exception : $ex")
                    FirebaseUtils.logCrashToFirebase("Exception in ScreenStateDelegate : onHandleTrigger()", ex.localizedMessage, "SQLiteDatabaseCorruptException")
                }
            }
        }
    }

    /**
     * Listens to screen on/off events.
     */
    private class ScreenStateReceiver(val coverageTriggerHandler: ICoverageTriggerHandler) : BroadcastReceiver() {

        override fun onReceive(
                context: Context,
                intent: Intent
        ) {
            EchoLocateLog.eLogD("Screen intent received : $intent")
            processEvent()
        }

        @Synchronized
        private fun processEvent() {
            val currTime = System.currentTimeMillis()

            if ((currTime - CoverageSharedPreference.lastScreenTriggerTime) >
                (DateUtils.HOUR_IN_MILLIS / CoverageSharedPreference.screenTriggerLimit) ) {

                CoverageSharedPreference.screenTriggerCount++
                CoverageSharedPreference.lastScreenTriggerTime = currTime

                coverageTriggerHandler.onHandleTrigger(TriggerSource.SCREEN_ACTIVITY)

                EchoLocateLog.eLogD("Coverage : Screen intent processed : ${CoverageSharedPreference.screenTriggerCount} / ${CoverageSharedPreference.screenTriggerLimit}")
            }
        }
    }
}
