package com.io.tazarapp.ui.citizen.news.adapter

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.io.tazarapp.R

class NewsViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
    val iv_image = itemView.findViewById<ImageView>(R.id.iv_news)
    val tv_title = itemView.findViewById<TextView>(R.id.tv_title)
    val tv_date = itemView.findViewById<TextView>(R.id.tv_date)
}