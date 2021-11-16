package com.io.tazarapp.ui.citizen.profile.item_profile.my_advertisements.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.io.tazarapp.R
import com.io.tazarapp.data.model.Paging
import com.io.tazarapp.data.model.advertisement.my_advertisement.MyAdvertisementModel
import com.io.tazarapp.data.state.State
import com.io.tazarapp.ui.citizen.profile.item_profile.my_advertisements.adapters.AdvertisementFragmentAdapter
import com.io.tazarapp.ui.citizen.profile.item_profile.my_advertisements.viewmodel.AdvertisementFragmentViewModel
import com.io.tazarapp.utils.BaseFragment
import com.io.tazarapp.utils.EndlessRecyclerOnScrollLister
import com.io.tazarapp.utils.gone
import com.io.tazarapp.utils.visible
import kotlinx.android.synthetic.main.edits_fragment.*
import okhttp3.ResponseBody
import org.koin.androidx.viewmodel.ext.android.viewModel


class AdvertisementFragment(openBottomFragment: (Int) -> Unit) : BaseFragment() {
    private val adapter = AdvertisementFragmentAdapter(openBottomFragment)
    private val viewModel : AdvertisementFragmentViewModel by viewModel()

    private var deletedID = 0
    //save state
    private val KEY_RECYCLER_STATE: String? = "recycler_state"
    private var mBundleRecyclerViewState: Bundle? = null

    private var paging : Paging<MyAdvertisementModel>? = null
    private lateinit var scrollLister : EndlessRecyclerOnScrollLister
    private lateinit var manager: LinearLayoutManager
    private var pages = 1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.edits_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        init()
    }

    override fun onResume() {
        super.onResume()

        getFirstPage()
    }

    override fun onPause() {
        saveState()
        super.onPause()
    }

    private fun init() {
        initRV()
        initViewModel()
        initScrollListener()
    }

    private fun initScrollListener() {
        scrollLister = object : EndlessRecyclerOnScrollLister(manager) {
            override fun update() {
                if(paging != null && paging!!.next != null){
                    //before update we get next page to path (@@@currentPage <= pages) -> if)
                    pages++
                    saveState()
                    getNextPages()
                }
            }
        }
        edit_fragment_rv.addOnScrollListener(scrollLister)
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
                        when (state.data) {
                            is ResponseBody? -> {
                                saveState()
                                adapter.removeByID(deletedID)
                                Handler().postDelayed({
                                    // it must be 1 , because we going to download from first page
                                    // remove all elements in adapter to
                                    getFirstPage()
                                },500)
                            }
                            is Paging<*> -> {
                                paging = state.data as Paging<MyAdvertisementModel>
                                update()
                                getNextPages()
                            }
                        }
                    }
                }
            } else {
                Log.e("Empty", "Item class is empty")
            }
        })
    }
    private fun update() {
        if (!paging?.results.isNullOrEmpty()) {
            paging?.results?.let { it1 -> adapter.updateData(it1) }
        }
    }

    private fun getFirstPage () {
        adapter.clearData()
        viewModel.getAllUserAds(1)
    }

    private fun getNextPages() {
        if (paging != null && paging!!.next != null && paging!!.next!! <= pages){
            //(@@@currentPage <= pages) to path here
            viewModel.getAllUserAds(paging!!.next!!)
            return
        }
        adapter.notifyDataSetChanged()
        if (checkAdapterSize()) {
            recoveryState()
        }
    }

    private fun recoveryState() {
        // when all pages downloaded we're going to get saved state
        if (mBundleRecyclerViewState != null) {
            val listState = mBundleRecyclerViewState!!.getParcelable<Parcelable>(KEY_RECYCLER_STATE)
            edit_fragment_rv.layoutManager?.onRestoreInstanceState(listState)
        }
    }
    private fun checkAdapterSize()= if (adapter.itemCount == 0) {
        image_no_ads.visible()
        text_no_ads.visible()
        false
    }else {
        image_no_ads.gone()
        text_no_ads.gone()
        true
    }

    private fun saveState() {
        mBundleRecyclerViewState = Bundle()
        val listState = edit_fragment_rv.layoutManager?.onSaveInstanceState()
        mBundleRecyclerViewState?.putParcelable(KEY_RECYCLER_STATE,listState)
    }

    private fun initRV() {
        manager = LinearLayoutManager(context)
        edit_fragment_rv.layoutManager = manager
        edit_fragment_rv.adapter = adapter
    }

    fun removeEditByID(id: Int) {
        deletedID = id
        viewModel.deleteMyEditById(id)
    }
}