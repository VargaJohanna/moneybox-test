package com.example.minimoneybox.repositories.userRepository

import com.example.minimoneybox.customException.IncorrectLoginException
import com.example.minimoneybox.network.authenticate.AuthenticateBody
import com.example.minimoneybox.network.MoneyBoxService
import com.example.minimoneybox.data.UserData
import io.reactivex.Single
import io.reactivex.subjects.BehaviorSubject

class UserRepositoryImpl(
    private val moneyBoxService: MoneyBoxService
) : UserRepository {
    private val userSubject: BehaviorSubject<UserData> = BehaviorSubject.createDefault(UserData.EMPTY)

    override fun login(email: String, password: String, name: String?): Single<UserData> {
        return moneyBoxService.login(
            AuthenticateBody(
                email,
                password,
                "ANYTHING"
            )
        )
            .flatMap {
                if(it.isSuccessful && it.body() != null) {
                    Single.just(UserData.User(name, it.body()!!.session.bearerToken))
                }
                else if(it.code() == 401){
                    Single.error(IncorrectLoginException())
                } else {
                    Single.just(UserData.EMPTY)
                }
            }
            .doOnSuccess { userSubject.onNext(it) }
    }

    override fun getUserData() = userSubject

    override fun clearUserData() {
        userSubject.onNext(UserData.EMPTY)
    }
}