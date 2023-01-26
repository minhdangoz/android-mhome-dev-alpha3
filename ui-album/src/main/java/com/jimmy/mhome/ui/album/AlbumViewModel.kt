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

package com.jimmy.mhome.ui.album

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.jimmy.api.UiMessageManager
import com.jimmy.datasource.model.ParamsConfig
import com.jimmy.datasource.resultentities.AlbumEntryWithImage
import com.jimmy.datasource.resultentities.PlaybackEntryWithVideo
import com.jimmy.domain.interactors.UpdateAlbum
import com.jimmy.domain.interactors.UpdatePlayback
import com.jimmy.domain.observers.ObservePagedAlbum
import com.jimmy.domain.observers.ObservePagedPlayback
import com.jimmy.extensions.combine
import com.jimmy.util.ObservableLoadingCounter
import com.jimmy.util.collectStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import com.jimmy.util.Logger
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
internal class AlbumViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val updateAlbum: UpdateAlbum,
    pagingAlbumInteractor: ObservePagedAlbum,
    private val logger: Logger,
) : ViewModel() {

    private val TAG = "CameraDetailsViewModel"

    private val cameraId: String = savedStateHandle.get("cameraId") ?: ""

    private val loadingState = ObservableLoadingCounter()
    private val uiMessageManager = UiMessageManager()

    val pagedList: Flow<PagingData<AlbumEntryWithImage>> =
        pagingAlbumInteractor.flow.cachedIn(viewModelScope)

    private val paramsConfig = ParamsConfig(
        camId = cameraId,
        startTs = System.currentTimeMillis() - 864000000,
        endTs = System.currentTimeMillis(),
    )

    init {
        pagingAlbumInteractor(ObservePagedAlbum.Params(
            // default get 10 days history
            paramsConfig = paramsConfig,
            pagingConfig = PAGING_CONFIG,
        ))
    }

    companion object {
        val PAGING_CONFIG = PagingConfig(
            pageSize = 10,
            initialLoadSize = 60,
        )
    }

//    val state = combine(
//        pagingAlbumInteractor.flow,
//        loadingState.observable,
//        uiMessageManager.message,
//    ) { album, refreshing, message ->
//        AlbumViewState(
//            album = album,
//            refreshing = refreshing,
//            message = message,
//        )
//    }.stateIn(
//        scope = viewModelScope,
//        started = SharingStarted.WhileSubscribed(5000),
//        initialValue = AlbumViewState.Empty,
//    )


    fun refresh(fromUser: Boolean = true) {
        viewModelScope.launch {
            updateAlbum(
                UpdateAlbum.Params(
                    page = 0,
                    forceRefresh = fromUser,
                    paramsConfig = paramsConfig,
                )
            ).collectStatus(loadingState, logger, uiMessageManager)
        }
    }


    fun clearMessage(id: Long) {
        viewModelScope.launch {
            uiMessageManager.clearMessage(id)
        }
    }
}
