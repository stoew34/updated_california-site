package com.tmobile.mytmobile.echolocate.voice.dataprocessor

import android.content.Context
import android.content.Intent
import com.tmobile.echolocate.CallSessionProto
import com.tmobile.mytmobile.echolocate.utils.EchoLocateLog


class RtpdlCallStateProcessor(context: Context) : BaseIntentProcessor(context) {

    /**
     *  fun processIntent
     *     -captures real time transport protocol data items from OS broadcast [diagandroid.phone.RTPDLStat]
     *     -instantiates RtpdlStateData object with captured data items
     *     -initiates saving of captured data items to database
     *  @param intent :Intent
     *  @param eventTimeStamp :Long
     *  @return RtpdlStateData
     *
     */
    override suspend fun processIntent(intent: Intent, eventTimeStamp: Long): Boolean {

        val voiceDataStore = getVoiceDataStore(intent)
        voiceDataStore.updateData { cellSessionProto ->
            val deviceIntents = cellSessionProto.deviceIntents
            val newRtpdlStateDataBuilder =
                CallSessionProto.DeviceIntents.RtpdlStateData.newBuilder()

            newRtpdlStateDataBuilder.delay =
                if (intent.hasExtra(OEM_DELAY_EXTRA)) getStringToDouble(
                    intent.getStringExtra(OEM_DELAY_EXTRA) ?: "0.0"
                ) else 0.0
            newRtpdlStateDataBuilder.sequence = 0.0
            newRtpdlStateDataBuilder.jitter =
                if (intent.hasExtra(OEM_JITTER_EXTRA)) getStringToDouble(
                    intent.getStringExtra(OEM_JITTER_EXTRA) ?: "0.0"
                ) else 0.0
            newRtpdlStateDataBuilder.measuredPeriod =
                if (intent.hasExtra(OEM_MEASURED_PERIOD_EXTRA)) getStringToDouble(
                    intent.getStringExtra(OEM_MEASURED_PERIOD_EXTRA)!!
                ) else 0.0
            newRtpdlStateDataBuilder.oemTimestamp =
                intent.getStringExtra(OEM_TIMESTAMP_EXTRA) ?: eventTimeStamp.toString()
            newRtpdlStateDataBuilder.eventTimestamp = eventTimeStamp.toString()

            val newEventInfoBuilder = CallSessionProto.DeviceIntents.EventInfo.newBuilder()
            newEventInfoBuilder.setCellInfo(CellInfoDataProcessor(context).getCellInfoBuilder(intent))
            newEventInfoBuilder.setLocationData(VoiceLocationDataProcessor(context).fetchLocationDataBuilderSync())
            newRtpdlStateDataBuilder.setEventInfo(newEventInfoBuilder)


            deviceIntents.toBuilder().addRtpdlStateData(newRtpdlStateDataBuilder)
            EchoLocateLog.eLogD("DS: RtpdlStateData :$newRtpdlStateDataBuilder")
            cellSessionProto.toBuilder().setDeviceIntents(deviceIntents).build()
        }

        return true
    }

    /**
     * Function to safely parse string to double.
     *
     * If string is not a valid double, then the function will return 0.0
     */
    private fun getStringToDouble(value: String): Double {
        try {
            return value.toDouble()
        } catch (exception: NumberFormatException) {
            EchoLocateLog.eLogE("error: ${exception.localizedMessage}")
        }

        return 0.0
    }
}