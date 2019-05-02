package com.example.minimoneybox.ui.userAccount

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.minimoneybox.customException.UserIsNotLoggedInException
import com.example.minimoneybox.data.InvestorProductData
import com.example.minimoneybox.data.ProductData
import com.example.minimoneybox.data.UserData
import com.example.minimoneybox.ext.plusAssign
import com.example.minimoneybox.repositories.productRepository.ProductRepository
import com.example.minimoneybox.repositories.userRepository.UserRepository
import com.example.minimoneybox.rx.RxSchedulers
import io.reactivex.disposables.CompositeDisposable

/**
 * The UserAccountViewModel will get all the neccessary data from the repositories and streams them to the fragment through LiveData
 */
class UserAccountViewModel(
    private val userRepository: UserRepository,
    private val productRepository: ProductRepository,
    private val rxSchedulers: RxSchedulers
) : ViewModel() {
    private val disposables = CompositeDisposable()
    private val name: MutableLiveData<String> = MutableLiveData()
    private val productList: MutableLiveData<List<InvestorProductData>> = MutableLiveData()
    private val totalPlanValue: MutableLiveData<Float> = MutableLiveData()
    private val isUserLoggedIn: MutableLiveData<Boolean> = MutableLiveData()

    init {
        observeUserData()
    }

    private fun observeUserData() {
        disposables += userRepository.getUserData()
            .subscribeOn(rxSchedulers.io())
            .observeOn(rxSchedulers.main())
            .subscribe {
                if (it is UserData.User) {
                    name.postValue(it.name)
                } else {
                    isUserLoggedIn.postValue(false)
                }
            }
    }

    fun observeProductList() {
        disposables += productRepository.fetchInvestorProducts()
            .subscribeOn(rxSchedulers.io())
            .observeOn(rxSchedulers.main())
            .subscribe(
                {
                    it as ProductData.Product
                    totalPlanValue.postValue(it.totalPlanValue)
                    productList.postValue(it.productList)
                    isUserLoggedIn.postValue(true)
                },
                {
                    if (it is UserIsNotLoggedInException) isUserLoggedIn.postValue(false)
                }
            )
    }

    fun getName(): LiveData<String> = name
    fun getProductList(): LiveData<List<InvestorProductData>> = productList
    fun isUserLoggedIn(): LiveData<Boolean> = isUserLoggedIn
    fun getTotalPlanValue(): LiveData<Float> = totalPlanValue

    override fun onCleared() {
        disposables.clear()
        super.onCleared()
    }
}