package com.io.tazarapp.ui.citizen.ad.adEdit.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.io.tazarapp.R
import com.io.tazarapp.data.model.City
import com.io.tazarapp.data.model.ad.AdModel
import com.io.tazarapp.data.state.State
import com.io.tazarapp.ui.citizen.ad.adCreate.adapter.Step4Adapter
import com.io.tazarapp.ui.citizen.ad.adCreate.viewModel.AdCreateViewModel
import com.io.tazarapp.ui.citizen.ad.adEdit.AdEditActivity
import com.io.tazarapp.ui.citizen.ad.adEdit.IValidate
import com.io.tazarapp.utils.BaseFragment
import com.io.tazarapp.utils.toast
import kotlinx.android.synthetic.main.step4_fragment.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class Step4Fragment : BaseFragment() {

    private val adModel: AdModel by inject()
    private lateinit var adapter: Step4Adapter
    private val viewModel: AdCreateViewModel by viewModel()
    private lateinit var mListener: IValidate

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.step4_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        initRV()
        initViewModel()
        listeners()
    }

    private fun listeners() {
        sv_step4.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter(newText)
                return true
            }
        })

        adapter.update = { checkedItemId ->
            adModel.city = adapter.getCheckedItem()?.id
            mListener.onValidate()
            if (checkedItemId == null) {
                btn_next.isEnabled = false
                btn_next.setBackgroundResource(R.drawable.rounded_shape_silver_12dp_7b818c)
            } else {
                btn_next.isEnabled = true
                btn_next.setBackgroundResource(R.drawable.rounded_shape_green_12dp_8cc341)
            }
        }
    }

    private fun initViewModel() {
        viewModel.state.observe(requireActivity(), Observer {
            when (it) {
                is State.LoadingState -> {
                    when {
                        it.isLoading -> showDialog()
                        else -> hideDialog()
                    }
                }

                is State.ErrorState -> {
                    requireContext().toast(it.message)
                }

                is State.SuccessListState<*> -> {
                    when {
                        it.data.all { item -> item is City } -> {
                            adapter.swapData(it.data as ArrayList<City>)
                            adapter.setCheckedItemId(adModel.city!!)
                        }
                    }
                }
            }
        })
        viewModel.getCities()
    }

    private fun initRV() {
        adapter = Step4Adapter()
        rv_step4.layoutManager = LinearLayoutManager(requireContext())
        rv_step4.adapter = adapter
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            mListener = requireActivity() as AdEditActivity
        } catch (e: ClassCastException) {
            throw ClassCastException("Error")
        }
    }
}