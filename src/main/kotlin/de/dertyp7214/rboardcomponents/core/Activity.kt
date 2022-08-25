package de.dertyp7214.rboardcomponents.core

import android.annotation.SuppressLint
import android.app.Activity
import android.content.res.Resources
import java.lang.reflect.Field

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

@Suppress("UNCHECKED_CAST")
@SuppressLint("DiscouragedPrivateApi", "PrivateApi")
fun getActivity(): Activity? {
    return try {
        val activityThreadClass = Class.forName("android.app.ActivityThread")
        val activityThread = activityThreadClass.getMethod("currentActivityThread").invoke(null)
        val activitiesField: Field = activityThreadClass.getDeclaredField("mActivities")
        activitiesField.isAccessible = true
        val activities = activitiesField.get(activityThread) as Map<Any, Any>
        for (activityRecord in activities.values) {
            val activityRecordClass: Class<*> = activityRecord.javaClass
            val pausedField: Field = activityRecordClass.getDeclaredField("paused")
            pausedField.isAccessible = true
            if (!pausedField.getBoolean(activityRecord)) {
                val activityField: Field = activityRecordClass.getDeclaredField("activity")
                activityField.isAccessible = true
                return activityField.get(activityRecord) as Activity
            }
        }
        null
    } catch (_: Exception) {
        null
    }
}