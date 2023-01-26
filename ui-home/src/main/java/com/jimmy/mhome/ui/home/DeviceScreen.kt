package com.jimmy.mhome.ui.home

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.paging.compose.collectAsLazyPagingItems
import com.jimmy.common.compose.EntryGrid
import com.jimmy.common.compose.rememberFlowWithLifecycle

@Composable
internal fun DeviceScreen(
    viewModel: HomeViewModel,
    state: HomeViewState,
    refresh: () -> Unit,
    paddingValues: PaddingValues,
    openShowDetails: (id: String) -> Unit,
    navigateUp: () -> Unit,
) {

    EntryGrid(
        lazyPagingItems = rememberFlowWithLifecycle(viewModel.pagedList).collectAsLazyPagingItems(),
        title = stringResource(id = R.string.title_cameras),
        openCameraDetails = openShowDetails,
        onNavigateUp = navigateUp,
        paddingValues = paddingValues,
        refresh = refresh,
    )

}