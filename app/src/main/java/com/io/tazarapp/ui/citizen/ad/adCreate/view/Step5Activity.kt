package com.io.tazarapp.ui.citizen.ad.adCreate.view

import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.io.tazarapp.R
import com.io.tazarapp.data.model.ad.*
import com.io.tazarapp.data.model.collectionplace.CollectionPointModel
import com.io.tazarapp.data.model.cp.CpFile
import com.io.tazarapp.data.model.cp.CpImageURl
import com.io.tazarapp.data.state.State
import com.io.tazarapp.ui.citizen.ad.adCreate.fragment.SuccessBottomSheetFragment
import com.io.tazarapp.ui.citizen.ad.adCreate.viewModel.AdCreateViewModel
import com.io.tazarapp.utils.*
import kotlinx.android.synthetic.main.activity_step5.*
import kotlinx.android.synthetic.main.adcreate_toolbar.*
import kotlinx.android.synthetic.main.adcreate_toolbar.view.*
import okhttp3.ResponseBody
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*


class Step5Activity : BaseActivity(), OnMapReadyCallback {

    private var mMap: GoogleMap? = null
    private var marker: MarkerOptions? = null
    private val adModel: AdModel by inject()
    private val adFile: AdFile by inject()
    private val cpModel: CollectionPointModel by inject()
    private val cpFile: CpFile by inject()
    private lateinit var geoCoder: Geocoder
    private lateinit var addresses: List<Address>
    private var long = 0.0
    private var lat = 0.0
    private val coordinates = Coordinates()
    private val adsMap = AdsMap()
    private var city: String? = null
    private var location: ArrayList<String>? = null
    private val viewModel: AdCreateViewModel by viewModel()
    private var isNewPoint = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_step5)
        city = intent.getStringExtra("city")
        isNewPoint = intent.getBooleanExtra("point", false)
        location = intent.getSerializableExtra("location") as? ArrayList<String>
        init()
    }

    private fun init() {
        initToolbar()
        initViewModel()
        initMap()
    }

    private fun initViewModel() {
        viewModel.state.observe(this, Observer { state ->
            if (state != null) {
                when (state) {
                    is State.LoadingState -> {
                        when {
                            state.isLoading -> {
                                showDialog()
                                btn_next.isEnabled = false
                            }
                            else -> {
                                hideDialog()
                                btn_next.isEnabled = true
                            }
                        }
                    }
                    is State.ErrorState -> {
                        handleError(state)
                    }
                    is State.SuccessObjectState<*> -> {
                        Log.e("STATE",state.data.toString())
                        when (state.data) {
                            is AdModel -> {
                                val model = state.data
                                if (adFile.file != null) {
                                    model.id?.let { viewModel.sendImage(adFile, it, "add") }
                                } else {
                                    showBottomSheet()
                                    adFile.clearData()
                                    adModel.clearData()
                                }
                            }
                            is CollectionPointModel -> {
                                Log.e("COLLECTION",state.data.toString())
                                showBottomSheet()
                                cpModel.clearData()
                            }
                            is CpImageURl -> {
                                cpModel.point_image = state.data.image
                                cpFile.clearData()
                                sendCpData()
                            }
                            is ResponseBody -> {
                                showBottomSheet()
                                adFile.clearData()
                                adModel.clearData()
                            }
                        }
                    }
                }
            }
        })
    }

    private fun initListeners() {
        mMap?.setOnMapClickListener {
            mapListener(it)
        }
        btn_next.setOnClickListener {
            marker?.position?.let {
                long = it.longitude
                lat = it.latitude
            }

            with(coordinates) {
                type = "Point"
                coordinates?.clear()
                coordinates?.add(lat)
                coordinates?.add(long)
                adsMap.coords = this
            }

            if (!this::geoCoder.isInitialized) geoCoder = Geocoder(this, Locale.getDefault())

            addresses = geoCoder.getFromLocation(lat, long, 1)
            adsMap.address = addresses[0].getAddressLine(0)

            if (isNewPoint) {
                if (cpFile.file != null) {
                    viewModel.sendCp(cpFile)
                } else {
                    sendCpData()
                }
            } else {
                adModel.ads_map.clear()
                adModel.ads_map.add(adsMap)
                viewModel.createAd(adModel)
            }
        }
    }

    private fun sendCpData() {
        cpModel.cp_map.clear()
        cpModel.cp_map.add(adsMap)
        viewModel.createCP(cpModel)
    }

    private fun mapListener(it: LatLng) {
        mMap?.clear()
        mMap?.addMarker(marker?.position(it))
        btn_next.isEnabled = true
        btn_next.setBackgroundResource(R.drawable.rounded_shape_green_12dp_8cc341)
    }

    private fun initMap() {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun initToolbar() {
        setSupportActionBar(toolbar)
        toolbar.custom_title.text = getString(R.string.step5_title)
        toolbar.sub_title.text = getString(R.string.step5_sub_title)
        toolbar.setNavigationOnClickListener { finish() }
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

    private fun showBottomSheet() {
        val bundle = Bundle()
        if (isNewPoint) {
            bundle.putBoolean("point", true)
        } else {
            bundle.putBoolean("point", false)
        }
        val bottomSheetFragment = SuccessBottomSheetFragment(success)
        bottomSheetFragment.arguments = bundle
        bottomSheetFragment.show(supportFragmentManager, bottomSheetFragment.tag)
    }

    private val success : () -> Unit = {
        val int = Intent()
        setResult(RESULT_OK,int)
        finish()
    }
}