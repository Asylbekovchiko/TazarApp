package com.io.tazarapp.utils

import android.widget.AbsListView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

abstract class EndlessRecyclerOnScrollLister(
    private val manager: LinearLayoutManager
) : RecyclerView.OnScrollListener() {
    private var isScrolling = false
    private var currentItems = 0
    private var totalItems = 0
    private var scrollOutItems = 0


    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        super.onScrollStateChanged(recyclerView, newState)
        if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
            isScrolling = true
        }
    }

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        currentItems = manager.childCount
        totalItems = manager.itemCount
        scrollOutItems  = manager.findFirstVisibleItemPosition()

        if (isScrolling && (currentItems + scrollOutItems == totalItems)){
            isScrolling = false
            update()
        }
    }

    abstract fun update()
}