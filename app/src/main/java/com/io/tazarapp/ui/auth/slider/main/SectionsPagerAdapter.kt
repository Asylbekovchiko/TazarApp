package com.io.tazarapp.ui.auth.slider.main

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class SectionsPagerAdapter(private val context: Context, fm: FragmentManager) :
    FragmentPagerAdapter(fm) {
    override fun getItem(position: Int): Fragment {
        return Placeholder.newInstance(position + 1)
    }

    override fun getCount(): Int {
        return 3
    }
}