package com.example.minimoneybox.repositories.userAccountRepository

import com.example.minimoneybox.model.User
import io.reactivex.Observable
import io.reactivex.Single

interface UserAccountRepository {
    fun login(email: String, password: String, name: String?): Single<User>
    fun getUserData(): Observable<User>
    fun clearUserData()
}