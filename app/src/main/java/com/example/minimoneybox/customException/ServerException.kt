package com.example.minimoneybox.customException

class ServerException(
    val name: String,
    val errorMessage: String
) : Exception()