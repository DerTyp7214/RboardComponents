package de.dertyp7214.rboardcomponents.core

import android.app.Activity
import android.content.res.Resources

fun Activity.getWindowDecorViewTheme(): Resources.Theme? {
    if (window != null) {
        val decorView = window.peekDecorView()
        if (decorView != null) {
            val context = decorView.context
            if (context != null) {
                return context.theme
            }
        }
    }
    return null
}