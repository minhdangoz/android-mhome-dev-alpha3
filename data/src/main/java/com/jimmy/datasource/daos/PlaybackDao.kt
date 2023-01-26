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
import com.jimmy.datasource.entities.PlaybackEntity
import kotlinx.coroutines.flow.Flow

@Dao
abstract class PlaybackDao : EntityDao<PlaybackEntity>() {

    @Query("SELECT * FROM playback WHERE id IN (:ids)")
    abstract fun getPlaybacksWithIds(ids: List<Long>): Flow<List<PlaybackEntity>>

    @Query("SELECT * FROM playback WHERE id = :id")
    abstract fun getPlaybackWithIdFlow(id: Long): Flow<PlaybackEntity>

    @Query("SELECT * FROM playback WHERE id = :id")
    abstract suspend fun getPlaybackWithId(id: Long): PlaybackEntity?

    suspend fun getPlaybackWithIdOrThrow(id: Long): PlaybackEntity {
        return getPlaybackWithId(id)
            ?: throw IllegalArgumentException("No show with aid $id in database")
    }

    @Query("DELETE FROM playback WHERE id = :id")
    abstract suspend fun delete(id: Long)

    @Query("DELETE FROM playback")
    abstract suspend fun deleteAll()

    @Query("SELECT aid FROM playback WHERE id = :id")
    abstract suspend fun getAidForId(id: String): Long?

    suspend fun getIdOrSavePlaceholder(show: PlaybackEntity): Long {
        val aid: Long? = getAidForId(show.id)

        if (aid != null) {
            return aid
        }

        return insert(show)
    }
}
