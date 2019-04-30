package com.example.minimoneybox.repositories

import com.example.minimoneybox.network.MoneyBoxService
import com.example.minimoneybox.user.UserData
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import io.reactivex.Single

class UserRepositoryImpl(
    private val moneyBoxService: MoneyBoxService
) : UserRepository {
    private val gsonBuilder: Gson = GsonBuilder().setPrettyPrinting().create()

    override fun login(email: String, password: String): Single<UserData> {
        return moneyBoxService.authenticate(gsonBuilder.toJson(AuthenticateBody(email, password, "ANYTHING")))
            .map {
                UserData(it.User.FirstName, it.User.LastName, it.Session.BearerToken)
            }
    }

    override fun getUserData() {
    }

    override fun clearUserData() {
    }

    data class AuthenticationEntity(
        val User: UserEntity,
        val Session: SessionEntity
    )

    data class UserEntity(
        val FirstName: String,
        val LastName: String
    )

    data class SessionEntity(val BearerToken: String)
    data class AuthenticateBody(
        val Email: String,
        val Password: String,
        val Idfa: String
    )
}