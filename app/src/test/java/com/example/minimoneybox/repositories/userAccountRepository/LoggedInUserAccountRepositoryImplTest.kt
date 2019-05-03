package com.example.minimoneybox.repositories.userAccountRepository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.minimoneybox.customException.ServerException
import com.example.minimoneybox.model.User.EMPTY
import com.example.minimoneybox.network.MoneyBoxService
import com.example.minimoneybox.network.authenticate.AuthenticateBody
import com.example.minimoneybox.network.authenticate.AuthenticationEntity
import com.example.minimoneybox.network.authenticate.SessionEntity
import com.example.minimoneybox.network.authenticate.UserEntity
import com.example.minimoneybox.test.idling.FetcherListener
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.Single
import okhttp3.MediaType
import okhttp3.ResponseBody
import org.junit.Rule
import org.junit.Test
import retrofit2.Response

class LoggedInUserAccountRepositoryImplTest {
    @Rule
    @JvmField
    var mockito = InstantTaskExecutorRule()

    private val service = mock<MoneyBoxService>()
    private val fetcherListener = mock<FetcherListener>()

    private val correctEmail = "androidtest@moneyboxapp.com"
    private val correctPassword = "P455word12"
    private val correctName = "Errol the Owl"
    private val wrongEmail = "testEmail"
    private val wrongPassword = "password"
    private val failedLoginErrorBody = "{\n" +
            "    \"Name\": \"Login failed\",\n" +
            "    \"Message\": \"Incorrect email address or password. Please check and try again.\",\n" +
            "    \"ValidationErrors\": []\n" +
            "}"

    @Test
    fun `should login the user with correct details and name`() {
        // Given
        val userAccountRepository = givenUserAccountRepositoryWithLogin()

        //When
        val testObserver =
            userAccountRepository.login(email = correctEmail, password = correctPassword, name = correctName).test()

        //Then
        testObserver.assertValue {
            it != EMPTY
        }
            .assertNoErrors()
            .dispose()
    }

    @Test
    fun `should login the user with correct details and no name`() {
        // Given
        val userAccountRepository = givenUserAccountRepositoryWithLogin()

        //When
        val testObserver =
            userAccountRepository.login(email = correctEmail, password = correctPassword, name = "").test()

        //Then
        testObserver.assertValue {
            it != EMPTY
        }
            .assertNoErrors()
            .dispose()
    }

    @Test
    fun `should fail login when the email is wrong`() {
        // Given
        val userAccountRepository = givenUserAccountRepositoryFailedLogin(failedLoginErrorBody)

        //When
        val testObserver = userAccountRepository.login(email = wrongEmail, password = correctPassword, name = "").test()

        //Then
        val serverException =
            ServerException("Login failed", "Incorrect email address or password. Please check and try again.")

        testObserver.assertError(serverException)
            .dispose()
    }

    @Test
    fun `should fail login when the password is wrong`() {
        // Given
        val userAccountRepository = givenUserAccountRepositoryFailedLogin(failedLoginErrorBody)

        //When
        val testObserver = userAccountRepository.login(email = correctEmail, password = wrongPassword, name = "").test()

        //Then
        val serverException =
            ServerException("Login failed", "Incorrect email address or password. Please check and try again.")

        testObserver.assertError(serverException)
            .dispose()
    }

    @Test
    fun `should fail login when both email and password is wrong`() {
        // Given
        val userAccountRepository = givenUserAccountRepositoryFailedLogin(failedLoginErrorBody)

        //When
        val testObserver = userAccountRepository.login(email = wrongEmail, password = wrongPassword, name = "").test()

        //Then
        val serverException =
            ServerException("Login failed", "Incorrect email address or password. Please check and try again.")

        testObserver.assertError(serverException)
            .dispose()
    }

    @Test
    fun `should set userData to EMPTY when clearUserData() is called`() {
        //Given
        val userAccountRepository = givenUserAccountRepositoryWithLogin()

        //When
        userAccountRepository.clearUserData()

        //Then
        val testObserver = userAccountRepository.getUserData().test()
        testObserver.assertValue {
            it == EMPTY
        }
            .assertNoErrors()
            .dispose()
    }

    private fun givenUserAccountRepositoryWithLogin(): UserAccountRepositoryImpl {
        whenever(service.login(AuthenticateBody(correctEmail, correctPassword, "ANYTHING"))).thenReturn(
            Single.just(
                Response.success(AuthenticationEntity(UserEntity("Tech", "Test"), SessionEntity("token")))
            )
        )
        return UserAccountRepositoryImpl(service, fetcherListener)
    }

    private fun givenUserAccountRepositoryFailedLogin(errorContent: String): UserAccountRepositoryImpl {
        whenever(service.login(any())).thenReturn(
            Single.just(
                Response.error(401, ResponseBody.create(MediaType.parse("application/json"), errorContent))
            )
        )
        return UserAccountRepositoryImpl(service, fetcherListener)
    }
}