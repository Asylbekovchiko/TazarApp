package com.io.tazarapp.ui.citizen

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.io.tazarapp.R
import com.io.tazarapp.ui.citizen.handbook.HandBookFragment
import com.io.tazarapp.ui.citizen.map.MapListFragment
import com.io.tazarapp.ui.citizen.news.NewsDetailActivity
import com.io.tazarapp.ui.citizen.news.NewsFragment
import com.io.tazarapp.ui.citizen.partners.PartnersFragment
import com.io.tazarapp.ui.citizen.profile.ProfileFragment
import com.io.tazarapp.utils.BottomSelectedFragment
import com.io.tazarapp.utils.setLocale
import kotlinx.android.synthetic.main.activity_main.*
import java.util.ArrayList

class CitizenActivity : AppCompatActivity() {
    private var partnersFragment = PartnersFragment()
    private var handBookFragment = HandBookFragment()
    private var mapListFragment = MapListFragment()
    private var profileFragment = ProfileFragment()
    private var newsFragment = NewsFragment()
    private var currentFragment : BottomSelectedFragment = handBookFragment

    private val fragmentStack = ArrayList<BottomSelectedFragment>()
    private var isInByPush = false

    override fun onCreate(savedInstanceState: Bundle?) {
        setLocale(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initSelection()
        bottom_navigation.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.map -> {
                    title = resources.getString(R.string.map)
                    loadFragment(mapListFragment,true)
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.partners -> {
                    title = resources.getString(R.string.main)
                    loadFragment(partnersFragment,true)
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.handbook -> {
                    title = resources.getString(R.string.directory)
                    loadFragment(handBookFragment,true)
                    return@setOnNavigationItemSelectedListener true
                }
//                R.id.rating -> {
//                    title = resources.getString(R.string.top)
//                    loadFragment(RatingFragment())
//                    return@setOnNavigationItemSelectedListener true
//                }
                R.id.news -> {
                    title = resources.getString(R.string.News)
                    loadFragment(newsFragment,true)
                    return@setOnNavigationItemSelectedListener true
                }

                R.id.profile -> {
                    title = resources.getString(R.string.profile_title)
                    loadFragment(profileFragment,true)
                    return@setOnNavigationItemSelectedListener true
                }
            }
            false
        }

        if (intent.hasExtra("push_id")) {
            val i = Intent(this, NewsDetailActivity::class.java)
            val id = (intent.getStringExtra("push_id") ?: "-1").toInt()
            i.putExtra("id", id)
            isInByPush = true
            startActivity(i)
        } else {
            loadFragment(mapListFragment,true)
        }
    }

    private fun initSelection() {
        partnersFragment.selectedItem = R.id.partners
        handBookFragment.selectedItem = R.id.handbook
        mapListFragment.selectedItem = R.id.map
        newsFragment.selectedItem = R.id.news
        profileFragment.selectedItem = R.id.profile
    }

    override fun onResume() {
        if (isInByPush) {
            loadFragment(newsFragment, true)
            val item = bottom_navigation.findViewById<View>(R.id.news)
            item.performClick()
            isInByPush = false
        }
        super.onResume()
    }

    private fun loadFragment(fragment: BottomSelectedFragment, isAdd : Boolean) {
        if (currentFragment != fragment) {
            if (isAdd) fragmentStack.add(currentFragment)

            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.container, fragment)
            transaction.addToBackStack(null)
            transaction.commit()
            currentFragment = fragment
        }
    }

    @OptIn(ExperimentalStdlibApi::class)
    override fun onBackPressed() {
        if (fragmentStack.isNotEmpty()) {
            val last = fragmentStack.removeLast()
            loadFragment(last,false)
            bottom_navigation.selectedItemId = last.selectedItem
        } else {
            finish()
        }
    }
}
