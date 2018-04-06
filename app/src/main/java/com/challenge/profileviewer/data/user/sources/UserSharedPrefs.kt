package com.challenge.profileviewer.data.user.sources

import android.content.SharedPreferences
import com.challenge.profileviewer.data.user.model.User
import com.google.gson.Gson
import io.reactivex.Single
import android.R.id.edit
import javax.inject.Inject


class UserSharedPrefs @Inject constructor(val sharedPreferences: SharedPreferences) {
    var user: User?
        get() =
            sharedPreferences.getString("user", null)?.let {
                Gson().fromJson(it, User::class.java)
            }
        set(value) =
            sharedPreferences.edit().putString("user", Gson().toJson(value)).apply()
}