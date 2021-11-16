package com.io.tazarapp.ui.shared_ui.new_cp

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import androidx.lifecycle.Observer
import com.io.tazarapp.R
import com.io.tazarapp.data.model.auth.CheckUserModel
import com.io.tazarapp.data.model.collectionplace.CollectionPointModel
import com.io.tazarapp.data.model.cp.CpFile
import com.io.tazarapp.data.state.State
import com.io.tazarapp.ui.auth.login.signUp.SignUpViewModel
import com.io.tazarapp.ui.citizen.ad.adCreate.view.Step2Activity
import com.io.tazarapp.utils.*
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import id.zelory.compressor.Compressor
import kotlinx.android.synthetic.main.activity_new_point.*
import kotlinx.android.synthetic.main.activity_new_point.btn_delete
import kotlinx.android.synthetic.main.activity_new_point.btn_next
import kotlinx.android.synthetic.main.activity_new_point.edit_name
import kotlinx.android.synthetic.main.activity_new_point.edit_phone
import kotlinx.android.synthetic.main.activity_new_point.image
import kotlinx.android.synthetic.main.activity_step1.*
import kotlinx.android.synthetic.main.adcreate_toolbar.*
import kotlinx.android.synthetic.main.adcreate_toolbar.view.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File

class NewCollectionPointActivity : ActivityWithCleanBack(), TextWatcher {
    private val cpModel: CollectionPointModel by inject()
    private val viewModel: SignUpViewModel by viewModel()

    // helper variables
    private var fileName = ""
    private val REQUEST_TAKE_PHOTO = 78
    private val REQUEST_GET_FROM_GALLERY = 23
    private var imagePermissionsGranted = false
    private var photoFile: File? = null
    private val file: CpFile by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_point)
        init()
    }

    private fun init() {
        initToolbar()
        initWatchers()
        initListeners()
        boom(btn_next)
        configureViewModel()
    }

    private fun initListeners() {
        toolbar.setNavigationOnClickListener { finish() }

        image.setOnClickListener {
            Dexter.initialize(this)
            Dexter.checkPermissions(
                    object : MultiplePermissionsListener {
                        override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                            imagePermissionsGranted = if (report.grantedPermissionResponses.size == 3) {
                                selectImage(this@NewCollectionPointActivity)
                                true
                            } else {
                                false
                            }
                        }

                        override fun onPermissionRationaleShouldBeShown(
                                permissions: List<PermissionRequest>,
                                token: PermissionToken
                        ) {
                            token.continuePermissionRequest()
                        }
                    },
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
            )
        }

        btn_delete.setOnClickListener { deleteImage() }

        btn_next.setOnClickListener {
            viewModel.checkUser(clearPhoneNumb(edit_phone.text.toString()))
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
                    val uri = getCaptureImageOutputUri(this, fileName)
                    if (uri != null) {
                        val file = File(uri.path)
                        if (Build.VERSION.SDK_INT >= 24) {
                            takePicture.putExtra(
                                    MediaStore.EXTRA_OUTPUT,
                                    FileProvider.getUriForFile(
                                            this,
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
                2 -> {
                    dialog.dismiss()
                }
            }
        }
        builder.show()
    }

    private fun deleteImage() {
        photoFile = null
        image.setImageDrawable(null)
        btn_delete.gone()
    }

    private fun initToolbar() {
        setSupportActionBar(toolbar)
        toolbar.custom_title.text = getString(R.string.point_step1_title)
        toolbar.sub_title.text = getString(R.string.point_step1_sub_title)
    }

    private fun initWatchers() {
        edit_name.addTextChangedListener(this)
        edit_phone.addTextChangedListener(this)
    }


    override fun afterTextChanged(p0: Editable?) {
        if (hasEmptyFields(edit_name)&& edit_phone.text.toString().count() == 18) {
            btn_next.setBackgroundResource(R.drawable.rounded_shape_green_12dp_8cc341)
            btn_next.isEnabled = true
        } else {
            btn_next.setBackgroundResource(R.drawable.rounded_shape_silver_12dp_7b818c)
            btn_next.isEnabled = false
        }
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }

    private fun configureViewModel() {
        viewModel.state.observe(this, Observer { state ->
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
                        is CheckUserModel -> {
                            if (state.data.is_exists) {
                                toast(getString(R.string.have_number_for_recycle))
                            } else {
                                cpModel.title = edit_name.text.toString()
                                cpModel.contact = clearPhoneNumb(edit_phone.text.toString())
                                val i = Intent(this, Step2Activity::class.java)
                                i.putExtra("point", true)
                                startActivityWithCleanBack(i)
                            }
                        }
                    }
                }
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_TAKE_PHOTO -> {
                    val uri = getPickImageResultUri(this, data, fileName)
                    val file = getNormalizedUri(this, uri)
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
                    val compressedFile = Compressor.compress(this@NewCollectionPointActivity, photoFile!!)
                    file.file = compressedFile
                    runOnUiThread(Runnable { btn_delete.visible() })

                }
            }
        }
    }

    private fun imageCrop(uri: Uri?) {
        CropImage.activity(uri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1, 1)
                .start(this)
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
}