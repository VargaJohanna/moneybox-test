package com.example.minimoneybox.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.minimoneybox.R
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.android.synthetic.main.fragment_login.view.*
import java.util.regex.Pattern

/**
 * A login screen that offers login via email/password.
 */
class LoginFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_login, container, false).apply {
            setButtonClickListener(btn_sign_in)
        }
    }

    override fun onStart() {
        super.onStart()
        setupAnimation()
    }

    private fun setButtonClickListener(signInButton: Button) {
        signInButton.setOnClickListener {
            if (allFieldsValid()) {
                Toast.makeText(requireContext(), R.string.input_valid, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun allFieldsValid(): Boolean {
        hideErrors()
        val emailValid = Pattern.matches(EMAIL_REGEX, et_email.text.toString())
        val passwordValid = Pattern.matches(PASSWORD_REGEX, et_password.text.toString())
        val nameValid = Pattern.matches(NAME_REGEX, et_name.text.toString().replace("\\s".toRegex(), "")) || et_name.text.toString().isEmpty()

        if (!nameValid) {
            til_name.error = getString(R.string.full_name_error)
        }

        return if (!emailValid && !passwordValid) {
            til_password.error = getString(R.string.password_error)
            til_email.error = getString(R.string.email_address_error)
            false
        } else if(!emailValid) {
            til_email.error = getString(R.string.email_address_error)
            false
        } else if(!passwordValid) {
            til_password.error = getString(R.string.password_error)
            false
        } else {
            true
        }
    }

    private fun hideErrors() {
        til_email.error = null
        til_password.error = null
        til_name.error = null
    }

    private fun setupAnimation() {
        pig_animation.playAnimation()
    }

    companion object {
        val EMAIL_REGEX = "[^@]+@[^.]+\\..+"
        val NAME_REGEX = "[a-zA-Z]{6,30}"
        val PASSWORD_REGEX = "^(?=.*[0-9])(?=.*[A-Z]).{10,50}$"
        val firstAnim = 0 to 109
        val secondAnim = 131 to 158
    }
}