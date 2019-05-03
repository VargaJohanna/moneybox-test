package com.example.minimoneybox.model

sealed class User {
    object EMPTY : User()

    data class LoggedInUser(
        val name: String?,
        val bearerToken: String
    ) : User()
}