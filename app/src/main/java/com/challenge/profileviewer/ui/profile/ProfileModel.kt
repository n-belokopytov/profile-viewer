package com.challenge.profileviewer.ui.profile

import com.challenge.profileviewer.data.user.model.User

data class ProfileModel(val command: ProfileViewModel.Command, val user: User?)