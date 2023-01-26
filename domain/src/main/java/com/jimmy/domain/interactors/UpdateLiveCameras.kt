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

import com.jimmy.datasource.fetch
import com.jimmy.datasource.repositories.livecameras.LiveCamerasStore
import com.jimmy.domain.Interactor
import com.jimmy.util.AppCoroutineDispatchers
import com.jimmy.util.Logger
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UpdateLiveCameras @Inject constructor(
    private val liveCamerasStore: LiveCamerasStore,
    private val dispatchers: AppCoroutineDispatchers,
    private val logger: Logger,
) : Interactor<UpdateLiveCameras.Params>() {

    override suspend fun doWork(params: Params) {
        withContext(dispatchers.io) {

            logger.i("--> UpdateLiveCameras params = $params")

            liveCamerasStore.fetch(
                key = params.id,
                forceFresh = params.forceRefresh
            )
        }
    }

    data class Params(val id: String, val forceRefresh: Boolean = false)

}
