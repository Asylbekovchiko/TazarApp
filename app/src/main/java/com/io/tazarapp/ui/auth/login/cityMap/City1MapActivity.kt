package com.io.tazarapp.ui.auth.login.cityMap

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.io.tazarapp.R
import com.io.tazarapp.data.model.CityDataModel
import com.io.tazarapp.data.model.auth.CityModel
import com.io.tazarapp.data.state.State
import com.io.tazarapp.ui.auth.login.city.CityAdapter
import com.io.tazarapp.ui.auth.login.city.CityViewModel
import com.io.tazarapp.utils.*
import kotlinx.android.synthetic.main.activity_city_map.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class City1MapActivity : BaseActivity() {
    override fun handleCustomError(message: String) {
        toast(message)
    }

    private val viewModelCity: CityViewModel by viewModel()
    var phoneNumber: String = ""

    private var selectedCity: CityModel? = null
    private lateinit var adapterCity: CityAdapter
    var address: String? = null
    var location: String? = null
    var city: String? = null
    private var isNewData = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_city_map)
        initRV()
        initViewModel()
        search()
        listeners()
        clickItem(intent.getSerializableExtra(CITY) as CityModel?)
        adapterCity.selectedCity = selectedCity
        adapterCity.notifyDataSetChanged()
    }

    private fun listeners() {
        btn_city_next.setOnClickListener {
            val intent = Intent(this, City2MapActivity::class.java)
            intent.putExtra("point", isNewData)
            intent.putExtra("location", getLatLng())
            intent.putExtra("city", adapterCity.selectedCity?.id)
            startActivity(intent)
            finish()
        }

    }

    private fun getLatLng(): ArrayList<String>? =
        adapterCity.getCheckedItem()?.geolocation?.split(",") as ArrayList<String>?

    private fun search() {
        adapterCity.searchViewCollapsed()
        val searchView: SearchView = sv_city
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapterCity.filter(newText)
                return true
            }
        })
    }

    private fun initRV() {
        adapterCity =
            CityAdapter { item: CityModel? ->
                clickItem(item)
            }
        rv_city.layoutManager = LinearLayoutManager(this)
        rv_city.adapter = adapterCity
    }


    private fun clickItem(item: CityModel?) {
        selectedCity = item
        if (selectedCity != null) {
            btn_city_next.activate()
        } else {
            btn_city_next.disActivate()
        }
    }

    private fun initViewModel() {
        with(viewModelCity) {
            state.observe(this@City1MapActivity, Observer {
                when (it) {
                    is State.LoadingState -> {
                        when {
                            it.isLoading -> showDialog()
                            else -> hideDialog()
                        }
                    }
                    is State.ErrorState -> {
                        handleError(it)
                    }
                    is State.SuccessListState<*> -> {
                        when {
                            it.data[0] is CityModel -> {

                            }
                        }
                        when {
                            it.data.all { item -> item is CityModel } -> {
                                adapterCity.update(it.data as ArrayList<CityModel>)
                                adapterCity.setCheckedItemId(it.data[0].id)
                            }
                        }
                    }
                }
            })
            getCity()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}