package com.challenge.profileviewer.data.user

import com.challenge.profileviewer.data.common.Config
import com.challenge.profileviewer.data.user.model.ChangeAvatarRequest
import com.challenge.profileviewer.data.user.model.User
import com.challenge.profileviewer.data.user.sources.UserSharedPrefs
import com.challenge.profileviewer.data.user.sources.UserNetworkAPI
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepo @Inject constructor(private val userSharedPrefs: UserSharedPrefs, retrofitBuilder: Retrofit) {

    private val userNetworkAPI: UserNetworkAPI = retrofitBuilder.create(UserNetworkAPI::class.java)

    fun getUser(userId: String): Observable<User> {
        return Observable.concatArrayEager(
                Observable.fromCallable { userSharedPrefs.user },
                userNetworkAPI.getUser(userId).toObservable().materialize()
                        .filter { !it.isOnError }
                        .dematerialize<User>()
                        .debounce(Config.TIMEOUT_MILLIS, TimeUnit.MILLISECONDS).map {
                            var networkUser = it
                            userSharedPrefs.user?.password?.let {
                                networkUser = networkUser.copy(password = it)
                            }
                            userSharedPrefs.user = networkUser
                            networkUser
                        })
    }

    fun storeNewUserCreds(userId: String, email: String, password: String): Completable {
        return Completable.create { userSharedPrefs.user = User(userId, email, password, null) }
    }

    fun getCurrentUser(): Observable<User> {
        val currentUser = userSharedPrefs.user
        return Observable.create<User>({
            emitter ->
            if (currentUser?.userId != null) {
                getUser(currentUser.userId)
            } else {
                emitter.onError(SecurityException("User not authenticated"))
            }
        })
    }

    fun changeAvatar(imageInBase64: String): Single<String> {
        val currentUserId = userSharedPrefs.user?.userId
        if (currentUserId != null) {
            return userNetworkAPI.changeAvatar(currentUserId, ChangeAvatarRequest(imageInBase64)).map { it.avatarUrl }
        } else {
            throw SecurityException("User not authenticated")
        }
    }
}