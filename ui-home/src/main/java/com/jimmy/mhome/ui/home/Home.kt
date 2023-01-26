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

package com.jimmy.mhome.ui.home

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ContentAlpha
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.ScrollableTabRow
import androidx.compose.material.Tab
import androidx.compose.material.TabPosition
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.rememberInsetsPaddingValues
import com.google.accompanist.insets.ui.Scaffold
import com.google.accompanist.insets.ui.TopAppBar
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import com.jimmy.common.compose.Layout
import com.jimmy.common.compose.OverflowMenu
import com.jimmy.common.compose.rememberStateWithLifecycle
import com.jimmy.common.compose.theme.AppBarAlphas
import com.jimmy.common.compose.ui.RefreshButton
import com.jimmy.common.compose.ui.SwipeDismissSnackbarHost
import kotlinx.coroutines.flow.collect

@Composable
fun Home(
    openCameraDetails: (deviceId: String) -> Unit,
    onAddButtonClick: () -> Unit,
    onScanButtonClick: () -> Unit,
) {
    Home(
        viewModel = hiltViewModel(),
        openCameraDetails = openCameraDetails,
        onAddButtonClick = onAddButtonClick,
        onScanButtonClick = onScanButtonClick,
    )
}

@Composable
internal fun Home(
    viewModel: HomeViewModel,
    openCameraDetails: (deviceId: String) -> Unit,
    onAddButtonClick: () -> Unit,
    onScanButtonClick: () -> Unit,
) {
    val viewState by rememberStateWithLifecycle(viewModel.state)

    Home(
        viewModel = viewModel,
        state = viewState,
        refresh = { viewModel.refresh(true) },
        openCameraDetails = openCameraDetails,
        onMessageShown = { viewModel.clearMessage(it) },
        onAddButtonClick = onAddButtonClick,
        onScanButtonClick = onScanButtonClick,
    )
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun Home(
    viewModel: HomeViewModel,
    state: HomeViewState,
    refresh: () -> Unit,
    openCameraDetails: (deviceId: String) -> Unit,
    onMessageShown: (id: Long) -> Unit,
    onAddButtonClick: () -> Unit,
    onScanButtonClick: () -> Unit,
) {

    val scaffoldState = rememberScaffoldState()

    val tabs = listOf(TabItem.Device, TabItem.Camera)

    val pagerState = rememberPagerState()

    state.message?.let { message ->
        LaunchedEffect(message) {
            scaffoldState.snackbarHostState.showSnackbar(message.message)
            // Notify the view model that the message has been dismissed
            onMessageShown(message.id)
        }
    }

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            HomeAppBar(
                refreshing = state.refreshing,
                onRefreshActionClick = refresh,
                modifier = Modifier.fillMaxWidth(),
                onAddButtonClick = onAddButtonClick,
                onScanButtonClick = onScanButtonClick,
            )
        },
        snackbarHost = { snackBarHostState ->
            SwipeDismissSnackbarHost(
                hostState = snackBarHostState,
                modifier = Modifier
                    .padding(horizontal = Layout.bodyMargin)
                    .fillMaxWidth()
                    .navigationBarsPadding()
            )
        },
        modifier = Modifier.fillMaxSize(),

        ) { paddingValues ->

        // tabs

        val selectedTabIndex = remember { mutableStateOf(pagerState.currentPage) }

        LaunchedEffect(selectedTabIndex.value) {
            pagerState.animateScrollToPage(selectedTabIndex.value)
        }

        LaunchedEffect(pagerState) {
            snapshotFlow { pagerState.currentPage }.collect { page ->
                selectedTabIndex.value = if (page == 0) {
                    0
                } else {
                    1
                }
            }
        }


        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .navigationBarsPadding()
        ) {
            Tabs(
                tabs = tabs,
                pagerState = pagerState,
                selectedTab = selectedTabIndex.value,
                onSelectedLanguageChanged = {
                    selectedTabIndex.value = it
                })
            TabsContent(
                viewModel = viewModel,
                tabs = tabs,
                pagerState = pagerState,
                state = state,
                paddingValues = paddingValues,
                refresh = refresh,
                openCameraDetails = openCameraDetails,
            )
        }

    }
}

