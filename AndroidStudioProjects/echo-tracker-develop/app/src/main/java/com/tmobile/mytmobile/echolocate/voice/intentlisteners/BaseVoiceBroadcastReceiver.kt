package com.tmobile.mytmobile.echolocate.voice.intentlisteners

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import com.tmobile.echolocate.HistoryProto
import com.tmobile.mytmobile.echolocate.utils.DevLogUtils
import com.tmobile.mytmobile.echolocate.utils.EchoLocateLog
import com.tmobile.mytmobile.echolocate.utils.FileUtils
import com.tmobile.mytmobile.echolocate.variant.Constants
import com.tmobile.mytmobile.echolocate.voice.dataprocessor.BaseIntentProcessor
import com.tmobile.mytmobile.echolocate.voice.manager.IVoiceIntentHandler
import com.tmobile.mytmobile.echolocate.voice.repository.datastore.HistorySerializer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*

/**
 * This class is the main class that handles receiver of all the actions that are registered dynamically,
 * from here this intent is tranferred to the respective listeners through onHandleIntent method
 */
class BaseVoiceBroadcastReceiver(context: Context) : BroadcastReceiver() {

    private var voiceHandler: IVoiceIntentHandler? = null
    private var fileNameVoiceIntents: String = "log_voice_intents.txt"

    private var historyDataStore = DataStoreFactory.create(
        serializer = HistorySerializer,
        produceFile = { context.applicationContext.dataStoreFile("history.pb") }
    )

    /**
     * This method is called when the BroadcastReceiver is receiving an Intent broadcast.
     * @param context: [Context]
     *
     * @param intent: [Intent]
     *
     */
    override fun onReceive(context: Context?, intent: Intent?) {
        EchoLocateLog.eLogD("Intent received: ")
        GlobalScope.launch(Dispatchers.Main) {
            storeIntentDataIntoAFile(intent)
            saveIntentIntoDataStore(intent)
            voiceHandler?.onHandleIntent(intent, System.currentTimeMillis())
        }
    }

    private suspend fun saveIntentIntoDataStore(intent: Intent?) {
        historyDataStore.updateData { historyProto: HistoryProto ->

            var capture = HistoryProto.Capture.newBuilder()
            capture.time = Calendar.getInstance().time.time
            capture.callID = intent!!.getStringExtra(BaseIntentProcessor.CALL_ID_EXTRA)
            capture.intent = DevLogUtils.getIntentData(intent)

            historyProto.toBuilder().addCapture(capture).build()
        }
    }

    /**
     * This method sets the listener to handle callback mechanism
     */
    fun setListener(voiceIntentHandler: IVoiceIntentHandler) {
        voiceHandler = voiceIntentHandler
    }

    /**
     * This method is used to store intent data into a file
     * @param intent: [Intent]
     *
     */
    private suspend fun storeIntentDataIntoAFile(intent: Intent?) {
        if (Constants.SAVE_DATA_TO_FILE) {
            FileUtils.saveFileToExternalStorage(
                DevLogUtils.getIntentData(
                    intent
                ), fileNameVoiceIntents, true
            )
        }
    }
}