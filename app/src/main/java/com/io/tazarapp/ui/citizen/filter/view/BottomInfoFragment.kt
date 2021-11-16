package com.io.tazarapp.ui.citizen.filter.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.io.tazarapp.R
import com.io.tazarapp.data.model.filter.FilterSubcategoryModel
import kotlinx.android.synthetic.main.bottom_filter_info_layout.*

class BottomInfoFragment(private val filterParts: FilterSubcategoryModel) : BottomSheetDialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.bottom_filter_info_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        text.text = filterParts.description
        title.text = filterParts.name
        Glide.with(requireContext()).load(filterParts.image).into(image)

        btn_cancel.setOnClickListener { dismiss() }
    }
}