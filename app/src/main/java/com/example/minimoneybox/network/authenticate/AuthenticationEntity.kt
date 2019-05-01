package com.example.minimoneybox.network.authenticate

import com.google.gson.annotations.SerializedName

data class AuthenticationEntity(
    @SerializedName("User")
    val user: UserEntity,
    @SerializedName("Session")
    val session: SessionEntity
)