package com.example.minimoneybox.repositories.paymentRepository

import android.content.res.Resources
import com.example.minimoneybox.R
import com.example.minimoneybox.customException.ServerException
import com.example.minimoneybox.model.MoneyboxValue
import com.example.minimoneybox.model.User
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
     * Then map teh response into a MoneyboxValue.
     */
    override fun payMoneybox(productId: Int, amount: Float): Observable<MoneyboxValue> {
        return userRepository.getUserData()
            .flatMapSingle {
                when (it) {
                    User.EMPTY -> Single.error(ServerException(Resources.getSystem().getString(R.string.generic_error_name), Resources.getSystem().getString(R.string.generic_error)))
                    else -> service.payOneOffPayment(
                        "Bearer ${(it as User.LoggedInUser).bearerToken}",
                        OneOffPaymentBody(amount, productId)
                    )
                }
            }.flatMap { response ->
                if (response.isSuccessful && response.body() != null) {
                    Observable.just(MoneyboxValue(response.body()!!.moneyBoxNewValue.toFloat()))
                } else if (response.errorBody() != null) {
                    generateError(response.errorBody()!!)
                } else {
                    Observable.error(ServerException(Resources.getSystem().getString(R.string.generic_error_name), Resources.getSystem().getString(R.string.generic_error)))
                }
            }
    }

    private fun generateError(response: ResponseBody): Observable<MoneyboxValue> {
        val jsonObjectError = JSONObject(response.string())
        return Observable.error(ServerException(name = jsonObjectError.getString("Name"), errorMessage = jsonObjectError.getString("Message")))
    }
}