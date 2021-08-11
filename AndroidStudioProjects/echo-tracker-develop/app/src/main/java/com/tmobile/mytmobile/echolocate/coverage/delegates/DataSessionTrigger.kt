package com.tmobile.mytmobile.echolocate.coverage.delegates

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import com.tmobile.mytmobile.echolocate.configuration.model.Coverage
import com.tmobile.mytmobile.echolocate.coverage.utils.CoverageSharedPreference
import com.tmobile.mytmobile.echolocate.utils.EchoLocateLog
import com.tmobile.mytmobile.echolocate.coverage.utils.CoverageUtils.Companion.hasTelephonyFeature
import com.tmobile.mytmobile.echolocate.utils.SingletonHolder

class DataSessionTrigger(context: Context) : BaseDelegate(context) {

    private var phoneStateListener: PhoneStateListener? = null
    private var isDataSessionStartEnabled: Boolean = false
    private var isDataSessionStopEnabled: Boolean = false

    private var lastDataSessionState = -1

    companion object : SingletonHolder<DataSessionTrigger, Context>(::DataSessionTrigger)


    override fun initTrigger(coverage: Coverage): Boolean {
        CoverageSharedPreference.init(context)
        checkDataSessionState(coverage)
        saveTriggerData(coverage)
        if (!isReceiverRegistered && (isDataSessionStartEnabled || isDataSessionStopEnabled)) {
            checkTelephonyAndRegisterReceiver()
        } else if (isReceiverRegistered && (!isDataSessionStartEnabled && !isDataSessionStopEnabled)) {
            dispose()
        }
        return isReceiverRegistered
    }

    /**
     * This function checks data session start and data session stop and returns true/false
     */
    private fun checkDataSessionState(coverage: Coverage) {
        isDataSessionStartEnabled = coverage.dataSessionStart.enabled
        isDataSessionStopEnabled = coverage.dataSessionEnd.enabled
        EchoLocateLog.eLogD("Coverage : Data session Start value : $isDataSessionStartEnabled ")
        EchoLocateLog.eLogD("Coverage : Data session Stop value : $isDataSessionStopEnabled ")

    }

    /**
     * This function checks telephony feature and registers the phone state listener
     */
    private fun checkTelephonyAndRegisterReceiver() {
        if (hasTelephonyFeature(context)) {
            registerReceiver()
        }
    }

    /**
     * This function registers the phone state listener to listen dataSession states
     */
    override fun registerReceiver(): Boolean {
        if (!isReceiverRegistered) {
            lastDataSessionState = telephonyManager!!.dataState
            try {
                /* DataSession Listener should be created on main thread. */
                Handler(Looper.getMainLooper()).post {
                    phoneStateListener = DataSessionTrigger(context).DataSessionListener(this)
                    telephonyManager!!.listen(
                        phoneStateListener,
                        PhoneStateListener.LISTEN_DATA_CONNECTION_STATE
                    )
                }
                EchoLocateLog.eLogD("Coverage : data session state trigger initiated")
                isReceiverRegistered = true
            } catch (e: Exception) {
                EchoLocateLog.eLogE("Coverage : Exception in creating call state listener -> ${e.localizedMessage}")
            }
        }
        return isReceiverRegistered
    }

    /**
     * Reset the trigger count to zero
     * and re-register the broadcast if required
     */
    override fun resetTrigger() {
        resetIfTriggersEnabled()
        registerReceiver()
    }

    private fun resetIfTriggersEnabled() {
        if (isDataSessionStartEnabled) {
            CoverageSharedPreference.dataSessionStartTriggerCount = 0
            EchoLocateLog.eLogD("Coverage : DataSession start Trigger count after reset ${CoverageSharedPreference.dataSessionStartTriggerCount}")
        }
        if (isDataSessionStopEnabled) {
            CoverageSharedPreference.dataSessionStopTriggerCount = 0
            EchoLocateLog.eLogD("Coverage : DataSession stop Trigger count after reset ${CoverageSharedPreference.dataSessionStopTriggerCount}")
        }

    }

    /**
     * This function stores the eventsPerHour values for Data Session state
     */
    private fun saveTriggerData(coverage: Coverage) {
        if (isDataSessionStartEnabled) {
            if (CoverageSharedPreference.dataSessionStartTriggerLimit != coverage.dataSessionStart.eventsPerHour)
                CoverageSharedPreference.dataSessionStartTriggerLimit =
                    coverage.dataSessionStart.eventsPerHour
        }

        if (isDataSessionStopEnabled) {
            if (CoverageSharedPreference.dataSessionStopTriggerLimit != coverage.dataSessionEnd.eventsPerHour)
                CoverageSharedPreference.dataSessionStopTriggerLimit =
                    coverage.dataSessionEnd.eventsPerHour
        }
    }


    override fun onHandleTrigger(triggerSource: TriggerSource) {
        when {
            TriggerSource.DATA_SESSION_ATTEMPT == triggerSource ->
                if (isDataSessionStartEnabled) {
                    if (CoverageSharedPreference.dataSessionStartTriggerCount < CoverageSharedPreference.dataSessionStartTriggerLimit) {
                        CoverageSharedPreference.dataSessionStartTriggerCount++
                        EchoLocateLog.eLogD(
                            "Coverage : Data session start trigger Count : ${CoverageSharedPreference.dataSessionStartTriggerCount}"
                                    + " Data Session Start Limit : ${CoverageSharedPreference.dataSessionStartTriggerLimit}"
                        )
                        storeTriggerState(triggerSource)
                    }
                }
            TriggerSource.DATA_SESSION_END == triggerSource -> {
                if (isDataSessionStopEnabled) {
                    if (CoverageSharedPreference.dataSessionStopTriggerCount < CoverageSharedPreference.dataSessionStopTriggerLimit) {
                        CoverageSharedPreference.dataSessionStopTriggerCount++
                        EchoLocateLog.eLogD(
                            "Coverage : Data session stop trigger count : ${CoverageSharedPreference.dataSessionStopTriggerCount}" +
                                    " Data Session Limit : ${CoverageSharedPreference.dataSessionStopTriggerLimit}"
                        )
                        storeTriggerState(triggerSource)
                    }
                }
            }
        }
    }

    /**
     * Disposes the object.
     */
    override fun dispose() {
        super.dispose()
        disposeListener()
    }

    private fun disposeListener() {
        if (telephonyManager != null) {
            Handler(Looper.getMainLooper()).post {
                telephonyManager!!.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE)
            }
        }
        isReceiverRegistered = false
        EchoLocateLog.eLogD("Phone state listener unregistered")
    }

    /**
     * The listener interface for receiving data session state changed events.
     */
    inner class DataSessionListener(val coverageTriggerHandler: ICoverageTriggerHandler) :
        PhoneStateListener() {
        override fun onDataConnectionStateChanged(state: Int) {
            if (lastDataSessionState != state) {
                if (state == TelephonyManager.DATA_CONNECTING || (state == TelephonyManager.DATA_CONNECTED && lastDataSessionState != TelephonyManager.DATA_CONNECTING)) {
                    coverageTriggerHandler.onHandleTrigger(TriggerSource.DATA_SESSION_ATTEMPT)
                } else if (state == TelephonyManager.DATA_DISCONNECTED) {
                    coverageTriggerHandler.onHandleTrigger(TriggerSource.DATA_SESSION_END)
                }
                lastDataSessionState = state
            }
        }
    }

}