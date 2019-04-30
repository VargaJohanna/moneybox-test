package com.example.minimoneybox.repositories

import com.example.minimoneybox.user.UserData
import io.reactivex.Single

interface UserRepository {
    fun login(email: String, password: String): Single<UserData>
    fun getUserData()
    fun clearUserData()
}