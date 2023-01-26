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

package com.jimmy.settings.preferences.ui

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavGraphBuilder
import com.jimmy.settings.R
import com.jimmy.settings.preferences.components.PreferenceGroup
import com.jimmy.settings.preferences.components.PreferenceLayout
import com.jimmy.settings.preferences.preferenceGraph

@ExperimentalMaterialApi
@ExperimentalAnimationApi
fun NavGraphBuilder.generalGraph(route: String) {
    preferenceGraph(route, { GeneralPreferences() }) { subRoute ->
//        iconPackGraph(route = subRoute(GeneralRoutes.ICON_PACK))
    }
}

@ExperimentalMaterialApi
@ExperimentalAnimationApi
@Composable
fun GeneralPreferences() {

//    Log.i("PREF", "== enableFontSelection ${prefs.enableFontSelection} ==")

    PreferenceLayout(label = stringResource(id = R.string.settings_general)) {
        PreferenceGroup(isFirstChild = true) {
//            SwitchPreference(
//                adapter = prefs.allowRotation.getAdapter(),
//                label = stringResource(id = R.string.home_screen_rotation_label),
//                description = stringResource(id = R.string.home_screen_rotaton_description),
//            )
        }
        PreferenceGroup(
            heading = stringResource(id = R.string.call_history_title)
        ) {
        }
    }
}
