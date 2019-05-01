package com.example.minimoneybox.network.authenticate

import com.google.gson.annotations.SerializedName

data class AuthenticateBody(
    @SerializedName("Email")
    val email: String,
    @SerializedName("Password")
    val password: String,
    @SerializedName("Idfa")
    val idfa: String
)