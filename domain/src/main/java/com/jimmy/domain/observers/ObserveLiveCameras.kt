/*
 * Copyright 2020 Google LLC
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

import com.dropbox.android.external.store4.StoreRequest
import com.jimmy.datasource.entities.LiveCameraEntity
import com.jimmy.datasource.filterForResult
import com.jimmy.datasource.model.LiveCameraModel
import com.jimmy.datasource.repositories.livecameras.LiveCamerasStore
import com.jimmy.domain.SubjectInteractor
import com.jimmy.util.AppCoroutineDispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ObserveLiveCameras @Inject constructor(
    private val store: LiveCamerasStore,
    private val dispatchers: AppCoroutineDispatchers,
) : SubjectInteractor<ObserveLiveCameras.Params, LiveCameraModel>() {

    private val TAG = "ObserveLiveCameras"

    override fun createObservable(params: Params): Flow<LiveCameraModel> {
        return store.stream(StoreRequest.fresh(params.id))
            .filterForResult()
            .map { it.requireData() }
            .flowOn(dispatchers.computation)
    }

    data class Params(val id: String)
}
