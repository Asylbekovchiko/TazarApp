package com.io.tazarapp.ui.citizen.profile.item_profile.qr_info

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.io.tazarapp.R
import com.io.tazarapp.data.model.profile.ProfileMainModel
import com.io.tazarapp.data.state.State
import com.io.tazarapp.ui.citizen.profile.ProfileMainViewModel
import com.io.tazarapp.utils.BaseActivity
import com.io.tazarapp.utils.toast
import kotlinx.android.synthetic.main.activity_qr.*
import kotlinx.android.synthetic.main.custom_toolbar.*
import kotlinx.android.synthetic.main.custom_toolbar.view.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class QrActivity : BaseActivity() {

    private val viewModel: ProfileMainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qr)
        initToolbar()
        listeners()
        initViewModel()
    }

    private fun listeners() {
        btn_scan.setOnClickListener {
            startActivity(Intent(this, QrScanActivity::class.java))
        }
        toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    override fun handleCustomError(message: String) {
        toast(message)
    }

    private fun initToolbar() {
        setSupportActionBar(toolbar)
        toolbar.custom_title.text = getString(R.string.q_code)
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
                                Glide.with(this)
                                    .load(state.data.qr_image)
                                    .into(img_qr)
                                tv_points_qr.text = "${state.data.points}"
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