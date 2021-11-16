package com.io.tazarapp.utils

import android.annotation.SuppressLint
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import com.bumptech.glide.Glide
import com.io.tazarapp.R
import com.io.tazarapp.data.model.advertisement.SubcategoryAd
import com.zhy.view.flowlayout.FlowLayout
import com.zhy.view.flowlayout.TagAdapter
import com.zhy.view.flowlayout.TagFlowLayout
import kotlinx.android.synthetic.main.his_flow_layout_item.view.*

class SubCategoryAdFlowLayoutAdapter (
    subcategories: ArrayList<SubcategoryAd>,
    private val flowLayout: TagFlowLayout,
    private val status: Int
): TagAdapter<SubcategoryAd>(subcategories) {

    @SuppressLint("SetTextI18n")
    override fun getView(parent: FlowLayout?, position: Int, item: SubcategoryAd): View {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.his_flow_layout_item,flowLayout,false)

        view.apply {
            name.text = item.name
            weight.visible()
            when(status){
                0 -> weight.gone()
                1 -> weight.text = item.value.toString() + context.resources.getString(R.string.kg)
                2 -> weight.text = item.corrected_weight.toString() + context.resources.getString(R.string.kg)
            }
            bg_lay.setBackgroundColor(Color.parseColor(item.color))
            Glide.with(context)
                .load(item.inner_image)
                .into(image)
        }
        return view
    }
}