package com.example.minimoneybox.customException

data class ServerException(
    val name: String,
    val errorMessage: String
) : Exception()