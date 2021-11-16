package com.io.tazarapp.ui.citizen.filter.view

import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.io.tazarapp.data.model.filter.FilterCategoryModel
import com.io.tazarapp.ui.citizen.filter.view.parts.FilterPartsFragment

@Suppress("DEPRECATION")
class FilterTabsAdapter(fm : FragmentManager) : FragmentPagerAdapter(fm) {
    val fragmentList = ArrayList<FilterPartsFragment>()
    private val fragmentTitleList = ArrayList<String>()

    fun clear() {
        fragmentList.clear()
        fragmentTitleList.clear()
    }
    override fun getItem(position: Int) = fragmentList[position]

    override fun getCount() = fragmentList.size

    override fun getPageTitle(position: Int): CharSequence? {
        return fragmentTitleList[position]
    }

    fun addFragment(fragment: FilterPartsFragment, category : FilterCategoryModel) {
        fragmentList.add(fragment)
        fragmentTitleList.add(category.name)
    }
}