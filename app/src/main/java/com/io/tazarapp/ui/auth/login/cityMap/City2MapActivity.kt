package com.io.tazarapp.ui.auth.login.cityMap

import android.app.Activity
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.io.tazarapp.R
import com.io.tazarapp.data.model.CityDataModel
import com.io.tazarapp.data.model.ad.AdsMap
import com.io.tazarapp.ui.recycler.profile.item_profile.profile_info.EditResProfileActivity
import com.io.tazarapp.utils.BaseActivity
import com.io.tazarapp.utils.DEFAULT_ZOOM
import com.io.tazarapp.utils.activate
import kotlinx.android.synthetic.main.activity_city2_map.*
import kotlinx.android.synthetic.main.custom_toolbar.*
import kotlinx.android.synthetic.main.custom_toolbar.view.*
import org.koin.android.ext.android.inject
import java.util.*

class City2MapActivity : BaseActivity(), OnMapReadyCallback {
    private var mMap: GoogleMap? = null
    private var marker: MarkerOptions? = null
    private lateinit var geoCoder: Geocoder
    private lateinit var addresses: List<Address>
    private var long = 0.0
    private var lat = 0.0
    private val adsMap = AdsMap()
    private var city: Int? = null
    private var location: ArrayList<String>? = null
    private val cityData: CityDataModel by inject()
    private var isNewData = true


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_city2_map)
        city = intent.getIntExtra("city", -1)
        location = intent.getSerializableExtra("location") as? ArrayList<String>
        init()
        isNewData = intent.getBooleanExtra("point", false)
    }

    private fun init() {
        initToolbar()
        initMap()
    }

    private fun initListeners() {
        mMap?.setOnMapClickListener { mapListener(it) }
        btn_next.setOnClickListener {
            marker?.position?.let {
                long = it.longitude
                lat = it.latitude
            }
            if (!this::geoCoder.isInitialized) geoCoder = Geocoder(this, Locale.getDefault())

            addresses = geoCoder.getFromLocation(lat, long, 1)
            adsMap.address = addresses[0].getAddressLine(0)

            val location = "${addresses[0].latitude},${addresses[0].longitude}"
            cityData.clearData()
            cityData.address.add(addresses[0].getAddressLine(0).toString())
            cityData.location.add(location)
            cityData.city.add(city.toString())
            cityData.isNew = true
            finish()
        }
    }

    private fun mapListener(it: LatLng) {
        mMap?.clear()
        mMap?.addMarker(marker?.position(it))
        btn_next.activate()
    }

    private fun initMap() {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun initToolbar() {
        setSupportActionBar(toolbar)
        toolbar.custom_title.text = getString(R.string.adress_choose)
        toolbar.setNavigationOnClickListener {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        mMap = googleMap
        if (mMap != null) {
            initListeners()
            marker =
                MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_step5))

            mMap?.moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(location!![0].toDouble(), location!![1].toDouble()), DEFAULT_ZOOM
                )
            )
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}