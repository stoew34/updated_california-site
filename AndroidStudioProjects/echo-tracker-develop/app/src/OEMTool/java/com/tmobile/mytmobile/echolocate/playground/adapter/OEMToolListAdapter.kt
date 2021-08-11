package com.tmobile.mytmobile.echolocate.playground.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.Toast
import com.tmobile.mytmobile.echolocate.R
import com.tmobile.mytmobile.echolocate.playground.OEMToolEnum
import com.tmobile.mytmobile.echolocate.playground.activities.*

/**
 * Adapter class to displays all the modules used in the echolocate app as grid
 */
class OEMToolListAdapter internal constructor(
    private val mContext: Context,
    private val modules: Array<OEMToolEnum>
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
        buttonText.setOnClickListener {
            when (position) {
                OEMToolEnum.SA5G_DATAMETRICS.ordinal -> {
                    val intent = Intent(mContext, OEMToolSa5gDataMetricsActivity::class.java)
                    mContext.startActivity(intent)
                }
                OEMToolEnum.NSA_5G_DATAMETRICS.ordinal -> {
                    val intent = Intent(mContext, NSA5GDataMetricsActivity::class.java)
                    mContext.startActivity(intent)
                }
                OEMToolEnum.LTE_DATAMETRICS.ordinal -> {
                    val intent = Intent(mContext, LTEDataMetricsActivity::class.java)
                    mContext.startActivity(intent)
                }
                OEMToolEnum.VOICEPOC.ordinal -> {
                    val intent = Intent(mContext, OEMToolVoiceActivity::class.java)
                    mContext.startActivity(intent)
                }
                OEMToolEnum.ADBCOMMANDS.ordinal -> {
                    //Yet to implement ADBCOMMAND Activity
                    Toast.makeText(mContext, "Yet to implement", Toast.LENGTH_SHORT).show()
                }
                OEMToolEnum.ANDROID_PUBLIC_API.ordinal -> {
                    val intent = Intent(mContext, TestPublicAPIActivity::class.java)
                    mContext.startActivity(intent)
                }
                OEMToolEnum.LOCATION.ordinal -> {
                    val intent = Intent(mContext, LocationActivity::class.java)
                    mContext.startActivity(intent)
                }
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

