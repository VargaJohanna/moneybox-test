package com.example.minimoneybox.network

import io.reactivex.Single
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface MoneyBoxService {
    @Headers(
        "AppId: 3a97b932a9d449c981b595",
        "Content-Type: application/json",
        "appVersion: 5.10.0",
        "apiVersion: 3.0.0"
    )

    @POST("/users/login")
    fun login(@Body request: AuthenticateBody): Single<Response<AuthenticationEntity>>
}