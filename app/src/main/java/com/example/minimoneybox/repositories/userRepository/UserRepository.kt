package com.example.minimoneybox.repositories.userRepository

import com.example.minimoneybox.data.UserData
import io.reactivex.Observable
import io.reactivex.Single

interface UserRepository {
    fun login(email: String, password: String, name: String?): Single<UserData>
    fun getUserData(): Observable<UserData>
    fun clearUserData()
}