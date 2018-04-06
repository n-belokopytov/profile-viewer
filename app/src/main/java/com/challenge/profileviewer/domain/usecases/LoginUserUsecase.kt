package com.challenge.profileviewer.domain.usecases

import android.arch.lifecycle.MutableLiveData
import com.challenge.profileviewer.data.common.Config
import com.challenge.profileviewer.data.session.SessionRepo
import com.challenge.profileviewer.data.user.UserRepo
import com.challenge.profileviewer.ui.login.LoginModel
import com.challenge.profileviewer.ui.login.LoginViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class LoginUserUsecase @Inject constructor(private val sessionRepo: SessionRepo, private val userRepo: UserRepo) {
    private var disposable: CompositeDisposable = CompositeDisposable()

    fun openSession(loginModelLiveData: MutableLiveData<LoginModel>, email: String, password: String) {
        loginModelLiveData.value = LoginModel(command = LoginViewModel.Command.SHOW_LOADING)
        disposable.add(
                sessionRepo.openSession(email, password)
                        .subscribeOn(Schedulers.io())
                        .map {
                            Config.SESSION_TOKEN = it.token
                            userRepo.storeNewUserCreds(it.userId, email, password)
                        }
                        .subscribeOn(Schedulers.io())
                        .observeOn(Schedulers.io())
                        .subscribe({
                            loginModelLiveData.postValue(LoginModel(command = LoginViewModel.Command.FINISH))
                        }, {
                            loginModelLiveData.postValue(LoginModel(command = LoginViewModel.Command.SHOW_ERROR))
                        }))
    }

    fun clean() {
        disposable.clear()
    }
}