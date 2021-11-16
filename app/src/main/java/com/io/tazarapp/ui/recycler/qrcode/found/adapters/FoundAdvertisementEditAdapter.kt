package com.io.tazarapp.ui.recycler.qrcode.found.adapters

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.io.tazarapp.R
import com.io.tazarapp.data.model.advertisement.SubcategoryAd
import com.io.tazarapp.utils.afterTxtChanged
import kotlinx.android.synthetic.main.item_raw_materials.view.*

class FoundAdvertisementEditAdapter : RecyclerView.Adapter<FoundAdvertisementEditAdapter.FoundAdvertisementEditViewHolder>() {
    private var models = ArrayList<SubcategoryAd>()
    var verifyItems: () -> Unit = { }
    var removeItem: (Int) -> Unit = { }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = FoundAdvertisementEditViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_raw_materials, parent, false)
            )

    override fun onBindViewHolder(holder:FoundAdvertisementEditViewHolder, position: Int) = holder.bind(position)

    override fun getItemCount() = models.size

    fun submitList(newModels: ArrayList<SubcategoryAd>) {
        models = newModels
        notifyDataSetChanged()
    }

    inner class FoundAdvertisementEditViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(position: Int) {
            itemView.apply {
                txt_title.text = models[position].name

                models[position].corrected_weight = models[position].value
                edit_weight.setText(models[position].corrected_weight.toString())

                edit_weight.afterTxtChanged {
                    models[position].corrected_weight = it.toIntOrNull() ?: 0
                    verifyItems.invoke()
                }

                btn_delete.setOnClickListener {
                    removeItem.invoke(models[position].id)
                    models.remove(models[position])
                    notifyItemRemoved(adapterPosition)
                }
            }
        }
    }
}