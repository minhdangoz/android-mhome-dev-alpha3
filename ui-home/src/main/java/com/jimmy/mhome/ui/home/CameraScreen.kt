package com.jimmy.mhome.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.paging.compose.collectAsLazyPagingItems
import com.jimmy.common.compose.EntryGrid
import com.jimmy.common.compose.rememberFlowWithLifecycle

@Composable
internal fun CameraScreen(
    viewModel: HomeViewModel,
    state: HomeViewState,
    refresh: () -> Unit,
    paddingValues: PaddingValues,
    openCameraDetails: (id: String) -> Unit,
    navigateUp: () -> Unit,
) {

//    EntryGrid(
//        lazyPagingItems = rememberFlowWithLifecycle(viewModel.pagedList).collectAsLazyPagingItems(),
//        title = "Camera devices",
//        openCameraDetails = openCameraDetails,
//        onNavigateUp = navigateUp,
//        paddingValues = paddingValues,
//        refresh = refresh,
//    )

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center) {
        Text(text = "Camera list here")
    }

}
