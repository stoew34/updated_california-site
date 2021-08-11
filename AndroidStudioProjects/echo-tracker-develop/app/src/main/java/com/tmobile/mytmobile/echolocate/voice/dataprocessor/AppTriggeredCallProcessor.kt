package com.tmobile.mytmobile.echolocate.voice.dataprocessor

import android.content.Context
import android.content.Intent
import com.tmobile.echolocate.CallSessionProto
import com.tmobile.mytmobile.echolocate.utils.EchoLocateLog
import com.tmobile.mytmobile.echolocate.voice.model.AppTriggeredCallData
import com.tmobile.mytmobile.echolocate.voice.utils.AppTriggeredDataUtils

/**
 * Handler class that gets called when the app receives diagandroid.phone.AppTriggeredCall
 * broadcast. The intent carries data of the call state such as call code, call state,
 * oemtimestamp, event timestamp, call id,event info and location details
 */
class AppTriggeredCallProcessor(context: Context) : BaseIntentProcessor(context) {

    /**
     * This method is called when the intent is received, to handle each voice action individually
     * which gets the extras from the intent and generated object specific to the listener
     * @param intent : [Intent] intent received from the broad cast
     * @return [AppTriggeredCallData] object after converting the intent to AppTriggeredCallData
     */
    override suspend fun processIntent(intent: Intent, eventTimeStamp: Long): Boolean {

        val packageName = intent.getStringExtra(PACKAGE_NAME_EXTRA) ?: ""
        val oemTimestamp = intent.getStringExtra(OEM_TIMESTAMP_EXTRA) ?: eventTimeStamp.toString()

        val voiceDataStore = getVoiceDataStore(intent)
        voiceDataStore.updateData { cellSessionProto ->
            val deviceIntents = cellSessionProto.deviceIntents
            val newAppTriggeredCallDataBuilder =
                CallSessionProto.DeviceIntents.AppTriggeredCallData.newBuilder()

            newAppTriggeredCallDataBuilder.appName = AppTriggeredDataUtils.getPackageLabel(
                context,
                packageName
            )
            newAppTriggeredCallDataBuilder.appPackageId = packageName
            newAppTriggeredCallDataBuilder.appVersionCode = AppTriggeredDataUtils.getApplicationVersionCode(
                context,
                packageName
            )
            newAppTriggeredCallDataBuilder.appVersionName = AppTriggeredDataUtils.getApplicationVersionName(
                context,
                packageName
            )
            newAppTriggeredCallDataBuilder.oemTimestamp = oemTimestamp
            newAppTriggeredCallDataBuilder.eventTimestamp = eventTimeStamp.toString()

            val newEventInfoBuilder = CallSessionProto.DeviceIntents.EventInfo.newBuilder()
            newEventInfoBuilder.setCellInfo(CellInfoDataProcessor(context).getCellInfoBuilder(intent))
            newEventInfoBuilder.setLocationData(VoiceLocationDataProcessor(context).fetchLocationDataBuilderSync())
            newAppTriggeredCallDataBuilder.setEventInfo(newEventInfoBuilder)

            val addAppTriggeredCallData =
                deviceIntents.toBuilder().addAppTriggeredCallData(newAppTriggeredCallDataBuilder)
            EchoLocateLog.eLogD("DS: AppTriggeredCallData :$newAppTriggeredCallDataBuilder")
            cellSessionProto.toBuilder().setDeviceIntents(addAppTriggeredCallData).build()
        }

        return true
    }

}

