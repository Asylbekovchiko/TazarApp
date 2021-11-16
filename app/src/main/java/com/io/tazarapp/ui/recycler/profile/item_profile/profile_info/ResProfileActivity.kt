package com.io.tazarapp.ui.recycler.profile.item_profile.profile_info

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.io.tazarapp.R
import com.io.tazarapp.data.model.profile.ProfileRecyclerModel
import com.io.tazarapp.data.state.State
import com.io.tazarapp.ui.recycler.schedule.ScheduleViewModel
import com.io.tazarapp.utils.*
import kotlinx.android.synthetic.main.activity_res_profile.*
import kotlinx.android.synthetic.main.custom_toolbar.*
import kotlinx.android.synthetic.main.custom_toolbar.view.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class ResProfileActivity : BaseActivity() {
    private val viewModel: ScheduleViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_res_profile)
        initToolbar()
        initViewModel()
    }

    override fun handleCustomError(message: String) {
        toast(message)
    }

    private fun listeners() {
        toolbar.setNavigationOnClickListener {
            finish()
        }
        btn_edit_profile.setOnClickListener {
            startActivity<EditResProfileActivity>()
        }
    }

    private fun initToolbar() {
        setSupportActionBar(toolbar)
        toolbar.custom_title.text = resources.getString(R.string.profile_title)
        listeners()
    }

    private fun initViewModel() {
        viewModel.state.observe(this, Observer { state ->
            if (state == null) {
                Log.e("Empty", "Item class is empty")
            } else {
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
                                with(state.data) {
                                    Glide.with(this@ResProfileActivity).load(image)
                                        .circleCrop().into(img_profile_info)
                                    tv_name_profile.text = title
                                    tv_pn_profile.text = maskPhoneNumb(phone)
                                    tv_pn_profile.text = maskPhoneNumb(phone)
                                    if (point_image != null) {
                                        point_card.visible()
                                        Glide.with(this@ResProfileActivity)
                                                .load(point_image)
                                                .into(point_img)
                                    } else {
                                        point_card.gone()
                                    }
                                    if (collection_map.isNullOrEmpty()) {
                                        tv_city_profile.text = city
                                    } else {
                                        tv_city_profile.text = collection_map[0]!!.address
                                    }
                                    tv_desc_profile.text = description
                                }
                            }
                        }
                    }
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()
        viewModel.getMainProfile()
    }
}