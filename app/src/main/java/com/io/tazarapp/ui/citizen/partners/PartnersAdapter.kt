package com.io.tazarapp.ui.citizen.partners

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.io.tazarapp.R
import com.io.tazarapp.data.model.partners.PartnersModel
import com.io.tazarapp.data.model.rating.Makala
import com.io.tazarapp.utils.setTextHTML


class PartnersAdapter(private val function: (PartnersModel) -> Unit) :
    RecyclerView.Adapter<PartnersAdapter.MainItemViewHolder>() {
    private var category: List<PartnersModel> = mutableListOf()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view: View = inflater.inflate(R.layout.item_main_items, parent, false)
        return MainItemViewHolder(
            view,
            function
        )
    }


    fun update(list: ArrayList<PartnersModel>) {
        this.category = list
        notifyDataSetChanged()

    }

    override fun getItemCount(): Int {
        return category.size
    }

    override fun onBindViewHolder(holder: MainItemViewHolder, position: Int) {
        holder.bind(category[position])
    }


    class MainItemViewHolder(itemView: View, val function: (PartnersModel) -> Unit) :
        RecyclerView.ViewHolder(itemView) {

        val title: TextView = itemView.findViewById(R.id.title_items)
        val desc: TextView = itemView.findViewById(R.id.desc_items)
        val image: ImageView = itemView.findViewById(R.id.image_items)


        fun bind(item: PartnersModel) {
            Glide.with(itemView).load(item.logo).circleCrop().into(image)
            title.text = item.name
            desc.text = setTextHTML(item.summary)

            itemView.setOnClickListener {
                function(item)
            }
        }
    }
}