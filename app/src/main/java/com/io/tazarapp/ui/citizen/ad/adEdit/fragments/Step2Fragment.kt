package com.io.tazarapp.ui.citizen.ad.adEdit.fragments

import android.content.Context
import android.graphics.drawable.ClipDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.io.tazarapp.R
import com.io.tazarapp.data.model.ad.AdModel
import com.io.tazarapp.ui.citizen.ad.adCreate.adapter.FillScheduleAdapter
import com.io.tazarapp.ui.citizen.ad.adEdit.AdEditActivity
import com.io.tazarapp.ui.citizen.ad.adEdit.IValidate
import kotlinx.android.synthetic.main.step2_fragment.*
import org.koin.android.ext.android.inject

class Step2Fragment : Fragment(R.layout.step2_fragment) {
    private lateinit var scheduleLL: LinearLayoutManager
    private lateinit var scheduleAdapter: FillScheduleAdapter
    private val adModel: AdModel by inject()
    private lateinit var mListener: IValidate

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        initRV()
        updateUI()
        listeners()
    }

    private fun updateUI() = scheduleAdapter.swapData(adModel.schedule)

    private fun listeners() {
        scheduleAdapter.verifyItems = {
            adModel.schedule = scheduleAdapter.getData()
            mListener.onValidate()
        }
    }

    private fun initRV() {
        scheduleAdapter = FillScheduleAdapter(requireActivity().supportFragmentManager)
        scheduleLL = LinearLayoutManager(requireContext())
        val itemDecor = DividerItemDecoration(requireContext(), ClipDrawable.HORIZONTAL)
        with(rv_schedule) {
            layoutManager = scheduleLL
            adapter = scheduleAdapter
            addItemDecoration(itemDecor)
        }
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