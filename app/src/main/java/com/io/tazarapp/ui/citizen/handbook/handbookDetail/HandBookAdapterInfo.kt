package com.io.tazarapp.ui.citizen.handbook.handbookDetail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.io.tazarapp.R
import com.io.tazarapp.data.model.citizen_info.HandbookInfoModel

class HandBookAdapterInfo(private val function: (HandbookInfoModel) -> Unit) :
    RecyclerView.Adapter<HandBookAdapterInfo.ViewHolder>() {
    private var data: MutableList<HandbookInfoModel> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view: View = inflater.inflate(R.layout.rv_item_handbook_info, parent, false)
        return ViewHolder(view, function)
    }

    fun update(list: ArrayList<HandbookInfoModel>) {
        this.data = list
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position])
    }

    class ViewHolder(itemView: View, val function: (HandbookInfoModel) -> Unit) :
        RecyclerView.ViewHolder(itemView) {

        val title: TextView = itemView.findViewById(R.id.tv_item_handbook)
        val image: ImageView = itemView.findViewById(R.id.img_item_handbook)

        fun bind(item: HandbookInfoModel) {
            title.text = item.title
            Glide.with(itemView).load(item.image).into(image)

            itemView.setOnClickListener {
                function(item)
            }
        }

    }
}