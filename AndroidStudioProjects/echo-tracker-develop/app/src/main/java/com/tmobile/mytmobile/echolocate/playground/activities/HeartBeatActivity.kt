package com.tmobile.mytmobile.echolocate.playground.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.tmobile.echolocate.heartbeatsdk.heartbeat.HeartBeatSdk
import com.tmobile.echolocate.heartbeatsdk.heartbeat.triggers.HeartBeatTriggerManager.Force
import com.tmobile.mytmobile.echolocate.R

class HeartBeatActivity : AppCompatActivity() {
    private lateinit var heartBeatTV: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_heartbeat)
        heartBeatTV = findViewById(R.id.heartBeatTV)
    }

    fun onHistoryClicked(@Suppress("UNUSED_PARAMETER")view: View) {
        val intent = Intent(this, HeartBeatHistoryActivity::class.java)
        startActivity(intent)
    }
    fun onGenerateClicked(@Suppress("UNUSED_PARAMETER")view: View) {
        try {
            heartBeatTV.text = HeartBeatSdk.getInstance(this).generateHeartBeatDataForTrigger(Force)
        } catch (e: IllegalStateException) {
            Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
        }

    }

}