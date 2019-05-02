package com.example.minimoneybox.network.error

import com.google.gson.annotations.SerializedName

data class ErrorEntity (
    @SerializedName("Name")
    val name: String,
    @SerializedName("Message")
    val errorMessage: String
)