package com.challenge.profileviewer.ui.login

import android.arch.lifecycle.MutableLiveData
import com.challenge.profileviewer.domain.usecases.LoginUserUsecase
import javax.inject.Inject

class LoginViewModel @Inject constructor(private val loginUserUsecase: LoginUserUsecase) {

    enum class Command { STANDBY, SHOW_LOADING, SHOW_ERROR, FINISH }

    var loginModelLiveData: MutableLiveData<LoginModel> = MutableLiveData()

    init {
        loginModelLiveData.value = LoginModel(Command.STANDBY)
    }

    fun login(email: String, password: String) {
        loginUserUsecase.openSession(loginModelLiveData, email, password)
    }

    fun clean() {
        loginUserUsecase.clean()
    }
}