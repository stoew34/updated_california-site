package com.tmobile.mytmobile.echolocate.playground.activities

import android.os.Bundle
import android.os.Environment
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.tmobile.mytmobile.echolocate.EchoLocateApplication
import com.tmobile.mytmobile.echolocate.R
import com.tmobile.mytmobile.echolocate.playground.viewmodel.VoiceToolViewModel
import com.tmobile.mytmobile.echolocate.utils.ELDeviceUtils
import java.io.File

class OEMToolVoiceActivity : AppCompatActivity() {

    private lateinit var tvSteps: TextView
    private lateinit var tvStep_1: TextView
    private lateinit var tvStep_2: TextView
    private val SLASH = "/"
    private val debugFolderName: String = "dia_debug"
    private val viewModel by lazy {
        ViewModelProviders.of(this).get(VoiceToolViewModel::class.java)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_poc_tools_voice)
        tvSteps = findViewById(R.id.tv_steps)
        tvStep_1 = findViewById(R.id.tv_step1)
        tvStep_2 = findViewById(R.id.tv_step2)
        tvStep_2.text = resources.getString(R.string.step2,getVoiceLogIntentFilePath())

    }

    private fun getVoiceLogIntentFilePath(): String {
        val filePathAndName = "log_voice_intents.txt"
        return File(EchoLocateApplication.getContext()?.externalCacheDir,
                debugFolderName + SLASH + filePathAndName).absolutePath
    }
}
