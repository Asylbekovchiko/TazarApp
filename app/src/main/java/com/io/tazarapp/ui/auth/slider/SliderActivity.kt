package com.io.tazarapp.ui.auth.slider

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.io.tazarapp.R
import com.io.tazarapp.ui.auth.slider.main.SectionsPagerAdapter
import kotlinx.android.synthetic.main.activity_start_slider.*


class SliderActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme2)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_slider)
        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)
        val viewPager: ViewPager = findViewById(R.id.start_pager)
        viewPager.adapter = sectionsPagerAdapter
        indicator.setViewPager(viewPager)
    }

    override fun onBackPressed() {
        moveTaskToBack(true)
    }
}