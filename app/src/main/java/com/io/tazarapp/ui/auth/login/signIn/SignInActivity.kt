package com.io.tazarapp.ui.auth.login.signIn

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.lifecycle.Observer
import com.io.tazarapp.R
import com.io.tazarapp.data.model.auth.CheckUserModel
import com.io.tazarapp.data.state.State
import com.io.tazarapp.ui.auth.login.auth.VerificationActivity
import com.io.tazarapp.utils.*
import kotlinx.android.synthetic.main.activity_sign_in.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class SignInActivity : BaseActivity(), TextWatcher {
    private val viewModel: SignInViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        listeners()
        et_auth.addTextChangedListener(this)
    }

    private fun listeners() {
        back_auth_back.setOnClickListener {
            finish()
        }

        btn_auth.setOnClickListener {
            val phoneNumber = et_auth.text.toString()
            if (phoneNumber.isEmpty()) {
                toast(resources.getString(R.string.enter_phone_number))
                return@setOnClickListener
            } else {
                viewModel.checkUser(clearPhoneNumb(phoneNumber))
                configureViewModel()
            }

        }
    }

    override fun afterTextChanged(p0: Editable?) {
        if (et_auth.text.toString().count() == 19) {
            btn_auth.activate()
        } else {
            btn_auth.disActivate()
        }
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }

    private fun configureViewModel() {
        viewModel.state.observe(this, Observer {
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
                                val intent =
                                    Intent(this, VerificationActivity::class.java)
                                intent.putExtra(PHONE_NUMBER, et_auth.text.toString().trim())
                                intent.putExtra(USER_TYPE, SIGN_IN)
                                startActivity(intent)
                                finish()
                            } else {
                                toast(getString(R.string.have_not_number))
                            }
                        }
                    }
                }
            }
        })
    }
}
