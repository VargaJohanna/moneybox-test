package com.example.minimoneybox.ui.userAccount

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.minimoneybox.TestScheduler
import com.example.minimoneybox.customException.ServerException
import com.example.minimoneybox.model.InvestorProduct
import com.example.minimoneybox.model.Portfolio
import com.example.minimoneybox.model.User
import com.example.minimoneybox.repositories.productRepository.ProductRepository
import com.example.minimoneybox.repositories.userAccountRepository.UserAccountRepository
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.Observable
import io.reactivex.Single
import junit.framework.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import java.util.Arrays.asList

class LoggedInUserAccountViewModelTest {
    @Rule
    @JvmField
    var mockito = InstantTaskExecutorRule()

    private val userRepository = mock<UserAccountRepository>()
    private val productRepository = mock<ProductRepository>()
    private val productData = Portfolio.UserPortfolio(
        totalPlanValue = 1000f,
        productList = asList(
            InvestorProduct(
                id = 1, planValue = 100f, name = "test", moneyBoxValue = 50f, productColour = "colour"
            )
        )
    )

    @Test
    fun `should delegate to productRepository when observeProductList() is called`() {
        //Given
        val userAccountViewModel = givenUserAccountViewModel()

        //When
        userAccountViewModel.observeProductList()

        //Then
        verify(productRepository).fetchInvestorProducts()
    }

    @Test
    fun `should delegate to userRepository when clearData() is called`() {
        //Given
        val userAccountViewModel = givenUserAccountViewModel()

        //When
        userAccountViewModel.clearData()

        //Then
        verify(userRepository).clearUserData()
    }

    @Test
    fun `should get total plan value when getTotalPlanValue() is called`() {
        //Given
        val userAccountViewModel = givenUserAccountViewModel()
        userAccountViewModel.observeProductList()

        //When
        userAccountViewModel.getTotalPlanValue().observeForever(mock())

        //Then
        assertEquals(1000f, userAccountViewModel.getTotalPlanValue().value)
    }

    @Test
    fun `should get product list when getProductList() is called`() {
        //Given
        val userAccountViewModel = givenUserAccountViewModel()
        userAccountViewModel.observeProductList()

        //When
        userAccountViewModel.getProductList().observeForever(mock())

        //Then
        assertEquals(
            asList(
                InvestorProduct(
                    id = 1, planValue = 100f, name = "test", moneyBoxValue = 50f, productColour = "colour"
                )
            ),
            userAccountViewModel.getProductList().value
        )
    }

    @Test
    fun `should get user name when getName() is called`() {
        //Given
        val userAccountViewModel = givenUserAccountViewModel()

        //When
        userAccountViewModel.getName().observeForever(mock())

        //Then
        assertEquals(
            "name",
            userAccountViewModel.getName().value
        )
    }

    @Test
    fun `should delegate to userRepository to clear UserData when clearData() is called`() {
        //Given
        val userAccountViewModel = givenUserAccountViewModel()

        //When
        userAccountViewModel.clearData()

        //Then
        verify(userRepository).clearUserData()
    }

    @Test
    fun `logoutUser should be true when the token is expired`() {
        //Given
        val userAccountViewModel = givenUserAccountViewModelWithError()
        userAccountViewModel.observeProductList()

        //When
        userAccountViewModel.logoutUser().observeForever(mock())

        //Then
        assertEquals(true, userAccountViewModel.logoutUser().value)
    }

    @Test
    fun `should return error message when the token is expired`() {
        //Given
        val userAccountViewModel = givenUserAccountViewModelWithError()
        userAccountViewModel.observeProductList()

        //When
        userAccountViewModel.getErrorMessage().observeForever(mock())

        //Then
        assertEquals(
            "Your session has expired. Please close the app and log in again.",
            userAccountViewModel.getErrorMessage().value
        )
    }

    private fun givenUserAccountViewModel(): UserAccountViewModel {
        whenever(userRepository.getUserData()).thenReturn(Observable.just(User.LoggedInUser("name", "token")))
        whenever(productRepository.fetchInvestorProducts()).thenReturn(Single.just(productData))
        return UserAccountViewModel(userRepository, productRepository, TestScheduler())
    }

    private fun givenUserAccountViewModelWithError(): UserAccountViewModel {
        whenever(userRepository.getUserData()).thenReturn(Observable.just(User.LoggedInUser("name", "token")))
        whenever(productRepository.fetchInvestorProducts()).thenReturn(
            Single.error(
                ServerException(
                    "Bearer token expired",
                    "Your session has expired. Please close the app and log in again."
                )
            )
        )
        return UserAccountViewModel(userRepository, productRepository, TestScheduler())
    }
}