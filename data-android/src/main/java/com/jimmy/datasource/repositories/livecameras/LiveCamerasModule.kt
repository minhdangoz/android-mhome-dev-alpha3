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

package com.jimmy.datasource.repositories.livecameras

import com.dropbox.android.external.store4.Fetcher
import com.dropbox.android.external.store4.SourceOfTruth
import com.dropbox.android.external.store4.Store
import com.dropbox.android.external.store4.StoreBuilder
import com.jimmy.datasource.daos.LiveCameraDao
import com.jimmy.datasource.entities.LiveCameraEntity
import com.jimmy.datasource.model.LiveCameraModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.map
import javax.inject.Singleton

typealias LiveCamerasStore = Store<String, LiveCameraModel>

@InstallIn(SingletonComponent::class)
@Module
internal object LiveCamerasModule {

    @Provides
    @Singleton
    fun provideLiveCameraStore(
        remoteDataSource: LiveCamerasDataSource,
        localDataSource: LiveCameraDao,
    ) : LiveCamerasStore = StoreBuilder.from(
        fetcher = Fetcher.of { id: String ->
            val result = runCatching { remoteDataSource.getCameraDetails(id) }
            if (result.isSuccess) {
                return@of result.getOrThrow()
            }

            throw result.exceptionOrNull()!!
        },
//        sourceOfTruth = SourceOfTruth.of(
//            reader = { id ->
//                localDataSource.getCameraWithIdFlow(id).map {
//                    it
//                }
//            },
//            writer = { _, response ->
//                localDataSource.withTransaction {
//                    localDataSource.update(
//                        response
//                    )
//                }
//            },
//            delete = localDataSource::delete,
//            deleteAll = localDataSource::deleteAll
//        )
    ).build()
}
