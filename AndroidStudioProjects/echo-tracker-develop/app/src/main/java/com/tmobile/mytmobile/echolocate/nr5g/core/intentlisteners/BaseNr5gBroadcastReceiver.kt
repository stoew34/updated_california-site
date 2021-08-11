package com.tmobile.mytmobile.echolocate.nr5g.core.intentlisteners

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.tmobile.mytmobile.echolocate.BuildConfig
import com.tmobile.mytmobile.echolocate.nr5g.core.utils.Nr5gIntents.TRIGGER_TIMESTAMP
import com.tmobile.mytmobile.echolocate.nr5g.manager.INr5gIntentHandler
import com.tmobile.mytmobile.echolocate.utils.DevLogUtils
import com.tmobile.mytmobile.echolocate.utils.FileUtils
import com.tmobile.mytmobile.echolocate.variant.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class BaseNr5gBroadcastReceiver : BroadcastReceiver() {

    var nr5GHandler: INr5gIntentHandler? = null
    private var fileName: String = "log_all_intents.txt"


    /**
     * This method is called when the BroadcastReceiver is receiving an Intent broadcast.
     * @param context: [Context]
     *
     * @param intent: [Intent]
     *
     */
    override fun onReceive(context: Context?, intent: Intent?) {
        GlobalScope.launch(Dispatchers.IO) {
            runBlocking {
                intent?.putExtra(TRIGGER_TIMESTAMP, System.currentTimeMillis())
                storeIntentDataIntoAFile(intent)
                nr5GHandler?.onHandleIntent(intent, System.currentTimeMillis())
            }
        }
    }

    /**
     * This method sets the listener to handle callback mechanism
     */
    fun setListener(nr5GIntentHandler: INr5gIntentHandler) {
        nr5GHandler = nr5GIntentHandler
    }

    /**
     * This method is used to store intent data into a file
     * @param intent: [Intent]
     *
     */
    private fun storeIntentDataIntoAFile(intent: Intent?) {
        if (Constants.SAVE_DATA_TO_FILE) {
            FileUtils.saveFileToExternalStorage(
                DevLogUtils.getIntentData(
                    intent
                ), fileName, true
            )
        }
    }
}
