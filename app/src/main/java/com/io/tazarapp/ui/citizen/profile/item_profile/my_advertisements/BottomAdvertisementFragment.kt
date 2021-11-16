package com.io.tazarapp.ui.citizen.profile.item_profile.my_advertisements

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.io.tazarapp.R
import com.io.tazarapp.ui.citizen.ad.adEdit.AdEditActivity
import kotlinx.android.synthetic.main.bottom_edit_layout.*

class BottomAdvertisementFragment(
    private val removeEditByID : (Int) -> Unit) : BottomSheetDialogFragment() {
    private var deleteId = -1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_edit_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        deleteId = this.arguments?.getInt("id") as Int

        listeners()
    }

    private fun listeners() {
        btn_edit_fragment_cancel.setOnClickListener {
            dismiss()
        }
        bottom_delete_layout.setOnClickListener {
            removeEditByID(deleteId)
            dismiss()
        }
        bottom_edit_layout.setOnClickListener {
            editAdvertisement(deleteId)
            dismiss()
        }
    }

    private fun editAdvertisement (it : Int) {
        val i = Intent(requireContext(), AdEditActivity::class.java)
        i.putExtra("id",it)
        startActivity(i)
    }
}