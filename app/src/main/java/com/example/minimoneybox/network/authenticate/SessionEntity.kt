package com.example.minimoneybox.network.authenticate

import com.google.gson.annotations.SerializedName

data class SessionEntity(
    @SerializedName("BearerToken")
    val bearerToken: String
)
