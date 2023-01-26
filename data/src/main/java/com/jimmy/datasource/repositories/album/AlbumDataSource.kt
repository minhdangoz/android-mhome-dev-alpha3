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

package com.jimmy.datasource.repositories.album

import com.jimmy.datasource.entities.AlbumEntity
import com.jimmy.datasource.entities.AlbumEntry
import com.jimmy.datasource.mappers.AlbumModelToEntity
import com.jimmy.datasource.mappers.IndexedMapper
import com.jimmy.datasource.mappers.pairMapperOf
import com.jimmy.datasource.model.AlbumItem
import com.jimmy.datasource.model.ParamsConfig
import com.jimmy.datasource.services.CameraApiServices
import com.jimmy.extensions.withRetry
import com.jimmy.util.Logger
import retrofit2.awaitResponse
import javax.inject.Inject
import javax.inject.Provider

class AlbumDataSource @Inject constructor(
    private val apiServices: Provider<CameraApiServices>,
    mapper: AlbumModelToEntity,
    private val logger: Logger
) {
    private val entryMapper = IndexedMapper<AlbumItem, AlbumEntry> { index, _ ->
        AlbumEntry(aid = 0, pageOrder = index, page = 0)
    }

    private val resultsMapper = pairMapperOf(mapper, entryMapper)

    suspend operator fun invoke(
        page: Int,
        pageSize: Int,
        params: ParamsConfig,
    ): List<Pair<AlbumEntity, AlbumEntry>> = withRetry {

        val hashMap = HashMap<String, String>()
        hashMap["camId"] = params.camId
        hashMap["startTs"] = params.startTs.toString()
        hashMap["endTs"] = params.endTs.toString()
        hashMap["page"] = page.toString()
        hashMap["pageSize"] = pageSize.toString()

        logger.i("--> album source params = $hashMap")

        apiServices.get().getPagingAlbums(hashMap)
            .awaitResponse()
            .let {
                resultsMapper.invoke(it.body()!!.data)
            }
    }
}
