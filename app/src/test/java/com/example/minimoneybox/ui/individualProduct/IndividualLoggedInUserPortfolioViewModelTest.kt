package com.example.minimoneybox.ui.individualProduct

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.minimoneybox.TestScheduler
import com.example.minimoneybox.customException.ServerException
import com.example.minimoneybox.model.MoneyboxValue
import com.example.minimoneybox.repositories.paymentRepository.PaymentRepository
import com.example.minimoneybox.repositories.userAccountRepository.UserAccountRepository
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.Single
import junit.framework.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class IndividualLoggedInUserPortfolioViewModelTest {
    @Rule
    @JvmField
    var mockito = InstantTaskExecutorRule()

    private val useRepository = mock<UserAccountRepository>()
    private val paymentRepository = mock<PaymentRepository>()

    @Test
    fun `should delegate to paymentRepository when payMoneybox() is called`() {
        //Given
        val individualProductViewModel = givenIndividualProductViewModel()

        //When
        individualProductViewModel.payMoneybox(10f)

        //Then
        verify(paymentRepository).payMoneybox(1, 10f)
    }

    @Test
    fun `should delegate to userRepository and clear userData when clearData() is called`() {
        //Given
        val individualProductViewModel = givenIndividualProductViewModel()

        //When
        individualProductViewModel.clearData()

        //Then
        verify(useRepository).clearUserData()
    }

    @Test
    fun `should get the moneybox value when getMoneyboxValue() is called`() {
        //Given
        val individualProductViewModel = givenIndividualProductViewModel()
        individualProductViewModel.payMoneybox(10f)

        //When
        individualProductViewModel.getMoneyboxValue().observeForever(mock())

        //Then
        assertEquals(20f, individualProductViewModel.getMoneyboxValue().value)
    }

    @Test
    fun `should get the error message when getErrorMessage() is called and payMoneybox returned an error`() {
        //Given
        val individualProductViewModel = givenIndividualProductViewModelWithError()
        individualProductViewModel.payMoneybox(10f)

        //When
        individualProductViewModel.getErrorMessage().observeForever(mock())

        //Then
        assertEquals(
            "Your session has expired. Please close the app and log in again.",
            individualProductViewModel.getErrorMessage().value
        )
    }

    @Test
    fun `logoutUser should be true when the token is expired`() {
        //Given
        val individualProductViewModel = givenIndividualProductViewModelWithError()
        individualProductViewModel.payMoneybox(10f)

        //When
        individualProductViewModel.logoutUser().observeForever(mock())

        //Then
        assertEquals(true, individualProductViewModel.logoutUser().value)
    }

    private fun givenIndividualProductViewModel(): IndividualProductViewModel {
        whenever(paymentRepository.payMoneybox(any(), any())).thenReturn(Single.just(MoneyboxValue(20f)))
        return IndividualProductViewModel(1, paymentRepository, TestScheduler(), useRepository)
    }

    private fun givenIndividualProductViewModelWithError(): IndividualProductViewModel {
        whenever(paymentRepository.payMoneybox(any(), any())).thenReturn(
            Single.error(
                ServerException(
                    "Bearer token expired",
                    "Your session has expired. Please close the app and log in again."
                )
            )
        )
        return IndividualProductViewModel(1, paymentRepository, TestScheduler(), useRepository)
    }
}