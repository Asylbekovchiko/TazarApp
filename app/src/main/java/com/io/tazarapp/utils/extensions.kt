package com.io.tazarapp.utils

import android.Manifest
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Activity
import androidx.lifecycle.MutableLiveData
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.ShapeDrawable
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.text.Editable
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.astritveliu.boom.Boom
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.io.tazarapp.App
import com.io.tazarapp.BuildConfig
import com.io.tazarapp.R
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.StringBuilder
import java.io.File
import java.net.InetAddress
import java.net.UnknownHostException
import java.text.SimpleDateFormat
import java.util.*


fun timestamp() = System.currentTimeMillis()

/*
fun Context.takePictureIntent(fileName: String): Intent {
    var intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

    val imageUri = this.getCaptureImageOutputUri(fileName)
    if (imageUri != null) {
        val image = File(imageUri.path)
        if (Build.VERSION.SDK_INT >= 24) {
            intent.putExtra(MediaStore.EXTRA_OUTPUT,
                    FileProvider.getUriForFile(this,
                            BuildConfig.APPLICATION_ID + ".provider",
                            image))
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        } else intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)

    } else intent = Intent()
    return intent
}
*/

suspend fun hasInternet(): Boolean {
    return try {
        withContext(Dispatchers.Default) { InetAddress.getByName("google.com") }
        true
    } catch (e: UnknownHostException) {
        false
    }
}

val Int.toDp: Int
    get() = (this / Resources.getSystem().displayMetrics.density).toInt()
val Int.toPx: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()

val displayHeight = Resources.getSystem().displayMetrics.heightPixels
val displayWidth = Resources.getSystem().displayMetrics.widthPixels


inline fun <reified T : Activity> Activity.startActivityForResult(
    requestCode: Int,
    action: Intent.() -> Unit = {}
) {
    val intent = Intent(this, T::class.java)
    intent.action()
    startActivityForResult(intent, requestCode)
}

inline fun <reified T : Activity> Fragment.startActivityForResult(
    requestCode: Int,
    action: Intent.() -> Unit = {}
) {
    val intent = Intent(activity, T::class.java)
    intent.action()
    startActivityForResult(intent, requestCode)
}

inline fun <reified T : Activity> Context.startActivity(
    vararg flags: Int,
    action: Intent.() -> Unit = {}
) {
    val intent = Intent(this, T::class.java)
    flags.forEach { intent.addFlags(it) }
    intent.action()
    startActivity(intent)
}

inline fun <reified T : Activity> Fragment.startActivity(
    vararg flags: Int,
    action: Intent.() -> Unit = {}
) {
    val intent = Intent(activity, T::class.java)
    flags.forEach { intent.addFlags(it) }
    intent.action()
    startActivity(intent)
}

inline fun <T> MutableLiveData<T>.notifyObserver() {
    this.value = this.value
}

fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }
    })
}

fun View.setBackgroundColor(color: String) {
    val bg = background
    try {
        when (bg) {
            is GradientDrawable -> bg.setColor(Color.parseColor(color))
            is ShapeDrawable -> bg.paint.color = Color.parseColor(color)
            is ColorDrawable -> bg.color = Color.parseColor(color)
        }
    } catch (e: Exception) {

    }
}

inline fun Date?.parse(format: String): String {
    return if (this != null) SimpleDateFormat(format, Locale.getDefault()).format(this)
    else ""
}

fun setLocale(context: Context){
    Log.e("LANG",LanguagePref.LanguageModule().getLanguage(context))
    if (LanguagePref.LanguageModule().getLanguage(context) != "empty") {
        val locale: Locale = Locale(LanguagePref.LanguageModule().getLanguage(context))
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            updateResourcesLocaleLegacy(context, locale)
        }else{
            updateResourcesLocaleLegacy(context,locale)
        }
    }
}

@TargetApi(Build.VERSION_CODES.N)
private fun updateResourcesLocale(context: Context, locale: Locale): Context {
    val configuration = context.resources.configuration
    configuration.setLocale(locale)
    return context.createConfigurationContext(configuration)
}


@Suppress("DEPRECATION")
private fun updateResourcesLocaleLegacy(context: Context, locale: Locale): Context {
    val resources = context.resources
    val configuration = resources.configuration
    configuration.locale = locale
    resources.updateConfiguration(configuration, resources.displayMetrics)
    return context
}

@SuppressLint("SimpleDateFormat")
fun formatDateToday(): String {

    var date2 = Date()
    val sdf1 = SimpleDateFormat("dd MMMM yyyy")

    return sdf1.format(date2!!)
}

@SuppressLint("SimpleDateFormat")
fun formatDate(date: String): String {
    var dateWithoutUTC= date.split(".")[0]
    val sdf = SimpleDateFormat("yyyy-MM-ddThh:mm:ss.SS")
    var date2 = sdf.parse(dateWithoutUTC)
    val sdf1 = SimpleDateFormat("dd MMMM yyyy")

    return sdf1.format(date2!!)
}

fun setRoundedImageCenterCrop(radius: Int, imageView: ImageView, url: String, context: Context) {
    if (url != null) {
        Glide.with(context).load(url)
            .transform(CenterCrop(), RoundedCorners(convertDpToPixel(radius, context).toInt()))
            .into(imageView)
    }

}

fun setRoundedImageFitCentre(radius: Int, imageView: ImageView, url: String, context: Context) {

    Glide.with(context).load(url)
        .transform(FitCenter(), RoundedCorners(convertDpToPixel(radius, context).toInt()))
        .into(imageView)

}

