/*
 * Copyright 2021 Google LLC
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

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.navigation
import com.jimmy.mhome.login.MainLogin
import com.jimmy.mhome.ui.add_device.AddDevice
import com.jimmy.mhome.ui.album.Album
import com.jimmy.mhome.ui.details.CameraDetails
import com.jimmy.mhome.ui.home.Home
import com.jimmy.mhome.ui.playback.CameraPlayback
import com.jimmy.mhome.ui.playback.player.PlaybackPlayer
import com.jimmy.mhome.ui.qr.QRScan
import com.jimmy.mhome.ui.util.composable
import com.jimmy.settings.Preferences
import soup.compose.material.motion.materialSharedAxisZ

sealed class Screen(val route: String) {
    object Root : Screen("Root")
    object Home : Screen("Home")
    object News : Screen("News")
    object Profile : Screen("Profile")
    object Store : Screen("Store")

    object Login : Screen("Login")
}

sealed class LeafScreen(
    private val route: String,
) {
    fun createRoute(root: Screen) = "${root.route}/$route"

    object Home : LeafScreen("home")
    object News : LeafScreen("news")
    object Profile : LeafScreen("profile")
    object Store : LeafScreen("store")

    object MainLogin : LeafScreen("main_login")
    object Login : LeafScreen("login")
    object Terms : LeafScreen("terms")
    object ForgotPassword : LeafScreen("forgot_password")
    object ForgotPasswordConfirm : LeafScreen("forgot_password_confirm")
    object Register : LeafScreen("register")
    object RegisterOtp : LeafScreen("register_otp")
    object RegisterConfirmPassword : LeafScreen("register_confirm")

    object AddDevice : LeafScreen("add_device")
    object QRScan : LeafScreen("qr_scan")


    object CameraDetails : LeafScreen("camera/{cameraId}") {
        fun createRoute(root: Screen, cameraId: String): String {
            return "${root.route}/camera/$cameraId"
        }
    }


    object CameraPlayback : LeafScreen("camera/{cameraId}/playback") {
        fun createRoute(
            root: Screen,
            cameraId: String,
        ): String {
            return "${root.route}/camera/$cameraId/playback"
        }
    }


    object Player : LeafScreen("camera/{cameraId}/playback/player?id={id}") {
        fun createRoute(
            root: Screen,
            id: String, // open a video of the list
            cameraId: String,
        ): String {
            return "${root.route}/camera/$cameraId/playback/player?id=${id}"
        }
    }


    object CameraAlbum : LeafScreen("camera/{cameraId}/album") {
        fun createRoute(
            root: Screen,
            cameraId: String,
        ): String {
            return "${root.route}/camera/$cameraId/album"
        }
    }

    object ImageDetails : LeafScreen("camera/{cameraId}/album/image?id={id}") {
        fun createRoute(
            root: Screen,
            id: String,
            cameraId: String,
        ): String {
            return "${root.route}/camera/$cameraId/album/image?id=${id}"
        }
    }
}

@ExperimentalAnimationApi
@Composable
internal fun AppNavigation(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    authState: Boolean,
) {

    //TODO: if not logged in set start destination to Login else Home

    val startDestination = if(authState){
        Screen.Home.route
    } else {
        Screen.Login.route
    }

    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    val motionSpec = materialSharedAxisZ()
    val density = LocalDensity.current

    AnimatedNavHost(
        navController = navController,
        startDestination = startDestination,
        route = Screen.Root.route,
        enterTransition = { motionSpec.enter.transition(!isRtl, density) },
        exitTransition = { motionSpec.exit.transition(!isRtl, density) },
        popEnterTransition = { motionSpec.enter.transition(isRtl, density) },
        popExitTransition = { motionSpec.exit.transition(isRtl, density) },
        modifier = modifier,
    ) {

        addHomeTopLevel(navController)
        addNewsTopLevel(navController)
        addProfileTopLevel(navController)
        addStoreTopLevel(navController)

        addLoginTopLevel(navController)
    }
}

@ExperimentalAnimationApi
private fun NavGraphBuilder.addHomeTopLevel(
    navController: NavController,
) {
    navigation(
        route = Screen.Home.route,
        startDestination = LeafScreen.Home.createRoute(Screen.Home),
    ) {
        addHome(navController, Screen.Home)
        addCameraDetails(navController, Screen.Home)
        addCameraPlayback(navController, Screen.Home)
        addCameraAlbum(navController, Screen.Home)
        addPlayer(navController, Screen.Home)
        addAddDevice(navController, Screen.Home)
        addQRScan(navController, Screen.Home)
    }
}

@ExperimentalAnimationApi
private fun NavGraphBuilder.addNewsTopLevel(
    navController: NavController,
) {
    navigation(
        route = Screen.News.route,
        startDestination = LeafScreen.News.createRoute(Screen.News),
    ) {
        addNews(navController, Screen.News)
    }
}

@ExperimentalAnimationApi
private fun NavGraphBuilder.addProfileTopLevel(
    navController: NavController,
) {
    navigation(
        route = Screen.Profile.route,
        startDestination = LeafScreen.Profile.createRoute(Screen.Profile),
    ) {
        addProfileShows(navController, Screen.Profile)
    }
}

@ExperimentalAnimationApi
private fun NavGraphBuilder.addStoreTopLevel(
    navController: NavController,
) {
    navigation(
        route = Screen.Store.route,
        startDestination = LeafScreen.Store.createRoute(Screen.Store),
    ) {
        addStore(navController, Screen.Store)
    }
}


@ExperimentalAnimationApi
fun NavGraphBuilder.addMainLogin(
    navController: NavController,
    root: Screen,
) {
    composable(
        route = LeafScreen.MainLogin.createRoute(root),
    ) {

        MainLogin(
            openLogin = {
                navController.navigate(LeafScreen.Login.createRoute(root)){
                    // Avoid multiple copies of the same destination when
                    launchSingleTop = true
                    // Restore state when re-selecting a previously selected item
                    restoreState = true
                }

            },
            openSignup = {
                navController.navigate(LeafScreen.Register.createRoute(root)){
                    // Avoid multiple copies of the same destination when
                    launchSingleTop = true
                    // Restore state when re-selecting a previously selected item
                    restoreState = true
                }

            },
            openTerms = {
                navController.navigate(LeafScreen.Terms.createRoute(root)){
                    // Avoid multiple copies of the same destination when
                    launchSingleTop = true
                    // Restore state when re-selecting a previously selected item
                    restoreState = true
                }

            },
        )

    }
}

@ExperimentalAnimationApi
private fun NavGraphBuilder.addHome(
    navController: NavController,
    root: Screen,
) {
    composable(
        route = LeafScreen.Home.createRoute(root),

        ) {

        Home(
            openCameraDetails = { cameraId ->
                navController.navigate(LeafScreen.CameraDetails.createRoute(root, cameraId)){
                    // Avoid multiple copies of the same destination when
                    launchSingleTop = true
                    // Restore state when re-selecting a previously selected item
                    restoreState = true
                }
            },

            onAddButtonClick = {
                navController.navigate(LeafScreen.AddDevice.createRoute(root)){
                    // Avoid multiple copies of the same destination when
                    launchSingleTop = true
                    // Restore state when re-selecting a previously selected item
                    restoreState = true
                }
            },

            onScanButtonClick = {
                navController.navigate(LeafScreen.QRScan.createRoute(root)){
                    // Avoid multiple copies of the same destination when
                    launchSingleTop = true
                    // Restore state when re-selecting a previously selected item
                    restoreState = true
                }
            },

        )

    }
}

@ExperimentalAnimationApi
private fun NavGraphBuilder.addCameraPlayback(
    navController: NavController,
    root: Screen,
) {
    composable(
        route = LeafScreen.CameraPlayback.createRoute(root),
        debugLabel = "CameraPlayback()",
        arguments = listOf(
            navArgument("cameraId") {
                type = NavType.StringType
            },
        ),

        ) {
        CameraPlayback(
            navigateUp = navController::navigateUp,
            openPlayer = { id, cameraId ->
                navController.navigate(LeafScreen.Player.createRoute(root, cameraId, id)){
                    // Avoid multiple copies of the same destination when
                    launchSingleTop = true
                    // Restore state when re-selecting a previously selected item
//                    restoreState = true
                }
            },
        )
    }
}


@ExperimentalAnimationApi
private fun NavGraphBuilder.addCameraAlbum(
    navController: NavController,
    root: Screen,
) {
    composable(
        route = LeafScreen.CameraAlbum.createRoute(root),
        debugLabel = "CameraAlbum()",
        arguments = listOf(
            navArgument("cameraId") {
                type = NavType.StringType
            },
        ),
    ) {

        Album(
            navigateUp = navController::navigateUp,
            openImage = { id, cameraId ->
                navController.navigate(LeafScreen.ImageDetails.createRoute(root, cameraId, id)){
                    // Avoid multiple copies of the same destination when
                    launchSingleTop = true
                    // Restore state when re-selecting a previously selected item
                    restoreState = true
                }
            },
        )
    }
}




@ExperimentalAnimationApi
private fun NavGraphBuilder.addCameraDetails(
    navController: NavController,
    root: Screen,
) {
    composable(
        route = LeafScreen.CameraDetails.createRoute(root),
        debugLabel = "CameraDetails()",
        arguments = listOf(
            navArgument("cameraId") {
                type = NavType.StringType
            },
        ),
    ) {
        CameraDetails(
            navigateUp = navController::navigateUp,
            openPlayback = { cameraId ->
                navController.navigate(LeafScreen.CameraPlayback.createRoute(root, cameraId)){
                    // Avoid multiple copies of the same destination when
                    launchSingleTop = true
                    // Restore state when re-selecting a previously selected item
//                    restoreState = true
                }
            },

            openAlbum = { cameraId ->
                navController.navigate(LeafScreen.CameraAlbum.createRoute(root, cameraId)){
                    // Avoid multiple copies of the same destination when
                    launchSingleTop = true
                    // Restore state when re-selecting a previously selected item
                    restoreState = true
                }
            },

            openSettings = {},
            openMonitor = {},
        )
    }
}

@ExperimentalAnimationApi
private fun NavGraphBuilder.addPlayer(
    navController: NavController,
    root: Screen,
) {
    composable(
        route = LeafScreen.Player.createRoute(root),
        debugLabel = "Player()",
        arguments = listOf(
            navArgument("cameraId") {
                type = NavType.StringType
                nullable = true
            },
            navArgument("id") {
                type = NavType.StringType
                nullable = true
            },
        ),
    ) {
        PlaybackPlayer(
            navigateUp = navController::navigateUp,
        )
    }
}


@ExperimentalAnimationApi
private fun NavGraphBuilder.addAddDevice(
    navController: NavController,
    root: Screen,
) {
    composable(
        route = LeafScreen.AddDevice.createRoute(root),
        debugLabel = "AddDevice()",
    ) {
        AddDevice(
            navigateUp = navController::navigateUp,
            onScanButtonClick = {},
        )
    }
}

@ExperimentalAnimationApi
private fun NavGraphBuilder.addQRScan(
    navController: NavController,
    root: Screen,
) {
    composable(
        route = LeafScreen.QRScan.createRoute(root),
        debugLabel = "QRScan()",
    ) {
        QRScan(
            navigateUp = navController::navigateUp,
        )
    }
}

@ExperimentalAnimationApi
private fun NavGraphBuilder.addNews(
    navController: NavController,
    root: Screen,
) {
    composable(
        route = LeafScreen.News.createRoute(root),
        debugLabel = "News()",
    ) {

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Notifications panel",
            )
        }

    }
}

@OptIn(ExperimentalMaterialApi::class)
@ExperimentalAnimationApi
private fun NavGraphBuilder.addProfileShows(
    navController: NavController,
    root: Screen,
) {
    composable(
        route = LeafScreen.Profile.createRoute(root),
        debugLabel = "Profile()",
    ) {
        Preferences()

    }
}

@ExperimentalAnimationApi
private fun NavGraphBuilder.addStore(
    navController: NavController,
    root: Screen,
) {
    composable(LeafScreen.Store.createRoute(root)) {

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Store")
        }

    }
}

@ExperimentalAnimationApi
fun AnimatedContentScope<*>.defaultAppEnterTransition(
    initial: NavBackStackEntry,
    target: NavBackStackEntry,
): EnterTransition {
    val initialNavGraph = initial.destination.hostNavGraph
    val targetNavGraph = target.destination.hostNavGraph
    // If we're crossing nav graphs (bottom navigation graphs), we crossfade
    if (initialNavGraph.id != targetNavGraph.id) {
        return fadeIn()
    }
    // Otherwise we're in the same nav graph, we can imply a direction
    return fadeIn() + slideIntoContainer(AnimatedContentScope.SlideDirection.Start)
}

@ExperimentalAnimationApi
fun AnimatedContentScope<*>.defaultAppExitTransition(
    initial: NavBackStackEntry,
    target: NavBackStackEntry,
): ExitTransition {
    val initialNavGraph = initial.destination.hostNavGraph
    val targetNavGraph = target.destination.hostNavGraph
    // If we're crossing nav graphs (bottom navigation graphs), we crossfade
    if (initialNavGraph.id != targetNavGraph.id) {
        return fadeOut()
    }
    // Otherwise we're in the same nav graph, we can imply a direction
    return fadeOut() + slideOutOfContainer(AnimatedContentScope.SlideDirection.Start)
}

private val NavDestination.hostNavGraph: NavGraph
    get() = hierarchy.first { it is NavGraph } as NavGraph

@ExperimentalAnimationApi
public fun AnimatedContentScope<*>.defaultAppPopEnterTransition(): EnterTransition {
    return fadeIn() + slideIntoContainer(AnimatedContentScope.SlideDirection.End)
}

@ExperimentalAnimationApi
public fun AnimatedContentScope<*>.defaultAppPopExitTransition(): ExitTransition {
    return fadeOut() + slideOutOfContainer(AnimatedContentScope.SlideDirection.End)
}
