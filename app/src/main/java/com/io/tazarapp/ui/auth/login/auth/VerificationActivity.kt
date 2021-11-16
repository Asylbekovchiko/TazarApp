package com.io.tazarapp.ui.auth.login.auth

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import androidx.lifecycle.Observer
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.io.tazarapp.R
import com.io.tazarapp.data.model.auth.AuthResponseModel
import com.io.tazarapp.data.state.State
import com.io.tazarapp.ui.auth.login.LoginActivity
import com.io.tazarapp.ui.auth.login.city.CityActivity
import com.io.tazarapp.ui.auth.login.signUp.SignUpViewModel
import com.io.tazarapp.ui.auth.slider.SliderActivity
import com.io.tazarapp.ui.citizen.CitizenActivity
import com.io.tazarapp.ui.recycler.RecyclerActivity
import com.io.tazarapp.utils.*
import dmax.dialog.SpotsDialog
import kotlinx.android.synthetic.main.activity_verification.*
import kotlinx.android.synthetic.main.activity_verification.img_success
import kotlinx.android.synthetic.main.activity_verification.view.*
import kotlinx.android.synthetic.main.activity_verification.wrong_number
import kotlinx.android.synthetic.main.auth_layout.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.concurrent.TimeUnit


class VerificationActivity : BaseActivity() {
    override fun handleCustomError(message: String) {
        toast(message)
    }

    private val viewModel: SignUpViewModel by viewModel()
    private var token: PhoneAuthProvider.OnVerificationStateChangedCallbacks? = null
    var smsInet: String? = null
    private var isCodeSent = false
    var code: String = ""
    var phoneNumber: String = ""
    var userType: String = ""
    private var authModel: AuthResponseModel? = null
    private lateinit var codeVerificationContainer: CodeVerificationContainer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verification)
        init()
    }

    private fun requestOTP() {
        token = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
                Log.e("Firebase verification", "onVerificationCompleted")
                signIn(phoneAuthCredential)
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

    private fun init() {
        dialog = SpotsDialog.Builder().setContext(this).setMessage(R.string.loading_message).build()
        phoneNumber = intent.getStringExtra(PHONE_NUMBER)
        userType = intent.getStringExtra(USER_TYPE)
        requestOTP()
        configureViewModel()
        setOnClickAction()
        wrong_number.text = phoneNumber
        timerStart()
        initTextWatchers()
    }

    private fun initTextWatchers() {
        codeVerificationContainer = CodeVerificationContainer(numb1, numb2, numb3, numb4, numb5, numb6){isFull ->
            if (isFull) {
                btnSend.activate()
            } else {
                btnSend.disActivate()
            }
        }
    }

    private fun setOnClickAction() {
        tv_about_app.setOnClickListener {
            startActivity(Intent(this,SliderActivity::class.java))
        }
        tv_send_again.setOnClickListener {
            requestOTP()
            timerStart()
        }
        btn_back3.setOnClickListener {
            finish()
        }
        wrong_number.setOnClickListener {
            startActivity<LoginActivity>()
            finish()
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
    }

    private fun signIn(phoneAuthCredential: PhoneAuthCredential) {
        FirebaseAuth.getInstance()
            .signInWithCredential(phoneAuthCredential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    img_success.visible()
                    toast(resources.getString(R.string.success))
                    when (userType) {
                        SIGN_IN -> {
                            val mUser = FirebaseAuth.getInstance().currentUser
                            mUser!!.getIdToken(true)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        val idToken: String = task.result?.token.toString()
                                        viewModel.getAuthData(clearPhoneNumb(phoneNumber), idToken)
                                    }
                                }
                        }
                        else -> {
                            val intent = Intent(this, CityActivity::class.java)
                            intent.putExtra(PHONE_NUMBER, phoneNumber)
                            intent.putExtra(USER_TYPE, userType)
                            startActivity(intent)
                        }
                    }
                } else {
                    toast(getString(R.string.check_correct_code))
                }
            }
    }

    private fun timerStart() {
        tv_send_again.isEnabled = false
        object : CountDownTimer(60000, 1000) {
            override fun onTick(millis: Long) {
                var seconds = (millis / 1000).toInt()
                val minutes = seconds / 60
                seconds %= 60
                timerTextView.text = "0" + String.format("%d:%02d", minutes, seconds) + getString(R.string.seconds_short)
            }

            override fun onFinish() {
                timerTextView.text = getString(R.string.secunds)
                tv_send_again.isEnabled = true
                tv_send_again.setTextColor(resources.getColor(R.color.colorAccent_8CC341))
            }
        }.start()
    }

    override fun onBackPressed() {
        this.toast(resources.getString(R.string.confirm_verify))
    }

    private fun configureViewModel() {
        viewModel.state.observe(this@VerificationActivity, Observer { state ->
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
                            authModel = state.data
                            SharedPrefModule(this@VerificationActivity).TokenModule()
                                .saveToken(state.data)
                            viewModel.saveDeviceId()
                        }
                    }
                }
                is State.NoItemState -> {
                    if (authModel != null) {
                        val cls = when (authModel!!.user_type) {
                            CITIZEN -> { CitizenActivity::class.java }
                            PROCESSOR -> { RecyclerActivity::class.java }
                            else -> null
                        }
                        if (cls != null) {
                            val intent = Intent(this,cls)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                        }
                    } else {
                        val intent = Intent(this,LoginActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                    }
                }
            }
        })
    }
}

