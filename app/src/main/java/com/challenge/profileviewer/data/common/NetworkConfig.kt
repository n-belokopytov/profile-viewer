package com.challenge.profileviewer.data.common

import com.challenge.profileviewer.data.session.model.Session
import com.challenge.profileviewer.data.user.model.User

class NetworkConfig {
    companion object {
        const val FAKE_RESPONSES: Boolean = true
        const val TIMEOUT_MILLIS: Long = 400
        const val SESSIN_TOKEN_HEADER = "Bearer"
        const val HOST_URL = "https://my-json-server.typicode.com/n-belokopytov/"
        var SESSION_TOKEN = ""
        val FAKE_USER: User = User(
            "leet1337",
            "leet@gmail.com",
            "leet",
            "https://s.inyourpocket.com/gallery/7137.jpg"
        )

        val FAKE_SESSION: Session = Session(
            "leet1337",
            "leettoken"
        )

        val FAKE_AVATAR_URL: String = "https://s.inyourpocket.com/gallery/7137.jpg"
    }
}