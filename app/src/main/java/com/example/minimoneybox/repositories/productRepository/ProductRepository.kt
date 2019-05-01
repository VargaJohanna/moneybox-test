package com.example.minimoneybox.repositories.productRepository

import com.example.minimoneybox.data.ProductData
import io.reactivex.Observable

interface ProductRepository {
    fun fetchInvestorProducts(): Observable<ProductData>
    fun getCachedProduct(): Observable<ProductData>
}