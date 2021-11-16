package com.io.tazarapp.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.text.Editable
import android.text.Html
import android.text.Spanned
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.webkit.WebView
import android.widget.*
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.io.tazarapp.R
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


fun View.getParentActivity(): AppCompatActivity? {
    var context = this.context
    while (context is ContextWrapper) {
        if (context is AppCompatActivity) {
            return context
        }
        context = context.baseContext
    }
    return null
}

fun EditText.validate(
    validator: (String) -> Boolean,
    message: String
): Boolean {
    val flag = validator(this.text.toString())
    this.error = if (flag) null else message

    return flag
}

fun TextView.validate(
    validator: (String) -> Boolean,
    message: String
): Boolean {
    val flag = validator(this.text.toString())
    this.error = if (flag) null else message

    return flag
}

fun EditText.text(
    message: String?
) {
    if (!message.isNullOrEmpty()) {
        this.setText(message)
    }
}

fun EditText.cursorToEnd(
) {
    setSelection(text.toString().length)
}

fun TextView.text(
    message: String?
) {
    if (!message.isNullOrEmpty()) this.text = message
}

fun EditText.toInteger(
    message: String?
) {
    if (!message.isNullOrEmpty()) {
        this.setText(message)
    }
}

fun Context.toast(message: String, duration: Int = Toast.LENGTH_LONG): Toast =
    Toast.makeText(this, message, duration).apply {
        show()
    }

fun Context.toast(@StringRes resId: Int, duration: Int = Toast.LENGTH_LONG): Toast =
    Toast.makeText(this, resId, duration).apply {
        show()
    }

fun View.gone() {
    visibility = View.GONE
}

fun View.visible() {
    visibility = View.VISIBLE
}

fun Activity.hideKeyboard() {
    val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    //Find the currently focused view, so we can grab the correct window token from it.
    var view = currentFocus
    //If no view currently has focus, create a new one, just so we can grab a window token from it
    if (view == null) {
        view = View(this)
    }
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}

fun Fragment.hideKeyboard() {
    val imm = activity?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    //Find the currently focused view, so we can grab the correct window token from it.
    var view = activity?.currentFocus
    //If no view currently has focus, create a new one, just so we can grab a window token from it
    if (view == null) {
        view = View(activity)
    }
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}

fun Context.actionCall(phone: String) {
    val dialIntent = Intent(Intent.ACTION_DIAL)
    dialIntent.data = Uri.parse("tel:$phone")
    startActivity(dialIntent)
}


fun View.hideKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}

fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}


fun parseDateFromServer(inputDate: String?): String? {
    val DATE_FORMAT_I = "yyyy-MM-dd"
    val DATE_FORMAT_O = "dd-MM-yyyy"
    val formatInput = SimpleDateFormat(DATE_FORMAT_I)
    val formatOutput = SimpleDateFormat(DATE_FORMAT_O)
    var date: Date? = null
    try {
        date = formatInput.parse(inputDate)
    } catch (e: ParseException) {
        e.printStackTrace()
    }
    return formatOutput.format(date)
}


fun parseDateToServer(inputDate: String?): String {
    val DATE_FORMAT_I = "dd-MM-yyyy"
    val DATE_FORMAT_O = "yyyy-MM-dd"
    val formatInput = SimpleDateFormat(DATE_FORMAT_I)
    val formatOutput = SimpleDateFormat(DATE_FORMAT_O)
    var date: Date? = null
    try {
        date = formatInput.parse(inputDate)
    } catch (e: ParseException) {
        e.printStackTrace()
    }
    return formatOutput.format(date)
}

fun ImageView.setRoundedImage(
    radius: Int,
    url: String?,
    context: Context,
    scale: BitmapTransformation
) {

    Glide.with(context).load(url)
        .transform(scale, RoundedCorners(radius))
        .into(this)

}

fun ImageView.setImage(url: String?) =
    Glide.with(this.context).load(url).placeholder(R.drawable.rounded_shape_silver_12dp_7b818c)
        .into(this)

