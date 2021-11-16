package com.io.tazarapp.ui.citizen.map

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.io.tazarapp.R
import com.io.tazarapp.data.model.profile.ProfileMainModel
import com.io.tazarapp.data.state.State
import com.io.tazarapp.ui.citizen.ad.adCreate.view.Step1Activity
import com.io.tazarapp.ui.citizen.profile.ProfileMainViewModel
import com.io.tazarapp.utils.*
import kotlinx.android.synthetic.main.fragment_map_list.*
import kotlinx.android.synthetic.main.fragment_map_list_recycler.line_map
import kotlinx.android.synthetic.main.fragment_map_list_recycler.rv_result
import kotlinx.android.synthetic.main.fragment_map_list_recycler.switch_type
import kotlinx.coroutines.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.view.KeyEvent
import androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL
import com.io.tazarapp.data.model.auth.CityModel
import com.io.tazarapp.data.model.cp.CP
import com.io.tazarapp.data.model.cp.CpMap
import com.io.tazarapp.data.model.filter.FilterSubcategoryModel
import com.io.tazarapp.ui.auth.login.city.CityActivity
import com.io.tazarapp.ui.citizen.filter.view.FilterCategoryActivity
import com.io.tazarapp.ui.citizen.filter.view.bottom.BottomChooseCategoryFragment


