package com.io.tazarapp.ui.recycler.qrcode.found

import android.annotation.SuppressLint
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
import com.io.tazarapp.utils.boom
import kotlinx.android.synthetic.main.success_bottom_sheet_layout.*
import kotlinx.android.synthetic.main.success_bottom_sheet_layout.btn_save

class FoundSuccessBottomSheetFragment(private val finish : () -> Unit) : BottomSheetDialogFragment() {
    private var point = 0
    private var name = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bundle = this.arguments
        if (bundle != null) {
            point = bundle.getInt("point", 0)
            name = bundle.getString("name","")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.success_bottom_sheet_layout, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        title.text = "$point " + resources.getString(R.string.Points)
        sub_title.text = resources.getString(R.string.Have_been_credited) + " " + name
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

    private fun listeners() = btn_save.setOnClickListener {
        finish()
        dismiss()
    }
}