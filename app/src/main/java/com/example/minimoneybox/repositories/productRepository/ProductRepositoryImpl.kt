package com.example.minimoneybox.repositories.productRepository

import android.content.res.Resources
import com.example.minimoneybox.R
import com.example.minimoneybox.customException.ServerException
import com.example.minimoneybox.model.InvestorProduct
import com.example.minimoneybox.model.Portfolio
import com.example.minimoneybox.model.User
import com.example.minimoneybox.network.MoneyBoxService
import com.example.minimoneybox.repositories.userAccountRepository.UserAccountRepository
import io.reactivex.Single
import okhttp3.ResponseBody
import org.json.JSONObject

class ProductRepositoryImpl(
    private val service: MoneyBoxService,
    private val userRepository: UserAccountRepository
) : ProductRepository {

    /**
     * Get the user data and if it's not empty then fetch the investor products with the returned token.
     * Then map the response into a Portfolio data class
     */
    override fun fetchInvestorProducts(): Single<Portfolio> {
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
                    else -> service.getInvestorProducts("Bearer ${(it as User.LoggedInUser).bearerToken}")
                }
            }
            .flatMap { response ->
                if (response.isSuccessful && response.body() != null) {
                    Single.just(
                        Portfolio.UserPortfolio(
                            response.body()!!.totalPlanValue,
                            response.body()!!.productList.map {
                                InvestorProduct(
                                    it.productId,
                                    it.planValue,
                                    it.productDetailEntity.name,
                                    it.moneyBox,
                                    it.productDetailEntity.colour
                                )
                            })
                    )
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

    private fun generateError(response: ResponseBody): Single<Portfolio> {
        val jsonObjectError = JSONObject(response.string())
        return Single.error(
            ServerException(
                name = jsonObjectError.getString("Name"),
                errorMessage = jsonObjectError.getString("Message")
            )
        )
    }
}