package com.jimmy.settings.preferences.components

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.jimmy.settings.preferences.PrefEntry
import com.jimmy.settings.preferences.PreferenceChangeListener

class PrefLifecycleObserver<T>(
    private val prefEntry: PrefEntry<T>,
    private val onChange: Runnable
) : LifecycleObserver, PreferenceChangeListener {

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun connectListener() {
        prefEntry.addListener(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun disconnectListener() {
        prefEntry.removeListener(this)
    }

    override fun onPreferenceChange() {
        onChange.run()
    }
}
