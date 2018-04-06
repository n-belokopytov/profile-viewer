package com.challenge.profileviewer.ui.profile

import android.arch.lifecycle.MutableLiveData
import com.challenge.profileviewer.domain.usecases.ChangeAvatarUsecase
import com.challenge.profileviewer.domain.usecases.LoadProfileUsecase
import javax.inject.Inject

class ProfileViewModel @Inject constructor(
    private val loadProfileUsecase: LoadProfileUsecase,
    private val changeAvatarUsecase: ChangeAvatarUsecase
) {
    enum class Command { STANDBY, SHOW_LOADING, ADD_PHOTO, SHOW_ERROR, RELOGIN }

    var profileModelLiveData: MutableLiveData<ProfileModel> = MutableLiveData()

    init {
        profileModelLiveData.value = ProfileModel(Command.STANDBY, null)
        loadProfileUsecase.loadProfile(profileModelLiveData)
    }

    fun changeAvatar(url: String) {
        changeAvatarUsecase.changeAvatar(profileModelLiveData, url)
    }

    fun showError() {
        profileModelLiveData.value = profileModelLiveData.value?.copy(command = Command.SHOW_ERROR)
    }

    fun changeAvatarClicked() {
        profileModelLiveData.value = profileModelLiveData.value?.copy(command = Command.ADD_PHOTO)
    }

    fun clean() {
        changeAvatarUsecase.clean()
        loadProfileUsecase.clean()
    }

    fun refresh() {
        loadProfileUsecase.loadProfile(profileModelLiveData)
    }
}