package com.io.tazarapp.ui.recycler.history

import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.io.tazarapp.R
import com.io.tazarapp.data.model.Paging
import com.io.tazarapp.data.model.history.HistoryModel
import com.io.tazarapp.data.state.State
import com.io.tazarapp.utils.*
import kotlinx.android.synthetic.main.history_fragment.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class HistoryFragment : BottomSelectedFragment() {
    private val adapter = HistoryAdapter()
    private val viewModel : HistoryViewModel by viewModel()

    private lateinit var scrollLister: EndlessRecyclerOnScrollLister
    private var paging : Paging<HistoryModel>? = null
    private lateinit var manager: LinearLayoutManager
    private var pages = 1
    private var currentPage = 1

    //save state
    private val KEY_RECYCLER_STATE: String? = "recycler_state"
    private var mBundleRecyclerViewState: Bundle? = null
    override var selectedItem = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
            = inflater.inflate(R.layout.history_fragment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    override fun onPause() {
        super.onPause()
        saveState()
        adapter.removeAll()
    }

    private fun init() {
        currentPage = 1

        initRV()
        initViewModel()
        initScrollListener()
        listeners()
    }

    private fun initScrollListener() {
        scrollLister = object : EndlessRecyclerOnScrollLister(manager) {
            override fun update() {
                if(paging != null && paging!!.next != null){
                    pages++
                    saveState()
                    getPages()
                }
            }
        }
    }

    private fun listeners() {
        swipe_container.setOnRefreshListener {
            saveState()
            adapter.removeAll()
            pages = 1
            currentPage = 1
            getPages()
            swipe_container.isRefreshing = false
        }
        history_fragment_rv.addOnScrollListener(scrollLister)
    }


    private fun initViewModel() {
        viewModel.state.observe(viewLifecycleOwner, Observer {
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
                    paging = it.data as Paging<HistoryModel>
                    if (!paging?.results.isNullOrEmpty()) {
                        swipe_container.visible()
                        image_no_history.gone()
                        txt_no_history.gone()
                        paging?.results?.let { it1 -> adapter.updateData(it1) }
                        getPages()
                    } else {
                        swipe_container.gone()
                        image_no_history.visible()
                        txt_no_history.visible()
                    }
                }
            }
        })
        getPages()
    }

    private fun getPages() {
        if (currentPage <= pages) {
            viewModel.getHistories(currentPage)
            currentPage++
        } else {
            if (mBundleRecyclerViewState != null) {
                val listState = mBundleRecyclerViewState!!.getParcelable<Parcelable>(KEY_RECYCLER_STATE)
                history_fragment_rv.layoutManager?.onRestoreInstanceState(listState)
            }
        }
    }

    private fun initRV() {
        manager = LinearLayoutManager(context)
        history_fragment_rv.layoutManager = manager
        history_fragment_rv.adapter = adapter
    }

    private fun saveState() {
        // save RecyclerView state
        mBundleRecyclerViewState = Bundle()
        val listState = history_fragment_rv.layoutManager?.onSaveInstanceState()
        mBundleRecyclerViewState?.putParcelable(KEY_RECYCLER_STATE, listState)

    }
}