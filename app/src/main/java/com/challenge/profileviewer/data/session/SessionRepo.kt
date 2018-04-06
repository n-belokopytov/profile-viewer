package com.challenge.profileviewer.data.session

import com.challenge.profileviewer.data.common.NetworkConfig
import com.challenge.profileviewer.data.session.model.Session
import com.challenge.profileviewer.data.session.model.SessionRequest
import com.challenge.profileviewer.data.session.sources.SessionNetworkAPI
import io.reactivex.Single
import retrofit2.Retrofit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionRepo @Inject() constructor(retrofit: Retrofit) {

    private val sessionNetworkAPI: SessionNetworkAPI =
        retrofit.create(SessionNetworkAPI::class.java)

    fun openSession(email: String, password: String): Single<Session> {
        if (NetworkConfig.FAKE_RESPONSES) {
            return Single.fromCallable { NetworkConfig.FAKE_SESSION }
        } else {
            return sessionNetworkAPI.openSession(SessionRequest(email, password))
        }
    }
}