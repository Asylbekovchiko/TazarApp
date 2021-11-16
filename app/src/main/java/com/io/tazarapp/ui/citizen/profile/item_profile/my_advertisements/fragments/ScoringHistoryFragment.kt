package com.io.tazarapp.ui.citizen.profile.item_profile.my_advertisements.fragments

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
import com.io.tazarapp.data.model.advertisement.my_advertisement.ScoringHistoryModel
import com.io.tazarapp.data.state.State
import com.io.tazarapp.ui.citizen.profile.item_profile.my_advertisements.adapters.ScoringHistoryFragmentAdapter
import com.io.tazarapp.ui.citizen.profile.item_profile.my_advertisements.viewmodel.ScoringHistoryFragmentViewModel
import com.io.tazarapp.utils.*
import kotlinx.android.synthetic.main.scoring_history_fragment.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class ScoringHistoryFragment : BaseFragment() {
    private val adapter = ScoringHistoryFragmentAdapter()
    private val viewModel : ScoringHistoryFragmentViewModel by viewModel()

    private var paging : Paging<ScoringHistoryModel>? = null
    private lateinit var manager: LinearLayoutManager
    private var pages = 1
    private lateinit var scrollLister : EndlessRecyclerOnScrollLister

    //save state
    private val KEY_RECYCLER_STATE: String? = "recycler_state"
    private var mBundleRecyclerViewState: Bundle? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.scoring_history_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        initRV()
        initViewModel()
        initScrollListener()
        setLocale(requireActivity())
        getFirstPage()
    }

    override fun onResume() {
        super.onResume()
    }

    private fun initScrollListener() {
        scrollLister = object : EndlessRecyclerOnScrollLister(manager) {
            override fun update() {
                if(paging != null && paging!!.next != null){
                    //before update we get next page to path (@@@currentPage <= pages) -> if)
                    pages++
                    saveState()
                    getNextPage()
                }
            }
        }
        rv.addOnScrollListener(scrollLister)
    }

    override fun onPause() {
        saveState()
        super.onPause()
    }

    private fun initViewModel() {
        viewModel.state.observe(viewLifecycleOwner, Observer { state ->
            if (state != null) {
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
                        paging = state.data as Paging<ScoringHistoryModel>
                        update()
                        getNextPage()
                    }
                }
            } else {
                Log.e("Empty", "Item class is empty")
            }
        })
    }

    private fun getFirstPage() {
        adapter.clearData()
        viewModel.getAllUserScoringHistory(1)
    }

    private fun saveState() {
        if (rv != null){
            mBundleRecyclerViewState = Bundle()
            val listState = rv.layoutManager?.onSaveInstanceState()
            mBundleRecyclerViewState?.putParcelable(KEY_RECYCLER_STATE,listState)
        }
    }

    private fun getNextPage() {
        if (paging != null && paging!!.next != null && paging!!.next!! <= pages) {
            viewModel.getAllUserScoringHistory(paging!!.next!!)
            return
        }
        adapter.notifyDataSetChanged()
        if(checkAdapterSize()) {
            recoveryState()
        }
    }

    private fun recoveryState() {
        if (mBundleRecyclerViewState != null) {
            val listState = mBundleRecyclerViewState!!.getParcelable<Parcelable>(KEY_RECYCLER_STATE)
            rv.layoutManager?.onRestoreInstanceState(listState)
        }
    }

    private fun update() {
        if (!paging?.results.isNullOrEmpty()) {
            paging?.results?.let { it1 -> adapter.updateData(it1) }
        }
    }

    private fun checkAdapterSize() = if(adapter.itemCount == 0) {
        image_no_history.visible()
        text_no_history.visible()
        false
    }else {
        image_no_history.gone()
        text_no_history.gone()
        true
    }

    private fun initRV() {
        manager = LinearLayoutManager(context)
        rv.layoutManager = manager
        rv.adapter = adapter
    }
}