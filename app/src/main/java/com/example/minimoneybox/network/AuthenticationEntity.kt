package com.example.minimoneybox.network

import com.google.gson.annotations.SerializedName

data class AuthenticationEntity(
    @SerializedName("User")
    val user: UserEntity,
    @SerializedName("Session")
    val session: SessionEntity
)