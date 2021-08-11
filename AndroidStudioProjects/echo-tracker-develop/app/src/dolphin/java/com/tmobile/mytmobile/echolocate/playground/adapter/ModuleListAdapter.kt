package com.tmobile.mytmobile.echolocate.playground.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.tmobile.mytmobile.echolocate.R
import com.tmobile.mytmobile.echolocate.playground.ModulesEnum
import com.tmobile.mytmobile.echolocate.playground.activities.*

/**
 * Adapter class to displays all the modules used in the echolocate app as grid
 */
class ModuleListAdapter internal constructor(
    private val mContext: Context,
    private val modules: Array<ModulesEnum>
) : BaseAdapter() {

    override fun getCount(): Int {
        return modules.size
    }

    override fun getItem(position: Int): Any? {
        return modules.get(position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = mContext
            .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val gridView: View
        val vh: ListRowHolder
        if (convertView == null) {
            gridView = inflater.inflate(R.layout.layout_modules_grid, parent, false)
            vh = ListRowHolder(gridView)
            gridView.tag = vh
        } else {
            gridView = convertView
            vh = gridView.tag as ListRowHolder
        }
        vh.label.text = modules[position].key
        val buttonText = gridView.findViewById<View>(R.id.txt_module) as Button
        var intent: Intent? = null
        buttonText.setOnClickListener {
            intent = when (position) {
                ModulesEnum.VOICE.ordinal ->            Intent(mContext, VoiceActivity::class.java)
                ModulesEnum.LTE.ordinal ->              Intent(mContext, LteActivity::class.java)
                ModulesEnum.NR5G.ordinal ->             Intent(mContext, Nr5gActivity::class.java)
                ModulesEnum.REPORTS.ordinal ->          Intent(mContext, ReportActivity::class.java)
                ModulesEnum.ANALYTICS.ordinal ->        Intent(mContext, AnalyticsActivity::class.java)
                ModulesEnum.COVERAGE.ordinal ->         Intent(mContext, CoverageActivity::class.java)
                ModulesEnum.AUTH_TOKENS.ordinal ->      Intent(mContext, AuthActivity::class.java)
                ModulesEnum.USER_CONSENT.ordinal ->     Intent(mContext, UserConsentActivity::class.java)
                ModulesEnum.LOCATION.ordinal ->         Intent(mContext, LocationActivity::class.java)
                ModulesEnum.CONFIGURATION.ordinal ->    Intent(mContext, ConfigurationActivity::class.java)
                ModulesEnum.HEARTBEAT.ordinal ->        Intent(mContext, HeartBeatActivity::class.java)
                ModulesEnum.AUTOUPDATE.ordinal ->       Intent(mContext, AutoUpdateActivity::class.java)
                ModulesEnum.CRASHTEST.ordinal ->        Intent(mContext, CrashTestActivity::class.java)
                ModulesEnum.COPYAPPDATA.ordinal ->      Intent(mContext, CopyAppDataActivity::class.java)
                ModulesEnum.DEVICEINFO.ordinal ->       Intent(mContext, DeviceInfoActivity::class.java)
                ModulesEnum.OSS_LICENSES.ordinal ->     Intent(mContext, OssLicensesMenuActivity::class.java)
                ModulesEnum.FLAVOR_CONFIG.ordinal ->    Intent(mContext, FlavorConfigActivity::class.java)
                ModulesEnum.DOLPHIN_UI.ordinal ->       Intent(mContext, DolphinHomeActivity::class.java)
                else -> null
            }

            intent?.let { mIntent ->
                mContext.startActivity(mIntent)
            }
        }

        return gridView
    }

    /*
    This class is responsible to hold the view references
     */
    private class ListRowHolder(row: View?) {
        val label: Button = row?.findViewById(R.id.txt_module) as Button

    }
}

