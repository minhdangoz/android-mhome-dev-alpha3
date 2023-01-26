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

package com.jimmy.domain.interactors

import com.jimmy.datasource.daos.AlbumEntryDao
import com.jimmy.datasource.fetch
import com.jimmy.datasource.model.ParamsConfig
import com.jimmy.datasource.repositories.album.AlbumStore
import com.jimmy.domain.Interactor
import com.jimmy.util.AppCoroutineDispatchers
import com.jimmy.util.Logger
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UpdateAlbum @Inject constructor(
    private val albumStore: AlbumStore,
    private val albumEntryDao: AlbumEntryDao,
    private val dispatchers: AppCoroutineDispatchers,
    private val logger: Logger,
) : Interactor<UpdateAlbum.Params>() {

    override suspend fun doWork(params: Params) {
        withContext(dispatchers.io) {
            val page = when {
                params.page >= 0 -> params.page
                params.page == Page.NEXT_PAGE -> {
                    val lastPage = albumEntryDao.getLastPage()
                    if (lastPage != null) lastPage + 1 else 0
                }
                else -> 0
            }

            logger.i("==@== UpdateAlbum page = $page")

            albumStore.fetch(
                key = page to params.paramsConfig,
                forceFresh = params.forceRefresh
            )

        }
    }

    data class Params(
        val page: Int,
        val paramsConfig: ParamsConfig,
        val forceRefresh: Boolean = false
    )

    object Page {
        const val NEXT_PAGE = -1
        const val REFRESH = -2
    }
}
