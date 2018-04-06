package com.challenge.profileviewer.domain.usecases

import android.arch.lifecycle.MutableLiveData
import com.challenge.profileviewer.data.user.UserRepo
import com.challenge.profileviewer.domain.util.ImageToBase64Encoder
import com.challenge.profileviewer.ui.profile.ProfileModel
import com.challenge.profileviewer.ui.profile.ProfileViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject


class ChangeAvatarUsecase @Inject constructor(private val userRepo: UserRepo) {

    private val imageToBase64Encoder: ImageToBase64Encoder = ImageToBase64Encoder()
    private var disposable: CompositeDisposable = CompositeDisposable()

    fun changeAvatar(profileModelLiveData: MutableLiveData<ProfileModel>, pathToImage: String) {
        profileModelLiveData.value =
                profileModelLiveData.value?.copy(command = ProfileViewModel.Command.SHOW_LOADING)
        imageToBase64Encoder.encode(pathToImage)
            .map { userRepo.changeAvatar(it) }
            .flatMap { userRepo.getCurrentUser() }
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