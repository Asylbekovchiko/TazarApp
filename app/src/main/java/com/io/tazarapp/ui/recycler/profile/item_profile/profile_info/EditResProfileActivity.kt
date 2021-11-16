package com.io.tazarapp.ui.recycler.profile.item_profile.profile_info

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.io.tazarapp.R
import com.io.tazarapp.data.model.CityDataModel
import com.io.tazarapp.data.model.cp.CpFile
import com.io.tazarapp.data.model.cp.CpImageURl
import com.io.tazarapp.data.model.profile.CollectionMapSend
import com.io.tazarapp.data.model.profile.CoordsSend
import com.io.tazarapp.data.model.profile.ProfileRecyclerModel
import com.io.tazarapp.data.model.profile.ProfileSendRecyclerModel
import com.io.tazarapp.data.state.State
import com.io.tazarapp.ui.auth.changeNumber.ChangeNumActivity
import com.io.tazarapp.ui.auth.login.cityMap.City1MapActivity
import com.io.tazarapp.ui.recycler.schedule.ScheduleViewModel
import com.io.tazarapp.utils.*
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import id.zelory.compressor.Compressor
import kotlinx.android.synthetic.main.activity_edit_res_profile.*
import kotlinx.android.synthetic.main.activity_edit_res_profile.btn_delete
import kotlinx.android.synthetic.main.custom_toolbar.*
import kotlinx.android.synthetic.main.custom_toolbar.view.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File

class EditResProfileActivity : BaseActivity() {
    var city2: String? = null
    var address: String? = null
    var location: ArrayList<Double>? = null
    var cityId: Int? = null
    lateinit var phoneNum: String
    var image: String = ""
    var idNum: Int = 0
    private var list: CollectionMapSend? = null
    private var mainList: ProfileSendRecyclerModel? = null
    var mainPhotoFile: MultipartBody.Part? = null
    private var fileName: String = ""
    private val viewModel: ScheduleViewModel by viewModel()
    private val viewModelMain: ResProfileViewModel by viewModel()
    private val cityData: CityDataModel by inject()

