package com.io.tazarapp.ui.citizen.map

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.io.tazarapp.R
import com.io.tazarapp.data.model.advertisement.SubcategoryAd
import com.io.tazarapp.data.model.cp.CP
import com.io.tazarapp.ui.citizen.map.detail.AdDetailActivity
import com.io.tazarapp.utils.*
import kotlinx.android.synthetic.main.item_collection_place.view.*

class RVCitizenAdAdapter :
    RecyclerView.Adapter<RVCitizenAdAdapter.ShortCollectionPlacesHolder>() {
    private var places = ArrayList<CP>()

    fun swapData(ad: ArrayList<CP>) {
        places = ad
        notifyDataSetChanged()
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
        fun bind(place: CP) {
            itemView.apply {
                name.text = place.title
                info.text(place.description)
                address.text = place.cp_map[0].address
                var text = ""
                var textWeekend = ""

                place.cp_schedule.forEach {
                    if (!it.is_weekend){
                        text+= getDay(it.name,context)+", "
                    }else{
                        textWeekend+= getDay(it.name,context)+", "
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
                flow_layout.adapter = SubCategoryAdFlowLayoutAdapter(place.subcategory as ArrayList<SubcategoryAd>, flow_layout,0)

                worktime.text = days
                outlet.text = ends
                phone_number.text = maskPhoneNumb(place.contact)

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