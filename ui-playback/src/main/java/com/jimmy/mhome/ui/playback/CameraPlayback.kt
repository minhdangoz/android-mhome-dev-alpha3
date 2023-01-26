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

package com.jimmy.mhome.ui.playback

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.rememberInsetsPaddingValues
import com.google.accompanist.insets.ui.Scaffold
import com.google.accompanist.insets.ui.TopAppBar
import com.jimmy.common.compose.Layout
import com.jimmy.common.compose.bodyWidth
import com.jimmy.common.compose.rememberFlowWithLifecycle
import com.jimmy.common.compose.rememberStateWithLifecycle
import com.jimmy.common.compose.ui.AutoSizedCircularProgressIndicator
import com.jimmy.common.compose.ui.SwipeDismissSnackbarHost

@Composable
fun CameraPlayback(
    navigateUp: () -> Unit,
    openPlayer: (id: String, videoId: String) -> Unit,
) {
    CameraPlayback(
        viewModel = hiltViewModel(),
        navigateUp = navigateUp,
        openPlayer = openPlayer,
    )
}

@Composable
internal fun CameraPlayback(
    viewModel: CameraPlaybackViewModel,
    navigateUp: () -> Unit,
    openPlayer: (id: String, videoId: String) -> Unit,
) {
    val viewState by rememberStateWithLifecycle(viewModel.state)

    CameraPlayback(
        viewModel = viewModel,
        viewState = viewState,
        navigateUp = navigateUp,
        refresh = { viewModel.refresh() },
        onMessageShown = { viewModel.clearMessage(it) },
        openPlayer = openPlayer,
    )
}

@Composable
internal fun CameraPlayback(
    viewModel: CameraPlaybackViewModel,
    viewState: CameraPlaybackViewState,
    navigateUp: () -> Unit,
    refresh: () -> Unit,
    onMessageShown: (id: Long) -> Unit,
    openPlayer: (id: String, videoId: String) -> Unit,
    ) {

    // create grid 3 items each row
    PlaybackGrid(
        lazyPagingItems = rememberFlowWithLifecycle(viewModel.pagedList).collectAsLazyPagingItems(),
        title = stringResource(id = R.string.title_playback),
        onNavigateUp = navigateUp,
        openPlayer = openPlayer,
    )

}

@Preview(showBackground = true, backgroundColor = 0xffffff, device = Devices.PIXEL_4)
@Composable
fun PreviewCameraPlayback(){

    CameraPlayback(
        navigateUp = {},
        openPlayer = {id, url -> }
    )
}

@Preview(showBackground = true, backgroundColor = 0xffffff, device = Devices.PIXEL_C)
@Composable
fun PreviewCameraPlaybackTablet(){
    CameraPlayback(
        navigateUp = {},
        openPlayer = {id, url -> }
    )
}