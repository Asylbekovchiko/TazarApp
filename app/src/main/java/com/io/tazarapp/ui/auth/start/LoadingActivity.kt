package com.io.tazarapp.ui.auth.start

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.io.tazarapp.BuildConfig
import com.io.tazarapp.R
import com.io.tazarapp.data.model.Version
import com.io.tazarapp.data.state.State
import com.io.tazarapp.ui.auth.login.LoginActivity
import com.io.tazarapp.ui.auth.slider.SliderActivity
import com.io.tazarapp.ui.auth.version_check.VersionCheckViewModel
import com.io.tazarapp.utils.BaseActivity
import com.io.tazarapp.utils.LanguagePref
import com.io.tazarapp.utils.gone
import com.io.tazarapp.utils.setLocale
import kotlinx.android.synthetic.main.activity_loading.*
import kotlinx.android.synthetic.main.update_layout.view.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoadingActivity : BaseActivity() {
    private lateinit var mHandler: Handler
    private lateinit var mRunnable: Runnable
    private val viewModel: VersionCheckViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme2)
        setLocale(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loading)
        Glide.with(this).load(R.drawable.ic_new_app_logo).into(image)
        Glide.with(this).load(R.drawable.ic_app_logo_title).into(ic_title)
        val animation: Animation =
            AnimationUtils.loadAnimation(applicationContext, R.anim.clockwise)
        cv_image.startAnimation(animation)
        initViewModel()
    }

    private fun initViewModel() {
        viewModel.state.observe(this, Observer { state ->
            if (state != null) {
                when (state) {
                    is State.LoadingState -> when {
                        state.isLoading -> showDialog()
                        else -> hideDialog()
                    }
                    is State.ErrorState -> handleError(state)
                    is State.SuccessObjectState<*> -> checkVersion(state.data as Version)
                }
            }
        })
        viewModel.getVersion()
    }

    private fun checkLanguage() {
        mHandler = Handler()
        mRunnable = Runnable {
            val intent = if(LanguagePref.LanguageModule().getLanguage(this) == "empty") {
                Intent(this@LoadingActivity, LanguageActivity::class.java)
            } else {
                checkSlider()
            }
            startActivity(intent)
            overridePendingTransition(R.anim.one, R.anim.two)
            finish()
        }
        mHandler.postDelayed(mRunnable, 500 * 2 * 1)
    }

    private fun checkSlider() : Intent {
        return if(LanguagePref.FirstTimeModule().isFirstTime(this)){
            Intent(this@LoadingActivity, SliderActivity::class.java)
        } else {
            Intent(this@LoadingActivity, LoginActivity::class.java)
        }
    }

    private fun checkVersion(version: Version) =
        if (version.version != BuildConfig.VERSION_NAME && version.version.isNotEmpty()) {
            showAlert(version.version, version.force_update)
        } else {
            checkLanguage()
        }

    private fun showAlert(version: String, forceUpdate: Boolean) {

        val builder = AlertDialog.Builder(this)
        val view = LayoutInflater.from(this).inflate(
                R.layout.update_layout,
                findViewById<ConstraintLayout>(R.id.update_dialog)
        )

        builder.setView(view)
        val dialog = builder.create()

        view.apply {
            message.text = resources.getString(R.string.update_message, version)
            update.setOnClickListener {
                try {
                    startActivity(
                            Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse("market://details?id=$packageName")
                            )
                    )
                } catch (e: ActivityNotFoundException) {
                    startActivity(
                            Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
                            )
                    )
                }
            }
            if (!forceUpdate) {
                skip.setOnClickListener {
                    dialog.dismiss()
                    checkLanguage()
                }
            }else {
                skip.gone()
            }
        }

        if (dialog.window != null) {
            dialog.window!!.setBackgroundDrawable(ColorDrawable(0))
        }

        dialog.show()
    }
}
