package com.tmobile.mytmobile.echolocate.playground.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.tmobile.mytmobile.echolocate.R
import com.tmobile.mytmobile.echolocate.playground.PublicApisEnum

class PublicAPIListAdapter internal constructor(
    private val mContext: Context,
    private val publicApis: Array<PublicApisEnum>,
    private val mAPISelectedListener: ApiSelectedListener
) : BaseAdapter() {

    override fun getCount(): Int {
        return publicApis.size
    }

    override fun getItem(position: Int): Any? {
        return publicApis.get(position)
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
            gridView = inflater.inflate(R.layout.layout_public_apis_grid, parent,
                false)
            vh = ListRowHolder(gridView)
            gridView.tag = vh
        } else {
            gridView = convertView
            vh = gridView.tag as ListRowHolder
        }
        vh.label.text = publicApis[position].key
        val buttonText = gridView.findViewById<View>(R.id.txt_api) as Button
        buttonText.setOnClickListener {
            mAPISelectedListener.onAPIClicked(publicApis[position])
        }
        return gridView
    }

    interface ApiSelectedListener {
        fun onAPIClicked(api: PublicApisEnum)
    }

    /*
   This class is responsible to hold the view references
    */
    private class ListRowHolder(row: View?) {
        val label: Button = row?.findViewById(R.id.txt_api) as Button
    }

}