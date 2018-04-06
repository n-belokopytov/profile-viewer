package com.challenge.profileviewer.di

import android.app.Application
import com.challenge.profileviewer.App
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(
    modules = [(AndroidSupportInjectionModule::class),
        (ActivityModule::class), (AppModule::class)]
)
interface AppComponent : AndroidInjector<App> {
    @Component.Builder
    abstract class Builder : AndroidInjector.Builder<App>() {
        @BindsInstance
        abstract fun application(application: Application): Builder
    }
}