package com.challenge.profileviewer.data.user.sources

import com.challenge.profileviewer.data.user.model.ChangeAvatarRequest
import com.challenge.profileviewer.data.user.model.ChangeAvatarResponse
import com.challenge.profileviewer.data.user.model.User
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface UserNetworkAPI {
    @GET("users/{user}")
    fun getUser(@Path("user") userId: String): Single<User>

    @POST("/users/{user}/avatar")
    fun changeAvatar(@Path("user") userId: String,
                     @Body changeAvatarRequest: ChangeAvatarRequest): Single<ChangeAvatarResponse>
}