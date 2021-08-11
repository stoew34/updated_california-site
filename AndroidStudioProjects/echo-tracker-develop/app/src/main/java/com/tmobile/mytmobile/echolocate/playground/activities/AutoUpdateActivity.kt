package com.tmobile.mytmobile.echolocate.playground.activities

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.tmobile.echolocate.autoupdate.UpdateEvent
import com.tmobile.mytmobile.echolocate.R
import com.tmobile.mytmobile.echolocate.autoupdate.AutoUpdateManager
import com.tmobile.mytmobile.echolocate.autoupdate.OnUpdateChangedListener

class AutoUpdateActivity : AppCompatActivity(), OnUpdateChangedListener {

    private lateinit var autoUpdateProgress: ProgressBar
    private lateinit var autoUpdateBtn: Button
    private lateinit var autoupdateTv: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_autoupdate)
        autoUpdateBtn = findViewById(R.id.autoUpdateBtn)
        autoUpdateProgress = findViewById(R.id.autoUpdateProgress)
        autoupdateTv = findViewById(R.id.autoupdateTv)
        setAutoUpdateBtnStatus(AutoUpdateManager.getInstance().updateStatus)

    }

    override fun onResume() {
        super.onResume()
        AutoUpdateManager.getInstance().onUpdateChangedListener = this

    }

    override fun onPause() {
        AutoUpdateManager.getInstance().onUpdateChangedListener = null
        super.onPause()
    }

    fun onUpdateClicked(@Suppress("UNUSED_PARAMETER")view: View) {
        setAutoUpdateBtnStatus(AutoUpdateManager.getInstance().updateStatus)
        val action = AutoUpdateManager.getInstance().updateStatus
        when (action) {
            UpdateEvent.Action.INSTALL_READY -> AutoUpdateManager.getInstance().installUpdate()
            UpdateEvent.Action.READY -> AutoUpdateManager.getInstance().updateIfAvailable()
            else -> {
                AutoUpdateManager.getInstance().cancelUpdate()
                autoUpdateProgress.progress = 0
            }
        }

    }

    private fun setAutoUpdateBtnStatus(action: UpdateEvent.Action?) {
        val updateAvailable = AutoUpdateManager.getInstance().isUpdateAvailable
        autoupdateTv.visibility = if (updateAvailable) View.GONE else View.VISIBLE
        autoUpdateBtn.visibility = if (updateAvailable) View.VISIBLE else View.GONE
        autoUpdateProgress.visibility = View.GONE

        when (action) {
            UpdateEvent.Action.DOWNLOADING, UpdateEvent.Action.DOWNLOADING_PROGRESS -> {
                autoUpdateBtn.text = "Cancel"
                autoUpdateProgress.visibility = View.VISIBLE
            }
            UpdateEvent.Action.READY -> autoUpdateBtn.text = "Download"
            UpdateEvent.Action.INSTALL_READY -> autoUpdateBtn.text = "Install"
            else -> {
                autoUpdateBtn.text = "Resume"
            }
        }

    }

    private fun countProgress(updateEvent: UpdateEvent): Int {
        val totalBytes = updateEvent.bundle.getLong(UpdateEvent.EXTRA_PROGRESS_TOTAL_BYTES)
        val downloadedBytes =
            updateEvent.bundle.getLong(UpdateEvent.EXTRA_PROGRESS_DOWNLOADED_BYTES)
        return (downloadedBytes.toDouble() / totalBytes * 100).toInt()
    }

    override fun onUpdateChanged(updateEvent: UpdateEvent) {
        runOnUiThread {
            setAutoUpdateBtnStatus(updateEvent.action)
            if (UpdateEvent.Action.DOWNLOADING_PROGRESS == updateEvent.action) {
                val progress = countProgress(updateEvent)
                autoUpdateProgress.progress = progress
            } else if (UpdateEvent.Action.FAILED == updateEvent.action) {
                autoUpdateProgress.progress = 0

            }
        }

    }

}