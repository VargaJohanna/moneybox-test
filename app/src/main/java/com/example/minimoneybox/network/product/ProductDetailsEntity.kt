package com.example.minimoneybox.network.product

import com.google.gson.annotations.SerializedName

data class ProductDetailsEntity(
    @SerializedName("FriendlyName")
    val name: String,
    @SerializedName("ProductHexCode")
    val colour: String
)
