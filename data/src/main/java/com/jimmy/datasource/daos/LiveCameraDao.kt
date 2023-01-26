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

package com.jimmy.datasource.daos

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.jimmy.datasource.entities.LiveCameraEntity
import kotlinx.coroutines.flow.Flow

@Dao
abstract class LiveCameraDao: EntityDao<LiveCameraEntity>() {

    @Transaction
    @Query("SELECT * FROM live_cameras")
    abstract fun entriesObservable(): Flow<LiveCameraEntity>

    @Query("SELECT * FROM live_cameras WHERE id = :id")
    abstract fun getLiveCameraWithId(id: String): LiveCameraEntity

    @Query("SELECT * FROM live_cameras WHERE id = :id")
    abstract fun getLiveCameraWithIdFlow(id: String): Flow<LiveCameraEntity>

    @Query("DELETE FROM live_cameras WHERE id = :id")
    abstract suspend fun delete(id: String)

    @Query("DELETE FROM live_cameras")
    abstract suspend fun deleteAll()

}
