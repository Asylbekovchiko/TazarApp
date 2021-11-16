package com.io.tazarapp.ui.citizen.partners

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
import com.io.tazarapp.data.model.partners.PartnersModel
import com.io.tazarapp.data.state.State
import com.io.tazarapp.ui.citizen.partners.partnerInfo.PartnersInfoActivity
import com.io.tazarapp.utils.*
import kotlinx.android.synthetic.main.custom_toolbar.*
import kotlinx.android.synthetic.main.custom_toolbar.view.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class PartnersFragment : BottomSelectedFragment() {
    override var selectedItem = 0

    override fun handleCustomError(message:String) {
    }

    private lateinit var adapterPartners: PartnersAdapter
    private val viewModel: PartnersViewModel by viewModel()
    private var recyclerPartners: RecyclerView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_partners, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        partnersInit(view)
        initViewModels()
        initToolbar()
    }

    private fun initToolbar() {
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        toolbar.custom_title.text = resources.getString(R.string.partners)
    }

    private fun initViewModels() {
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
                            state.data.all { it is PartnersModel }  -> {
                                adapterPartners.update(state.data as ArrayList<PartnersModel>)
                            }
                        }
                    }
                }
            }
        })
    }


    private fun partnersInit(view: View) {
        recyclerPartners = view.findViewById(R.id.recycler_main_item)
        adapterPartners =
            PartnersAdapter { item: PartnersModel ->
                clickItemMain(item)
            }
        recyclerPartners?.layoutManager =
            LinearLayoutManager(this.context)
        recyclerPartners?.adapter = adapterPartners
    }

    private fun clickItemMain(itemView: PartnersModel) {
        val intent = Intent(this.requireContext(), PartnersInfoActivity::class.java)
        intent.putExtra(PARTNERS_ID, itemView.id)
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        viewModel.getPartners()
        setLocale(requireContext())
    }

}
