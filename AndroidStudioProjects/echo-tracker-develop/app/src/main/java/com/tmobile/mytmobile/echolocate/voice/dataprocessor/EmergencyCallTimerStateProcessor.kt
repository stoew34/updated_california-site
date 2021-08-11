package com.tmobile.mytmobile.echolocate.voice.dataprocessor

import android.content.Context
import android.content.Intent
import com.tmobile.echolocate.CallSessionProto
import com.tmobile.mytmobile.echolocate.voice.model.EmergencyCallTimerStateData

/**
 * Handler class that gets called when the app receives diagandroid.phone.emergencyCallTimerState
 * broadcast. OEM shall broadcast an "diagandroid.phone.emergencyCallTimerState" message whenever
 * the state of E1, E2, or E3 timer for E911 call on a device is changed.
 * This intent would contain String Extras that would contain all details
 */
class EmergencyCallTimerStateProcessor(context: Context) : BaseIntentProcessor(context) {

    /**
     * This method is called when the intent is received, to handle each voice action individually
     * which gets the extras from the intent and generated object specific to the listener
     * @param intent : [Intent] intent received from the broad cast
     * @return [EmergencyCallTimerStateData] object after converting the intent to EmergencyCallTimerStateData
     */
    override suspend fun processIntent(intent: Intent, eventTimeStamp: Long): Boolean {

        val timerName = intent.getStringExtra(EMERGENCY_CALL_TIMER_NAME) ?: ""
        val timerState = intent.getStringExtra(EMERGENCY_CALL_TIMER_STATE) ?: ""
        val oemTimestamp = intent.getStringExtra(OEM_TIMESTAMP_EXTRA) ?: eventTimeStamp.toString()


        val voiceDataStore = getVoiceDataStore(intent)
        voiceDataStore.updateData { cellSessionProto ->
            val deviceIntents = cellSessionProto.deviceIntents
            val newEmergencyCallTimerStateDataBuilder =
                CallSessionProto.DeviceIntents.EmergencyCallTimerStateData.newBuilder()

            newEmergencyCallTimerStateDataBuilder.timerName = timerName
            newEmergencyCallTimerStateDataBuilder.timerState = timerState
            newEmergencyCallTimerStateDataBuilder.eventTimestamp = eventTimeStamp.toString()
            newEmergencyCallTimerStateDataBuilder.oemTimestamp = oemTimestamp

            val newEventInfoBuilder = CallSessionProto.DeviceIntents.EventInfo.newBuilder()
            newEventInfoBuilder.setCellInfo(CellInfoDataProcessor(context).getCellInfoBuilder(intent))
            newEventInfoBuilder.setLocationData(VoiceLocationDataProcessor(context).fetchLocationDataBuilderSync())
            newEmergencyCallTimerStateDataBuilder.setEventInfo(newEventInfoBuilder)

            val addEmergencyCallTimerStateData = deviceIntents.toBuilder()
                .addEmergencyCallTimerStateData(newEmergencyCallTimerStateDataBuilder)

            cellSessionProto.toBuilder().setDeviceIntents(addEmergencyCallTimerStateData).build()
        }

        return true
    }
}