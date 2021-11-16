package com.io.tazarapp.ui.citizen.ad.adCreate.adapter

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.io.tazarapp.R
import com.io.tazarapp.data.model.ad.ScheduleItem
import com.io.tazarapp.ui.citizen.ad.adCreate.fragment.BottomSheetFragment
import com.io.tazarapp.utils.boom
import kotlinx.android.synthetic.main.schedule_item.view.*


class FillScheduleAdapter(val fm: FragmentManager) :
    RecyclerView.Adapter<FillScheduleAdapter.ScheduleViewHolder>() {
    var items: ArrayList<ScheduleItem> = arrayListOf()
    var verifyItems: ((ArrayList<ScheduleItem>) -> Unit) = { }

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): ScheduleViewHolder {
        return ScheduleViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.schedule_item,
                parent,
                false
            )
        )
    }

    fun swapData(items: ArrayList<ScheduleItem>) {
        this.items = items
        notifyDataSetChanged()
    }

    fun getData(): ArrayList<ScheduleItem> = items

    override fun onBindViewHolder(holder: ScheduleViewHolder, position: Int) =
        holder.bind(items[position])

    inner class ScheduleViewHolder(view: View) : RecyclerView.ViewHolder(view),
        BottomSheetFragment.BottomSheetListener {
        private val txtTime = view.time!!
        private val titleDay = view.day!!
        val button = view.plus!!
        val context = view.context!!

        fun bind(item: ScheduleItem) = with(itemView) {
            boom(button)
            when (item.name) {
                1 -> titleDay.text = getString(R.string.monday)
                2 -> titleDay.text = getString(R.string.tuesday)
                3 -> titleDay.text = getString(R.string.wednesday)
                4 -> titleDay.text = getString(R.string.thursday)
                5 -> titleDay.text = getString(R.string.friday)
                6 -> titleDay.text = getString(R.string.saturday)
                7 -> titleDay.text = getString(R.string.sunday)
            }

            when {
                item.is_weekend -> {
                    txtTime.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.colorAccent_8CC341
                        )
                    )
                    txtTime.text = context.resources.getString(R.string.day_off_short)
                }
                !item.start_at_time.isNullOrEmpty() && !item.expires_at_time.isNullOrEmpty() -> {
                    txtTime.setTextColor(ContextCompat.getColor(context, R.color.color_gray_7B818C))
                    txtTime.text = "${item.start_at_time} - ${item.expires_at_time}"
                }
                else -> {
                    txtTime.setTextColor(ContextCompat.getColor(context, R.color.color_gray_7B818C))
                    txtTime.text = context.resources.getString(R.string.not_chosen)
                }
            }
            button.setOnClickListener { showBottomSheet(item) }
        }

        private fun getString(resourceId: Int) = context.resources.getString(resourceId)

        private fun showBottomSheet(item: ScheduleItem) = with(itemView) {
            val bottomSheetFragment = BottomSheetFragment(this@ScheduleViewHolder)
            val bundle = Bundle()
            bundle.putSerializable("item", item)
            bottomSheetFragment.arguments = bundle
            bottomSheetFragment.show(fm, bottomSheetFragment.tag)
        }

        override fun onReadyButtonClicked(scheduleItem: ScheduleItem, isForAll: Boolean) {
            if (isForAll) {
                items.forEach {
                    it.start_at_time = scheduleItem.start_at_time
                    it.expires_at_time = scheduleItem.expires_at_time
                }
            }
            notifyDataSetChanged()
            verifyItems.invoke(items)
        }
    }

    override fun getItemCount(): Int = items.size
}