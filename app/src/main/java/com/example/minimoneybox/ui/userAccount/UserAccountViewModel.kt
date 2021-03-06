package com.example.minimoneybox.ui.userAccount

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.minimoneybox.customException.ServerException
import com.example.minimoneybox.model.InvestorProduct
import com.example.minimoneybox.model.Portfolio
import com.example.minimoneybox.model.User
import com.example.minimoneybox.ext.plusAssign
import com.example.minimoneybox.repositories.productRepository.ProductRepository
import com.example.minimoneybox.repositories.userAccountRepository.UserAccountRepository
import com.example.minimoneybox.rx.RxSchedulers
import io.reactivex.disposables.CompositeDisposable

/**
 * The UserAccountViewModel will get all the necessary data from the repositories and streams them to the fragment through LiveData
 */
class UserAccountViewModel(
    private val userRepository: UserAccountRepository,
    private val productRepository: ProductRepository,
    private val rxSchedulers: RxSchedulers
) : ViewModel() {
    private val disposables = CompositeDisposable()
    private val name: MutableLiveData<String> = MutableLiveData()
    private val productList: MutableLiveData<List<InvestorProduct>> = MutableLiveData()
    private val totalPlanValue: MutableLiveData<Float> = MutableLiveData()
    private val logoutUser: MutableLiveData<Boolean> = MutableLiveData()
    private val errorMessage: MutableLiveData<String> = MutableLiveData()

    init {
        observeUserData()
    }

    private fun observeUserData() {
        disposables += userRepository.getUserData()
            .subscribeOn(rxSchedulers.io())
            .observeOn(rxSchedulers.main())
            .subscribe {
                if (it is User.LoggedInUser) {
                    name.postValue(it.name)
                }
            }
    }

    fun observeProductList() {
        disposables += productRepository.fetchInvestorProducts()
            .subscribeOn(rxSchedulers.io())
            .observeOn(rxSchedulers.main())
            .subscribe(
                {
                    when (it) {
                        Portfolio.EMPTY -> {
                            totalPlanValue.postValue(0f)
                            productList.postValue(emptyList())
                        }
                        else -> {
                            it as Portfolio.UserPortfolio
                            totalPlanValue.postValue(it.totalPlanValue)
                            productList.postValue(it.productList)
                        }
                    }
                },
                {
                    if (it is ServerException) {
                        if (it.name == "Bearer token expired" || it.name == "LoggedInUser session not found") {
                            logoutUser.postValue(true)
                        }
                        errorMessage.postValue(it.errorMessage)
                    }
                }
            )
    }

    fun clearData() {
        userRepository.clearUserData()
    }

    fun getName(): LiveData<String> = name
    fun getProductList(): LiveData<List<InvestorProduct>> = productList
    fun logoutUser(): LiveData<Boolean> = logoutUser
    fun getTotalPlanValue(): LiveData<Float> = totalPlanValue
    fun getErrorMessage(): LiveData<String> = errorMessage

    override fun onCleared() {
        disposables.clear()
        super.onCleared()
    }
}