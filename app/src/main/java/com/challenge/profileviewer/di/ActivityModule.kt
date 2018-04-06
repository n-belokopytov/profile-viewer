package com.challenge.profileviewer.di

import com.challenge.profileviewer.ui.login.LoginActivity
import com.challenge.profileviewer.ui.profile.ProfileActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityModule {

    @ContributesAndroidInjector(modules = [(AppModule::class)])
    abstract fun loginActivity(): LoginActivity

    @ContributesAndroidInjector(modules = [(AppModule::class)])
    abstract fun profileActivity(): ProfileActivity
}