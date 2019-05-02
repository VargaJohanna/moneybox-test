package com.example.minimoneybox.repositories.userAccountRepository

import android.content.res.Resources
import com.example.minimoneybox.R
import com.example.minimoneybox.customException.ServerException
import com.example.minimoneybox.data.UserData
import com.example.minimoneybox.network.MoneyBoxService
import com.example.minimoneybox.network.authenticate.AuthenticateBody
import io.reactivex.Single
import io.reactivex.subjects.BehaviorSubject
import okhttp3.ResponseBody
import org.json.JSONObject

class UserAccountRepositoryImpl(
    private val moneyBoxService: MoneyBoxService
) : UserAccountRepository {
    private val userSubject: BehaviorSubject<UserData> = BehaviorSubject.createDefault(UserData.EMPTY)

    override fun login(email: String, password: String, name: String?): Single<UserData> {
        return moneyBoxService.login(
            AuthenticateBody(
                email,
                password,
                "ANYTHING"
            )
        )
            .flatMap { response ->
                if (response.isSuccessful && response.body() != null) {
                    Single.just(UserData.User(name, response.body()!!.session.bearerToken))
                } else if (response.errorBody() != null) {
                    generateError(response.errorBody()!!)
                } else {
                    Single.error(ServerException(Resources.getSystem().getString(R.string.generic_error)))
                }
            }
            .doOnSuccess { userSubject.onNext(it) }
    }

    private fun generateError(response: ResponseBody): Single<UserData> {
        val jsonObjectError = JSONObject(response.string())
        return Single.error(ServerException(jsonObjectError.getString("Message")))
    }

    override fun getUserData() = userSubject

    override fun clearUserData() {
        userSubject.onNext(UserData.EMPTY)
    }
}