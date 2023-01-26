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

package com.jimmy.mhome.ui.playback.player

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jimmy.api.UiMessageManager
import com.jimmy.extensions.combine
import com.jimmy.util.Logger
import com.jimmy.util.ObservableLoadingCounter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class PlayerViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val logger: Logger,
) : ViewModel() {

    private val TAG = "CameraDetailsViewModel"

    private val cameraId: String = savedStateHandle.get("cameraId")!!
    private val videoId: String = savedStateHandle.get("id")!!

    private val loadingState = ObservableLoadingCounter()
    private val uiMessageManager = UiMessageManager()

    val state = combine(
        loadingState.observable,
        uiMessageManager.message,
    ) { refreshing, message ->
        PlayerViewState(
            videoId = videoId,
            refreshing = refreshing,
            message = message,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = PlayerViewState.Empty,
    )

    init{
        println("-->cameraId<-- $cameraId")
        println("-->videoId<-- $videoId")

    }

    fun refresh(fromUser: Boolean = true) {
//        viewModelScope.launch {
//            updateCameraPlayback(
//                UpdatePlayback.Params()
//            ).collectStatus(loadingState, logger, uiMessageManager)
//        }
    }


    fun clearMessage(id: Long) {
        viewModelScope.launch {
            uiMessageManager.clearMessage(id)
        }
    }
}
