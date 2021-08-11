package com.tmobile.mytmobile.echolocate.lte.intentlisteners

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.tmobile.mytmobile.echolocate.lte.manager.ILteIntentHandler
import com.tmobile.mytmobile.echolocate.utils.DevLogUtils
import com.tmobile.mytmobile.echolocate.utils.EchoLocateLog
import com.tmobile.mytmobile.echolocate.utils.FileUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/**
 * This class is the main class that handles receiver of all the actions that are registered dynamically,
 * from here this intent is transferred to the respective listeners through onHandleIntent method
 */
class BaseLteBroadcastReceiver : BroadcastReceiver() {

    var lteHandler: ILteIntentHandler? = null
    private var fileName: String = "log_all_intents.txt"

    /**
     * This method is called when the BroadcastReceiver is receiving an Intent broadcast.
     * @param context: [Context]
     *
     * @param intent: [Intent]
     *
     */
    override fun onReceive(context: Context?, intent: Intent?) {

        EchoLocateLog.eLogD("Diagnostic : LTE Intent received")

        GlobalScope.launch(Dispatchers.IO) {
             runBlocking {
                storeIntentDataIntoAFile(intent)
                lteHandler?.onHandleIntent(intent, System.currentTimeMillis())
            }
        }

    }

    /**
     * This method sets the listener to handle callback mechanism
     */
    fun setListener(lteIntentHandler: ILteIntentHandler) {
        lteHandler = lteIntentHandler
    }

    /**
     *  method/function writes intent data to external storage file "dia_debug"
     */
    private fun storeIntentDataIntoAFile(intent: Intent?) {

        FileUtils.saveFileToExternalStorage(
            DevLogUtils.getIntentData(
                intent
            ), fileName, true
        )

    }
}