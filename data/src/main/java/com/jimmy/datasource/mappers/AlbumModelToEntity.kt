/*
 * Copyright 2018 Google LLC
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

package com.jimmy.datasource.mappers

import com.jimmy.datasource.entities.AlbumEntity
import com.jimmy.datasource.model.AlbumItem
import org.threeten.bp.Instant
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.ZoneId
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AlbumModelToEntity @Inject constructor(
) : Mapper<AlbumItem, AlbumEntity> {
    override suspend fun map(from: AlbumItem) = AlbumEntity(
        id = from.id ?: "",
        boxId = from.boxId ?: "",
        cameraId = from.cameraId ?: "",
        imgUrl = from.imgUrl ?: "",
        size = from.size ?: 0,
        recordTime = OffsetDateTime.ofInstant(from.recordTime?.let { Instant.ofEpochMilli(it) }, ZoneId.systemDefault()),
        createdTime = OffsetDateTime.ofInstant(from.createdTime?.let { Instant.ofEpochMilli(it) }, ZoneId.systemDefault()),
    )
}
