package com.jimmy

import android.os.Build
import androidx.core.os.BuildCompat

object Utilities {

    fun isValidPhoneNumber(input: String): Boolean {
        if (input.isEmpty()) return false
        val charFirst = input[0].toString()
        if (input.length in 9..12 && charFirst == "0") {
            return true
        }
        return false
    }
}