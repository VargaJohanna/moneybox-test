package com.example.minimoneybox.ui.individualProduct

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.minimoneybox.ext.plusAssign
import com.example.minimoneybox.repositories.paymentRepository.PaymentRepository
import com.example.minimoneybox.rx.RxSchedulers
import io.reactivex.disposables.CompositeDisposable

class IndividualProductViewModel(
    private val productId: Int,
    private val paymentRepository: PaymentRepository,
    private val rxSchedulers: RxSchedulers
) : ViewModel() {
    private val disposables = CompositeDisposable()
    private val moneyboxValue: MutableLiveData<Int> = MutableLiveData()
    private val paymentError: MutableLiveData<Boolean> = MutableLiveData()

    fun payMoneybox(amount: Int) {
        disposables += paymentRepository.payMoneybox(productId = productId, amount = amount)
            .subscribeOn(rxSchedulers.io())
            .observeOn(rxSchedulers.main())
            .subscribe(
                { moneyboxValue.postValue(it.moneyboxValue)},
                {
                    paymentError.postValue(true)
                }
            )
    }

    fun getMoneyboxValue(): LiveData<Int> = moneyboxValue
    fun getPaymentError(): LiveData<Boolean> = paymentError

    override fun onCleared() {
        disposables.clear()
        super.onCleared()
    }
}