fun ImageView.setImageIfNullGone(url: String?) {
    if (null == url) {
        this.gone()
    } else {
        Glide.with(this.context).load(url).placeholder(R.drawable.rounded_shape_silver_12dp_7b818c)
            .into(this)
    }
}


fun initInternet(context: Context): Boolean {
    val cm =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    @SuppressLint("MissingPermission") val networkInfo = cm.activeNetworkInfo
    return networkInfo != null && networkInfo.isConnectedOrConnecting
}

fun EditText.afterTxtChanged(cb: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            cb(s.toString())
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    })
}

//TODO transfer the extents to extension.kt (if that extends are not extended View class and it children classes)
fun hasEmptyFields(vararg list: EditText): Boolean {
    list.forEach { if (it.text.toString().isEmpty()) return false }
    return true
}

fun clearPhoneNumb(phoneNumber: String): String {
    val ph = phoneNumber
        .replace("(", "")
        .replace(")", "")
        .replace(" ", "")
        .trim()
    return ph
}

fun maskPhoneNumb(phoneNumber: String?): String {
    var ph = phoneNumber
    return try {
        if (phoneNumber != null) {
            ph = ph?.substring(0, 4) + " (" + ph?.substring(4, 7) + ") " + ph?.substring(
                    7,
                    9
            ) + " " + ph?.substring(9, 11) + " " + ph?.substring(11, 13)
            ph
        } else {
            ""
        }
    } catch (e : Exception) {
        phoneNumber ?: ""
    }
}

fun Button.activate() {
    this.isEnabled = true
    this.setBackgroundResource(R.drawable.rounded_shape_green_12dp_8cc341)
}

fun Button.disActivate() {
    this.isEnabled = false
    this.setBackgroundResource(R.drawable.rounded_shape_silver_12dp_7b818c)
}

fun setTextHTML(html: String): Spanned {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY)
    } else {
        Html.fromHtml(html)
    }
}

fun WebView.showFromUrl(state: String) {
    val html_kg =
        "<html><body>$state</body></html>"
    return this.loadDataWithBaseURL(
        null, "<style>img{display: inline;height: auto;max-width: 100%;}</style>$html_kg",
        "text/html",
        "UTF-8",
        null
    )
}

fun WebView.showFromUrlF2F1F4(state: String) {
    val html_kg =
        """<html><body>$state</body></html>"""
    return this.loadDataWithBaseURL(
        null, "<style>img{display: inline;height: auto;max-width: 100%;} body { background: #F2F1F4; }</style>$html_kg",
        "text/html",
        "UTF-8",
        null
    )
}


fun LinearLayout.progressShow(
    textInside: TextView,
    experience: Int,
    upper_line: Int,
    bottom_line: Int
): Boolean {
    val max = upper_line - bottom_line
    var current = experience - bottom_line
    return this.post {
        when {
            current > max -> current = max
            current < 0 -> current = 0
        }

        val allWidth = this.width
        val txtPx = (current * allWidth / max)
        textInside.layoutParams = LinearLayout.LayoutParams(
            txtPx,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
    }
}

class NextRequestFocusTextWatcher(
    private val own: EditText,
    private val btnSend: Button,
    private val fields: Array<EditText>,
    private val fieldsLast: Array<EditText>
) : TextWatcher {

    override fun afterTextChanged(p0: Editable?) {
        Log.e("After", p0.toString())
        if (!own.text.isNullOrEmpty()) {
            own.setBackgroundResource(R.drawable.rounded_shape_white_12dp_brd_2dp8cc341_ffffff)
            checkHasEmpties()
        } else {
            backWay()
            own.setBackgroundResource(R.drawable.rounded_shape_silver_12dp_f2f1f4)
        }
    }

    private fun checkHasEmpties() {
        fields.forEach {
            if (it.text.isNullOrEmpty()) {
                it.requestFocus()
                btnSend.disActivate()
                return
            }
        }
        btnSend.activate()
    }

    private fun backWay() {
        fieldsLast.forEach {
            if (!it.text.isNullOrEmpty()) {
                it.requestFocus()
                return
            }
        }
        btnSend.disActivate()
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        Log.e("BEFORE", p0.toString())
    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        Log.e("OnText", p0.toString())
    }
}
