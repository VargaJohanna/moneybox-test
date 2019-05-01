package com.example.minimoneybox.repositories.productRepository

import com.example.minimoneybox.UserIsNotLoggedInException
import com.example.minimoneybox.data.InvestorProductData
import com.example.minimoneybox.data.ProductData
import com.example.minimoneybox.data.UserData
import com.example.minimoneybox.network.MoneyBoxService
import com.example.minimoneybox.repositories.userRepository.UserRepository
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.subjects.BehaviorSubject

class ProductRepositoryImpl(
    private val service: MoneyBoxService,
    private val userRepository: UserRepository
) : ProductRepository {

    private val productSubject: BehaviorSubject<ProductData> = BehaviorSubject.create()

    override fun fetchInvestorProducts(): Observable<ProductData> {
        return userRepository.getUserData()
            .flatMapSingle {
                when (it) {
                    UserData.EMPTY -> Single.error(UserIsNotLoggedInException())
                    else -> service.getInvestorProducts((it as UserData.User).bearerToken)
                }
            }

            .map { responseEntity ->
                ProductData.Product(
                    responseEntity.totalPlanValue,
                    responseEntity.productList.map {
                        InvestorProductData(it.planValue, it.productDetailEntity.name, it.moneyBox)
                    })
            }
    }

    override fun getCachedProduct(): Observable<ProductData> = productSubject
}