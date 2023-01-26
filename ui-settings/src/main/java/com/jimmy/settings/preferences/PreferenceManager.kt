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

import android.content.SharedPreferences
import javax.inject.Inject

class PreferenceManager @Inject constructor(
    sp: SharedPreferences
) : BasePreferenceManager(sp) {

    init {
        sp.registerOnSharedPreferenceChangeListener(this)
    }


    val hiddenAppSet = StringSetPref("hidden-app-set", setOf())
    val iconSizeFactor = FloatPref("pref_iconSizeFactor", 1F)
    val hideAppSearchBar = BoolPref("pref_hideAppSearchBar", false)

    val enableFontSelection = BoolPref("pref_enableFontSelection", true)


}
