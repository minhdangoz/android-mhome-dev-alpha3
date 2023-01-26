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
import com.jimmy.datasource.resultentities.CameraEntryWithCamera
import com.jimmy.domain.PaginatedEntryRemoteMediator
import com.jimmy.datasource.daos.CameraEntryDao
import com.jimmy.domain.PagingInteractor
import com.jimmy.domain.interactors.UpdateCameras
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
class ObservePagedCameras @Inject constructor(
    private val updateCameras: UpdateCameras,
    private val cameraEntryDao: CameraEntryDao,
) : PagingInteractor<ObservePagedCameras.Params, CameraEntryWithCamera>() {

    override fun createObservable(
        params: Params
    ): Flow<PagingData<CameraEntryWithCamera>> {

        return Pager(
            config = params.pagingConfig,
            remoteMediator = PaginatedEntryRemoteMediator { page ->
                updateCameras.executeSync(
                    UpdateCameras.Params(page = page, forceRefresh = true)
                )
            },
            pagingSourceFactory = cameraEntryDao::entriesPagingSource
        ).flow

    }

    data class Params(
        override val pagingConfig: PagingConfig,
    ) : Parameters<CameraEntryWithCamera>
}
