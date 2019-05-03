package com.example.minimoneybox.network.authenticate

import com.google.gson.annotations.SerializedName

data class AuthenticationEntity(
    @SerializedName("LoggedInUser")
    val user: UserEntity,
    @SerializedName("Session")
    val session: SessionEntity
)