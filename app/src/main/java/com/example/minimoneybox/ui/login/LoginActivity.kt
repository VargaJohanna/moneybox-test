package com.example.minimoneybox.ui.login

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.example.minimoneybox.R

/**
 * A login screen that offers login via email/password.
 */
class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
    }

    /**
     * When the user is logged in and presses the back button then session will end.
     * On other screens the navigation component will handle the back press
     */
    override fun onBackPressed() {
        val onUserAccountScreen = findNavController(R.id.login_nav_host_fragment).currentDestination?.id == R.id.userAccountFragment

        if (onUserAccountScreen) {
            finish()
        } else {
            super.onBackPressed()
        }
    }

}
