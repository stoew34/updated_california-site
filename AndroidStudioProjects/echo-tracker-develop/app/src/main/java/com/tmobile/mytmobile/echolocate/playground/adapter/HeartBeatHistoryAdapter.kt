package com.tmobile.heartbeatapp.adapters

import com.tmobile.echolocate.heartbeatsdk.heartbeat.model.HeartbeatPojo

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.tmobile.mytmobile.echolocate.R

/**
 * Adapter for listing HeartBeat History
 */
class HeartBeatHistoryAdapter : BaseAdapter {
    private var context: Context? = null
    private var heartbeatPojoList: List<HeartbeatPojo>? = null

    constructor(context: Context) {
        this.context = context

    }

    constructor(context: Context, heartbeatPojoList: List<HeartbeatPojo>) {
        this.context = context
        this.heartbeatPojoList = heartbeatPojoList
    }

    override fun getCount(): Int {
        return if (heartbeatPojoList == null) {
            0
        } else {
            heartbeatPojoList!!.size
        }
    }

    override fun getItem(position: Int): Any? {
        return null
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val itemView: View
        val vh: RowHolder
        val heartbeatPojo = heartbeatPojoList!![position]
        if (convertView == null) {
            itemView = LayoutInflater.from(context).inflate(R.layout.view_heartbeat_item, null)
            vh = RowHolder(itemView)
            itemView.tag = vh
        }else{
            itemView = convertView
            vh = itemView.tag as RowHolder
        }

        vh.id?.text = heartbeatPojo.id.toString()
        vh.status?.text = heartbeatPojo.sendSatus
        vh.trigger?.text = heartbeatPojo.syncTrigger
        vh.timestamp?.text = heartbeatPojo.timestamp

        return itemView
    }

    fun setHeartbeatPojoList(heartbeatPojoList: List<HeartbeatPojo>) {
        this.heartbeatPojoList = heartbeatPojoList
        notifyDataSetChanged()
    }
    private class RowHolder(row: View?) {
        val id = row?.findViewById<TextView>(R.id.id)
        val status = row?.findViewById<TextView>(R.id.status)
        val trigger = row?.findViewById<TextView>(R.id.trigger)
        val timestamp = row?.findViewById<TextView>(R.id.timestamp)

    }
}
