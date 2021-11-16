package com.io.tazarapp.ui.citizen.filter.view.parts

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.io.tazarapp.R
import com.io.tazarapp.data.model.filter.FilterCategoryDetailModel
import com.io.tazarapp.data.model.filter.FilterSubcategoryModel
import com.io.tazarapp.data.state.State
import com.io.tazarapp.ui.citizen.filter.viewmodel.FilterCategoryViewModel
import com.io.tazarapp.utils.BaseFragment
import kotlinx.android.synthetic.main.filter_parts_fragment.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class FilterPartsFragment(
    private val showInfo:(parts:FilterSubcategoryModel) -> Unit,
    private val updateBg:() -> Unit
) : BaseFragment() {
    lateinit var adapter: FilterPartsAdapter
    private lateinit var selectedParts : ArrayList<FilterSubcategoryModel>
    private var categoryId = 0

    private val viewModel : FilterCategoryViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.filter_parts_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        getSerialize()
    }

    private fun initRV() {
        part_items.layoutManager = GridLayoutManager(context,2)

        adapter = FilterPartsAdapter(
            showInfo,
            checkParts,
            add,
            remove
        )
        part_items.adapter = adapter
    }

    private fun initViewModel() {
        viewModel.state.observe(viewLifecycleOwner, Observer { state ->
            if(state != null) {
                when(state) {
                    is State.ErrorState -> {
                        handleError(state)
                    }

                    is State.SuccessObjectState<*> -> {
                        when (state.data) {
                            is FilterCategoryDetailModel -> {
                                adapter.updateParts(state.data.subcategory)
                                Log.e("SUBCATEGORIES",state.data.subcategory.toString())
                            }
                        }
                    }
                }
            }else {
                Log.e("Empty", "Item class is empty")
            }
        })
        viewModel.getCategoryDetail(categoryId)
    }

    private fun getSerialize() {
        selectedParts = this.arguments?.getSerializable("selectedParts") as ArrayList<FilterSubcategoryModel>
        categoryId = this.arguments?.getInt("categoryId") ?: 0
        Log.e("SELECTED PARTS",selectedParts.toString())
    }

    private val checkParts : (FilterSubcategoryModel) -> Boolean = { filterParts->
        var boolean = false
        selectedParts.forEach {
            if (it.id == filterParts.id) {
                boolean=true
                return@forEach
            }
        }
        boolean
    }

    private val add : (FilterSubcategoryModel) -> Unit = { filterParts: FilterSubcategoryModel ->
        selectedParts.add(filterParts)
        updateBg()
    }

    private val remove : (FilterSubcategoryModel) -> Unit = { filterParts: FilterSubcategoryModel ->
        var index =0
        selectedParts.forEachIndexed { i, filterSubcategoryModel ->
            if (filterSubcategoryModel.id==filterParts.id){
                index = i
                return@forEachIndexed
            }
        }
        selectedParts.removeAt(index)
        updateBg()
    }

    override fun onResume() {
        initRV()
        initViewModel()
        super.onResume()
    }
}