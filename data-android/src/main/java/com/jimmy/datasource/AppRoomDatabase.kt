/*
 * Copyright 2017 Google LLC
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

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.jimmy.datasource.entities.AlbumEntity
import com.jimmy.datasource.entities.AlbumEntry
import com.jimmy.datasource.entities.LastRequest
import com.jimmy.datasource.entities.LiveCameraEntity
import com.jimmy.datasource.entities.CameraEntry
import com.jimmy.datasource.entities.CameraEntity
import com.jimmy.datasource.entities.PlaybackEntity
import com.jimmy.datasource.entities.PlaybackEntry

@Database(
    entities = [
        CameraEntity::class,
        CameraEntry::class,
        LastRequest::class,
        LiveCameraEntity::class,
        PlaybackEntity::class,
        PlaybackEntry::class,
        AlbumEntity::class,
        AlbumEntry::class,
    ],
//    views = [
//        FollowedShowsWatchStats::class,
//        FollowedShowsLastWatched::class,
//        FollowedShowsNextToWatch::class
//    ],
    version = 1,
//    autoMigrations = [
//        AutoMigration(from = 1, to = 2),
//        AutoMigration(from = 2, to = 3),
//    ],
)
@TypeConverters(AppTypeConverters::class)
abstract class AppRoomDatabase : RoomDatabase(), AppDatabase
