package com.io.tazarapp.ui.citizen.filter.view.bottom

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.io.tazarapp.R
import com.io.tazarapp.data.model.auth.CityModel
import com.io.tazarapp.data.model.filter.FilterSubcategoryModel
import com.io.tazarapp.ui.auth.login.city.CityActivity
import com.io.tazarapp.ui.citizen.filter.view.FilterCategoryActivity
import com.io.tazarapp.utils.BaseActivity
import com.io.tazarapp.utils.CATEGORY_REQUEST_CODE
import kotlinx.android.synthetic.main.activity_base_bottom.*
import java.util.ArrayList

class BaseBottomActivity : BaseActivity() {
    private val CITY_REQUEST_CODE = 312

    private lateinit var bundle: Bundle
    private lateinit var fragment: BottomChooseCategoryFragment

    // your need data
    private var parts = ArrayList<FilterSubcategoryModel>()
    private var city : CityModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base_bottom)

        listeners()
    }

    private fun listeners() {
        button.setOnClickListener { openBottomDialog() }
    }

    private fun openBottomDialog() {
//        bundle = Bundle().also {
//            it.putSerializable("selected",parts)
//            it.putSerializable("city",city)
//        }
//        fragment = BottomChooseCategoryFragment(openCategory,openCity)
//        fragment.arguments = bundle
//        fragment.show(supportFragmentManager,fragment.tag)
    }

    private val openCity : () -> Unit = {
        val intent = Intent(this, CityActivity::class.java)
        intent.putExtra("parent",true)
        intent.putExtra("city",city)
        this.startActivityForResult(
            intent,
            CITY_REQUEST_CODE
        )
    }

    private val openCategory : () -> Unit = {
        val intent = Intent(this, FilterCategoryActivity::class.java)
        intent.putExtra("parts",parts)
        this.startActivityForResult(
            intent,
            CATEGORY_REQUEST_CODE
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode ==  Activity.RESULT_OK) {
            when(requestCode) {
                CATEGORY_REQUEST_CODE -> {
                    parts = data?.getSerializableExtra("selectedParts") as ArrayList<FilterSubcategoryModel>
                    bundle.putSerializable("selected",parts)
                }
                CITY_REQUEST_CODE -> {
                    city = data?.getSerializableExtra("city") as CityModel
                    bundle.putSerializable("city",city)
                }
            }
        }
    }
}