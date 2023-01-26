/*
 * Copyright 2017 Google LLC
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

package com.jimmy.mhome.ui.home

import androidx.compose.runtime.Immutable
import com.jimmy.api.UiMessage
import com.jimmy.datasource.AppAuthState

@Immutable
data class HomeViewState(
//    val camera: LiveCameraEntity = LiveCameraEntity.EMPTY_CAMERA,
    val loading: Boolean = false,
    val message: UiMessage? = null,
    val language: String = "en",
    val authState: AppAuthState = AppAuthState.LOGGED_OUT,
    ) {

    val refreshing: Boolean
        get() = loading

    companion object {
        val Empty = HomeViewState()
    }
}
