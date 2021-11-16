package com.io.tazarapp.ui.auth.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.io.tazarapp.R
import com.io.tazarapp.ui.auth.login.signIn.SignInActivity
import com.io.tazarapp.ui.auth.login.signUp.RegistrationActivity
import com.io.tazarapp.ui.citizen.CitizenActivity
import com.io.tazarapp.ui.recycler.RecyclerActivity
import com.io.tazarapp.utils.CITIZEN
import com.io.tazarapp.utils.PROCESSOR
import com.io.tazarapp.utils.SharedPrefModule
import com.io.tazarapp.utils.gsm.FCMTokenUtils
import com.io.tazarapp.utils.startActivity
import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val token = SharedPrefModule(this).TokenModule().getToken()
        val hasFCM = FCMTokenUtils.checkTokenFromPref()
        if (token != null && hasFCM) {
            val cls = when (token.user_type) {
                CITIZEN -> { CitizenActivity::class.java }
                PROCESSOR -> { RecyclerActivity::class.java }
                else -> null
            }
            if (cls != null) {
                val i = Intent(this,cls)
                if(intent.hasExtra("push_id")) {
                    val id = intent.getStringExtra("push_id")
                    Log.e("LOgin", "id = $id")
                    i.putExtra("push_id", id)
                }
                i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(i)
            }
        }
        setContentView(R.layout.activity_login)
        listener()
    }

    private fun listener() {
        btn_registration.setOnClickListener {
            startActivity<RegistrationActivity>()
        }
        btn_auth.setOnClickListener {
            startActivity<SignInActivity>()
        }
    }
}
