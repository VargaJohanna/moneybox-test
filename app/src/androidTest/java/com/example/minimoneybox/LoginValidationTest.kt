package com.example.minimoneybox

import android.app.Activity
import android.app.Instrumentation
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.IntentMatchers.isInternal
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.espresso.matcher.RootMatchers.withDecorView
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.runner.AndroidJUnit4
import com.example.minimoneybox.idling.FetcherListener
import com.example.minimoneybox.ui.login.LoginActivity
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.not
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class LoginValidationTest {

    @get:Rule
    val intentsTestRule = IntentsTestRule(LoginActivity::class.java)

    @Before
    fun setup() {
        // Start fragment
        intentsTestRule.activity.supportFragmentManager.beginTransaction()

        //Block external intents
        intending(not(isInternal())).respondWith(Instrumentation.ActivityResult(Activity.RESULT_OK, null))

        //Setup idling
        val fetchingIdlingResource =
            (intentsTestRule.activity.application as MyApplication).koinApplication.koin.get<FetcherListener>()
        IdlingRegistry.getInstance().register(fetchingIdlingResource)
    }

    @After
    fun closeUp() {
        val fetchingIdlingResource =
            (intentsTestRule.activity.application as MyApplication).koinApplication.koin.get<FetcherListener>()
        IdlingRegistry.getInstance().unregister(fetchingIdlingResource)
    }


    @Test
    fun verifyPasswordAndEmailErrorsShowUp() {
        onView(withId(R.id.btn_sign_in)).perform(click())

        onView(withId(R.id.til_email)).check(
            matches(
                TextInputLayoutMatcher.hasTextInputLayoutErrorText(
                    intentsTestRule.activity.getString(
                        R.string.email_address_error
                    )
                )
            )
        )
        onView(withId(R.id.til_password)).check(
            matches(
                TextInputLayoutMatcher.hasTextInputLayoutErrorText(
                    intentsTestRule.activity.getString(R.string.password_error)
                )
            )
        )
    }

    @Test
    fun verifyLogin() {
        onView(withId(R.id.et_email)).perform(typeText("androidtest@moneyboxapp.com"))
        onView(withId(R.id.et_password)).perform(typeText("P455word12"))
        onView(withId(R.id.et_name)).perform(typeText("Errol the Owl"), closeSoftKeyboard())

        onView(withId(R.id.btn_sign_in)).perform(click())

        onView(withId(R.id.user_name)).check(matches(withText("Hello Errol the Owl!")))
    }

    @Test
    fun verifyErrorToastForInvalidEmail() {
        onView(withId(R.id.et_email)).perform(typeText("wrong@moneyboxapp.com"))
        onView(withId(R.id.et_password)).perform(typeText("P455word12"), closeSoftKeyboard())

        onView(withId(R.id.btn_sign_in)).perform(click())

        onView(withText("Incorrect email address or password. Please check and try again."))
            .inRoot(withDecorView(not(`is`(intentsTestRule.activity.window.decorView))))
            .check(matches(isDisplayed()))

    }

    @Test
    fun verifyErrorToastForInvalidPassword() {
        onView(withId(R.id.et_email)).perform(typeText("androidtest@moneyboxapp.com"))
        onView(withId(R.id.et_password)).perform(typeText("P455word123"), closeSoftKeyboard())

        onView(withId(R.id.btn_sign_in)).perform(click())

        onView(withText("Incorrect email address or password. Please check and try again."))
            .inRoot(withDecorView(not(`is`(intentsTestRule.activity.window.decorView))))
            .check(matches(isDisplayed()))

    }

    @Test
    fun verifyLoginWithoutName() {
        onView(withId(R.id.et_email)).perform(typeText("androidtest@moneyboxapp.com"))
        onView(withId(R.id.et_password)).perform(typeText("P455word12"), closeSoftKeyboard())

        onView(withId(R.id.btn_sign_in)).perform(click())

        onView(withId(R.id.user_name)).check(matches(withEffectiveVisibility(Visibility.GONE)))
    }

    @Test
    fun verifyEmailVerification() {
        onView(withId(R.id.et_email)).perform(typeText("wrong"), closeSoftKeyboard())
        onView(withId(R.id.btn_sign_in)).perform(click())

        onView(withId(R.id.til_email)).check(
            matches(
                TextInputLayoutMatcher.hasTextInputLayoutErrorText(
                    intentsTestRule.activity.getString(
                        R.string.email_address_error
                    )
                )
            )
        )
    }

    @Test
    fun verifyPasswordMinLength() {
        onView(withId(R.id.et_password)).perform(typeText("6Vt#T7"), closeSoftKeyboard())
        onView(withId(R.id.btn_sign_in)).perform(click())

        onView(withId(R.id.til_password)).check(
            matches(
                TextInputLayoutMatcher.hasTextInputLayoutErrorText(
                    intentsTestRule.activity.getString(
                        R.string.password_error
                    )
                )
            )
        )
    }

    @Test
    fun verifyPasswordHasCapital() {
        onView(withId(R.id.et_password)).perform(typeText("nocapital"), closeSoftKeyboard())
        onView(withId(R.id.btn_sign_in)).perform(click())

        onView(withId(R.id.til_password)).check(
            matches(
                TextInputLayoutMatcher.hasTextInputLayoutErrorText(
                    intentsTestRule.activity.getString(
                        R.string.password_error
                    )
                )
            )
        )
    }

    @Test
    fun verifyPasswordMaxLength() {
        onView(withId(R.id.et_password)).perform(
            typeText("6iispmFhhaJMo&f9zp3r&VfP4TUres920gNk#CSfK4K6v1nv^IQ"),
            closeSoftKeyboard()
        )
        onView(withId(R.id.btn_sign_in)).perform(click())

        onView(withId(R.id.til_password)).check(
            matches(
                TextInputLayoutMatcher.hasTextInputLayoutErrorText(
                    intentsTestRule.activity.getString(
                        R.string.password_error
                    )
                )
            )
        )
    }

    @Test
    fun verifyNameMaxLength() {
        onView(withId(R.id.et_name)).perform(typeText("Loremipsumdolorsitametcoqwertyu"), closeSoftKeyboard())
        onView(withId(R.id.btn_sign_in)).perform(click())

        onView(withId(R.id.til_name)).check(
            matches(
                TextInputLayoutMatcher.hasTextInputLayoutErrorText(
                    intentsTestRule.activity.getString(
                        R.string.full_name_error
                    )
                )
            )
        )
    }

    @Test
    fun verifyNameMinLength() {
        onView(withId(R.id.et_name)).perform(typeText("short"), closeSoftKeyboard())
        onView(withId(R.id.btn_sign_in)).perform(click())

        onView(withId(R.id.til_name)).check(
            matches(
                TextInputLayoutMatcher.hasTextInputLayoutErrorText(
                    intentsTestRule.activity.getString(
                        R.string.full_name_error
                    )
                )
            )
        )
    }
}

