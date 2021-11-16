package com.io.tazarapp.ui.shared_ui.fragments.rating

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.io.tazarapp.R
import kotlinx.android.synthetic.main.activity_rating_det.*

class RatingDetActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rating_det)

        val rnds = (0..100).random()

        prog_bar_rating_det.max = 100
        prog_bar_rating_det.progress = rnds

        back_rate_info.setOnClickListener {
            finish()
        }
    }
}