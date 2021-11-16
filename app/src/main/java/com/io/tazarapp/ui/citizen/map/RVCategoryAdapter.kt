package com.io.tazarapp.ui.citizen.map

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.io.tazarapp.R
import com.io.tazarapp.data.model.filter.FilterSubcategoryModel
import kotlinx.android.synthetic.main.item_category.view.*

class RVCategoryAdapter(private val filterClick: () -> Unit) :
    RecyclerView.Adapter<RVCategoryAdapter.ShortCollectionPlacesHolder>() {
    private var places = ArrayList<FilterSubcategoryModel>()
    private var choosenCategory = ArrayList<FilterSubcategoryModel>()

    fun swapData(ad: ArrayList<FilterSubcategoryModel>) {
        places = ad
        notifyDataSetChanged()
    }

    fun setChoces(ad: ArrayList<FilterSubcategoryModel>) {
        choosenCategory = ad
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ShortCollectionPlacesHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_category,
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: ShortCollectionPlacesHolder, position: Int) =
        holder.bind(places[position],filterClick)


    override fun getItemCount() = places.size

    inner class ShortCollectionPlacesHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(
            place: FilterSubcategoryModel,
            filterClick: () -> Unit
        ) {
            itemView.apply {
                text.text = place.name
                var bool = false

                if (choosenCategory.size == 0 && place.id == -1) {
                    line.setBackgroundResource(R.drawable.rounded_shape_green_6dpt)
                    text.setTextColor(Color.WHITE)
                } else {
                    choosenCategory.forEachIndexed { index, it ->
                        if (it.id == place.id) {
                            bool = true
                            return@forEachIndexed
                        }
                    }
                    if (bool){
                        line.setBackgroundResource(R.drawable.rounded_shape_green_6dpt)
                        text.setTextColor(Color.WHITE)
                    }else{
                        line.setBackgroundResource(R.drawable.rounded_gray_6dp)
                        text.setTextColor(Color.BLACK)
                    }
                }

                setOnClickListener {
                    if (place.id == -1) {
                        choosenCategory.clear()
                    }
                    else {
                        if (bool) choosenCategory.remove(place)
                        else choosenCategory.add(place)
                    }
                    filterClick()
                }
            }
        }

    }
}