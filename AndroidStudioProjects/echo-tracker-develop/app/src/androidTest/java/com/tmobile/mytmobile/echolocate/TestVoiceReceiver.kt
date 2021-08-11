package com.tmobile.mytmobile.echolocate

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.tmobile.mytmobile.echolocate.voice.intentlisteners.BaseVoiceBroadcastReceiver

class TestVoiceReceiver : BroadcastReceiver() {
    var baseVoiceBroadcastReceiver = BaseVoiceBroadcastReceiver()


    override fun onReceive(context: Context?, intent: Intent?) {
        baseVoiceBroadcastReceiver.onReceive(context,intent)
    }

}