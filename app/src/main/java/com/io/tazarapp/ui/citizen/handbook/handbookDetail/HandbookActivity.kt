package com.io.tazarapp.ui.citizen.handbook.handbookDetail

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.webkit.WebView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.io.tazarapp.R
import com.io.tazarapp.data.model.citizen_info.HandbookInfoModel
import com.io.tazarapp.data.model.citizen_info.HandbookModel
import com.io.tazarapp.data.state.State
import com.io.tazarapp.ui.citizen.handbook.HandBookViewModel
import com.io.tazarapp.utils.*
import kotlinx.android.synthetic.main.activity_hand_book.*
import kotlinx.android.synthetic.main.custom_toolbar.*
import kotlinx.android.synthetic.main.custom_toolbar.view.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class HandbookActivity : BaseActivity() {
    private val viewModel: HandBookViewModel by viewModel()
    private lateinit var adapterHandbookInfo: HandBookAdapterInfo
    var textWeb: WebView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hand_book)
        initToolbar()
        webViewInit()
    }

    private fun webViewInit() {
        textWeb = findViewById(R.id.tv_handbook_detail)
        val typeId = intent.getIntExtra(ID_TYPE, -1)
        val types = intent.getStringExtra(TYPES)
        if (!types.isNullOrEmpty()) {
            textWeb?.showFromUrl(types)
            textWeb!!.settings.javaScriptEnabled = true
            val title = intent.getStringExtra(TITLE)
            toolbar.custom_title.text = title
        } else {
            initViewModel(typeId)
        }
    }

    override fun onResume() {
        super.onResume()
        val id = intent.getIntExtra(ID, -1)
        viewModel.getHandbookDetail(id)
    }

    private fun initToolbar() {
        val title = intent.getStringExtra(TITLE)
        toolbar.custom_title.text = title
        toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun typeImages(state: HandbookModel) {
        initRV()
        adapterHandbookInfo.update(state.image_guide)
        rv_handbook_detail.visible()
        tv_handbook_detail.gone()
    }

    private fun typeText(state: HandbookModel) {
        tv_handbook_detail.visible()
        rv_handbook_detail.gone()
        textWeb?.showFromUrl(state.description)
        textWeb!!.settings.javaScriptEnabled = true
    }

    private fun initRV() {
        adapterHandbookInfo = HandBookAdapterInfo { item: HandbookInfoModel -> clickItem(item) }
        rv_handbook_detail.layoutManager = GridLayoutManager(this, 2)
        rv_handbook_detail.adapter = adapterHandbookInfo
    }

    private fun clickItem(item: HandbookInfoModel) {
        val intent = Intent(this, HandbookActivity::class.java)
        intent.putExtra(ID_TYPE, 1)
        intent.putExtra(TYPES, item.description)
        intent.putExtra(TITLE, item.title)
        startActivity(intent)
    }

    private fun initViewModel(typeId: Int) {
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
                            is HandbookModel -> {
                                when (typeId) {
                                    1 -> {
                                        typeText(state.data)
                                    }
                                    2 -> {
                                        typeImages(state.data)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        })
    }
}