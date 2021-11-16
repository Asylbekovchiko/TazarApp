package com.io.tazarapp.ui.citizen.profile.item_profile.my_advertisements.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.io.tazarapp.R
import com.io.tazarapp.data.model.advertisement.SubcategoryAd
import com.io.tazarapp.data.model.advertisement.my_advertisement.ScoringHistoryModel
import com.io.tazarapp.utils.SubCategoryAdFlowLayoutAdapter
import com.io.tazarapp.utils.formatDateScData
import com.io.tazarapp.utils.gone
import com.io.tazarapp.utils.visible
import kotlinx.android.synthetic.main.scoring_history_item.view.*
import kotlin.collections.ArrayList

class ScoringHistoryFragmentAdapter : RecyclerView.Adapter<ScoringHistoryFragmentAdapter.PointHistoryViewHolder>(){
    private var models = ArrayList<ScoringHistoryModel>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = PointHistoryViewHolder (
        LayoutInflater.from(parent.context).inflate(
            R.layout.scoring_history_item,
            parent,
            false
        )
    )

    override fun onBindViewHolder(holder: PointHistoryViewHolder, position: Int) = holder.bind(models[position])

    override fun getItemCount() = models.size

    fun updateData(newModels : ArrayList<ScoringHistoryModel>) {
        newModels.forEach { models.add(it) }
    }

    fun clearData() {
        models.clear()
        notifyDataSetChanged()
    }

    inner class PointHistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        @SuppressLint("SetTextI18n")
        fun bind(model: ScoringHistoryModel) {
            itemView.apply {
                point_history_date_value.text = formatDateScData(model.date_created)
                point_history_point_value.text = "${model.quantity}  ${resources.getString(R.string.Points)}"
                if (model.status == 1) {
                    Glide.with(context).load(R.drawable.ic_simple_green_up_right).into(point_history_icon)
                    point_history_status.setTextColor(ContextCompat.getColor(context,R.color.colorAccent_8CC341))
                    point_history_collector.text = resources.getText(R.string.collector)
                    point_history_collector_value.text = model.sender ?: "-"
                    point_history_status.text = resources.getString(R.string.get_status)
                    if(!model.subcategory.isNullOrEmpty()) {
                        point_history_flowlayout.adapter = SubCategoryAdFlowLayoutAdapter(
                            model.subcategory,
                            point_history_flowlayout,
                            2
                        )
                    }
                }else {
                    Glide.with(context).load(R.drawable.ic_simple_red_down_left).into(point_history_icon)
                    point_history_status.setTextColor(ContextCompat.getColor(context,R.color.color_red_ea565e))
                    point_history_collector.text = resources.getText(R.string.partner)
                    point_history_collector_value.text = model.present_sender ?: "-"
                    point_history_status.text = resources.getString(R.string.take_status)
                    point_history_flowlayout.adapter = SubCategoryAdFlowLayoutAdapter(
                        ArrayList(),
                        point_history_flowlayout,
                        2
                    )
                }
            }
        }
    }
}