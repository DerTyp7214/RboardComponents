package de.dertyp7214.rboardcomponents.utils

import android.content.Context
import de.dertyp7214.rboard.IRboard

object RboardUtils {
    fun getRboardService(context: Context, callback: (IRboard) -> Unit): Boolean {
        return ThemeUtils.bindService(context, IRboard::class.java) { _, service ->
            callback(IRboard.Stub.asInterface(service))
        }
    }
}