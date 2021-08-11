package com.tmobile.mytmobile.echolocate.playground.activities

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.tmobile.mytmobile.echolocate.R

class CrashTestActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crash_test)
    }
    fun onCrashButtonClicked(@Suppress("UNUSED_PARAMETER") view: View) {
        throw Exception("Clicked on Test Crash button")
    }
}