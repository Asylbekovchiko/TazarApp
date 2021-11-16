package com.io.tazarapp.ui.recycler.map

import android.Manifest
import android.content.Intent
import android.graphics.Color
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
import com.io.tazarapp.data.model.map.Ad
import com.io.tazarapp.R
import com.io.tazarapp.data.model.map.AdsMap
import com.io.tazarapp.data.model.profile.ProfileMainModel
import com.io.tazarapp.data.state.State
import com.io.tazarapp.ui.citizen.profile.ProfileMainViewModel
import com.io.tazarapp.ui.recycler.map.detail.AdDetailActivity
import com.io.tazarapp.utils.*
import kotlinx.android.synthetic.main.fragment_map_list_recycler.*
import kotlinx.coroutines.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class MapListFragment : BottomSelectedFragment(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private lateinit var mMap: GoogleMap
    private val viewModel: MapRecyclerViewModel by viewModel()
    private val viewModelProfile: ProfileMainViewModel by viewModel()
    private lateinit var adapter: RVRecyclerAdAdapter
    lateinit var job: Job
    override var selectedItem: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_map_list_recycler, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        job = SupervisorJob()
        setMap()
        setupRV()
        setClickListener()
        initViewModels()
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
    }

    override fun onResume() {
        super.onResume()
        viewModelProfile.getMainProfile()
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
                        when {
                            state.data.all { it is Ad } -> {
                                setupAds(state.data as ArrayList<Ad>)
                            }

                        }

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
                                if(state.data.geolocation!=null) {
                                    var location = state.data.geolocation.split(",")
                                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(location[0].toDouble(),location[1].toDouble()),12f))
                                }
                                viewModel.getAds()
                            }
                        }

                    }
                }
            }
        })

    }

    private var ads: ArrayList<Ad> = ArrayList()
    private fun setupAds(ads: ArrayList<Ad>) {
        adapter.swapData(ads)
        this.ads = ads
        if (ads.isNotEmpty()) {

            CoroutineScope(Dispatchers.IO + job).launch {
                ads.forEachIndexed { index, countriesMap ->

                    val job = MainScope().launch {
                        drawMarker(mMap, index, countriesMap.ads_map[0])
                    }
                    job.join()

                }
            }
        }
    }

    private fun setupRV() {
        rv_result.layoutManager = LinearLayoutManager(requireContext())
        adapter = RVRecyclerAdAdapter()
        rv_result.adapter = adapter
    }

    private fun drawMarker(map: GoogleMap, index: Int, countriesMap: AdsMap) {

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
                AdDetailActivity::class.java
            ).putExtra("id", id)
        )
    }


}
