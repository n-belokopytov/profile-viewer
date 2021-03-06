package com.challenge.profileviewer.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.challenge.profileviewer.App
import com.challenge.profileviewer.data.common.NetworkConfig
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


@Module
class AppModule {
    @Provides
    @Singleton
    fun providePreferences(application: Application): SharedPreferences {
        return application.getSharedPreferences(
            App::getPackageName.name,
            Context.MODE_PRIVATE
        )
    }

    @Provides
    @Singleton
    fun provideGson(): Gson {
        val gsonBuilder = GsonBuilder()
        gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
        return gsonBuilder.create()
    }

    @Provides
    @Singleton
    fun provideOkHttpCache(application: Application): Cache {
        val cacheSize: Long = 10 * 1024 * 1024 // 10 MiB
        return Cache(application.cacheDir, cacheSize)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(cache: Cache): OkHttpClient {
        val client = OkHttpClient.Builder()
        client.cache(cache)
        client.addInterceptor { chain ->
            chain.proceed(
                chain.request().newBuilder().addHeader(
                    NetworkConfig.SESSIN_TOKEN_HEADER,
                    NetworkConfig.SESSION_TOKEN
                ).build()
            )
        }
        return client.build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(gson: Gson, okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .baseUrl(NetworkConfig.HOST_URL)
            .client(okHttpClient)
            .build()
    }
}