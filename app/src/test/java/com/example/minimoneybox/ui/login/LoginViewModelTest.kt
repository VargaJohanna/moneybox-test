package com.example.minimoneybox.ui.login

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.minimoneybox.TestScheduler
import com.example.minimoneybox.customException.ServerException
import com.example.minimoneybox.data.UserData
import com.example.minimoneybox.repositories.userAccountRepository.UserAccountRepository
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.Single
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class LoginViewModelTest {
    @Rule
    @JvmField
    var mockito = InstantTaskExecutorRule()

    private val userRepository = mock<UserAccountRepository>()

    private val correctEmail = "androidtest@moneyboxapp.com"
    private val correctPassword = "P455word12"
    private val correctName = "Errol the Owl"
    private val user = UserData.User(correctName, "token")

    @Test
    fun `should delegate to userRepository when login() is called`() {
        //Given
        val loginViewModel = givenLoginViewModel()

        //When
        loginViewModel.login(correctEmail, correctPassword, correctName)

        //Then
        verify(userRepository).login(correctEmail, correctPassword, correctName)
    }

    @Test
    fun `should set userData to EMPTY when resetUserData() is called`() {
        //Given
        val loginViewModel = givenLoginViewModel()

        //When
        loginViewModel.resetUserData()

        //Then
        loginViewModel.getUserData().observeForever(mock())
        assertEquals(UserData.EMPTY, loginViewModel.getUserData().value)
    }

    @Test
    fun `should update userData when login() is called`() {
        //Given
        val loginViewModel = givenLoginViewModel()

        //When
        loginViewModel.login(correctEmail, correctName, correctName)

        //Then
        loginViewModel.getUserData().observeForever(mock())
        assertEquals(user, loginViewModel.getUserData().value)
    }

    @Test
    fun `should get custom error message when getErrorMessage and login returned a server error`() {
        //Given
        val loginViewModel = givenLoginViewModelWithServerError()
        loginViewModel.login(correctEmail, correctName, correctName)

        //When
        loginViewModel.getErrorMessage().observeForever(mock())

        //Then
        assertEquals(
            "Your session has expired. Please close the app and log in again.",
            loginViewModel.getErrorMessage().value
        )
    }

    @Test
    fun `should get generic error message when getErrorMessage and login returned an error`() {
        //Given
        val loginViewModel = givenLoginViewModelWithGenericError()
        loginViewModel.login(correctEmail, correctName, correctName)

        //When
        loginViewModel.getErrorMessage().observeForever(mock())

        //Then
        assertEquals("", loginViewModel.getErrorMessage().value)
    }

    private fun givenLoginViewModel(): LoginViewModel {
        whenever(userRepository.login(any(), any(), any())).thenReturn(Single.just(user))
        return LoginViewModel(userRepository, TestScheduler())
    }

    private fun givenLoginViewModelWithServerError(): LoginViewModel {
        whenever(userRepository.login(any(), any(), any())).thenReturn(
            Single.error(
                ServerException(
                    "Bearer token expired",
                    "Your session has expired. Please close the app and log in again."
                )
            )
        )
        return LoginViewModel(userRepository, TestScheduler())
    }

    private fun givenLoginViewModelWithGenericError(): LoginViewModel {
        whenever(userRepository.login(any(), any(), any())).thenReturn(Single.error(Exception()))
        return LoginViewModel(userRepository, TestScheduler())
    }
}