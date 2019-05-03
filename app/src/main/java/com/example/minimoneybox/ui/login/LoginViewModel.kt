package com.example.minimoneybox.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.minimoneybox.customException.ServerException
import com.example.minimoneybox.model.User
import com.example.minimoneybox.ext.plusAssign
import com.example.minimoneybox.repositories.userAccountRepository.UserAccountRepository
import com.example.minimoneybox.rx.RxSchedulers
import io.reactivex.disposables.CompositeDisposable

class LoginViewModel(
    private val userRepository: UserAccountRepository,
    private val rxSchedulers: RxSchedulers
) : ViewModel() {
    private val disposables = CompositeDisposable()
    private val userData: MutableLiveData<User> = MutableLiveData()
    private val errorMessage: MutableLiveData<String> = MutableLiveData()

    /**
     * Call the login request through the repository and map the result to LiveData
     */
    fun login(email: String, password: String, name: String) {
        disposables += userRepository.login(email, password, name)
            .subscribeOn(rxSchedulers.io())
            .observeOn(rxSchedulers.main())
            .subscribe(
                { t -> userData.postValue(t) },
                {
                    if (it is ServerException) {
                        errorMessage.postValue(it.errorMessage)
                    } else {
                        errorMessage.postValue("")
                    }
                }
            )
    }

    fun resetUserData() {
        userData.postValue(User.EMPTY)
    }

    fun getUserData(): LiveData<User> = userData
    fun getErrorMessage(): LiveData<String> = errorMessage

    override fun onCleared() {
        disposables.clear()
        super.onCleared()
    }
}