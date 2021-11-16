package com.io.tazarapp.ui.recycler.profile.item_profile.types

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import com.io.tazarapp.R
import com.io.tazarapp.data.model.filter.FilterCategoryModel
import com.io.tazarapp.data.model.filter.FilterSubcategoryModel
import com.io.tazarapp.data.model.profile.ProfileRecyclerModel
import com.io.tazarapp.data.state.State
import com.io.tazarapp.ui.citizen.filter.view.BottomInfoFragment
import com.io.tazarapp.ui.citizen.filter.view.FilterTabsAdapter
import com.io.tazarapp.ui.citizen.filter.view.parts.FilterPartsFragment
import com.io.tazarapp.ui.citizen.filter.viewmodel.FilterCategoryViewModel
import com.io.tazarapp.ui.recycler.schedule.ScheduleViewModel
import com.io.tazarapp.utils.BaseActivity
import com.io.tazarapp.utils.activate
import com.io.tazarapp.utils.disActivate
import kotlinx.android.synthetic.main.activity_type.*
import kotlinx.android.synthetic.main.custom_bold_toolbar.*
import kotlinx.android.synthetic.main.custom_bold_toolbar.view.*
import okhttp3.ResponseBody
import org.koin.androidx.viewmodel.ext.android.viewModel

class TypeActivity : BaseActivity() {
    private var selectedParts = ArrayList<FilterSubcategoryModel>()
    private lateinit var pages: FilterTabsAdapter

    private val viewModel: FilterCategoryViewModel by viewModel()
    private val viewModelMain: ScheduleViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_type)
        init()
    }

    private fun init() {
        initToolbar()
        initTabLayout()
        initViewModel()
        initMainViewModel()
        initListener()
    }

    private fun initViewModel() {
        viewModel.state.observe(this, Observer { state ->
            if (state != null) {
                when (state) {
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
                                Log.e("data", state.data.toString())
                            }
                        }
                    }
                    is State.SuccessObjectState<*> ->{
                        if (state.data is ResponseBody){
                            finish()
                        }
                    }
                }
            } else {
                Log.e("Empty", "Item class is empty")
            }
        })

    }

    private fun initMainViewModel() {
        viewModelMain.state.observe(this, Observer { state ->
            if (state != null) {
                when (state) {
                    is State.LoadingState -> {
                        when {
                            state.isLoading -> showDialog()
                            else -> hideDialog()
                        }
                    }
                    is State.ErrorState -> {
                        handleError(state)
                    }
                    is State.SuccessObjectState<*> -> {
                        when (state.data) {
                            is ProfileRecyclerModel -> {
                                selectedParts.addAll(state.data.subcategory)
                                viewModel.getCategoryList()
                            }
                        }
                    }
                }
            } else {
                Log.e("Empty", "Item class is empty")
            }
        })
    }

    override fun onResume() {
        super.onResume()
        viewModelMain.getMainProfile()
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

private fun initListener() {
    val idUser = intent.getIntExtra("idUser", -1)
    reset.setOnClickListener { cleanSelectedParts() }

    btn_filter_ready.setOnClickListener {
        val isList = ArrayList<Int>()
        for (id in selectedParts.iterator()) {
            isList.add(id.id)
        }
        viewModel.postUserType(idUser, isList)
    }

    custom_bold_back.setOnClickListener {
        finish()
    }
}

private fun cleanSelectedParts() {
    selectedParts.clear()
    for (item in pages.fragmentList) {
        try {
            item.adapter.notifyDataSetChanged()
        } catch (e: Exception) {
        }
    }
    btn_filter_ready.disActivate()
}

private fun initTabLayout() {
    pages = FilterTabsAdapter(supportFragmentManager)

    filter_parts.adapter = pages
    filter_parts.offscreenPageLimit = 2
    tabs.setupWithViewPager(filter_parts)
}

private fun createBundle(id: Int) = Bundle().also {
    it.putSerializable("selectedParts", selectedParts)
    it.putInt("categoryId", id)
}

private val updateBtnBg: () -> Unit = {
    btn_filter_ready.apply {
        if (selectedParts.isEmpty()) {
            disActivate()
        } else {
            activate()
        }
    }
}

private val showBottomInfo = { filterParts: FilterSubcategoryModel ->
    val fragment = BottomInfoFragment(filterParts)
    fragment.show(supportFragmentManager, fragment.tag)
}

private fun initToolbar() {
    setSupportActionBar(toolbar)
    toolbar.textView4.text = "Выбрать сырье"
}
}
