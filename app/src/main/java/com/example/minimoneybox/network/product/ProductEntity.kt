package com.example.minimoneybox.network.product

import com.google.gson.annotations.SerializedName

data class ProductEntity(
    @SerializedName("Id")
    val productId: Int,
    @SerializedName("PlanValue")
    val planValue: Float,
    @SerializedName("Moneybox")
    val moneyBox: Float,
    @SerializedName("Product")
    val productDetailEntity: ProductDetailsEntity
)
