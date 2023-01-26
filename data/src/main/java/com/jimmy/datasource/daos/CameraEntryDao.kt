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

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.jimmy.datasource.entities.CameraEntry
import com.jimmy.datasource.resultentities.CameraEntryWithCamera
import kotlinx.coroutines.flow.Flow

@Dao
abstract class CameraEntryDao : PaginatedEntryDao<CameraEntry, CameraEntryWithCamera>() {
    @Transaction
    @Query("SELECT * FROM camera_entry WHERE page = :page ORDER BY page_order")
    abstract fun entriesObservable(page: Int): Flow<List<CameraEntry>>

    @Transaction
    @Query("SELECT * FROM camera_entry ORDER BY page, page_order LIMIT :count OFFSET :offset")
    abstract fun entriesObservable(count: Int, offset: Int): Flow<List<CameraEntryWithCamera>>

    @Transaction
    @Query("SELECT * FROM camera_entry ORDER BY page, page_order")
    abstract fun entriesPagingSource(): PagingSource<Int, CameraEntryWithCamera>

    @Query("DELETE FROM camera_entry WHERE page = :page")
    abstract override suspend fun deletePage(page: Int)

    @Query("DELETE FROM camera_entry")
    abstract override suspend fun deleteAll()

    @Query("SELECT MAX(page) from camera_entry")
    abstract override suspend fun getLastPage(): Int?
}