@Composable
private fun HomeAppBar(
    refreshing: Boolean,
    onRefreshActionClick: () -> Unit,
    onAddButtonClick: () -> Unit,
    onScanButtonClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        backgroundColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
        contentPadding = rememberInsetsPaddingValues(
            insets = LocalWindowInsets.current.systemBars,
            applyBottom = false,
        ),
        modifier = modifier,
        title = { Text(stringResource(id = R.string.screen_home)) },
        elevation = 0.dp,
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

//            PlusButton(
//                onClick = onAddButonClick,
//                modifier = Modifier.align(Alignment.CenterVertically)
//            )

            OverflowMenu(
                imageVector = Icons.Outlined.MoreVert,
                block = {
                    DropdownMenuItem(
                        onClick = {
                            hideMenu()
                            onAddButtonClick()
                        },
                    ) {
                        Text(text = stringResource(id = R.string.add_device))
                    }

                    DropdownMenuItem(
                        onClick = {
                            hideMenu()
                            onScanButtonClick()
                        }
                    ) {
                        Text(text = stringResource(id = R.string.scan))
                    }
                }
            )

        }
    )
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun Tabs(
    pagerState: PagerState,
    tabs: List<TabItem>,
    selectedTab: Int,
    onSelectedLanguageChanged: (tab: Int) -> Unit
) {
    // OR ScrollableTabRow()
    ScrollableTabRow(
        // Our selected tab is our current page
        divider = {},
        selectedTabIndex = selectedTab,
        backgroundColor = MaterialTheme.colorScheme.surface.copy(alpha = AppBarAlphas.translucentBarAlpha()),
        contentColor = MaterialTheme.colorScheme.onSurface,
        // Override the indicator, using the provided pagerTabIndicatorOffset modifier
        indicator = { tabPositions: List<TabPosition> ->
            Box {}
        },
        edgePadding = 12.dp,
    ) {
        // Add tabs for all of our pages
        tabs.forEachIndexed { index, tab ->
            // OR Tab()

            Tab(
                modifier = if (selectedTab == index)
                    Modifier
                        .clip(RoundedCornerShape(50))
                        .background(
                            MaterialTheme.colorScheme.primary
                        )
                        .wrapContentSize()
                        .heightIn(15.dp, 25.dp)
//                        .padding(horizontal = 8.dp)
                else Modifier
                    .clip(RoundedCornerShape(50))
                    .background(
                        MaterialTheme.colorScheme.surface
                    )
                    .wrapContentSize()
                    .heightIn(15.dp, 25.dp),
//                    .padding(horizontal = 8.dp)
                text = {
                    Text(
                        text = stringResource(id = tab.title),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal,
                        color = if (selectedTab == index)
                            MaterialTheme.colorScheme.onPrimary
                        else
                            MaterialTheme.colorScheme.onSurface
                    )
                },
                selected = selectedTab == index,
                onClick = {
                    onSelectedLanguageChanged(index)
                },
//                selectedContentColor = MaterialTheme.colorScheme.onSurface,
//                unselectedContentColor = Color(0xFF575757)
            )
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun TabsContent(
    viewModel: HomeViewModel,
    tabs: List<TabItem>,
    pagerState: PagerState,
    state: HomeViewState,
    refresh: () -> Unit,
    paddingValues: PaddingValues,
    openCameraDetails: (deviceId: String) -> Unit,
) {
    HorizontalPager(
        state = pagerState,
        count = tabs.size,
    ) { page ->
        tabs[page].screen(
            viewModel = viewModel,
            state = state,
            refresh = refresh,
            paddingValues = paddingValues,
            openCameraDetails = openCameraDetails,
        )
    }
}

@Preview
@Composable
private fun PreviewHome() {
    Home(
        openCameraDetails = {
        },
        onScanButtonClick = {},
        onAddButtonClick = {},
    )
}

@Preview
@Composable
private fun PreviewHomeAppBar() {
    HomeAppBar(
        refreshing = false,
        onRefreshActionClick = {},
        onAddButtonClick = {},
        onScanButtonClick = {},
    )
}