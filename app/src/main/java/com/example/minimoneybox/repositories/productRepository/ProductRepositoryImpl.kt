package com.example.minimoneybox.repositories.productRepository

import android.content.res.Resources
import com.example.minimoneybox.R
import com.example.minimoneybox.customException.ServerException
import com.example.minimoneybox.data.InvestorProductData
import com.example.minimoneybox.data.ProductData
import com.example.minimoneybox.data.UserData
import com.example.minimoneybox.network.MoneyBoxService
import com.example.minimoneybox.repositories.userAccountRepository.UserAccountRepository
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.subjects.BehaviorSubject
import okhttp3.ResponseBody
import org.json.JSONObject

class ProductRepositoryImpl(
    private val service: MoneyBoxService,
    private val userRepository: UserAccountRepository
) : ProductRepository {

    /**
     * Get the user data and if it's not empty then fetch the investor products with the returned token.
     * Then map the response into a Product data class
     */
    override fun fetchInvestorProducts(): Observable<ProductData> {
        return userRepository.getUserData()
            .flatMapSingle {
                when (it) {
                    UserData.EMPTY -> Single.error(ServerException(Resources.getSystem().getString(R.string.generic_error_name), Resources.getSystem().getString(R.string.generic_error)))
                    else -> service.getInvestorProducts("Bearer ${(it as UserData.User).bearerToken}")
                }
            }
            .flatMap { response ->
                if (response.isSuccessful && response.body() != null) {
                    Observable.just(
                        ProductData.Product(
                            response.body()!!.totalPlanValue,
                            response.body()!!.productList.map {
                                InvestorProductData(
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
                    Observable.error(ServerException(Resources.getSystem().getString(R.string.generic_error_name), Resources.getSystem().getString(R.string.generic_error)))
                }
            }
    }

    private fun generateError(response: ResponseBody): Observable<ProductData> {
        val jsonObjectError = JSONObject(response.string())
        return Observable.error(ServerException(name = jsonObjectError.getString("Name"), errorMessage = jsonObjectError.getString("Message")))
    }
}