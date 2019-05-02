package com.example.minimoneybox.network.payment

import com.google.gson.annotations.SerializedName

data class MoneyboxEntity (
    @SerializedName("Moneybox")
    val moneyBoxNewValue: Int
)