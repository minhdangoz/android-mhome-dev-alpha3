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

import com.jimmy.datasource.daos.AlbumDao
import com.jimmy.datasource.daos.AlbumEntryDao
import com.jimmy.datasource.daos.PlaybackDao
import com.jimmy.datasource.daos.LastRequestDao
import com.jimmy.datasource.daos.LiveCameraDao
import com.jimmy.datasource.daos.PlaybackEntryDao
import com.jimmy.datasource.daos.CameraEntryDao
import com.jimmy.datasource.daos.CameraDao

interface AppDatabase {
    fun cameraDao(): CameraDao
    fun liveCameraDao(): LiveCameraDao
    fun cameraEntryDao(): CameraEntryDao
    fun playbackDao(): PlaybackEntryDao
    fun allPlaybackDao(): PlaybackDao
    fun albumDao(): AlbumDao
    fun albumEntryDao(): AlbumEntryDao
    fun lastRequestDao(): LastRequestDao
}
