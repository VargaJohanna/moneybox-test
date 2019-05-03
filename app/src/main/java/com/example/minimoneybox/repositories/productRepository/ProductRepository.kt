package com.example.minimoneybox.repositories.productRepository

import com.example.minimoneybox.model.Portfolio
import io.reactivex.Observable

interface ProductRepository {
    fun fetchInvestorProducts(): Observable<Portfolio>
}