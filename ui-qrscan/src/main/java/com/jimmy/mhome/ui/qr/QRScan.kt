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

package com.jimmy.mhome.ui.qr

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun QRScan(
    navigateUp: () -> Unit,
) {
    QRScan(
        viewModel = hiltViewModel(),
        navigateUp = navigateUp,
    )
}

@Composable
internal fun QRScan(
    viewModel: QRScanViewModel,
    navigateUp: () -> Unit,
) {

}

@Preview(showBackground = true, backgroundColor = 0xffffff, device = Devices.PIXEL_4)
@Composable
fun PreviewQRScan(){

    QRScan(
        navigateUp = {},
    )
}

@Preview(showBackground = true, backgroundColor = 0xffffff, device = Devices.PIXEL_C)
@Composable
fun PreviewQRScanTablet(){
    QRScan(
        navigateUp = {},
    )
}