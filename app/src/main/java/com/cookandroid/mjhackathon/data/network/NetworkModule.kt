package com.cookandroid.mjhackathon.data.network

import com.cookandroid.mjhackathon.BuildConfig
import com.cookandroid.mjhackathon.data.service.detail.DetailService
import com.cookandroid.mjhackathon.data.service.home.HomeService
import com.cookandroid.mjhackathon.data.service.profile.ProfileService
import com.cookandroid.mjhackathon.data.service.postDaily.PostDailyService
import com.cookandroid.mjhackathon.data.service.my.MyService
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import kotlin.jvm.java

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    fun provideProfileService(retrofit: Retrofit): ProfileService =
        retrofit.create(ProfileService::class.java)

    @Provides
    fun providePostDailyService(retrofit: Retrofit): PostDailyService =
        retrofit.create(PostDailyService::class.java)

    @Provides
    fun provideMyService(retrofit: Retrofit): MyService =
        retrofit.create(MyService::class.java)

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient =
        OkHttpClient.Builder()
            .apply {
                if (BuildConfig.DEBUG) {
                    addInterceptor(HttpLoggingInterceptor().apply {
                        level = HttpLoggingInterceptor.Level.BODY
                    })
                }
            }
            .build()

    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient): Retrofit {
        val json = Json { ignoreUnknownKeys = true }

        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(client)
            .addConverterFactory(
                json.asConverterFactory("application/json".toMediaType())
            )
            .build()
    }

//    @Provides   // 예시 Service 주입
//    @Singleton
//    fun provideExampleService(retrofit: Retrofit): ExampleService =
//        retrofit.create(ExampleService::class.java)

    @Provides   // 예시 Service 주입
    @Singleton
    fun provideHomeService(retrofit: Retrofit): HomeService =
        retrofit.create(HomeService::class.java)

    @Provides
    fun provideDetailService(retrofit: Retrofit): DetailService =
        retrofit.create(DetailService::class.java)
}
