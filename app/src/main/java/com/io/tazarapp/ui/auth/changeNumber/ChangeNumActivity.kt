package com.io.tazarapp.ui.auth.changeNumber

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import com.io.tazarapp.R
import com.io.tazarapp.data.model.profile.ProfileMainModel
import com.io.tazarapp.data.state.State
import com.io.tazarapp.ui.citizen.profile.ProfileMainViewModel
import com.io.tazarapp.utils.*
import kotlinx.android.synthetic.main.activity_change_num.*
import kotlinx.android.synthetic.main.custom_toolbar.*
import kotlinx.android.synthetic.main.custom_toolbar.view.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class ChangeNumActivity : BaseActivity() {
    private val viewModel: ProfileMainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_num)
        initToolbar()
        listeners()
        initViewModel()
    }

    private fun initToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.custom_title.text = getString(R.string.change_number)
    }

    override fun handleCustomError(message: String) {
        toast(message)
    }

    private fun listeners() {
        btn_change_number.setOnClickListener {
            val intent = Intent(this, AuthCodeActivity::class.java)
            intent.putExtra(PHONE_NUMBER, clearPhoneNumb(tv_change_number.text.toString()))
            intent.putExtra(ACTIVITY_WAY, MAIN_INTENT)
            startActivity(intent)
            finish()
        }
        toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun initViewModel() {
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
                            is ProfileMainModel -> {
                                with(state.data) {
                                    tv_change_number.text = maskPhoneNumb(phone)
                                }
                            }
                        }
                    }
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()
        viewModel.getMainProfile()
    }
}