package com.io.tazarapp.ui.auth.login.signUp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.io.tazarapp.R
import com.io.tazarapp.utils.ID
import kotlinx.android.synthetic.main.activity_registration.*

class RegistrationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)
        listeners()
    }

    private fun listeners() {
        back_auth.setOnClickListener {
            finish()
        }
        citizen.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            intent.putExtra(ID, "1")
            startActivity(intent)
        }
        processor.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            intent.putExtra(ID, "2")
            startActivity(intent)
        }
    }
}
