package com.example.minimoneybox.repositories

import com.example.minimoneybox.network.AuthenticateBody
import com.example.minimoneybox.network.MoneyBoxService
import com.example.minimoneybox.user.UserData
import io.reactivex.Single
import io.reactivex.subjects.BehaviorSubject

class UserRepositoryImpl(
    private val moneyBoxService: MoneyBoxService
) : UserRepository {
    private val userSubject: BehaviorSubject<UserData> = BehaviorSubject.create()

    override fun login(email: String, password: String, name: String?): Single<UserData> {
        return moneyBoxService.login(AuthenticateBody(email, password, "ANYTHING"))
            .map {
                if(it.isSuccessful && it.body() != null) UserData.User(name, it.body()!!.session.bearerToken)
                else UserData.FAILED_AUTH
            }
            .doOnSuccess { userSubject.onNext(it) }
    }

    override fun getUserData() = userSubject

    override fun clearUserData() {
    }
}