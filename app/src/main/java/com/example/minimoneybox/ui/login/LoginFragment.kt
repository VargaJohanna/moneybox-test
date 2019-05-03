package com.example.minimoneybox.ui.login

import android.animation.Animator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.minimoneybox.R
import com.example.minimoneybox.model.User
import com.example.minimoneybox.ext.show
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.android.synthetic.main.fragment_login.view.*
import org.koin.androidx.viewmodel.ext.viewModel
import java.util.regex.Pattern

/**
 * A login screen that offers login via email/password.
 */
class LoginFragment : Fragment() {
    private val loginViewModel: LoginViewModel by viewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        loginViewModel.resetUserData()
        return inflater.inflate(R.layout.fragment_login, container, false).apply {
            observeUserData(login_progress_bar)
            setButtonClickListener(btn_sign_in)
            showErrorMessage()
        }
    }

    override fun onStart() {
        super.onStart()
        setupAnimation()
    }

    /**
     * Reset animation onStop so the app won't crash when it's in the background
     */
    override fun onStop() {
        pig_animation.setMinAndMaxFrame(firstAnim.first, secondAnim.second)
        super.onStop()
    }

    /**
     * Send the login request when all fields are valid
     */
    private fun setButtonClickListener(signInButton: Button) {
        signInButton.setOnClickListener {
            if (allFieldsValid()) {
                loginViewModel.login(et_email.text.toString(), et_password.text.toString(), et_name.text.toString())
                login_progress_bar.show(true)
                pig_animation.pauseAnimation()
                Toast.makeText(requireContext(), R.string.input_valid, Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * When LoggedInUser.LoggedInUser is returned then navigate to the next screen.
     */
    private fun observeUserData(progressbar: ProgressBar) {
        loginViewModel.getUserData().observe(this, Observer {
            if(it is User.LoggedInUser) {
                findNavController().navigate(R.id.from_login_to_user_account)
            } else {
                progressbar.show(false)
                pig_animation.playAnimation()
            }
        })
    }

    /**
     * Observe the error message and show the returned text in a toast
     */
    private fun showErrorMessage() {
        loginViewModel.getErrorMessage().observe(this, Observer {
            if(!it.isNullOrEmpty()) {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(requireContext(), getString(R.string.generic_error), Toast.LENGTH_LONG).show()
            }
            login_progress_bar.show(false)
            pig_animation.playAnimation()
        })
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
        } else til_name.error == null
    }

    private fun hideErrors() {
        til_email.error = null
        til_password.error = null
        til_name.error = null
    }

    private fun setupAnimation() {
        pig_animation.setMaxFrame(firstAnim.second)
        pig_animation.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(p0: Animator?) {
                pig_animation.setMinAndMaxFrame(secondAnim.first, secondAnim.second)
            }

            override fun onAnimationEnd(p0: Animator?) {
            }

            override fun onAnimationCancel(p0: Animator?) {
            }

            override fun onAnimationStart(p0: Animator?) {
            }
        })
    }

    companion object {
        val EMAIL_REGEX = "[^@]+@[^.]+\\..+"
        val NAME_REGEX = "[a-zA-Z]{6,30}"
        val PASSWORD_REGEX = "^(?=.*[0-9])(?=.*[A-Z]).{10,50}$"
        val firstAnim = 0 to 109
        val secondAnim = 131 to 158
    }
}