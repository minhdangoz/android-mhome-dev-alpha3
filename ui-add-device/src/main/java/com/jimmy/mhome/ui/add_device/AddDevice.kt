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

package com.jimmy.mhome.ui.add_device

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.ContentAlpha
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.QrCode
import androidx.compose.material.icons.outlined.QrCodeScanner
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.rememberInsetsPaddingValues
import com.google.accompanist.insets.ui.Scaffold
import com.google.accompanist.insets.ui.TopAppBar
import com.jimmy.common.compose.Layout
import com.jimmy.common.compose.rememberStateWithLifecycle
import com.jimmy.common.compose.ui.RefreshButton
import com.jimmy.common.compose.ui.SwipeDismissSnackbarHost

@Composable
fun AddDevice(
    navigateUp: () -> Unit,
    onScanButtonClick: () -> Unit,
    ) {

    AddDevice(
        viewModel = hiltViewModel(),
        navigateUp = navigateUp,
        onScanButtonClick = onScanButtonClick,
    )
}

@Composable
internal fun AddDevice(
    viewModel: AddDeviceViewModel,
    navigateUp: () -> Unit,
    onScanButtonClick: () -> Unit,

    ) {
    val viewState by rememberStateWithLifecycle(viewModel.state)

    AddDevice(
        viewModel = viewModel,
        state = viewState,
        navigateUp = navigateUp,
        onMessageShown = { viewModel.clearMessage(it) },
        onScanButtonClick = onScanButtonClick,
    )
}

@Composable
internal fun AddDevice(
    viewModel: AddDeviceViewModel,
    state: AddDeviceViewState,
    navigateUp: () -> Unit,
    onScanButtonClick: () -> Unit,
    onMessageShown: (id: Long) -> Unit,
){


    val scaffoldState = rememberScaffoldState()

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
            AddDeviceAppBar(
                refreshing = state.refreshing,
                onScanButtonClick = onScanButtonClick,
                modifier = Modifier.fillMaxWidth(),
                onNavigateUp = navigateUp,
            )
        },
        bottomBar = { BottomSpacer() } ,
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

        Column(modifier = Modifier
            .fillMaxSize()
            .scrollable(
                state = rememberScrollState(),
                orientation = Orientation.Vertical
            )
            .padding(paddingValues)
        ) {

        }

    }
}


@Composable
private fun AddDeviceAppBar(
    refreshing: Boolean,
    onScanButtonClick: () -> Unit,
    modifier: Modifier = Modifier,
    onNavigateUp: () -> Unit,
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
            ScanButton(
                onClick = onScanButtonClick
            )

        }
    )
}


@Composable
private fun ScanButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
    ) {
        Icon(
            imageVector = Icons.Outlined.QrCodeScanner,
            contentDescription = "qr code scan"
        )
    }
}

@Composable
fun BottomSpacer() {
    Box(
        contentAlignment = Alignment.BottomStart
    ) {
        Spacer(modifier = Modifier
            .navigationBarsPadding()
            .imePadding())
        Spacer(
            modifier = Modifier
                .windowInsetsBottomHeight(WindowInsets.navigationBars)
                .fillMaxWidth()
                .background(color = MaterialTheme.colorScheme.background.copy(alpha = 0.9f))
        )
    }
}


@Preview(showBackground = true, backgroundColor = 0xffffff, device = Devices.PIXEL_4)
@Composable
fun PreviewAddDevice(){

    AddDeviceAppBar(
        onScanButtonClick = {},
        refreshing = true,
        onNavigateUp = {},
    )
}

@Preview(showBackground = true, backgroundColor = 0xffffff, device = Devices.PIXEL_C)
@Composable
fun PreviewAddDeviceTablet(){
    AddDeviceAppBar(
        onScanButtonClick = {},
        refreshing = true,
        onNavigateUp = {},
    )
}