package com.io.tazarapp.ui.citizen.ad.adEdit

import android.content.Intent
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.io.tazarapp.R
import com.io.tazarapp.data.model.ad.AdFile
import com.io.tazarapp.data.model.ad.AdModel
import com.io.tazarapp.data.model.ad.ScheduleItem
import com.io.tazarapp.data.model.ad.SubCategory
import com.io.tazarapp.data.state.State
import com.io.tazarapp.ui.citizen.ad.adEdit.fragments.*
import com.io.tazarapp.ui.citizen.ad.adEdit.viewModel.AdEditViewModel
import com.io.tazarapp.utils.BaseActivity
import com.io.tazarapp.utils.gone
import com.io.tazarapp.utils.toast
import kotlinx.android.synthetic.main.activity_ad_edit.*
import kotlinx.android.synthetic.main.adcreate_toolbar.*
import kotlinx.android.synthetic.main.adcreate_toolbar.view.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class AdEditActivity : BaseActivity(), IValidate {

    private val viewModel: AdEditViewModel by viewModel()
    private val adModel: AdModel by inject()
    private var isFirstRequest = true
    var id: Int = -1
    private val step1Fragment = Step1Fragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ad_edit)
        id = intent.getIntExtra("id", -1)
        init()
    }

    private fun init() {
        initToolbar()
        initViewModel()
        initListeners()
    }

    private fun initListeners() = btn_next_edit.setOnClickListener { viewModel.updateAd(adModel) }

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
                    is State.ErrorState -> handleError(state)

                    is State.SuccessObjectState<*> -> {
                        when (state.data) {
                            is AdModel -> {
                                if (!isFirstRequest) {
                                    finish()
                                } else {
                                    isFirstRequest = false
                                    val model = state.data
                                    adModel.clearData()
                                    adModel.fillData(model)
                                    initViewPager()
                                }
                            }
                        }
                    }
                }
            }
        })
        viewModel.getAd(id)
    }

    private fun initToolbar() {
        setSupportActionBar(toolbar)
        toolbar.custom_title.text = getString(R.string.edit_title)
        toolbar.sub_title.gone()
        toolbar.elevation = 4f
        toolbar.setNavigationOnClickListener { finish() }
    }

    private fun initViewPager() {
        val adapter = ViewPagerAdapter(supportFragmentManager)
        with(adapter) {
            addFragment(Step3Fragment(), resources.getString(R.string.raw_title))
            addFragment(step1Fragment, resources.getString(R.string.contacts))
            addFragment(Step2Fragment(), resources.getString(R.string.schedule_title))
            addFragment(Step4Fragment(), resources.getString(R.string.city_title))
            addFragment(Step5Fragment(), resources.getString(R.string.map_title))
        }
        view_pager.adapter = adapter
        view_pager.offscreenPageLimit = 4
        tab_layout.setupWithViewPager(view_pager)
    }

    //If Validation is FAILED, failFlag = True
    override fun onValidate() {
        var failFlag = false
        with(adModel) {
            if (name.isNullOrEmpty() || phone.toString()
                    .count() != 13 || description.isNullOrEmpty()
            ) {
                failFlag = true
            }
            if (validateRawMaterials(subcategory_ads)) {
                failFlag = true
            }
            if (city == null || city == 0) {
                failFlag = true
            }
            if (ads_map.isNullOrEmpty()) {
                failFlag = true
            }
        }

        btn_next_edit.isEnabled = !failFlag

        if (failFlag) {
            btn_next_edit.background =
                ContextCompat.getDrawable(this, R.drawable.rounded_shape_silver_12dp_7b818c)
        } else {
            btn_next_edit.background =
                ContextCompat.getDrawable(
                    this,
                    R.drawable.rounded_shape_green_12dp_8cc341
                )
        }
    }

    private fun validateRawMaterials(list: ArrayList<SubCategory>): Boolean {
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
        return failFlag
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        step1Fragment.onActivityResult(requestCode, resultCode, data)
    }
}