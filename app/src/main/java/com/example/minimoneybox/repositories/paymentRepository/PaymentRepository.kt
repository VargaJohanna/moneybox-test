package com.example.minimoneybox.repositories.paymentRepository

import com.example.minimoneybox.model.MoneyboxValue
import io.reactivex.Single

interface PaymentRepository {
    fun payMoneybox(productId: Int, amount: Float): Single<MoneyboxValue>
}