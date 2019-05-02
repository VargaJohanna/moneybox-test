package com.example.minimoneybox.repositories.paymentRepository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.minimoneybox.customException.ServerException
import com.example.minimoneybox.data.UserData
import com.example.minimoneybox.network.MoneyBoxService
import com.example.minimoneybox.network.payment.MoneyboxEntity
import com.example.minimoneybox.repositories.userAccountRepository.UserAccountRepository
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.Observable
import io.reactivex.Single
import okhttp3.MediaType
import okhttp3.ResponseBody
import org.junit.Rule
import org.junit.Test
import retrofit2.Response

class PaymentRepositoryImplTest {
    @Rule
    @JvmField
    var mockito = InstantTaskExecutorRule()

    private val service = mock<MoneyBoxService>()
    private val userRepository = mock<UserAccountRepository>()
    private val expiredTokenErrorBody = "{\n" +
            "    \"Name\": \"Bearer token expired\",\n" +
            "    \"Message\": \"Your session has expired. Please close the app and log in again.\",\n" +
            "    \"ValidationErrors\": []\n" +
            "}"

    @Test
    fun `should return MoneyboxData when payMoneybox() is successful`() {
        //Given
        val paymentRepository = givenPaymentRepositorySuccess()

        //When
        val testObserver = paymentRepository.payMoneybox(1, 10).test()

        //Then
        testObserver.assertValue {
            it.moneyboxValue == 100
        }
            .assertNoErrors()
            .dispose()
    }

    @Test
    fun `should return error when payMoneybox() and token is expired`() {
        //Given
        val paymentRepository = givenPaymentRepositoryExpiredToken()

        //When
        val testObserver = paymentRepository.payMoneybox(1, 10).test()

        //Then
        val serverException =
            ServerException("Bearer token expired", "Your session has expired. Please close the app and log in again.")

        testObserver.assertError(serverException)
            .dispose()
    }

    private fun givenPaymentRepositorySuccess(): PaymentRepositoryImpl {
        whenever(userRepository.getUserData()).thenReturn(Observable.just(UserData.User("name", "token")))
        whenever(service.payOneOffPayment(any(), any())).thenReturn(
            Single.just(
                Response.success(MoneyboxEntity(100))
            )
        )
        return PaymentRepositoryImpl(service, userRepository)
    }

    private fun givenPaymentRepositoryExpiredToken(): PaymentRepositoryImpl {
        whenever(userRepository.getUserData()).thenReturn(Observable.just(UserData.User("name", "token")))
        whenever(service.payOneOffPayment(any(), any())).thenReturn(
            Single.just(
                Response.error(401, ResponseBody.create(MediaType.parse("application/json"), expiredTokenErrorBody))
            )
        )
        return PaymentRepositoryImpl(service, userRepository)
    }
}