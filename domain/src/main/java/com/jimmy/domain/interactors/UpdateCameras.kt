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

import com.jimmy.datasource.daos.CameraEntryDao
import com.jimmy.datasource.fetch
import com.jimmy.datasource.repositories.allcameras.AllCameraStore
import com.jimmy.domain.Interactor
import com.jimmy.util.AppCoroutineDispatchers
import com.jimmy.util.Logger
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UpdateCameras @Inject constructor(
    private val cameraStore: AllCameraStore,
    private val cameraEntryDao: CameraEntryDao,
    private val dispatchers: AppCoroutineDispatchers,
    private val logger: Logger,
) : Interactor<UpdateCameras.Params>() {

    override suspend fun doWork(params: Params) {
        withContext(dispatchers.io) {
            val page = when {
                params.page >= 0 -> params.page
                params.page == Page.NEXT_PAGE -> {
                    val lastPage = cameraEntryDao.getLastPage()
                    if (lastPage != null) lastPage + 1 else 0
                }
                else -> 0
            }

            logger.i("==@== UpdateCameras page = $page")

            cameraStore.fetch(page, forceFresh = params.forceRefresh)
//                .forEach {
//                allCameraStore.fetch(it.showId)
//            }
        }
    }

    data class Params(val page: Int, val forceRefresh: Boolean = false)

    object Page {
        const val NEXT_PAGE = -1
        const val REFRESH = -2
    }
}
