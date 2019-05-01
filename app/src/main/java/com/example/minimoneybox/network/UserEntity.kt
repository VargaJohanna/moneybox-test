package com.example.minimoneybox.network

import com.google.gson.annotations.SerializedName

data class UserEntity(
    @SerializedName("FirstName")
    val firstName: String,
    @SerializedName("LastName")
    val lastName: String
)