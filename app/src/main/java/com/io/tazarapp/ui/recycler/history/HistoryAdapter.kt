package com.io.tazarapp.ui.recycler.history

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.io.tazarapp.R
import com.io.tazarapp.data.model.history.HistoryModel
import com.io.tazarapp.utils.SubCategoryAdFlowLayoutAdapter
import com.io.tazarapp.utils.reFormatDate
import kotlinx.android.synthetic.main.history_item.view.*

class HistoryAdapter : RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {
    private var items = ArrayList<HistoryModel>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = HistoryViewHolder(
        LayoutInflater.from(parent.context).inflate(
            R.layout.history_item,
            parent,
            false
        )
    )

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) = holder.bind(items[position])

    override fun getItemCount() = items.size

    fun updateData(newModel: ArrayList<HistoryModel>) {
        newModel.forEach { items.add(it) }
        notifyDataSetChanged()
    }

    fun removeAll() {
        items.clear()
        notifyDataSetChanged()
    }

    inner class HistoryViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        fun bind(historyModel: HistoryModel) {
            itemView.apply {
                history_item_datetime.text = reFormatDate(context,historyModel.date_created)
                history_item_title.text = historyModel.ads_name
                history_item_address.text = historyModel.address
                history_item_flow_layout.adapter = SubCategoryAdFlowLayoutAdapter(historyModel.subcategory, history_item_flow_layout,2)
            }
        }
    }
}