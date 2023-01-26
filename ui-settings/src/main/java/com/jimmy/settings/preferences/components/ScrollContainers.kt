package com.jimmy.settings.preferences.components

import androidx.compose.foundation.MutatePriority
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.unit.dp
import com.jimmy.settings.preferences.addIf
import com.jimmy.settings.preferences.rememberExtendPadding
import kotlinx.coroutines.awaitCancellation

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun PreferenceColumn(
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    scrollState: ScrollState? = rememberScrollState(),
    content: @Composable ColumnScope.() -> Unit
) {
    NestedScrollStretch {
        Column(
            verticalArrangement = verticalArrangement,
            horizontalAlignment = horizontalAlignment,
            modifier = Modifier
                .fillMaxHeight()
                .addIf(scrollState != null) {
                    this
                        .verticalScroll(scrollState!!)
                        .pointerInteropFilter {
                            // return true if scrolling
                            scrollState.isScrollInProgress
                        }
                }
                .padding(rememberExtendPadding(PaddingValues(), bottom = 8.dp)),
            content = content
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun PreferenceLazyColumn(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    state: LazyListState = rememberLazyListState(),
    content: LazyListScope.() -> Unit
) {
    if (!enabled) {
        LaunchedEffect(key1 = null) {
            state.scroll(scrollPriority = MutatePriority.PreventUserInput) {
                awaitCancellation()
            }
        }
    }
    NestedScrollStretch {
        LazyColumn(
            modifier = modifier
                .fillMaxHeight()
                .pointerInteropFilter {
                    // return true if scrolling
                    state.isScrollInProgress
                },
            contentPadding = rememberExtendPadding(PaddingValues(), bottom = 8.dp),
            state = state,
            content = content
        )
    }
}
