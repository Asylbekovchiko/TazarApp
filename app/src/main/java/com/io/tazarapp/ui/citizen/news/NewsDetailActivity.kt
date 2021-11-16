package com.io.tazarapp.ui.citizen.news

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.io.tazarapp.R
import com.io.tazarapp.data.model.news.NewsDetailModel
import com.io.tazarapp.data.state.State
import com.io.tazarapp.utils.*
import kotlinx.android.synthetic.main.activity_news_detail.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class NewsDetailActivity : BaseActivity() {
    private val viewModel by viewModel<NewsViewModel>()
    private var id : Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news_detail)

        init()
    }

    override fun onResume() {
        super.onResume()
        if (id == -1) {
            finish()
        } else {
            viewModel.getNewsDetail(id)
        }
    }

    private fun init() {
        getExtras()
        initViewModel()
        initListener()
    }

    private fun initListener() {
        tb_news.setNavigationOnClickListener {
            finish()
        }
    }


    private fun getExtras() {
        if (intent.hasExtra("id")) {
            id = intent.getIntExtra("id", -1)
        }
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
                        initUI(state.data as NewsDetailModel)
                    }
                }
            }
        })
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initUI(newsDetailModel: NewsDetailModel) {
        if (newsDetailModel.image != null) {
            Glide.with(this)
                .load(newsDetailModel.image)
                .into(iv_news)
        } else {
            iv_news.gone()
        }

        tv_title.text = newsDetailModel.title
        tv_date.text = newsDetailModel.created_at
        wv_data.showFromUrlF2F1F4(newsDetailModel.description)
        wv_data.settings.javaScriptEnabled = true
        setLocale(this)
    }

}