package com.example.minimoneybox.network.product

import com.google.gson.annotations.SerializedName

data class InvestorProductResponseEntity(
    @SerializedName("TotalPlanValue")
    val totalPlanValue: Float,
    @SerializedName("ProductResponses")
    val productList: List<ProductEntity>
)