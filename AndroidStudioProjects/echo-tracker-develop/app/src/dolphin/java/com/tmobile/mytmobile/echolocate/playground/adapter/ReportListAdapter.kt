package com.tmobile.mytmobile.echolocate.playground.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tmobile.mytmobile.echolocate.R
import com.tmobile.mytmobile.echolocate.reporting.database.entities.ReportSenderEntity
import kotlinx.android.synthetic.dolphin.card_report_queue.view.*

class ReportListAdapter : RecyclerView.Adapter<ReportListAdapter.ReportListViewHolder>() {

    private var mReportList: List<ReportSenderEntity> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportListViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.card_report_queue, parent, false)
        return ReportListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReportListViewHolder, position: Int) {
        mReportList[position].let { mReport ->
            holder.bindReport(mReport)
        }
    }

    override fun getItemCount(): Int {
        return mReportList.size
    }

    fun refreshData(reportList: List<ReportSenderEntity>) {
        mReportList = reportList
        notifyDataSetChanged()
    }


    class ReportListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvFileName = itemView.tv_file_link
        var tvFileStatus = itemView.tv_file_status
        var tvHttpError = itemView.tv_http_error

        fun bindReport (reportSenderEntity: ReportSenderEntity) {
            this.tvFileName.text = reportSenderEntity.fileName
            this.tvFileStatus.text = reportSenderEntity.status
            if (reportSenderEntity.httpError != null)
                this.tvHttpError.text = reportSenderEntity.httpError?.take(50)
        }
    }
}

