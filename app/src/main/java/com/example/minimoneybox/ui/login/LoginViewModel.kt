package com.example.minimoneybox.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.minimoneybox.data.UserData
import com.example.minimoneybox.ext.plusAssign
import com.example.minimoneybox.repositories.userRepository.UserRepository
import com.example.minimoneybox.rx.RxSchedulers
import io.reactivex.disposables.CompositeDisposable

class LoginViewModel(
    private val userRepository: UserRepository,
    private val rxSchedulers: RxSchedulers
): ViewModel() {
    private val disposables = CompositeDisposable()
    private val userData: MutableLiveData<UserData> = MutableLiveData()

    fun login(email:String, password: String, name: String) {
        disposables += userRepository.login(email, password, name)
            .subscribeOn(rxSchedulers.io())
            .observeOn(rxSchedulers.main())
            .subscribe{t -> userData.postValue(t)}
    }

    fun getUserData(): LiveData<UserData> = userData
}