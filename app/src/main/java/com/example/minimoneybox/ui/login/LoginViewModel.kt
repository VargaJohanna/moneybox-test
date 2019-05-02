package com.example.minimoneybox.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.minimoneybox.customException.IncorrectLoginException
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
    private val showIncorrectLogin: MutableLiveData<Boolean> = MutableLiveData()

    /**
     * Call the login request through the repository and turn the result into LiveData
     */
    fun login(email:String, password: String, name: String) {
        disposables += userRepository.login(email, password, name)
            .subscribeOn(rxSchedulers.io())
            .observeOn(rxSchedulers.main())
            .subscribe(
                { t -> userData.postValue(t)},
                {
                    if(it is IncorrectLoginException) {
                        showIncorrectLogin.postValue(true)
                    } else {
                        userData.postValue(UserData.EMPTY)
                    }
                })
    }

    fun getUserData(): LiveData<UserData> = userData
    fun showInCorrectLogin(): LiveData<Boolean> = showIncorrectLogin

    override fun onCleared() {
        disposables.clear()
        super.onCleared()
    }
}