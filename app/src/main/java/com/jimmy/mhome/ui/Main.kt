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

package com.jimmy.mhome.ui

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.navigationBarsHeight
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.rememberInsetsPaddingValues
import com.google.accompanist.insets.ui.BottomNavigation
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.jimmy.common.compose.theme.AppBarAlphas
import com.jimmy.mhome.R

@OptIn(
    ExperimentalAnimationApi::class, ExperimentalMaterialApi::class,
    ExperimentalMaterial3Api::class
)
@Composable
internal fun Main(
    authState: Boolean
) {
    val navController = rememberAnimatedNavController()

    // Launch an effect to track changes to the current back stack entry, and push them
    // as a screen views to analytics
    LaunchedEffect(navController) {
        navController.currentBackStackEntryFlow.collect { entry ->
        }
    }

    val configuration = LocalConfiguration.current

    val useBottomNavigation by remember {
        derivedStateOf { configuration.smallestScreenWidthDp < 600 }
    }

    // Subscribe to navBackStackEntry, required to get current route
    val navBackStackEntry by navController.currentBackStackEntryAsState()

    val bottomBarState = rememberSaveable { (mutableStateOf(true)) }

    when (navBackStackEntry?.destination?.route) {

        Screen.Login.route -> {
            // Hide BottomBar
            bottomBarState.value = false
        }

        LeafScreen.MainLogin.createRoute(Screen.Login) -> {
            // Hide BottomBar
            bottomBarState.value = false
        }

        LeafScreen.Login.createRoute(Screen.Login) -> {
            // Hide BottomBar
            bottomBarState.value = false
        }

        LeafScreen.Terms.createRoute(Screen.Login) -> {
            // Hide BottomBar
            bottomBarState.value = false
        }

        LeafScreen.Register.createRoute(Screen.Login) -> {
            // Hide BottomBar
            bottomBarState.value = false
        }

        LeafScreen.RegisterOtp.createRoute(Screen.Login) -> {
            // Hide BottomBar
            bottomBarState.value = false
        }

        LeafScreen.RegisterConfirmPassword.createRoute(Screen.Login) -> {
            // Hide BottomBar
            bottomBarState.value = false
        }

        LeafScreen.ForgotPassword.createRoute(Screen.Login) -> {
            // Hide BottomBar
            bottomBarState.value = false
        }

        LeafScreen.ForgotPasswordConfirm.createRoute(Screen.Login) -> {
            // Hide BottomBar
            bottomBarState.value = false
        }

        LeafScreen.CameraDetails.createRoute(Screen.Home) -> {
            // Hide BottomBar
            bottomBarState.value = false
        }

        LeafScreen.Player.createRoute(Screen.Home) -> {
            // Hide BottomBar
            bottomBarState.value = false
        }

        LeafScreen.ImageDetails.createRoute(Screen.Home) -> {
            // Hide BottomBar
            bottomBarState.value = false
        }

        LeafScreen.CameraAlbum.createRoute(Screen.Home) -> {
            // Hide BottomBar
            bottomBarState.value = false
        }
        LeafScreen.CameraPlayback.createRoute(Screen.Home) -> {
            // Hide BottomBar
            bottomBarState.value = false
        }

        else -> {
            bottomBarState.value = true
        }
    }

    Scaffold(
        bottomBar = {
            if (useBottomNavigation) {
                val currentSelectedItem by navController.currentScreenAsState()
                HomeBottomNavigation(
                    bottomBarState = bottomBarState,
                    selectedNavigation = currentSelectedItem,
                    onNavigationSelected = { selected ->
                        navController.navigate(selected.route) {
                            launchSingleTop = true
                            restoreState = true

                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                Spacer(
                    Modifier
                        .navigationBarsHeight()
                        .fillMaxWidth()
                )
            }
        }
    ) { paddingValues ->
        Row(Modifier.fillMaxSize()) {
            if (!useBottomNavigation) {
                val currentSelectedItem by navController.currentScreenAsState()

                HomeNavigationRail(

                    selectedNavigation = currentSelectedItem,
                    onNavigationSelected = { selected ->
                        navController.navigate(selected.route) {
                            launchSingleTop = true
                            restoreState = true

                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxHeight()
                        .background(MaterialTheme.colorScheme.surface.copy(alpha = AppBarAlphas.translucentBarAlpha())),
                    bottomBarState = bottomBarState,
                )

                Divider(
                    Modifier
                        .fillMaxHeight()
                        .width(1.dp),
                    color = MaterialTheme.colorScheme.inverseOnSurface
                )
            }


            AppNavigation(
                navController = navController,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                authState = authState
            )
        }
    }
}

/**
 * Adds an [NavController.OnDestinationChangedListener] to this [NavController] and updates the
 * returned [State] which is updated as the destination changes.
 */
@Stable
@Composable
private fun NavController.currentScreenAsState(): State<Screen> {
    val selectedItem = remember { mutableStateOf<Screen>(Screen.Home) }

    DisposableEffect(this) {
        val listener = NavController.OnDestinationChangedListener { _, destination, _ ->
            when {
                destination.hierarchy.any { it.route == Screen.Home.route } -> {
                    selectedItem.value = Screen.Home
                }
                destination.hierarchy.any { it.route == Screen.News.route } -> {
                    selectedItem.value = Screen.News
                }
                destination.hierarchy.any { it.route == Screen.Profile.route } -> {
                    selectedItem.value = Screen.Profile
                }
                destination.hierarchy.any { it.route == Screen.Store.route } -> {
                    selectedItem.value = Screen.Store
                }
            }
        }
        addOnDestinationChangedListener(listener)

        onDispose {
            removeOnDestinationChangedListener(listener)
        }
    }

    return selectedItem
}

@Composable
internal fun HomeBottomNavigation(
    bottomBarState: MutableState<Boolean>,
    selectedNavigation: Screen,
    onNavigationSelected: (Screen) -> Unit,
    modifier: Modifier = Modifier,
) {

    AnimatedVisibility(
        visible = bottomBarState.value,
        enter = slideInVertically(initialOffsetY = { it }),
        exit = slideOutVertically(targetOffsetY = { it }),
        content = {

            NavigationBar(
//                containerColor = MaterialTheme.colorScheme.surface,
//                contentColor = contentColorFor(MaterialTheme.colorScheme.onSurface),
//                contentPadding = rememberInsetsPaddingValues(LocalWindowInsets.current.navigationBars),
                tonalElevation = 0.dp,
                modifier = modifier.padding(
                    rememberInsetsPaddingValues(
                        LocalWindowInsets.current.navigationBars,
                        applyEnd = false
                    )
                ),
            ) {
                HomeNavigationItems.forEach { item ->
                    NavigationBarItem(

                        alwaysShowLabel = false,
                        icon = {
                            HomeNavigationItemIcon(
                                item = item,
                                selected = selectedNavigation == item.screen
                            )
                        },
                        label = {
                            Text(
                                text = stringResource(item.labelResId),
                                fontSize = 12.sp,
                            )
                        },
                        selected = selectedNavigation == item.screen,
                        onClick = { onNavigationSelected(item.screen) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurface,
                            indicatorColor = MaterialTheme.colorScheme.surface.copy(alpha = AppBarAlphas.translucentBarAlpha()),
                        )
                    )
                }
            }
//            BottomNavigation(
//                backgroundColor = MaterialTheme.colorScheme.surface.copy(alpha = AppBarAlphas.translucentBarAlpha()),
//                contentColor = contentColorFor(MaterialTheme.colorScheme.onSurface),
//                contentPadding = rememberInsetsPaddingValues(LocalWindowInsets.current.navigationBars),
//                modifier = modifier
//            ) {
//                HomeNavigationItems.forEach { item ->
//                    BottomNavigationItem(
//                        selectedContentColor = MaterialTheme.colorScheme.primary,
//                        alwaysShowLabel = false,
//                        icon = {
//                            HomeNavigationItemIcon(
//                                item = item,
//                                selected = selectedNavigation == item.screen
//                            )
//                        },
//                        label = {
//                            Text(
//                                text = stringResource(item.labelResId),
//                                fontSize = 11.sp,
//                                fontWeight = FontWeight.Light,
//                            ) },
//                        selected = selectedNavigation == item.screen,
//                        onClick = { onNavigationSelected(item.screen) },
//                    )
//                }
//            }
        }
    )
}

@ExperimentalMaterialApi
@Composable
internal fun HomeNavigationRail(
    selectedNavigation: Screen,
    bottomBarState: MutableState<Boolean>,
    onNavigationSelected: (Screen) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        color = MaterialTheme.colorScheme.surface.copy(alpha = AppBarAlphas.translucentBarAlpha()),
        contentColor = contentColorFor(MaterialTheme.colorScheme.onSurface),
        modifier = modifier,
    ) {

        AnimatedVisibility(
            visible = bottomBarState.value,
            enter = slideInVertically(initialOffsetY = { it }),
            exit = slideOutVertically(targetOffsetY = { it }),
            content = {
                NavigationRail(
                    contentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = AppBarAlphas.translucentBarAlpha()),
                    modifier = Modifier.padding(
                        rememberInsetsPaddingValues(
                            LocalWindowInsets.current.systemBars,
                            applyEnd = false
                        )
                    )
                ) {
                    HomeNavigationItems.forEach { item ->
                        NavigationRailItem(
                            icon = {
                                HomeNavigationItemIcon(
                                    item = item,
                                    selected = selectedNavigation == item.screen
                                )
                            },
                            alwaysShowLabel = false,
                            label = { Text(text = stringResource(item.labelResId)) },
                            selected = selectedNavigation == item.screen,
                            onClick = { onNavigationSelected(item.screen) },
                        )
                    }
                }
            }
        )
    }
}

@Composable
private fun HomeNavigationItemIcon(item: HomeNavigationItem, selected: Boolean) {
    val painter = when (item) {
        is HomeNavigationItem.ResourceIcon -> painterResource(item.iconResId)
        is HomeNavigationItem.ImageVectorIcon -> rememberVectorPainter(item.iconImageVector)
    }
    val selectedPainter = when (item) {
        is HomeNavigationItem.ResourceIcon -> item.selectedIconResId?.let { painterResource(it) }
        is HomeNavigationItem.ImageVectorIcon -> item.selectedImageVector?.let {
            rememberVectorPainter(
                it
            )
        }
    }

    if (selectedPainter != null) {
        Crossfade(targetState = selected) {
            Icon(
                painter = if (it) selectedPainter else painter,
                contentDescription = stringResource(item.contentDescriptionResId),
                tint = MaterialTheme.colorScheme.primary,
            )
        }
    } else {
        Icon(
            painter = painter,
            contentDescription = stringResource(item.contentDescriptionResId),
        )
    }
}

private sealed class HomeNavigationItem(
    val screen: Screen,
    @StringRes val labelResId: Int,
    @StringRes val contentDescriptionResId: Int,
) {
    class ResourceIcon(
        screen: Screen,
        @StringRes labelResId: Int,
        @StringRes contentDescriptionResId: Int,
        @DrawableRes val iconResId: Int,
        @DrawableRes val selectedIconResId: Int? = null,
    ) : HomeNavigationItem(screen, labelResId, contentDescriptionResId)

    class ImageVectorIcon(
        screen: Screen,
        @StringRes labelResId: Int,
        @StringRes contentDescriptionResId: Int,
        val iconImageVector: ImageVector,
        val selectedImageVector: ImageVector? = null,
    ) : HomeNavigationItem(screen, labelResId, contentDescriptionResId)
}

private val HomeNavigationItems = listOf(
    HomeNavigationItem.ResourceIcon(
        screen = Screen.Home,
        labelResId = R.string.title_home,
        contentDescriptionResId = R.string.title_home,
        iconResId = R.drawable.ic_home,
    ),
    HomeNavigationItem.ResourceIcon(
        screen = Screen.News,
        labelResId = R.string.title_notifications,
        contentDescriptionResId = R.string.title_notifications,
        iconResId = R.drawable.ic_news,
    ),
    HomeNavigationItem.ResourceIcon(
        screen = Screen.Profile,
        labelResId = R.string.title_profile,
        contentDescriptionResId = R.string.title_profile,
        iconResId = R.drawable.ic_profile,
    ),
    HomeNavigationItem.ResourceIcon(
        screen = Screen.Store,
        labelResId = R.string.title_store,
        contentDescriptionResId = R.string.title_store,
        iconResId = R.drawable.ic_store,
    ),
)
