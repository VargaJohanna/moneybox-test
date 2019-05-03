package com.example.minimoneybox.repositories.paymentRepository

import android.content.res.Resources
import com.example.minimoneybox.R
import com.example.minimoneybox.customException.ServerException
import com.example.minimoneybox.model.MoneyboxValue
import com.example.minimoneybox.model.User
import com.example.minimoneybox.network.MoneyBoxService
import com.example.minimoneybox.network.payment.OneOffPaymentBody
import com.example.minimoneybox.repositories.userAccountRepository.UserAccountRepository
import io.reactivex.Single
import okhttp3.ResponseBody
import org.json.JSONObject

class PaymentRepositoryImpl(
    private val service: MoneyBoxService,
    private val userRepository: UserAccountRepository

) : PaymentRepository {
    /**
     * Get the user data and if it's not empty then send the payment request with the returned token.
     * Then map the response into a MoneyboxValue.
     */
    override fun payMoneybox(productId: Int, amount: Float): Single<MoneyboxValue> {
        return userRepository.getUserData()
            .firstOrError()
            .flatMap {
                when (it) {
                    User.EMPTY -> Single.error(
                        ServerException(
                            Resources.getSystem().getString(R.string.generic_error_name),
                            Resources.getSystem().getString(R.string.generic_error)
                        )
                    )
                    else -> service.payOneOffPayment(
                        "Bearer ${(it as User.LoggedInUser).bearerToken}",
                        OneOffPaymentBody(amount, productId)
                    )
                }
            }.flatMap { response ->
                if (response.isSuccessful && response.body() != null) {
                    Single.just(MoneyboxValue(response.body()!!.moneyBoxNewValue.toFloat()))
                } else if (response.errorBody() != null) {
                    generateError(response.errorBody()!!)
                } else {
                    Single.error(
                        ServerException(
                            Resources.getSystem().getString(R.string.generic_error_name),
                            Resources.getSystem().getString(R.string.generic_error)
                        )
                    )
                }
            }
    }

    private fun generateError(response: ResponseBody): Single<MoneyboxValue> {
        val jsonObjectError = JSONObject(response.string())
        return Single.error(
            ServerException(
                name = jsonObjectError.getString("Name"),
                errorMessage = jsonObjectError.getString("Message")
            )
        )
    }
}