class MapListFragment : BottomSelectedFragment(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener  {
    override var selectedItem = 0
    private lateinit var mMap: GoogleMap
    private val viewModel: MapCitizenViewModel by viewModel()
    private val viewModelProfile: ProfileMainViewModel by viewModel()
    private lateinit var adapter: RVCitizenAdAdapter
    private lateinit var adapterSubcategory: RVCategoryAdapter
    lateinit var job: Job
    private var parts = ArrayList<FilterSubcategoryModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_map_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        job = SupervisorJob()
        setMap()
        setupRV()
        setClickListener()
        initViewModels()
        viewModelProfile.getMainProfile()
    }

    private fun setClickListener() {
        switch_type.setOnCheckedChangeListener { buttonView, isChecked ->

            if (isChecked) {
                line_map.gone()
                rv_result.visible()
            } else {
                line_map.visible()
                rv_result.gone()
            }

        }
        btn_create.setOnClickListener {
            val newIntent = Intent(requireContext(), FilterCategoryActivity::class.java)
            newIntent.putExtra(IS_FILTER_CREATING, true)
            startActivity(newIntent)
        }

        img_filter.setOnClickListener { openBottomDialog() }

        edit_search.setOnEditorActionListener(object : TextView.OnEditorActionListener {
            override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    viewModel.getAds(edit_search.text.toString(), city, parts)
                    return true
                }
                return false
            }
        })
    }

    override fun onResume() {
        super.onResume()
        setLocale(requireContext())
    }

    private fun setMap() {
        val mapFragment =
            childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        val sydney = LatLng(74.63, 42.87)
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
        mMap.setOnMarkerClickListener(this)
    }


    private fun initViewModels() {
        viewModel.state.observe(viewLifecycleOwner, Observer { state ->
            if (state == null) {
                Log.e("Empty", "Item class is empty")
            } else {
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
                        Log.e("A", state.data.toString())
                        when {
                            state.data.all { it is CP } -> {
                                setupAds(state.data as ArrayList<CP>)
                            }
                            state.data.all { it is FilterSubcategoryModel } -> {
                                Log.e("FilterSubCategory", state.data.toString())
                                adapterSubcategory.swapData(state.data as ArrayList<FilterSubcategoryModel>)
                            }

                        }

                    }
                    is State.NoItemState -> {
                        setupAds(ArrayList())
                    }
                }
            }
        })

        viewModelProfile.state.observe(viewLifecycleOwner, Observer { state ->
            if (state == null) {
                Log.e("Empty", "Item class is empty")
            } else {
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

                    is State.SuccessObjectState<*> -> {
                        when {
                            state.data is ProfileMainModel -> {
                                if (state.data.geolocation != null) {
                                    var location = state.data.geolocation.split(",")
                                    mMap.moveCamera(
                                        CameraUpdateFactory.newLatLngZoom(
                                            LatLng(
                                                location[0].toDouble(),
                                                location[1].toDouble()
                                            ), 12f
                                        )
                                    )
                                }
                                viewModel.getAds(null, city, parts)
                            }
                        }

                    }
                }
            }
        })
        val name = this.resources.getString(R.string.All)
        viewModel.getCategoryList(name)

    }

    private var ads: ArrayList<CP> = ArrayList()
    private fun setupAds(ads: ArrayList<CP>) {
        adapter.swapData(ads)
        this.ads = ads
        mMap.clear()
        if (ads.isNotEmpty()) {
            Log.e("ADS", ads.toString())
            CoroutineScope(Dispatchers.IO + job).launch {
                ads.forEachIndexed { index, countriesMap ->
                    val job = MainScope().launch {
                        if (countriesMap.cp_map.isNotEmpty()) {
                            drawMarker(mMap, index, countriesMap.cp_map[0])
                        }
                    }
                    job.join()

                }
            }
        }
    }

    private fun setupRV() {
        rv_result.layoutManager = LinearLayoutManager(requireContext())
        adapter = RVCitizenAdAdapter()
        rv_result.adapter = adapter

        rv_filter.layoutManager = LinearLayoutManager(requireContext(), HORIZONTAL, false)
        rv_filter.setHasFixedSize(false)
        adapterSubcategory = RVCategoryAdapter(filterClick)
        adapterSubcategory.setChoces(parts)
        rv_filter.adapter = adapterSubcategory

    }

    private fun drawMarker(map: GoogleMap, index: Int, countriesMap: CpMap) {

        val marker = map.addMarker(
            MarkerOptions()
                .position(
                    LatLng(
                        countriesMap.coords.coordinates[0],
                        countriesMap.coords.coordinates[1]
                    )
                )
                .icon(
                    bitmapDescriptorFromVector(requireContext(), R.drawable.ic_marker_red_ea565e)
                )
        )
        marker.tag = index
    }

    override fun onMarkerClick(p0: Marker?): Boolean {
        if (p0 != null) {
            var index = p0.tag as Int
            startDetailActivity(index)
        }
        return true
    }

    private fun startDetailActivity(index: Int) {
        val id = ads[index].id
        requireContext().startActivity(
            Intent(
                requireContext(),
                com.io.tazarapp.ui.citizen.map.detail.AdDetailActivity::class.java
            ).putExtra("id", id)
        )
    }

    private val CITY_REQUEST_CODE = 312

    private lateinit var bundle: Bundle
    private lateinit var fragment: BottomChooseCategoryFragment

    // your need data
    private var city: CityModel? = null

    private fun openBottomDialog() {
        bundle = Bundle().also {
            it.putSerializable("selected", parts)
            it.putSerializable("city", city)
        }
        fragment = BottomChooseCategoryFragment(openCategory, openCity, filterClick)
        fragment.arguments = bundle
        fragment.show(childFragmentManager, fragment.tag)
    }

    private val openCity: () -> Unit = {
        val intent = Intent(requireContext(), CityActivity::class.java)
        intent.putExtra("parent", true)
        intent.putExtra("city", city)
        this.startActivityForResult(
            intent,
            CITY_REQUEST_CODE
        )
    }

    private val openCategory: () -> Unit = {
        val intent = Intent(requireContext(), FilterCategoryActivity::class.java)
        intent.putExtra("parts", parts)
        this.startActivityForResult(
            intent,
            CATEGORY_REQUEST_CODE
        )
    }

    private val filterClick: () -> Unit = {
        viewModel.getAds(edit_search.text.toString(), city, parts)
        adapterSubcategory.notifyDataSetChanged()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.e("DATA", data.toString())
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                CATEGORY_REQUEST_CODE -> {
                    parts =
                        data?.getSerializableExtra("selectedParts") as java.util.ArrayList<FilterSubcategoryModel>
                    adapterSubcategory.setChoces(parts)

                    bundle.putSerializable("selected", parts)
                }
                CITY_REQUEST_CODE -> {
                    city = data?.getSerializableExtra("city") as CityModel
                    city?.geolocation?.let { geo ->
                        val points = geo.split(",")
                        mMap.moveCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                LatLng(
                                    points[0].toDouble(),
                                    points[1].toDouble()
                                ), 12f
                            )
                        )
                    }
                    bundle.putSerializable("city", city)
                }
            }
        }
    }
}
