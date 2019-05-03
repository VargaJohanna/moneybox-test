package com.example.minimoneybox.repositories.userAccountRepository

import android.content.res.Resources
import com.example.minimoneybox.R
import com.example.minimoneybox.customException.ServerException
import com.example.minimoneybox.model.User
import com.example.minimoneybox.idling.FetcherListener
import com.example.minimoneybox.network.MoneyBoxService
import com.example.minimoneybox.network.authenticate.AuthenticateBody
import io.reactivex.Single
import io.reactivex.subjects.BehaviorSubject
import okhttp3.ResponseBody
import org.json.JSONObject

class UserAccountRepositoryImpl(
    private val moneyBoxService: MoneyBoxService,
    private val fetcherListener: FetcherListener
) : UserAccountRepository {
    private val userSubject: BehaviorSubject<User> = BehaviorSubject.createDefault(User.EMPTY)

    override fun login(email: String, password: String, name: String?): Single<User> {
        return moneyBoxService.login(
            AuthenticateBody(
                email,
                password,
                "ANYTHING"
            )
        )
            .flatMap { response ->
                if (response.isSuccessful && response.body() != null) {
                    Single.just(User.LoggedInUser(name, response.body()!!.session.bearerToken))
                } else if (response.errorBody() != null) {
                    generateError(response.errorBody()!!)
                } else {
                    Single.error(ServerException(Resources.getSystem().getString(R.string.generic_error_name), Resources.getSystem().getString(R.string.generic_error)))
                }
            }
            .doOnSubscribe { fetcherListener.beginFetching() }
            .doFinally { fetcherListener.doneFetching() }
            .doOnSuccess { userSubject.onNext(it) }
    }

    private fun generateError(response: ResponseBody): Single<User> {
        val jsonObjectError = JSONObject(response.string())
        return Single.error(ServerException(name = jsonObjectError.getString("Name"), errorMessage = jsonObjectError.getString("Message")))
    }

    override fun getUserData() = userSubject

    override fun clearUserData() {
        userSubject.onNext(User.EMPTY)
    }
}