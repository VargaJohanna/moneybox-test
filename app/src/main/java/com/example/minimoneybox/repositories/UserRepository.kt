package com.example.minimoneybox.repositories

import com.example.minimoneybox.user.UserData
import io.reactivex.Observable
import io.reactivex.Single

interface UserRepository {
    fun login(email: String, password: String, name: String?): Single<UserData>
    fun getUserData(): Observable<UserData>
    fun clearUserData()
}