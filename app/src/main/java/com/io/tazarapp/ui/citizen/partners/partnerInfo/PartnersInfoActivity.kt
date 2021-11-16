package com.io.tazarapp.ui.citizen.partners.partnerInfo

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.io.tazarapp.R
import com.io.tazarapp.data.model.partners.PartnersDetModel
import com.io.tazarapp.data.state.State
import com.io.tazarapp.ui.citizen.partners.PartnersViewModel
import com.io.tazarapp.utils.*
import kotlinx.android.synthetic.main.activity_partners_info.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class PartnersInfoActivity : BaseActivity() {
    override fun handleCustomError(message: String) {
        toast(message)
    }

    private val viewModel: PartnersViewModel by viewModel()
    private lateinit var adapterPartnersInfo: PartnersInfoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLocale(this)
        setContentView(R.layout.activity_partners_info)
        listeners()
        partnersInfoInit()
        initViewModel()
    }

    private fun listeners() {
        back_part.setOnClickListener {
            finish()
        }
    }

    private fun partnersInfoInit() {
        adapterPartnersInfo =
            PartnersInfoAdapter()
        rv_partners_info?.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rv_partners_info?.adapter = adapterPartnersInfo
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initViewModel() {
        viewModel.state.observe(this, Observer { state ->
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
                        is PartnersDetModel -> {
                            with(state.data) {
                                adapterPartnersInfo.update(prize)
                                Glide.with(this@PartnersInfoActivity).load(logo)
                                    .into(image_partners_info)
                                title_partners_info.text = name

                                contacts_partners_info.showFromUrl(contacts)
                                desc_partners_info.showFromUrl(description)
                                contacts_partners_info!!.settings.javaScriptEnabled = true
                                desc_partners_info!!.settings.javaScriptEnabled = true
                                setLocale(this@PartnersInfoActivity)
                                tv_about_com.text = resources.getString(R.string.about_company)
                            }
                        }
                    }
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()
        val ids = intent.getIntExtra(PARTNERS_ID, -1)
        viewModel.getPartnersDetail(ids)
    }
}
