package com.io.tazarapp.ui.citizen.map.detail

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.io.tazarapp.R
import com.io.tazarapp.data.model.advertisement.SubcategoryAd
import com.io.tazarapp.data.model.cp.CP
import com.io.tazarapp.data.model.cp.CpSchedule
import com.io.tazarapp.data.model.map.mapQuestModel.MapClassModel
import com.io.tazarapp.data.state.State
import com.io.tazarapp.ui.citizen.map.MapCitizenViewModel
import com.io.tazarapp.ui.recycler.map.mapQuest.MapQuestViewModel
import com.io.tazarapp.utils.*
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import kotlinx.android.synthetic.main.activity_collection_place_detal.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class AdDetailActivity : BaseActivity(), OnMapReadyCallback, LocationListener {

    private val viewModelMap: MapQuestViewModel by viewModel()
    private var canDraw = false
    private var myLocation: Location? = null
    private val viewModel: MapCitizenViewModel by viewModel()
    private lateinit var map: GoogleMap
    private lateinit var detailAdapter: RVCategoryDetailAdapter
    private lateinit var coords: ArrayList<Double>
    lateinit var locationManager: LocationManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_collection_place_detal)
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        checkWriteExternalStoragePermission()
        initViewModels()
        setMap()
        setupRV()
        listeners()
        setLocationManager()
        initViewModelMap()
    }

    private fun setLocationManager() {

        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                10000, 10f, this
            )
            locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER, 1000, 10f,
                this
            )
        }
    }

    override fun onLocationChanged(location: Location?) {
        Log.e("LOCATION", location.toString())
        if (location != null) {
            myLocation = location
            if (canDraw) {
                canDraw = false
                drawRoute()
                hideDialog()
            }
        }
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
    }

    override fun onProviderEnabled(provider: String?) {
    }

    override fun onProviderDisabled(provider: String?) {
    }

    private fun initViewModels() {
        viewModel.state.observe(this, Observer { state ->
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
                            state.data is CP -> {
                                setData(state.data)
                            }
                        }
                    }
                }
            }
        })

        viewModel.getAdDetail(intent.getIntExtra("id", -1))
    }


    private fun setData(ad: CP) {
        coords = ad.cp_map[0].coords.coordinates as ArrayList<Double>

        setMarker(ad.cp_map[0].coords)
        detailAdapter.swapData(ad.subcategory as ArrayList<SubcategoryAd>)
        name.text = ad.title
        info.text = ad.description
        profile_image.setImage(ad.cp_user.image)
        profile_name.text = ad.cp_user.title
        address.text = ad.cp_map[0].address
        phone.text = maskPhoneNumb(ad.contact)
        image.setImageIfNullGone(ad.point_image)
        ad.cp_schedule.forEach {
            setSchedule(it)
        }
    }

    override fun onResume() {
        super.onResume()

    }

    private fun setSchedule(schedule: CpSchedule) {
        var textView = getDayTextView(schedule.name)

        if (!schedule.is_weekend) {
            textView.setTextColor(Color.parseColor("#A5A9AF"))
            if (schedule.start_at_time.isNullOrEmpty() || schedule.expires_at_time.isNullOrEmpty()) {
                textView.text = "---- - ----"
            } else {
                textView.text = "${schedule.start_at_time} - ${schedule.expires_at_time}"
            }
        } else {
            textView.setTextColor(ContextCompat.getColor(this, R.color.colorAccent_8CC341))
            textView.text = getString(R.string.day_off_short)
        }


    }

    private fun getDayTextView(name: Int): TextView {

        return when (name) {
            1 -> collection_place_monday_time
            2 -> collection_place_tuesday_time
            3 -> collection_place_wednesday_time
            4 -> collection_place_thursday_time
            5 -> collection_place_friday_time
            6 -> collection_place_saturday_time
            7 -> collection_place_sunday_time
            else -> collection_place_monday_time
        }

    }

    private fun setMarker(coords: com.io.tazarapp.data.model.cp.Coords) {
        val latLng = LatLng(coords.coordinates[0], coords.coordinates[1])
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,12f))
        map.addMarker(
            MarkerOptions()
                .position(latLng)
                .icon(bitmapDescriptorFromVector(this, R.drawable.ic_marker_red_ea565e))
        )
    }

    private fun setMap() {
        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.collection_place_map) as WorkaroundMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        (supportFragmentManager.findFragmentById(R.id.collection_place_map) as WorkaroundMapFragment?)?.setListener(
            object : WorkaroundMapFragment.OnTouchListener {
                override fun onTouch() {
                    scrollView.requestDisallowInterceptTouchEvent(true)
                }
            })
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        map.isMyLocationEnabled = true
    }

    private fun setupRV() {
        rv_category.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        detailAdapter = RVCategoryDetailAdapter()
        rv_category.adapter = detailAdapter
    }

    private fun checkWriteExternalStoragePermission() {
        Dexter.initialize(this)
        Dexter.checkPermissions(
            object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    Log.e("REPORT", report.toString())

                    if (report.grantedPermissionResponses.size == 2) {
                        initLocation()
                    } else {

                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: List<PermissionRequest>,
                    token: PermissionToken
                ) {
                    token.continuePermissionRequest()
                }
            },
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        )

    }

    private fun listeners() {
        collection_place_build_route.setOnClickListener {

            if (myLocation != null) {
                canDraw = false
                drawRoute()
            } else {
                if (isGPSEnable) {
                    showDialog1(R.string.loading_location_message)
                }
                canDraw = true
                checkWriteExternalStoragePermission()

            }
        }
        back.setOnClickListener { finish() }
        phone.setOnClickListener {
            var intent = Intent(
                Intent.ACTION_DIAL,
                Uri.parse("tel:" + clearPhoneNumb(phone.text.toString()))
            )
            startActivity(intent)
        }
        collection_place_ic_phone.setOnClickListener {
            var intent = Intent(
                Intent.ACTION_DIAL,
                Uri.parse("tel:" + clearPhoneNumb(phone.text.toString()))
            )
            startActivity(intent)
        }
    }

    var isGPSEnable = false
    private fun initLocation() {

        GpsUtils(this).turnGPSOn { isGPSEnable ->
            Log.e("GPS", "turn on GPS $isGPSEnable")
            this.isGPSEnable = isGPSEnable
        }
    }

    private fun drawRoute() {
        val gmmIntentUri = Uri.parse("google.navigation:q=${coords[0]},${coords[1]}&mode=d")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")
        startActivity(mapIntent)
    }

    private fun initViewModelMap() {

        viewModelMap.state.observe(this, Observer { state ->
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
                        when (state.data) {
                            is MapClassModel -> {
                                val latLng =
                                    getByTwoFromListMap(state.data.route.shape.shapePoints, this)
                                map.addPolyline(
                                    PolylineOptions()
                                        .clickable(true)
                                        .addAll(latLng)
                                        .color(Color.GREEN)
                                )
                            }
                        }
                    }
                }
            }
        })
    }
}