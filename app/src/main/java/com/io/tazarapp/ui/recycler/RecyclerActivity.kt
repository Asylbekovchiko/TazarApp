package com.io.tazarapp.ui.recycler

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.io.tazarapp.R
import com.io.tazarapp.ui.citizen.news.NewsDetailActivity
import com.io.tazarapp.ui.citizen.news.NewsFragment
import com.io.tazarapp.ui.recycler.history.HistoryFragment
import com.io.tazarapp.ui.recycler.map.MapListFragment
import com.io.tazarapp.ui.recycler.profile.RecProfileFragment
import com.io.tazarapp.ui.recycler.qrcode.QrFragment
import com.io.tazarapp.ui.recycler.qrcode.ScanQRFragment
import com.io.tazarapp.ui.shared_ui.fragments.rating.RatingFragment
import com.io.tazarapp.utils.BottomSelectedFragment
import com.io.tazarapp.utils.setLocale
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_recycler.*
import kotlinx.android.synthetic.main.activity_recycler.bottom_navigation
import java.util.*

class RecyclerActivity : AppCompatActivity() {
    private val historyFragment = HistoryFragment()
    private val mapListFragment = MapListFragment()
    private val scanQRFragment = QrFragment()
    private val newsFragment = NewsFragment()
    private val recProfileFragment = RecProfileFragment()

    private val fragmentStack = ArrayList<BottomSelectedFragment>()

    private var currentFragment: BottomSelectedFragment = recProfileFragment
    override fun onCreate(savedInstanceState: Bundle?) {
        setLocale(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recycler)

        initSelection()
        loadFragment(mapListFragment,false)
        bottom_navigation.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.map -> {
                    title = resources.getString(R.string.map)
                    loadFragment(mapListFragment,true)
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.history -> {
                    title = resources.getString(R.string.history)
                    loadFragment(historyFragment,true)
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.qr_code -> {
                    title = resources.getString(R.string.qr)
                    loadFragment(scanQRFragment,true)
                    return@setOnNavigationItemSelectedListener true
                }
//                R.id.rating_res -> {
//                    title = resources.getString(R.string.top)
//                    loadFragment(RatingFragment())
//                    return@setOnNavigationItemSelectedListener true
//                }
                R.id.news -> {
                    title = resources.getString(R.string.News)
                    loadFragment(newsFragment,true)
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.profile_res -> {
                    title = resources.getString(R.string.profile_title)
                    loadFragment(recProfileFragment,true)
                    return@setOnNavigationItemSelectedListener true
                }
            }
            false

        }

        if (intent.hasExtra("push_id")) {
            bottom_navigation.selectedItemId = R.id.news
            val i = Intent(this, NewsDetailActivity::class.java)
            val id = (intent.getStringExtra("push_id") ?: "-1").toInt()
            i.putExtra("id", id)
            startActivity(i)
        }
    }

    private fun initSelection() {
        historyFragment.selectedItem = R.id.history
        mapListFragment.selectedItem = R.id.map
        scanQRFragment.selectedItem = R.id.qr_code
        recProfileFragment.selectedItem = R.id.profile_res
    }

    override fun onResume() {
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
