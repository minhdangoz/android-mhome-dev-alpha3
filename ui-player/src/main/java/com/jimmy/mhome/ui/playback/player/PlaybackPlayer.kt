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

package com.jimmy.mhome.ui.playback.player

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.LifecycleOwner
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.rememberInsetsPaddingValues
import com.google.accompanist.insets.ui.Scaffold
import com.google.accompanist.insets.ui.TopAppBar
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.google.android.exoplayer2.upstream.DefaultDataSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.upstream.DefaultLoadErrorHandlingPolicy
import com.google.android.exoplayer2.upstream.HttpDataSource
import com.google.android.exoplayer2.upstream.LoadErrorHandlingPolicy
import com.google.android.exoplayer2.upstream.LoadErrorHandlingPolicy.LoadErrorInfo
import com.jimmy.common.compose.Layout
import com.jimmy.common.compose.bodyWidth
import com.jimmy.common.compose.rememberStateWithLifecycle
import com.jimmy.common.compose.ui.AutoSizedCircularProgressIndicator
import com.jimmy.common.compose.ui.SwipeDismissSnackbarHost
import com.jimmy.common.compose.ui.iconButtonBackgroundScrim

@Composable
fun PlaybackPlayer(
    navigateUp: () -> Unit,
) {
    PlaybackPlayer(
        viewModel = hiltViewModel(),
        navigateUp = navigateUp,
    )
}

@Composable
internal fun PlaybackPlayer(
    viewModel: PlayerViewModel,
    navigateUp: () -> Unit,
) {
    val viewState by rememberStateWithLifecycle(viewModel.state)

    PlaybackPlayer(
        viewModel = viewModel,
        viewState = viewState,
        navigateUp = navigateUp,
        refresh = { viewModel.refresh() },
        onMessageShown = { viewModel.clearMessage(it) },
    )
}

@Composable
internal fun PlaybackPlayer(
    viewModel: PlayerViewModel,
    viewState: PlayerViewState,
    navigateUp: () -> Unit,
    refresh: () -> Unit,
    onMessageShown: (id: Long) -> Unit,
) {
    val scaffoldState = rememberScaffoldState()
    val listState = rememberLazyListState()

    viewState.message?.let { message ->
        LaunchedEffect(message) {
            scaffoldState.snackbarHostState.showSnackbar(message.message)
            // Notify the view model that the message has been dismissed
            onMessageShown(message.id)
        }
    }


    Scaffold(
        modifier = Modifier.navigationBarsPadding(),
        topBar = {
            var appBarHeight by remember { mutableStateOf(0) }
            val showAppBarBackground by remember {
                derivedStateOf {
                    val visibleItemsInfo = listState.layoutInfo.visibleItemsInfo
                    when {
                        visibleItemsInfo.isEmpty() -> false
                        appBarHeight <= 0 -> false
                        else -> {
                            val firstVisibleItem = visibleItemsInfo[0]
                            when {
                                // If the first visible item is > 0, we want to show the app bar background
                                firstVisibleItem.index > 0 -> true
                                // If the first item is visible, only show the app bar background once the only
                                // remaining part of the item is <= the app bar
                                else -> firstVisibleItem.size + firstVisibleItem.offset <= appBarHeight
                            }
                        }
                    }
                }
            }

            PlaybackPlayerAppBar(
                title = "Playback",
                isRefreshing = viewState.refreshing,
                showAppBarBackground = showAppBarBackground,
                navigateUp = navigateUp,
                refresh = refresh,
                modifier = Modifier
                    .fillMaxWidth()
                    .onSizeChanged { appBarHeight = it.height }
            )
        },
        snackbarHost = { snackBarHostState ->
            SwipeDismissSnackbarHost(
                hostState = snackBarHostState,
                modifier = Modifier
                    .padding(horizontal = Layout.bodyMargin)
                    .fillMaxWidth()
            )
        }
    ) { contentPadding ->

        Surface(modifier = Modifier.bodyWidth()) {
            PlaybackPlayerScrollingContent(
                state = viewState,
                listState = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = contentPadding,
            )
        }
    }
}

