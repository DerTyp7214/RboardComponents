package de.dertyp7214.rboardcomponents.core

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.os.Handler
import android.os.Looper
import android.util.TypedValue
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import androidx.core.os.postDelayed
import androidx.preference.PreferenceManager

val Context.preferences: SharedPreferences
    get() = PreferenceManager.getDefaultSharedPreferences(this)

fun delayed(delay: Long, callback: () -> Unit) {
    Handler(Looper.getMainLooper()).postDelayed(delay) { callback() }
}

fun Context.getAttr(@AttrRes attr: Int): Int {
    val typedValue = TypedValue()
    theme.resolveAttribute(attr, typedValue, true)
    return typedValue.data
}

fun Context.applyThemeOverlay(@StyleRes theme: Int) {
    this.theme.applyStyle(theme, true)

    if (this is Activity) {
        val windowDecorViewTheme = getWindowDecorViewTheme()
        windowDecorViewTheme?.applyStyle(theme, true)
    }
}