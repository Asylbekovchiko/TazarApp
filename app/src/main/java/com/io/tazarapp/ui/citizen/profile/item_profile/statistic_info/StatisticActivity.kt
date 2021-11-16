package com.io.tazarapp.ui.citizen.profile.item_profile.statistic_info

import android.graphics.Color
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.MPPointF
import com.io.tazarapp.R
import com.io.tazarapp.data.model.statistics.StatisticData
import com.io.tazarapp.data.state.State
import com.io.tazarapp.utils.*
import kotlinx.android.synthetic.main.activity_statistic.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class StatisticActivity : BaseActivity() {
    private val colors = ArrayList<Int>()
    private lateinit var adapterStatistic: StatisticAdapter
    private val viewModel: StatisticsViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_statistic)
        setLocale(this)
        initRV()
        initToolbar()
        listeners()
        viewModelInit()
    }

    private fun listeners() {
        toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    override fun handleCustomError(message: String) {
        toast(message)
    }

    private fun initRV() {
        adapterStatistic = StatisticAdapter()
        rv_statistic.layoutManager =
            LinearLayoutManager(this)
        rv_statistic.adapter = adapterStatistic
    }

    private fun initToolbar() {
        setSupportActionBar(toolbar)
        custom_title.text = getString(R.string.sales_statistics)
    }

    private fun viewModelInit() {
        viewModel.getStatistics()
        viewModel.state.observe(this, Observer { state ->
            when (state) {
                is State.LoadingState -> {
                    when {
                        state.isLoading -> showDialog()
                        else -> hideDialog()
                    }
                }
                is State.ErrorState -> {
                    handleError(state)
                }
                is State.SuccessListState<*> -> {
                    when {
                        state.data.all { it is StatisticData } -> {
                            val statistics = state.data as ArrayList<StatisticData>
                            if ( statistics.isEmpty() ) {
                                piechart.gone()
                                rv_statistic.gone()
                                image_bg.visible()
                                txt_bg.visible()
                            } else {
                                piechart.visible()
                                rv_statistic.visible()
                                image_bg.gone()
                                txt_bg.gone()
                                adapterStatistic.update(statistics)
                                chart(state.data)
                            }
                        }
                    }
                }
            }
        })
    }

    private fun chart(ts: ArrayList<StatisticData>) {
        piechart.setUsePercentValues(true)
        piechart.description.isEnabled = false
        piechart.legend.isEnabled = false
        piechart.setExtraOffsets(0f, 0f, 0f, 0f)
        piechart.dragDecelerationFrictionCoef = 0.95f
        piechart.isDrawHoleEnabled = true
        piechart.setHoleColor(Color.WHITE)
        piechart.setTransparentCircleColor(Color.WHITE)
        piechart.setTransparentCircleAlpha(110)
        piechart.holeRadius = 30f
        piechart.transparentCircleRadius = 0f
        piechart.setDrawCenterText(true)
        piechart.rotationAngle = 0f
        piechart.isRotationEnabled = true
        piechart.isHighlightPerTapEnabled = true
        val entries = ArrayList<PieEntry>()
        for (i in ts.iterator()) {
            entries.add(PieEntry(i.percent))
        }
        val dataSet = PieDataSet(entries, "")
        dataSet.iconsOffset = MPPointF(0f, 40f)
        dataSet.selectionShift = 5f
        for (c in ts.iterator()) {
            colors.add(Color.parseColor(c.color))
        }
        dataSet.colors = colors
        val data = PieData(dataSet)
        data.setValueFormatter(PercentFormatter(piechart))
        data.setValueTextSize(14f)
        data.setValueTextColor(Color.WHITE)
        piechart.data = data
        piechart.highlightValues(null)
        piechart.invalidate()
        piechart.animateY(1400, Easing.EaseInExpo)
    }
}
