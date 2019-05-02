package com.example.minimoneybox.repositories.paymentRepository

import com.example.minimoneybox.data.MoneyboxData
import io.reactivex.Observable

interface PaymentRepository {
    fun payMoneybox(productId: Int, amount: Float): Observable<MoneyboxData>
}