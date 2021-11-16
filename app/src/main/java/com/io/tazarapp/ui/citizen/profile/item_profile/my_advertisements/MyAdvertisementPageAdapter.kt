package com.io.tazarapp.ui.citizen.profile.item_profile.my_advertisements

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.io.tazarapp.ui.citizen.profile.item_profile.my_advertisements.fragments.AdvertisementFragment
import com.io.tazarapp.ui.citizen.profile.item_profile.my_advertisements.fragments.ScoringHistoryFragment

@Suppress("DEPRECATION")
class MyAdvertisementPageAdapter(fm: FragmentManager, openBottomFragment: (Int) -> Unit, private val titles: Array<String>) : FragmentPagerAdapter(fm) {

    private val editFragment = AdvertisementFragment(openBottomFragment)
    private val historyFragment = ScoringHistoryFragment()

    override fun getItem(position: Int): Fragment = when (position) {
        0 -> {
            editFragment
        }
        else -> {
            historyFragment
        }
    }

    override fun getCount() = titles.size

    override fun getPageTitle(position: Int): CharSequence? = titles[position]

    fun removeEditByID(id: Int) {
        editFragment.removeEditByID(id)
    }
}