fun convertDpToPixel(dp: Int, context: Context): Float {
    return dp * (context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
}

fun convertPixelsToDp(px: Float, context: Context): Float {
    return px / (context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
}

fun boom(vararg views: View) {

    views.forEach { view ->
        Boom(view)
    }
}


// map extention

const val FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION
const val COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION
const val LOCATION_PERMISSION_REQUEST_CODE = 1234
const val DEFAULT_ZOOM = 12f

val permissions = listOf(
    FINE_LOCATION,
    COURSE_LOCATION
).toTypedArray()

private fun checkFineLocation(context: Context) =
    ActivityCompat.checkSelfPermission(context, FINE_LOCATION)

private fun checkCoarceLocation(context: Context) =
    ActivityCompat.checkSelfPermission(context, COURSE_LOCATION)

fun isCheckPermissionComplete(context: Context) =
    if (checkFineLocation(context) == PackageManager.PERMISSION_GRANTED) {
        checkCoarceLocation(context) == PackageManager.PERMISSION_GRANTED
    } else {
        false
    }

fun isCheckPermissionFailed(context: Context) =
    checkFineLocation(context) != PackageManager.PERMISSION_GRANTED &&
            checkCoarceLocation(context) != PackageManager.PERMISSION_GRANTED

fun bitmapDescriptorFromVector(context: Context, vectorResId:Int): BitmapDescriptor {
    var vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
    vectorDrawable!!.setBounds(0, 0, vectorDrawable.intrinsicWidth, vectorDrawable.intrinsicHeight);
    var bitmap = Bitmap.createBitmap(vectorDrawable.intrinsicWidth, vectorDrawable.intrinsicHeight, Bitmap.Config.ARGB_8888);
    var canvas =  Canvas(bitmap);
    vectorDrawable.draw(canvas);
    return BitmapDescriptorFactory.fromBitmap(bitmap);
}
// 2020-09-10T11:23:41.011069+06:00 ---> 14 окт 2020 - 14:00
fun reFormatDate(context : Context,date: String) : String {
    return try {
        val day = date.substring(8,10)
        val month = date.substring(5,7)
        val year = date.substring(0,4)
        val time = date.substring(11,16)

        val builder = StringBuilder("")
        builder.append("$day ")

        val arrayMonth = context.resources.getStringArray(R.array.month)
        builder.append(arrayMonth[month.toInt() - 1] + " ")
        builder.append("$year - ")
        builder.append(time)
        builder.toString()
    }catch (e : Exception) {
        return "-"
    }
}

fun formatDateScData(date : String) : String {
    return try {
        val day = date.substring(8,10)
        val month = date.substring(5,7)
        val year = date.substring(2,4)
        val time = date.substring(11,16)

        val builder = StringBuilder("")
        builder.append("$day/")
        builder.append("$month/")
        builder.append("$year - ")
        builder.append(time)
        builder.toString()
    }catch (e : Exception) {
        return "-"
    }
}


//Image get funcs

fun getPickImageResultUri(context: Context, data: Intent?, fileName: String): Uri? {
    var isCamera = true
    if (data != null && data.data != null) {
        val action = data.action
        isCamera = action != null
    }
    return if (isCamera || data!!.data == null)
        getCaptureImageOutputUri(context, fileName)
    else
        data.data
}
fun getCaptureImageOutputUri(context: Context, fileName: String): Uri? {
    var outputFileUri: Uri? = null
    val getImage = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    if (getImage != null) {
        outputFileUri = Uri.fromFile(File(getImage.path, "$fileName.jpg"))
    }
    return outputFileUri
}

fun getNormalizedUri(context: Context, uri: Uri?): Uri? {
    return if (uri != null && uri.toString().contains("content:"))
        Uri.fromFile(getPath(context, uri, MediaStore.Images.Media.DATA))
    else
        uri
}

fun getPath(context: Context, uri: Uri, column: String): File? {
    val columns = arrayOf(column)
    val cursor = context.contentResolver.query(uri, columns, null, null, null) ?: return null
    val column_index = cursor.getColumnIndexOrThrow(column)
    cursor.moveToFirst()
    val path = cursor.getString(column_index)
    cursor.close()
    return File(path)
}

fun imageCrop(uri: Uri?, activity: Activity) {
    CropImage.activity(uri)
        .setGuidelines(CropImageView.Guidelines.ON)
        .setAspectRatio(5, 5)
        .setCropShape(CropImageView.CropShape.OVAL)
        .start(activity)
}
fun getDay(day:Int,context: Context):String {

    return when(day){
        1->context.getString(R.string.pn)
        2->context.getString(R.string.vt)
        3->context.getString(R.string.sr)
        4->context.getString(R.string.cht)
        5->context.getString(R.string.pt)
        6->context.getString(R.string.sb)
        7->context.getString(R.string.vs)
        else->context.getString(R.string.pn)
    }

}


fun getByTwoFromListMap(state: List<Double>, context: Context): ArrayList<LatLng> {
    val latLng = ArrayList<LatLng>()
    try {
        for (i in state.indices) {
            if (i % 2 == 0) {
                latLng.add(
                    LatLng(state[i], state[i + 1])
                )
            }
        }
    } catch (e: ArrayIndexOutOfBoundsException) {
        context.toast("Неверные координаты")
        Log.e("message", e.message.toString())
    }

    return latLng
}