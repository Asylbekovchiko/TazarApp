package com.io.tazarapp.ui.citizen.filter.view.bottom

import android.view.LayoutInflater
import android.view.View
import com.io.tazarapp.R
import com.io.tazarapp.data.model.filter.FilterSubcategoryModel
import com.zhy.view.flowlayout.FlowLayout
import com.zhy.view.flowlayout.TagAdapter
import com.zhy.view.flowlayout.TagFlowLayout
import kotlinx.android.synthetic.main.filter_flow_layout_item.view.*
import java.util.ArrayList

class FilterFlowLayoutAdapter (
    private val parts : ArrayList<FilterSubcategoryModel>,
    private val flowLayout: TagFlowLayout
) : TagAdapter<FilterSubcategoryModel>(parts) {
    override fun getView(parent: FlowLayout?, position: Int, model: FilterSubcategoryModel): View {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.filter_flow_layout_item,flowLayout,false)

        view.filter_layout_item_name.text = model.name
        view.filter_layout_item_back_icon.setOnClickListener {
            parts.remove(model)
            notifyDataChanged()
        }
        return view
    }

}