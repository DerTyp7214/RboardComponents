package de.dertyp7214.rboardcomponents.core

import kotlin.reflect.KFunction

fun <R> KFunction<R>.delayed(delay: Long, vararg args: Any? = arrayOf()) {
    delayed(delay) {
        call(*args)
    }
}