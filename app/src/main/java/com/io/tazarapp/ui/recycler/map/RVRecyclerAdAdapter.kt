package com.io.tazarapp.ui.recycler.map

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.io.tazarapp.R
import com.io.tazarapp.data.model.map.Ad
import com.io.tazarapp.ui.recycler.map.detail.AdDetailActivity
import com.io.tazarapp.utils.SubCategoryAdFlowLayoutAdapter
import com.io.tazarapp.utils.getDay
import com.io.tazarapp.utils.maskPhoneNumb
import kotlinx.android.synthetic.main.item_collection_place.view.*

class RVRecyclerAdAdapter :
    RecyclerView.Adapter<RVRecyclerAdAdapter.ShortCollectionPlacesHolder>() {
    private var places = ArrayList<Ad>()

    fun swapData(ad: ArrayList<Ad>) {
        places = ad
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ShortCollectionPlacesHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_collection_place,
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: ShortCollectionPlacesHolder, position: Int) =
        holder.bind(places[position])


    override fun getItemCount() = places.size

    class ShortCollectionPlacesHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(place: Ad) {
            itemView.apply {
                name.text = place.name
                info.text = place.description
                address.text = place.ads_map[0].address

                var text = ""
                var textWeekend = ""

                place.schedule.forEach {
                    if (!it.is_weekend){
                        text+= getDay(it.name,context) +", "
                    }else{
                        textWeekend+= getDay(it.name,context) +", "
                    }
                }
                var days = ""
                var ends = ""
                if (text.isNotEmpty()){
                    days += context.getString(R.string.work_days) + " " + text.substring(0,text.length-2)
                }
                if (textWeekend.isNotEmpty()){
                    ends += context.getString(R.string.wekend) + " " + textWeekend.substring(0,textWeekend.length-2)
                }


                worktime.text = days
                outlet.text = ends
                phone_number.text = maskPhoneNumb(place.phone)

                flow_layout.adapter = SubCategoryAdFlowLayoutAdapter(place.subcategory_ads, flow_layout,0)

                setOnClickListener {
                    context.startActivity(
                        Intent(
                            context,
                            AdDetailActivity::class.java
                        ).putExtra("id", place.id)
                    )
                }
            }
        }

    }
}