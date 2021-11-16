package com.io.tazarapp.ui.citizen.profile.item_profile.qr_info

import android.content.Intent
import android.graphics.drawable.AnimatedVectorDrawable
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.io.tazarapp.R
import com.io.tazarapp.ui.citizen.CitizenActivity
import com.io.tazarapp.utils.POINT
import com.io.tazarapp.utils.boom
import kotlinx.android.synthetic.main.qr_bottom_sheet_layout.*

class QrBottomSheetFragment : BottomSheetDialogFragment() {
    private var mView: View? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mView = inflater.inflate(R.layout.qr_bottom_sheet_layout, container, false)
        return mView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bundle = this.arguments
        if (bundle != null) {
            val point = bundle.getInt(POINT, -1).toString() + getString(R.string.space_points)
            tv_get_prize_score.text = point
        }
        image.setImageResource(R.drawable.animated_done)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val drawable = image.drawable as AnimatedVectorDrawable
            drawable.start()
        } else {
            val drawable = image.drawable as AnimatedVectorDrawableCompat
            drawable.start()
        }
        boom(btn_bot_sh_close)
        listeners()
    }

    private fun listeners() = btn_bot_sh_close.setOnClickListener { dismiss() }

    override fun onDestroyView() {
        mView = null
        super.onDestroyView()
        val i = Intent(requireContext(), CitizenActivity::class.java)
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(i)
    }
}