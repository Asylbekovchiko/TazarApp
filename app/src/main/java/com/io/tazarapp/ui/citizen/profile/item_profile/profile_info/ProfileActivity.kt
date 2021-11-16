package com.io.tazarapp.ui.citizen.profile.item_profile.profile_info

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.io.tazarapp.R
import com.io.tazarapp.data.model.profile.ProfileMainModel
import com.io.tazarapp.data.state.State
import com.io.tazarapp.ui.citizen.profile.ProfileMainViewModel
import com.io.tazarapp.utils.BaseActivity
import com.io.tazarapp.utils.maskPhoneNumb
import com.io.tazarapp.utils.startActivity
import com.io.tazarapp.utils.toast
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.custom_toolbar.*
import kotlinx.android.synthetic.main.custom_toolbar.view.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class ProfileActivity : BaseActivity() {
    private val viewModel: ProfileMainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
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
            startActivity<EditProfileActivity>()
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
                            is ProfileMainModel -> {
                                with(state.data) {
                                    Glide.with(this@ProfileActivity).load(image)
                                        .circleCrop().into(img_profile_info)
                                    tv_name_profile.text = name
                                    tv_pn_profile.text = maskPhoneNumb(phone)
                                    tv_city_profile.text = city
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