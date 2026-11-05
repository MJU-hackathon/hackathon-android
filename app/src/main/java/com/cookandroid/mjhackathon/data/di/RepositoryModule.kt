package com.cookandroid.mjhackathon.data.di

import com.cookandroid.mjhackathon.data.repository.detail.DetailRepository
import com.cookandroid.mjhackathon.data.repository.detail.DetailRepositoryImpl
import com.cookandroid.mjhackathon.data.repository.home.HomeRepository
import com.cookandroid.mjhackathon.data.repository.home.HomeRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindHomeRepository(
        impl: HomeRepositoryImpl,
    ): HomeRepository

    @Binds
    abstract fun bindDetailRepository(
        impl: DetailRepositoryImpl,
    ): DetailRepository
}
