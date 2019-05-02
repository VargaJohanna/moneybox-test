package com.example.minimoneybox.ui.individualProduct

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.minimoneybox.customException.ServerException
import com.example.minimoneybox.ext.plusAssign
import com.example.minimoneybox.repositories.paymentRepository.PaymentRepository
import com.example.minimoneybox.repositories.userAccountRepository.UserAccountRepository
import com.example.minimoneybox.rx.RxSchedulers
import io.reactivex.disposables.CompositeDisposable

class IndividualProductViewModel(
    private val productId: Int,
    private val paymentRepository: PaymentRepository,
    private val rxSchedulers: RxSchedulers,
    private val userRepository: UserAccountRepository

    ) : ViewModel() {
    private val disposables = CompositeDisposable()
    private val moneyboxValue: MutableLiveData<Int> = MutableLiveData()
    private val errorMessage: MutableLiveData<String> = MutableLiveData()
    private val logoutUser: MutableLiveData<Boolean> = MutableLiveData()

    fun payMoneybox(amount: Int) {
        disposables += paymentRepository.payMoneybox(productId = productId, amount = amount)
            .subscribeOn(rxSchedulers.io())
            .observeOn(rxSchedulers.main())
            .subscribe(
                { moneyboxValue.postValue(it.moneyboxValue) },
                {
                    if (it is ServerException) {
                        if(it.name == "Bearer token expired" || it.name == "User session not found") {
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

    fun getMoneyboxValue(): LiveData<Int> = moneyboxValue
    fun getErrorMessage(): LiveData<String> = errorMessage
    fun logoutUser(): LiveData<Boolean> = logoutUser

    override fun onCleared() {
        disposables.clear()
        super.onCleared()
    }
}