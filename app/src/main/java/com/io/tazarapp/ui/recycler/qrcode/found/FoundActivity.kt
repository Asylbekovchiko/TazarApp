package com.io.tazarapp.ui.recycler.qrcode.found

import android.os.Bundle
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.io.tazarapp.R
import com.io.tazarapp.data.model.advertisement.SubcategoryAd
import com.io.tazarapp.data.model.advertisement.user_advertisement.Advertisement
import com.io.tazarapp.data.model.advertisement.user_advertisement.OwnedScoreModel
import com.io.tazarapp.data.model.advertisement.user_advertisement.UserAdvertisementModel
import com.io.tazarapp.data.state.State
import com.io.tazarapp.ui.recycler.qrcode.found.adapters.FoundAdvertisementEditAdapter
import com.io.tazarapp.ui.recycler.qrcode.found.adapters.FoundAdvertisementsAdapter
import com.io.tazarapp.utils.BaseActivity
import com.io.tazarapp.utils.gone
import com.io.tazarapp.utils.toast
import com.io.tazarapp.utils.visible
import kotlinx.android.synthetic.main.activity_found.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class FoundActivity : BaseActivity() {
    private val viewModel : FoundViewModel by viewModel()
    private lateinit var showAdapter : FoundAdvertisementsAdapter
    private var editAdapter = FoundAdvertisementEditAdapter()

    private var IS_FIRST_STATE = true

    private var originList = ArrayList<SubcategoryAd>()
    private var citizenID = 0
    private var advertisementId = 0
    private var citizenName = "unknown"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_found)

        init()
    }

    private fun init() {
        initToolbar()
        initAdapters()
        initViewModel()
        initListeners()
    }

    private fun initListeners() {
        found_activity_toolbar.setNavigationOnClickListener { finish() }
        btn_next.setOnClickListener {
            viewModel.putSubcategoryAds(advertisementId,citizenID,originList)
        }

        editAdapter.verifyItems = {
            var failFlag = false
            if (originList.isNullOrEmpty()) {
                failFlag = true
            } else {
                originList.forEach {
                    if (it.corrected_weight == 0) {
                        failFlag = true
                        return@forEach
                    }
                }
            }

            btn_next.isEnabled = !failFlag

            if (failFlag) {
                btn_next.background =
                    ContextCompat.getDrawable(this, R.drawable.rounded_shape_silver_12dp_7b818c)
            } else {
                btn_next.background =
                    ContextCompat.getDrawable(
                        this,
                        R.drawable.rounded_selector_green_gradwith_grn_sil
                    )
            }
        }

        editAdapter.removeItem = { id ->
            (originList.find { it.id == id })?.is_delete = true
        }
    }

    private fun openBottomNav(ownedScoreModel: OwnedScoreModel) {
        val bundle = Bundle()
        bundle.putInt("point", ownedScoreModel.score)
        bundle.putString("name", citizenName)

        val bottomSheetFragment = FoundSuccessBottomSheetFragment(finish)
        bottomSheetFragment.arguments = bundle
        bottomSheetFragment.show(supportFragmentManager, bottomSheetFragment.tag)
    }

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
                    handleError(it)
                }
                is State.SuccessListState<*> -> {
                    when {
                        it.data[0] is UserAdvertisementModel -> {
                            val citizen = it.data[0] as UserAdvertisementModel
                            citizenID = citizen.id
                            citizenName = citizen.name
                            bindData(citizen)
                        }
                    }
                }
                is State.SuccessObjectState<*> -> {
                    if (it.data is OwnedScoreModel){
                        openBottomNav(it.data)
                    }
                }
            }
        })
        val qrToken = intent.getStringExtra("qr_token") ?: ""
        viewModel.getUserAdvertisement(qrToken)
    }

    private fun bindData(citizen: UserAdvertisementModel) {
        Glide.with(this)
            .load(citizen.image)
            .circleCrop()
            .into(activity_found_image)

        activity_found_name.text = citizen.name

        showAdapter.submitList(citizen.advertisement)
    }

    private fun initAdapters() {
        activity_found_rv_show.layoutManager = LinearLayoutManager(this)
        showAdapter = FoundAdvertisementsAdapter(secondStateUI)
        activity_found_rv_show.adapter = showAdapter

        activity_found_rv_edit.layoutManager = LinearLayoutManager(this)
        activity_found_rv_edit.adapter = editAdapter
    }

    private fun initToolbar() {
        setSupportActionBar(found_activity_toolbar)
        found_activity_custom_title.text = resources.getString(R.string.Found)
    }

    private fun firstStateUI() {
        AnimationUtils.loadAnimation(this,R.anim.show_anim_rvshow).also { animation ->
            activity_found_rv_show.visible()
            activity_found_rv_show.startAnimation(animation)
        }
        AnimationUtils.loadAnimation(this,R.anim.hide_anim_rvedit).also { animation ->
            activity_found_rv_edit.startAnimation(animation)
            activity_found_rv_edit.gone()
        }
        btn_layout.visible()
        IS_FIRST_STATE = true
    }

    private val secondStateUI : (Advertisement) -> Unit = {
        AnimationUtils.loadAnimation(this,R.anim.hide_amin_rvshow).also { animation ->
            activity_found_rv_show.startAnimation(animation)
            activity_found_rv_show.gone()
        }
        AnimationUtils.loadAnimation(this,R.anim.show_anim_rvedit).also { animation ->
            activity_found_rv_edit.visible()
            activity_found_rv_edit.startAnimation(animation)
        }
        btn_layout.visible()

        advertisementId = it.id
        originList = it.subcategory_ads
        editAdapter.submitList(originList.clone() as ArrayList<SubcategoryAd>)
        IS_FIRST_STATE = false
    }

    private val finish : () -> Unit = { finish() }

    override fun handleCustomError(message: String) { toast(message) }

    override fun onBackPressed() {
        if (!IS_FIRST_STATE) {
            firstStateUI()
        }else {
            super.onBackPressed()
        }
    }
}