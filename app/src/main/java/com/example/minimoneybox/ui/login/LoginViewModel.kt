package com.example.minimoneybox.ui.login

import androidx.lifecycle.ViewModel
import com.example.minimoneybox.ext.plusAssign
import com.example.minimoneybox.repositories.UserRepository
import com.example.minimoneybox.rx.RxSchedulers
import io.reactivex.disposables.CompositeDisposable

class LoginViewModel(
    private val userRepository: UserRepository,
    private val rxSchedulers: RxSchedulers
): ViewModel() {
    private val disposables = CompositeDisposable()

    fun login(email:String, password: String, name: String) {
        disposables += userRepository.login(email, password, name)
            .subscribeOn(rxSchedulers.io())
            .observeOn(rxSchedulers.main())
            .subscribe()
    }
}