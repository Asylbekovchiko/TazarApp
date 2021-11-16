package com.io.tazarapp.ui.auth.changeNumber

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import com.io.tazarapp.R
import com.io.tazarapp.data.model.auth.CheckUserModel
import com.io.tazarapp.data.state.State
import com.io.tazarapp.ui.auth.login.signIn.SignInViewModel
import com.io.tazarapp.utils.*
import dmax.dialog.SpotsDialog
import kotlinx.android.synthetic.main.activity_new_number.*
import kotlinx.android.synthetic.main.custom_toolbar.*
import kotlinx.android.synthetic.main.custom_toolbar.view.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class NewNumActivity : BaseActivity(), TextWatcher {
    var code: String = ""
    private val viewModel: SignInViewModel by viewModel()
    private val REQUEST_CODE_NEW_PHONE = 854

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_number)
        inits()
    }

    private fun inits() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.custom_title.text = getString(R.string.enter_new_number)
        dialog = SpotsDialog.Builder().setContext(this).setMessage(R.string.loading_message).build()
        et_phone_numb.addTextChangedListener(this)
        configureViewModel()
        listeners()
    }

    private fun listeners() {
        btn_new_number.setOnClickListener {
            val ee = clearPhoneNumb(et_phone_numb.text.toString())
            val phoneNumber = "+" + ccp.selectedCountryCode + ee
            if (TextUtils.isEmpty(ee)) {
                et_phone_numb.error = getString(R.string.enter_phone_number)
                return@setOnClickListener
            } else {
                if (phoneNumber.isNotEmpty()) {
                    viewModel.checkUser(clearPhoneNumb(phoneNumber))
                } else {
                    et_phone_numb.error = getString(R.string.enter_new_phone_number)
                }
            }
        }
        toolbar.setNavigationOnClickListener {
            finish()
        }
        tv_send_code_new_num.setOnClickListener { finish() }
    }

    override fun afterTextChanged(p0: Editable?) {
        if (et_phone_numb.text.toString().isNotEmpty()) {
            btn_new_number.activate()
        } else {
            btn_new_number.disActivate()
        }
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }

    private fun configureViewModel() {
        viewModel.state.observe(this@NewNumActivity, Observer {
            when (it) {
                is State.LoadingState -> {
                    when {
                        it.isLoading -> showDialog()
                        else -> hideDialog()
                    }
                }
                is State.ErrorState -> {
                    handleError(it)
                }
                is State.SuccessObjectState<*> -> {
                    when (it.data) {
                        is CheckUserModel -> {
                            if (it.data.is_exists) {
                                toast(getString(R.string.have_number))
                            } else {
                                val intent =
                                    Intent(this, AuthCodeActivity::class.java)
                                intent.putExtra(
                                    PHONE_NUMBER,
                                    "+" + ccp.selectedCountryCode + clearPhoneNumb(et_phone_numb.text.toString())
                                )
                                intent.putExtra(ACTIVITY_WAY, NEW_INTENT)
                                startActivityForResult(intent,REQUEST_CODE_NEW_PHONE)
                            }

                        }
                    }
                }
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode== Activity.RESULT_OK&&requestCode==REQUEST_CODE_NEW_PHONE){
            finish()
        }
    }
}