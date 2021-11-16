package com.io.tazarapp.ui.citizen.ad.adEdit.fragments

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.Task
import com.io.tazarapp.R
import com.io.tazarapp.data.model.ad.AdModel
import com.io.tazarapp.data.model.ad.AdsMap
import com.io.tazarapp.data.model.ad.Coordinates
import com.io.tazarapp.ui.citizen.ad.adEdit.AdEditActivity
import com.io.tazarapp.ui.citizen.ad.adEdit.IValidate
import com.io.tazarapp.ui.citizen.ad.adEdit.viewModel.AdEditViewModel
import com.io.tazarapp.utils.DEFAULT_ZOOM
import com.io.tazarapp.utils.isCheckPermissionFailed
import com.io.tazarapp.utils.toast
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import kotlinx.android.synthetic.main.step5_fragment.*
import org.koin.android.ext.android.inject
import java.util.*


class Step5Fragment : Fragment(R.layout.step5_fragment), OnMapReadyCallback {
    private var mMap: GoogleMap? = null
    private lateinit var geoCoder: Geocoder
    private lateinit var addresses: List<Address>
    private var marker: MarkerOptions? = null
    private val adModel: AdModel by inject()
    private lateinit var mListener: IValidate
    private var long = 0.0
    private var lat = 0.0
    private val coordinates = Coordinates()
    private val adsMap = AdsMap()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initMap()
    }

    private fun updateUI() {
        val coordinates = LatLng(
            adModel.ads_map[0].coords?.coordinates?.get(0)!!,
            adModel.ads_map[0].coords?.coordinates?.get(1)!!
        )
        mMap?.moveCamera(
            CameraUpdateFactory.newLatLngZoom(coordinates, DEFAULT_ZOOM)
        )



        mapListener(coordinates)
    }

    private fun initListeners() {
        mMap?.setOnMapClickListener { mapListener(it) }
    }

    private fun mapListener(it: LatLng) {
        mMap?.clear()
        mMap?.addMarker(marker?.position(it))
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

        if (!this::geoCoder.isInitialized) geoCoder =
            Geocoder(requireContext(), Locale.getDefault())

        try {
            addresses = geoCoder.getFromLocation(lat, long, 1)
            adsMap.address = addresses[0].getAddressLine(0)
            adsMap.id = adModel.ads_map[0].id
        } catch (e : Exception) {
            Toast.makeText(requireContext(), requireContext().resources.getString(R.string.address_not_found), Toast.LENGTH_SHORT).show()
        }
        adModel.ads_map.clear()
        adModel.ads_map.add(adsMap)
        adModel.category = 1
        mListener.onValidate()
    }

    private fun initMap() {
        val mapFragment =
            childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        mMap = googleMap
        if (mMap != null) {
            initListeners()
            marker =
                MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_step5))
            updateUI()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            mListener = requireActivity() as AdEditActivity
        } catch (e: ClassCastException) {
            throw ClassCastException("Error")
        }
    }
}