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

package com.jimmy.datasource.repositories.camera

import com.dropbox.android.external.store4.Fetcher
import com.dropbox.android.external.store4.SourceOfTruth
import com.dropbox.android.external.store4.Store
import com.dropbox.android.external.store4.StoreBuilder
import com.jimmy.datasource.daos.CameraDao
import com.jimmy.datasource.entities.CameraEntity
import com.jimmy.datasource.repositories.cameras.CameraLastRequestStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.map
import org.threeten.bp.Duration
import javax.inject.Singleton

typealias CameraStore = Store<Long, CameraEntity>

@InstallIn(SingletonComponent::class)
@Module
object ShowStoreModule {
    @Provides
    @Singleton
    fun provideShowStore(
        showDao: CameraDao,
        lastRequestStore: CameraLastRequestStore,
    ): CameraStore = StoreBuilder.from(
        fetcher = Fetcher.of { id: Long ->
            showDao.getCameraWithIdOrThrow(id)
        },
        sourceOfTruth = SourceOfTruth.of(
            reader = { showId ->
                showDao.getCameraWithIdFlow(showId).map {
                    when {
                        // If the request is expired, our data is stale
//                        lastRequestStore.isRequestExpired(showId, Duration.ofDays(14)) -> null
                        lastRequestStore.isRequestExpired(Duration.ofDays(14)) -> null
                        // Otherwise, our data is fresh and valid
                        else -> it
                    }
                }
            },
            writer = { id, response ->
                showDao.withTransaction {
                    showDao.insertOrUpdate(
                        response
                    )
                }
            },
            delete = showDao::delete,
            deleteAll = showDao::deleteAll
        )
    ).build()
}
