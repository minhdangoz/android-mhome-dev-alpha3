/*
 * Copyright 2019 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jimmy.datasource

import android.content.Context
import android.os.Debug
import androidx.room.Room
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object RoomDatabaseModule {
    @Singleton
    @Provides
    fun provideDatabase(
        @ApplicationContext context: Context
    ): AppRoomDatabase {
        val builder = Room.databaseBuilder(context, AppRoomDatabase::class.java, "mhome.db")
            .fallbackToDestructiveMigration()
        if (Debug.isDebuggerConnected()) {
            builder.allowMainThreadQueries()
        }
        return builder.build()
    }
}

@InstallIn(SingletonComponent::class)
@Module
object DatabaseDaoModule {

    @Provides
    fun provideLiveCamerasDao(db: AppDatabase) = db.liveCameraDao()

    @Provides
    fun provideCameraDao(db: AppDatabase) = db.cameraDao()

    @Provides
    fun provideCameraEntryDao(db: AppDatabase) = db.cameraEntryDao()

    @Provides
    fun providePlaybackDao(db: AppDatabase) = db.playbackDao()

    @Provides
    fun providePlaybackAll(db: AppDatabase) = db.allPlaybackDao()

    @Provides
    fun provideAlbumDao(db: AppDatabase) = db.albumDao()

    @Provides
    fun provideAlbumEntryDao(db: AppDatabase) = db.albumEntryDao()

    @Provides
    fun provideLastRequestsDao(db: AppDatabase) = db.lastRequestDao()

}

@InstallIn(SingletonComponent::class)
@Module
abstract class DatabaseModuleBinds {
    @Binds
    abstract fun bindAppDatabase(db: AppRoomDatabase): AppDatabase

    @Singleton
    @Binds
    abstract fun provideDatabaseTransactionRunner(runner: RoomTransactionRunner): DatabaseTransactionRunner
}
