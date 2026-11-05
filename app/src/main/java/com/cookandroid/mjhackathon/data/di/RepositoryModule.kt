package com.cookandroid.mjhackathon.data.di

import com.cookandroid.mjhackathon.data.repository.detail.DetailRepository
import com.cookandroid.mjhackathon.data.repository.detail.DetailRepositoryImpl
import com.cookandroid.mjhackathon.data.repository.home.HomeRepository
import com.cookandroid.mjhackathon.data.repository.home.HomeRepositoryImpl
import com.cookandroid.mjhackathon.data.repository.profile.ProfileRepository
import com.cookandroid.mjhackathon.data.repository.profile.ProfileRepositoryImpl
import com.cookandroid.mjhackathon.data.repository.postDaily.PostDailyRepository
import com.cookandroid.mjhackathon.data.repository.postDaily.PostDailyRepositoryImpl
import com.cookandroid.mjhackathon.data.repository.my.MyRepository
import com.cookandroid.mjhackathon.data.repository.my.MyRepositoryImpl
import com.cookandroid.mjhackathon.data.repository.location.CurrentLocationProvider
import com.cookandroid.mjhackathon.data.repository.location.FusedCurrentLocationProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    abstract fun bindCurrentLocationProvider(impl: FusedCurrentLocationProvider): CurrentLocationProvider


    @Binds
    abstract fun bindProfileRepository(
        impl: ProfileRepositoryImpl,
    ): ProfileRepository

    @Binds
    abstract fun bindPostDailyRepository(
        impl: PostDailyRepositoryImpl,
    ): PostDailyRepository

    @Binds
    abstract fun bindMyRepository(
        impl: MyRepositoryImpl,
    ): MyRepository

    @Binds
    abstract fun bindHomeRepository(
        impl: HomeRepositoryImpl,
    ): HomeRepository

    @Binds
    abstract fun bindDetailRepository(
        impl: DetailRepositoryImpl,
    ): DetailRepository
}
