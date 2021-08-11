package com.tmobile.mytmobile.echolocate.voice.dataprocessor

import android.content.Context
import android.content.Intent
import com.tmobile.echolocate.CallSessionProto
import com.tmobile.mytmobile.echolocate.utils.EchoLocateLog
import com.tmobile.mytmobile.echolocate.voice.repository.database.entity.OEMSoftwareVersionEntity
import com.tmobile.mytmobile.echolocate.voice.utils.VoiceOEMSoftwareVersionCollector

/**
 * class OEMSoftwareVersionProcessor
 * @param context : Context
 *
 * extends
 * class  BaseIntentProcessor
 * @param context
 */
class OEMSoftwareVersionProcessor(context: Context) : BaseIntentProcessor(context) {

    /**
     * This method is called when the intent is received, to handle each voice action individually
     * which gets the extras from the intent and generated object specific to the listener
     * @param intent : [Intent] intent received from the broad cast
     *
     * @param eventTimeStamp The timestamp at which the intent was received by the application.
     *
     * @return [OEMSoftwareVersionEntity] object after converting the intent to OEMSoftwareVersionEntity
     */
    override suspend fun processIntent(intent: Intent, eventTimeStamp: Long): Boolean {

        val oemInfo = VoiceOEMSoftwareVersionCollector().getOEMSoftwareVersion(context)

        val voiceDataStore = getVoiceDataStore(intent)
        voiceDataStore.updateData { cellSessionProto ->
            val newOemsvBuilder = CallSessionProto.Oemsv.newBuilder()
            newOemsvBuilder.setAndroidVersion(oemInfo.androidVersion)
            newOemsvBuilder.setBuildName(oemInfo.buildName)
            newOemsvBuilder.setCustomVersion(oemInfo.customVersion)
            newOemsvBuilder.setRadioVersion(oemInfo.radioVersion)
            newOemsvBuilder.setSv(oemInfo.softwareVersion)

            EchoLocateLog.eLogD("DS: Oemsv :$newOemsvBuilder")
            cellSessionProto.toBuilder().setOem(newOemsvBuilder).build()
        }

        return true
    }
}