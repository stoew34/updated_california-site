package com.tmobile.mytmobile.echolocate.playground.activities

import android.os.Bundle
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.tmobile.echolocate.heartbeatsdk.heartbeat.database.HeartBeatDB
import com.tmobile.heartbeatapp.adapters.HeartBeatHistoryAdapter
import com.tmobile.mytmobile.echolocate.R

class HeartBeatHistoryActivity : AppCompatActivity() {
    private lateinit var historyLV: ListView
    private lateinit var heartBeatHistoryAdapter: HeartBeatHistoryAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_heartbeat_history)
        historyLV = findViewById(R.id.historyLV)
        heartBeatHistoryAdapter = HeartBeatHistoryAdapter(this)
        historyLV.adapter = heartBeatHistoryAdapter
        HeartBeatDB.getDatabase(this).heartbeatDao()?.allEvents?.observe(this,
            Observer{ heartbeatPojos -> heartBeatHistoryAdapter.setHeartbeatPojoList(heartbeatPojos)})
    }

}