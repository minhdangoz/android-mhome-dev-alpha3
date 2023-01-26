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

package com.jimmy.datasource.repositories.playback

import com.jimmy.datasource.entities.PlaybackEntity
import com.jimmy.datasource.entities.PlaybackEntry
import com.jimmy.datasource.mappers.IndexedMapper
import com.jimmy.datasource.mappers.PlaybackModelToEntity
import com.jimmy.datasource.mappers.pairMapperOf
import com.jimmy.datasource.model.ParamsConfig
import com.jimmy.datasource.model.PlaybackItem
import com.jimmy.datasource.services.CameraApiServices
import com.jimmy.extensions.withRetry
import retrofit2.awaitResponse
import javax.inject.Inject
import javax.inject.Provider

class PlaybackDataSource @Inject constructor(
    private val apiServices: Provider<CameraApiServices>,
    mapper: PlaybackModelToEntity
) {
    private val entryMapper = IndexedMapper<PlaybackItem, PlaybackEntry> { index, _ ->
        PlaybackEntry(aid = 0, pageOrder = index, page = 0)
    }

    private val resultsMapper = pairMapperOf(mapper, entryMapper)

    suspend operator fun invoke(
        page: Int,
        pageSize: Int,
        paramsConfig: ParamsConfig,
    ): List<Pair<PlaybackEntity, PlaybackEntry>> = withRetry {

        println("==> play back data source <--")
//        val threehoursbefore = System.currentTimeMillis() - 10800000
        val hashMap = HashMap<String, String>()
        hashMap["camId"] = paramsConfig.camId
        hashMap["startTs"] = paramsConfig.startTs.toString()
        hashMap["endTs"] = paramsConfig.endTs.toString()
        hashMap["recordType"] = paramsConfig.recordType
        hashMap["page"] = page.toString()
        hashMap["pageSize"] = pageSize.toString()

        apiServices.get().getPagingPlaybackVideos(hashMap)
            .awaitResponse()
            .let {
                resultsMapper.invoke(it.body()!!.data)
            }
    }
}
