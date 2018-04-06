package com.challenge.profileviewer.data.user

import com.challenge.profileviewer.data.common.NetworkConfig
import com.challenge.profileviewer.data.user.model.ChangeAvatarRequest
import com.challenge.profileviewer.data.user.model.User
import com.challenge.profileviewer.data.user.sources.UserNetworkAPI
import com.challenge.profileviewer.data.user.sources.UserSharedPrefs
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepo @Inject constructor(
    private val userSharedPrefs: UserSharedPrefs,
    retrofitBuilder: Retrofit
) {

    private val userNetworkAPI: UserNetworkAPI = retrofitBuilder.create(UserNetworkAPI::class.java)

    fun getUser(userId: String): Observable<User> {
        if (NetworkConfig.FAKE_RESPONSES) {
            return Observable.concatArrayEager(
                Observable.fromCallable { userSharedPrefs.user },
                Observable.fromCallable {
                    NetworkConfig.FAKE_USER
                }.materialize()
                    .filter { !it.isOnError }
                    .dematerialize<User>()
                    .debounce(NetworkConfig.TIMEOUT_MILLIS, TimeUnit.MILLISECONDS).map {
                        var networkUser = it
                        userSharedPrefs.user?.password?.let {
                            networkUser = networkUser.copy(password = it)
                        }
                        userSharedPrefs.user = networkUser
                        networkUser
                    })
        } else {
            return Observable.concatArrayEager(
                Observable.fromCallable { userSharedPrefs.user },
                userNetworkAPI.getUser(userId).toObservable().materialize()
                    .filter { !it.isOnError }
                    .dematerialize<User>()
                    .debounce(NetworkConfig.TIMEOUT_MILLIS, TimeUnit.MILLISECONDS).map {
                        var networkUser = it
                        userSharedPrefs.user?.password?.let {
                            networkUser = networkUser.copy(password = it)
                        }
                        userSharedPrefs.user = networkUser
                        networkUser
                    })
        }
    }

    fun storeNewUserCreds(userId: String, email: String, password: String): Completable {
        return Completable.create { userSharedPrefs.user = User(userId, email, password, null) }
    }

    fun getCurrentUser(): Observable<User> {
        val currentUser = userSharedPrefs.user
        return if (currentUser?.userId != null) {
            getUser(currentUser.userId)
        } else {
            Observable.error(SecurityException("User not authenticated"))
        }
    }

    fun changeAvatar(imageInBase64: String): Single<String> {
        val currentUserId = userSharedPrefs.user?.userId
        if (NetworkConfig.FAKE_RESPONSES) {
            return if (currentUserId != null) {
                Single.fromCallable { NetworkConfig.FAKE_AVATAR_URL }
            } else {
                Single.error(SecurityException("User not authenticated"))
            }
        } else {
            return if (currentUserId != null) {
                userNetworkAPI.changeAvatar(
                    currentUserId,
                    ChangeAvatarRequest(imageInBase64)
                ).map { it.avatarUrl }
            } else {
                Single.error(SecurityException("User not authenticated"))
            }
        }
    }
}