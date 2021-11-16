package com.io.tazarapp.ui.citizen.profile.item_profile.about_info

import android.os.Bundle
import android.webkit.WebView
import androidx.lifecycle.Observer
import com.io.tazarapp.R
import com.io.tazarapp.data.model.citizen_info.CitizenInfoModel
import com.io.tazarapp.data.state.State
import com.io.tazarapp.utils.BaseActivity
import com.io.tazarapp.utils.setLocale
import com.io.tazarapp.utils.showFromUrl
import com.io.tazarapp.utils.toast
import kotlinx.android.synthetic.main.custom_toolbar.*
import kotlinx.android.synthetic.main.custom_toolbar.view.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class AboutAppActivity : BaseActivity() {
    override fun handleCustomError(message: String) {
        toast(message)
    }

    private val viewModel: AboutAppViewModel by viewModel()
    var textWeb: WebView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_app)
        setLocale(this)
        inits()
        listeners()
        viewModelInit()
    }

    private fun viewModelInit() {
        viewModel.getAboutInfo()
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
                is State.SuccessObjectState<*> -> {
                    when (it.data) {
                        is CitizenInfoModel -> {
                            textWeb?.showFromUrl(it.data.description)
                            textWeb!!.settings.javaScriptEnabled = true
                        }
                    }
                }
            }
        })
    }

    private fun listeners() {
        toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun inits() {
        setSupportActionBar(toolbar)
        toolbar.custom_title.text = resources.getString(R.string.about_app_title)
        textWeb = findViewById(R.id.wView)
    }
}