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
import androidx.annotation.StyleRes
import androidx.core.content.edit
import com.google.android.material.color.DynamicColors
import de.dertyp7214.rboard.IAppTheme
import de.dertyp7214.rboardcomponents.R
import de.dertyp7214.rboardcomponents.core.applyThemeOverlay
import de.dertyp7214.rboardcomponents.core.getActivity
import de.dertyp7214.rboardcomponents.core.preferences

object ThemeUtils {
    val APP_THEMES = mapOf(
        R.string.style_amoled to THEMES.AMOLED.name,
        R.string.style_apocyan to THEMES.APOCYAN.name,
        R.string.style_blue to THEMES.BLUE.name,
        R.string.style_brown_blue to THEMES.BROWN_BLUE.name,
        R.string.style_green to THEMES.GREEN.name,
        R.string.style_green_brown to THEMES.GREEN_BROWN.name,
        R.string.style_lavender_tonic to THEMES.LAVENDER_TONIC.name,
        R.string.style_lime to THEMES.LIME.name,
        R.string.style_mary_blue to THEMES.MARY_BLUE.name,
        R.string.style_monochrome to THEMES.MONOCHROME.name,
        R.string.style_mud_pink to THEMES.MUD_PINK.name,
        R.string.style_night_rider to THEMES.NIGHT_RIDER.name,
        R.string.style_orange to THEMES.ORANGE.name,
        R.string.style_pink to THEMES.PINK.name,
        R.string.style_peach_pearl to THEMES.PEACH_PEARL.name,
        R.string.style_rboard_v1 to THEMES.RBOARD_V1.name,
        R.string.style_rboard_v2 to THEMES.RBOARD_V2.name,
        R.string.style_red to THEMES.RED.name,
        R.string.style_samoan_sun to THEMES.SAMOAN_SUN.name,
        R.string.style_vert_pierre to THEMES.VERT_PIERRE.name,
        R.string.style_yellow to THEMES.YELLOW.name,
        R.string.style_yellow_blue to THEMES.YELLOW_BLUE.name,
        R.string.style_default to THEMES.DEFAULT.name
    )

    fun registerActivityLifecycleCallbacks(application: Application) {
        application.registerActivityLifecycleCallbacks(activityLifecycleCallbacks)
    }

    fun applyTheme(
        context: Context,
        recreate: (Activity?, changed: Boolean) -> Unit = { a, b -> if (b) a?.recreate() }
    ) {
        if (!bindService(context, IAppTheme::class.java) { _, service ->
                IAppTheme.Stub.asInterface(service).appTheme.let {
                    recreate(getActivity(), setStyle(context, it))
                }
            }) recreate(null, false)
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
        serviceInterface: Class<*> = IAppTheme::class.java,
        onDisconnected: (className: ComponentName) -> Unit = {},
        onConnected: (className: ComponentName, service: IBinder) -> Unit = { _, _ -> }
    ) = bindService(
        context,
        getServiceConnection(onDisconnected, onConnected),
        serviceInterface
    )

    fun bindService(
        context: Context,
        serviceConnection: ServiceConnection,
        serviceInterface: Class<*> = IAppTheme::class.java
    ): Boolean {
        Intent(serviceInterface.name).apply {
            val rboardPackage = "de.dertyp7214.rboardthememanager"
            val rboardPackageDebug = "de.dertyp7214.rboardthememanager.debug"

            val rboardInstalled = isPackageInstalled(rboardPackage, context.packageManager)
            val rboardDebugInstalled =
                isPackageInstalled(rboardPackageDebug, context.packageManager)

            if (rboardInstalled) setPackage(rboardPackage)
            if (rboardDebugInstalled) setPackage(rboardPackageDebug)

            return if (rboardInstalled || rboardDebugInstalled) context.bindService(
                this, serviceConnection, Context.BIND_AUTO_CREATE
            )
            else false
        }
    }

