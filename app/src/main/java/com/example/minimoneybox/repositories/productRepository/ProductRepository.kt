package com.example.minimoneybox.repositories.productRepository

import com.example.minimoneybox.model.Portfolio
import io.reactivex.Single

interface ProductRepository {
    fun fetchInvestorProducts(): Single<Portfolio>
}