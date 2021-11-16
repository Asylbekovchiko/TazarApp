package com.io.tazarapp.ui.recycler.qrcode


import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker.checkSelfPermission
import androidx.lifecycle.Observer
import com.google.zxing.BarcodeFormat
import com.google.zxing.Result

import com.io.tazarapp.R
import com.io.tazarapp.data.model.Moderation
import com.io.tazarapp.data.state.State
import com.io.tazarapp.ui.citizen.profile.item_profile.qr_info.QrPrizeActivity
import com.io.tazarapp.ui.recycler.qrcode.found.FoundActivity
import com.io.tazarapp.utils.BaseFragment
import com.io.tazarapp.utils.BottomSelectedFragment
import com.io.tazarapp.utils.TOKEN_ID
import kotlinx.android.synthetic.main.activity_qr_scan.*
import kotlinx.android.synthetic.main.fragment_qr.*
import me.dm7.barcodescanner.zxing.ZXingScannerView
import org.koin.androidx.viewmodel.ext.android.viewModel


class QrFragment : BottomSelectedFragment(), ZXingScannerView.ResultHandler {
    private val viewModel: ModerationCheckViewModel by viewModel()
    private var notPassedModeration = false
    private var profileNotFilled = false
    override var selectedItem = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_qr, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setScannerProperties()
        initViewModel()
    }

    private fun setScannerProperties() {
        with(qrScanner) {
            setFormats(listOf(BarcodeFormat.QR_CODE))
            setAutoFocus(true)
            setLaserColor(R.color.colorAccent_8CC341)
            setMaskColor(R.color.colorAccent_8CC341)
            setAspectTolerance(0.5f)
        }
    }

    override fun onResume() {
        super.onResume()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    requireActivity(), arrayOf(Manifest.permission.CAMERA),
                    101
                )
                return
            }
        }
        qrScanner.startCamera()
        qrScanner.setResultHandler(this)
    }

    override fun onPause() {
        super.onPause()
        qrScanner.stopCamera()
    }

    private fun initViewModel() {
        viewModel.state.observe(viewLifecycleOwner, Observer { state ->
            if (state != null) {
                when (state) {
                    is State.LoadingState -> when {
                        state.isLoading -> showDialog()
                        else -> hideDialog()
                    }
                    is State.ErrorState -> handleError(state)
                    is State.SuccessObjectState<*> -> {
                        val moderate = state.data as Moderation
                        when {
                            !moderate.is_profile_exists -> {
                                profileNotFilled = true
                                showAlert(R.string.is_profile_title, R.string.is_profile_message)
                            }
                            !moderate.is_moderate -> {
                                notPassedModeration = true
                                showAlert(R.string.is_moderate_title, R.string.is_moderate_message)
                            }
                            else -> qrScanner.startCamera()
                        }
                    }
                }
            }
        })
        viewModel.getModerationStatus()
    }

    override fun handleResult(data: Result?) {
        if (data != null) {
            val i = Intent(requireContext(), FoundActivity::class.java)
            i.putExtra("qr_token", data.text)
            requireActivity().startActivity(i)
        }
    }

    private fun showAlert(title: Int, message: Int) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton("OK") { dialog, _ ->

            val menuItem = when {
                profileNotFilled -> requireActivity().findViewById<View>(R.id.profile_res)
                notPassedModeration -> requireActivity().findViewById<View>(R.id.map)
                else -> requireActivity().findViewById<View>(R.id.map)
            }
            menuItem.callOnClick()
            dialog.dismiss()
        }
        builder.setCancelable(false)
        val mDialog: AlertDialog = builder.create()
        mDialog.show()
    }
}
