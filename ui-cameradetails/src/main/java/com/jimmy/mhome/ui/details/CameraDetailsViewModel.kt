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

package com.jimmy.mhome.ui.details

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jimmy.api.UiMessageManager
import com.jimmy.domain.interactors.UpdateLiveCameras
import com.jimmy.domain.observers.ObserveLiveCameras
import com.jimmy.extensions.combine
import com.jimmy.util.ObservableLoadingCounter
import com.jimmy.util.collectStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import com.jimmy.util.Logger
import javax.inject.Inject

@HiltViewModel
internal class CameraDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val updateCameraDetails: UpdateLiveCameras,
    observeLiveCameras: ObserveLiveCameras,
    private val logger: Logger,
) : ViewModel() {

    private val TAG = "CameraDetailsViewModel"

//    private val cameraId: String = savedStateHandle.get("cameraId") ?: ""
    private val cameraId = MutableStateFlow(savedStateHandle.get("cameraId") ?: "")

    private val loadingState = ObservableLoadingCounter()
    private val uiMessageManager = UiMessageManager()

    val state = combine(
        cameraId,
        observeLiveCameras.flow,
        loadingState.observable,
        uiMessageManager.message,
    ) { id, camera, refreshing, message ->
        CameraDetailsViewState(
            cameraId = id,
            camera = camera,
            refreshing = refreshing,
            message = message,
        )
    }.stateIn(
        scope = viewModelScope,
        /**
         * It's about the Activity going into the background (without being destroyed) and the Flow still being active.
         * This puts a time limit of 5 seconds for the Flow to be paused after the last collector stops collecting.
         *
         * */
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CameraDetailsViewState.Empty,
    )

    init {
        Log.i(TAG,"==init cameraId == ${cameraId.value}")
        observeLiveCameras(ObserveLiveCameras.Params(id = cameraId.value))
        refresh(false)
    }

    fun refresh(fromUser: Boolean = true) {
        viewModelScope.launch {
            updateCameraDetails(
                UpdateLiveCameras.Params(cameraId.value, fromUser)
            ).collectStatus(loadingState, logger, uiMessageManager)
        }
    }


    fun clearMessage(id: Long) {
        viewModelScope.launch {
            uiMessageManager.clearMessage(id)
        }
    }

}
