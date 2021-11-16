package com.io.tazarapp.ui.auth.start

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.io.tazarapp.R
import com.io.tazarapp.ui.auth.slider.SliderActivity
import com.io.tazarapp.utils.LanguagePref
import com.io.tazarapp.utils.setLocale
import kotlinx.android.synthetic.main.activity_language.*

class LanguageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme2)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_language)
        Glide.with(this).load(R.drawable.ic_circle_green_world).into(image)
        listeners()
    }

    private fun listeners() {
        cl_en.setOnClickListener { setLanguage("en") }
        cl_rus.setOnClickListener { setLanguage("ru") }
        cl_ky.setOnClickListener { setLanguage("ky") }
    }

    private fun setLanguage(lan: String) {
        LanguagePref.LanguageModule().saveLanguage(this,lan)
        LocaleManager().updateResources(this, LanguagePref.LanguageModule().getLanguage(this))
        setLocale(this)
        gotoNextPage()
    }

    private fun gotoNextPage() = if (LanguagePref.FirstTimeModule().isFirstTime(this)) {
        startActivity(Intent(this, SliderActivity::class.java))
        recreate()
    } else {
        val intent = Intent(this, LoadingActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}