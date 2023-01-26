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

package com.jimmy.mhome.ui.album

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import com.jimmy.common.compose.rememberFlowWithLifecycle
import com.jimmy.common.compose.rememberStateWithLifecycle

@Composable
fun Album(
    navigateUp: () -> Unit,
    openImage: (id: String, imageId: String) -> Unit,
) {
    Album(
        viewModel = hiltViewModel(),
        navigateUp = navigateUp,
        openPlayer = openImage,
    )
}

@Composable
internal fun Album(
    viewModel: AlbumViewModel,
    navigateUp: () -> Unit,
    openPlayer: (id: String, imageId: String) -> Unit,
) {

    AlbumGrid(
        lazyPagingItems = rememberFlowWithLifecycle(viewModel.pagedList).collectAsLazyPagingItems(),
        title = "Album",
        onNavigateUp = navigateUp,
        openImage = openPlayer,
    )
}

@Preview(showBackground = true, backgroundColor = 0xffffff, device = Devices.PIXEL_4)
@Composable
fun PreviewAlbum(){

    Album(
        navigateUp = {},
        openImage = {id, url -> }
    )
}

@Preview(showBackground = true, backgroundColor = 0xffffff, device = Devices.PIXEL_C)
@Composable
fun PreviewAlbumTablet(){
    Album(
        navigateUp = {},
        openImage = {id, url -> }
    )
}