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

package com.jimmy.mhome.ui.add_device

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
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
internal class AddDeviceViewModel @Inject constructor(
    private val logger: Logger,
) : ViewModel() {

    private val TAG = "CameraDetailsViewModel"
    private val uiMessageManager = UiMessageManager()

    private val loadingState = ObservableLoadingCounter()

    val state: StateFlow<AddDeviceViewState> = kotlinx.coroutines.flow.combine(
        loadingState.observable,
        uiMessageManager.message,
    ) { loading, message ->
        AddDeviceViewState(
            refreshing = loading,
            message = message,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AddDeviceViewState.Empty,
    )


    fun refresh(fromUser: Boolean = true) {
    }


    fun clearMessage(id: Long) {
        viewModelScope.launch {
            uiMessageManager.clearMessage(id)
        }
    }
}
