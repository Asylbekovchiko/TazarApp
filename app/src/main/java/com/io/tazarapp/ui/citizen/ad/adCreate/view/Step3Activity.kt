package com.io.tazarapp.ui.citizen.ad.adCreate.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.io.tazarapp.R
import com.io.tazarapp.data.model.ad.AdModel
import com.io.tazarapp.data.model.ad.SubCategory
import com.io.tazarapp.data.model.collectionplace.CollectionPointModel
import com.io.tazarapp.data.model.filter.FilterSubcategoryModel
import com.io.tazarapp.ui.citizen.ad.adCreate.adapter.RawMaterialsAdapter
import com.io.tazarapp.ui.citizen.filter.view.FilterCategoryActivity
import com.io.tazarapp.ui.shared_ui.new_cp.NewCollectionPointActivity
import com.io.tazarapp.utils.*
import kotlinx.android.synthetic.main.activity_step3.*
import kotlinx.android.synthetic.main.adcreate_toolbar.*
import kotlinx.android.synthetic.main.adcreate_toolbar.view.*
import kotlinx.android.synthetic.main.maps_page.*
import org.koin.android.ext.android.inject

class Step3Activity : ActivityWithCleanBack() {
    private lateinit var rawAdapter: RawMaterialsAdapter
    private lateinit var rawLL: LinearLayoutManager
    private var listOfRawMaterials = ArrayList<SubCategory>()
    private val adModel: AdModel by inject()
    private var parts = ArrayList<FilterSubcategoryModel>()
    private val cpModel: CollectionPointModel by inject()
    private var isNewPoint = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_step3)
        isNewPoint = intent.getBooleanExtra(POINT, false)
        init()
    }

    private fun init() {
        boom(btn_category)
        initToolbar()
        initRV()
        listeners()
        getDataFromPrev()
    }

    private fun getDataFromPrev() {
        val parts = intent.getSerializableExtra(PARTS) as? ArrayList<FilterSubcategoryModel>
                ?: this.parts

        this.parts = parts
        mapper()
        rawAdapter.swapData(listOfRawMaterials)
    }

    private fun listeners() {
        toolbar.setNavigationOnClickListener { onBackPressed() }

        btn_category.setOnClickListener {
            val i = Intent(this, FilterCategoryActivity::class.java)
            i.putExtra("parts", parts)
            startActivityForResult(i, CATEGORY_REQUEST_CODE)
        }

        btn_next.setOnClickListener {
            val newIntent =
                    if (isNewPoint) {
                        cpModel.subcategory = rawAdapter.getIDs()
                        Intent(this, NewCollectionPointActivity::class.java)
                    } else {
                        adModel.subcategory_ads = rawAdapter.getData()
                        Intent(this, Step1Activity::class.java)
                    }
            newIntent.putExtra("point", isNewPoint)
            startActivityWithCleanBack(newIntent)
        }

        rawAdapter.verifyItems = { list ->
            var failFlag = false
            if (isNewPoint) {
                if (list.isNullOrEmpty()) {
                    failFlag = true
                }
            } else {
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
            }

            btn_next.isEnabled = !failFlag

            if (failFlag) {
                btn_next.background =
                    ContextCompat.getDrawable(this, R.drawable.rounded_shape_silver_12dp_7b818c)
            } else {
                btn_next.background =
                    ContextCompat.getDrawable(this, R.drawable.rounded_shape_green_12dp_8cc341)
            }

        }
        rawAdapter.deleteItem = { item ->
            parts.remove(parts.find { it.id == item })
        }
    }

    private fun initRV() {
        rawAdapter = RawMaterialsAdapter(isNewPoint)
        rawLL = LinearLayoutManager(this)
        with(rv_raw_material) {
            layoutManager = rawLL
            adapter = rawAdapter
        }
    }

    private fun initToolbar() {
        setSupportActionBar(toolbar)
        toolbar.custom_title.text = getString(R.string.step3_title)
        if (isNewPoint){
            toolbar.sub_title.text = getString(R.string.step3_new_point_title)
        }else {
            toolbar.sub_title.text = getString(R.string.step3_sub_title)
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
                    rawAdapter.swapData(listOfRawMaterials)
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
    }

    override fun onBackPressed() {
        val result = Intent()
        result.putExtra(PARTS, parts)
        setResult(RESULT_OK, result)
        finish()
    }
}