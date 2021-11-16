package com.io.tazarapp.ui.recycler.schedule

import android.graphics.drawable.ClipDrawable
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.io.tazarapp.R
import com.io.tazarapp.data.model.ad.ScheduleItem
import com.io.tazarapp.data.model.profile.ProfileRecyclerModel
import com.io.tazarapp.data.model.profile.ScheduleModel
import com.io.tazarapp.data.state.State
import com.io.tazarapp.ui.citizen.ad.adCreate.adapter.FillScheduleAdapter
import com.io.tazarapp.utils.BaseActivity
import com.io.tazarapp.utils.boom
import com.io.tazarapp.utils.toast
import kotlinx.android.synthetic.main.activity_schedule.*
import kotlinx.android.synthetic.main.activity_schedule.rv_schedule
import kotlinx.android.synthetic.main.custom_toolbar.toolbar
import kotlinx.android.synthetic.main.custom_toolbar.view.custom_title
import okhttp3.ResponseBody
import org.koin.androidx.viewmodel.ext.android.viewModel

class ScheduleActivity : BaseActivity() {

    private lateinit var scheduleLL: LinearLayoutManager
    private lateinit var scheduleAdapter: FillScheduleAdapter
    private val viewModel: ScheduleViewModel by viewModel()
    var profile: ProfileRecyclerModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule)
        init()
    }

    private fun init() {
        initToolbar()
        initRV()
        listeners()
        initViewModel()
        boom(btn_save)
    }

    private fun initViewModel() {
        viewModel.state.observe(this, Observer { state ->
            if (state != null) {
                when (state) {
                    is State.LoadingState -> {
                        when {
                            state.isLoading -> {
                                showDialog()
                                btn_save.isEnabled = false
                            }
                            else -> {
                                hideDialog()
                                btn_save.isEnabled = true
                            }
                        }
                    }
                    is State.ErrorState -> handleError(state)
                    is State.SuccessObjectState<*> -> {
                        when (state.data) {
                            is ProfileRecyclerModel -> {
                                profile = state.data
                                if (state.data.user_schedule.isNullOrEmpty()) {
                                    val list = ArrayList<ScheduleItem>()
                                    val weekSlugs = resources.getIntArray(R.array.week)

                                    weekSlugs.forEach {
                                        val item = ScheduleItem()
                                        item.name = it

                                        list.add(item)
                                    }
                                    scheduleAdapter.swapData(list)
                                } else {
                                    updateUI(state.data.user_schedule)
                                }
                            }
                            is ResponseBody -> toast(R.string.success)
                        }
                    }
                }
            }
        })
        viewModel.getMainProfile()
    }

    private fun updateUI(list: ArrayList<ScheduleItem>) = scheduleAdapter.swapData(list)

    private fun listeners() {
        toolbar.setNavigationOnClickListener { finish() }


        btn_save.setOnClickListener {
            val model = ScheduleModel()
            model.user_schedule = scheduleAdapter.getData()
            viewModel.updateSchedule(model, profile?.id)
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
    }

    private fun initToolbar() {
        setSupportActionBar(toolbar)
        toolbar.custom_title.text = getString(R.string.step2_title)
    }
}