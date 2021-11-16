package com.io.tazarapp.ui.citizen.profile.item_profile.qr_info

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.io.tazarapp.R
import com.io.tazarapp.data.model.qr_prize.QrPrizeModel
import com.io.tazarapp.data.model.qr_prize.QrScoreModel
import com.io.tazarapp.data.state.State
import com.io.tazarapp.utils.*
import kotlinx.android.synthetic.main.activity_qr_prize.*
import kotlinx.android.synthetic.main.custom_toolbar.*
import kotlinx.android.synthetic.main.custom_toolbar.view.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class QrPrizeActivity : BaseActivity() {

    private val viewModel: QrViewModel by viewModel()
    var idScore: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qr_prize)
        val qrToken = intent.getStringExtra(TOKEN_ID)
        setSupportActionBar(toolbar)
        listener(qrToken)
        initViewModel(qrToken)
    }

    private fun initViewModel(qrToken: String) {
        with(viewModel) {
            getPartnersPrize(qrToken)
            state.observe(this@QrPrizeActivity, Observer { state ->
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
                                is QrPrizeModel -> {
                                    Glide.with(this@QrPrizeActivity).load(state.data.image)
                                        .into(img_qr_prize)
                                    tv_title_prize.text = state.data.name
                                    tv_price_prize.text =
                                        "${state.data.price}" + getString(R.string.p)
                                    tv_points_prize.text = "${state.data.user_points}"
                                    toolbar.custom_title.text = state.data.partner
                                    idScore = state.data.id

                                    if (state.data.user_points < state.data.price) {
                                        tv_error_prize.visible()
                                        btn_scan_prize.disActivate()
                                    } else {
                                        tv_error_prize.gone()
                                        btn_scan_prize.activate()
                                    }
                                }

                                is QrScoreModel -> {
                                    showBottomSheet(state.data.score)
                                }
                            }
                        }
                    }
                }
            })
        }
    }

    private fun listener(qrToken: String) {
        btn_scan_prize.setOnClickListener {
            viewModel.postPartnersPrize(qrToken, idScore)
        }
        toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun showBottomSheet(point: Int) {
        val bundle = Bundle()
        bundle.putInt(POINT, point)
        val bottomSheetFragment = QrBottomSheetFragment()
        bottomSheetFragment.arguments = bundle
        bottomSheetFragment.show(supportFragmentManager, bottomSheetFragment.tag)
    }
}