package com.io.tazarapp.ui.shared_ui.fragments.rating

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.io.tazarapp.R
import com.io.tazarapp.ui.shared_ui.fragments.rating.company.CompanyFragment
import com.io.tazarapp.ui.shared_ui.fragments.rating.user.UserFragment


private val TAB_TITLES = arrayOf(
    R.string.tab_company,
    R.string.tab_users
)

class CBSPAdapterTab(
    private val context: FragmentActivity, fm: FragmentManager
) : FragmentPagerAdapter(fm) {
    private val push: String = ""
    private val tabStatistic =
        CompanyFragment()
    private var tabRBTH = UserFragment()

    override fun getItem(position: Int): Fragment {
        val bundle1 = Bundle()
        when (position) {
            0 -> {
                tabStatistic.arguments = bundle1
                return tabStatistic
            }
            1 -> {
                tabRBTH.arguments = bundle1
                return tabRBTH
            }
            else -> null!!
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return context.resources?.getString(TAB_TITLES[position])
    }

    override fun getCount(): Int {
        return 2
    }
}