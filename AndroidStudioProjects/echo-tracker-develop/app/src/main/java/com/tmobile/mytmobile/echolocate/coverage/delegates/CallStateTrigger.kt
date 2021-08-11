package com.tmobile.mytmobile.echolocate.coverage.delegates

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import com.tmobile.mytmobile.echolocate.configuration.model.Coverage
import com.tmobile.mytmobile.echolocate.coverage.delegates.TriggerSource.VOICE_CALL_ENDED
import com.tmobile.mytmobile.echolocate.coverage.delegates.TriggerSource.VOICE_CALL_STARTED
import com.tmobile.mytmobile.echolocate.coverage.utils.CoverageSharedPreference
import com.tmobile.mytmobile.echolocate.utils.EchoLocateLog
import com.tmobile.mytmobile.echolocate.coverage.utils.CoverageUtils.Companion.hasTelephonyFeature
import com.tmobile.mytmobile.echolocate.utils.SingletonHolder

/**
 * Triggers data collection when an incoming or outgoing call is placed.
 */
class CallStateTrigger(context: Context) : BaseDelegate(context) {

    private var phoneStateListener: PhoneStateListener? = null
    private var isCallStartEnabled: Boolean = false
    private var isCallStopEnabled: Boolean = false
    /**
     * The last call state. One of [PhoneStateListener] call states
     */
    private var lastCallState = -1
    companion object : SingletonHolder<CallStateTrigger, Context>(::CallStateTrigger)
    /**
     * Initializes the call state
     */
    override fun initTrigger(coverage: Coverage): Boolean {
        CoverageSharedPreference.init(context)
        checkCallState(coverage)
        saveTriggerData(coverage)
        if (!isReceiverRegistered && (isCallStartEnabled || isCallStopEnabled)) {
            checkTelephonyAndRegisterReceiver()
        } else if (isReceiverRegistered && (!isCallStartEnabled && !isCallStopEnabled)) {
            dispose()
        }
        return isReceiverRegistered
    }
    /**
     * This function checks call start and call stop and returns true/false
     */
    private fun checkCallState(coverage: Coverage) {
        isCallStartEnabled = coverage.voiceCallStart.enabled
        isCallStopEnabled = coverage.voiceCallEnd.enabled
        EchoLocateLog.eLogD("Coverage : Call Start value : $isCallStartEnabled ")
        EchoLocateLog.eLogD("Coverage : Call Stop value : $isCallStopEnabled ")
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
     *  This function stores the eventsPerHour values for voice state
    */
    private fun saveTriggerData(coverage: Coverage) {
        if (isCallStartEnabled) {
            if (CoverageSharedPreference.voiceStartTriggerLimit != coverage.voiceCallStart.eventsPerHour)
                CoverageSharedPreference.voiceStartTriggerLimit =
                    coverage.voiceCallStart.eventsPerHour
        }

        if (isCallStopEnabled) {
            if (CoverageSharedPreference.voiceStopTriggerLimit != coverage.voiceCallEnd.eventsPerHour)
                CoverageSharedPreference.voiceStopTriggerLimit =
                    coverage.voiceCallEnd.eventsPerHour
        }
    }

    /**
     * This function registers the phone state listener
     */
    override fun registerReceiver(): Boolean {
        if (!isReceiverRegistered) {
            lastCallState = telephonyManager!!.callState
            try {
                /* PhoneState Listener should be created on main thread. */
                Handler(Looper.getMainLooper()).post {
                    phoneStateListener = CallStateTrigger(context).VoiceStateListener(this)
                    telephonyManager!!.listen(
                        phoneStateListener,
                        PhoneStateListener.LISTEN_CALL_STATE
                    )
                }
                EchoLocateLog.eLogD("Coverage : Voice state trigger initiated")
                isReceiverRegistered = true
            } catch (e: Exception) {
                EchoLocateLog.eLogE("Coverage : Exception in creating call state listener -> ${e.localizedMessage}")
            }
        }
        return isReceiverRegistered
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
     * Reset the trigger count to zero
     * and re-register the broadcast if required
     */
    override fun resetTrigger() {
        resetIfTriggersEnabled()
        registerReceiver()
    }

    private fun resetIfTriggersEnabled() {
        if (isCallStartEnabled) {
            CoverageSharedPreference.voiceStartTriggerCount = 0
            EchoLocateLog.eLogD("Coverage : CallStart Trigger count after reset ${CoverageSharedPreference.voiceStartTriggerCount}")
        }
        if (isCallStopEnabled) {
            CoverageSharedPreference.voiceStopTriggerCount = 0
            EchoLocateLog.eLogD("Coverage : CallStop Trigger count after reset ${CoverageSharedPreference.voiceStopTriggerCount}")
        }
    }

    override fun onHandleTrigger(triggerSource: TriggerSource) {
        when {
            VOICE_CALL_STARTED == triggerSource -> {
                if (isCallStartEnabled) {
                    if (CoverageSharedPreference.voiceStartTriggerCount < CoverageSharedPreference.voiceStartTriggerLimit) {
                        CoverageSharedPreference.voiceStartTriggerCount++
                        EchoLocateLog.eLogD(
                            "Coverage : Voice call start trigger Count : ${CoverageSharedPreference.voiceStartTriggerCount}"
                                    + " Voice Start Limit : ${CoverageSharedPreference.voiceStartTriggerLimit}"
                        )
                        storeTriggerState(triggerSource)
                    }
                }
            }
            VOICE_CALL_ENDED == triggerSource -> {
                if (isCallStopEnabled) {
                    if (CoverageSharedPreference.voiceStopTriggerCount < CoverageSharedPreference.voiceStopTriggerLimit) {
                        CoverageSharedPreference.voiceStopTriggerCount++
                        EchoLocateLog.eLogD(
                            "Coverage : Voice call stop trigger count : ${CoverageSharedPreference.voiceStopTriggerCount}" +
                                    " Voice Stop Limit : ${CoverageSharedPreference.voiceStopTriggerLimit}"
                        )
                        storeTriggerState(triggerSource)
                    }
                }
            }
        }
    }

    /**
     * The listener interface for receiving call state changed events.
     */
    inner class VoiceStateListener(private val coverageTriggerHandler: ICoverageTriggerHandler) :
        PhoneStateListener() {
        override fun onCallStateChanged(state: Int, incomingNumber: String?) {
            if (lastCallState != state) {
                if (state == TelephonyManager.CALL_STATE_OFFHOOK) {
                    coverageTriggerHandler.onHandleTrigger(VOICE_CALL_STARTED)
                } else if (state == TelephonyManager.CALL_STATE_IDLE && lastCallState == TelephonyManager.CALL_STATE_OFFHOOK) {
                    coverageTriggerHandler.onHandleTrigger(VOICE_CALL_ENDED)
                }
                lastCallState = state
            }
        }
    }
}