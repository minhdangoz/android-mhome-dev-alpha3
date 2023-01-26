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
import com.jimmy.datasource.entities.CameraEntity
import kotlinx.coroutines.flow.Flow

@Dao
abstract class CameraDao : EntityDao<CameraEntity>() {

    @Query("SELECT * FROM cameras WHERE id IN (:ids)")
    abstract fun getCamerasWithIds(ids: List<Long>): Flow<List<CameraEntity>>

    @Query("SELECT * FROM cameras WHERE aid = :aid")
    abstract fun getCameraWithIdFlow(aid: Long): Flow<CameraEntity>

    @Query("SELECT * FROM cameras WHERE aid = :aid")
    abstract suspend fun getCameraWithId(aid: Long): CameraEntity?

    suspend fun getCameraWithIdOrThrow(id: Long): CameraEntity {
        return getCameraWithId(id)
            ?: throw IllegalArgumentException("No show with aid $id in database")
    }

    @Query("DELETE FROM cameras WHERE aid = :aid")
    abstract suspend fun delete(aid: Long)

    @Query("DELETE FROM cameras")
    abstract suspend fun deleteAll()

    @Query("SELECT aid FROM cameras WHERE id = :id")
    abstract suspend fun getAidForId(id: String): Long?

    suspend fun getIdOrSavePlaceholder(show: CameraEntity): Long {
        val aid: Long? = if (show.id != null) getAidForId(show.id) else null
        if (aid != null && aid >= 0) {
            return aid
        }
        return insert(show)
    }
}
