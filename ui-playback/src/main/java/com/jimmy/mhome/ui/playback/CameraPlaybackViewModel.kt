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

package com.jimmy.mhome.ui.playback

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.jimmy.api.UiMessageManager
import com.jimmy.datasource.model.ParamsConfig
import com.jimmy.datasource.resultentities.PlaybackEntryWithVideo
import com.jimmy.domain.Const
import com.jimmy.domain.interactors.UpdatePlayback
import com.jimmy.domain.observers.ObservePagedPlayback
import com.jimmy.extensions.combine
import com.jimmy.util.Logger
import com.jimmy.util.ObservableLoadingCounter
import com.jimmy.util.collectStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class CameraPlaybackViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val updateCameraPlayback: UpdatePlayback,
    pagingPlaybackInteractor: ObservePagedPlayback,
    private val logger: Logger,
) : ViewModel() {

    private val TAG = "CameraDetailsViewModel"

    private val cameraId: String = savedStateHandle.get("cameraId") ?: ""

    private val loadingState = ObservableLoadingCounter()

    private val uiMessageManager = UiMessageManager()

    private val paramsConfig = ParamsConfig(
        camId = cameraId,
        startTs = System.currentTimeMillis() - 86400000,
        endTs = System.currentTimeMillis(),
        recordType = Const.NORMAL_RECORD,
    )

    val pagedList: Flow<PagingData<PlaybackEntryWithVideo>> =
        pagingPlaybackInteractor.flow.cachedIn(viewModelScope)

    init {
        pagingPlaybackInteractor(
            ObservePagedPlayback.Params(
                pagingConfig = PAGING_CONFIG,
                paramsConfig = paramsConfig,
            )
        )
    }

    companion object {
        val PAGING_CONFIG = PagingConfig(
            pageSize = 20,
            initialLoadSize = 60,
        )
    }

    val state = combine(
        pagingPlaybackInteractor.flow,
        loadingState.observable,
        uiMessageManager.message,
    ) { camera, refreshing, message ->
        CameraPlaybackViewState(
            refreshing = refreshing,
            message = message,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CameraPlaybackViewState.Empty,
    )


    fun refresh(fromUser: Boolean = true) {
        viewModelScope.launch {
            updateCameraPlayback(
                UpdatePlayback.Params(
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
