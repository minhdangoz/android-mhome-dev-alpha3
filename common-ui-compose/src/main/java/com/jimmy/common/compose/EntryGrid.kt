/*
 * Copyright 2021 Google LLC
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

package com.jimmy.common.compose

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.ContentAlpha
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.rememberInsetsPaddingValues
import com.google.accompanist.insets.ui.Scaffold
import com.google.accompanist.insets.ui.TopAppBar
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshIndicator
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.jimmy.common.compose.theme.AppBarAlphas
import com.jimmy.common.compose.ui.PlaceholderPosterCard
import com.jimmy.common.compose.ui.PosterCard
import com.jimmy.common.compose.ui.RefreshButton
import com.jimmy.common.compose.ui.SwipeDismissSnackbarHost
import com.jimmy.datasource.Entry
import com.jimmy.datasource.resultentities.EntryWithCamera

@Composable
fun <E : Entry> EntryGrid(
    lazyPagingItems: LazyPagingItems<out EntryWithCamera<E>>,
    title: String,
    onNavigateUp: () -> Unit,
    openCameraDetails: (String) -> Unit,
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues,
    refresh: () -> Unit,
) {
    val scaffoldState = rememberScaffoldState()

    lazyPagingItems.loadState.prependErrorOrNull()?.let { message ->
        LaunchedEffect(message) {
            scaffoldState.snackbarHostState.showSnackbar(message.message)
        }
    }
    lazyPagingItems.loadState.appendErrorOrNull()?.let { message ->
        LaunchedEffect(message) {
            scaffoldState.snackbarHostState.showSnackbar(message.message)
        }
    }
    lazyPagingItems.loadState.refreshErrorOrNull()?.let { message ->
        LaunchedEffect(message) {
            scaffoldState.snackbarHostState.showSnackbar(message.message)
        }
    }


    Scaffold(
        scaffoldState = scaffoldState,
//        topBar = {
//            EntryGridAppBar(
//                title = title,
//                onNavigateUp = onNavigateUp,
//                refreshing = lazyPagingItems.loadState.refresh == LoadState.Loading,
//                onRefreshActionClick = { lazyPagingItems.refresh() },
//                modifier = Modifier.fillMaxWidth()
//            )
//        },
        snackbarHost = { snackHostState ->
            SwipeDismissSnackbarHost(
                hostState = snackHostState,
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
            )
        },
        modifier = modifier.navigationBarsPadding(),
        backgroundColor = MaterialTheme.colorScheme.surface,
    ) { paddingValues ->
        SwipeRefresh(
            state = rememberSwipeRefreshState(
                isRefreshing = lazyPagingItems.loadState.refresh == LoadState.Loading
            ),
            onRefresh = { lazyPagingItems.refresh() },
//            indicatorPadding = paddingValues,
            indicator = { state, trigger ->
                SwipeRefreshIndicator(
                    state = state,
                    refreshTriggerDistance = trigger,
                    scale = true
                )
            }
        ) {
            val columns = Layout.columns
            val bodyMargin = Layout.bodyMargin
            val gutter = Layout.gutter


            LazyColumn(
//                contentPadding = paddingValues,
                modifier = Modifier
                    .fillMaxSize()
            ) {
                itemsInGrid(
                    lazyPagingItems = lazyPagingItems,
                    columns = columns / 2,
                    contentPadding = PaddingValues(horizontal = bodyMargin, vertical = gutter),
                    verticalItemPadding = gutter,
                    horizontalItemPadding = gutter,
                ) { entry ->
                    val mod = Modifier
                        .aspectRatio(4.5f / 3f)
                        .fillMaxWidth()
                    if (entry != null) {
                        PosterCard(
                            show = entry.camera,
                            onClick = { entry.camera.id?.let { openCameraDetails(it) } },
                            modifier = mod
                        )
                    } else {
                        PlaceholderPosterCard(mod)
                    }
                }

                if (lazyPagingItems.loadState.append == LoadState.Loading) {
                    item {
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .padding(24.dp)
                        ) {
                            CircularProgressIndicator(Modifier.align(Alignment.Center))
                        }
                    }
                }
            }

            if(lazyPagingItems.itemCount <= 0){
                EmptyCameraView()
            }
        }
    }
}

@Composable
fun EmptyCameraView(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally) {
        Image(
            painter = painterResource(
            id = R.drawable.ic_load_empty),
            contentDescription = "empty",
            modifier = modifier.size(64.dp)
        )
        Text(
            text = stringResource(id = R.string.empty_camera),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
private fun EntryGridAppBar(
    title: String,
    refreshing: Boolean,
    onNavigateUp: () -> Unit,
    onRefreshActionClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TopAppBar(
        navigationIcon = {
            IconButton(onClick = onNavigateUp) {
                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = stringResource(R.string.cd_navigate_up)
                )
            }
        },
        backgroundColor = MaterialTheme.colorScheme.surface.copy(
            alpha = AppBarAlphas.translucentBarAlpha()
        ),
        contentColor = MaterialTheme.colorScheme.onSurface,
        contentPadding = rememberInsetsPaddingValues(LocalWindowInsets.current.statusBars),
        modifier = modifier,
        title = { Text(text = title) },
        actions = {
            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                // This button refresh allows screen-readers, etc to trigger a refresh.
                // We only show the button to trigger a refresh, not to indicate that
                // we're currently refreshing, otherwise we have 4 indicators showing the
                // same thing.
                Crossfade(
                    targetState = refreshing,
                    modifier = Modifier.align(Alignment.CenterVertically)
                ) { isRefreshing ->
                    if (!isRefreshing) {
                        RefreshButton(onClick = onRefreshActionClick)
                    }
                }
            }
        },
    )
}
