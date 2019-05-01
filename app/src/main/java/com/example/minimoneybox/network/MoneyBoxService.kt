package com.example.minimoneybox.network

import com.example.minimoneybox.network.authenticate.AuthenticateBody
import com.example.minimoneybox.network.authenticate.AuthenticationEntity
import com.example.minimoneybox.network.product.InvestorProductResponseEntity
import io.reactivex.Single
import retrofit2.Response
import retrofit2.http.*

interface MoneyBoxService {
    @Headers(
        "AppId: 3a97b932a9d449c981b595",
        "Content-Type: application/json",
        "appVersion: 5.10.0",
        "apiVersion: 3.0.0"
    )
    @POST("/users/login")
    fun login(@Body request: AuthenticateBody): Single<Response<AuthenticationEntity>>

    @Headers(
        "AppId: 3a97b932a9d449c981b595",
        "Content-Type: application/json",
        "appVersion: 5.10.0",
        "apiVersion: 3.0.0"
    )
    @GET("/investorproducts")
    fun getInvestorProducts(@Header("Authorization") bearerToken: String?): Single<InvestorProductResponseEntity>
}