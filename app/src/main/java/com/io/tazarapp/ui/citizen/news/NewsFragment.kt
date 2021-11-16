package com.io.tazarapp.ui.citizen.news

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.io.tazarapp.R
import com.io.tazarapp.data.model.Paging
import com.io.tazarapp.data.model.news.NewsModel
import com.io.tazarapp.data.model.partners.PartnersModel
import com.io.tazarapp.data.state.State
import com.io.tazarapp.ui.citizen.news.adapter.NewsAdapter
import com.io.tazarapp.utils.BottomSelectedFragment
import com.io.tazarapp.utils.EndlessRecyclerOnScrollLister
import com.io.tazarapp.utils.gone
import com.io.tazarapp.utils.visible
import kotlinx.android.synthetic.main.custom_title_toolbar.*
import kotlinx.android.synthetic.main.news_fragment.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.ArrayList

class NewsFragment : BottomSelectedFragment() {

    private val viewModel by viewModel<NewsViewModel>()
    private val adapter = NewsAdapter()
    private lateinit var manager: LinearLayoutManager
    override var selectedItem = 0

    private var paging: Paging<NewsModel>? = null
    private lateinit var scrollLister: EndlessRecyclerOnScrollLister

    private var isPageNotAsked = true
    private var currentPage = 1
    private var pages = 1

    //save state
    private val KEY_RECYCLER_STATE: String? = "recycler_state"
    private var mBundleRecyclerViewState: Bundle? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.news_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
    }

    override fun onResume() {
        super.onResume()
        currentPage = 1
        viewModel.setLoading(true)
        viewModel.getNews(1)
    }

    override fun onPause() {
        super.onPause()
        adapter.clear()
        saveState()
    }


    private fun init() {
        initToolbar()
        initRV()
        initVM()
        initListener()
        initPaging()
    }

    private fun initPaging() {
        scrollLister = object : EndlessRecyclerOnScrollLister(manager) {
            override fun update() {
                if (paging != null && paging!!.next != null && isPageNotAsked) {
                    currentPage = paging!!.next!!.toInt()
                    pages = currentPage
                    isPageNotAsked = false
                    viewModel.setLoading(true)
                    viewModel.getNews(currentPage)
                }

            }
        }
        rv_news.addOnScrollListener(scrollLister)
    }

    private fun initListener() {
        adapter.onItemClick = { id ->
            val i = Intent(requireContext(), NewsDetailActivity::class.java)
            i.putExtra("id", id)
            startActivity(i)
        }
    }

    private fun initVM() {
        viewModel.state.observe(viewLifecycleOwner, Observer { state ->
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
                        paging = state.data as Paging<NewsModel>
                        with(paging!!.results) {
                            if (pages >= 1) {
                                adapter.update(this!!)
                                if (currentPage < pages && state.data.next != null) {
                                    rv_news.gone()
                                    currentPage = state.data.next.toInt()
                                    viewModel.getNews(currentPage)
                                } else {
                                    isPageNotAsked = true
                                    viewModel.setLoading(false)
                                    if (mBundleRecyclerViewState != null) {
                                        val listState =
                                            mBundleRecyclerViewState!!.getParcelable<Parcelable>(
                                                KEY_RECYCLER_STATE
                                            )
                                        rv_news.layoutManager?.onRestoreInstanceState(listState)
                                        rv_news.visible()
                                        mBundleRecyclerViewState = null
                                    }
                                }
                            } else {
                                viewModel.setLoading(false)
                                listEmptyChecker(this)
                            }
                        }

                    }
                }
            }
        })
    }

    private fun listEmptyChecker(arrayList: ArrayList<NewsModel>?) {
        if (arrayList.isNullOrEmpty()) {
            rv_news.gone()
            cl_no_news.visible()
        } else {
            rv_news.visible()
            cl_no_news.gone()
        }
    }

    private fun saveState() {
        // save RecyclerView state
        mBundleRecyclerViewState = Bundle()
        val listState = rv_news.layoutManager?.onSaveInstanceState()
        mBundleRecyclerViewState?.putParcelable(KEY_RECYCLER_STATE, listState)
    }


    private fun initRV() {
        manager = LinearLayoutManager(requireContext())
        rv_news.layoutManager = manager
        rv_news.adapter = adapter
    }

    private fun initToolbar() {
        custom_title.text = resources.getText(R.string.News)
    }
}