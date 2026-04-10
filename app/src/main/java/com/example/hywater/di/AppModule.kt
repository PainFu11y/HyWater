package com.example.hywater.di

import com.example.hywater.data.repository.FountainRepositoryImpl
import com.example.hywater.domain.repository.FountainRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Binds interface types to their concrete implementations.
 * Using @Binds instead of @Provides avoids unnecessary object instantiation at the DI level.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    @Singleton
    abstract fun bindFountainRepository(
        impl: FountainRepositoryImpl
    ): FountainRepository
}
