package com.example.minimoneybox.network.payment

import com.google.gson.annotations.SerializedName

data class OneOffPaymentBody(
    @SerializedName("Amount")
    val amount: Float,
    @SerializedName("InvestorProductId")
    val productId: Int
)