@Composable
private fun PlaybackPlayerScrollingContent(
    state: PlayerViewState,
    listState: LazyListState,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
) {

    val context = LocalContext.current
    val lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current

    var autoPlay by remember { mutableStateOf(true) }
    var window by remember { mutableStateOf(0) }
    var position by remember { mutableStateOf(0L) }

    val mPlayer = remember {
        val player = ExoPlayer.Builder(context)
            .build()

        player.playWhenReady = autoPlay
        player.seekTo(window, position)
        player
    }

    fun updateState() {
        autoPlay = mPlayer.playWhenReady
        window = mPlayer.currentMediaItemIndex
        position = 0L.coerceAtLeast(mPlayer.contentPosition)
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.surface,
//        horizontalAlignment = Alignment.CenterHorizontally,
//        verticalArrangement = Arrangement.Center,
    ) {

        // Implementing ExoPlayer
        AndroidView(factory = { context ->
            StyledPlayerView(context).apply {
                player = mPlayer
                controllerShowTimeoutMs = 2000
                controllerAutoShow = true
                resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
            }
        })

        if(state.videoId != null && state.videoId.isNotEmpty()){

            val videoUrl = "https://vod.mcloudcam.vn/api/videoHistory/streaming/${state.videoId}"
            println("--> play video: $videoUrl")

            val httpDataSourceFactory: HttpDataSource.Factory =
                DefaultHttpDataSource.Factory()
                    .setAllowCrossProtocolRedirects(true)

            val dataSourceFactory = DefaultDataSource.Factory(context, httpDataSourceFactory)


//            val cacheDataSourceFactory: DataSource.Factory = CacheDataSource.Factory()
//                .setCache(cache)
//                .setUpstreamDataSourceFactory(httpDataSourceFactory)

            val loadErrorHandlingPolicy: LoadErrorHandlingPolicy =
                object : DefaultLoadErrorHandlingPolicy() {
                    override fun getRetryDelayMsFor(loadErrorInfo: LoadErrorInfo): Long {
                        return 1000L
                    }
                }

            val mediaSource: MediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
                .setLoadErrorHandlingPolicy(loadErrorHandlingPolicy)
                .createMediaSource(MediaItem.fromUri(videoUrl))

            mPlayer.addListener(object : Player.Listener{
                override fun onPlayerError(error: PlaybackException) {
                    super.onPlayerError(error)

                    println("--> onPlayerError <-- ${error.printStackTrace()}")
                }
            })

            mPlayer.setMediaSource(mediaSource)
            mPlayer.prepare()
            mPlayer.play()
        }

    }

}



@Composable
private fun PlaybackPlayerAppBar(
    title: String,
    isRefreshing: Boolean,
    showAppBarBackground: Boolean,
    navigateUp: () -> Unit,
    refresh: () -> Unit,
    modifier: Modifier = Modifier,
) {

    val backgroundColor by animateColorAsState(
        targetValue = when {
            showAppBarBackground -> MaterialTheme.colorScheme.surface
            else -> Color.Transparent
        },
        animationSpec = spring(),
    )

    val elevation by animateDpAsState(
        targetValue = when {
            showAppBarBackground -> 4.dp
            else -> 0.dp
        },
        animationSpec = spring(),
    )

    TopAppBar(
        title = {
            Crossfade(showAppBarBackground) { show ->
                if (show) Text(text = title)
            }
        },
        contentPadding = rememberInsetsPaddingValues(
            LocalWindowInsets.current.systemBars,
            applyBottom = false
        ),
        navigationIcon = {
            IconButton(
                onClick = navigateUp,
                modifier = Modifier.iconButtonBackgroundScrim(enabled = !showAppBarBackground),
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = stringResource(R.string.cd_navigate_up),
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        },
        actions = {
            if (isRefreshing) {
                AutoSizedCircularProgressIndicator(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .fillMaxHeight()
                        .padding(16.dp)
                )
            } else {
                IconButton(
                    onClick = refresh,
                    modifier = Modifier.iconButtonBackgroundScrim(enabled = !showAppBarBackground),
                ) {
                    Icon(
                        tint = MaterialTheme.colorScheme.onPrimary,
                        imageVector = Icons.Default.Refresh,
                        contentDescription = stringResource(R.string.cd_refresh)
                    )
                }
            }
        },
        elevation = elevation,
        backgroundColor = backgroundColor,
        modifier = modifier
    )
}



@Preview(showBackground = true, backgroundColor = 0xffffff, device = Devices.PIXEL_4)
@Composable
fun previewPlaybackPlayer(){

    PlaybackPlayer(
        navigateUp = {},
    )
}

@Preview(showBackground = true, backgroundColor = 0xffffff, device = Devices.PIXEL_C)
@Composable
fun previewPlaybackPlayerTablet(){
    PlaybackPlayer(
        navigateUp = {},
    )
}