package com.challenge.profileviewer.domain.usecases

import android.arch.lifecycle.MutableLiveData
import com.challenge.profileviewer.data.user.UserRepo
import com.challenge.profileviewer.ui.profile.ProfileModel
import com.challenge.profileviewer.ui.profile.ProfileViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class LoadProfileUsecase @Inject constructor(private val userRepo: UserRepo) {
    private var disposable: CompositeDisposable = CompositeDisposable()

    fun loadProfile(profileModelLiveData: MutableLiveData<ProfileModel>) {
        profileModelLiveData.value =
                profileModelLiveData.value?.copy(command = ProfileViewModel.Command.SHOW_LOADING)
        userRepo.getCurrentUser()
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribe({
                profileModelLiveData.postValue(ProfileModel(ProfileViewModel.Command.STANDBY, it))
            }, {
                if (it is SecurityException) {
                    profileModelLiveData.postValue(profileModelLiveData.value?.copy(command = ProfileViewModel.Command.RELOGIN))
                } else {
                    profileModelLiveData.postValue(profileModelLiveData.value?.copy(command = ProfileViewModel.Command.SHOW_ERROR))
                }
            })
    }

    fun clean() {
        disposable.clear()
    }
}
