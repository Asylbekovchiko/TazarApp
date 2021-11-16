package com.io.tazarapp.ui.citizen.ad.adCreate.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.io.tazarapp.R
import com.io.tazarapp.data.model.City
import com.io.tazarapp.data.model.ad.AdModel
import com.io.tazarapp.data.model.collectionplace.CollectionPointModel
import com.io.tazarapp.data.state.State
import com.io.tazarapp.ui.citizen.ad.adCreate.adapter.Step4Adapter
import com.io.tazarapp.ui.citizen.ad.adCreate.viewModel.AdCreateViewModel
import com.io.tazarapp.utils.ActivityWithCleanBack
import com.io.tazarapp.utils.BaseActivity
import com.io.tazarapp.utils.toast
import kotlinx.android.synthetic.main.activity_step4.*
import kotlinx.android.synthetic.main.adcreate_toolbar.*
import kotlinx.android.synthetic.main.adcreate_toolbar.view.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.android.ext.android.inject


@Suppress("UNCHECKED_CAST")
class Step4Activity : ActivityWithCleanBack() {
    private val viewModel: AdCreateViewModel by viewModel()
    private lateinit var adapter: Step4Adapter
    private val adModel: AdModel by inject()
    private val cpModel: CollectionPointModel by inject()
    private var isNewPoint = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_step4)
        isNewPoint = intent.getBooleanExtra("point", false)
        init()
    }

    private fun init() {
        initToolbar()
        initRV()
        initViewModel()
        listeners()
    }

    private fun listeners() {
        sv_step4.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter(newText)
                return true
            }
        })

        btn_next.setOnClickListener {
            if (isNewPoint) {
                cpModel.city = adapter.getCheckedItem()?.id
            } else {
                adModel.city = adapter.getCheckedItem()?.id
            }

            val i = Intent(this, Step5Activity::class.java)
            i.putExtra("location", getLatLng())
            i.putExtra("city", adapter.getCheckedItem()?.name)
            i.putExtra("point", isNewPoint)
            startActivityWithCleanBack(i)
        }

        adapter.update = { checkedItemId ->
            if (checkedItemId == null) {
                btn_next.isEnabled = false
                btn_next.setBackgroundResource(R.drawable.rounded_shape_silver_12dp_7b818c)
            } else {
                btn_next.isEnabled = true
                btn_next.setBackgroundResource(R.drawable.rounded_shape_green_12dp_8cc341)
            }
        }
    }

    private fun getLatLng(): ArrayList<String>? =
        adapter.getCheckedItem()?.geolocation?.split(",") as ArrayList<String>?

    private fun initViewModel() {
        viewModel.state.observe(this, Observer {
            when (it) {
                is State.LoadingState -> {
                    when {
                        it.isLoading -> showDialog()
                        else -> hideDialog()
                    }
                }

                is State.ErrorState -> {
                    toast(it.message)
                }

                is State.SuccessListState<*> -> {
                    when {
                        it.data.all { item -> item is City } -> {
                            adapter.swapData(it.data as ArrayList<City>)
                        }
                    }
                }
            }
        })
        viewModel.getCities()
    }

    private fun initRV() {
        adapter = Step4Adapter()
        rv_step4.layoutManager = LinearLayoutManager(this)
        rv_step4.adapter = adapter
    }

    private fun initToolbar() {
        setSupportActionBar(toolbar)
        toolbar.custom_title.text = getString(R.string.step4_title)
        toolbar.sub_title.text = getString(R.string.step4_sub_title)
        toolbar.setNavigationOnClickListener { finish() }
    }
}