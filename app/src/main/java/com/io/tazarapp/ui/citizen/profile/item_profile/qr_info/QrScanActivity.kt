package com.io.tazarapp.ui.citizen.profile.item_profile.qr_info

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.core.app.ActivityCompat
import com.google.zxing.BarcodeFormat
import com.google.zxing.Result
import com.io.tazarapp.R
import com.io.tazarapp.utils.BaseActivity
import com.io.tazarapp.utils.TOKEN_ID
import kotlinx.android.synthetic.main.activity_qr_scan.*
import me.dm7.barcodescanner.zxing.ZXingScannerView


class QrScanActivity : BaseActivity(), ZXingScannerView.ResultHandler {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qr_scan)
        setScannerProperties()
    }

    private fun setScannerProperties() {
        with(qrCodeScanner) {
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
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this, arrayOf(Manifest.permission.CAMERA),
                    101
                )
                return
            }
        }
        qrCodeScanner.startCamera()
        qrCodeScanner.setResultHandler(this)
    }

    override fun onPause() {
        super.onPause()
        qrCodeScanner.stopCamera()
    }

    override fun handleResult(data: Result?) {
        if (data != null) {
            val intent = Intent(this, QrPrizeActivity::class.java)
            intent.putExtra(TOKEN_ID, data.toString())
            startActivity(intent)
        }
    }
}