    // collection_point
    private val cpFile: CpFile by inject()
    private var cpFileName = ""
    private var photoFile: File? = null
    private var isCpImage = false
    private var imagePermissionsGranted = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_res_profile)
        initToolbar()
        initViewModel()
        listeners()
    }

    override fun handleCustomError(message: String) {
        val err = message.split("[","]")
        if (err.size > 1) toast(err[1])
    }

    override fun onResume() {
        super.onResume()
        if (cityData.isNew) {
            val coordsSplit = cityData.location[0]?.split(",")
            location = arrayListOf(coordsSplit!![0].toDouble(), coordsSplit[1].toDouble())
            address = cityData.address[0].toString()
            city2 = cityData.city[0].toString()
            tv_city_prof.text = cityData.address[0].toString()
        }
    }

    private fun listeners() {
        const_phone_edit_profile.setOnClickListener {
            startActivity<ChangeNumActivity>()
        }
        toolbar.setNavigationOnClickListener {
            finish()
        }
        const_city_edit_profile.setOnClickListener {
            startActivity<City1MapActivity>()
        }

        btn_delete.setOnClickListener { deleteImage() }

        cp_image.setOnClickListener {
            isCpImage = true
            Dexter.initialize(this)
            Dexter.checkPermissions(
                    object : MultiplePermissionsListener {
                        override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                            imagePermissionsGranted = if (report.grantedPermissionResponses.size == 3) {
                                selectImage()
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
        tv_add_pic.setOnClickListener {
            isCpImage = false
            Dexter.initialize(this)
            Dexter.checkPermissions(
                object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                        if (report.grantedPermissionResponses.size == 3) {
                            selectImage()
                        } else {
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
        btn_update_profile.setOnClickListener {
            if (photoFile != null) {
                viewModelMain.sendCp(cpFile)
            } else {
                sendData(null)
            }
        }
    }

    private fun sendData(cpUrl: String?) {
        list = if (!address.isNullOrEmpty() && !location.isNullOrEmpty()) {
            CollectionMapSend(
                    cityData.address[0].toString(),
                    CoordsSend("Point", location!!)
            )
        } else {
            null
        }

        mainList = if (!city2.isNullOrEmpty()) {
            ProfileSendRecyclerModel(
                    tv_name_et_profile.text.toString(),
                    tv_desc_et_profile.text.toString(),
                    "$city2",cpUrl, clearPhoneNumb(tv_phone_edit_profile.text.toString()) ,list
            )
        } else {
            ProfileSendRecyclerModel(
                    tv_name_et_profile.text.toString(),
                    tv_desc_et_profile.text.toString(),
                    "$cityId",cpUrl, clearPhoneNumb(tv_phone_edit_profile.text.toString()),null
            )
        }

        when (mainPhotoFile) {
            null -> viewModelMain.updateRecyclerProfile(idNum, mainList)
            else -> {
                viewModelMain.updateRecyclerProfileImage(mainPhotoFile!!)
                viewModelMain.updateRecyclerProfile(idNum, mainList)
            }
        }
    }

    private fun deleteImage() {
        photoFile = null
        cp_image.setImageDrawable(null)
        btn_delete.gone()
    }

    private fun initViewModel() {
        viewModel.getMainProfile()
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
                        when (state.data) {
                            is ProfileRecyclerModel -> {
                                with(state.data) {
                                    idNum = id
                                    Glide.with(this@EditResProfileActivity)
                                        .load(image)
                                        .circleCrop().into(img_profile_edit_info)
                                    tv_name_et_profile.setText(title)
                                    phoneNum = "$phone"
                                    tv_phone_edit_profile.text = maskPhoneNumb(phone)
                                    tv_desc_et_profile.setText(description)
                                    cityId = city_id

                                    if (address.isNullOrEmpty()) {
//                                        tv_city_prof.text =
//                                            collection_map[0]?.address.toString()
                                    } else {
                                        tv_city_prof.text = address.toString()
                                    }
                                }
                            }
                        }
                    }
                }
            }
        })
        viewModelMain.state.observe(this, Observer { state ->
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
                        Log.e("here1", state.message)
                    }
                    is State.SuccessObjectState<*> -> {
                        if (state.data is CpImageURl) {
                            sendData(state.data.image)
                            cpFile.clearData()
                        } else {
                            finish()
                        }
                    }
                }
            }
        })
    }

    private fun initToolbar() {
        setSupportActionBar(toolbar)
        toolbar.custom_title.text = getString(R.string.change)
    }

    private fun selectImage() {
        val options = resources.getStringArray(R.array.makePhoto)
        val builder =
            AlertDialog.Builder(this)
        builder.setTitle(resources.getString(R.string.makePhotoTitle))
        builder.setItems(options) { dialog, item ->
            when (item) {
                0 -> {
                    if (isCpImage) this.fileName = System.nanoTime().toString()
                    else this.cpFileName = System.nanoTime().toString()

                    val takePicture = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

                    val uri = if (isCpImage) getCaptureImageOutputUri(this, this.cpFileName)
                    else getCaptureImageOutputUri(this, this.fileName)

                    if (uri != null) {
                        val file = File(uri.path)
                        if (Build.VERSION.SDK_INT >= 24) {
                            takePicture.putExtra(
                                MediaStore.EXTRA_OUTPUT,
                                FileProvider.getUriForFile(
                                    this, "com.io.tazarapp.provider",
                                    file
                                )
                            )
                            takePicture.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                            takePicture.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        } else {
                            takePicture.putExtra(MediaStore.EXTRA_OUTPUT, uri)
                        }
                    }
                    startActivityForResult(takePicture, 0)
                }
                1 -> {
                    val pickPhoto = Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    )
                    startActivityForResult(pickPhoto, 1)
                }
                2 -> {
                    dialog.dismiss()
                }
            }
        }
        builder.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                0 -> {
                    val uri = if (isCpImage) getPickImageResultUri(this, data, cpFileName)
                    else getPickImageResultUri(this, data, fileName)

                    val file = getNormalizedUri(this, uri)
                    Log.e("cr","before")
                    if (isCpImage) {
                        CropImage.activity(file)
                                .setGuidelines(CropImageView.Guidelines.ON)
                                .setAspectRatio(1, 1)
                                .start(this)

                    } else {
                        imageCrop(file, this)
                    }
                }
                1 -> if (resultCode == Activity.RESULT_OK && data != null) {
                    val selectedImage = data.data
                    val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
                    if (selectedImage != null) {
                        val cursor = contentResolver.query(
                                selectedImage,
                                filePathColumn, null, null, null
                        )
                        if (cursor != null) {
                            cursor.moveToFirst()
                            val columnIndex = cursor.getColumnIndex(filePathColumn[0])
                            val picturePath = cursor.getString(columnIndex)
                            val image = BitmapFactory.decodeFile(picturePath)
                            val uri = ImageExt().getImageUriFromBitmap(this, image)
                            if (isCpImage) {
                                CropImage.activity(uri)
                                        .setGuidelines(CropImageView.Guidelines.ON)
                                        .setAspectRatio(1, 1)
                                        .start(this)

                            } else {
                                imageCrop(uri, this)
                            }
                            cursor.close()
                        }
                    }

                }
            }
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == Activity.RESULT_OK) {
                Log.e("cr","in")
                val resultUri = result.uri
                if (isCpImage) {
                    cp_image.setImage(resultUri.toString())
                    photoFile = File(resultUri.path.toString())
                    GlobalScope.launch {
                        val compressedFile = Compressor.compress(this@EditResProfileActivity, photoFile!!)
                        cpFile.file = compressedFile
                        runOnUiThread(Runnable { btn_delete.visible() })
                    }
                } else {
                    img_profile_edit_info.setImage(resultUri.toString())
                    val photoFile = File(resultUri.path.toString())
                    GlobalScope.launch {
                        val compressedFile = Compressor.compress(this@EditResProfileActivity, photoFile)
                        val fileParts = ImageExt().multipartBodyGet(compressedFile.path)
                        mainPhotoFile = fileParts
                    }
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
                Log.e("error", error.message.toString())
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cpFile.clearData()
    }
}