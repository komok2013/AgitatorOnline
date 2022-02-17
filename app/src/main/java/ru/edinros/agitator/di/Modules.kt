package ru.edinros.agitator.di

import android.content.Context
import androidx.room.Room
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import ru.edinros.agitator.BuildConfig
import ru.edinros.agitator.core.local.AppDB
import ru.edinros.agitator.core.local.dao.AppDao
import ru.edinros.agitator.core.remote.BasicAuthInterceptor
import ru.edinros.agitator.core.remote.NetworkHandler
import ru.edinros.agitator.core.remote.RemoteApi
import ru.edinros.agitator.core.remote.RemoteApi.Companion.BASE_URL
import ru.edinros.agitator.core.remote.RemoteApi.Companion.BASE_URL_DEV
import javax.inject.Singleton

@ExperimentalSerializationApi
@Module
@InstallIn(SingletonComponent::class)
object Modules {
    private val json = Json {
        this.encodeDefaults = true
        ignoreUnknownKeys = true
        explicitNulls = false
    }
/*
    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val httpClientBuilder = OkHttpClient.Builder()
        if (BuildConfig.DEBUG) {
            val loggingInterceptor = HttpLoggingInterceptor()
            loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
            httpClientBuilder.addInterceptor(loggingInterceptor)
        }
        return httpClientBuilder.build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(BASE_URL_DEV)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
*/
    @Provides
    @Singleton
    fun provideOkHttpClientDev(): OkHttpClient {
        val httpClientBuilder = OkHttpClient.Builder()
        if (BuildConfig.DEBUG) {
            val loggingInterceptor = HttpLoggingInterceptor()
            loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
            httpClientBuilder.addInterceptor(loggingInterceptor)
//            httpClientBuilder.addInterceptor(BasicAuthInterceptor("user", "PfrhjqTuj"))
        }
        return httpClientBuilder.build()
    }

    @Provides
    @Singleton
    fun provideRetrofitDev(okHttpClient: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(BASE_URL_DEV)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))

            .build()

    @Provides
    @Singleton
    fun provideRemoteAPI(retrofit: Retrofit): RemoteApi = retrofit.create(RemoteApi::class.java)

    @Provides
    @Singleton
    fun provideHandler(@ApplicationContext appContext: Context) = NetworkHandler(appContext)

    @Provides
    @Singleton
    fun provideDao(@ApplicationContext appContext: Context) = AppDB.getInstance(appContext).dao()
}