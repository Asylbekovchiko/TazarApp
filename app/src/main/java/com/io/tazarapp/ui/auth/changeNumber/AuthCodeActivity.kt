package com.io.tazarapp.ui.auth.changeNumber

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.io.tazarapp.R
import com.io.tazarapp.data.model.auth.AuthResponseModel
import com.io.tazarapp.data.state.State
import com.io.tazarapp.utils.*
import dmax.dialog.SpotsDialog
import kotlinx.android.synthetic.main.activity_auth_code.*
import kotlinx.android.synthetic.main.activity_auth_code.btnSend
import kotlinx.android.synthetic.main.activity_verification.*
import kotlinx.android.synthetic.main.auth_layout.*
import kotlinx.android.synthetic.main.auth_layout.img_success
import kotlinx.android.synthetic.main.auth_layout.view.*
import kotlinx.android.synthetic.main.custom_toolbar.*
import kotlinx.android.synthetic.main.custom_toolbar.view.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.concurrent.TimeUnit

class AuthCodeActivity : BaseActivity() {

    private var token: PhoneAuthProvider.OnVerificationStateChangedCallbacks? = null
    var smsInet: String? = null
    private var isCodeSent = false
    var code: String = ""
    var phoneNumber: String = ""
    var intentType: String = ""
    private val viewModel: ChangeNumViewModel by viewModel()
    private lateinit var codeVerificationContainer: CodeVerificationContainer


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth_code)
        inits()
    }

    private fun inits() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        phoneNumber = intent.getStringExtra(PHONE_NUMBER)
        intentType = intent.getStringExtra(ACTIVITY_WAY)

        const_change_numb.wrong_number.text = phoneNumber

        when (intentType) {
            MAIN_INTENT -> {
                toolbar.custom_title.text = getString(R.string.change_number)
                requestOTP()
            }
            NEW_INTENT -> {
                toolbar.custom_title.text = getString(R.string.accept_code)
                configureViewModel()
            }
        }
        dialog = SpotsDialog.Builder().setContext(this).setMessage(R.string.loading_message).build()
        timerStart()
        setOnClickAction()
        initTextWatchers()
    }

    private fun setOnClickAction() {
        tv_send_again_include.setOnClickListener {
            requestOTP()
            timerStart()
        }
        btnSend.setOnClickListener {
            code =
                    codeVerificationContainer.getCode()
            if (code.length == 6) {
                val credential =
                    PhoneAuthProvider.getCredential(smsInet!!, code)
                signIn(credential)
            } else {
                toast(resources.getString(R.string.six_number_code))
            }
        }
        toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun requestOTP() {
        token = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
                Log.e("Firebase verification", "onVerificationCompleted")
            }

            override fun onVerificationFailed(e: FirebaseException) {
                Log.e("Firebase verification", "onVerificationCompleted" + e.message)
            }

            override fun onCodeSent(
                s: String,
                forceResendingToken: PhoneAuthProvider.ForceResendingToken
            ) {
                super.onCodeSent(s, forceResendingToken)
                isCodeSent = true
                smsInet = s
            }
        }
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            phoneNumber,
            60,
            TimeUnit.SECONDS,
            this,
            token!!
        )
    }

    private fun signIn(phoneAuthCredential: PhoneAuthCredential) {
        FirebaseAuth.getInstance()
            .signInWithCredential(phoneAuthCredential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    img_success.visible()
                    toast(resources.getString(R.string.success))
                    when (intentType) {
                        MAIN_INTENT -> {startActivity(Intent(this, NewNumActivity::class.java))
                        finish()}
                        NEW_INTENT -> {
                            val mUser = FirebaseAuth.getInstance().currentUser
                            mUser!!.getIdToken(true)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        val idToken: String = task.result?.token.toString()
                                        viewModel.changeNumber(clearPhoneNumb(phoneNumber), idToken)
                                    }
                                }
                        }
                    }
                } else {
                    toast(getString(R.string.check_correct_code))
                }
            }
    }

    private fun timerStart() {
        tv_send_again_include.isEnabled = false
        object : CountDownTimer(60000, 1000) {
            override fun onTick(millis: Long) {
                var seconds = (millis / 1000).toInt()
                val minutes = seconds / 60
                seconds %= 60
                timerTextView_include.text =
                    "0" + String.format("%d:%02d", minutes, seconds) + " сек"
            }

            override fun onFinish() {
                timerTextView_include.text = getString(R.string.secunds)
                tv_send_again_include.isEnabled = true
                tv_send_again_include.setTextColor(resources.getColor(R.color.colorAccent_8CC341))
            }
        }.start()
    }

    private fun initTextWatchers() {
        codeVerificationContainer = CodeVerificationContainer(numb1_cont, numb2_cont, numb3_cont, numb4_cont, numb5_cont, numb6_cont){ isFull ->
            if (isFull) {
                btnSend.activate()
            } else {
                btnSend.disActivate()
            }
        }
    }

    private fun configureViewModel() {
        requestOTP()
        timerStart()

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
                            is AuthResponseModel -> {
                                SharedPrefModule(this).TokenModule()
                                    .saveToken(state.data)
                                setResult(Activity.RESULT_OK)
                                finish()
                            }
                        }
                    }
                }
            }
        })
    }
}