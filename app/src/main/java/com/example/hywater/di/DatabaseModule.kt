package com.example.hywater.di

import android.content.Context
import androidx.room.Room
import com.example.hywater.data.local.db.FountainDao
import com.example.hywater.data.local.db.HyWaterDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideHyWaterDatabase(@ApplicationContext context: Context): HyWaterDatabase =
        Room.databaseBuilder(
            context,
            HyWaterDatabase::class.java,
            HyWaterDatabase.DATABASE_NAME
        )
            .fallbackToDestructiveMigration() // Replace with proper migrations before production release
            .build()

    @Provides
    fun provideFountainDao(database: HyWaterDatabase): FountainDao =
        database.fountainDao()
}
