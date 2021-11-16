package com.io.tazarapp.ui.citizen.filter.view.parts

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.io.tazarapp.R
import com.io.tazarapp.data.model.filter.FilterSubcategoryModel
import kotlinx.android.synthetic.main.filter_card_item.view.*

class FilterPartsAdapter(
    private val showInfo : (part : FilterSubcategoryModel) -> Unit,
    private val check : (part : FilterSubcategoryModel) -> Boolean,
    private val add : (part : FilterSubcategoryModel) -> Unit,
    private val remove : (part : FilterSubcategoryModel) -> Unit
) : RecyclerView.Adapter<FilterPartsAdapter.PartsViewHolder>()  {

    var parts = ArrayList<FilterSubcategoryModel>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PartsViewHolder = PartsViewHolder(
        LayoutInflater.from(parent.context).inflate(
            R.layout.filter_card_item,
            parent,
            false
        )
    )

    override fun getItemCount() = parts.size

    override fun onBindViewHolder(holder: PartsViewHolder, position: Int)  = holder.bind(position)

    fun updateParts(newParts : ArrayList<FilterSubcategoryModel>) {
        parts = newParts
        notifyDataSetChanged()
    }

    inner class PartsViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        fun bind(position: Int) {
            itemView.apply {
                title.text = parts[position].name

                if(check(parts[position])) {
                    part_parent.setBackgroundResource(R.drawable.rounded_shape_green_12dp_8cc341)
                }else {
                    part_parent.setBackgroundResource(R.drawable.rounded_shape_white_12dp_ffffff)
                }

                Glide.with(context).load(parts[position].image).into(image)

                info.setOnClickListener { showInfo(parts[position]) }

                part_card.setOnClickListener {
                    if(check(parts[position])) {
                        part_parent.setBackgroundResource(R.drawable.rounded_shape_white_12dp_ffffff)
                        remove(parts[position])
                    }else {
                        part_parent.setBackgroundResource(R.drawable.rounded_shape_green_12dp_8cc341)
                        add(parts[position])
                    }
                }
            }
        }
    }
}