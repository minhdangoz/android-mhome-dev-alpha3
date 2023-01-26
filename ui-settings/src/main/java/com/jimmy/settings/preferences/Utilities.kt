package com.jimmy.settings.preferences

import android.os.Build
import androidx.core.os.BuildCompat

object Utilities {

    val ATLEAST_S = (BuildCompat.isAtLeastS()
        || Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)

}