    private fun isPackageInstalled(packageName: String, packageManager: PackageManager): Boolean {
        return try {
            if (Build.VERSION.SDK_INT >= 33) packageManager.getPackageInfo(
                packageName, PackageManager.PackageInfoFlags.of(0L)
            )
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

    @StyleRes
    fun getTheme(context: Context, style: String = getStyleName(context)): Int? {
        return when (style) {
            THEMES.AMOLED.name -> R.style.ThemeOverlay_RboardThemeManager_Colors_amoled
            THEMES.APOCYAN.name -> R.style.ThemeOverlay_RboardThemeManager_Colors_apocyan
            THEMES.BLUE.name -> R.style.ThemeOverlay_RboardThemeManager_Colors_blue
            THEMES.BROWN_BLUE.name -> R.style.ThemeOverlay_RboardThemeManager_Colors_brown_blue
            THEMES.MUD_PINK.name -> R.style.ThemeOverlay_RboardThemeManager_Colors_mud_pink
            THEMES.GREEN.name -> R.style.ThemeOverlay_RboardThemeManager_Colors_green
            THEMES.GREEN_BROWN.name -> R.style.ThemeOverlay_RboardThemeManager_Colors_green_brown
            THEMES.LAVENDER_TONIC.name -> R.style.ThemeOverlay_RboardThemeManager_Colors_lavender_tonic
            THEMES.LIME.name -> R.style.ThemeOverlay_RboardThemeManager_Colors_lime
            THEMES.MARY_BLUE.name -> R.style.ThemeOverlay_RboardThemeManager_Colors_mary_blue
            THEMES.MONOCHROME.name -> R.style.ThemeOverlay_RboardThemeManager_Colors_monochrome
            THEMES.NIGHT_RIDER.name -> R.style.ThemeOverlay_RboardThemeManager_Colors_night_rider
            THEMES.ORANGE.name -> R.style.ThemeOverlay_RboardThemeManager_Colors_orange
            THEMES.PINK.name -> R.style.ThemeOverlay_RboardThemeManager_Colors_pink
            THEMES.PEACH_PEARL.name -> R.style.ThemeOverlay_RboardThemeManager_Colors_peach_pearl
            THEMES.RBOARD_V1.name -> R.style.ThemeOverlay_RboardThemeManager_Colors_rboard_v1
            THEMES.RBOARD_V2.name -> R.style.ThemeOverlay_RboardThemeManager_Colors_rboard_v2
            THEMES.RED.name -> R.style.ThemeOverlay_RboardThemeManager_Colors_red
            THEMES.SAMOAN_SUN.name -> R.style.ThemeOverlay_RboardThemeManager_Colors_samoan_sun
            THEMES.VERT_PIERRE.name -> R.style.ThemeOverlay_RboardThemeManager_Colors_vert_pierre
            THEMES.YELLOW.name -> R.style.ThemeOverlay_RboardThemeManager_Colors_yellow
            THEMES.YELLOW_BLUE.name -> R.style.ThemeOverlay_RboardThemeManager_Colors_yellow_blue
            else -> null
        }
    }

    fun applyThemeOverlay(context: Context, defaultTheme: Int? = null): Context {
        val style = getStyleName(context)
        val themeOverlay = getTheme(context, style)
        if (style == THEMES.DEFAULT.name) {
            if (context is Activity)
                DynamicColors.applyToActivityIfAvailable(context)
            else if (defaultTheme != null) return DynamicColors.wrapContextIfAvailable(context.apply {
                applyThemeOverlay(
                    defaultTheme
                )
            })
            else return DynamicColors.wrapContextIfAvailable(context)
        } else if (themeOverlay != null) context.applyThemeOverlay(themeOverlay)
        return context
    }
}

enum class THEMES {
    BLUE, GREEN, GREEN_BROWN, MONOCHROME, APOCYAN, LAVENDER_TONIC, VERT_PIERRE, RBOARD_V1, RBOARD_V2, BROWN_BLUE, MUD_PINK, MARY_BLUE, NIGHT_RIDER, AMOLED, PEACH_PEARL, RED, YELLOW, YELLOW_BLUE, SAMOAN_SUN, ORANGE, PINK, LIME, DEFAULT
}