package com.example.minimoneybox.ui.login

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.minimoneybox.R

/**
 * A login screen that offers login via email/password.
 */
class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}
