package com.io.tazarapp.ui.citizen.ad.adCreate.view

import android.content.Intent
import android.graphics.drawable.ClipDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.io.tazarapp.R
import com.io.tazarapp.data.model.ad.AdModel
import com.io.tazarapp.data.model.ad.ScheduleItem
import com.io.tazarapp.data.model.collectionplace.CollectionPointModel
import com.io.tazarapp.ui.citizen.ad.adCreate.adapter.FillScheduleAdapter
import com.io.tazarapp.utils.ActivityWithCleanBack
import com.io.tazarapp.utils.boom
import kotlinx.android.synthetic.main.activity_step2.*
import kotlinx.android.synthetic.main.adcreate_toolbar.*
import kotlinx.android.synthetic.main.adcreate_toolbar.view.*
import org.koin.android.ext.android.inject


class Step2Activity : ActivityWithCleanBack() {
    private lateinit var scheduleLL: LinearLayoutManager
    private lateinit var scheduleAdapter: FillScheduleAdapter
    private val adModel: AdModel by inject()
    private val cpModel: CollectionPointModel by inject()
    private var isNewPoint = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_step2)
        btn_next.setBackgroundResource(R.drawable.rounded_shape_green_12dp_8cc341)

        isNewPoint = intent.getBooleanExtra("point", false)
        init()
    }

    private fun init() {
        initToolbar()
        initRV()
        listeners()
        boom(btn_next)
    }

    private fun listeners() {
        toolbar.setNavigationOnClickListener { finish() }


        btn_next.setOnClickListener {
            if (isNewPoint) {
                cpModel.cp_schedule = scheduleAdapter.getData()
            } else {
                adModel.schedule = scheduleAdapter.getData()
            }

            val i = Intent(this, Step4Activity::class.java)
            i.putExtra("point", isNewPoint)
            startActivityWithCleanBack(i)
        }
    }

    private fun initRV() {
        scheduleAdapter = FillScheduleAdapter(supportFragmentManager)
        scheduleLL = LinearLayoutManager(this)
        val itemDecor = DividerItemDecoration(this, ClipDrawable.HORIZONTAL)
        with(rv_schedule) {
            layoutManager = scheduleLL
            adapter = scheduleAdapter
            addItemDecoration(itemDecor)
        }
        val list = ArrayList<ScheduleItem>()
        val weekSlugs = resources.getIntArray(R.array.week)

        weekSlugs.forEach {
            val item = ScheduleItem()
            item.name = it
            list.add(item)
        }
        scheduleAdapter.swapData(list)
    }

    private fun initToolbar() {
        setSupportActionBar(toolbar)
        toolbar.custom_title.text = getString(R.string.step2_title)
        toolbar.sub_title.text = getString(R.string.step2_sub_title)
    }
}