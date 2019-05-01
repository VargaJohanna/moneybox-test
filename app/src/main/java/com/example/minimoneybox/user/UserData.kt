package com.example.minimoneybox.user

sealed class UserData {
    object FAILED_AUTH : UserData()

    data class User(
        val name: String?,
        val bearerToken: String
    ) : UserData()
}