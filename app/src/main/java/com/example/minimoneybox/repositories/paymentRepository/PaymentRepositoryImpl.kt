package com.example.minimoneybox.repositories.paymentRepository

import com.example.minimoneybox.customException.ErrorWithPaymentException
import com.example.minimoneybox.customException.UserIsNotLoggedInException
import com.example.minimoneybox.data.MoneyboxData
import com.example.minimoneybox.data.UserData
import com.example.minimoneybox.network.MoneyBoxService
import com.example.minimoneybox.network.payment.OneOffPaymentBody
import com.example.minimoneybox.repositories.userRepository.UserRepository
import io.reactivex.Observable
import io.reactivex.Single

class PaymentRepositoryImpl(
    private val service: MoneyBoxService,
    private val userRepository: UserRepository

): PaymentRepository {

    override fun payMoneybox( productId: Int, amount: Int) : Observable<MoneyboxData> {
        return userRepository.getUserData()
            .flatMapSingle {
                when (it) {
                    UserData.EMPTY -> Single.error(UserIsNotLoggedInException())
                    else -> service.payOneOffPayment(
                        "Bearer ${(it as UserData.User).bearerToken}",
                        OneOffPaymentBody(amount, productId)
                    )
                }
            }.flatMap {
                if(it.isSuccessful && it.body() != null) {
                    Observable.just(MoneyboxData(it.body()!!.moneyBoxNewValue))
                } else {
                    Observable.error(ErrorWithPaymentException())
                }
            }
    }
}