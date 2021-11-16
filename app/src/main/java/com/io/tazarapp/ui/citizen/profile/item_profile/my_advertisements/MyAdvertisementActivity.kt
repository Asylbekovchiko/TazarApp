package com.io.tazarapp.ui.citizen.profile.item_profile.my_advertisements

import android.os.Bundle
import android.view.MenuItem
import com.io.tazarapp.R
import com.io.tazarapp.utils.BaseActivity
import com.io.tazarapp.utils.setLocale
import kotlinx.android.synthetic.main.activity_my_edits.*
import kotlinx.android.synthetic.main.custom_toolbar.*
import kotlinx.android.synthetic.main.custom_toolbar.view.*

class MyAdvertisementActivity : BaseActivity() {
    private lateinit var adapter: MyAdvertisementPageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_edits)
        init()
    }

    override fun onResume() {
        setLocale(this)
        super.onResume()
    }

    private fun init() {
        initToolbar()
        inTabLayout()
    }

    private fun inTabLayout() {
        val titles = this.resources.getStringArray(R.array.my_edit_titles)
        adapter = MyAdvertisementPageAdapter(supportFragmentManager, openBottomFragment, titles)
        my_edits_view_pager.adapter = adapter
        my_edits_tabs.setupWithViewPager(my_edits_view_pager)
    }

    private fun initToolbar() {
        setSupportActionBar(toolbar)
        toolbar.custom_title.text = resources.getString(R.string.my_edits_title)
    }

    private val openBottomFragment : (Int) -> Unit = {
        val fragment = BottomAdvertisementFragment(removeEditByID)
        val bundle = Bundle()

        bundle.putInt("id",it)
        fragment.arguments = bundle

        fragment.show(supportFragmentManager, fragment.tag)
    }

    private val removeEditByID : (Int) -> Unit = {
        adapter.removeEditByID(it)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        finish()
        return super.onOptionsItemSelected(item)
    }
}