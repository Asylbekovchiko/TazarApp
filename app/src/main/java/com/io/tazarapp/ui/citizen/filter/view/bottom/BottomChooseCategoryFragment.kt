package com.io.tazarapp.ui.citizen.filter.view.bottom

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.io.tazarapp.R
import com.io.tazarapp.data.model.auth.CityModel
import com.io.tazarapp.data.model.filter.FilterSubcategoryModel
import kotlinx.android.synthetic.main.bottom_choose_category_layout.*
import java.util.ArrayList

class BottomChooseCategoryFragment constructor(
    private val openCategory: () -> Unit,
    private val openCity: () -> Unit,
    private val filterClick: () -> Unit
) : BottomSheetDialogFragment() {
    private lateinit var parts: ArrayList<FilterSubcategoryModel>
    private var city: CityModel? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_choose_category_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listeners()
    }


    override fun onResume() {
        super.onResume()
        updateFlowLayout()
        updateCity()
        updateBtnBg()
    }

    private fun updateBtnBg() {
        if (parts.isEmpty() && city == null) {
            btn_show_results.setBackgroundResource(R.drawable.rounded_shape_silver_12dp_7b818c)
            btn_show_results.isClickable = false
        } else {
            btn_show_results.setBackgroundResource(R.drawable.rounded_selector_green_gradwith_grn_sil)
            btn_show_results.isClickable = true
        }
    }

    private fun updateCity() {
        city = this.arguments?.getSerializable("city") as CityModel?
        if (city != null) {
            selected_city.text = city!!.name
        }
    }

    private fun listeners() {
        choose_raw_materials.setOnClickListener { openCategory() }
        choose_city.setOnClickListener { openCity() }
        btn_close.setOnClickListener { dismiss() }
        btn_show_results.setOnClickListener {
            filterClick()
            dismiss()
        }
    }

    private fun updateFlowLayout() {
        parts = this.arguments?.getSerializable("selected") as ArrayList<FilterSubcategoryModel>

        filter_flow_layout.adapter = FilterFlowLayoutAdapter(parts, filter_flow_layout)
    }
}