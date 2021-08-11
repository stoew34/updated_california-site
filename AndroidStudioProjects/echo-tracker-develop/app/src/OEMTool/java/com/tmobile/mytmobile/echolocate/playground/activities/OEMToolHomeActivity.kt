package com.tmobile.mytmobile.echolocate.playground.activities

import android.content.Intent
import android.os.Bundle
import android.widget.GridView
import androidx.appcompat.app.AppCompatActivity
import com.tmobile.mytmobile.echolocate.R
import com.tmobile.mytmobile.echolocate.playground.OEMToolEnum
import com.tmobile.mytmobile.echolocate.playground.adapter.OEMToolListAdapter
import com.tmobile.mytmobile.echolocate.utils.EchoLocateLog


class OEMToolHomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_poc_tools_main_menu)

        val pocToolsGridView = findViewById<GridView>(R.id.poc_tools_grid_view)

        val adbIntent = intent

        if (adbIntent.hasExtra(TEST_ACTION) && adbIntent.getStringExtra(TEST_ACTION) == GENERATE_5G_SA) {
            val intent = Intent(this, OEMToolSa5gDataMetricsActivity::class.java)
            intent.putExtra(TEST_PARAM, GENERATE_5G_SA_DATAMETRICS_REPORT)
            EchoLocateLog.eLogD("OEMToolHomeActivity to OEMToolSa5gDataMetricsActivity via adb")
            startActivity(intent)
        } else if (adbIntent.hasExtra(TEST_ACTION) && adbIntent.getStringExtra(TEST_ACTION) == GENERATE_LTE) {
            val intent = Intent(this, LTEDataMetricsActivity::class.java)
            intent.putExtra(TEST_PARAM, GENERATE_LTE_DATAMETRICS_REPORT)
            EchoLocateLog.eLogD("OEMToolHomeActivity to LTEDataMetricsActivity via adb")
            startActivity(intent)
        } else if (adbIntent.hasExtra(TEST_ACTION) && adbIntent.getStringExtra(TEST_ACTION) == GENERATE_NSA5G) {
            val intent = Intent(this, NSA5GDataMetricsActivity::class.java)
            intent.putExtra(TEST_PARAM, GENERATE_NSA5G_DATAMETRICS_REPORT)
            EchoLocateLog.eLogD("OEMToolHomeActivity to NSA5GDataMetricsActivity via adb")
            startActivity(intent)
        }

        val adapter = OEMToolListAdapter(this, OEMToolEnum.values())
        pocToolsGridView.adapter = adapter
    }

    companion object {
        const val TEST_ACTION = "TEST_ACTION"
        const val GENERATE_5G_SA = "5G_SA"
        const val TEST_PARAM = "TEST_PARAM"
        const val GENERATE_5G_SA_DATAMETRICS_REPORT = "5G_SA_REPORT"
        const val GENERATE_LTE = "LTE"
        const val GENERATE_LTE_DATAMETRICS_REPORT = "LTE_REPORT"
        const val GENERATE_NSA5G = "NSA5G"
        const val GENERATE_NSA5G_DATAMETRICS_REPORT = "NSA5G_REPORT"
    }
}
