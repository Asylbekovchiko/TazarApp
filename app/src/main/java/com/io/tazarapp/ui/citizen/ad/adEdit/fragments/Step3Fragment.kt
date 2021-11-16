package com.io.tazarapp.ui.citizen.ad.adEdit.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.io.tazarapp.R
import com.io.tazarapp.data.model.ad.AdModel
import com.io.tazarapp.data.model.ad.SubCategory
import com.io.tazarapp.data.model.filter.FilterSubcategoryModel
import com.io.tazarapp.ui.citizen.ad.adCreate.adapter.RawMaterialsAdapter
import com.io.tazarapp.ui.citizen.ad.adEdit.AdEditActivity
import com.io.tazarapp.ui.citizen.ad.adEdit.IValidate
import com.io.tazarapp.ui.citizen.ad.adEdit.viewModel.AdEditViewModel
import com.io.tazarapp.ui.citizen.filter.view.FilterCategoryActivity
import com.io.tazarapp.utils.CATEGORY_REQUEST_CODE
import com.io.tazarapp.utils.boom
import kotlinx.android.synthetic.main.step3_fragment.*
import org.koin.android.ext.android.inject

class Step3Fragment : Fragment(R.layout.step3_fragment) {
    lateinit var rawAdapter: RawMaterialsAdapter
    lateinit var rawLL: LinearLayoutManager
    private var listOfRawMaterials = ArrayList<SubCategory>()
    private lateinit var viewModel: AdEditViewModel
    private val adModel: AdModel by inject()
    private var parts = ArrayList<FilterSubcategoryModel>()
    private lateinit var mListener: IValidate

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        mapperRawMaterialsToParts()
        boom(btn_category)
        initRV()
        updateUI()
        listeners()
    }

    private fun updateUI() = rawAdapter.swapData(adModel.subcategory_ads)

    private fun initRV() {
        rawAdapter = RawMaterialsAdapter()
        rawLL = LinearLayoutManager(requireContext())

        with(rv_raw_material) {
            layoutManager = rawLL
            adapter = rawAdapter
        }
    }

    private fun listeners() {
        btn_category.setOnClickListener {
            val i = Intent(requireContext(), FilterCategoryActivity::class.java)
            i.putExtra("parts", parts)
            startActivityForResult(i, CATEGORY_REQUEST_CODE)
        }

        rawAdapter.verifyItems = { list ->

            adModel.subcategory_ads = rawAdapter.getData().clone() as ArrayList<SubCategory>
            mListener.onValidate()

            var failFlag = false
            if (list.isNullOrEmpty()) {
                failFlag = true
            } else {
                list.forEach {
                    if (it.value == 0 || it.value == null) {
                        failFlag = true
                        return@forEach
                    }
                }
            }

            btn_next.isEnabled = !failFlag

            if (failFlag) {
                btn_next.background =
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.rounded_shape_silver_12dp_7b818c
                    )
            } else {
                btn_next.background =
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.rounded_shape_green_12dp_8cc341
                    )
            }
        }
        rawAdapter.deleteItem = { item ->
            parts.remove(parts.find { it.id == item })
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                CATEGORY_REQUEST_CODE -> {
                    parts =
                        data?.getSerializableExtra("selectedParts") as ArrayList<FilterSubcategoryModel>
                    mapper()

                }
            }
        }
    }

    private fun mapper() {
        listOfRawMaterials.clear()
        parts.forEach { item ->
            val subCategory = SubCategory()
            subCategory.subcategory = item.id
            subCategory.name = item.name
            listOfRawMaterials.add(subCategory)
        }
        Log.e("Admodel",adModel.subcategory_ads.toString())
        listOfRawMaterials.forEach { subCategory ->

            adModel.subcategory_ads.forEach {

                if (subCategory.subcategory==it.subcategory){
                    subCategory.value = it.value
                    subCategory.corrected_weight = it.corrected_weight
                    subCategory.id = it.id
                }

            }
        }

        Log.e("RAWMATERIALS",listOfRawMaterials.toString())
        rawAdapter.swapData(listOfRawMaterials)
    }

    private fun mapperRawMaterialsToParts() {
        parts.clear()
        adModel.subcategory_ads.forEach { item ->
            val subCategory = FilterSubcategoryModel(item.subcategory!!,item.name!!,"","")
            parts.add(subCategory)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            mListener = requireActivity() as AdEditActivity
        } catch (e: ClassCastException) {
            throw ClassCastException("Error")
        }
    }
}