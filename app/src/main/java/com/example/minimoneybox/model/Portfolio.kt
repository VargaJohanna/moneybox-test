package com.example.minimoneybox.model

sealed class Portfolio {
    object EMPTY : Portfolio()

    data class UserPortfolio(
        val totalPlanValue: Float,
        val productList: List<InvestorProduct>
    ) : Portfolio()
}