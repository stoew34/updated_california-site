package com.tmobile.mytmobile.echolocate.playground.activities

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.lifecycle.ViewModelProviders
import com.tmobile.mytmobile.echolocate.R
import com.tmobile.mytmobile.echolocate.playground.viewmodel.AdbCommandsToolViewModel

class OEMToolAdbCommands : AppCompatActivity() {

    private lateinit var btnAdbCmd: AppCompatButton

    private val viewModel by lazy {
        ViewModelProviders.of(this).get(AdbCommandsToolViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_poc_tools_adb_commands)
        initViews()
    }

    /**
     * Initialization section of views used in the activity
     */
    @SuppressLint("ClickableViewAccessibility")
    private fun initViews() {
        btnAdbCmd = findViewById(R.id.clear_btn)

    }

}
