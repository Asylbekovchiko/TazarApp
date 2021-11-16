package com.io.tazarapp.ui.citizen.ad.adEdit.fragments

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import androidx.lifecycle.Observer
import com.io.tazarapp.R
import com.io.tazarapp.data.model.ad.AdFile
import com.io.tazarapp.data.model.ad.AdModel
import com.io.tazarapp.data.state.State
import com.io.tazarapp.ui.citizen.ad.adCreate.viewModel.AdCreateViewModel
import com.io.tazarapp.ui.citizen.ad.adEdit.AdEditActivity
import com.io.tazarapp.ui.citizen.ad.adEdit.IValidate
import com.io.tazarapp.utils.*
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import id.zelory.compressor.Compressor
import kotlinx.android.synthetic.main.step1_fragment.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File

class Step1Fragment : BaseFragment(), TextWatcher {

    private var fileName = ""
    private val REQUEST_TAKE_PHOTO = 78
    private val REQUEST_GET_FROM_GALLERY = 23
    private val adModel: AdModel by inject()
    private val file: AdFile by inject()
    private var imagePermissionsGranted = false
    private var photoFile: File? = null
    private lateinit var mListener: IValidate
    private val viewModel: AdCreateViewModel by viewModel()
    private var isDelete = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.step1_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        initListeners()
        updateUI()
        initWatchers()
        initViewModel()
    }

    private fun initViewModel() {

        viewModel.state.observe(viewLifecycleOwner, Observer { state ->
            if (state != null) {
                when (state) {
                    is State.LoadingState -> {
                        when {
                            state.isLoading -> showDialog()
                            else -> hideDialog()
                        }
                    }
                    is State.ErrorState -> handleError(state)

                    is State.SuccessObjectState<*> -> {
                        when (state.data) {
                            is ResponseBody -> {
                                if (isDelete) {
                                    photoFile = null
                                    adModel.image = null
                                    image.setImageDrawable(null)
                                    btn_delete.gone()
                                }
                                file.clearData()
                                requireContext().toast(R.string.success)
                            }
                        }
                    }
                }
            }
        })
    }

    private fun updateUI() {
        edit_name.setText(adModel.name)
        edit_phone.setText(adModel.phone)
        edit_description.setText(adModel.description)
        check_company.isChecked = adModel.as_company
        if (adModel.image != null) {
            image.setImage(adModel.image.toString())
            btn_delete.visible()
        } else {
            image.setImageDrawable(null)
            btn_delete.gone()
        }
    }

    private fun initListeners() {
        image.setOnClickListener {
            Dexter.initialize(requireContext())
            Dexter.checkPermissions(
                object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                        imagePermissionsGranted = if (report.grantedPermissionResponses.size == 3) {
                            selectImage(requireContext())
                            true
                        } else {
                            false
                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        permissions: List<PermissionRequest>,
                        token: PermissionToken
                    ) {
                    }
                },
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        }
        btn_delete.setOnClickListener { deleteImage() }
        check_company.setOnCheckedChangeListener { buttonView, isChecked ->
            adModel.as_company = isChecked
        }
    }

    private fun selectImage(context: Context) {
        val options = resources.getStringArray(R.array.makePhoto)
        val builder = AlertDialog.Builder(context)
        builder.setTitle(resources.getString(R.string.makePhotoTitle))
        builder.setItems(options) { dialog, item ->
            when (item) {
                0 -> {
                    fileName = System.nanoTime().toString()
                    val takePicture = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

                    val uri = getCaptureImageOutputUri(requireContext(), fileName)
                    if (uri != null) {
                        val file = File(uri.path)
                        if (Build.VERSION.SDK_INT >= 24) {
                            takePicture.putExtra(
                                MediaStore.EXTRA_OUTPUT,
                                FileProvider.getUriForFile(
                                    requireContext(),
                                    "com.io.tazarapp.provider",
                                    file
                                )
                            )
                            takePicture.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                            takePicture.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        } else {
                            takePicture.putExtra(MediaStore.EXTRA_OUTPUT, uri)
                        }
                    }
                    startActivityForResult(takePicture, REQUEST_TAKE_PHOTO)
                }
                1 -> {
                    val pickPhoto = Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    )
                    startActivityForResult(pickPhoto, REQUEST_GET_FROM_GALLERY)
                }
                2 -> dialog.dismiss()
            }
        }
        builder.show()
    }

    private fun getCaptureImageOutputUri(context: Context, filename: String): Uri? {
        val getImage = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return if (getImage != null) {
            Uri.fromFile(File(getImage.path, "$filename.jpg"))
        } else {
            null
        }
    }

    private fun deleteImage() = adModel.id?.let {
        isDelete = true
        viewModel.sendImage(null, it, "delete")
    }

    private fun initWatchers() {
        edit_name.addTextChangedListener(this)
        edit_phone.addTextChangedListener(this)
        edit_description.addTextChangedListener(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_TAKE_PHOTO -> {
                    val uri = getPickImageResultUri(requireContext(), data, fileName)
                    val file = getNormalizedUri(requireContext(), uri)
                    imageCrop(file)
                }
                REQUEST_GET_FROM_GALLERY -> {
                    if (data != null) {
                        imageCrop(data.data)
                    }
                }
            }
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == Activity.RESULT_OK) {
                val resultUri = result.uri
                image.setImage(resultUri.toString())
                photoFile = File(resultUri.path.toString())
                GlobalScope.launch {
                    val compressedFile = Compressor.compress(requireContext(), photoFile!!)
                    file.file = compressedFile
                    requireActivity().runOnUiThread(Runnable { btn_delete.visible() })
                    isDelete = false
                    adModel.id?.let { viewModel.sendImage(file, it, "add") }
                }
            }
        }
    }

    private fun imageCrop(uri: Uri?) {
        CropImage.activity(uri)
            .setGuidelines(CropImageView.Guidelines.ON)
            .setAspectRatio(1, 1)
            .start(requireActivity())
    }

    private fun getNormalizedUri(context: Context, uri: Uri?) =
        if (uri != null && uri.toString().contains("content:")) {
            Uri.fromFile(getPath(context, uri, MediaStore.Images.Media.DATA))
        } else {
            uri
        }

    private fun getPath(context: Context, uri: Uri, column: String): File? {
        val columns = arrayOf(column)
        val cursor = context.contentResolver.query(uri, columns, null, null, null) ?: return null
        val column_index = cursor.getColumnIndexOrThrow(column)
        cursor.moveToFirst()
        val path = cursor.getString(column_index)
        cursor.close()
        return File(path)
    }

    private fun getPickImageResultUri(context: Context, data: Intent?, fileName: String): Uri? {
        val isCamera = if (data != null && data.data != null) {
            data.action != null
        } else {
            true
        }
        return if (isCamera || data!!.data == null) {
            getCaptureImageOutputUri(context, fileName)
        } else {
            data.data
        }
    }

    override fun afterTextChanged(p0: Editable?) {
        file.file = photoFile
        adModel.name = edit_name.text.toString()
        adModel.phone = clearPhoneNumb(edit_phone.text.toString())
        adModel.description = edit_description.text.toString()
        mListener.onValidate()
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
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