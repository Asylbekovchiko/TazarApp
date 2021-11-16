package com.io.tazarapp.ui.auth.login.signUp

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.lifecycle.Observer
import com.io.tazarapp.R
import com.io.tazarapp.data.model.auth.CheckUserModel
import com.io.tazarapp.data.state.State
import com.io.tazarapp.ui.auth.login.auth.VerificationActivity
import com.io.tazarapp.utils.*
import kotlinx.android.synthetic.main.activity_sign_up.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class SignUpActivity : BaseActivity(), TextWatcher {
    private val viewModel: SignUpViewModel by viewModel()

    override fun handleCustomError(message: String) {
        toast(message)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        val id = intent?.getStringExtra(ID)?.toInt()
        et_reg_info.addTextChangedListener(this)

        when (id) {
            1 -> {
                tv_title_info.text = resources.getString(R.string.auth_like_citizens)
                listeners(CITIZEN)
            }
            2 -> {
                tv_title_info.text = resources.getString(R.string.auth_like_recycler)
                listeners(PROCESSOR)
            }
            else -> {
                Log.e("user_type_id", "null")
            }
        }
    }

    private fun listeners(id: String) {
        back_auth_info.setOnClickListener {
            finish()
        }
        btn_reg_info.setOnClickListener {
            val phoneNumber = et_reg_info.text.toString()
            if (phoneNumber.isEmpty()) {
                toast(resources.getString(R.string.enter_phone_number))
                return@setOnClickListener
            } else {
                viewModel.checkUser(clearPhoneNumb(phoneNumber))
                configureViewModel(id)
            }
        }
    }

    override fun afterTextChanged(p0: Editable?) {
        if (et_reg_info.text.toString().count() == 19) {
            btn_reg_info.activate()
        } else {
            btn_reg_info.disActivate()
        }
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }

    private fun configureViewModel(id: String) {
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
                                toast(getString(R.string.have_number))
                            } else {
                                val intent = Intent(this, VerificationActivity::class.java)
                                intent.putExtra(PHONE_NUMBER, et_reg_info.text.toString())
                                intent.putExtra(USER_TYPE, id)
                                startActivity(intent)
                                finish()
                            }
                        }
                    }
                }
            }
        })
    }

}
