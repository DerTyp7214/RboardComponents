@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package de.dertyp7214.rboardcomponents.utils

import android.app.Activity
import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import com.google.android.material.color.DynamicColors
import de.dertyp7214.rboardcomponents.R
import de.dertyp7214.rboardcomponents.core.applyThemeOverlay
import de.dertyp7214.rboardcomponents.core.preferences

object ThemeUtils {
    val APP_THEMES = mapOf(
        R.string.style_blue to "blue",
        R.string.style_green to "green",
        R.string.style_red to "red",
        R.string.style_yellow to "yellow",
        R.string.style_orange to "orange",
        R.string.style_pink to "pink",
        R.string.style_lime to "lime",
        R.string.style_default to "default"
    )

    fun registerActivityLifecycleCallbacks(application: Application) {
        application.registerActivityLifecycleCallbacks(activityLifecycleCallbacks)
    }

    fun getStyleName(context: Context): String {
        return context.preferences.getString("app_style", "default") ?: "default"
    }

    fun getServiceConnection(
        onDisconnected: (className: ComponentName) -> Unit = {},
        onConnected: (className: ComponentName, service: IBinder) -> Unit = { _, _ -> }
    ): ServiceConnection {
        return object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName, service: IBinder) {
                onConnected(name, service)
            }

            override fun onServiceDisconnected(name: ComponentName) {
                onDisconnected(name)
            }
        }
    }

    fun bindService(
        context: Context,
        onDisconnected: (className: ComponentName) -> Unit = {},
        onConnected: (className: ComponentName, service: IBinder) -> Unit = { _, _ -> }
    ) = bindService(context, getServiceConnection(onDisconnected, onConnected))

    fun bindService(context: Context, serviceConnection: ServiceConnection) {
        Intent().apply {
            setClassName(
                "de.dertyp7214.rboardthememanager",
                "de.dertyp7214.rboardthememanager.services.AppThemeService"
            )
            context.bindService(this, serviceConnection, Context.BIND_AUTO_CREATE)
        }
    }

    private val activityLifecycleCallbacks = object : Application.ActivityLifecycleCallbacks {
        override fun onActivityPreCreated(activity: Activity, savedInstanceState: Bundle?) {
            applyThemeOverlay(activity)
        }

        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
        override fun onActivityStarted(activity: Activity) {}
        override fun onActivityResumed(activity: Activity) {}
        override fun onActivityPaused(activity: Activity) {}
        override fun onActivityStopped(activity: Activity) {}
        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
        override fun onActivityDestroyed(activity: Activity) {}
    }

    private fun applyThemeOverlay(activity: Activity) {
        val style = getStyleName(activity)
        val themeOverlay = when (style) {
            "blue" -> R.style.ThemeOverlay_RboardThemeManager_Colors_blue
            "green" -> R.style.ThemeOverlay_RboardThemeManager_Colors_green
            "lime" -> R.style.ThemeOverlay_RboardThemeManager_Colors_lime
            "orange" -> R.style.ThemeOverlay_RboardThemeManager_Colors_orange
            "pink" -> R.style.ThemeOverlay_RboardThemeManager_Colors_pink
            "red" -> R.style.ThemeOverlay_RboardThemeManager_Colors_red
            "yellow" -> R.style.ThemeOverlay_RboardThemeManager_Colors_yellow
            else -> null
        }
        if (style == "default") DynamicColors.applyToActivityIfAvailable(activity)
        else if (themeOverlay != null) activity.applyThemeOverlay(themeOverlay)
    }
}