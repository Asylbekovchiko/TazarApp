package com.io.tazarapp.ui.citizen.news.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.io.tazarapp.R
import com.io.tazarapp.data.model.news.NewsModel
import com.io.tazarapp.utils.gone
import com.io.tazarapp.utils.visible

class NewsAdapter : RecyclerView.Adapter<NewsViewHolder>(){
    private var items = ArrayList<NewsModel>()
    var onItemClick : (id : Int) -> Unit = { }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = NewsViewHolder (
        LayoutInflater.from(parent.context).inflate(R.layout.item_news, parent, false)
    )

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        holder.apply {
            val currentItem = items[position]

            if (currentItem.image != null) {
                iv_image.visible()
                Glide.with(itemView.context)
                        .load(currentItem.image)
                        .into(iv_image)
            } else {
                iv_image.gone()
            }

            tv_title.text = currentItem.title
            tv_date.text = currentItem.created_at

            itemView.setOnClickListener { onItemClick.invoke(currentItem.id) }
        }
    }

    fun update(newItems: ArrayList<NewsModel>) {
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    fun clear() {
        items.clear()
    }
}