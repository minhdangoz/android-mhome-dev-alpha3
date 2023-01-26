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

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.jimmy.settings.preferences.PreferenceChangeListener
import kotlin.reflect.KProperty

interface PreferenceAdapter<T> {
    val state: State<T>
    fun onChange(newValue: T)

    operator fun getValue(thisObj: Any?, property: KProperty<*>): T = state.value
    operator fun setValue(thisObj: Any?, property: KProperty<*>, newValue: T) {
        onChange(newValue)
    }
}

private class MutableStatePreferenceAdapter<T>(
    private val mutableState: MutableState<T>
) : PreferenceAdapter<T> {
    override val state = mutableState

    override fun onChange(newValue: T) {
        mutableState.value = newValue
    }
}

class PreferenceAdapterImpl<T>(
    private val get: () -> T,
    private val set: (T) -> Unit
) : PreferenceAdapter<T>, PreferenceChangeListener {
    private val stateInternal = mutableStateOf(get())
    override val state: State<T> get() = stateInternal

    override fun onChange(newValue: T) {
        set(newValue)
        stateInternal.value = newValue
    }

    override fun onPreferenceChange() {
        stateInternal.value = get()
    }
}


@Composable
fun <T> PrefEntry<T>.getAdapter() = getAdapter(this, ::get, ::set)

@Composable
fun <T> PrefEntry<T>.getState() = getAdapter().state

@Composable
fun <T> PrefEntry<T>.observeAsState() = getAdapter().state

@Composable
private fun <P, T> getAdapter(
    pref: PrefEntry<P>,
    get: () -> T,
    set: (T) -> Unit
): PreferenceAdapter<T> {
    val adapter = remember { PreferenceAdapterImpl(get, set) }
    DisposableEffect(pref) {
        pref.addListener(adapter)
        onDispose { pref.removeListener(adapter) }
    }
    return adapter
}

@Composable
fun <T, R> rememberTransformAdapter(
    adapter: PreferenceAdapter<T>,
    transformGet: (T) -> R,
    transformSet: (R) -> T
): PreferenceAdapter<R> = remember(adapter) {
    TransformPreferenceAdapter(adapter, transformGet, transformSet)
}

@Composable
fun <T> MutableState<T>.asPreferenceAdapter(): PreferenceAdapter<T> {
    return remember(this) { MutableStatePreferenceAdapter(this) }
}

private class TransformPreferenceAdapter<T, R>(
    private val parent: PreferenceAdapter<T>,
    private val transformGet: (T) -> R,
    private val transformSet: (R) -> T
) : PreferenceAdapter<R> {
    override val state = derivedStateOf { transformGet(parent.state.value) }

    override fun onChange(newValue: R) {
        parent.onChange(transformSet(newValue))
    }
}

@Composable
fun <T> customPreferenceAdapter(value: T, onValueChange: (T) -> Unit): PreferenceAdapter<T> {
    val state = remember { mutableStateOf(value) }
    state.value = value
    return object : PreferenceAdapter<T> {
        override val state = state
        override fun onChange(newValue: T) {
            onValueChange(newValue)
        }
    }
}

@Composable
operator fun PreferenceAdapter<Boolean>.not(): PreferenceAdapter<Boolean> {
    return rememberTransformAdapter(adapter = this, transformGet = { !it }, transformSet = { !it })
}
