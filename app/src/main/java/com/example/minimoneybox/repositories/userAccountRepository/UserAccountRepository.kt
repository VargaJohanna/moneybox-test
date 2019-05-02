package com.example.minimoneybox.repositories.userAccountRepository

import com.example.minimoneybox.data.UserData
import io.reactivex.Observable
import io.reactivex.Single

interface UserAccountRepository {
    fun login(email: String, password: String, name: String?): Single<UserData>
    fun getUserData(): Observable<UserData>
    fun clearUserData()
}