package com.io.tazarapp.ui.citizen.handbook

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.io.tazarapp.R
import com.io.tazarapp.data.model.citizen_info.HandbookModel
import com.io.tazarapp.data.state.State
import com.io.tazarapp.ui.citizen.handbook.handbookDetail.HandbookActivity
import com.io.tazarapp.utils.*
import kotlinx.android.synthetic.main.custom_toolbar.*
import kotlinx.android.synthetic.main.custom_toolbar.view.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class HandBookFragment : BottomSelectedFragment() {
    override var selectedItem = 0

    private val viewModel: HandBookViewModel by viewModel()
    private lateinit var adapterHandbook: HandBookAdapter
    private var recyclerView: RecyclerView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_hand_book, container, false)
        initRV(root)
        initViewModel()
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar()
    }
    private fun initToolbar() {
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        toolbar.custom_title.text = resources.getString(R.string.directory)
    }


    private fun initViewModel() {
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
                    is State.SuccessListState<*> -> {
                        when {
                            state.data.all { it is HandbookModel } -> {
                                adapterHandbook.update(state.data as ArrayList<HandbookModel>)
                            }
                        }
                    }
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()
        viewModel.getHandbook()
        setLocale(requireContext())
    }

    private fun initRV(root: View) {
        recyclerView = root.findViewById(R.id.rv_handbook)
        adapterHandbook = HandBookAdapter { item: HandbookModel -> clickItem(item) }
        recyclerView?.layoutManager = LinearLayoutManager(requireContext())
        recyclerView?.adapter = adapterHandbook
    }

    private fun clickItem(item: HandbookModel) {
        val intent = Intent(this.requireContext(), HandbookActivity::class.java)
        intent.putExtra(ID, item.id)
        intent.putExtra(ID_TYPE, item.type_guide)
        intent.putExtra(TITLE, item.name)
        startActivity(intent)
    }
}