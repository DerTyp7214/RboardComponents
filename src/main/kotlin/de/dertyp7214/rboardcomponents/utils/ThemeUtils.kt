@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package de.dertyp7214.rboardcomponents.utils

import android.app.Activity
import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import androidx.core.content.edit
import com.google.android.material.color.DynamicColors
import de.dertyp7214.rboard.IAppTheme
import de.dertyp7214.rboardcomponents.R
import de.dertyp7214.rboardcomponents.core.applyThemeOverlay
import de.dertyp7214.rboardcomponents.core.getActivity
import de.dertyp7214.rboardcomponents.core.preferences

object ThemeUtils {
    val APP_THEMES = mapOf(
        R.string.style_blue to THEMES.BLUE.name,
        R.string.style_green to THEMES.GREEN.name,
        R.string.style_red to THEMES.RED.name,
        R.string.style_yellow to THEMES.YELLOW.name,
        R.string.style_orange to THEMES.ORANGE.name,
        R.string.style_pink to THEMES.PINK.name,
        R.string.style_lime to THEMES.LIME.name,
        R.string.style_default to THEMES.DEFAULT.name
    )

    fun registerActivityLifecycleCallbacks(application: Application) {
        application.registerActivityLifecycleCallbacks(activityLifecycleCallbacks)
    }

    fun applyTheme(application: Application) {
        bindService(application) { _, service ->
            IAppTheme.Stub.asInterface(service).appTheme.let {
                if (setStyle(application, it)) getActivity()?.recreate()
            }
        }
    }

    fun getStyleName(context: Context): String {
        return context.preferences.getString("app_style", THEMES.DEFAULT.name)
            ?: THEMES.DEFAULT.name
    }

    fun setStyle(context: Context, theme: THEMES) = setStyle(context, theme.name)
    fun setStyle(context: Context, style: String) = context.preferences.let { preferences ->
        val current = preferences.getString("app_style", THEMES.DEFAULT.name)
        preferences.edit {
            putString("app_style", style)
        }
        current != style
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

    fun bindService(context: Context, serviceConnection: ServiceConnection): Boolean {
        Intent(IAppTheme::class.java.name).apply {
            val rboardPackage = "de.dertyp7214.rboardthememanager"
            val rboardPackageDebug = "de.dertyp7214.rboardthememanager.debug"

            val rboardInstalled = isPackageInstalled(rboardPackage, context.packageManager)
            val rboardDebugInstalled =
                isPackageInstalled(rboardPackageDebug, context.packageManager)

            if (rboardInstalled) setPackage(rboardPackage)
            if (rboardDebugInstalled) setPackage(rboardPackageDebug)

            return if (rboardInstalled || rboardDebugInstalled) context.bindService(
                this,
                serviceConnection,
                Context.BIND_AUTO_CREATE
            )
            else false
        }
    }

    @Suppress("DEPRECATION")
    private fun isPackageInstalled(packageName: String, packageManager: PackageManager): Boolean {
        return try {
            if (Build.VERSION.SDK_INT >= 33)
                packageManager.getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(0L))
            else packageManager.getPackageInfo(packageName, 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
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
            THEMES.BLUE.name -> R.style.ThemeOverlay_RboardThemeManager_Colors_blue
            THEMES.GREEN.name -> R.style.ThemeOverlay_RboardThemeManager_Colors_green
            THEMES.LIME.name -> R.style.ThemeOverlay_RboardThemeManager_Colors_lime
            THEMES.ORANGE.name -> R.style.ThemeOverlay_RboardThemeManager_Colors_orange
            THEMES.PINK.name -> R.style.ThemeOverlay_RboardThemeManager_Colors_pink
            THEMES.RED.name -> R.style.ThemeOverlay_RboardThemeManager_Colors_red
            THEMES.YELLOW.name -> R.style.ThemeOverlay_RboardThemeManager_Colors_yellow
            else -> null
        }
        if (style == THEMES.DEFAULT.name) DynamicColors.applyToActivityIfAvailable(activity)
        else if (themeOverlay != null) activity.applyThemeOverlay(themeOverlay)
    }
}

enum class THEMES {
    BLUE,
    GREEN,
    RED,
    YELLOW,
    ORANGE,
    PINK,
    LIME,
    DEFAULT
}