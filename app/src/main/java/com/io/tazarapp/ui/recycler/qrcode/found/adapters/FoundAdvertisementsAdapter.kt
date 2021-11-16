package com.io.tazarapp.ui.recycler.qrcode.found.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.io.tazarapp.R
import com.io.tazarapp.data.model.advertisement.user_advertisement.Advertisement
import com.io.tazarapp.utils.SubCategoryAdFlowLayoutAdapter
import kotlinx.android.synthetic.main.found_dvertisements_item.view.*

class FoundAdvertisementsAdapter(private val secondStateUI: (Advertisement) -> Unit) :
    RecyclerView.Adapter<FoundAdvertisementsAdapter.FoundAdvertisementsViewHolder>() {
    private var models = ArrayList<Advertisement>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        FoundAdvertisementsViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.found_dvertisements_item, parent, false)
        )

    override fun onBindViewHolder(holder: FoundAdvertisementsViewHolder, position: Int) =
        holder.bind(models[position])

    override fun getItemCount() = models.size

    fun submitList(newModels: ArrayList<Advertisement>) {
        models = newModels
        notifyDataSetChanged()
    }

    inner class FoundAdvertisementsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: Advertisement) {
            itemView.apply {
                found_adapter_firm.text = item.name
                if (item.status == 1) {
                    found_adapter_status.text = resources.getString(R.string.newedit)
                    found_adapter_status.setTextColor(ContextCompat.getColor(context,R.color.colorAccent_8CC341))
                } else {
                    found_adapter_status.text = resources.getString(R.string.sent)
                    found_adapter_status.setTextColor(ContextCompat.getColor(context,R.color.color_red_ea565e))
                }
                if (!item.ads_map.isNullOrEmpty()) {
                    found_adapter_address.text = item.ads_map[0].address
                } else {
                    found_adapter_address.text = ""
                }
                found_subcategory_flow_layout.adapter = SubCategoryAdFlowLayoutAdapter(
                    item.subcategory_ads,
                    found_subcategory_flow_layout,
                    1
                )

                setOnClickListener { secondStateUI(item) }
            }
        }
    }
}