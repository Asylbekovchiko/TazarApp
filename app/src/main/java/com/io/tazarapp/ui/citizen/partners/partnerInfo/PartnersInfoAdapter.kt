package com.io.tazarapp.ui.citizen.partners.partnerInfo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.io.tazarapp.R
import com.io.tazarapp.data.model.partners.PartnersDetPrizeModel

class PartnersInfoAdapter :
    RecyclerView.Adapter<PartnersInfoAdapter.MainItemViewHolder>() {
    private var category: List<PartnersDetPrizeModel> = mutableListOf()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view: View = inflater.inflate(R.layout.rv_item_partners_info, parent, false)
        return MainItemViewHolder(
            view
        )
    }

    override fun onBindViewHolder(holder: MainItemViewHolder, position: Int) {
        holder.bind(category[position])
    }

    fun update(list: ArrayList<PartnersDetPrizeModel>) {
        this.category = list
        notifyDataSetChanged()

    }

    override fun getItemCount(): Int {
        return category.size
    }

    class MainItemViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        val title: TextView = itemView.findViewById(R.id.tv_points_item_partner_info)
        val image: ImageView = itemView.findViewById(R.id.img_item_partners_info)

        fun bind(item: PartnersDetPrizeModel) {
            Glide.with(itemView).load(item.image).circleCrop().into(image)
            title.text = "${item.price}" + itemView.resources.getString(R.string.p)
        }
    }


}