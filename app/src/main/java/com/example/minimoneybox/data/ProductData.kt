package com.example.minimoneybox.data

sealed class ProductData {
    object EMPTY : ProductData()

    data class Product(
        val totalPlanValue: Float,
        val productList: List<InvestorProductData>
    ) : ProductData()
}