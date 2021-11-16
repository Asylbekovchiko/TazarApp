package com.io.tazarapp.ui.recycler.qrcode

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import com.io.tazarapp.R
import com.io.tazarapp.data.model.Moderation
import com.io.tazarapp.data.state.State
import com.io.tazarapp.ui.recycler.qrcode.found.FoundActivity
import com.io.tazarapp.utils.BaseFragment
import kotlinx.android.synthetic.main.activity_recycler.*
import me.dm7.barcodescanner.zxing.ZXingScannerView
import org.koin.androidx.viewmodel.ext.android.viewModel

class ScanQRFragment : BaseFragment() {
    private lateinit var scanView: ZXingScannerView
    private lateinit var placeholder: ViewGroup
    private val viewModel: ModerationCheckViewModel by viewModel()
    private var notPassedModeration = false
    private var profileNotFilled = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        scanView = ZXingScannerView(requireContext())
        return scanView
    }

    override fun onPause() {
        super.onPause()
        scanView.stopCamera()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewModel()
        listeners()
        scanView.stopCamera()
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
                            else -> scanView.startCamera()
                        }
                    }
                }
            }
        })
        viewModel.getModerationStatus()
    }

    private fun listeners() {
        scanView.setResultHandler { rawResult ->
            val i = Intent(requireContext(), FoundActivity::class.java)
            i.putExtra("qr_token", rawResult.text)
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