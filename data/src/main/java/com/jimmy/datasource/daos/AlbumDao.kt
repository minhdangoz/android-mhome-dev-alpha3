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
import com.jimmy.datasource.entities.AlbumEntity
import kotlinx.coroutines.flow.Flow

@Dao
abstract class AlbumDao : EntityDao<AlbumEntity>() {

    @Query("SELECT * FROM album WHERE id IN (:ids)")
    abstract fun getAlbumsWithIds(ids: List<Long>): Flow<List<AlbumEntity>>

    @Query("SELECT * FROM album WHERE id = :id")
    abstract fun getAlbumWithIdFlow(id: Long): Flow<AlbumEntity>

    @Query("SELECT * FROM album WHERE id = :id")
    abstract suspend fun getAlbumWithId(id: Long): AlbumEntity?

    suspend fun getAlbumWithIdOrThrow(id: Long): AlbumEntity {
        return getAlbumWithId(id)
            ?: throw IllegalArgumentException("No show with aid $id in database")
    }

    @Query("DELETE FROM album WHERE id = :id")
    abstract suspend fun delete(id: Long)

    @Query("DELETE FROM album")
    abstract suspend fun deleteAll()

    @Query("SELECT aid FROM album WHERE id = :id")
    abstract suspend fun getAidForId(id: String): Long?

    suspend fun getIdOrSavePlaceholder(show: AlbumEntity): Long {
        val aid: Long? = getAidForId(show.id)

        if (aid != null) {
            return aid
        }

        return insert(show)
    }
}
