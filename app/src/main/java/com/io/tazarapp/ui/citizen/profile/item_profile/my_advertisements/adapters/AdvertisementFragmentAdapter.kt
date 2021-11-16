package com.io.tazarapp.ui.citizen.profile.item_profile.my_advertisements.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.io.tazarapp.R
import com.io.tazarapp.data.model.advertisement.my_advertisement.MyAdvertisementModel
import com.io.tazarapp.utils.SubCategoryAdFlowLayoutAdapter
import com.io.tazarapp.utils.gone
import com.io.tazarapp.utils.visible
import kotlinx.android.synthetic.main.my_edits_item.view.*

class AdvertisementFragmentAdapter(private val openBottomFragment: (Int) -> Unit) : RecyclerView.Adapter<AdvertisementFragmentAdapter.EditFragmentViewHolder>() {
    private var models = ArrayList<MyAdvertisementModel>()
    private val diff = RemoveDiffCallback()
    private lateinit var diffResult : DiffUtil.DiffResult

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = EditFragmentViewHolder(
        LayoutInflater.from(parent.context).inflate(
            R.layout.my_edits_item,
            parent,
            false
        )
    )

    override fun onBindViewHolder(holder: EditFragmentViewHolder, position: Int) = holder.bind(models[position])

    override fun getItemCount() = models.size

    inner class EditFragmentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        @SuppressLint("SetTextI18n")
        fun bind(model: MyAdvertisementModel) {
            itemView.apply {
                my_edits_item_firm_name.text = model.name
                if (!model.ads_map.isNullOrEmpty()){
                    my_edits_item_address.text = model.ads_map[0].address
                }else{
                    my_edits_item_address.text = ""
                }
                my_edits_item_flowlayout.adapter = SubCategoryAdFlowLayoutAdapter(model.subcategory_ads,my_edits_item_flowlayout,1)

                if (model.status == 1){
                    my_edits_item_status.text = resources.getString(R.string.newedit)
                    my_edits_item_status.setTextColor(ContextCompat.getColor(context,R.color.colorAccent_8CC341))
                    my_edits_item_view_line.gone()
                    my_edits_item_take.gone()
                    my_edits_item_take_value.gone()
                    my_edits_item_three_commas.visible()
                    my_edits_item_three_commas.setOnClickListener {
                        openBottomFragment(model.id)
                    }
                }else {
                    my_edits_item_status.text = resources.getString(R.string.sent)
                    my_edits_item_view_line.visible()
                    my_edits_item_status.setTextColor(ContextCompat.getColor(context,R.color.color_red_ea565e))
                    my_edits_item_take_value.text = "${model.points} " + context.resources.getString(R.string.Points)
                    my_edits_item_three_commas.gone()
                }
            }
        }
    }

    fun updateData(newModels : ArrayList<MyAdvertisementModel>) {
        newModels.forEach { models.add(it) }
    }

    fun removeByID(id: Int) {
        val model = getModelById(id)
        diff.oldDataset = models
        diff.newDataset = (models.clone() as ArrayList<MyAdvertisementModel>).also { it.remove(model) }

        models = diff.newDataset
        diffResult = DiffUtil.calculateDiff(diff)
        diffResult.dispatchUpdatesTo(this)
    }

    private fun getModelById(id: Int): MyAdvertisementModel {
        lateinit var model : MyAdvertisementModel

        models.forEach {
            if (it.id == id) {
                model = it
                return@forEach
            }
        }
        return model
    }

    fun clearData() {
        // not use notifyDataSetChanged method, because after we use updateData() where data is notifyDataSetChanged() going to called.
        models.clear()
    }

    class RemoveDiffCallback : DiffUtil.Callback() {

        lateinit var oldDataset: ArrayList<MyAdvertisementModel>
        lateinit var newDataset: ArrayList<MyAdvertisementModel>

        override fun getOldListSize() = oldDataset.size

        override fun getNewListSize() = newDataset.size

        // here we check the objects id
        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) = oldDataset[oldItemPosition] == newDataset[newItemPosition]

        // here if we use object we need to use equal
        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) = oldDataset[oldItemPosition] == newDataset[newItemPosition]
    }
}