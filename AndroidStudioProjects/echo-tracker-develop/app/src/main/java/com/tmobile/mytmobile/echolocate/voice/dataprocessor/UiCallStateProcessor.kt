package com.tmobile.mytmobile.echolocate.voice.dataprocessor

import android.content.Context
import android.content.Intent
import com.tmobile.echolocate.CallSessionProto
import com.tmobile.mytmobile.echolocate.voice.model.UiCallStateData

/**
 * Handler class that gets called when the app receives diagandroid.phone.UICallState
 * broadcast everytime the UI state of the call changes. Message will only be broadcasted
 * during an active voice call session.
 */
class UiCallStateProcessor(context: Context) : BaseIntentProcessor(context) {

    /**
     * This method is called when the intent is received, to handle each voice action individually
     * which gets the extras from the intent and generated object specific to the listener
     * @param intent : [Intent] intent received from the broad cast
     *
     * @param eventTimeStamp The timestamp at which the intent was received by the application.
     *
     * @return [UiCallStateData] object after converting the intent to DetailedCallStateData
     */
    override suspend fun processIntent(intent: Intent, eventTimeStamp: Long): Boolean {

        val voiceDataStore = getVoiceDataStore(intent)
        voiceDataStore.updateData { cellSessionProto ->
            val deviceIntents = cellSessionProto.deviceIntents
            val newUiCallStateDataBuilder =
                CallSessionProto.DeviceIntents.UiCallStateData.newBuilder()

            newUiCallStateDataBuilder.uiCallState = intent.getStringExtra(UI_CALL_STATE_EXTRA) ?: ""
            newUiCallStateDataBuilder.oemTimestamp =
                intent.getStringExtra(OEM_TIMESTAMP_EXTRA) ?: eventTimeStamp.toString()
            newUiCallStateDataBuilder.eventTimestamp = eventTimeStamp.toString()

            val newEventInfoBuilder = CallSessionProto.DeviceIntents.EventInfo.newBuilder()
            newEventInfoBuilder.setCellInfo(CellInfoDataProcessor(context).getCellInfoBuilder(intent))
            newEventInfoBuilder.setLocationData(VoiceLocationDataProcessor(context).fetchLocationDataBuilderSync())
            newUiCallStateDataBuilder.setEventInfo(newEventInfoBuilder)

            val addUiCallStateData =
                deviceIntents.toBuilder().addUiCallStateData(newUiCallStateDataBuilder)

            cellSessionProto.toBuilder().setDeviceIntents(addUiCallStateData).build()
        }

        return true
    }

    /**
     * To validate the received intent if it has call id and call number
     * @param intent: [Intent]
     */
    override fun isValidIntent(intent: Intent): Boolean {
        /**checking if intent null or empty and do nothing in this case*/
        return (super.isValidIntent(intent) && (intent.getStringExtra(CALL_NUMBER_EXTRA))!!.isNotEmpty())
    }
}