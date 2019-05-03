package com.example.minimoneybox.repositories.paymentRepository

import com.example.minimoneybox.model.MoneyboxValue
import io.reactivex.Observable

interface PaymentRepository {
    fun payMoneybox(productId: Int, amount: Float): Observable<MoneyboxValue>
}