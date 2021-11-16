package com.io.tazarapp.ui.citizen.handbook

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.io.tazarapp.R
import com.io.tazarapp.data.model.citizen_info.HandbookModel

class HandBookAdapter(private val function: (HandbookModel) -> Unit) :
    RecyclerView.Adapter<HandBookAdapter.HistoryViewHolder>() {
    private var data: MutableList<HandbookModel> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view: View = inflater.inflate(R.layout.rv_item_handbook, parent, false)
        return HistoryViewHolder(view, function)
    }

    fun update(list: MutableList<HandbookModel>) {
        this.data = list
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.bind(data[position])
    }

    class HistoryViewHolder(itemView: View, val function: (HandbookModel) -> Unit) :
        RecyclerView.ViewHolder(itemView) {

        val title: TextView = itemView.findViewById(R.id.tv_title_handbook)

        fun bind(item: HandbookModel) {
            title.text = item.name
            itemView.setOnClickListener {
                function(item)
            }
        }
    }

}