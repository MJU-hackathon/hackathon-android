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
    @Singleton
    fun provideOkHttpClient(userKeyStore: UserKeyStore): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor { chain ->
                chain.proceed(
                    chain.request().newBuilder()
                        .header("X-User-Key", userKeyStore.getOrCreate())
                        .build()
                )
            }
            .apply {
                if (BuildConfig.DEBUG) {
                    val headerLogger = HttpLoggingInterceptor().apply {
                        redactHeader("X-User-Key")
                        level = HttpLoggingInterceptor.Level.HEADERS
                    }
                    val questBodyLogger = HttpLoggingInterceptor().apply {
                        redactHeader("X-User-Key")
                        level = HttpLoggingInterceptor.Level.BODY
                    }
                    addInterceptor { chain ->
                        val path = chain.request().url.encodedPath
                        val logger = if (
                            path.endsWith("/api/daily-quests/today") ||
                            path.endsWith("/api/daily-quests/ensure") ||
                            (chain.request().method == "GET" &&
                                Regex("/api/quests/[0-9]+$").containsMatchIn(path))
                        ) questBodyLogger else headerLogger
                        logger.intercept(chain)
                    }
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

    @Provides
    @Singleton
    fun provideHomeService(retrofit: Retrofit): HomeService =
        retrofit.create(HomeService::class.java)

    @Provides
    fun provideDetailService(retrofit: Retrofit): DetailService =
        retrofit.create(DetailService::class.java)

    @Provides
    fun provideProfileService(retrofit: Retrofit): ProfileService =
        retrofit.create(ProfileService::class.java)

    @Provides
    fun providePostDailyService(retrofit: Retrofit): PostDailyService =
        retrofit.create(PostDailyService::class.java)

    @Provides
    fun provideMyService(retrofit: Retrofit): MyService =
        retrofit.create(MyService::class.java)
}
