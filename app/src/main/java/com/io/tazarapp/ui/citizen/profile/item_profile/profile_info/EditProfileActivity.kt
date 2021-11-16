package com.io.tazarapp.ui.citizen.profile.item_profile.profile_info

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
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
import com.io.tazarapp.data.model.auth.CityModel
import com.io.tazarapp.data.model.profile.ProfileMainModel
import com.io.tazarapp.data.state.State
import com.io.tazarapp.ui.auth.changeNumber.ChangeNumActivity
import com.io.tazarapp.ui.auth.login.city.CityActivity
import com.io.tazarapp.ui.citizen.profile.ProfileMainViewModel
import com.io.tazarapp.utils.*
import com.theartofdev.edmodo.cropper.CropImage
import id.zelory.compressor.Compressor
import kotlinx.android.synthetic.main.activity_edit_profile.*
import kotlinx.android.synthetic.main.custom_toolbar.*
import kotlinx.android.synthetic.main.custom_toolbar.view.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import org.koin.androidx.viewmodel.ext.android.viewModel
import pub.devrel.easypermissions.EasyPermissions
import java.io.File

class EditProfileActivity : BaseActivity() {
    private var city: CityModel? = null
    private val CITY_REQUEST_CODE = 312
    lateinit var phoneNum: String
    var image: String = ""
    var idNum: Int = 0
    var mainPhotoFile: MultipartBody.Part? = null
    private var fileName: String = ""
    private val viewModel: ProfileViewModel by viewModel()
    private val mainViewModel: ProfileMainViewModel by viewModel()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)
        initToolbar()
        initViewModel()
        listeners()
    }

    private fun listeners() {
        const_phone_edit_profile.setOnClickListener {
            startActivity<ChangeNumActivity>()
        }
        toolbar.setNavigationOnClickListener {
            finish()
        }
        const_city_edit_profile.setOnClickListener {
            val intent = Intent(this, CityActivity::class.java)
            intent.putExtra(PARENT, true)
            intent.putExtra(CITY, city)
            this.startActivityForResult(
                intent,
                CITY_REQUEST_CODE
            )
        }
        tv_add_pic.setOnClickListener {
            if (EasyPermissions.hasPermissions(this, *galleryPermissions)) {
                selectImage()
            } else {
                EasyPermissions.requestPermissions(
                    this, "Access for storage",
                    101, *galleryPermissions
                )
                if (EasyPermissions.hasPermissions(this, *galleryPermissions)) {
                    selectImage()
                }
            }
        }
        btn_update_profile.setOnClickListener {
            when (mainPhotoFile) {
                null -> viewModel.updateProfileInfo(
                    idNum,
                    city?.id,
                    tv_name_et_profile.text.toString()
                )
                else -> {
                    viewModel.updateProfileImage(mainPhotoFile!!)
                    viewModel.updateProfileInfo(
                        idNum,
                        city?.id,
                        tv_name_et_profile.text.toString()
                    )
                }
            }
        }
    }

    private fun initViewModel() {
        mainViewModel.getMainProfile()
        mainViewModel.state.observe(this, Observer { state ->
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
                            is ProfileMainModel -> {
                                with(state.data) {
                                    idNum = id
                                    tv_city_prof.text = city
                                    Glide.with(this@EditProfileActivity).load(image)
                                        .circleCrop().into(img_profile_edit_info)
                                    tv_name_et_profile.setText(name)
                                    phoneNum = phone
                                    tv_phone_edit_profile.text = maskPhoneNumb(phone)
                                }
                            }
                        }
                    }
                }
            }
        })
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
                    is State.SuccessObjectState<*> -> {
                        finish()
                    }
                }
            }
        })
    }

    private val galleryPermissions = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

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
                    fileName = System.nanoTime().toString()
                    val takePicture = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

                    val uri = getCaptureImageOutputUri(this, fileName)
                    if (uri != null) {
                        val file = File(uri.path)
                        if (Build.VERSION.SDK_INT >= 24) {
                            takePicture.putExtra(
                                MediaStore.EXTRA_OUTPUT,
                                FileProvider.getUriForFile(
                                    this, "com.io.tazarapp.provider", file
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
                CITY_REQUEST_CODE -> {
                    city = data?.getSerializableExtra(CITY) as CityModel
                    tv_city_prof.text = city!!.name
                }
            }
        }
        Log.e("RESULT", "resultcode - $resultCode \n data - $data")
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                0 -> {
                    Log.e("picked", "photo - ${fileName.toUri()}")

                    val uri = getPickImageResultUri(this, data, fileName)
                    val file = getNormalizedUri(this, uri)
                    imageCrop(file, this)
                }
                1 -> if (resultCode == Activity.RESULT_OK && data != null) {
                    Log.e("DATA",data.toString())
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
                            imageCrop(uri, this)
                            cursor.close()
                        }
                    }
                }
            }
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == Activity.RESULT_OK) {
                val resultUri = result.uri
                Glide.with(this)
                    .load(resultUri)
                    .circleCrop()
                    .into(img_profile_edit_info)
                val photoFile = File(resultUri.path.toString())
                GlobalScope.launch {
                    val compressedFile = Compressor.compress(this@EditProfileActivity, photoFile)
                    val fileParts = ImageExt().multipartBodyGet(compressedFile.path)
                    mainPhotoFile = fileParts
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
                Log.e("error", error.message.toString())
            }
        }
    }

    override fun handleCustomError(message: String) {
        val err = message.split("[","]")
        if (err.size > 1) toast(err[1])
    }
}