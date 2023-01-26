package com.jimmy.mhome.ui.home

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

typealias ComposableFun = @Composable (
    viewModel: HomeViewModel,
    state: HomeViewState,
    refresh: () -> Unit,
    paddingValues: PaddingValues,
    openCameraDetails: (deviceId: String) -> Unit,
) -> Unit

sealed class TabItem(
    var title: Int,
    var screen: ComposableFun,
) {
    object Device : TabItem(
        title = R.string.title_devices,
        {viewModel, state, refresh, paddingValues, openDetails -> DeviceScreen(
            viewModel = viewModel,
            state = state,
            refresh = refresh,
            paddingValues = paddingValues,
            navigateUp = {},
            openShowDetails = {id -> openDetails(id)}

        ) })

    object Camera : TabItem(R.string.title_cameras, {
            viewModel,
            state,
            refresh,
            paddingValues,
            openDetails -> CameraScreen(

        viewModel = viewModel,
        state = state,
        refresh = refresh,
        paddingValues = paddingValues,
        navigateUp = {},
        openCameraDetails = {id -> openDetails(id)}
    ) })
}