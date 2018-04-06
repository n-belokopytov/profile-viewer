package com.challenge.profileviewer.data.session.sources

import com.challenge.profileviewer.data.session.model.Session
import com.challenge.profileviewer.data.session.model.SessionRequest
import io.reactivex.Observable
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.POST

interface SessionNetworkAPI {
    @POST("sessions/new")
    fun openSession(@Body request: SessionRequest): Single<Session>
}