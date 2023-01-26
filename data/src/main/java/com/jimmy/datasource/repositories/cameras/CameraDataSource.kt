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

package com.jimmy.datasource.repositories.cameras

import com.jimmy.datasource.entities.CameraEntity
import com.jimmy.datasource.entities.CameraEntry
import com.jimmy.datasource.mappers.IndexedMapper
import com.jimmy.datasource.mappers.CameraModelToEntity
import com.jimmy.datasource.mappers.pairMapperOf
import com.jimmy.datasource.model.CameraItem
import com.jimmy.datasource.services.CameraApiServices
import com.jimmy.extensions.bodyOrThrow
import com.jimmy.extensions.withRetry
import retrofit2.awaitResponse
import javax.inject.Inject
import javax.inject.Provider

class CameraDataSource @Inject constructor(
    private val showService: Provider<CameraApiServices>,
    showMapper: CameraModelToEntity
) {
    private val entryMapper = IndexedMapper<CameraItem, CameraEntry> { index, _ ->
        CameraEntry(aid = 0, pageOrder = index, page = 0)
    }

    private val resultsMapper = pairMapperOf(showMapper, entryMapper)

    suspend operator fun invoke(
        page: Int,
        pageSize: Int
    ): List<Pair<CameraEntity, CameraEntry>> = withRetry {

        println("==> get camera remote data source <--")

        showService.get().getAllCamera(page, pageSize)
            .awaitResponse()
            .let {
                resultsMapper.invoke(it.bodyOrThrow().data)
            }
    }
}
