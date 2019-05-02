package com.example.minimoneybox.repositories.productRepository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.minimoneybox.customException.ServerException
import com.example.minimoneybox.data.InvestorProductData
import com.example.minimoneybox.data.ProductData
import com.example.minimoneybox.data.UserData
import com.example.minimoneybox.network.MoneyBoxService
import com.example.minimoneybox.network.product.InvestorProductResponseEntity
import com.example.minimoneybox.network.product.ProductDetailsEntity
import com.example.minimoneybox.network.product.ProductEntity
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
import java.util.Arrays.asList

class ProductRepositoryImplTest {
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

    private val invalidTokenErrorBody = "{\n" +
            "    \"Name\": \"User session not found\",\n" +
            "    \"Message\": \"Sorry but it looks like your session is not valid. Please close the app and log in again.\",\n" +
            "    \"ValidationErrors\": []\n" +
            "}"

    @Test
    fun `should fetch investor products when token is valid`() {
        //Given
        val productRepository = givenProductRepositoryWithData()

        //When
        val testObserver = productRepository.fetchInvestorProducts().test()

        //Then
        testObserver.assertValue {
            it == ProductData.Product(
                totalPlanValue = 1000f,
                productList = asList(
                    InvestorProductData(
                        id = 1, planValue = 100f, name = "test", moneyBoxValue = 50f, productColour = "colour"
                    )
                )
            )
        }
    }

    @Test
    fun `should return error when token is expired`() {
        //Given
        val productRepository = givenProductRepositoryInvalidToken(expiredTokenErrorBody)

        //When
        val testObserver = productRepository.fetchInvestorProducts().test()

        //Then
        val serverException =
            ServerException("Bearer token expired", "Your session has expired. Please close the app and log in again.")

        testObserver.assertError(serverException)
            .dispose()
    }

    @Test
    fun `should return error when token is invalid`() {
        //Given
        val productRepository = givenProductRepositoryInvalidToken(invalidTokenErrorBody)

        //When
        val testObserver = productRepository.fetchInvestorProducts().test()

        //Then
        val serverException =
            ServerException(
                "User session not found",
                "Sorry but it looks like your session is not valid. Please close the app and log in again."
            )

        testObserver.assertError(serverException)
            .dispose()
    }

    private fun givenProductRepositoryWithData(): ProductRepositoryImpl {
        whenever(userRepository.getUserData()).thenReturn(Observable.just(UserData.User("name", "token")))
        whenever(service.getInvestorProducts(any())).thenReturn(
            Single.just(
                Response.success(
                    InvestorProductResponseEntity(
                        totalPlanValue = 1000f,
                        productList = asList(
                            ProductEntity(
                                productId = 1,
                                planValue = 100f,
                                moneyBox = 50f,
                                productDetailEntity = ProductDetailsEntity("test", "colour")
                            )
                        )
                    )
                )
            )
        )
        return ProductRepositoryImpl(service, userRepository)
    }

    private fun givenProductRepositoryInvalidToken(errorContent: String): ProductRepositoryImpl {
        whenever(userRepository.getUserData()).thenReturn(Observable.just(UserData.User("name", "token")))
        whenever(service.getInvestorProducts(any())).thenReturn(
            Single.just(
                Response.error(401, ResponseBody.create(MediaType.parse("application/json"), errorContent))
            )
        )
        return ProductRepositoryImpl(service, userRepository)
    }
}