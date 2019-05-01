package com.example.minimoneybox.data

sealed class UserData {
    object EMPTY : UserData()

    data class User(
        val name: String?,
        val bearerToken: String
    ) : UserData()
}