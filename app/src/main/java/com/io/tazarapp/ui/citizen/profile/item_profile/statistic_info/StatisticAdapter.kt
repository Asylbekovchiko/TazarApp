package com.io.tazarapp.ui.citizen.profile.item_profile.statistic_info

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.io.tazarapp.R
import com.io.tazarapp.data.model.statistics.StatisticData

class StatisticAdapter :
    RecyclerView.Adapter<StatisticAdapter.StatisticViewHolder>() {
    private var category: List<StatisticData> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StatisticViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view: View = inflater.inflate(R.layout.item_statistic, parent, false)
        return StatisticViewHolder(
            view
        )
    }

    fun update(list: ArrayList<StatisticData>) {
        this.category = list
        notifyDataSetChanged()

    }

    override fun getItemCount(): Int {
        return category.size
    }

    override fun onBindViewHolder(holder: StatisticViewHolder, position: Int) {
        holder.bind(category[position])
    }

    class StatisticViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        val title: TextView = itemView.findViewById(R.id.tv_title_statistic)
        val weight: TextView = itemView.findViewById(R.id.tv_weight_statistic)
        val image: ImageView = itemView.findViewById(R.id.img_circle_statistic)

        fun bind(item: StatisticData) {
            title.text = item.name
            weight.text = "${item.total}" + itemView.resources.getString(R.string.kg)
            image.backgroundTintList = ColorStateList.valueOf(Color.parseColor(item.color))
        }
    }
}