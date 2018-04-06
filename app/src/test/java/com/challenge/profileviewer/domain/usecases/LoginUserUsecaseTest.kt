package com.challenge.profileviewer.domain.usecases

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.lifecycle.MutableLiveData
import com.challenge.profileviewer.data.common.Config
import com.challenge.profileviewer.data.session.SessionRepo
import com.challenge.profileviewer.data.session.model.Session
import com.challenge.profileviewer.data.user.UserRepo
import com.challenge.profileviewer.data.user.model.User
import com.challenge.profileviewer.ui.login.LoginModel
import com.challenge.profileviewer.ui.login.LoginViewModel
import io.github.plastix.rxschedulerrule.RxSchedulerRule
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations

class LoginUserUsecaseTest {

    inline fun <reified T> lambdaMock(): T = mock(T::class.java)

    companion object {
        const val TEST_USER_ID = "leet1337"
        const val TEST_EMAIL = "leet@gmail.com"
        const val TEST_PASSWORD = "leet"
        const val TEST_TOKEN = "leettoken"
    }

    @InjectMocks
    lateinit var loginUserUsecase: LoginUserUsecase

    @Mock
    lateinit var sessionRepo: SessionRepo

    @Mock
    lateinit var userRepo: UserRepo

    @get:Rule
    val schedulerRule = RxSchedulerRule()
    @get:Rule
    val rule = InstantTaskExecutorRule()

    val loginModelLiveData = MutableLiveData<LoginModel>()


    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        loginModelLiveData.value = LoginModel(LoginViewModel.Command.STANDBY)
    }

    @Test
    fun test_successful_login_stores_session_and_user_and_finishes() {

        val getUserObservable = Observable.fromCallable {
            User(TEST_USER_ID, TEST_EMAIL, TEST_PASSWORD, null)
        }

        val storeUserCompletable = Completable.fromCallable { }

        val openSessionSingle = Single.fromCallable {
            Session(TEST_USER_ID, TEST_TOKEN)
        }

        `when`(userRepo.getUser(TEST_USER_ID)).thenReturn(getUserObservable)
        `when`(userRepo.storeNewUserCreds(TEST_USER_ID, TEST_EMAIL, TEST_PASSWORD)).thenReturn(
            storeUserCompletable
        )
        `when`(sessionRepo.openSession(TEST_EMAIL, TEST_PASSWORD)).thenReturn(openSessionSingle)

        val observer = lambdaMock<(LoginModel?) -> Unit>()
        loginModelLiveData.observeForever {
            observer(it)
        }

        loginUserUsecase.openSession(loginModelLiveData, TEST_EMAIL, TEST_PASSWORD)

        assert(loginModelLiveData.value?.command == LoginViewModel.Command.FINISH)
        assert(Config.SESSION_TOKEN == TEST_TOKEN)
        verify(sessionRepo).openSession(TEST_EMAIL, TEST_PASSWORD)
        verify(userRepo).storeNewUserCreds(TEST_USER_ID, TEST_EMAIL, TEST_PASSWORD)
        verify(observer).invoke(LoginModel(LoginViewModel.Command.SHOW_LOADING))
        verify(observer).invoke(LoginModel(LoginViewModel.Command.FINISH))
    }

    @Test
    fun test_unsuccessful_login_errors() {

        val testEmail = "leet@gmail.com"
        val testPassword = "leet"

        `when`(sessionRepo.openSession(testEmail, testPassword)).thenReturn(
            Single.error(
                SecurityException("User not authenticated")
            )
        )

        val observer = lambdaMock<(LoginModel?) -> Unit>()
        loginModelLiveData.observeForever {
            observer(it)
        }

        loginUserUsecase.openSession(loginModelLiveData, testEmail, testPassword)

        assert(loginModelLiveData.value?.command == LoginViewModel.Command.SHOW_ERROR)
        verify(sessionRepo).openSession(testEmail, testPassword)
        verify(observer).invoke(LoginModel(LoginViewModel.Command.SHOW_LOADING))
        verify(observer).invoke(LoginModel(LoginViewModel.Command.SHOW_ERROR))

    }
}