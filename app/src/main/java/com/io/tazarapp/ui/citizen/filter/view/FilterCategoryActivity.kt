package com.io.tazarapp.ui.citizen.filter.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import com.io.tazarapp.R
import com.io.tazarapp.data.model.filter.FilterCategoryModel
import com.io.tazarapp.data.model.filter.FilterSubcategoryModel
import com.io.tazarapp.data.state.State
import com.io.tazarapp.ui.citizen.ad.adCreate.view.Step3Activity
import com.io.tazarapp.ui.citizen.filter.view.parts.FilterPartsFragment
import com.io.tazarapp.ui.citizen.filter.viewmodel.FilterCategoryViewModel
import com.io.tazarapp.utils.*
import kotlinx.android.synthetic.main.activity_filter.*
import kotlinx.android.synthetic.main.custom_bold_toolbar.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class FilterCategoryActivity : BaseActivity() {
    private lateinit var selectedParts : ArrayList<FilterSubcategoryModel>
    private lateinit var pages : FilterTabsAdapter

    private val viewModel:  FilterCategoryViewModel by viewModel()

    private var isFilterCreating = false
    private var isNewPoint = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filter)
        init()
    }

    private fun init() {
        getDataFromPrev()
        initToolbar()
        initTablayout()
        initViewModel()
        initListener()
    }

    private fun initViewModel() {
        viewModel.state.observe(this, Observer { state ->
            if(state != null) {
                when(state) {
                    is State.LoadingState -> {
                        when {
                            state.isLoading -> showDialog()
                            else -> hideDialog()
                        }
                    }

                    is State.ErrorState -> {
                        handleError(state)
                    }

                    is State.SuccessListState<*> -> {
                        when {
                            state.data.all { it is FilterCategoryModel } -> {
                                createPages(state.data as ArrayList<FilterCategoryModel>)
                            }
                        }
                    }
                }
            }else {
                Log.e("Empty", "Item class is empty")
            }
        })
        viewModel.getCategoryList()
    }

    private fun createPages(arrayList: ArrayList<FilterCategoryModel>) {
        pages.clear()
        arrayList.forEach { item ->
            pages.addFragment(
                FilterPartsFragment(
                    showBottomInfo,
                    updateBtnBg
                ).also {
                    it.arguments = createBundle(item.id)
                },
                item
            )
        }
        pages.notifyDataSetChanged()
    }

    private fun getDataFromPrev() {
        isFilterCreating = intent.getBooleanExtra(IS_FILTER_CREATING, false)
        isNewPoint = intent.getBooleanExtra(POINT, false)
        selectedParts =
                intent.getSerializableExtra("parts") as? ArrayList<FilterSubcategoryModel>
                        ?: ArrayList()
        updateBtnBg()
    }

    private fun initListener() {
        reset.setOnClickListener { cleanSelectedParts() }

        btn_filter_ready.setOnClickListener {
            if (isFilterCreating) nextStep()
            else comeBack()
        }

        custom_bold_back.setOnClickListener {
            if(selectedParts.isEmpty()){
                comeBack()
            }else{
                setResult(Activity.RESULT_CANCELED)
            }
            finish()
        }
    }

    private fun comeBack() {
        val result = Intent()
        Log.e("selectedParts",selectedParts.toString())
        result.putExtra("selectedParts",selectedParts)
        setResult(Activity.RESULT_OK,result)
        finish()
    }

    private fun nextStep() {
        val newIntent = Intent(this,Step3Activity::class.java)
        newIntent.putExtra(POINT, isNewPoint)
        newIntent.putExtra(PARTS, selectedParts)
        startActivityForResult(newIntent, FILTER_CATEGORY_REQUEST_CODE)
    }

    private fun cleanSelectedParts() {
        selectedParts.clear()
        for (item in pages.fragmentList) {
            try {
                item.adapter.notifyDataSetChanged()
            }catch (e : Exception) {}
        }
        btn_filter_ready.setBackgroundResource(R.drawable.rounded_shape_silver_12dp_7b818c)
        btn_filter_ready.isEnabled = false
    }

    private fun initTablayout() {
        pages = FilterTabsAdapter(supportFragmentManager)

        filter_parts.adapter = pages
        filter_parts.offscreenPageLimit = 2
        tabs.setupWithViewPager(filter_parts)
    }

    private fun createBundle(id : Int) = Bundle().also {
        it.putSerializable("selectedParts",selectedParts)
        it.putInt("categoryId",id)
    }

    private val updateBtnBg:() -> Unit = {R.drawable.rounded_shape_silver_12dp_7b818c
        btn_filter_ready.apply {
            isEnabled = if (selectedParts.isEmpty()) {
                setBackgroundResource(R.drawable.rounded_shape_silver_12dp_7b818c)
                false
            }else {
                setBackgroundResource(R.drawable.rounded_shape_green_12dp_8cc341)
                true
            }
        }
    }

    private val showBottomInfo = { filterParts: FilterSubcategoryModel ->
        val fragment = BottomInfoFragment(filterParts)
        fragment.show(supportFragmentManager,fragment.tag)
    }

    private fun initToolbar() {
        if (isFilterCreating) {
            tv_description.text =
                    if (isNewPoint) getString(R.string.step3_new_point_title)
                    else getString(R.string.step3_sub_title)
            tv_description.visible()
            textView4.text = getString(R.string.step3_title)
        }
        setSupportActionBar(toolbar)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                FILTER_CATEGORY_REQUEST_CODE -> {
                    val parts = data?.getSerializableExtra(PARTS) as? ArrayList<FilterSubcategoryModel>
                    if (parts == null) {
                        finish()
                    } else {
                        selectedParts.clear()
                        selectedParts.addAll(parts)
                        notifyAdapters()
                    }
                }
            }
        }
    }

    private fun notifyAdapters() {
        pages.fragmentList.forEach {
            it.adapter.notifyDataSetChanged()
        }
    }
}