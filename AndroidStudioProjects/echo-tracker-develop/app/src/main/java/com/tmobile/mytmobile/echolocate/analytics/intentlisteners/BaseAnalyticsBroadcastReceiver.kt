package com.tmobile.mytmobile.echolocate.analytics.intentlisteners

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.tmobile.mytmobile.echolocate.BuildConfig
import com.tmobile.mytmobile.echolocate.analytics.utils.AnalyticsSharedPreference
import com.tmobile.mytmobile.echolocate.utils.DevLogUtils
import com.tmobile.mytmobile.echolocate.utils.EchoLocateLog
import com.tmobile.mytmobile.echolocate.utils.FileUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class BaseAnalyticsBroadcastReceiver: BroadcastReceiver() {
    private var fileName: String = "log_all_intents.txt"

    override fun onReceive(context: Context?, intent: Intent?) {
        EchoLocateLog.eLogD("Echolocate Analytics module Diagnostic : Analytics Boot complete Intent received")
        GlobalScope.launch(Dispatchers.IO) {
            runBlocking {
                storeIntentDataIntoAFile(intent)
                if(context != null) {
                    AnalyticsSharedPreference.init(context)
                    AnalyticsSharedPreference.numOfReboots =
                        AnalyticsSharedPreference.numOfReboots + 1
                    AnalyticsSharedPreference.numOfCrashesAtReboot =
                        AnalyticsSharedPreference.numOfCrashes
                    AnalyticsSharedPreference.numOfAnrsAtReboot =
                        AnalyticsSharedPreference.numOfAnrs
                    EchoLocateLog.eLogD("Echolocate Analytics module Diagnostic Reboots:"+AnalyticsSharedPreference.numOfReboots)
                }
            }
        }
    }

    /**
     *  method/function writes intent data to external storage file "dia_debug"
     */
    private fun storeIntentDataIntoAFile(intent: Intent?) {
        if (BuildConfig.DEBUG) {
            FileUtils.saveFileToExternalStorage(
                DevLogUtils.getIntentData(
                    intent
                ), fileName, true
            )
        }
    }
}