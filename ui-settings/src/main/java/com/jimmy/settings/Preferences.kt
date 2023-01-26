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

package com.jimmy.settings

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.jimmy.common.compose.Layout
import com.jimmy.settings.preferences.PreferencesDashboard
import com.jimmy.settings.preferences.ProvideBottomSheetHandler
import com.jimmy.settings.preferences.ProvideLifecycleState
import com.jimmy.settings.preferences.portal.ProvidePortalNode
import com.jimmy.settings.preferences.preferenceGraph
import com.jimmy.settings.preferences.ui.aboutGraph
import com.jimmy.settings.preferences.ui.accountGraph
import com.jimmy.settings.preferences.ui.contactGraph
import com.jimmy.settings.preferences.ui.feedbackGraph
import com.jimmy.settings.preferences.ui.generalGraph
import com.jimmy.settings.preferences.ui.messageGraph
import com.jimmy.settings.preferences.ui.phoneGraph
import com.jimmy.settings.preferences.ui.scheduleGraph
import com.jimmy.settings.preferences.ui.userManualGraph
import soup.compose.material.motion.materialSharedAxisX

object Routes {
    const val ACCOUNT: String = "account"
    const val GENERAL: String = "general"
    const val SCHEDULED: String = "scheduled"
    const val CALL: String = "call"
    const val MESSAGE: String = "message"
    const val FEEDBACK: String = "feedback"
    const val USER_MANUAL: String = "user_manual"
    const val CONTACT: String = "contact"
    const val ABOUT: String = "about"
}

val LocalNavController = staticCompositionLocalOf<NavController> {
    error("CompositionLocal LocalNavController not present")
}

val LocalPreferenceInteractor = staticCompositionLocalOf {
    error("CompositionLocal LocalPreferenceInteractor not present")
}

@ExperimentalMaterialApi
@ExperimentalAnimationApi
@Composable
fun Preferences() {
    val navController = rememberAnimatedNavController()
    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    val motionSpec = materialSharedAxisX()
    val density = LocalDensity.current

    Providers {
        Surface {
            CompositionLocalProvider(
                LocalNavController provides navController
            ) {
                AnimatedNavHost(
                    navController = navController,
                    startDestination = "/",
                    enterTransition = { motionSpec.enter.transition(!isRtl, density) },
                    exitTransition = { motionSpec.exit.transition(!isRtl, density) },
                    popEnterTransition = { motionSpec.enter.transition(isRtl, density) },
                    popExitTransition = { motionSpec.exit.transition(isRtl, density) },
                ) {
                    preferenceGraph(route = "/", { PreferencesDashboard() }) { subRoute ->
                        accountGraph(
                            route = subRoute(
                                Routes.ACCOUNT
                            )
                        )
                        generalGraph(
                            route = subRoute(
                                Routes.GENERAL
                            )
                        )
                        scheduleGraph(
                            route = subRoute(
                                Routes.SCHEDULED
                            )
                        )
                        phoneGraph(
                            route = subRoute(
                                Routes.CALL
                            )
                        )
                        messageGraph(
                            route = subRoute(
                                Routes.MESSAGE
                            )
                        )
                        feedbackGraph(
                            route = subRoute(
                                Routes.FEEDBACK
                            )
                        )
                        userManualGraph(
                            route = subRoute(
                                Routes.USER_MANUAL
                            )
                        )
                        contactGraph(
                            route = subRoute(
                                Routes.CONTACT
                            )
                        )
                        aboutGraph(
                            route = subRoute(
                                Routes.ABOUT
                            )
                        )

                    }

                } // nav host

                Box(
                    contentAlignment = Alignment.BottomCenter,
                    modifier = Modifier.fillMaxSize().padding(bottom = 72.dp)
                ){
                    Button(
                        onClick = { /*TODO*/ },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary,
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(Layout.bodyMargin)
                    ) {
                        Text(text = stringResource(id = R.string.sign_out))
                    }
                }

            }
        }
    }

}

@ExperimentalMaterialApi
@Composable
private fun Providers(
    content: @Composable () -> Unit
) {
    ProvidePortalNode {
        ProvideLifecycleState {
            ProvideBottomSheetHandler {
                content()
            }
        }
    }
}
