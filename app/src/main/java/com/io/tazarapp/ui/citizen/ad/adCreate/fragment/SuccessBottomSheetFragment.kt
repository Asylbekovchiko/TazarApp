package com.io.tazarapp.ui.citizen.ad.adCreate.fragment

import android.graphics.drawable.AnimatedVectorDrawable
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.io.tazarapp.R
import com.io.tazarapp.utils.*
import kotlinx.android.synthetic.main.success_bottom_sheet_layout.*

class SuccessBottomSheetFragment(private val success: () -> Unit) : BottomSheetDialogFragment() {
    private var mView: View? = null
    private var isNewPoint = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bundle = this.arguments
        if (bundle != null) {
            isNewPoint = bundle.getBoolean("point", false)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mView = inflater.inflate(R.layout.success_bottom_sheet_layout, container, false)
        return mView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (isNewPoint) {
            title.text = requireActivity().getString(R.string.success_send)
            sub_title.gone()
        } else {
            title.text = requireActivity().getString(R.string.success_added)
            sub_title.text = requireActivity().getString(R.string.success_added_sub_title)
        }
        image.setImageResource(R.drawable.animated_done)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val drawable = image.drawable as AnimatedVectorDrawable
            drawable.start()
        } else {
            val drawable = image.drawable as AnimatedVectorDrawableCompat
            drawable.start()
        }
        boom(btn_save)
        listeners()

    }

    private fun listeners() = btn_save.setOnClickListener { dismiss() }

    override fun onDestroyView() {
        mView = null
        super.onDestroyView()
        success.invoke()

//        val token = SharedPrefModule(requireContext()).TokenModule().getToken()
//        if(token != null) {
//            when (token.user_type) {
//                CITIZEN -> {
//                    val i = Intent(requireContext(), CitizenActivity::class.java)
//                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
//                    startActivity(i)
//                }
//                PROCESSOR -> {
//                    val i = Intent(requireContext(), RecyclerActivity::class.java)
//                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
//                    startActivity(i)
//                }
//            }
//        }
    }
}