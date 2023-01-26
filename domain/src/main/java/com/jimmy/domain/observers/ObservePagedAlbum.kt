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

package com.jimmy.domain.observers

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.jimmy.datasource.daos.AlbumDao
import com.jimmy.datasource.daos.AlbumEntryDao
import com.jimmy.datasource.model.ParamsConfig
import com.jimmy.datasource.resultentities.AlbumEntryWithImage
import com.jimmy.domain.PaginatedEntryAlbumRemoteMediator
import com.jimmy.domain.PagingInteractor
import com.jimmy.domain.interactors.UpdateAlbum
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
class ObservePagedAlbum @Inject constructor(
    private val updateAlbum: UpdateAlbum,
    private val albumDao: AlbumEntryDao,
) : PagingInteractor<ObservePagedAlbum.Params, AlbumEntryWithImage>() {

    override fun createObservable(
        params: Params
    ): Flow<PagingData<AlbumEntryWithImage>> {

        return Pager(
            config = params.pagingConfig,
            remoteMediator = PaginatedEntryAlbumRemoteMediator { page ->
                updateAlbum.executeSync(
                    UpdateAlbum.Params(
                        page = page,
                        forceRefresh = true,
                        paramsConfig = params.paramsConfig,
                    )
                )
            },
            pagingSourceFactory = albumDao::entriesPagingSource
        ).flow

    }

    data class Params(
        override val pagingConfig: PagingConfig,
        val paramsConfig: ParamsConfig,
    ) : Parameters<AlbumEntryWithImage>
}
