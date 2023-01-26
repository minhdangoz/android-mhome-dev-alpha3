/*
 * Copyright 2021, StarLauncher
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

package com.jimmy.settings.preferences

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.LocalWindowInsets
import kotlinx.coroutines.launch
import kotlin.math.max
import androidx.compose.material.MaterialTheme as Material3Theme

internal val LocalBottomSheetHandler = staticCompositionLocalOf { BottomSheetHandler() }

val bottomSheetHandler: BottomSheetHandler
    @Composable
    @ReadOnlyComposable
    get() = LocalBottomSheetHandler.current

@ExperimentalMaterialApi
@Composable
fun ProvideBottomSheetHandler(
    content: @Composable () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val bottomSheetState = remember { ModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden) }
    var bottomSheetContent by remember { mutableStateOf(emptyBottomSheetContent) }
    val bottomSheetHandler = remember {
        BottomSheetHandler(
            show = { sheetContent ->
                bottomSheetContent = BottomSheetContent(content = sheetContent)
                coroutineScope.launch { bottomSheetState.show() }
            },
            hide = {
                coroutineScope.launch { bottomSheetState.hide() }
            }
        )
    }

    ModalBottomSheetLayout(
        sheetContent = {
            val isSheetShown = bottomSheetState.isAnimationRunning || bottomSheetState.isVisible
            BackHandler(enabled = isSheetShown) {
                bottomSheetHandler.hide()
            }
            CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurface) {
                StatusBarOffset {
                    bottomSheetContent.content()
                }
            }
        },
        sheetState = bottomSheetState,
        sheetShape = Material3Theme.shapes.large.copy(
            bottomStart = CornerSize(0.dp),
            bottomEnd = CornerSize(0.dp),
        ),
    ) {
        CompositionLocalProvider(LocalBottomSheetHandler provides bottomSheetHandler) {
            content()
        }
    }
}

data class BottomSheetHandler(
    val show: (@Composable () -> Unit) -> Unit = {},
    val hide: () -> Unit = {}
)

@Composable
fun StatusBarOffset(content: @Composable () -> Unit) {
    val windowInsets = LocalWindowInsets.current
    val statusBarHeight = max(windowInsets.statusBars.top, windowInsets.displayCutout.top)
    val topOffset = statusBarHeight + with(LocalDensity.current) { 8.dp.roundToPx() }

    Box(
        modifier = Modifier
            .layout { measurable, constraints ->
                val newConstraints = Constraints(
                    minWidth = constraints.minWidth,
                    maxWidth = constraints.maxWidth,
                    minHeight = constraints.minHeight,
                    maxHeight = when (constraints.maxHeight) {
                        Constraints.Infinity -> Constraints.Infinity
                        else -> constraints.maxHeight - topOffset
                    }
                )
                val placeable = measurable.measure(newConstraints)

                layout(placeable.width, placeable.height) {
                    placeable.placeRelative(0, 0)
                }
            }
    ) {
        content()
    }
}
