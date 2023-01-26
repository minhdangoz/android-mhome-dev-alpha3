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

package com.jimmy.datasource.repositories.album

import com.dropbox.android.external.store4.Fetcher
import com.dropbox.android.external.store4.SourceOfTruth
import com.dropbox.android.external.store4.Store
import com.dropbox.android.external.store4.StoreBuilder
import com.jimmy.datasource.daos.AlbumDao
import com.jimmy.datasource.daos.AlbumEntryDao
import com.jimmy.datasource.entities.AlbumEntity
import com.jimmy.datasource.entities.AlbumEntry
import com.jimmy.datasource.model.ParamsConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.map
import org.threeten.bp.Duration
import javax.inject.Singleton

typealias AlbumStore = Store<Pair<Int, ParamsConfig>, List<AlbumEntry>>

@InstallIn(SingletonComponent::class)
@Module
internal object AlbumStoreModule {

    @Provides
    @Singleton
    fun provideAlbumStore(
        remoteDataSource: AlbumDataSource,
        localDataSource: AlbumEntryDao,
        allAlbumDao: AlbumDao,
        lastRequestStore: AlbumLastRequestStore
    ) : AlbumStore = StoreBuilder.from<Pair<Int, ParamsConfig>, List<Pair<AlbumEntity, AlbumEntry>>, List<AlbumEntry>>(
        fetcher = Fetcher.of { (page, params) ->
            remoteDataSource(page, 60, params)
                .also {
                    if (page == 0) lastRequestStore.updateLastRequest()
                }
        },
        sourceOfTruth = SourceOfTruth.of(
            reader = { (page, _) ->
                localDataSource.entriesObservable(page).map { entries ->
                    when {
                        // Store only treats null as 'no value', so convert to null
                        // entries.isEmpty() -> null
                        // If the request is expired, our data is stale
                        lastRequestStore.isRequestExpired(Duration.ofHours(3)) -> null
                        // Otherwise, our data is fresh and valid
                        else -> entries
                    }
                }
            },
            writer = { (page, _), response ->
                localDataSource.withTransaction {
                    val entries = response.map { (show, entry) ->
                        entry.copy(showId = allAlbumDao.getIdOrSavePlaceholder(show), page = page)
                    }
                    if (page == 0) {
                        // If we've requested page 0, remove any existing entries first
                        localDataSource.deleteAll()
                        localDataSource.insertAll(entries)
                    } else {
                        localDataSource.updatePage(page, entries)
                    }
                }
            },
        )
    ).build()


}
