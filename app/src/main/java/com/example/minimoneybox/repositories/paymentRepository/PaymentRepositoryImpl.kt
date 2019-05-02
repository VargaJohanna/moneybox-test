package com.example.minimoneybox.repositories.paymentRepository

import android.content.res.Resources
import com.example.minimoneybox.R
import com.example.minimoneybox.customException.ServerException
import com.example.minimoneybox.data.MoneyboxData
import com.example.minimoneybox.data.UserData
import com.example.minimoneybox.network.MoneyBoxService
import com.example.minimoneybox.network.payment.OneOffPaymentBody
import com.example.minimoneybox.repositories.userAccountRepository.UserAccountRepository
import io.reactivex.Observable
import io.reactivex.Single
import okhttp3.ResponseBody
import org.json.JSONObject

class PaymentRepositoryImpl(
    private val service: MoneyBoxService,
    private val userRepository: UserAccountRepository

) : PaymentRepository {
    /**
     * Get the user data and if it's not empty then send the payment request with the returned token.
     * Then map teh response into a MoneyboxData.
     */
    override fun payMoneybox(productId: Int, amount: Int): Observable<MoneyboxData> {
        return userRepository.getUserData()
            .flatMapSingle {
                when (it) {
                    UserData.EMPTY -> Single.error(ServerException("Session has expired"))
                    else -> service.payOneOffPayment(
                        "Bearer ${(it as UserData.User).bearerToken}",
                        OneOffPaymentBody(amount, productId)
                    )
                }
            }.flatMap { response ->
                if (response.isSuccessful && response.body() != null) {
                    Observable.just(MoneyboxData(response.body()!!.moneyBoxNewValue))
                } else if (response.errorBody() != null) {
                    generateError(response.errorBody()!!)
                } else {
                    Observable.error(ServerException(Resources.getSystem().getString(R.string.generic_error)))
                }
            }
    }

    private fun generateError(response: ResponseBody): Observable<MoneyboxData> {
        val jsonObjectError = JSONObject(response.string())
        return Observable.error(ServerException(jsonObjectError.getString("Message")))
    }
}