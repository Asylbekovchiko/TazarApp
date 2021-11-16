package com.io.tazarapp.ui.citizen.map.detail

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.io.tazarapp.R
import com.io.tazarapp.data.model.advertisement.SubcategoryAd
import com.io.tazarapp.utils.gone
import com.io.tazarapp.utils.visible
import kotlinx.android.synthetic.main.item_rv_category.view.*

class RVCategoryDetailAdapter : RecyclerView.Adapter<RVCategoryDetailAdapter.ShortCollectionPlacesHolder>(){
    private var places = ArrayList<SubcategoryAd>()

    fun swapData(ad: ArrayList<SubcategoryAd>){
        places = ad
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ShortCollectionPlacesHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_rv_category,
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: ShortCollectionPlacesHolder, position: Int) = holder.bind(places[position])


    override fun getItemCount() = places.size

    class ShortCollectionPlacesHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        fun bind(place: SubcategoryAd) {
            itemView.apply {
                title.text = place.name

                Glide.with(context).load(place.inner_image).into(image)
                if (place.value==0){
                    quantity.gone()
                }else {
                    quantity.visible()
                    quantity.text = place.value.toString() + " кг"
                }

            }
        }
    }
}