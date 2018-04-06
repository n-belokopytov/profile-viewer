package com.challenge.profileviewer.data.session.model

import com.google.gson.annotations.SerializedName

data class Session(@SerializedName("userid")val userId: String, val token: String)